<template>
  <div class="lock-page">
    <el-card class="lock-card" shadow="always">
      <template #header>屏幕已锁定</template>
      <p>为保护当前账号，请重新登录后继续使用管理台。</p>
      <el-button type="primary" @click="relogin">退出并重新登录</el-button>
    </el-card>
  </div>
</template>
<script setup>
  import useUserStore from '@/store/modules/user'
  const router = useRouter()
  async function relogin() {
    await useUserStore().logOut();
    router.replace('/login')
  }

</script>
<style scoped>
  .lock-page {
    min-height: 100vh;
    display: grid;
    place-items: center;
    background: #001529;
  }

  .lock-card {
    width: min(360px, calc(100vw - 40px));
    text-align: center;
  }

</style>
