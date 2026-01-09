const db = {
  save(key, value) {
    const projectName = process.env.VUE_APP_PROJECT_NAME
    localStorage.setItem(projectName + '_' + key, JSON.stringify(value))
  },
  get(key, defaultValue = {}) {
    const projectName = process.env.VUE_APP_PROJECT_NAME
    try {
      return JSON.parse(localStorage.getItem(projectName + '_' + key)) || defaultValue
    } catch (err) {
      return defaultValue
    }
  },
  remove(key) {
    const projectName = process.env.VUE_APP_PROJECT_NAME
    localStorage.removeItem(projectName + '_' + key)
  },
  clear() {
    const projectName = process.env.VUE_APP_PROJECT_NAME
    for (const key in localStorage) {
      console.log(key)
      if (key.indexOf(projectName) !== -1) {
        localStorage.removeItem(key)
      }
    }
  }
}

export default db
