import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import enUS from './en-US'
// 导入 Element Plus 的语言包
import elZhCn from 'element-plus/es/locale/lang/zh-cn'
import elEnUs from 'element-plus/es/locale/lang/en'

// 合并自有字典与第三方字典
const messages = {
  'zh-CN': {
    ...zhCN,
    el: elZhCn.el
  },
  'en-US': {
    ...enUS,
    el: elEnUs.el
  }
}

export type SupportedLocale = 'zh-CN' | 'en-US'

// 取本地存储的语种或默认中文
const savedLocale = localStorage.getItem('app-locale') as SupportedLocale
const initLocale = savedLocale || 'zh-CN'

const i18n = createI18n({
  legacy: false,          // 启用 Composition API 模式
  locale: initLocale,     // 默认语种
  fallbackLocale: 'zh-CN',// 降级语种
  messages
})

export default i18n
