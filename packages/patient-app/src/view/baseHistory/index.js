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
  components:{
    navBar: () => import('@/components/headers/navBar'),
  },
  data () {
    return {
      list: [],
      name: "",
      patient: "",
      assistant: "",
      loading: false,
      finished: false,
      refreshing: false,
      pages: "",
      current: 1
    };
  },
  mounted () {
    this.name = this.$getDictItem('doctor')//医生
    this.patient = this.$getDictItem('patient')//患者assistant
    this.assistant = this.$getDictItem('assistant')//患者assistant
    this.onRefresh()
  },
  methods: {
    detailsHistory (e) {
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
      const params = {
        current: this.current,
        model: {
          formResultId: this.$route.query.id
        },
      }
      if (this.loading) {
        return
      }
      this.loading = true;
      Api.formResultBackUp(params).then((res) => {
        this.loading = false
        this.refreshing = false
        if (res.data.code === 0) {
          this.list.push(...res.data.data.records)
          if (res.data.data.pages <= this.current) {
            this.finished = true;
          } else {
            this.finished = false
            this.current++
          }
        }
      })
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false;
      this.current = 1
      this.list = []
      // 重新加载数据
      this.onLoad();
    }
  },
};
