<template>
  <div class="login">
    <div class="login-form">
      <div class="brand"><span>P</span><b>PORIA</b></div>
      <h3>登录管理台</h3>
      <p>账号和密码由 Poria Auth 与 UPMS 服务校验。</p>
      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" autocomplete="username" placeholder="用户名" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" autocomplete="current-password" placeholder="密码" show-password size="large" type="password">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="verifyCode">
          <div class="captcha-field">
            <el-input v-model="loginForm.verifyCode" autocomplete="one-time-code" placeholder="验证码" size="large">
              <template #prefix><el-icon><Key /></el-icon></template>
            </el-input>
            <button class="captcha-image" type="button" :disabled="captchaLoading" title="点击刷新验证码" @click="refreshCaptcha">
              <img v-if="captchaUrl" :src="captchaUrl" alt="验证码，点击刷新" />
              <span v-else>{{ captchaLoading ? '加载中' : '刷新验证码' }}</span>
            </button>
          </div>
        </el-form-item>
        <el-button :loading="loading" size="large" type="primary" class="full" @click="handleLogin">
          登录 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </el-form>
      <small>验证码有效期为 60 秒，点击图片可刷新。</small>
    </div>
    <div class="footer">Poria Platform · OAuth2 / UPMS</div>
  </div>
</template>

<script setup>
  import { ElMessage } from 'element-plus'
  import useUserStore from '@/store/modules/user'
  import { gatewayUrl } from '@/utils/request'

  const userStore = useUserStore()
  const route = useRoute()
  const router = useRouter()
  const loginRef = ref()
  const loading = ref(false)
  const captchaLoading = ref(false)
  const captchaUrl = ref('')
  const loginForm = ref({
    username: '', password: '', loginType: 'ADMIN_PWD',
    userType: import.meta.env.VITE_AUTH_USER_TYPE || '', verifyCode: '', randomStr: '',
    appId: '', scope: import.meta.env.VITE_AUTH_SCOPE || 'server', additionalParameters: {}
  })
  const loginRules = {
    username: [{ required: true, trigger: 'blur', message: '请输入用户名' }],
    password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
    verifyCode: [{ required: true, trigger: 'blur', message: '请输入验证码' }]
  }

  function createRandomStr() {
    if (window.crypto?.randomUUID) return window.crypto.randomUUID()
    return `${Date.now()}-${Math.random().toString(36).slice(2)}`
  }
  async function refreshCaptcha() {
    captchaLoading.value = true
    loginForm.value.verifyCode = ''
    loginForm.value.randomStr = createRandomStr()
    try {
      const response = await fetch(gatewayUrl(`/code?randomStr=${encodeURIComponent(loginForm.value.randomStr)}`), { headers: { Accept: 'text/plain' } })
      if (!response.ok) throw new Error('验证码加载失败')
      const nextCaptchaUrl = URL.createObjectURL(await response.blob())
      if (captchaUrl.value) URL.revokeObjectURL(captchaUrl.value)
      captchaUrl.value = nextCaptchaUrl
    } catch (error) {
      ElMessage.error(error?.message || '验证码加载失败，请检查网关服务')
    } finally {
      captchaLoading.value = false
    }
  }
  function handleLogin() {
    loginRef.value.validate(valid => {
      if (!valid) return
      loading.value = true
      userStore.login(loginForm.value)
        .then(() => router.push(route.query.redirect || '/index'))
        .catch(error => { ElMessage.error(error?.message || '登录失败，请检查认证服务配置'); refreshCaptcha() })
        .finally(() => { loading.value = false })
    })
  }
  onMounted(refreshCaptcha)
  onBeforeUnmount(() => { if (captchaUrl.value) URL.revokeObjectURL(captchaUrl.value) })
</script>

<style lang="scss" scoped>
  .login { min-height: 100vh; display: grid; place-items: center; background: radial-gradient(circle at 15% 15%, #1d5fae, transparent 38%), #001529; }
  .login-form { width: min(420px, calc(100vw - 42px)); padding: 38px; border-radius: 12px; background: #fff; box-shadow: 0 20px 60px #0006; }
  .brand { display: flex; align-items: center; gap: 10px; color: #001529; letter-spacing: 2px; }
  .brand span { display: grid; width: 32px; height: 32px; place-items: center; border-radius: 8px; background: #1677ff; color: #fff; font-weight: 800; font-size: 19px; }
  .login-form h3 { margin: 28px 0 8px; font-size: 25px; }
  .login-form p { margin: 0 0 24px; color: #909399; }
  .full { width: 100%; }
  .captcha-field { display: flex; width: 100%; gap: 12px; }
  .captcha-field :deep(.el-input) { flex: 1; }
  .captcha-image { display: grid; width: 110px; height: 40px; flex: 0 0 110px; place-items: center; overflow: hidden; padding: 0; border: 1px solid #dcdfe6; border-radius: 4px; background: #f5f7fa; color: #909399; cursor: pointer; }
  .captcha-image:disabled { cursor: wait; }
  .captcha-image img { display: block; width: 100%; height: 100%; object-fit: cover; }
  .login-form small { display: block; margin-top: 18px; text-align: center; color: #b0b4bd; }
  .footer { position: fixed; bottom: 24px; color: #8bb7e8; font-size: 12px; }
</style>
