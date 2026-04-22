<template>
  <div class="transaction-page">
    <!-- 添加记账表单 -->
    <div class="add-form-card">
      <h3 class="card-title">记一笔</h3>
      <el-form :model="form" label-position="top">
        <el-form-item label="类型">
          <el-radio-group v-model="form.type" size="large">
            <el-radio-button label="expense">支出</el-radio-button>
            <el-radio-button label="income">收入</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="金额">
          <el-input-number
            v-model="form.amount"
            :precision="2"
            :min="0.01"
            :max="999999"
            :controls="false"
            size="large"
            class="amount-input"
          />
        </el-form-item>

        <el-form-item label="分类">
          <div class="category-grid">
            <div
              v-for="cat in currentCategories"
              :key="cat"
              class="category-item"
              :class="{ active: form.category === cat }"
              @click="form.category = cat"
            >
              {{ cat }}
            </div>
          </div>
        </el-form-item>

        <el-form-item label="付款人">
          <el-select v-model="form.payerId" placeholder="选择付款人" size="large">
            <el-option
              v-for="member in familyMembers"
              :key="member.userId"
              :label="member.nickname"
              :value="member.userId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="日期">
          <el-date-picker
            v-model="form.recordDate"
            type="date"
            placeholder="选择日期"
            size="large"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="form.note"
            placeholder="可选：添加备注"
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="submit-btn"
            :loading="submitting"
            @click="handleSubmit"
          >
            保存
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 记账列表 -->
    <div class="list-card">
      <h3 class="card-title">记账记录</h3>
      <div v-if="transactions.length === 0" class="empty-tip">
        暂无记账记录
      </div>
      <div v-else class="transaction-list">
        <div
          v-for="item in transactions"
          :key="item.id"
          class="transaction-item"
        >
          <div class="item-left">
            <div class="category-tag" :class="item.type">
              {{ item.category }}
            </div>
            <div class="item-info">
              <div class="item-note">{{ item.note || item.category }}</div>
              <div class="item-meta">
                {{ formatDate(item.recordDate) }} · {{ getPayerName(item.payerId) }}
              </div>
            </div>
          </div>
          <div class="item-right">
            <div class="item-amount" :class="item.type">
              {{ item.type === 'income' ? '+' : '-' }}¥{{ formatAmount(item.amount) }}
            </div>
            <el-button
              type="danger"
              link
              size="small"
              @click="handleDelete(item.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'
import { addTransaction, getTransactions, deleteTransaction } from '../api/transaction'
import { getFamilyInfo } from '../api/family'
import dayjs from 'dayjs'

const userStore = useUserStore()

// 表单数据
const form = ref({
  type: 'expense',
  amount: undefined,
  category: '',
  payerId: '',
  recordDate: dayjs().format('YYYY-MM-DD'),
  note: ''
})

// 支出分类
const expenseCategories = ['餐饮', '交通', '购物', '娱乐', '住房', '医疗', '教育', '通讯', '人情', '其他']
// 收入分类
const incomeCategories = ['工资', '奖金', '投资收益', '兼职', '红包', '其他']

// 根据类型显示对应的分类
const currentCategories = computed(() => {
  return form.value.type === 'expense' ? expenseCategories : incomeCategories
})

// 数据
const transactions = ref([])
const familyMembers = ref([])
const submitting = ref(false)

// 格式化金额
const formatAmount = (amount) => {
  return Number(amount).toFixed(2)
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('MM-DD')
}

// 获取付款人名称
const getPayerName = (payerId) => {
  const member = familyMembers.value.find(m => m.userId === payerId)
  return member?.nickname || '未知'
}

// 加载数据
const loadData = async () => {
  try {
    // 加载记账列表
    const transRes = await getTransactions()
    if (transRes.code === 200) {
      transactions.value = transRes.data
    }
    
    // 加载家庭成员
    const familyRes = await getFamilyInfo()
    if (familyRes.code === 200) {
      familyMembers.value = familyRes.data.members
      // 默认选择自己作为付款人
      if (!form.value.payerId && userStore.userId) {
        form.value.payerId = Number(userStore.userId)
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  // 表单验证
  if (!form.value.amount || form.value.amount <= 0) {
    ElMessage.warning('请输入金额')
    return
  }
  if (!form.value.category) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!form.value.payerId) {
    ElMessage.warning('请选择付款人')
    return
  }

  submitting.value = true
  try {
    const res = await addTransaction(form.value)
    if (res.code === 200) {
      ElMessage.success('记账成功')
      // 重置表单
      form.value = {
        type: 'expense',
        amount: undefined,
        category: '',
        payerId: Number(userStore.userId),
        recordDate: dayjs().format('YYYY-MM-DD'),
        note: ''
      }
      // 刷新列表
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

// 删除记录
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteTransaction(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    // 用户取消删除
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.transaction-page {
  padding-bottom: 20px;
}

.add-form-card,
.list-card {
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

.amount-input {
  width: 100%;
}

.amount-input :deep(.el-input__inner) {
  font-size: 20px;
  text-align: center;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.category-item {
  padding: 10px 4px;
  text-align: center;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item.active {
  background: #4CAF50;
  color: white;
}

.submit-btn {
  width: 100%;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.transaction-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.category-tag {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  background: #f0f0f0;
  color: #666;
  white-space: nowrap;
}

.category-tag.expense {
  background: #fef0f0;
  color: #f56c6c;
}

.category-tag.income {
  background: #f0f9eb;
  color: #67c23a;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.item-note {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  font-size: 12px;
  color: #999;
}

.item-right {
  text-align: right;
}

.item-amount {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.item-amount.expense {
  color: #f56c6c;
}

.item-amount.income {
  color: #67c23a;
}
</style>
