import { acceptHMRUpdate, defineStore } from 'pinia'
export const useUserStore = defineStore('storeUser', {
	state: () => {
		return {
			token: '', // 登录token

		}
	},
	getters: {
		token: (state) => state.token,
	},
	persist: {
		key: 'user',
	},
})

if (import.meta.hot) {
	import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}
