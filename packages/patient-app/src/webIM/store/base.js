

const Base = {
  state: {
    pageActive: 0,
  },
  mutations: {
    // 修改状态的方法（同步）
    setPageActive(state, payload) {
      const {pageActive} = payload;
      state.pageActive = pageActive;
    }
  },
  getters: {
    // 获取派生状态
    getPageActive(state) {
      return state.pageActive;
    }
  }
}
export default Base;
