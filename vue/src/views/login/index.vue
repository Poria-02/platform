<template>
  <div class="login-shell">
    <aside class="login-story">
      <div class="brand"><span class="brand-mark" aria-hidden="true"><i /><i /><i /><i /></span><span>PORIA</span></div>
      <div class="story-body">
        <div class="eyebrow">PORIA PLATFORM · CONSOLE</div>
        <h1>让复杂系统，<br><em>变得清晰可控。</em></h1>
        <p>统一管理用户、权限、路由与平台服务。菜单由 UPMS 分发，每个页面都对应独立的业务目录。</p>
        <div class="story-line"><span></span><span>安全访问</span><span>清晰协作</span><span>灵活扩展</span></div>
      </div>
      <small>© 2026 Poria Platform</small>
    </aside>
    <main class="login-main">
      <div class="login-card">
        <div class="login-mobile-brand">PORIA<span>.</span></div>
        <div class="login-kicker">欢迎回来</div>
        <h2>登录工作台</h2>
        <p class="login-intro">使用平台账号继续访问你的工作空间。</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" size="large" autocomplete="username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" size="large" type="password" show-password autocomplete="current-password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="验证码" prop="verifyCode">
            <div class="captcha-row">
              <el-input v-model="form.verifyCode" size="large" autocomplete="one-time-code" placeholder="输入图片中的字符" />
              <button class="captcha-button" type="button" :disabled="captchaLoading" title="刷新验证码" @click="refreshCaptcha">
                <img v-if="captchaUrl" :src="captchaUrl" alt="验证码" />
                <span v-else>{{ captchaLoading ? '加载中' : '点击刷新' }}</span>
              </button>
            </div>
          </el-form-item>
          <el-button type="primary" size="large" class="submit-button" :loading="submitting" @click="submit">进入工作台 <span aria-hidden="true">↗</span></el-button>
        </el-form>
        <div class="login-help">验证码看不清？点击图片换一张</div>
      </div>
    </main>
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

async function refreshCaptcha() {
  captchaLoading.value = true
  form.verifyCode = ''
  form.randomStr = crypto.randomUUID?.() || `${Date.now()}-${Math.random()}`
  try {
    const blob = await captcha(form.randomStr)
    if (captchaUrl.value) URL.revokeObjectURL(captchaUrl.value)
    captchaUrl.value = URL.createObjectURL(blob)
  } catch {
    ElMessage.error('验证码加载失败，请检查网关连接')
  } finally {
    captchaLoading.value = false
  }
}

async function submit() {
  if (!(await formRef.value.validate().catch(() => false))) return
  submitting.value = true
  try {
    await session.signIn(form)
    const redirect = String(route.query.redirect || '')
    await router.replace(redirect.startsWith('/') && !redirect.startsWith('//') ? redirect : '/index')
  } catch (error) {
    ElMessage.error(error?.message || '登录失败')
    refreshCaptcha()
  } finally {
    submitting.value = false
  }
}

onMounted(refreshCaptcha)
onBeforeUnmount(() => { if (captchaUrl.value) URL.revokeObjectURL(captchaUrl.value) })
</script>

<style scoped>
.login-shell { min-height: 100vh; display: grid; grid-template-columns: minmax(400px, 50%) 1fr; background: #fff; }
.login-story { position: relative; display: flex; flex-direction: column; justify-content: space-between; overflow: hidden; padding: 48px 6vw; border-right: 1px solid #e7edf2; background: linear-gradient(145deg, #f0faf6, #f8fbfc 70%); color: #26384e; }
.login-story::before { content: ""; position: absolute; right: -205px; bottom: -205px; width: 470px; height: 470px; border: 1px solid #b3e3d3; border-radius: 50%; box-shadow: 0 0 0 22px #f5fbf8, 0 0 0 23px #c2e8da, 0 0 0 45px #f5fbf8, 0 0 0 46px #d2eee3, 0 0 0 68px #f5fbf8, 0 0 0 69px #ddf1e9; }
.login-story::after { content: ""; position: absolute; right: 200px; bottom: 90px; width: 10px; height: 10px; border-radius: 50%; background: #f47d52; }
.brand, .login-mobile-brand { display: flex; align-items: center; gap: 13px; font-size: 22px; font-weight: 800; letter-spacing: .14em; }
.brand-mark { flex: 0 0 34px; width: 34px; height: 34px; display: grid; grid-template-columns: repeat(2, 1fr); grid-template-rows: repeat(2, 1fr); gap: 3px; transform: rotate(-8deg); }
.brand-mark i { display: block; border-radius: 3px; background: #89d4b9; }
.brand-mark i:nth-child(2) { background: #cceee2; }
.brand-mark i:nth-child(3) { background: #b3e3d0; }
.brand-mark i:nth-child(4) { background: #f5a07f; }
.story-body { position: relative; z-index: 1; max-width: 640px; }
.eyebrow, .login-kicker { color: #319a7a; font-size: 11px; font-weight: 800; letter-spacing: .16em; text-transform: uppercase; }
h1 { margin: 21px 0; font-size: clamp(41px, 4.2vw, 68px); line-height: 1.18; letter-spacing: -.055em; }
h1 em { color: #2ba17f; font-style: normal; }
.story-body p { max-width: 445px; color: #778a9d; font-size: 15px; line-height: 1.85; }
.story-line { display: flex; align-items: center; gap: 19px; margin-top: 58px; color: #8496a7; font-size: 12px; }
.story-line span:first-child { width: 35px; height: 1px; background: #5ec3a0; }
.login-story small { z-index: 1; color: #a0aebb; }
.login-main { display: grid; place-items: center; padding: 32px; background: #fff; }
.login-card { width: min(430px, 100%); }
.login-mobile-brand { display: none; color: #26384e; }
.login-mobile-brand span { color: #f47d52; }
h2 { margin: 12px 0 9px; color: #26384e; font-size: 36px; letter-spacing: -.04em; }
.login-intro { margin: 0 0 36px; color: #8392a5; }
.login-card :deep(.el-form-item) { margin-bottom: 23px; }
.login-card :deep(.el-form-item__label) { color: #45596c; font-weight: 700; }
.login-card :deep(.el-input__wrapper) { min-height: 48px; border-radius: 9px; box-shadow: 0 0 0 1px #dfe8ee inset; }
.captcha-row { display: flex; width: 100%; gap: 10px; }
.captcha-button { width: 120px; height: 48px; flex: 0 0 120px; overflow: hidden; padding: 0; border: 1px solid #dfe8ee; border-radius: 9px; background: #fff; color: #778a9d; cursor: pointer; }
.captcha-button img { width: 100%; height: 100%; object-fit: cover; }
.submit-button { width: 100%; height: 50px; margin-top: 8px; border-radius: 9px; font-weight: 700; }
.submit-button span { margin-left: 8px; font-size: 18px; }
.login-help { margin-top: 23px; color: #9aa9b6; font-size: 12px; text-align: center; }
@media (max-width: 900px) { .login-shell { grid-template-columns: 1fr; } .login-story { display: none; } .login-mobile-brand { display: flex; margin-bottom: 55px; } .login-main { min-height: 100vh; } }
</style>
