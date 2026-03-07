<template>
  <div class="pricing-page">
    <div class="pricing-hero">
      <h1>选择适合您的套餐</h1>
      <p class="hero-sub">写一次，发布到所有平台。节省 80% 的发布时间。</p>
    </div>

    <!-- 当前会员状态 -->
    <div class="current-plan-banner" v-if="currentStatus">
      <el-alert
        :title="`当前套餐：${currentStatus.plan === 'FREE' ? '免费版' : currentStatus.plan === 'PRO' ? '专业版' : '团队版'} ${currentStatus.expiredAt ? `（到期：${formatDate(currentStatus.expiredAt)}）` : ''}`"
        :type="currentStatus.plan === 'FREE' ? 'info' : 'success'"
        show-icon
        :closable="false"
      />
    </div>

    <!-- 套餐卡片 -->
    <div class="plans-grid">
      <div
        v-for="plan in plans"
        :key="plan.id"
        class="plan-card"
        :class="{ 'plan-card--popular': plan.id === 'PRO', 'plan-card--current': currentStatus?.plan === plan.id }"
      >
        <div class="plan-badge" v-if="plan.id === 'PRO'">🔥 最受欢迎</div>
        <div class="plan-badge plan-badge--current" v-if="currentStatus?.plan === plan.id">当前套餐</div>
        
        <div class="plan-header">
          <h2 class="plan-name">{{ plan.name }}</h2>
          <div class="plan-price">
            <span class="price-amount">¥{{ billingAnnual ? plan.yearPrice : plan.price }}</span>
            <span class="price-period">/ {{ billingAnnual ? '年' : '月' }}</span>
          </div>
          <div class="price-savings" v-if="billingAnnual && plan.price > 0">
            相比月付节省 ¥{{ plan.price * 12 - plan.yearPrice }}
          </div>
        </div>

        <ul class="plan-features">
          <li v-for="feature in plan.features" :key="feature">
            <el-icon color="#67c23a"><Select /></el-icon>
            {{ feature }}
          </li>
        </ul>

        <el-button
          :type="plan.id === 'PRO' ? 'primary' : 'default'"
          size="large"
          class="plan-cta"
          @click="handleSubscribe(plan)"
          :disabled="currentStatus?.plan === plan.id"
        >
          {{ currentStatus?.plan === plan.id ? '当前套餐' : plan.price === 0 ? '免费使用' : '立即订阅' }}
        </el-button>
      </div>
    </div>

    <!-- 计费周期切换 -->
    <div class="billing-toggle">
      <span :class="{ 'active': !billingAnnual }">月付</span>
      <el-switch v-model="billingAnnual" />
      <span :class="{ 'active': billingAnnual }">年付 <el-tag size="small" type="success">省最多</el-tag></span>
    </div>

    <!-- 邀请码区域 -->
    <div class="invite-section">
      <el-card class="invite-card">
        <h3>🎁 邀请好友，共同获益</h3>
        <p>每邀请一位好友注册，双方各获得 <strong>14天专业版</strong></p>
        
        <div class="invite-code-block" v-if="currentStatus?.inviteCode">
          <span class="invite-code">{{ currentStatus.inviteCode }}</span>
          <el-button size="small" @click="copyInviteCode">复制邀请码</el-button>
        </div>
        <el-button v-else @click="generateInviteCode" :loading="generatingCode">获取我的邀请码</el-button>

        <el-divider />
        <p>使用好友邀请码激活 14 天专业版：</p>
        <div class="redeem-block">
          <el-input v-model="redeemCode" placeholder="输入邀请码" style="width: 200px" />
          <el-button type="primary" @click="redeemInvite" :loading="redeeming">激活</el-button>
        </div>
      </el-card>
    </div>

    <!-- 功能对比表 -->
    <div class="comparison-section">
      <h2>详细功能对比</h2>
      <el-table :data="comparisonRows" border style="width: 100%; max-width: 800px; margin: 0 auto">
        <el-table-column prop="feature" label="功能" width="200" />
        <el-table-column label="免费版" align="center" width="140">
          <template #default="{ row }">
            <span v-html="formatCell(row.free)" />
          </template>
        </el-table-column>
        <el-table-column label="专业版" align="center" width="140">
          <template #default="{ row }">
            <span v-html="formatCell(row.pro)" />
          </template>
        </el-table-column>
        <el-table-column label="团队版" align="center" width="140">
          <template #default="{ row }">
            <span v-html="formatCell(row.team)" />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Select } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const plans = ref<any[]>([])
const currentStatus = ref<any>(null)
const billingAnnual = ref(true)
const generatingCode = ref(false)
const redeeming = ref(false)
const redeemCode = ref('')

const comparisonRows = [
  { feature: '每月文章数', free: '5篇', pro: '无限', team: '无限' },
  { feature: '支持平台数', free: '2个', pro: '全部', team: '全部' },
  { feature: 'AI 发布优化', free: false, pro: true, team: true },
  { feature: '定时发布', free: false, pro: true, team: true },
  { feature: '数据分析仪表板', free: false, pro: true, team: true },
  { feature: '团队成员', free: '1人', pro: '1人', team: '5人' },
  { feature: '内容审核流', free: false, pro: false, team: true },
  { feature: '客服支持', free: '社区', pro: '优先邮件', team: '专属' },
]

function formatCell(val: any) {
  if (val === true) return '✅'
  if (val === false) return '❌'
  return val
}

function formatDate(dateStr: string) {
  return dateStr ? dateStr.substring(0, 10) : ''
}

async function loadPlans() {
  try {
    const res = await api.get('/subscription/plans')
    plans.value = res.data
  } catch (e) { console.error(e) }
}

async function loadStatus() {
  try {
    const res = await api.get('/subscription/status')
    currentStatus.value = res.data
  } catch (e) { /* 未登录时不显示 */ }
}

async function generateInviteCode() {
  generatingCode.value = true
  try {
    const res = await api.post('/subscription/generate-invite-code')
    currentStatus.value = { ...currentStatus.value, inviteCode: res.data.inviteCode }
    ElMessage.success('邀请码已生成！')
  } finally { generatingCode.value = false }
}

function copyInviteCode() {
  navigator.clipboard.writeText(currentStatus.value.inviteCode)
  ElMessage.success('已复制邀请码')
}

async function redeemInvite() {
  if (!redeemCode.value) return ElMessage.warning('请输入邀请码')
  redeeming.value = true
  try {
    const res = await api.post('/subscription/redeem-invite', { inviteCode: redeemCode.value })
    ElMessage.success(res.data.message)
    loadStatus()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.error || '激活失败')
  } finally { redeeming.value = false }
}

function handleSubscribe(plan: any) {
  if (plan.price === 0) return
  // 预留支付接口，暂时弹出提示
  ElMessage.info('支付功能即将上线，请联系客服开通: support@yigaotong.com')
}

onMounted(() => {
  loadPlans()
  loadStatus()
})
</script>

<style scoped>
.pricing-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 40px 20px;
}

.pricing-hero {
  text-align: center;
  margin-bottom: 40px;
}

.pricing-hero h1 {
  font-size: 2.2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 12px;
}

.hero-sub {
  color: #666;
  font-size: 1.1rem;
}

.current-plan-banner {
  margin-bottom: 24px;
}

.billing-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 32px;
  font-size: 1rem;
}

.billing-toggle .active { font-weight: 600; color: #409eff; }

.plans-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

@media (max-width: 768px) {
  .plans-grid { grid-template-columns: 1fr; }
}

.plan-card {
  border: 2px solid #e4e7ed;
  border-radius: 16px;
  padding: 32px 24px;
  position: relative;
  transition: all 0.3s;
  background: white;
}

.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.1);
}

.plan-card--popular {
  border-color: #409eff;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.2);
}

.plan-card--current {
  border-color: #67c23a;
  background: #f0f9eb;
}

.plan-badge {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #409eff, #764ba2);
  color: white;
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  white-space: nowrap;
}

.plan-badge--current {
  background: linear-gradient(135deg, #67c23a, #95d475);
}

.plan-header { text-align: center; margin-bottom: 24px; }

.plan-name { font-size: 1.4rem; font-weight: 700; margin-bottom: 8px; }

.plan-price { display: flex; align-items: baseline; justify-content: center; gap: 4px; }

.price-amount { font-size: 2.5rem; font-weight: 800; color: #303133; }

.price-period { color: #909399; font-size: 0.9rem; }

.price-savings { color: #67c23a; font-size: 0.85rem; margin-top: 4px; }

.plan-features {
  list-style: none;
  padding: 0;
  margin: 0 0 24px;
}

.plan-features li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  font-size: 0.95rem;
  border-bottom: 1px solid #f5f7fa;
}

.plan-cta { width: 100%; }

.invite-section { margin: 48px 0; }

.invite-card {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
  border-radius: 16px;
}

.invite-card h3 { font-size: 1.3rem; margin-bottom: 8px; }

.invite-code-block {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 16px 0;
}

.invite-code {
  font-size: 1.8rem;
  font-weight: 800;
  letter-spacing: 4px;
  color: #409eff;
  font-family: monospace;
  background: #ecf5ff;
  padding: 8px 20px;
  border-radius: 8px;
}

.redeem-block {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 12px;
}

.comparison-section { margin-top: 48px; text-align: center; }
.comparison-section h2 { font-size: 1.6rem; margin-bottom: 24px; }
</style>
