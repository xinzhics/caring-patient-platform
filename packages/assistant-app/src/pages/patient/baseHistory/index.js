import Vue from 'vue'
import { Icon, Tag, Col, Row, List, PullRefresh, Sticky } from 'vant'
import { pageFormResultBackUp } from '@/api/formApi.js'
Vue.use(PullRefresh)
Vue.use(Col)
Vue.use(List)
Vue.use(Row)
Vue.use(Tag)
Vue.use(Icon)
Vue.use(Sticky)
export default {
  data () {
    return {
      list: [],
      name: '',
      patient: '',
      assistant: '',
      loading: false,
      finished: false,
      refreshing: false,
      current: 1
    }
  },
  mounted () {
    this.name = this.$getDictItem('doctor')// 医生
    this.patient = this.$getDictItem('patient')// 患者assistant
    this.assistant = this.$getDictItem('assistant')// 患者assistant
  },
  methods: {
    gonextpage (e) {
      this.$router.push({
        path: `/patient/baseinfo/index/history/detailsHistory`,
        query: {
          id: e.id,
          name: e.updateUserName,
          createTime: e.createTime,
          userType: e.userType
        }
      })
    },
    onLoad () {
      this.loading = true
      const params = {
        current: this.current,
        model: {
          formResultId: this.$route.query.id
        }
      }
      pageFormResultBackUp(params).then((res) => {
        if (res.code === 0) {
          this.list.push(...res.data.records)
          if (res.data.pages === 0 || res.data.pages === this.current) {
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
          this.loading = false
          this.refreshing = false
        }
      })
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      // 重新加载数据
      this.list = []
      this.current = 1
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true
      this.onLoad()
    },
    back () {
      this.$router.go(-1)
    }
  }
}
