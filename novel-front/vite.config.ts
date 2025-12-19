import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts',
    }),
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 8080,
    proxy: {
      '/novel': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
      '/users': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
      '/file': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
      '/upload': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
      '/xiaoshuoxinxi': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
      '/config': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
      '/messages': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
      '/yonghu': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
      '/zuozhe': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
        },
      },
    },
  },
})
