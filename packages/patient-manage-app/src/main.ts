
import { createApp } from 'vue'
import 'amfe-flexible'
import 'reset.css'
import router from './router'
import App from './App.vue'
import { Toast } from 'vant'
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'
const pinia = createPinia()
import 'vant/lib/index.css';


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



const app = createApp(App)
Toast.setDefaultOptions({ duration: 3000 })
app.use(router).
	use(pinia).mount('#app')


