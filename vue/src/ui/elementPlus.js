import { ElButton } from 'element-plus/es/components/button/index.mjs'
import { ElDialog } from 'element-plus/es/components/dialog/index.mjs'
import { ElForm, ElFormItem } from 'element-plus/es/components/form/index.mjs'
import { ElIcon } from 'element-plus/es/components/icon/index.mjs'
import { ElInput } from 'element-plus/es/components/input/index.mjs'
import { ElLoading } from 'element-plus/es/components/loading/index.mjs'
import { ElMessage } from 'element-plus/es/components/message/index.mjs'
import 'element-plus/es/components/button/style/css.mjs'
import 'element-plus/es/components/dialog/style/css.mjs'
import 'element-plus/es/components/form/style/css.mjs'
import 'element-plus/es/components/form-item/style/css.mjs'
import 'element-plus/es/components/icon/style/css.mjs'
import 'element-plus/es/components/input/style/css.mjs'
import 'element-plus/es/components/loading/style/css.mjs'
import 'element-plus/es/components/message/style/css.mjs'

let pageComponents

// 进入业务页时再加载对应控件，登录和工作台不等待这组组件。
export function loadPageComponents(app) {
  if (!pageComponents) {
    pageComponents = import('./pageComponents').then(({ default: plugin }) => {
      app.use(plugin)
    }).catch(error => {
      pageComponents = null
      throw error
    })
  }
  return pageComponents
}

// 只同步注册登录和公共布局需要的组件和服务。
export default {
  install(app) {
    for (const component of [
      ElButton, ElDialog, ElForm, ElFormItem, ElIcon, ElInput, ElLoading, ElMessage
    ]) app.use(component)
  }
}
