import Vue from 'vue';
import {
  Search,
  DropdownMenu,
  DropdownItem
} from 'vant';
Vue.use(Search);
Vue.use(DropdownMenu);
Vue.use(DropdownItem);

import Api from '@/api/doctor.js'



export default {
  // beforeRouteLeave(to, from, next) {
  //   to.meta.keepAlive = true
  //   next(0)
  // },
  data() {
    return {
      list: [],
      loading: false,
      finished: false,
      refreshing: false,
      provinceId: '',

      cityId: '',
      // 请求参数
      params: {
        order: 'ascending',
        current: 1,
        model: {},
        size: 100
      },
      type: 1,
      beforeSearchType: 1,
      hospitalName: '',
      focus: true,
      issousuo:0
    }
  },
  // scrollTop
  props: {
    field: {
      type: Object,
      default: {}
    },
    scrollTop: {
      type: Number,
    },
  },
  created () {
    this.pageProvince()
  },
  methods: {
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.onLoad();
    },
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.params.current = 1;
          this.refreshing = false;
        }
        if (this.type === 1) {
          this.pageProvince()
        } else if (this.type === 2) {
          this.pageCity()
        } else if (this.type === 3 || this.type === 4) {
          this.hospitalPage()
        }
      }, 500);
    },
    // 点省
    chooseOptions(count) {
      if (count === 1 && this.type !== 1) {
        this.list = []
        this.type = 1;
        this.pageProvince()
      }
      if (count === 2 && this.type !== 2 && this.provinceId) {
        this.list = []
        this.type = 2;
        this.params.current = 1
        this.pageCity()
      }
      if (count === 3 && this.type !== 3 && this.cityId) {
        this.list = []
        this.type = 3;
        this.params.current = 1
        this.hospitalPage()
      }
    },
    // 点省选项
    chooseOption(val) {
      this.finished = false;
      if (this.type == 1) {
        // this.params.model.provinceId = val.id
        this.provinceId = val.id;
        this.params.current = 1
        this.type = 2;
        this.list = []
        this.finished = false;
        this.pageCity()
      } else if (this.type === 2) {
        this.cityId = val.id;
        this.params.current = 1
        this.type = 3;
        this.list = []
        this.finished = false;

        this.hospitalPage()
      } else {
        this.chooseHospitalOption2(val)
      }
    },
    chooseHospitalOption2(val) {
      let arr = {
        valueText: val.hospitalName,
        attrValue: val.id
      }
      this.field.values = []
      this.field.values.push(arr)
      this.hide()
    },
    hide() {
      this.issousuo=0
      this.$emit("hideList", false);
      document.documentElement.scrollTop=this.scrollTop
    },
    // 获取焦点
    onFocus () {
      this.focus=false
      document.body.style.overflow = "hidden";
    },

    onBlur() {
      this.focus=true
      document.body.style.overflow = "visible";
    },
    // 点搜索
    onSearch() {
      if (this.issousuo === 2) {
        this.type = 4;
        this.refreshing = true
        this.finished = false;
        // this.onLoad()
        this.issousuo=1
      } else {
        this.issousuo=3
      }
    },
    onInput(val) {
      if (val !== '') {
        this.list=[]
        this.issousuo=2
      }
      if (val === "") {
        this.type = this.beforeSearchType
        this.refreshing = true
        this.finished = false;
        this.onLoad()
      }
      // if (val === "") {
      //   this.list = []
      //   if (this.beforeSearchType === 1) {
      //     this.params.current = 1
      //     this.type = 1
      //     this.finished = false;
      //     this.pageProvince()
      //   } else if (this.beforeSearchType === 2) {
      //     this.params.current = 1
      //     this.type = 2
      //     this.finished = false;
      //     this.pageCity()
      //   } else if (this.beforeSearchType === 3) {
      //     this.params.current = 1
      //     this.type = 3
      //     // this.finished = false;
      //     this.hospitalPage()
      //   }
      // }
    },
    // 渲染数据
    getItem(item) {
      if (this.type === 1) {
        return item.province;
      } else if (this.type === 2) {
        return item.city;
      } else {
        return item.hospitalName
      }
    },

    // 分页查询省
    pageProvince() {
      this.params.model = {}
      this.params.current = 1
      Api.listByIds(this.params).then(res => {
        if (res.data && res.data.code === 0) {
          if (this.params.current === 1) {
            this.list = res.data.data.records
          } else {
            this.list.push(...res.data.data.records)
          }

          this.loading = false;
          if (res.data.data.records && res.data.data.records.length < 100) {
            this.finished = true;
          }
          this.params.current++;
        }
      })
    },
    // 分页查询市
    pageCity() {
      this.params.model.provinceId = this.provinceId
      Api.listByCity(this.params).then(res => {
        if (res.data && res.data.code === 0) {
          if (res.data && res.data.code === 0) {
            if (this.params.current === 1) {
              this.list = res.data.data.records
            } else {
              this.list.push(...res.data.data.records)
            }

            this.params.current++;
            this.loading = false;
            if (res.data.data.records && res.data.data.records.length < 100) {
              this.finished = true;
            }
          }
        }
      })
    },
    hospitalPage() {
      this.params.model = {
        cityId: this.cityId,
        hospitalName: this.hospitalName,
        provinceId: this.provinceId
      }
      // console.log(this.params.current);
      Api.hospitalPage(this.params).then(res => {

        if (res.data && res.data.code === 0) {
            console.log(this.params.current);
            if (this.params.current === 1 ) {
              this.list = res.data.data.records
            } else  {
              console.log(this.list == res.data.data.records);
              this.list.push(...res.data.data.records)
              // console.log(this.list);
              // console.log(res.data.data.records);
              // if (this.list == res.data.data.records) {
              //   console.log(123);
              // }
          }
          this.params.current++;
          // if (this.issousuo !== 3) {
          //   this.params.current++;
          // }

            this.loading = false;
            if (res.data.data.records && res.data.data.records.length < 100) {
              this.finished = true;
            }
        }
      })
      // console.log(this.list);
    }
  }
};

