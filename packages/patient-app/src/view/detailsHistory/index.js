import Vue from 'vue';
import Api from '@/api/Content.js'
import { Icon, Tag, Col, Row } from 'vant';
Vue.use(Col);
Vue.use(Row);
Vue.use(Tag);
Vue.use(Icon);
export default {
  components:{
    attrPage:() => import('@/components/arrt/formResult'),
    navBar: () => import('@/components/headers/navBar'),
  },
  data () {
    return {
      name: "",
      patient: "",
      assistant: "",
      obj: {
        id: ""
      },
      currentFields: [],

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
      this.currentFields = JSON.parse(res.data.data.jsonContent)
      console.log(this.currentFields);
      this.$forceUpdate()
    })
  },
};
