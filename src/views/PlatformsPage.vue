<template>
  <div class="platforms-page p-6">
    <h1 class="text-3xl font-bold text-white mb-6">平台配置</h1>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div
        v-for="platform in platforms"
        :key="platform.name"
        class="bg-gray-900 border border-gray-800 rounded-lg p-6 hover:border-blue-500 transition-colors"
      >
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-xl font-semibold text-white">{{ platform.label }}</h3>
          <el-tag
            :type="platform.connected ? 'success' : 'danger'"
          >
            {{ platform.connected ? '已连接' : '未连接' }}
          </el-tag>
        </div>

        <p class="text-gray-400 text-sm mb-4">{{ platform.description }}</p>

        <div v-if="platform.connected" class="space-y-3">
          <div class="text-sm text-gray-500">
            <p>账号: {{ platform.account }}</p>
            <p>连接时间: {{ formatDate(platform.connectedAt) }}</p>
          </div>
          <div class="flex gap-2">
            <el-button @click="editPlatform(platform)" text type="primary" size="small">
              <el-icon><edit /></el-icon>
              修改
            </el-button>
            <el-button @click="disconnectPlatform(platform)" text type="danger" size="small">
              <el-icon><delete /></el-icon>
              断开连接
            </el-button>
          </div>
        </div>

        <el-button
          v-else
          @click="connectPlatform(platform)"
          type="primary"
          class="w-full"
        >
          <el-icon><link /></el-icon>
          立即连接
        </el-button>
      </div>
    </div>

    <!-- 配置对话框 -->
    <el-dialog
      v-model="configDialogVisible"
      :title="`配置${editingPlatform?.label || ''}`"
      width="500px"
    >
      <el-form v-if="editingPlatform" :model="platformConfig" label-width="120px">
        <el-form-item label="账号/ID" required>
          <el-input v-model="platformConfig.account" />
        </el-form-item>

        <el-form-item label="密钥/Token" required>
          <el-input v-model="platformConfig.token" type="password" show-password />
        </el-form-item>

        <el-form-item v-if="editingPlatform.name === 'wechat'" label="AppID" required>
          <el-input v-model="platformConfig.appId" />
        </el-form-item>

        <el-form-item v-if="editingPlatform.name === 'wechat'" label="AppSecret" required>
          <el-input v-model="platformConfig.appSecret" type="password" show-password />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="platformConfig.remark" type="textarea" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePlatformConfig" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Edit, Delete, Link } from '@element-plus/icons-vue';

interface Platform {
  name: string;
  label: string;
  description: string;
  connected: boolean;
  account?: string;
  connectedAt?: Date;
}

const platforms = ref<Platform[]>([
  {
    name: 'wechat',
    label: '微信公众号',
    description: '连接您的微信公众号，一键发布文章',
    connected: false
  },
  {
    name: 'juejin',
    label: '稀土掘金',
    description: '稀土掘金技术社区',
    connected: false
  },
  {
    name: 'cnblogs',
    label: '博客园',
    description: '专业的C#/.NET博客平台',
    connected: false
  },
  {
    name: 'zhihu',
    label: '知乎',
    description: '中文互联网最大的内容平台',
    connected: false
  },
  {
    name: 'csdn',
    label: 'CSDN',
    description: '中文IT社区及服务平台',
    connected: false
  },
  {
    name: 'toutiao',
    label: '今日头条',
    description: '智能推荐内容平台',
    connected: false
  },
  {
    name: 'jianshu',
    label: '简书',
    description: '创意写作社区',
    connected: false
  }
]);

const configDialogVisible = ref(false);
const editingPlatform = ref<Platform | null>(null);
const saving = ref(false);

const platformConfig = ref({
  account: '',
  token: '',
  appId: '',
  appSecret: '',
  remark: ''
});

const formatDate = (date: any) => {
  if (!date) return '-';
  return new Date(date).toLocaleString('zh-CN');
};

const editPlatform = (platform: Platform) => {
  editingPlatform.value = platform;
  configDialogVisible.value = true;
};

const connectPlatform = (platform: Platform) => {
  editingPlatform.value = platform;
  platformConfig.value = {
    account: '',
    token: '',
    appId: '',
    appSecret: '',
    remark: ''
  };
  configDialogVisible.value = true;
};

const disconnectPlatform = async (platform: Platform) => {
  platform.connected = false;
  ElMessage.success('已断开连接');
};

const savePlatformConfig = async () => {
  if (!platformConfig.value.account || !platformConfig.value.token) {
    ElMessage.warning('请填写必要信息');
    return;
  }

  saving.value = true;
  try {
    // 模拟API调用
    if (editingPlatform.value) {
      editingPlatform.value.connected = true;
      editingPlatform.value.account = platformConfig.value.account;
      editingPlatform.value.connectedAt = new Date();
    }
    ElMessage.success('配置保存成功');
    configDialogVisible.value = false;
  } catch (error) {
    ElMessage.error('保存失败');
  } finally {
    saving.value = false;
  }
};
</script>
