export interface User {
  id: string
  username: string
  email: string
  avatar?: string
}

export interface Article {
  id: string
  title: string
  content: string
  markdown: string
  theme: string
  status: 'draft' | 'published' | 'archived'
  createdAt: string
  updatedAt: string
  userId: string
}

export interface PlatformAccount {
  id: string
  platform: string
  accountName: string
  token: string
  isConnected: boolean
  createdAt: string
}

export interface PlatformPublish {
  id: string
  articleId: string
  platform: string
  publicUrl: string
  status: 'pending' | 'success' | 'failed'
  errorMessage?: string
  publishedAt: string
}

export interface PublishRequest {
  articleId: string
  platforms: string[]
}

export interface PublishResult {
  platform: string
  success: boolean
  publicUrl?: string
  error?: string
}
