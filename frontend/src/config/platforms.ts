/**
 * 平台配置中心
 * 统一管理全站支持的发布平台及其元数据
 */

export interface PlatformConfig {
  id: number;
  name: string;
  key: string; // 内部标识符
  description: string;
  color: string;
  icon: string;
}
export const SUPPORTED_PLATFORMS: PlatformConfig[] = [
  {
    id: 1,
    name: '微信公众号',
    key: 'wechat',
    description: '中国最大的公众号平台',
    color: '#07C160',
    icon: 'chat-dot-round'
  },
  {
    id: 2,
    name: '稀土掘金',
    key: 'juejin',
    description: '深耕技术的社区',
    color: '#1E80FF',
    icon: 'memo'
  },
  {
    id: 3,
    name: '博客园',
    key: 'cnblogs',
    description: '中文博客社区',
    color: '#1D5A94',
    icon: 'notebook'
  },
  {
    id: 4,
    name: '知乎',
    key: 'zhihu',
    description: '中文问答社区',
    color: '#0084FF',
    icon: 'promotion'
  },
  {
    id: 5,
    name: 'CSDN',
    key: 'csdn',
    description: '中国最大的IT社区',
    color: '#FC5531',
    icon: 'cpu'
  },
  {
    id: 6,
    name: '今日头条',
    key: 'toutiao',
    description: '内容聚合平台',
    color: '#F04142',
    icon: 'reading'
  },
  {
    id: 7,
    name: '简书',
    key: 'jianshu',
    description: '创作你的创作',
    color: '#EA6F5A',
    icon: 'edit-pen'
  },
  {
    id: 8,
    name: 'Medium',
    key: 'medium',
    description: '高质量的英文创作与阅读平台',
    color: '#000000',
    icon: 'management'
  },
  {
    id: 9,
    name: 'Hashnode',
    key: 'hashnode',
    description: '全球开发者博客社区',
    color: '#2962FF',
    icon: 'connection'
  },
  {
    id: 10,
    name: 'Dev.to',
    key: 'devto',
    description: '极客与开发者的交流平台',
    color: '#0A0A0A',
    icon: 'monitor'
  },
  {
    id: 11,
    name: '开源中国',
    key: 'oschina',
    description: '中文开源技术社区',
    color: '#21B351',
    icon: 'open'
  },
  {
    id: 12,
    name: '思否',
    key: 'segmentfault',
    description: '中文开发者社区',
    color: '#009A61',
    icon: 'question-filled'
  },
  {
    id: 13,
    name: '百家号',
    key: 'baijiahao',
    description: '百度内容创作平台',
    color: '#2932E1',
    icon: 'news'
  },
  {
    id: 14,
    name: '小红书',
    key: 'xiaohongshu',
    description: '生活方式分享',
    color: '#FF2442',
    icon: 'camera'
  },
  {
    id: 15,
    name: '豆瓣',
    key: 'douban',
    description: '文艺青年聚集地',
    color: '#007722',
    icon: 'film'
  },
  {
    id: 16,
    name: '微博',
    key: 'weibo',
    description: '随时随地发现新鲜事',
    color: '#E6162D',
    icon: 'view'
  }
];

export const getPlatformByKey = (key: string) => 
  SUPPORTED_PLATFORMS.find(p => p.key === key || p.name === key);
