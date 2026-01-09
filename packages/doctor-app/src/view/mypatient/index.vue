<template>
  <section class="allContent">
    <x-header @on-click-back="gobackpage" style="margin: 0 !important;" :left-options="{backText: '',preventGoBack:true}">{{ title }}
      <img v-if="independence !== 1" slot="right" :src="require('@/assets/my/all_selector.png')" width="20px"
           height="20px" @click="isOverlay()">
    </x-header>
    <div
        style="margin-bottom: 100px;height: 99px;background: #3F86FF;padding-top: 14px;padding-left: 12px;padding-right: 13px">
      <div

          style="background: #fff;padding-top: 18px;padding-left: 25px;padding-right:26px;padding-bottom: 19px ;box-shadow: 0px 2px 4px rgba(0,0,0,0.0800);border-radius: 4px">
        <div  @click="goItem('所有'+patient, {})" style="display: flex;justify-content: space-between;border-bottom:1px dashed #D6D6D6">
          <div>
            <div style="font-size: 11px;color: #999999">全部</div>
            <div style="font-size: 31px;color: #4288FC;">{{ alldata.total }}</div>
          </div>
          <div style="line-height: 5" >
            <van-icon style="color: #B8B8B8 !important;" name="arrow"/>
          </div>
        </div>

        <div>
          <div
              style="display: flex;justify-content: space-around;color: #666666;font-size: 17px;align-content: center;padding-top: 20px">
            <div v-for="(i,k) in types" :key="k" style="display: flex">
              <div @click="goItems(i.status)">
                <div style="text-align: center">{{ i.total }}</div>
                <div style="font-size: 12px;color: #999999;">
                  {{
                    i.status == 1 ?  $getDictItem('register') : i.status == 0 ? $getDictItem('notregister') : '已取关'
                  }}
                </div>
              </div>
              <div style="position: relative">
                <van-icon style="position: absolute;bottom: 2px;font-size: 12px" name="arrow"/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="!show">
      <group :title="diagnostictype" v-if="alldata.diagnosis&&alldata.diagnosis.length>0">
        <cell v-for="(i,k) in alldata.diagnosis" :key="k+'z'" @click.native="goItem(i.name, i)" is-link>
          <div slot="title">
            <div v-if="i.name">
              <div
                  :style="{width:'20px',height:'20px',display:'inline-block',textAlign:'center',padding:'5px',borderRadius:'50%',color:'#fff',fontSize:'16px',lineHeight:'20px',background:colorJson[k%5]}">
                {{ i.name.substr(0, 1) }}
              </div>
              {{ i.name + '（' + i.count + '）' }}
            </div>
          </div>
        </cell>
      </group>
      <group title="标签" v-if="tagList.length>0">
        <cell v-for="(i,k) in tagList" :key="i.id" @click.native="goTagPatient(i.name, i.id)" is-link>
          <div slot="title">
            <div v-if="i.name">
              <div
                :style="{width:'20px',height:'20px',display:'inline-block',textAlign:'center',padding:'5px',borderRadius:'50%',color:'#fff',fontSize:'16px',lineHeight:'20px',background:colorJson[k%5]}">
                {{ i.name.substr(0, 1) }}
              </div>
              {{ i.name + '（' + i.countPatient + '）' }}
            </div>
          </div>
        </cell>
      </group>
      <group title="自定义分组" >
        <cell is-link @click.native="toUpdataTeam(i.id,i.groupName)" v-for="(i,index) in doctorCustomGroupList" :key="index">
          <div slot="title" >
            <div>
              <div
                  :style="{width:'20px',height:'20px',display:'inline-block',textAlign:'center',padding:'5px',borderRadius:'50%'
                ,color:'#fff',fontSize:'16px',lineHeight:'20px',background:'#FFAD6C'}">
                {{ i.groupName.substr(0, 1) }}
              </div>
              <!-- <span :style="{'padding':'8px 10px','borderRadius':'50%','fontSize':'16px','color':'#fff','background':colorJson[k%3]}"></span> -->
              {{ i.groupName }}{{'('+i.groupNumberTotal+')'}}
            </div>
          </div>
        </cell>
        <cell is-link @click.native="toAddTeam">
          <div slot="title" >
            <div>
              <div
                  :style="{width:'20px',height:'20px',display:'inline-block',textAlign:'center',padding:'5px',borderRadius:'50%',color:'#fff',fontSize:'16px',lineHeight:'20px',background:'#3F86FF'}">
                <van-icon name="plus" />
              </div>
              新建分组
            </div>
          </div>
        </cell>
      </group>
    </div>
    <van-overlay :show="show" style="margin-top: 50px">
      <div style="width: 100%; background: #FFF">
        <div v-for="(item, i) in doctorList" :key="i" @click="setDoctor(item)">
          <div style="padding-top: 10px; padding-bottom: 10px; padding-left: 15px; border-bottom: 1px solid #c8c8c8;">
            {{ item.name === '我' ? '我的' + patient : item.name + '的' + patient }}
          </div>
        </div>
      </div>
    </van-overlay>
  </section>
</template>
<script>
import Api from '@/api/Content.js'
import doctorApi from '@/api/doctor.js'
import tagApi from '@/api/tag.js'
import {
  Icon
} from 'vux'
import {
  Popup
} from 'vant';

export default {
  components: {
    [Popup.name]: Popup,
    Icon
  },
  data() {
    return {
      patientImg: require('@/assets/drawable-xhdpi/patient.png'),
      // fields:[],
      alldata: {
        total: 0,
        diagnosis: [],
        status: []
      },
      independence: 1, //1 为独立医生， 0 为非独立医生
      title: "我的" + this.$getDictItem('patient'),
      colorJson: [
        '#FFCA7A',
        '#8FB7F4',
        '#C2A5E2',
        '#7BD0DD',
        '#F59A99'
      ],
      show: false,
      doctorList: [{
        name: "全部医生",
        doctorId: 'all',
      }],
      types: [],
      tagList: [],
      params: {
        id: localStorage.getItem('caring_doctor_id'),
        dimension: "",
      },
      name: this.$getDictItem('doctor'),
      patient: this.$getDictItem('patient'),
      register: this.$getDictItem('register'),
      diagnostictype: this.$getDictItem('diagnostictype'),
      doctorCustomGroupList:[]
    }
  },
  created() {
    this.getInfo()
    this.getContent()
    this.findTagList()
  },
  mounted() {
    this.doctorList[0].name = "全部" + this.name
    this.title = "我的" + this.patient
    this.getdoctorCustomGroup()
  },
  methods: {
    findTagList() {
      let params = {
        doctorId: this.params.id,
        dimension: this.params.dimension
      }
      tagApi.getTagListAndCountPatientNumber(params).then(res => {
        console.log('getTagListAndCountPatientNumber', res.data)
        this.tagList = res.data.data
      })
    },
    gobackpage(){
      this.$router.push({
        path: '/index',
      })
    },
    //获取已定义小组列表
    getdoctorCustomGroup(){
      Api.doctorCustomGroup(this.params.id).then(res=>{
        console.log("============>",res)
        if(res.data.code==0){
          this.doctorCustomGroupList=res.data.data
        }
      })
    },
    //跳转新增页面
    toAddTeam(){
      this.$router.push({
        path: '/mypatient/addteam',
        query: {
          tempUuid: new Date().getTime()
        }
      })
    },
    toUpdataTeam(id,title){
      this.$router.push({
        path: '/mypatient/GroupIdPatientList',
        query:{
          id:id,
          title:title
        }
      })
    },
    goItems(item){
      this.$router.push({
        path: '/mypatient/list',
        query: {
          title: '',
          tagId: '',
          dimension: this.params.dimension,
          doctorId: this.params.id,
          status: item,
          diagnosisId: ''
        }
      })
    },
    setDoctor(item) {
      if ("all" === item.doctorId) {
        this.params.dimension = 'all'
        this.params.id = localStorage.getItem('caring_doctor_id')
      } else {
        this.params.dimension = ''
        this.params.id = item.doctorId
      }
      this.title = item.name === '我' ? '我的' + this.patient : item.name + '的' + this.patient
      this.isOverlay();
      this.getInfo();
      this.getdoctorCustomGroup()
      this.findTagList()
    },
    isOverlay() {
      this.show = !this.show;
    },
    getInfo() {
      Api.getPatientsByStatusGroupNew(this.params)
          .then((res) => {
            this.types=[]
            if (res.data.code === 0) {
              let a={},b={},c={}
              this.alldata = res.data.data
              this.alldata.status.forEach((item, index) => {
                if (item.status == 1) {
                  a = item
                }else if(item.status == 0){
                  b =item
                }else if(item.status == 2){
                  c=item
                }
                console.log(this.types)
              })
              this.types.push(a,b,c)
            }
          })
    },
    goTagPatient(tagName, tagId) {
      this.$router.push({
        path: '/mypatient/list',
        query: {
          title: tagName,
          tagId: tagId,
          dimension: this.params.dimension,
          doctorId: this.params.id,
          status: '',
          diagnosisId: ''
        }
      })
    },
    goItem(name, i) {
      let status = '';
      if (i) {
        if (i.isCompleteEnterGroup === 1) {
          status = 1;
        }
        if (i.isCompleteEnterGroup === 0) {
          status = 0;
        }
      }
      this.$router.push({
        path: '/mypatient/list',
        query: {
          title: name,
          tagId: '',
          dimension: this.params.dimension,
          doctorId: this.params.id,
          status: status,
          diagnosisId: i.id ? i.id : ''
        }
      })
    },
    getDoctorList() {
      Api.getGroupDoctor().then((res) => {
        if (res.data.code === 0) {
          res.data.data.forEach((item) => {
            this.doctorList.push(item)
          })
        }
      })
    },
    getContent() {
      const params = {
        id: localStorage.getItem('caring_doctor_id')
      }
      doctorApi.getContent(params)
          .then((res) => {
            if (res.data.code === 0) {
              this.independence = res.data.data.independence
              if (this.independence === 0) {
                this.getDoctorList();
              }
            }
          })
    }
  }
}

</script>


<style lang="less" scoped>
/deep/ .van-icon-arrow {
  color: #B8B8B8 !important;
}


.allContent {
  width: 100vw;
  height: 100vh;
  background: #fafafa;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1;
  width: 100%;
  height: 100%;
  margin-top: 50px;
  background-color: #66000000;
}

/deep/ .vux-header {
  height: 50px;
}

/deep/ .weui-cell {
  border-top: unset;
}

</style>
