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
    color: '#0066FF',
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
    id: 14,
    name: '小红书',
    key: 'xiaohongshu',
    description: '生活方式分享',
    color: '#FF2442',
    icon: 'camera'
  }
];

export const getPlatformByKey = (key: string) => 
  SUPPORTED_PLATFORMS.find(p => p.key === key || p.name === key);
