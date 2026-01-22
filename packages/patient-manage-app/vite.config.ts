import { ConfigEnv, UserConfigExport, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import Components from 'unplugin-vue-components/vite'
// import { VantResolver } from 'unplugin-vue-components/resolvers'  // 暂时注释掉Vant解析器
// import styleImport, { VantResolve } from 'vite-plugin-style-import';  // 暂时注释掉样式导入插件
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
			// 禁用Vant的自动导入，改用手动导入
			Components({
				// 不再使用VantResolver
				directives: true,
			}),
			// 移除vite-plugin-style-import插件
		],
		server: {
			host: true,
			port: Number(VITE_PORT),
			open: false,
			hmr: true,
			proxy: {
				'/api': {
					target: VITE_PROXY,
					changeOrigin: true,
				},
			},
		},
	}
}