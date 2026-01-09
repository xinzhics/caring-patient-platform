import Vue from 'vue';
import { Icon, Tag, Col, Row, List, PullRefresh } from 'vant';
import Api from '@/api/Content.js'
Vue.use(PullRefresh);
Vue.use(Col);
Vue.use(List);
Vue.use(Row);
Vue.use(Tag);
Vue.use(Icon);
export default {
  data () {
    return {
      list: [],
      name: "",
      patient: "",
      assistant: "",
      obj: {
        current: 1,
        model: {
          formResultId: "",
          updateUserId: ""
        },

      },
      loading: false,
      finished: false,
      refreshing: false,
      pages: "",
      current: 2
    };
  },
  mounted () {

    this.name = this.$getDictItem('doctor')//医生
    this.patient = this.$getDictItem('patient')//患者assistant
    this.assistant = this.$getDictItem('assistant')//患者assistant
  },
  beforeMount () {
    // this.obj.model.updateUserId = localStorage.getItem('caring_doctor_id')
    this.obj.model.formResultId = this.$route.query.id
    Api.formResultBackUp(this.obj).then(res => {
      this.list = res.data.data.records
      this.pages = res.data.data.pages
    })
  },
  methods: {
    gonextpage (e) {
      // this.$router.push("/baseinfo/index/history/detailsHistory")
      this.$router.push({
        path: `/baseinfo/index/history/detailsHistory`,
        query: {
          id: e.id,
          name: e.updateUserName,
          createTime: e.createTime,
          userType: e.userType
        }
      })
    },
    onLoad () {
      setTimeout(() => {
        if (this.refreshing) {
          this.list = [];
          this.current = 1;
          this.refreshing = false;
        }
        this.getInfo()
      }, 500);
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.onLoad();
    },
    getInfo () {
      const params = {
        current: this.current,
        model: {
          formResultId: this.$route.query.id,
          updateUserId: ""
        },
      }
      this.current += 1;
      Api.formResultBackUp(params).then((res) => {
        if (res.data.code === 0) {
          if (this.current === 1) {
            this.list = res.data.data.records
          }
          if (this.current > 1) {
            this.list.push(...res.data.data.records)
          }
          this.loading = false;

        }
      })
      if (this.current > this.pages) {
        this.finished = true;
        //
      }
    },
  },
};
