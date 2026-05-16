<template>
  <div class="auth-page">
    <router-link class="auth-home" to="/">
      <el-icon><ArrowLeft /></el-icon>
      返回首页
    </router-link>

    <section class="auth-shell soft-card">
      <aside class="auth-visual">
        <div class="visual-badge">
          <el-icon><Collection /></el-icon>
          Library Seat Booking
        </div>
        <h1>图书馆座位预约系统</h1>
        <p>完成登录后可预约座位、查看审核结果，并在到馆后完成签到签退。</p>

        <div class="flow-list">
          <span>选择自习室</span>
          <span>提交预约</span>
          <span>审核通过</span>
          <span>签到使用</span>
        </div>

        <div class="visual-preview">
          <div class="preview-head">
            <strong>今日座位概览</strong>
            <em>实时状态</em>
          </div>
          <div class="mini-seat-grid" aria-hidden="true">
            <i class="free"></i>
            <i class="free"></i>
            <i class="reserved"></i>
            <i class="free"></i>
            <i class="occupied"></i>
            <i class="free"></i>
            <i class="locked"></i>
            <i class="free"></i>
            <i class="free"></i>
            <i class="occupied"></i>
            <i class="free"></i>
            <i class="reserved"></i>
          </div>
        </div>
      </aside>

      <div class="auth-form">
        <div class="form-title">
          <span>账号登录</span>
          <h2>欢迎回来</h2>
          <p>请输入账号信息进入系统。</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" size="large" :prefix-icon="User" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" size="large" :prefix-icon="Lock" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item class="remember-row">
            <el-checkbox v-model="form.remember">记住我</el-checkbox>
          </el-form-item>
          <el-button class="auth-submit" type="primary" size="large" :loading="loading" @click="handleLogin">登录系统</el-button>
        </el-form>
        <div class="auth-link">还没有账号？<router-link to="/register">立即注册</router-link></div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Collection, Lock, User } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ username: '', password: '', remember: false })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password, form.value.remember)
    const redirect = router.currentRoute.value.query.redirect || (userStore.isAdmin ? '/admin' : '/')
    router.push(redirect)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 44px 24px;
  position: relative;
  background:
    radial-gradient(circle at 16% 18%, rgba(183, 121, 63, 0.18), transparent 28%),
    radial-gradient(circle at 84% 12%, rgba(31, 111, 91, 0.14), transparent 28%),
    linear-gradient(135deg, rgba(31, 111, 91, 0.12), rgba(18, 33, 28, 0.08)),
    #eef3f0;
}

.auth-page::before {
  content: "";
  position: fixed;
  inset: 0;
  pointer-events: none;
  opacity: 0.42;
  background-image:
    linear-gradient(rgba(31, 111, 91, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(31, 111, 91, 0.08) 1px, transparent 1px);
  background-size: 38px 38px;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.75), transparent);
}

.auth-home {
  position: absolute;
  top: 24px;
  left: 28px;
  z-index: 2;
  height: 38px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 0 12px;
  border: 1px solid rgba(215, 225, 220, 0.92);
  border-radius: 8px;
  color: #40564d;
  background: rgba(255, 255, 255, 0.78);
  font-size: 14px;
  font-weight: 700;
  backdrop-filter: blur(12px);
  transition: transform 0.18s ease, color 0.18s ease, border-color 0.18s ease;
}

.auth-home:hover {
  color: var(--primary);
  border-color: rgba(31, 111, 91, 0.28);
  transform: translateY(-1px);
}

.auth-shell {
  width: min(980px, 100%);
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) 410px;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.auth-visual {
  min-height: 600px;
  padding: 46px;
  color: #fff;
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(145deg, rgba(18, 33, 28, 0.94), rgba(31, 111, 91, 0.72)),
    url("../../assets/hero.png") right 32px bottom 34px / 260px auto no-repeat,
    #12211c;
}

.auth-visual::after {
  content: "";
  position: absolute;
  right: -80px;
  bottom: -70px;
  width: 260px;
  height: 260px;
  border-radius: 50%;
  background: rgba(183, 121, 63, 0.18);
}

.visual-badge {
  width: fit-content;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.88);
  background: rgba(255, 255, 255, 0.08);
  font-size: 13px;
  font-weight: 700;
}

.auth-visual h1 {
  max-width: 420px;
  margin: 28px 0 16px;
  font-size: clamp(38px, 5vw, 54px);
  line-height: 1.05;
  text-wrap: balance;
}

.auth-visual p {
  max-width: 430px;
  margin: 0;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.82);
}

.flow-list {
  max-width: 440px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 34px;
}

.flow-list span {
  padding: 12px 14px;
  border: 1px solid rgba(255, 255, 255, 0.13);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.08);
  font-weight: 700;
}

.visual-preview {
  width: min(320px, 100%);
  margin-top: 34px;
  padding: 18px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.10);
  backdrop-filter: blur(12px);
}

.preview-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.preview-head em {
  color: rgba(255, 255, 255, 0.68);
  font-size: 12px;
  font-style: normal;
}

.mini-seat-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}

.mini-seat-grid i {
  height: 28px;
  border-radius: 7px;
}

.mini-seat-grid .free { background: #41b883; }
.mini-seat-grid .reserved { background: #4f8fc4; }
.mini-seat-grid .occupied { background: #9aa69f; }
.mini-seat-grid .locked { background: #d15a5a; }

.auth-form {
  padding: 44px 40px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(250, 252, 249, 0.98)),
    #fff;
}

.form-title {
  margin-bottom: 26px;
}

.form-title span {
  color: var(--primary);
  font-size: 13px;
  font-weight: 800;
}

.form-title h2 {
  margin: 8px 0 8px;
  font-size: 32px;
  line-height: 1.15;
}

.form-title p {
  margin: 0;
  color: var(--muted);
}

.remember-row {
  margin-bottom: 18px;
}

.auth-submit {
  width: 100%;
  min-height: 44px;
  box-shadow: 0 14px 30px rgba(31, 111, 91, 0.22);
}

.auth-link {
  margin-top: 22px;
  text-align: center;
  color: var(--muted);
}

.auth-link a {
  color: var(--primary);
  font-weight: 700;
}

@media (max-width: 760px) {
  .auth-page {
    padding: 76px 16px 24px;
  }

  .auth-home {
    left: 16px;
  }

  .auth-shell {
    grid-template-columns: 1fr;
  }

  .auth-visual {
    min-height: auto;
    padding: 34px 28px;
  }

  .auth-form {
    padding: 32px 24px;
  }
}
</style>
