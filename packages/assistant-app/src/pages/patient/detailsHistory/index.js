import Vue from 'vue'
import { nursingFormResultBackUp } from '@/api/formApi.js'
import { Icon, Tag, Col, Row, Sticky } from 'vant'
import attrPage from '@/components/arrt/formResultHistory'
Vue.use(Col)
Vue.use(Row)
Vue.use(Tag)
Vue.use(Icon)
Vue.use(Sticky)
export default {
  components: { attrPage },
  data () {
    return {
      name: '',
      patient: '',
      assistant: '',
      formResultBackUpId: this.$route.query.id,
      currentFields: []
    }
  },
  mounted () {
    this.name = this.$getDictItem('doctor') // 医生
    this.patient = this.$getDictItem('patient') // 患者assistant
    this.assistant = this.$getDictItem('assistant') // 患者assistant
  },
  created () {
    nursingFormResultBackUp(this.formResultBackUpId).then(res => {
      this.currentFields = JSON.parse(res.data.jsonContent)
      this.$forceUpdate()
    })
  },
  methods: {
    back () {
      this.$router.go(-1)
    }
  }
}
