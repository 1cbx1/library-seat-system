<template>
  <div class="page-shell">
    <UserNav />

    <main class="page-container user-center-page">
      <div class="profile-card soft-card">
        <div class="avatar">{{ userForm.realName?.slice(0, 1) || userForm.username?.slice(0, 1) || '用' }}</div>
        <div>
          <h1>个人中心</h1>
          <p>{{ userForm.username }} · 信用分 {{ userForm.creditScore }}</p>
        </div>
        <el-tag :type="userForm.role === 'admin' ? 'danger' : 'info'">{{ userForm.role === 'admin' ? '管理员' : '普通用户' }}</el-tag>
      </div>

      <section class="profile-metrics">
        <article v-for="item in profileMetrics" :key="item.label" class="metric-card soft-card">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <p>{{ item.tip }}</p>
        </article>
      </section>

      <section class="center-layout">
        <aside class="credit-card soft-card">
          <span>当前信用分</span>
          <strong>{{ userForm.creditScore }}</strong>
          <el-progress :percentage="Number(userForm.creditScore || 0)" :stroke-width="10" color="#1f6f5b" />
          <p>信用分影响预约资格，请按时签到、签退并遵守自习室规则。</p>
        </aside>

        <el-tabs v-model="activeTab" class="center-tabs soft-card">
          <el-tab-pane label="基本信息" name="info">
            <section class="form-card">
              <el-form :model="userForm" label-position="top">
                <div class="form-grid">
                  <el-form-item label="用户名"><el-input v-model="userForm.username" disabled /></el-form-item>
                  <el-form-item label="真实姓名"><el-input v-model="userForm.realName" /></el-form-item>
                </div>
                <div class="form-grid">
                  <el-form-item label="手机号"><el-input v-model="userForm.phone" /></el-form-item>
                  <el-form-item label="邮箱"><el-input v-model="userForm.email" /></el-form-item>
                </div>
                <el-form-item label="信用分"><el-input v-model="userForm.creditScore" disabled /></el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="loading" @click="handleUpdateProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </section>
          </el-tab-pane>

          <el-tab-pane label="修改密码" name="password">
            <section class="form-card">
              <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top">
                <el-form-item label="原密码" prop="oldPassword"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
                <div class="form-grid">
                  <el-form-item label="新密码" prop="newPassword"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
                  <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="passwordForm.confirmPassword" type="password" show-password /></el-form-item>
                </div>
                <el-form-item>
                  <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </section>
          </el-tab-pane>
        </el-tabs>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import UserNav from '../../components/UserNav.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('info')
const loading = ref(false)
const passwordLoading = ref(false)
const passwordFormRef = ref(null)

const userForm = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  creditScore: 100,
  role: 'user'
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileMetrics = computed(() => [
  { label: '账号类型', value: userForm.value.role === 'admin' ? '管理员' : '普通用户', tip: '系统访问权限' },
  { label: '信用状态', value: Number(userForm.value.creditScore || 0) >= 80 ? '良好' : '需关注', tip: '按时签到签退可保持信用' },
  { label: '联系方式', value: userForm.value.phone ? '已填写' : '未填写', tip: '用于管理员核对信息' }
])

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: validateConfirmPassword, trigger: 'blur' }]
}

const loadUserInfo = async () => {
  const res = await request.get('/user/info')
  userForm.value = {
    username: res.data?.username || '',
    realName: res.data?.realName || '',
    phone: res.data?.phone || '',
    email: res.data?.email || '',
    creditScore: res.data?.creditScore || 100,
    role: res.data?.role || 'user'
  }
}

const handleUpdateProfile = async () => {
  loading.value = true
  try {
    await request.put('/user/profile', {
      realName: userForm.value.realName,
      phone: userForm.value.phone,
      email: userForm.value.email
    })
    await userStore.getUserInfo()
    ElMessage.success('资料更新成功')
  } finally {
    loading.value = false
  }
}

const handleChangePassword = async () => {
  await passwordFormRef.value.validate()
  passwordLoading.value = true
  try {
    await request.post('/user/password', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.push('/login')
  } finally {
    passwordLoading.value = false
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadUserInfo()
})
</script>

<style scoped>
.user-center-page {
  padding: 32px 0 48px;
}

.profile-card {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 26px;
  margin-bottom: 20px;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.94), rgba(232, 240, 235, 0.9)),
    url("https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=1400&q=80") right center/36% 100% no-repeat;
}

.profile-card::after {
  content: "";
  position: absolute;
  right: 28px;
  bottom: -34px;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(31, 111, 91, 0.08);
}

.avatar {
  width: 58px;
  height: 58px;
  display: grid;
  place-items: center;
  border-radius: 16px;
  color: #fff;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  font-size: 24px;
  font-weight: 700;
}

.profile-card h1 {
  margin: 0;
  font-size: 30px;
}

.profile-card p {
  margin: 6px 0 0;
  color: var(--muted);
}

.profile-card .el-tag {
  margin-left: auto;
}

.profile-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.metric-card {
  padding: 18px;
}

.metric-card span {
  color: var(--muted);
  font-size: 13px;
}

.metric-card strong {
  display: block;
  margin-top: 8px;
  color: var(--primary);
  font-size: 24px;
}

.metric-card p {
  margin: 8px 0 0;
  color: var(--muted);
  font-size: 13px;
}

.center-tabs {
  min-width: 0;
  padding: 6px 22px 22px;
}

.form-card {
  max-width: 760px;
  padding-top: 18px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.center-layout {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 20px;
}

.credit-card {
  padding: 24px;
  align-self: start;
}

.credit-card span {
  color: var(--muted);
}

.credit-card strong {
  display: block;
  margin: 10px 0 14px;
  color: var(--primary);
  font-size: 54px;
  line-height: 1;
}

.credit-card p {
  margin: 16px 0 0;
  color: var(--muted);
  line-height: 1.8;
}

@media (max-width: 900px) {
  .center-layout,
  .profile-metrics {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .user-center-page {
    padding: 22px 0 38px;
  }

  .profile-card {
    align-items: flex-start;
    flex-direction: column;
    padding: 20px;
    background:
      linear-gradient(120deg, rgba(255, 255, 255, 0.94), rgba(232, 240, 235, 0.9)),
      url("https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=760&q=80") right bottom/60% auto no-repeat;
  }

  .profile-card .el-tag {
    margin-left: 0;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .center-tabs,
  .credit-card {
    padding: 18px;
  }

  .form-card .el-button {
    width: 100%;
  }
}
</style>
