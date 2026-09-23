<template>
  <div class="login-shell">
    <aside class="login-story">
      <div class="brand"><span class="brand-mark">P</span><span>PORIA<span class="brand-dot">.</span></span></div>
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
.login-shell { min-height: 100vh; display: grid; grid-template-columns: minmax(400px, 52%) 1fr; background: #f6f8fb; }
.login-story { display: flex; flex-direction: column; justify-content: space-between; position: relative; overflow: hidden; padding: 48px 6vw; color: white; background: radial-gradient(circle at 85% 15%, #2768a8 0, transparent 28%), radial-gradient(circle at 0 100%, #226d77 0, transparent 30%), #071c32; }
.login-story::before { content: ''; position: absolute; width: 620px; height: 620px; border: 1px solid #ffffff24; border-radius: 50%; right: -270px; bottom: -270px; box-shadow: 0 0 0 85px #ffffff06, 0 0 0 170px #ffffff04; }
.brand, .login-mobile-brand { display: flex; align-items: center; gap: 13px; font-size: 23px; font-weight: 800; letter-spacing: .1em; }
.brand-mark { width: 42px; height: 42px; display: grid; place-items: center; border-radius: 13px; background: #d1efdd; color: #0c413d; font-size: 25px; letter-spacing: 0; }
.brand-dot, .login-mobile-brand span { color: #85d9aa; }
.story-body { position: relative; max-width: 640px; }
.eyebrow, .login-kicker { font-size: 12px; font-weight: 800; letter-spacing: .22em; text-transform: uppercase; color: #8ed7b3; }
h1 { margin: 24px 0; font-size: clamp(42px, 4.2vw, 76px); line-height: 1.13; letter-spacing: -.055em; }
h1 em { font-style: normal; color: #8ed7b3; }
.story-body p { max-width: 450px; line-height: 1.9; color: #bdd0de; font-size: 16px; }
.story-line { display: flex; align-items: center; gap: 20px; margin-top: 70px; color: #b2cad8; font-size: 13px; }
.story-line span:first-child { width: 38px; height: 1px; background: #90dcb7; }
.login-story small { color: #90afc2; }
.login-main { display: grid; place-items: center; padding: 32px; }
.login-card { width: min(430px, 100%); }
.login-mobile-brand { display: none; color: #0d273e; }
.login-kicker { color: #21836c; }
h2 { margin: 12px 0 9px; font-size: 38px; letter-spacing: -.04em; color: #122b3f; }
.login-intro { margin: 0 0 38px; color: #738596; }
.login-card :deep(.el-form-item) { margin-bottom: 24px; }
.login-card :deep(.el-form-item__label) { color: #30485b; font-weight: 700; }
.login-card :deep(.el-input__wrapper) { min-height: 48px; border-radius: 10px; box-shadow: 0 0 0 1px #dce5eb inset; }
.captcha-row { display: flex; width: 100%; gap: 10px; }
.captcha-button { width: 120px; height: 48px; flex: 0 0 120px; overflow: hidden; padding: 0; border: 1px solid #dce5eb; border-radius: 10px; background: white; color: #6b7b88; cursor: pointer; }
.captcha-button img { width: 100%; height: 100%; object-fit: cover; }
.submit-button { width: 100%; height: 50px; margin-top: 8px; border-radius: 10px; font-weight: 700; }
.submit-button span { margin-left: 8px; font-size: 19px; }
.login-help { margin-top: 24px; text-align: center; color: #93a1ac; font-size: 12px; }
@media (max-width: 900px) { .login-shell { grid-template-columns: 1fr; } .login-story { display: none; } .login-mobile-brand { display: flex; margin-bottom: 55px; } .login-main { min-height: 100vh; } }
</style>
