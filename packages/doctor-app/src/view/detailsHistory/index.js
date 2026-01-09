import Vue from 'vue';
import Api from '@/api/Content.js'
import { Icon, Tag, Col, Row, Popup, Dialog } from 'vant';
Vue.use(Popup);
import attrPage from '@/components/arrt/editorCopy'
Vue.use(Col);
Vue.use(Row);
Vue.use(Tag);
Vue.use(Icon);
Vue.use(Dialog);
export default {
  components: { attrPage },
  data () {
    return {
      name: "",
      patient: "",
      assistant: "",
      obj: {
        id: ""
      },
      currentFields: [],
      // "ids[]":"123"
      id: "",
      show: false,
    };
  },
  mounted () {
    // console.log("789123", this.ccrField, this.gfrField, this.courseOfDiseaseField);
    this.name = this.$getDictItem('doctor')//医生
    this.patient = this.$getDictItem('patient')//患者assistant
    this.assistant = this.$getDictItem('assistant')//患者assistant

  },
  created () {

    this.obj.id = this.$route.query.id
    Api.nursingFormResultBackUp(this.$route.query.id).then(res => {
      // console.log("家哈哈哈哈", res);
      this.id = res.data.data.id
      this.currentFields = JSON.parse(res.data.data.jsonContent)
      console.log("this.currentFields123", this.currentFields);
      this.$forceUpdate()
    })
  },
  methods: {
    showPopup () {
      this.show = true;
    },
    confirm () {
      let params = {
        id: this.id
      }
      Api.delFormResultBackUp(params).then(res => {
        // console.log("res", res);
      })
      this.$router.go(-1)
    }
  }
};
