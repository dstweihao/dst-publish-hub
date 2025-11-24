export type PlatformType = 
  | 'wechat' 
  | 'juejin' 
  | 'cnblogs' 
  | 'zhihu' 
  | 'csdn' 
  | 'toutiao' 
  | 'jianshu'
  | 'medium'
  | 'hashnode'
  | 'devto'
  | 'oschina'
  | 'segmentfault'
  | 'douban'
  | 'baidu'
  | 'xiaohongshu'
  | 'weibo';

export interface Platform {
  id: PlatformType;
  name: string;
  icon: string;
  description: string;
  category: 'tech' | 'lifestyle' | 'social' | 'international';
  requiresAuth: boolean;
  colorClass: string;
}

export const PLATFORMS: Platform[] = [
  // 技术类平台
  {
    id: 'juejin',
    name: '稀土掘金',
    icon: '🔶',
    description: '高质量技术社区',
    category: 'tech',
    requiresAuth: true,
    colorClass: 'from-blue-500 to-blue-600',
  },
  {
    id: 'csdn',
    name: 'CSDN',
    icon: '📚',
    description: '全球最大中文IT社区',
    category: 'tech',
    requiresAuth: true,
    colorClass: 'from-orange-500 to-orange-600',
  },
  {
    id: 'cnblogs',
    name: '博客园',
    icon: '🌐',
    description: '专业的IT技术社区',
    category: 'tech',
    requiresAuth: true,
    colorClass: 'from-teal-500 to-teal-600',
  },
  {
    id: 'oschina',
    name: '开源中国',
    icon: '🔓',
    description: '开源社区&服务平台',
    category: 'tech',
    requiresAuth: true,
    colorClass: 'from-green-500 to-green-600',
  },
  {
    id: 'segmentfault',
    name: '思否',
    icon: '💬',
    description: '技术问答与讨论社区',
    category: 'tech',
    requiresAuth: true,
    colorClass: 'from-purple-500 to-purple-600',
  },
  {
    id: 'devto',
    name: 'Dev.to',
    icon: '👨‍💻',
    description: '开发者社区',
    category: 'tech',
    requiresAuth: true,
    colorClass: 'from-gray-700 to-gray-800',
  },
  {
    id: 'hashnode',
    name: 'Hashnode',
    icon: '⛓️',
    description: '开发者博客平台',
    category: 'tech',
    requiresAuth: true,
    colorClass: 'from-blue-600 to-blue-700',
  },
  {
    id: 'medium',
    name: 'Medium',
    icon: '📝',
    description: '国际内容发布平台',
    category: 'international',
    requiresAuth: true,
    colorClass: 'from-black to-gray-800',
  },
  
  // 综合内容平台
  {
    id: 'wechat',
    name: '微信公众号',
    icon: '📱',
    description: '微信官方内容平台',
    category: 'social',
    requiresAuth: true,
    colorClass: 'from-green-400 to-green-500',
  },
  {
    id: 'toutiao',
    name: '今日头条',
    icon: '📰',
    description: '内容分发平台',
    category: 'social',
    requiresAuth: true,
    colorClass: 'from-red-500 to-red-600',
  },
  {
    id: 'baidu',
    name: '百家号',
    icon: '🔍',
    description: '百度内容平台',
    category: 'social',
    requiresAuth: true,
    colorClass: 'from-red-500 to-orange-500',
  },
  {
    id: 'weibo',
    name: '微博',
    icon: '🐦',
    description: '中国最大社交平台',
    category: 'social',
    requiresAuth: true,
    colorClass: 'from-red-400 to-red-500',
  },
  
  // 生活方式类
  {
    id: 'zhihu',
    name: '知乎',
    icon: '🤔',
    description: '中文知识内容平台',
    category: 'lifestyle',
    requiresAuth: true,
    colorClass: 'from-blue-400 to-blue-500',
  },
  {
    id: 'jianshu',
    name: '简书',
    icon: '✍️',
    description: '创意写作社区',
    category: 'lifestyle',
    requiresAuth: true,
    colorClass: 'from-gray-600 to-gray-700',
  },
  {
    id: 'xiaohongshu',
    name: '小红书',
    icon: '❤️',
    description: '生活方式分享社区',
    category: 'lifestyle',
    requiresAuth: true,
    colorClass: 'from-red-500 to-red-600',
  },
  {
    id: 'douban',
    name: '豆瓣',
    icon: '🎬',
    description: '电影评论与社区',
    category: 'lifestyle',
    requiresAuth: true,
    colorClass: 'from-green-600 to-green-700',
  },
];
