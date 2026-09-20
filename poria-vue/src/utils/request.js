import axios from 'axios'
import {
  ElNotification,
  ElMessageBox,
  ElMessage,
  ElLoading
} from 'element-plus'
import {
  getToken,
  removeToken
} from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import {
  tansParams,
  blobValidate
} from '@/utils/ruoyi'
import cache from '@/plugins/cache'
import {
  saveAs
} from 'file-saver'
import useUserStore from '@/store/modules/user'

let downloadLoadingInstance
// 是否显示重新登录
export let isRelogin = {
  show: false
}

// 原生上传、下载和图片预览不会经过 axios 实例。业务 API 必须在各自模块中
// 显式声明 /auth/** 或 /upms/**，页面路由不会参与接口路径转换。
export function gatewayUrl(url = '') {
  const baseURL = (import.meta.env.VITE_APP_BASE_API || '').replace(/\/$/, '')
  const path = url || '/upms'
  return `${baseURL}${path.startsWith('/') ? path : `/${path}`}`
}

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  baseURL: import.meta.env.VITE_APP_BASE_API,
  // 超时
  timeout: 10000
})

// request拦截器
service.interceptors.request.use(config => {
  // 是否需要设置 token
  const isToken = (config.headers || {}).isToken === false
  // 是否需要防止数据重复提交
  const isRepeatSubmit = (config.headers || {}).repeatSubmit === false
  // 间隔时间(ms)，小于此时间视为重复提交
  const interval = (config.headers || {}).interval || 1000
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  // get请求映射params参数
  if (config.method === 'get' && config.params) {
    let url = config.url + '?' + tansParams(config.params)
    url = url.slice(0, -1)
    config.params = {}
    config.url = url
  }
  if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
    const requestObj = {
      url: config.url,
      data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
      time: new Date().getTime()
    }
    const requestSize = Object.keys(JSON.stringify(requestObj)).length // 请求数据大小
    const limitSize = 5 * 1024 * 1024 // 限制存放数据5M
    if (requestSize >= limitSize) {
      console.warn(`[${config.url}]: ` + '请求数据大小超出允许的5M限制，无法进行防重复提交验证。')
      return config
    }
    const sessionObj = cache.session.getJSON('sessionObj')
    if (sessionObj === undefined || sessionObj === null || sessionObj === '') {
      cache.session.setJSON('sessionObj', requestObj)
    } else {
      const s_url = sessionObj.url // 请求地址
      const s_data = sessionObj.data // 请求数据
      const s_time = sessionObj.time // 请求时间
      if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url) {
        const message = '数据正在处理，请勿重复提交'
        console.warn(`[${s_url}]: ` + message)
        return Promise.reject(new Error(message))
      } else {
        cache.session.setJSON('sessionObj', requestObj)
      }
    }
  }
  return config
}, error => {
  console.log(error)
  Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(res => {
    // OAuth2 错误使用 error/error_description 格式，不带统一响应 code。
    if (res.data?.error) {
      const message = res.data.error_description || res.data.error
      ElMessage({
        message,
        type: 'error'
      })
      return Promise.reject(new Error(message))
    }
    // 未设置状态码则默认成功状态
    // Poria 的统一响应码为 0；若依为 200。两者均视为成功。
    const code = res.data.code === undefined || res.data.code === 0 ? 200 : res.data.code
    // 获取错误信息
    const msg = errorCode[code] || res.data.msg || errorCode['default']
    // 二进制数据则直接返回
    if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
      return res.data
    }
    if (code === 401) {
      // 登录接口在尚未取得令牌时也会用 401 表示认证失败（例如验证码错误）。
      // 此时不能清除会话或展示“登录状态已过期”，应把服务端错误交给登录页展示。
      if (res.config?.skipAuthExpiredHandling) {
        return Promise.reject(new Error(res.data.msg || msg))
      }
      if (!isRelogin.show) {
        isRelogin.show = true
        ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          isRelogin.show = false
          useUserStore().logOut().then(() => {
            location.href = '/index'
          })
        }).catch(() => {
          isRelogin.show = false
        })
      }
      return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
    } else if (code === 500) {
      ElMessage({
        message: msg,
        type: 'error'
      })
      return Promise.reject(new Error(msg))
    } else if (code === 601) {
      ElMessage({
        message: msg,
        type: 'warning'
      })
      return Promise.reject(new Error(msg))
    } else if (code !== 200) {
      ElNotification.error({
        title: msg
      })
      return Promise.reject('error')
    } else {
      return Promise.resolve(res.data)
    }
  },
  error => {
    console.log('err' + error)
    let {
      message
    } = error
    // 与上方的业务码处理保持一致：登录前的认证失败不是既有会话过期。
    if (error.config?.skipAuthExpiredHandling && error.response?.status === 401) {
      const responseMessage = error.response.data?.error_description || error.response.data?.msg
      return Promise.reject(new Error(responseMessage || message || '登录认证失败'))
    }
    if (error.response?.status === 401 || error.response?.status === 424) {
      removeToken()
      location.href = '/login'
      message = '登录状态已失效，请重新登录'
    } else if (message == "Network Error") {
      message = "后端接口连接异常"
    } else if (message.includes("timeout")) {
      message = "系统接口请求超时"
    } else if (message.includes("Request failed with status code")) {
      message = "系统接口" + message.slice(-3) + "异常"
    }
    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

// 通用下载方法
export function download(url, params, filename, config) {
  downloadLoadingInstance = ElLoading.service({
    text: "正在下载数据，请稍候",
    background: "rgba(0, 0, 0, 0.7)",
  })
  return service.post(url, params, {
    transformRequest: [(params) => {
      return tansParams(params)
    }],
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    responseType: 'blob',
    ...config
  }).then(async (data) => {
    const isBlob = blobValidate(data)
    if (isBlob) {
      const blob = new Blob([data])
      saveAs(blob, filename)
    } else {
      const resText = await data.text()
      const rspObj = JSON.parse(resText)
      const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
      ElMessage.error(errMsg)
    }
    downloadLoadingInstance.close()
  }).catch((r) => {
    console.error(r)
    ElMessage.error('下载文件出现错误，请联系管理员！')
    downloadLoadingInstance.close()
  })
}

export default service
