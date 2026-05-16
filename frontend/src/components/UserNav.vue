<template>
  <header class="app-nav">
    <div class="app-nav__inner">
      <router-link class="brand" to="/">
        <span class="brand__mark">座</span>
        <span class="brand__text">
          <strong>图书馆座位预约系统</strong>
          <span>Library Seat Booking</span>
        </span>
      </router-link>

      <nav class="nav-links desktop-nav">
        <router-link v-for="item in navItems" :key="item.to" :to="item.to">{{ item.label }}</router-link>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="nav-user">
              {{ userStore.userInfo?.realName || userStore.userInfo?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </nav>

      <button class="mobile-menu-button" type="button" aria-label="打开菜单" @click="drawerVisible = true">
        <el-icon><Menu /></el-icon>
      </button>
    </div>

    <el-drawer v-model="drawerVisible" title="菜单" direction="rtl" size="78%" custom-class="mobile-nav-drawer">
      <nav class="mobile-drawer-links">
        <router-link v-for="item in navItems" :key="item.to" :to="item.to" @click="drawerVisible = false">
          {{ item.label }}
        </router-link>
        <router-link v-if="!userStore.isLoggedIn" to="/login" @click="drawerVisible = false">登录</router-link>
        <router-link v-if="!userStore.isLoggedIn" to="/register" @click="drawerVisible = false">注册</router-link>
        <button v-if="userStore.isLoggedIn" type="button" @click="handleMobileLogout">退出登录</button>
      </nav>
    </el-drawer>
  </header>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown, Menu } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const drawerVisible = ref(false)

const navItems = computed(() => {
  const items = [
    { to: '/', label: '首页' },
    { to: '/studyrooms', label: '自习室' },
    { to: '/announcements', label: '公告' }
  ]
  if (userStore.isLoggedIn) {
    items.push(
      { to: '/forum', label: '论坛' },
      { to: '/message', label: '留言板' },
      { to: '/my/reservations', label: '我的预约' },
      { to: '/user/center', label: '个人中心' }
    )
  }
  if (userStore.isAdmin) {
    items.push({ to: '/admin', label: '后台' })
  }
  return items
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

const handleMobileLogout = () => {
  drawerVisible.value = false
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.mobile-menu-button {
  display: none;
  width: 42px;
  height: 42px;
  place-items: center;
  border: 1px solid rgba(31, 111, 91, 0.14);
  border-radius: 8px;
  color: var(--primary);
  background: rgba(255, 255, 255, 0.82);
  font-size: 22px;
}

.mobile-drawer-links {
  display: grid;
  gap: 10px;
}

.mobile-drawer-links a,
.mobile-drawer-links button {
  min-height: 44px;
  display: flex;
  align-items: center;
  border: 1px solid rgba(215, 225, 220, 0.9);
  border-radius: 8px;
  padding: 0 14px;
  color: #26352f;
  background: #f7faf7;
  font-size: 15px;
  font-weight: 700;
}

.mobile-drawer-links button {
  cursor: pointer;
}

.mobile-drawer-links a.router-link-active {
  color: #fff;
  border-color: var(--primary);
  background: var(--primary);
}

@media (max-width: 820px) {
  .app-nav__inner {
    min-height: 62px;
    flex-direction: row;
    align-items: center;
    padding: 0;
  }

  .desktop-nav {
    display: none;
  }

  .mobile-menu-button {
    display: grid;
    flex: 0 0 auto;
  }

  .brand {
    min-width: 0;
  }

  .brand__text strong {
    max-width: 190px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .brand__text span {
    display: none;
  }
}
</style>
