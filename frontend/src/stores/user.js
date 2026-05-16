import { defineStore } from 'pinia'
import request from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('user') || 'null')
  }),

  getters: {
    isLoggedIn: state => !!state.token,
    isAdmin: state => state.userInfo?.role === 'admin'
  },

  actions: {
    async login(username, password, remember) {
      const res = await request.post('/user/login', { username, password }, {
        params: { remember }
      })
      this.token = res.data.token
      this.userInfo = res.data.userInfo
      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.userInfo))
      return res.data
    },

    async getUserInfo() {
      const res = await request.get('/user/info')
      this.userInfo = res.data
      localStorage.setItem('user', JSON.stringify(this.userInfo))
      return res.data
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
