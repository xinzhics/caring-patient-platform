import Vue from "vue";
import Vuex from "vuex";
import Chat from "./chat";

Vue.use(Vuex);

const store = new Vuex.Store({
	modules: {
		chat: Chat
	}
});

export default store;
