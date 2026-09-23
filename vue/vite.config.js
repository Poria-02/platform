import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  const prefix = env.VITE_API_PREFIX || '/api'
  return {
    plugins: [vue()],
    resolve: { alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) } },
    server: { port: 8000, proxy: { [prefix]: {
      target: env.VITE_GATEWAY_TARGET || 'http://localhost:9999',
      changeOrigin: true,
      rewrite: path => path.slice(prefix.length) || '/'
    } } }
  }
})
