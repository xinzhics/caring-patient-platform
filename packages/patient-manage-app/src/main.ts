import { createApp } from 'vue'
import 'amfe-flexible'
import 'reset.css'
import router from './router'
import App from './App.vue'
import { Toast } from 'vant'
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'
import { vantPlugins } from './plugins/vant'
const pinia = createPinia()
import 'vant/lib/index.css'

pinia.use(
	createPersistedState({
		storage: window.localStorage,
		beforeRestore: (context) => {
		},
		afterRestore: (context) => {
		},
		serializer: {
			serialize: JSON.stringify,
			deserialize: JSON.parse,
		},
	})
)

// 设置Toast默认持续时间为3秒
Toast.setDefaultOptions({ duration: 3000 })

const app = createApp(App)
app.use(vantPlugins).
	use(router).
	use(pinia).mount('#app')