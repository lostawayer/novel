import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:9080',
        changeOrigin: true
      },
      '/novel': {
        target: 'http://localhost:9080',
        changeOrigin: true
      },
      '/users': {
        target: 'http://localhost:9080',
        changeOrigin: true
      },
      '/file': {
        target: 'http://localhost:9080',
        changeOrigin: true
      },
      // 静态资源 - 图片等
      '/upload': {
        target: 'http://localhost:9080',
        changeOrigin: true
      }
    }
  }
})
