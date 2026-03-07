export default {
  common: {
    loading: '加载中...',
    confirm: '确定',
    cancel: '取消',
    save: '保存',
    delete: '删除',
    edit: '编辑',
    success: '成功',
    error: '错误',
    noData: '暂无数据',
    brand: '一稿通'
  },
  nav: {
    dashboard: '仪表盘',
    articles: '文章管理',
    publish: '一键发布',
    editor: '新建文章',
    platforms: '平台配置',
    settings: '系统设置',
    logout: '退出登录',
    subscription: '会员中心'
  },
  platform: {
    title: '平台配置',
    bindBtn: '绑定账号',
    unbindBtn: '解绑',
    status: {
      bound: '已绑定',
      unbound: '未绑定',
      expired: '已过期'
    }
  },
  article: {
    title: '文章管理',
    newBtn: '新建文章',
    searchPlaceholder: '搜索文章标题...',
    table: {
      title: '标题',
      status: '状态',
      theme: '主题',
      updatedAt: '更新时间',
      actions: '操作'
    },
    status: {
      draft: '草稿',
      published: '已发布',
      scheduled: '定时发布'
    },
    action: {
      edit: '编辑内容',
      publish: '发布到平台',
      delete: '删除文章'
    }
  },
  editor: {
    title: '编辑文章',
    titlePlaceholder: '输入文章标题',
    themeSelect: '选择排版样式',
    copyToWechat: '复制到公众号',
    saveDraft: '保存草稿'
  },
  auth: {
    loginTitle: '一稿通',
    loginSubtitle: '登录你的账户',
    registerTitle: '创建新账户',
    registerSubtitle: '加入一稿通',
    username: '用户名',
    usernamePlaceholder: '请输入用户名',
    email: '邮箱',
    emailPlaceholder: '请输入邮箱',
    password: '密码',
    passwordPlaceholder: '请输入密码',
    confirmPassword: '确认密码',
    confirmPasswordPlaceholder: '请再次输入密码',
    loginBtn: '登录',
    registerBtn: '注册',
    noAccount: '没有账户？',
    registerLink: '注册新账户',
    hasAccount: '已有账户？',
    loginLink: '登录',
    loginSuccess: '登录成功',
    loginFailed: '登录失败',
    registerSuccess: '注册成功，请登录',
    registerFailed: '注册失败',
    fillAll: '请填写所有字段',
    passwordMismatch: '两次输入的密码不一致'
  },
  dashboard: {
    title: '仪表板',
    totalArticles: '文章总数',
    drafts: '草稿数',
    published: '已发布',
    connectedPlatforms: '连接平台',
    quickActions: '快速操作',
    newArticle: '新建文章',
    manageArticles: '管理文章',
    configPlatforms: '配置平台'
  },
  setting: {
    title: '账户设置',
    profile: '个人信息',
    username: '用户名',
    email: '邮箱',
    saveChanges: '保存更改',
    updatePwd: '修改密码',
    currentPwd: '当前密码',
    newPwd: '新密码',
    confirmNewPwd: '确认新密码',
    themeSetting: '主题设置',
    otherSetting: '其他设置',
    darkMode: '深色模式',
    profileEmpty: '用户名和邮箱不能为空',
    profileSuccess: '个人信息已更新',
    pwdEmpty: '请填写完整密码信息',
    pwdMismatch: '两次输入的新密码不一致',
    pwdSuccess: '密码修改成功，请重新登录'
  },
  changelog: {
    title: '更新日志',
    subtitle: '记录每一次进化，见证我们的成长历程',
    latest: '最新',
    v1_2_0: {
      title: 'v1.2.0 - 自动化引擎与发布深度优化',
      newFeatures: '🚀 新功能',
      feat1Title: '浏览器辅助登录助手',
      feat1Desc: '引入 Playwright 自动化技术，支持一键引导用户登录并截获凭证，彻底解决 Token 获取难题。',
      feat2Title: '全新平台配置中心',
      feat2Desc: '重构了平台管理 UI，集成知乎蓝、掘金蓝等高清品牌图标，交互更直观。',
      fixes: '🛠️ 关键修复',
      fix1Title: '发布凭证同步修复',
      fix1Desc: '重构后端凭证分发逻辑，解决 `credentials` 字段断供问题，知乎发布流程现已 100% 走通。',
      fix2Title: '接口闭环',
      fix2Desc: '补全了 `ArticleController` 中遗留的发布 TODO 接口，确保全量 API 均可正常调用。',
      fix3Title: '并发安全增强',
      fix3Desc: '发布结果容器升级为 `ConcurrentHashMap`，极大提升了多平台群发时的稳定性。'
    },
    v1_1_0: {
      title: 'v1.1.0 - 极致深色模式与 UI 融合',
      visual: '🎨 视觉升级',
      vis1Title: '沉浸式深色模式',
      vis1Desc: '深度优化全局配色方案，消除 stark white 突兀感，编辑器与全站 UI 完美融合。',
      vis2Title: '玻璃拟态效果',
      vis2Desc: '登录页与注册页引入高级 Glassmorphism 设计，打造未来感视觉。',
      system: '⚙️ 系统增强',
      sys1Title: '主题系统解耦',
      sys1Desc: '重构 `themeStore`，实现品牌颜色与深浅模式独立切换，互不冲突。',
      sys2Title: 'Markdown 编辑器优化',
      sys2Desc: '实现了编辑器暗黑主题的动态跟随，阅读体验更出色。'
    },
    v1_0_0: {
      title: 'v1.0.0 - 核心发布引擎诞生',
      core: '🏗️ 核心成就',
      ach1Title: '全栈架构搭建',
      ach1Desc: '完成 Spring Boot 3 + Vue 3 + MySQL 的核心框架集成。',
      ach2Title: '多平台分发引擎',
      ach2Desc: '初步实现微信、掘金、知乎等主流平台的适配器架构。',
      ach3Title: '文章管理系统',
      ach3Desc: '支持 Markdown 编辑、多图上传与本地草稿存储。'
    }
  }
}
