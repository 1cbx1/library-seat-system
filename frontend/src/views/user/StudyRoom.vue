<template>
  <div class="page-shell">
    <UserNav />

    <main class="page-container rooms-page">
      <div class="list-hero soft-card">
        <div>
          <span>Study rooms</span>
          <h1>自习室列表</h1>
          <p>查看位置、楼层、容量和开放时间，选择后进入座位网格。</p>
        </div>
        <el-input v-model="keyword" placeholder="搜索自习室名称或位置" clearable class="search-box" />
      </div>

      <div class="room-list">
        <article v-for="room in filteredRooms" :key="room.id" class="room-row soft-card" @click="goToSeatMap(room.id)">
          <img :src="room.imageUrl || fallbackImage(room.id)" :alt="room.roomName" />
          <div class="room-info">
            <div class="room-info__head">
              <div>
                <h2>{{ room.roomName }}</h2>
                <p>{{ room.description || '安静明亮的学习空间，适合个人自习和课程复习。' }}</p>
              </div>
              <el-tag :type="room.status === 0 ? 'info' : 'success'">{{ room.status === 0 ? '停用' : '开放' }}</el-tag>
            </div>
            <div class="room-facts">
              <span>位置：{{ room.location || '-' }}</span>
              <span>楼层：{{ room.floor || '-' }}</span>
              <span>容量：{{ room.capacity || 0 }} 人</span>
              <span>开放：{{ room.openTime || '08:00-22:00' }}</span>
              <span>收费：{{ room.hourPrice ? `${room.hourPrice} 元/小时` : '免费' }}</span>
            </div>
          </div>
          <el-button type="primary">选座</el-button>
        </article>
      </div>

      <el-empty v-if="!filteredRooms.length" description="暂无匹配自习室" />
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import UserNav from '../../components/UserNav.vue'
import request from '../../api'

const router = useRouter()
const userStore = useUserStore()
const rooms = ref([])
const keyword = ref('')

const filteredRooms = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  if (!key) return rooms.value
  return rooms.value.filter(room => `${room.roomName || ''}${room.location || ''}`.toLowerCase().includes(key))
})

const fallbackImage = (id) => `https://picsum.photos/seed/study-room-${id}/360/240`

const goToSeatMap = (roomId) => {
  if (!userStore.isLoggedIn) {
    router.push({ path: '/login', query: { redirect: `/studyrooms/${roomId}/seats` } })
    return
  }
  router.push(`/studyrooms/${roomId}/seats`)
}

onMounted(async () => {
  const res = await request.get('/room/list')
  rooms.value = res.data || []
})
</script>

<style scoped>
.rooms-page {
  padding: 34px 0 54px;
}

.list-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 22px;
  padding: 26px;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.92), rgba(232, 240, 235, 0.92)),
    url("https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=1400&q=80") right center/42% 100% no-repeat;
}

.list-hero span {
  display: inline-flex;
  margin-bottom: 10px;
  color: var(--accent);
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.list-hero h1 {
  margin: 0;
  font-size: 42px;
  line-height: 1.08;
}

.list-hero p {
  margin: 10px 0 0;
  color: var(--muted);
}

.search-box {
  width: 300px;
}

.room-list {
  display: grid;
  gap: 18px;
}

.room-row {
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr) auto;
  gap: 22px;
  align-items: center;
  padding: 14px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.room-row:hover {
  transform: translateY(-3px);
  box-shadow: 0 20px 50px rgba(25, 42, 86, 0.14);
}

.room-row img {
  width: 240px;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
}

.room-info__head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.room-info h2 {
  margin: 0;
  font-size: 23px;
}

.room-info p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.6;
}

.room-facts {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.room-facts span {
  padding: 8px 10px;
  border-radius: 6px;
  color: #4f6259;
  background: #f1f6f2;
  font-size: 13px;
}

@media (max-width: 820px) {
  .list-hero,
  .room-row {
    grid-template-columns: 1fr;
    display: grid;
  }

  .search-box,
  .room-row img {
    width: 100%;
  }
}

@media (max-width: 560px) {
  .rooms-page {
    padding: 22px 0 38px;
  }

  .list-hero {
    align-items: stretch;
    gap: 18px;
    padding: 20px;
    background:
      linear-gradient(120deg, rgba(255, 255, 255, 0.94), rgba(232, 240, 235, 0.94)),
      url("https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=760&q=80") right bottom/62% auto no-repeat;
  }

  .list-hero h1 {
    font-size: 34px;
  }

  .room-row {
    gap: 14px;
    padding: 12px;
  }

  .room-row img {
    height: 190px;
  }

  .room-info__head {
    align-items: flex-start;
    flex-direction: column;
  }

  .room-info h2 {
    font-size: 21px;
  }

  .room-row .el-button {
    width: 100%;
  }
}
</style>
