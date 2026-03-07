export default {
  common: {
    loading: 'Loading...',
    confirm: 'Confirm',
    cancel: 'Cancel',
    save: 'Save',
    delete: 'Delete',
    edit: 'Edit',
    success: 'Success',
    error: 'Error',
    noData: 'No Data',
    brand: 'DST Publish Hub'
  },
  nav: {
    dashboard: 'Dashboard',
    articles: 'Articles',
    publish: 'Quick Publish',
    editor: 'New Post',
    platforms: 'Platforms',
    settings: 'Settings',
    logout: 'Logout',
    subscription: 'Subscription',
    changelog: 'Changelog'
  },
  platform: {
    title: 'Platforms Configuration',
    bindBtn: 'Bind Account',
    unbindBtn: 'Unbind',
    status: {
      bound: 'Bound',
      unbound: 'Unbound',
      expired: 'Expired'
    }
  },
  article: {
    title: 'Articles Management',
    newBtn: 'New Article',
    searchPlaceholder: 'Search article titles...',
    table: {
      title: 'Title',
      status: 'Status',
      theme: 'Theme',
      updatedAt: 'Updated Time',
      actions: 'Actions'
    },
    status: {
      draft: 'Draft',
      published: 'Published',
      scheduled: 'Scheduled'
    },
    action: {
      edit: 'Edit',
      publish: 'Publish',
      delete: 'Delete'
    }
  },
  editor: {
    title: 'Edit Article',
    titlePlaceholder: 'Enter article title',
    themeSelect: 'Select Theme',
    copyToWechat: 'Copy to WeChat',
    saveDraft: 'Save Draft'
  },
  auth: {
    loginTitle: 'DST Publish Hub',
    loginSubtitle: 'Log into your account',
    registerTitle: 'Create An Account',
    registerSubtitle: 'Join DST Publish Hub',
    username: 'Username',
    usernamePlaceholder: 'Enter your username',
    email: 'Email',
    emailPlaceholder: 'Enter your email',
    password: 'Password',
    passwordPlaceholder: 'Enter your password',
    confirmPassword: 'Confirm Password',
    confirmPasswordPlaceholder: 'Re-enter your password',
    loginBtn: 'Login',
    registerBtn: 'Register',
    noAccount: 'No account yet?',
    registerLink: 'Create a new account',
    hasAccount: 'Already have an account?',
    loginLink: 'Log in here',
    loginSuccess: 'Login successful',
    loginFailed: 'Login failed',
    registerSuccess: 'Registered successfully, please login.',
    registerFailed: 'Registration failed',
    fillAll: 'Please fill in all fields',
    passwordMismatch: 'Passwords do not match'
  },
  dashboard: {
    title: 'Dashboard',
    totalArticles: 'Total Articles',
    drafts: 'Drafts',
    published: 'Published',
    connectedPlatforms: 'Connected Platforms',
    quickActions: 'Quick Actions',
    newArticle: 'New Article',
    manageArticles: 'Manage Articles',
    configPlatforms: 'Platforms config'
  },
  setting: {
    title: 'Account Settings',
    profile: 'Profile',
    username: 'Username',
    email: 'Email',
    saveChanges: 'Save Changes',
    updatePwd: 'Change Password',
    currentPwd: 'Current Password',
    newPwd: 'New Password',
    confirmNewPwd: 'Confirm New Password',
    themeSetting: 'Theme Settings',
    otherSetting: 'Other Settings',
    darkMode: 'Dark Mode',
    profileEmpty: 'Username and email cannot be empty',
    profileSuccess: 'Profile customized successfully',
    pwdEmpty: 'Please fill out all password fields',
    pwdMismatch: 'New passwords do not match',
    pwdSuccess: 'Password changed successfully, please log in again.'
  },
  changelog: {
    title: 'Changelog',
    subtitle: 'Tracking every step of our evolution and growth.',
    latest: 'Latest',
    v1_2_0: {
      title: 'v1.2.0 - Automation Engine & Deep Publishing Optimization',
      newFeatures: '🚀 New Features',
      feat1Title: 'Browser-assisted Login Assistant',
      feat1Desc: 'Introduced Playwright automation to guide users to log in with one click and intercept credentials, solving the Token acquisition problem entirely.',
      feat2Title: 'Brand New Platform Configuration Center',
      feat2Desc: 'Refactored platform management UI, integrating high-definition brand icons like Zhihu Blue to make interaction more intuitive.',
      fixes: '🛠️ Key Fixes',
      fix1Title: 'Publish Credentials Sync Fix',
      fix1Desc: 'Refactored backend credential distribution logic. The Zhihu publishing pipeline is now 100% operational.',
      fix2Title: 'API Completed',
      fix2Desc: 'Completed left-over publish APIs in `ArticleController`, ensuring all APIs are fully functional.',
      fix3Title: 'Concurrency Safety Enhanced',
      fix3Desc: 'Upgraded the publish result container to `ConcurrentHashMap`, significantly improving stability during multi-platform mass publishing.'
    },
    v1_1_0: {
      title: 'v1.1.0 - Ultimate Dark Mode & UI Integration',
      visual: '🎨 Visual Upgrades',
      vis1Title: 'Immersive Dark Mode',
      vis1Desc: 'Deeply optimized the global color scheme, eliminating stark white contrast. Editor and full-site UI blend perfectly.',
      vis2Title: 'Glassmorphism Effect',
      vis2Desc: 'Advanced Glassmorphism design introduced to login and registration pages for a futuristic look.',
      system: '⚙️ System Enhancements',
      sys1Title: 'Theme System Decoupled',
      sys1Desc: 'Refactored `themeStore` to enable independent toggling of brand colors and light/dark modes without conflict.',
      sys2Title: 'Markdown Editor Optimized',
      sys2Desc: 'Implemented dynamic tracking of the dark theme for the editor, resulting in a superior reading experience.'
    },
    v1_0_0: {
      title: 'v1.0.0 - Core Publishing Engine Born',
      core: '🏗️ Core Achievements',
      ach1Title: 'Full-Stack Architecture Setup',
      ach1Desc: 'Completed core framework integration with Spring Boot 3 + Vue 3 + MySQL.',
      ach2Title: 'Multi-Platform Distribution Engine',
      ach2Desc: 'Initially implemented adapter architecture for platforms like WeChat, Juejin, and Zhihu.',
      ach3Title: 'Article Management System',
      ach3Desc: 'Supports Markdown editing, multi-image upload, and local draft storage.'
    }
  }
}
