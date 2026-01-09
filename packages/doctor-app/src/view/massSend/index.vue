<template>
  <section class="allContent">
    <div>
      <x-header :left-options="{backText: ''}">选择群发对象</x-header>

      <div class="item_first" v-for="(item,index) in patientList" :key="item.id">
        <div v-if="item.islabel" style="color: #666666; font-size: 14px; padding-top: 10px;
                padding-bottom: 10px; padding-left: 15px;">
          {{ item.label }}
        </div>
        <div v-else>
          <div @click="checkItem(index)" class="item">
            <div class="checkGroup" @click="checkAll(index)" @click.stop>
              <img v-if="item.group" slot="right" :src="require('@/assets/my/checkbox_check.png')"
                   width="20px" height="20px">
              <img v-else slot="right" :src="require('@/assets/my/checkbox_round.png')"
                   width="20px" height="20px">
            </div>
            {{ item.label }} ({{ item.type && item.type === 'group' ? item.count : item.member.length }})
            <van-icon v-if="isOpenItem[index] && isOpenItem[index].group" name="arrow-down"
                      style="flex: 1; text-align: right; margin-right: 10px"/>
            <van-icon v-else name="arrow" style="flex: 1; text-align: right; margin-right: 10px"/>
          </div>
          <ul class="item_second" v-show="isOpenItem[index] && isOpenItem[index].group">
            <li v-for="(people,i) in item.member" :key="people.pid"
                @click="stateChanged(index, i)">
              <div class="checkGroup">
                <img v-if="people.flag" slot="right" :src="require('@/assets/my/checkbox_check.png')"
                     width="20px" height="20px">
                <img v-else slot="right" :src="require('@/assets/my/checkbox_round.png')"
                     width="20px" height="20px">
              </div>
              <img :src="people.avatar" alt=""
                   style="width:2.2rem;height:2.2rem;vertical-align: middle; margin-right: 10px; border-radius: 50%">
              <div>
                {{ doctorId === people.doctorId && people.doctorRemark ? people.name + '('+people.doctorRemark + ')': people.name }}
              </div>
            </li>
          </ul>
        </div>
      </div>
      <x-button style="margin-top:40px;width:40%; margin-bottom: 30px; background-color:#3F86FF" type="primary"
                @click.native="jumpPage()">确定
      </x-button>
    </div>
  </section>
</template>
<script>
  import Api from '@/api/doctor.js'
  import ContentApi from '@/api/Content.js'

  export default {
    name: "demo",
    data() {
      return {
        headColor: ['#1c71fb', '#f7aa47', '#00c182', '#ff6769', '#917ee6', '#2cb2eb'],//待选颜色
        patientList: [], //从后台获取的人员列表信息
        isOpenItem: [],//控制每级面板的打开与折叠
        diagnosisList: [], //诊段类别
        current: 1,
        data: [],
        patient: this.$getDictItem('patient'),
        register:this.$getDictItem('register'),
        params:{
          current: 1,
          model: {
            doctorCustomGroupId: '',
          },
          size: 50,
        },
        groupList: [],
        position: 0,
        isClickType: false,
        doctorId: localStorage.getItem('caring_doctor_id')

      }
    },
    mounted() {
      this.getPatient()
    },
    methods: {
      getPatient() {//获取患者
        let params = {
          current: this.current,
          model: {doctorId: localStorage.getItem('caring_doctor_id')},
          order: 'descending',
          size: 50,
        }
        Api.postquery(params).then((res) => {
          if (res.data.code === 0) {
            if (res.data.data.records.length > 0 && res.data.data.records.length === 50) {
              res.data.data.records.forEach(item => {
                this.data.push(item)
              })
              this.current++;
              this.getPatient()
            } else {
              res.data.data.records.forEach(item => {
                this.data.push(item)
              })
              let allPatient = [];
              let scanCodePatient = [];
              let registerPatient = [];
              this.data.forEach(item => {
                //全部患者
                allPatient.push({
                  id: item.id,
                  name: item.name,
                  avatar: item.avatar,
                  flag: false,
                  doctorRemark:item.doctorRemark,
                  doctorId:item.doctorId
                })
                if (item.status == 0) {
                  //扫码患者
                  scanCodePatient.push({
                    id: item.id,
                    name: item.name,
                    avatar: item.avatar,
                    flag: false,
                    doctorRemark:item.doctorRemark,
                    doctorId:item.doctorId
                  })
                } else if (item.status == 1) {
                  //注册患者
                  registerPatient.push({
                    id: item.id,
                    name: item.name,
                    avatar: item.avatar,
                    flag: false,
                    doctorRemark:item.doctorRemark,
                    doctorId:item.doctorId
                  })
                }
                //判断患者是否有诊段类别
                if (item.diagnosisName) {
                  if (this.diagnosisList.length == 0) {
                    this.diagnosisList.push({
                      diagnosisId: item.diagnosisId,
                      diagnosisName: item.diagnosisName,
                      doctorRemark:item.doctorRemark,
                      doctorId:item.doctorId
                    })
                  } else {
                    let result = this.diagnosisList.some(diagnosisItem => {
                      if (item.diagnosisId === diagnosisItem.diagnosisId) {
                        return true
                      }
                    })
                    if (!result) {
                      this.diagnosisList.push({
                        diagnosisId: item.diagnosisId,
                        diagnosisName: item.diagnosisName,
                        doctorRemark:item.doctorRemark,
                        doctorId:item.doctorId
                      })
                    }
                  }
                }
              })
              this.patientList.push({
                id: 1,
                label: "所有"+this.patient,
                member: allPatient,
                islabel: false,
                group: false
              })

              this.patientList.push({
                label: "按"+this.patient+"状态",
                islabel: true,
              })

              this.patientList.push({
                id: 2,
                label: "扫码"+this.patient,
                islabel: false,
                member: scanCodePatient,
                group: false
              })

              this.patientList.push({
                id: 3,
                label: this.register+this.patient,
                member: registerPatient,
                islabel: false,
                group: false
              })

              if (this.diagnosisList.length > 0) {
                this.patientList.push({
                  label: "按诊段类型",
                  islabel: true,
                })
              }

              this.diagnosisList.forEach(item => {
                let diagnosis = []
                this.data.forEach(resItem => {
                  if (resItem.diagnosisName && item.diagnosisName == resItem.diagnosisName) {

                    //诊段患者
                    diagnosis.push({
                      id: resItem.id,
                      name: resItem.name,
                      avatar: resItem.avatar,
                      flag: false,
                      doctorRemark:resItem.doctorRemark,
                      doctorId: resItem.doctorId
                    })
                  }
                })

                this.patientList.push({
                  id: item.diagnosisId,
                  label: item.diagnosisName,
                  islabel: false,
                  member: diagnosis,
                  group: false,
                  doctorRemark:item.doctorRemark,
                  doctorId:item.doctorId
                })
              })
            }

          } else {
            this.patientList.push({
              id: 1,
              label: "所有"+this.patient,
              member: allPatient,
              islabel: false,
              group: false
            })

            this.patientList.push({
              label: "按"+this.patient+"状态",
              islabel: true,
            })

            this.patientList.push({
              id: 2,
              label: "扫码"+this.patient,
              islabel: false,
              member: scanCodePatient,
              group: false
            })

            this.patientList.push({
              id: 3,
              label: this.register+this.patient,
              islabel: false,
              member: registerPatient,
              group: false
            })
          }
          this.getDoctorCustomGroup()
          this.initData();
        })
      },
      getDoctorCustomGroup() {
        Api.getdoctorCustomGroup()
          .then(res => {
            if (res.data.code === 0) {
              this.patientList.push({
                label: "按自定义小组",
                islabel: true,
              })
              this.isOpenItem.push({
                group: false,
              });
              res.data.data.forEach(item => {
                this.patientList.push({
                  id: item.id,
                  label: item.groupName,
                  islabel: false,
                  member: [],
                  count: item.groupNumberTotal,
                  group: false,
                  type: 'group',
                  isloading: false,

                })
                this.isOpenItem.push({
                  group: false,
                });
              })
            }
          })
      },
      jumpPage() {//跳转到群发内容
        let patient = "";
        this.patientList.forEach(item => {
          if (!item.islabel) {
            item.member.forEach(memberItem => {
              if (memberItem.flag && patient.indexOf(memberItem.id) == -1) {
                patient += memberItem.id + ","
              }
            })
          }
        })
        if (patient) {
          this.$router.push({name: '群发消息编辑', params: {patientId: patient}})
        } else {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '请选择'+this.patient
          })
        }
      },
      checkItem(index) {//展开或折叠面板
        console.log(this.patientList[index])
        if (this.patientList[index].type && this.patientList[index].type === 'group') {
          if (!this.patientList[index].isloading) {
            this.params.current = 1;
            this.params.model.doctorCustomGroupId = this.patientList[index].id;
            this.position = index
            this.isClickType = true
            this.getDoctorCustomPatient()
          }else {
            this.$set(this.isOpenItem[index], 'group', !this.isOpenItem[index].group);
          }
        }else {
          this.$set(this.isOpenItem[index], 'group', !this.isOpenItem[index].group);
        }
      },
      checkAll(index) {//第一层级的全选操作
        if (this.patientList[index].type && this.patientList[index].type === 'group') {

          //如没有加载过数据，则需要加载
          if (!this.patientList[index].isloading) {
            this.isClickType = false
            this.position = index
            this.params.current = 1;
            this.params.model.doctorCustomGroupId = this.patientList[index].id;
            this.$set(this.patientList[index], 'group', !this.patientList[index].group);//改变当前按钮的选中状态
            this.getDoctorCustomPatient()
          }else {
            //有数据，则全选或者取消
            this.$set(this.patientList[index], 'group', !this.patientList[index].group);//改变当前按钮的选中状态
            if (!this.patientList[index].member) {
              return
            }
            if (!this.patientList[index].group) {//从全选 =》 全不选
              for (let i = 0, len = this.patientList[index].member.length; i < len; i++) {
                this.$set(this.patientList[index].member[i], 'flag', false)
              }
            } else {
              for (let i = 0, len = this.patientList[index].member.length; i < len; i++) {
                this.$set(this.patientList[index].member[i], 'flag', true)
              }
            }
          }
        }else{
          this.$set(this.patientList[index], 'group', !this.patientList[index].group);//改变当前按钮的选中状态
          if (!this.patientList[index].member) {
            return
          }
          if (!this.patientList[index].group) {//从全选 =》 全不选
            for (let i = 0, len = this.patientList[index].member.length; i < len; i++) {
              this.$set(this.patientList[index].member[i], 'flag', false)
            }
          } else {
            for (let i = 0, len = this.patientList[index].member.length; i < len; i++) {
              this.$set(this.patientList[index].member[i], 'flag', true)
            }
          }
        }
      },
      stateChanged(index, i) {
        if (this.patientList[index].member[i].flag) {
          this.$set(this.patientList[index].member[i], 'flag', false)
          this.$set(this.patientList[index], 'group', false);//改变当前按钮的选中状态
        } else {
          this.$set(this.patientList[index].member[i], 'flag', true)
          for (let i = 0, len = this.patientList[index].member.length; i < len; i++) {
            if (!this.patientList[index].member[i].flag) {
              return false
            }
          }
          this.$set(this.patientList[index], 'group', true);//改变当前按钮的选中状态
        }
      },
      initData() {
        //数据初始化
        //设置头像背景颜色
        let len = this.patientList.length;
        for (let i = 0; i < len; i++) {
          //根据数据初始化折叠面板折叠状态
          this.isOpenItem.push([]);
          this.$set(this.isOpenItem[i], 'group', false)
        }
      },
      getDoctorCustomPatient() {
        ContentApi.pageGroupPatient(this.params).then(res=>{
          if (res.data.code==0){
            console.log(res.data.data)
            res.data.data.records.forEach(item => {
              this.groupList.push({
                id: item.id,
                name: item.name,
                avatar: item.avatar,
                flag: false,
                doctorRemark:item.doctorRemark,
                doctorId:item.doctorId
              })
            })
            if (this.groupList.length >= res.data.data.total) {
              this.patientList[this.position].member = this.groupList
              this.patientList[this.position].isloading = true
              this.groupList = []
              if (this.isClickType) {
                console.log('this.isOpenItem[this.position]', this.isOpenItem[this.position])
                this.$set(this.isOpenItem[this.position], 'group', !this.isOpenItem[this.position].group);
              }else {

                if (!this.patientList[this.position].member) {
                  return
                }
                if (!this.patientList[this.position].group) {//从全选 =》 全不选
                  for (let i = 0, len = this.patientList[this.position].member.length; i < len; i++) {
                    this.$set(this.patientList[this.position].member[i], 'flag', false)
                  }
                } else {
                  for (let i = 0, len = this.patientList[this.position].member.length; i < len; i++) {
                    this.$set(this.patientList[this.position].member[i], 'flag', true)
                  }
                }
              }
            }else {
              this.params.current++;
              this.getDoctorCustomPatient()
            }
          }
        })
      }
    },
  }
</script>

<style lang="less" scoped>

  .item_first {
    border-bottom: 1px solid #DDDDDD;
  }

  .item {
    display: flex;
    font-size: 16px;
    height: 50px;
    text-align: center;
    align-items: center;
    background-color: white;
  }

  .item_second li {
    display: flex;
    align-items: center;
    height: 50px;
    padding-left: 15px;
    background-color: white;
    border-top: 1px solid #DDDDDD;
  }

  .item_second label {
    flex: 1;
    align-items: center;
  }

  .checkGroup {
    padding-left: 15px;
    padding-right: 15px;
    display: flex;
    align-items: center;
  }

  .allContent {
    width: 100vw;
    height: 100vh;
    background: #fafafa;
  }

</style>
