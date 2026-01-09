import Vue from "vue";
import Vuex from "vuex";
import Chat from "./chat";
import consultation from './consultation';
import aiChat from './aiChat';
import baiDuAIChat from './baiDuAIChat';

Vue.use(Vuex);

const store = new Vuex.Store({
	modules: {
		chat: Chat,
    consultation: consultation,
    aiChat: aiChat,
    baiDuAIChat: baiDuAIChat,
	}
});

export default store;
