<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="admin-logo">
        <span class="admin-logo__mark">管</span>
        <span>预约管理后台</span>
      </div>
      <nav class="admin-nav">
        <router-link to="/admin">数据概览</router-link>
        <router-link to="/admin/rooms">自习室管理</router-link>
        <router-link to="/admin/seats">座位管理</router-link>
        <router-link to="/admin/reservations">预约审核</router-link>
        <router-link to="/admin/users">用户管理</router-link>
        <router-link to="/admin/banners">轮播图管理</router-link>
        <router-link to="/admin/announcements">公告管理</router-link>
        <router-link to="/admin/forum">论坛管理</router-link>
        <router-link to="/admin/messages">留言管理</router-link>
        <router-link to="/admin/stats">统计图表</router-link>
        <router-link to="/">返回前台</router-link>
      </nav>
    </aside>

    <main class="admin-main">
      <div class="admin-topbar">
        <strong>{{ title }}</strong>
        <div class="admin-user">
          <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </div>
      </div>
      <section class="admin-content">
        <slot />
      </section>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

defineProps({
  title: {
    type: String,
    default: '管理后台'
  }
})

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-user {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #475569;
}
</style>
