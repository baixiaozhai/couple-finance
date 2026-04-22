<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <h1 class="logo">
          <el-icon><WalletFilled /></el-icon>
          情侣记账
        </h1>
        <div class="user-info">
          <span class="nickname">{{ userStore.nickname }}</span>
          <el-button type="danger" size="small" plain @click="handleLogout">退出</el-button>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部导航栏（移动端） -->
    <nav class="bottom-nav">
      <router-link
        v-for="route in menuRoutes"
        :key="route.path"
        :to="route.path"
        class="nav-item"
        :class="{ active: $route.path === route.path }"
      >
        <el-icon :size="20">
          <component :is="route.meta.icon" />
        </el-icon>
        <span class="nav-label">{{ route.meta.title }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 获取需要显示的菜单路由
const menuRoutes = computed(() => {
  const layoutRoute = router.getRoutes().find(r => r.name === 'Layout')
  return layoutRoute?.children?.filter(r => r.meta?.title) || []
})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  })
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  padding-bottom: 60px;
}

.header {
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  padding: 0 16px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 600px;
  margin: 0 auto;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nickname {
  font-size: 14px;
}

.main-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 16px;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-around;
  padding: 8px 0;
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #999;
  text-decoration: none;
  font-size: 12px;
  padding: 4px 12px;
  transition: color 0.3s;
}

.nav-item.active {
  color: #4CAF50;
}

.nav-label {
  font-size: 11px;
}
</style>
