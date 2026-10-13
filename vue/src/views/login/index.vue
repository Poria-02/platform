<template>
  <div class="login-shell">
    <header class="login-header">
      <div class="brand"><span class="brand-mark" aria-hidden="true">P</span><span>PORIA</span></div>
    </header>
    <main class="login-main">
      <section class="login-card" aria-labelledby="login-title">
        <h2 id="login-title">登录工作台</h2>
        <p class="login-intro">请输入平台账号，继续你的工作。</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" name="username" size="large" autocomplete="username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" name="password" size="large" type="password" show-password autocomplete="current-password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="验证码" prop="verifyCode" class="captcha-item">
            <div class="captcha-row">
              <el-input v-model="form.verifyCode" name="verifyCode" size="large" autocomplete="one-time-code" placeholder="请输入验证码" />
              <button class="captcha-button" type="button" :disabled="captchaLoading || submitting" title="刷新验证码" @click="refreshCaptcha">
                <img v-if="captchaUrl" :src="captchaUrl" alt="验证码" />
                <span v-else>{{ captchaLoading ? '加载中' : '点击刷新' }}</span>
              </button>
            </div>
          </el-form-item>
          <p class="login-help">看不清？点击验证码刷新</p>
          <el-button type="primary" size="large" class="submit-button" v-stable-loading="submitting" :loading="submitting" :disabled="captchaLoading || !captchaUrl" @click="submit">进入工作台</el-button>
        </el-form>
      </section>
    </main>
    <footer class="login-footer">
      <span class="footer-tagline">LESS, BUT BETTER.</span>
      <small class="login-copyright">© 2026 Poria Platform</small>
    </footer>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useSession } from '@/core/session'
import { captcha } from './api'

const session = useSession()
const route = useRoute()
const router = useRouter()
const formRef = ref()
const form = reactive({ username: '', password: '', verifyCode: '', randomStr: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  verifyCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}
const captchaUrl = ref('')
const captchaLoading = ref(false)
const submitting = ref(false)
let captchaRequest

async function refreshCaptcha() {
  if (captchaLoading.value) return
  captchaLoading.value = true
  const controller = new AbortController()
  captchaRequest = controller
  if (captchaUrl.value) URL.revokeObjectURL(captchaUrl.value)
  captchaUrl.value = ''
  form.verifyCode = ''
  form.randomStr = crypto.randomUUID?.() || `${Date.now()}-${Math.random()}`
  try {
    const blob = await captcha(form.randomStr, { signal: controller.signal })
    if (controller.signal.aborted) return
    captchaUrl.value = URL.createObjectURL(blob)
  } catch {
    if (!controller.signal.aborted) ElMessage.error('验证码加载失败，请检查网关连接')
  } finally {
    captchaLoading.value = false
  }
}

async function submit() {
  if (submitting.value || captchaLoading.value || !captchaUrl.value) return
  submitting.value = true
  try {
    if (!(await formRef.value.validate().catch(() => false))) return
    await session.signIn({ ...form })
    const redirect = String(route.query.redirect || '')
    await router.replace(redirect.startsWith('/') && !redirect.startsWith('//') ? redirect : '/index')
  } catch (error) {
    ElMessage.error(error?.message || '登录失败')
    await refreshCaptcha()
  } finally {
    submitting.value = false
  }
}

onMounted(refreshCaptcha)
onBeforeUnmount(() => {
  captchaRequest?.abort()
  if (captchaUrl.value) URL.revokeObjectURL(captchaUrl.value)
})
</script>

<style scoped>
.login-shell {
  --el-color-primary: #202020;
  --el-color-primary-light-3: #626262;
  --el-color-primary-light-5: #949494;
  --el-color-primary-light-7: #c6c6c6;
  --el-color-primary-light-9: #f2f2f2;
  --el-color-primary-dark-2: #080808;
  --el-input-text-color: #242424;
  --login-input-fill: #fafafa;
  display: grid;
  grid-template-rows: auto 1fr auto;
  min-height: 100vh;
  min-height: 100dvh;
  padding: 38px 52px 24px;
  background: #fff;
  color: #242424;
}
.login-header { padding-bottom: 28px; border-bottom: 1px solid #eee; }
.brand { display: flex; align-items: center; gap: 12px; font-size: 24px; line-height: 32px; font-weight: 700; }
.brand-mark { display: grid; width: 30px; height: 30px; place-items: center; border-radius: 6px; background: #202020; color: #fff; font-size: 22px; font-weight: 700; }
.login-main { display: flex; align-items: center; justify-content: center; min-width: 0; padding: 48px 0; }
.login-card { width: min(390px, 100%); }
h2 { margin: 0 0 12px; color: #242424; font-size: 32px; line-height: 44px; letter-spacing: -.025em; }
.login-intro { margin: 0 0 34px; color: #858585; font-size: 14px; line-height: 22px; }
.login-card :deep(.el-form-item) { margin-bottom: 26px; }
.login-card :deep(.el-form-item__label) { height: auto; margin-bottom: 8px; padding: 0; color: #363636; font-size: 14px; line-height: 20px; font-weight: 600; }
.login-card :deep(.el-input__wrapper) { min-height: 48px; padding: 0 15px; border-radius: 3px; background: var(--login-input-fill); box-shadow: 0 0 0 1px #e1e1e1 inset; transition: box-shadow .18s ease; }
.login-card :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px #a0a0a0 inset; }
.login-card :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 1px #303030 inset; }
.login-card :deep(.el-form-item.is-error .el-input__wrapper) { box-shadow: 0 0 0 1px var(--el-color-danger) inset; }
.login-card :deep(.el-form-item__error) { padding-top: 6px; font-size: 12px; line-height: 16px; }
.login-card :deep(.el-input__inner) { color: #242424; font-size: 14px; }
.login-card :deep(.el-input__inner::placeholder) { color: #a0a0a0; }
/* 覆盖浏览器自动填充背景，保留账号和密码的自动填充功能。 */
.login-card :deep(.el-input__inner:-webkit-autofill) {
  -webkit-box-shadow: 0 0 0 1000px var(--login-input-fill, #fff) inset !important;
  box-shadow: 0 0 0 1000px var(--login-input-fill, #fff) inset !important;
  -webkit-text-fill-color: var(--el-input-text-color, #242424) !important;
  caret-color: var(--el-input-text-color, #242424);
}
.login-card :deep(.el-input__inner:autofill) {
  box-shadow: 0 0 0 1000px var(--login-input-fill, #fff) inset !important;
  -webkit-text-fill-color: var(--el-input-text-color, #242424) !important;
  caret-color: var(--el-input-text-color, #242424);
}
.login-card :deep(.captcha-item) { margin-bottom: 26px; }
.captcha-row { display: flex; width: 100%; gap: 12px; }
.captcha-row :deep(.el-input) { flex: 1; min-width: 0; }
.captcha-button { width: 126px; height: 48px; flex: 0 0 126px; overflow: hidden; padding: 0; border: 1px solid #e1e1e1; border-radius: 3px; background: #fff; color: #858585; cursor: pointer; }
.captcha-button:hover { border-color: #a0a0a0; }
.captcha-button:focus-visible { outline: 2px solid #303030; outline-offset: 3px; }
.captcha-button:disabled { cursor: wait; }
.captcha-button img { display: block; width: 100%; height: 100%; object-fit: contain; }
.submit-button { width: 100%; height: 48px; border-radius: 3px; font-size: 15px; font-weight: 600; letter-spacing: .12em; }
.login-help { margin: 0 0 22px; color: #858585; font-size: 12px; line-height: 18px; }
.login-footer { position: relative; display: flex; justify-content: center; min-height: 18px; color: #aaa; font-size: 12px; line-height: 18px; }
.footer-tagline { position: absolute; left: 0; font-size: 10px; letter-spacing: .02em; }
.login-copyright { font-size: 12px; }
@media (max-width: 600px) {
  .login-shell { padding: 24px 20px 20px; }
  .login-header { padding-bottom: 22px; }
  .brand { font-size: 22px; }
  .login-main { padding: 38px 0; }
  h2 { font-size: 28px; }
  .captcha-row { gap: 8px; }
  .captcha-button { width: 110px; flex-basis: 110px; }
  .footer-tagline { display: none; }
}
</style>
