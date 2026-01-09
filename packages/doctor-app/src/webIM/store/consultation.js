const consultation = {
  state: {
    addData: {},
  },

  mutations: {
    //更新添加数据
    addData(state, payload) {
      state.addData = payload
    },
  }
}

export default consultation;
