<template>
  <div class="profile-page" :aria-busy="loading">
    <header class="profile-heading">
      <div><h1>个人中心</h1><p>查看你的账号资料与角色信息。</p></div>
      <el-button v-stable-loading="loading" :loading="loading" :disabled="!canRead" @click="loadProfile">刷新资料</el-button>
    </header>
    <div class="profile-grid">
      <section class="profile-identity" aria-label="账号概览">
        <div class="profile-avatar" aria-hidden="true">{{ initial }}</div>
        <h2>{{ profile?.username || session.displayName }}</h2>
        <p class="profile-identity__caption">{{ profile?.deptName || (profile ? '未设置部门' : '平台账号') }}</p>
        <span v-if="profile" class="profile-status">{{ accountStatus }}</span>
        <div class="profile-identity__divider" />
        <div class="profile-identity__label">所属角色</div>
        <div v-if="roles.length" class="profile-roles"><span v-for="role in roles" :key="role">{{ role }}</span></div>
        <p v-else class="profile-empty">{{ profile ? '暂未分配角色' : loading ? '正在加载…' : '暂无资料' }}</p>
        <router-link class="profile-home" to="/index">返回工作台 <span aria-hidden="true">↗</span></router-link>
      </section>
      <div class="profile-details">
        <section class="profile-section" aria-labelledby="profile-basic-title">
          <header class="profile-section__heading"><div><h2 id="profile-basic-title">基本资料</h2><p>当前登录账号的信息</p></div></header>
          <div v-if="loading && !profile" class="profile-loading" role="status">正在加载账号资料…</div>
          <dl v-else class="profile-fields">
            <div><dt>账号名称</dt><dd>{{ profile?.username || '—' }}</dd></div>
            <div><dt>账号编号</dt><dd>{{ profile?.userId ?? '—' }}</dd></div>
            <div><dt>手机号</dt><dd class="profile-phone"><span>{{ profile?.phone || (profile ? '未设置' : '—') }}</span><el-button link :disabled="!canEdit || !profile || loading" @click="editDialog.open('phone', profile)">修改</el-button></dd></div>
            <div><dt>所属部门</dt><dd>{{ profile?.deptName || (profile ? '未设置' : '—') }}</dd></div>
            <div><dt>创建时间</dt><dd>{{ formatTime(profile?.createTime) }}</dd></div>
            <div><dt>更新时间</dt><dd>{{ formatTime(profile?.updateTime) }}</dd></div>
          </dl>
          <div class="profile-security"><div><strong>登录密码</strong><p>定期更新密码，保护账号安全。</p></div><el-button :disabled="!canEdit || !profile || loading" @click="editDialog.open('password', profile)">修改密码</el-button></div>
        </section>
        <section class="profile-section" aria-labelledby="profile-access-title">
          <header class="profile-section__heading"><div><h2 id="profile-access-title">账号与权限</h2><p>你的账号状态与角色分配</p></div></header>
          <dl class="profile-access">
            <div><dt>登录状态</dt><dd>{{ session.authenticated ? '已登录' : '未登录' }}</dd></div>
            <div><dt>账号状态</dt><dd>{{ profile ? accountStatus : '—' }}</dd></div>
            <div><dt>已分配角色</dt><dd>{{ profile ? `${roles.length} 个` : '—' }}</dd></div>
          </dl>
          <p class="profile-footnote">部门和角色由管理员维护，需要调整时请联系管理员。</p>
        </section>
      </div>
    </div>
    <EditDialog ref="editDialog" @saved="handleSaved" />
  </div>
</template>

<script setup>
import { computed, onActivated, onMounted, ref } from 'vue'
import { useSession } from '@/core/session'
import { useResourcePermissions } from '@/core/permissions'
import { fetchProfile } from './api'
import EditDialog from './EditDialog.vue'

const session = useSession()
const { canRead, canEdit } = useResourcePermissions('profile')
const editDialog = ref()
const profile = ref(null)
const loading = ref(false)
const roles = computed(() => profile.value?.roles || [])
const initial = computed(() => (profile.value?.username || session.displayName).slice(0, 1).toUpperCase())
const accountStatus = computed(() => String(profile.value?.lockFlag) === '0' ? '正常' : String(profile.value?.lockFlag) === '9' ? '已锁定' : '未知')

async function loadProfile() {
  if (!canRead.value || loading.value) return
  loading.value = true
  try {
    const data = await fetchProfile()
    if (!data || typeof data.username !== 'string') throw new Error('未获取到账号资料')
    profile.value = data
  } catch {
    // 资料加载失败时保留当前资料，可通过页头刷新重试。
  } finally { loading.value = false }
}
function formatTime(value) {
  return value ? String(value).replace('T', ' ').replace(/\.\d+$/, '') : '—'
}
async function handleSaved(mode) {
  if (mode === 'password') {
    profile.value = null
    await session.signOut()
  } else { await loadProfile() }
}

onMounted(loadProfile)
onActivated(loadProfile)
</script>

<style scoped>
.profile-page { max-width: 1200px; margin: 0 auto; }
.profile-heading { display: flex; align-items: center; justify-content: space-between; gap: 20px; padding: 6px 0 28px; }
.profile-heading h1 { margin: 0; color: var(--ink); font-size: 28px; font-weight: 650; letter-spacing: -.04em; }
.profile-heading p { margin: 9px 0 0; color: var(--muted); font-size: 13px; }
.profile-grid { display: grid; grid-template-columns: 280px minmax(0, 1fr); gap: 24px; align-items: start; }
.profile-identity, .profile-section { background: #fff; border: 1px solid var(--line); border-radius: 6px; }
.profile-identity { padding: 36px 24px 24px; text-align: center; }
.profile-avatar { display: grid; place-items: center; width: 80px; height: 80px; margin: 0 auto 20px; background: #242424; color: #fff; border-radius: 50%; font-size: 30px; font-weight: 600; }
.profile-identity h2 { margin: 0; overflow-wrap: anywhere; font-size: 21px; font-weight: 650; }
.profile-identity__caption { margin: 9px 0 16px; color: var(--muted); font-size: 13px; }
.profile-status { display: inline-flex; align-items: center; gap: 7px; padding: 5px 11px; border-radius: 20px; background: #f3f3f3; color: #555; font-size: 12px; }
.profile-status::before { content: ''; width: 5px; height: 5px; border-radius: 50%; background: #555; }
.profile-identity__divider { height: 1px; margin: 28px 0 22px; background: var(--line); }
.profile-identity__label { margin-bottom: 14px; color: var(--muted); font-size: 12px; }
.profile-roles { display: flex; justify-content: center; flex-wrap: wrap; gap: 8px; }
.profile-roles span { max-width: 100%; padding: 6px 10px; border: 1px solid var(--line); border-radius: 3px; color: #555; font-size: 12px; overflow-wrap: anywhere; }
.profile-empty { color: var(--muted); font-size: 13px; }
.profile-home { display: flex; justify-content: space-between; align-items: center; margin-top: 30px; padding: 13px 14px; background: #f7f7f7; border-radius: 3px; font-size: 13px; }
.profile-home:hover { background: #eee; }
.profile-details { display: grid; gap: 24px; min-width: 0; }
.profile-section__heading { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 24px 28px 20px; border-bottom: 1px solid var(--line); }
.profile-section__heading h2 { margin: 0; font-size: 16px; font-weight: 600; }
.profile-section__heading p { margin: 8px 0 0; color: var(--muted); font-size: 12px; }
.profile-section__note { color: var(--muted); font-size: 12px; }
.profile-fields { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 28px 32px; margin: 0; padding: 28px; }
.profile-fields dt, .profile-access dt { margin-bottom: 10px; color: var(--muted); font-size: 12px; }
.profile-fields dd, .profile-access dd { margin: 0; color: var(--ink); font-size: 14px; line-height: 1.6; overflow-wrap: anywhere; }
.profile-access { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 20px; margin: 0; padding: 28px; }
.profile-phone { display: flex; align-items: center; flex-wrap: wrap; gap: 12px; }
.profile-phone .el-button { color: var(--ink); font-size: 12px; text-decoration: underline; text-underline-offset: 3px; }
.profile-security { display: flex; align-items: center; justify-content: space-between; gap: 18px; padding: 0 28px 28px; }
.profile-security strong { font-size: 13px; font-weight: 500; }
.profile-security p { margin: 8px 0 0; color: var(--muted); font-size: 12px; line-height: 1.7; }
.profile-footnote { margin: 0; padding: 16px 28px; border-top: 1px solid var(--line); color: var(--muted); font-size: 12px; line-height: 1.7; }
.profile-loading { padding: 28px; color: var(--muted); font-size: 13px; }
.profile-home:focus-visible { outline: 2px solid var(--accent); outline-offset: 2px; }
@media (max-width: 1100px) { .profile-grid { grid-template-columns: 240px minmax(0, 1fr); gap: 18px; } }
@media (max-width: 760px) { .profile-grid { grid-template-columns: 1fr; } .profile-heading { align-items: flex-start; } .profile-heading h1 { font-size: 25px; } .profile-section__heading { padding: 22px; } .profile-fields, .profile-access { padding: 22px; } .profile-footnote { padding: 16px 22px; } }
@media (max-width: 480px) { .profile-fields { grid-template-columns: 1fr; } .profile-access { gap: 12px; } }
</style>
