import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/user/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/user/Register.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/user/Home.vue')
  },
  {
    path: '/announcements',
    name: 'Announcement',
    component: () => import('../views/user/Announcement.vue')
  },
  {
    path: '/studyrooms',
    name: 'StudyRooms',
    component: () => import('../views/user/StudyRoom.vue')
  },
  {
    path: '/studyrooms/:id/seats',
    name: 'SeatMap',
    component: () => import('../views/user/SeatMap.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my/reservations',
    name: 'MyReservation',
    component: () => import('../views/user/MyReservation.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/forum',
    name: 'Forum',
    component: () => import('../views/user/Forum.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/center',
    name: 'UserCenter',
    component: () => import('../views/user/UserCenter.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/message',
    name: 'MessageBoard',
    component: () => import('../views/user/MessageBoard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/rooms',
    name: 'AdminRoomMgmt',
    component: () => import('../views/admin/RoomManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/seats',
    name: 'AdminSeatMgmt',
    component: () => import('../views/admin/SeatManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/reservations',
    name: 'AdminReservationAudit',
    component: () => import('../views/admin/ReservationAudit.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUserMgmt',
    component: () => import('../views/admin/UserManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/banners',
    name: 'AdminBannerMgmt',
    component: () => import('../views/admin/BannerManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/announcements',
    name: 'AdminAnnouncementMgmt',
    component: () => import('../views/admin/AnnouncementMgmt.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/forum',
    name: 'AdminForumMgmt',
    component: () => import('../views/admin/ForumManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/messages',
    name: 'AdminMessageMgmt',
    component: () => import('../views/admin/MessageManagement.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/stats',
    name: 'AdminStats',
    component: () => import('../views/admin/Stats.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
