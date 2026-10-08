import { networkInterfaces } from 'node:os'
import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

function displayCurrentAddress() {
  return {
    name: 'display-current-address',
    apply: 'serve',
    configureServer(server) {
      const printUrls = server.printUrls.bind(server)
      server.printUrls = () => {
        const address = server.httpServer?.address()
        const ip = Object.entries(networkInterfaces())
          .filter(([name]) => !/vEthernet|VMware|VirtualBox|Hyper-V|WSL|Docker/i.test(name))
          .flatMap(([, entries]) => entries || [])
          .find(entry => entry.family === 'IPv4' && !entry.internal && !entry.address.startsWith('169.254.'))?.address
        const logger = server.config.logger
        const info = logger.info
        let displayed = false
        logger.info = (message, options) => {
          if (displayed && String(message).includes('Network')) return
          info.call(logger, message, options)
          if (!displayed && String(message).includes('Local') && ip && address && typeof address !== 'string') {
            const colored = String(message).includes('\x1b[')
            const arrow = colored ? '\x1b[32m➜\x1b[39m' : '➜'
            const url = `http://${ip}:${address.port}/`
            info.call(logger, `  ${arrow}  IP:      ${colored ? `\x1b[36m${url}\x1b[39m` : url}`)
            displayed = true
          }
        }
        try { printUrls() }
        finally { logger.info = info }
      }
    }
  }
}

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  const prefix = env.VITE_API_PREFIX || '/api'
  return {
    plugins: [vue(), displayCurrentAddress()],
    resolve: { alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) } },
    server: { host: '0.0.0.0', port: 8000, proxy: { [prefix]: {
      target: env.VITE_GATEWAY_TARGET || 'http://localhost:9999',
      changeOrigin: true,
      rewrite: path => path.slice(prefix.length) || '/'
    } } }
  }
})
