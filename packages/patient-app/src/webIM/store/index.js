import Vue from "vue";
import Vuex from "vuex";
import Chat from "./chat";
import Base from "./base";

Vue.use(Vuex);

const store = new Vuex.Store({
	modules: {
		chat: Chat,
    base: Base
	}
});

export default store;
