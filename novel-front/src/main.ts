import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import config from './config/config'
import validate from './common/validate'
import { isAuth, getCurDateTime, getCurDate, getImageUrl } from './common/system'

const app = createApp(App)

// 全局属性
app.config.globalProperties.$config = config
app.config.globalProperties.$validate = validate
app.config.globalProperties.isAuth = isAuth
app.config.globalProperties.getCurDateTime = getCurDateTime
app.config.globalProperties.getCurDate = getCurDate
app.config.globalProperties.getImageUrl = getImageUrl

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册全局组件
app.component('QuillEditor', QuillEditor)

// 使用插件
app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})

// 挂载应用
app.mount('#app')
