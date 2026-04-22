<template>
  <div class="settings-page">
    <!-- 家庭信息 -->
    <div class="settings-card">
      <h3 class="card-title">我的家庭</h3>
      
      <div v-if="!userStore.hasFamily" class="no-family">
        <p>您还没有加入家庭</p>
        <div class="action-buttons">
          <el-button type="primary" @click="goToDashboard">创建或加入家庭</el-button>
        </div>
      </div>
      
      <div v-else-if="familyInfo" class="family-info">
        <div class="info-item">
          <span class="label">家庭名称</span>
          <span class="value">{{ familyInfo.familyName }}</span>
        </div>
        <div class="info-item">
          <span class="label">邀请码</span>
          <div class="invite-code">
            <span class="code">{{ familyInfo.inviteCode }}</span>
            <el-button link type="primary" @click="copyInviteCode">
              <el-icon><CopyDocument /></el-icon>
            </el-button>
          </div>
        </div>
        
        <div class="members-section">
          <div class="section-label">家庭成员</div>
          <div class="members-list">
            <div
              v-for="member in familyInfo.members"
              :key="member.userId"
              class="member-item"
            >
              <el-avatar :size="40" :icon="UserFilled" />
              <div class="member-info">
                <div class="member-name">
                  {{ member.nickname }}
                  <el-tag v-if="member.isCreator" size="small" type="success">创建者</el-tag>
                </div>
                <div class="member-username">{{ member.username }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="danger-zone">
          <el-button type="danger" plain @click="handleLeaveFamily">
            {{ isCreator ? '解散家庭' : '退出家庭' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 关于 -->
    <div class="settings-card">
      <h3 class="card-title">关于</h3>
      <div class="about-content">
        <p>情侣记账 v1.0.0</p>
        <p class="desc">简单实用的情侣/家庭财务管理工具</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CopyDocument, UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { getFamilyInfo, leaveFamily } from '../api/family'

const router = useRouter()
const userStore = useUserStore()

const familyInfo = ref(null)

// 判断当前用户是否是创建者
const isCreator = computed(() => {
  if (!familyInfo.value || !userStore.userId) return false
  return String(familyInfo.value.createdBy) === String(userStore.userId)
})

// 加载家庭信息
const loadFamilyInfo = async () => {
  if (!userStore.hasFamily) return
  
  try {
    const res = await getFamilyInfo()
    if (res.code === 200) {
      familyInfo.value = res.data
    }
  } catch (error) {
    console.error('加载家庭信息失败:', error)
  }
}

// 复制邀请码
const copyInviteCode = () => {
  if (familyInfo.value?.inviteCode) {
    navigator.clipboard.writeText(familyInfo.value.inviteCode)
    ElMessage.success('邀请码已复制')
  }
}

// 跳转到首页
const goToDashboard = () => {
  router.push('/')
}

// 退出/解散家庭
const handleLeaveFamily = async () => {
  const action = isCreator.value ? '解散' : '退出'
  const warning = isCreator.value 
    ? '解散家庭将删除所有相关数据，此操作不可恢复！' 
    : '退出后需要重新加入才能查看家庭数据。'
  
  try {
    await ElMessageBox.confirm(warning, `确定要${action}家庭吗？`, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await leaveFamily()
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      userStore.setFamilyId(null)
      familyInfo.value = null
      router.push('/')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    // 用户取消
  }
}

onMounted(() => {
  loadFamilyInfo()
})
</script>

<style scoped>
.settings-page {
  padding-bottom: 20px;
}

.settings-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.no-family {
  text-align: center;
  padding: 20px 0;
  color: #666;
}

.action-buttons {
  margin-top: 16px;
}

.family-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-of-type {
  border-bottom: none;
}

.label {
  color: #666;
  font-size: 14px;
}

.value {
  color: #333;
  font-weight: 500;
}

.invite-code {
  display: flex;
  align-items: center;
  gap: 8px;
}

.code {
  font-size: 18px;
  font-weight: 600;
  color: #4CAF50;
  letter-spacing: 2px;
}

.members-section {
  margin-top: 8px;
}

.section-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.members-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.member-info {
  flex: 1;
}

.member-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.member-username {
  font-size: 13px;
  color: #999;
}

.danger-zone {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.about-content {
  text-align: center;
  padding: 20px 0;
  color: #333;
}

.about-content .desc {
  color: #999;
  font-size: 14px;
  margin-top: 8px;
}
</style>
