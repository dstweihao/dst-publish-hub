// 文章类型定义
export interface Article {
  id?: string;
  title: string;
  content: string;
  description?: string;
  tags?: string[];
  theme: string;
  platforms: PublishPlatform[];
  status: 'draft' | 'published' | 'scheduled';
  createdAt?: Date;
  updatedAt?: Date;
  publishedAt?: Date;
  viewCount?: number;
}

export interface PublishPlatform {
  name: 'wechat' | 'juejin' | 'cnblogs' | 'zhihu' | 'csdn' | 'toutiao' | 'jianshu';
  enabled: boolean;
  config?: Record<string, any>;
  publishedUrl?: string;
  publishedAt?: Date;
  status: 'pending' | 'published' | 'failed';
  errorMsg?: string;
}

export interface PublishResult {
  platform: PublishPlatform['name'];
  success: boolean;
  url?: string;
  error?: string;
}

export interface User {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  createdAt: Date;
}

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data?: T;
}
