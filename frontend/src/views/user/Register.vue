<template>
  <div class="auth-page">
    <router-link class="auth-home" to="/">
      <el-icon><ArrowLeft /></el-icon>
      返回首页
    </router-link>

    <section class="auth-shell register-shell soft-card">
      <aside class="auth-visual">
        <div class="visual-badge">
          <el-icon><Collection /></el-icon>
          New user access
        </div>
        <h1>创建校园预约账号</h1>
        <p>注册后可以预约自习室座位、参与交流论坛，并查看管理员留言回复。</p>

        <div class="benefit-list">
          <span>查看自习室开放时间</span>
          <span>提交座位预约申请</span>
          <span>签到签退形成记录</span>
          <span>维护个人信用分</span>
        </div>
      </aside>

      <div class="auth-form">
        <div class="form-title">
          <span>用户注册</span>
          <h2>填写账号信息</h2>
          <p>请使用真实信息创建普通用户账号。</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleRegister">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" size="large" :prefix-icon="User" placeholder="3-20 位用户名" />
          </el-form-item>
          <div class="form-grid">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" size="large" :prefix-icon="Lock" type="password" placeholder="6-20 位密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" size="large" :prefix-icon="Lock" type="password" placeholder="再次输入密码" show-password />
            </el-form-item>
          </div>
          <div class="form-grid">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" size="large" :prefix-icon="Avatar" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" size="large" :prefix-icon="Iphone" placeholder="请输入手机号" />
            </el-form-item>
          </div>
          <el-button class="auth-submit" type="primary" size="large" :loading="loading" @click="handleRegister">创建账号</el-button>
        </el-form>
        <div class="auth-link">已有账号？<router-link to="/login">立即登录</router-link></div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Avatar, Collection, Iphone, Lock, User } from '@element-plus/icons-vue'
import request from '../../api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ username: '', password: '', confirmPassword: '', realName: '', phone: '' })

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '用户名长度为 3-20 位', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '密码长度为 6-20 位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirmPassword, trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await request.post('/user/register', {
      username: form.value.username,
      password: form.value.password,
      realName: form.value.realName,
      phone: form.value.phone
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
  width: min(1040px, 100%);
  display: grid;
  grid-template-columns: minmax(0, 0.86fr) minmax(520px, 1fr);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.auth-visual {
  min-height: 620px;
  padding: 46px;
  color: #fff;
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(145deg, rgba(18, 33, 28, 0.94), rgba(31, 111, 91, 0.72)),
    url("../../assets/hero.png") right 28px bottom 34px / 230px auto no-repeat,
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
  margin: 28px 0 16px;
  font-size: clamp(36px, 4.4vw, 48px);
  line-height: 1.06;
  text-wrap: balance;
}

.auth-visual p {
  max-width: 380px;
  margin: 0;
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.8;
}

.benefit-list {
  display: grid;
  gap: 12px;
  margin-top: 34px;
}

.benefit-list span {
  width: fit-content;
  padding: 12px 14px;
  border: 1px solid rgba(255, 255, 255, 0.13);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.08);
  font-weight: 700;
}

.auth-form {
  padding: 42px 40px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(250, 252, 249, 0.98)),
    #fff;
}

.form-title {
  margin-bottom: 24px;
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

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.auth-submit {
  width: 100%;
  min-height: 44px;
  margin-top: 2px;
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

@media (max-width: 880px) {
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

@media (max-width: 620px) {
  .form-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>
