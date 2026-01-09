import { ConfigEnv, UserConfigExport, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import Components from 'unplugin-vue-components/vite'
import { VantResolver } from 'unplugin-vue-components/resolvers'
import styleImport, { VantResolve } from 'vite-plugin-style-import';
// https://vitejs.dev/config/
export default ({ mode }: ConfigEnv): UserConfigExport => {
	const {
		VITE_PROXY,
		VITE_PUBLIC_PATH = '/',
		VITE_PORT = 9085,
	} = loadEnv(mode, process.cwd())
	return {
		base: VITE_PUBLIC_PATH,
		build: {
			minify: 'terser',
			terserOptions: {
				compress: {
					drop_console: true,
					drop_debugger: true,
				}
			}
		},
		css: {
			preprocessorOptions: {
				less: {
					javascriptEnabled: true,
					modifyVars: {},

				},
			},
		},
		resolve: {
			extensions: ['.ts', '.js'],
			alias: {
				'@': '/src',
			},
		},
		plugins: [
			vue(),
			Components({
				resolvers: [VantResolver()],
			}),
		],
		server: {
			host: '0.0.0.0',
			//@ts-ignore
			port: VITE_PORT,
			proxy: {
				'^/api/.*': {
					changeOrigin: true,
					target: VITE_PROXY,
					rewrite: (path) => path.replace(/^\/api/, '')

				},
			},
		},
	}
}
