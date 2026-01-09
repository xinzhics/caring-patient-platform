<template>
  <div class="app-container">
    <div class="filter-container">
        <div class="topheader">
            <img :src="baseData.avatar" alt="" width="60px" height="60px">
            <div class="inner">
                <p class="name">{{baseData.name}}</p>
                <p class="other">
                    所属医生：{{baseData.doctorName}}   丨    所属专员：{{baseData.serviceAdvisorName}}
                </p>
            </div>
        </div>
    </div>
    <div class="content">
        <el-tabs v-model="activeName" @tab-click="handleClick" stretch>
            <el-tab-pane label="基本信息" name="first"></el-tab-pane>
            <el-tab-pane label="健康档案" name="second"></el-tab-pane>
            <el-tab-pane label="检验数据" name="third"></el-tab-pane>
            <el-tab-pane label="监测数据" name="fourth"></el-tab-pane>
            <el-tab-pane label="健康日志" name="fifth"></el-tab-pane>
            <el-tab-pane label="用药信息" name="sixth"></el-tab-pane>
            <el-tab-pane label="智能提醒" name="seventh"></el-tab-pane>
        </el-tabs>
        <div v-if="activeName==='first'" class="contentInner">
            <div class="detail" :key="activeName+'1'">
                <p>姓名：{{baseData.name}}</p>
                <p>性别：{{baseData.sex===1?'女':'男'}}</p>
                <p>出生年月：{{baseData.birthday}}</p>
                <p>联系方式：{{baseData.mobile}}</p>
            </div>
        </div>
        <div v-if="activeName==='second'" class="contentInner">
            <div class="detail" v-for="(i,k) in healthData" :key="k" >
                <p  v-for="(j,y) in i" :key="y+'1'" style="width:50%">{{j.label}}：<span v-for=" (z,f) in j.values" :key="f+'2'">{{z.valueText}}{{f>=1?'、':''}}</span></p>
            </div>
        </div>
        <div v-if="activeName==='third'" class="contentInner">
             <el-table :data="testNumberData" :key="activeName+'3'"
              border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
                <el-table-column label="序号" align="center" type="index" width="60px" :reserve-selection="true"/>
                <el-table-column label="类型" :show-overflow-tooltip="true" align="center"  width="">
                    <!-- <template slot-scope="scope"> -->
                        <span>检验数据</span>
                    <!-- </template> -->
                </el-table-column>
                <el-table-column label="时间" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                         <span>{{ scope.row.createTime }}</span>
                    </template>
                </el-table-column>
                <el-table-column type="expand" label="操作" width="60px">
                    <template slot-scope="props">
                        <div class="detail" v-for="(i,k) in props.row.list" :key="k">
                            <p  v-for="(j,y) in i" :key="y+'11'" style="vertical-align: middle;width:50%">{{j.label==='下一页'?'':j.label+'：'}}
                                <span v-for=" (z,f) in j.values" :key="f+'22'">
                                    <img v-if="j.widgetType==='MultiImageUpload'" :src="z.attrValue" alt="" width="60px" height="60px" style="vertical-align: middle;">
                                    <span v-if="j.widgetType!=='MultiImageUpload'">
                                        {{z.valueText?z.valueText:z.attrValue}}{{f>=1?'、':''}}
                                    </span>
                                </span>
                            </p>
                        </div>
                    </template>
                </el-table-column>
             </el-table>
        </div>
        <div v-if="activeName==='fourth'" class="contentInner">
            <div class="detail" :key="activeName+'4'">
                <div class="myinner">
                    <p>血压监测</p>
                    <div v-for="(i,k) in pressureData" :key="k" class="item">
                        <span>{{ getymd(i.createTime) }}</span>
                        <span>{{ setVal(i.jsonContent)}}</span>
                    </div>
                </div>
                
                <div class="myinner">
                    <p>血糖监测</p>
                    <div v-for="(i,k) in sugarData" :key="k+'sugar'" class="itemT">
                        <p>{{ changeDate(i.createDate) }}</p>
                        <div class="docs" v-for="(z,y) in i.sugars" :key="y+'item'">
                            <div>
                                <span>{{ changeTeps(z.type) }}</span>
                            </div>
                            <span>{{ z.sugarValue }}mmol/L</span>
                        </div>
                        <!-- <span>{{ setVal(i.jsonContent)}}</span> -->
                    </div>
                </div>
                <!-- <p>姓名：{{baseData.name}}</p>
                <p>性别：{{baseData.sex===1?'女':'男'}}</p>
                <p>出生年月：{{baseData.birthday}}</p>
                <p>联系方式：{{baseData.mobile}}</p> -->
            </div>
        </div>
        <div v-if="activeName==='fifth'" class="contentInner">
           <el-table :data="healthCalendarData" :key="activeName+'5'"
              border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
                <el-table-column label="序号" align="center" type="index" width="60px" :reserve-selection="true"/>
                <el-table-column label="类型" :show-overflow-tooltip="true" align="center"  width="">
                    <!-- <template slot-scope="scope"> -->
                        <span>健康日志</span>
                    <!-- </template> -->
                </el-table-column>
                <el-table-column label="时间" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                         <span>{{ scope.row.createTime }}</span>
                    </template>
                </el-table-column>
                <el-table-column type="expand" label="操作" width="60px">
                    <template slot-scope="props">
                        <div class="detail" v-for="(i,k) in props.row.list" :key="k">
                            <p  v-for="(j,y) in i" :key="y+'11'" style="vertical-align: middle;width:50%">{{j.label==='下一页'?'':j.label+'：'}}
                                <span v-for=" (z,f) in j.values" :key="f+'22'">
                                    <img v-if="j.widgetType==='MultiImageUpload'" :src="z.attrValue" alt="" width="60px" height="60px" style="vertical-align: middle;">
                                    <span v-if="j.widgetType!=='MultiImageUpload'">
                                        {{z.valueText?z.valueText:z.attrValue}}{{f>=1?'、':''}}
                                    </span>
                                </span>
                            </p>
                        </div>
                    </template>
                </el-table-column>
             </el-table>
        </div>
        <div v-if="activeName==='sixth'" class="contentInner">
            <p style="color:#1890FF;font-weight:600">正在使用：</p>
            <el-table  :data="medicineData.medicines" :key="activeName+'6'" border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
                <el-table-column label="药品名称" :show-overflow-tooltip="true" align="center"  width="200px">
                    <template slot-scope="scope">
                        <span><img :src="scope.row.medicineIcon" alt="" width="60px" height="60px" style="vertical-align: middle;margin-right:10px">{{scope.row.medicineName}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.createTime}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="每日用药次数" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.numberOfDay}}次</span>
                    </template>
                </el-table-column>
                <el-table-column label="用药时间" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.drugsTime}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="每次剂量" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.dose}}/{{scope.row.unit}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="用药周期 " :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.cycle===0?'长期':scope.row.cycleDay+'天'}}</span>
                    </template>
                </el-table-column>
            </el-table>
            <p style="color:#1890FF;font-weight:600">历史用药：</p>
            <el-table :key="activeName+'61'"  :data="medicineData.historyMedicines" border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
                <el-table-column label="药品名称" :show-overflow-tooltip="true" align="center"  width="200px">
                    <template slot-scope="scope">
                        <span><img :src="scope.row.medicineIcon" alt="" width="60px" height="60px" style="vertical-align: middle;margin-right:10px">{{scope.row.medicineName}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.createTime}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="每日用药次数" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.numberOfDay}}次</span>
                    </template>
                </el-table-column>
                <el-table-column label="用药时间" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.drugsTime}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="每次剂量" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.dose}}/{{scope.row.unit}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="用药周期 " :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.cycle===0?'长期':scope.row.cycleDay+'天'}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="停用时间" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.updateTime}}</span>
                    </template>
                </el-table-column>
            </el-table>

        </div>
        <div v-if="activeName==='seventh'" class="contentInner">
             <p style="color:#1890FF;font-weight:600">正在使用：</p>
            <el-table :key="activeName+'7'":data="remindData" border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
                <el-table-column label="名称" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span>{{scope.row.nursingPlanName}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="是否启用" :show-overflow-tooltip="true" align="center"  width="">
                    <template slot-scope="scope">
                        <span style="color:#1890FF;">{{scope.row.isSubscribe===1?'已启用':'未启用'}}</span>
                    </template>
                </el-table-column>
               
            </el-table>
        </div>
    </div>
    
    <!-- <el-table :data="tableData.records" :key="tableKey" @cell-click="cellClick"
              @filter-change="filterChange" @selection-change="onSelectChange" @sort-change="sortChange"
              border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
      <el-table-column align="center" type="selection" width="40px" :reserve-selection="true"/>
      <el-table-column :label="$t('table.channel.channelName')" :show-overflow-tooltip="true" align="center" prop="channelName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.channelName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.channel.sort')" :show-overflow-tooltip="true" align="center" prop="sort"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.sort }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.channel.channelType')" :show-overflow-tooltip="true" align="center" prop="channelType"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.channelType }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.createTime')"
        align="center"
        prop="createTime"
        sortable="custom"
        width="170px">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.operation')" align="center" column-key="operation" class-name="small-padding fixed-width" width="100px">
        <template slot-scope="{ row }">
          <i @click="copy(row)" class="el-icon-copy-document table-operation" :title="$t('common.delete')"
             style="color: #2db7f5;" v-hasPermission="['channel:add']"/>
          <i @click="edit(row)" class="el-icon-edit table-operation" :title="$t('common.delete')"
             style="color: #2db7f5;" v-hasPermission="['channel:update']"/>
          <i @click="singleDelete(row)" class="el-icon-delete table-operation" :title="$t('common.delete')"
             style="color: #f50;" v-hasPermission="['channel:delete']"/>
          <el-link class="no-perm" v-has-no-permission="['channel:update', 'channel:copy', 'channel:delete']">
            {{ $t("tips.noPermission") }}
          </el-link>
        </template>
      </el-table-column>
    </el-table>
    <pagination :limit.sync="queryParams.size" :page.sync="queryParams.current"
      :total="Number(tableData.total)" @pagination="fetch" v-show="tableData.total > 0"/>
    <channel-edit :dialog-visible="dialog.isVisible" :type="dialog.type"
            @close="editClose" @success="editSuccess" ref="edit"/>
    <channel-import ref="import" :dialog-visible="fileImport.isVisible" :type="fileImport.type"
            :action="fileImport.action" accept=".xls,.xlsx" @close="importClose" @success="importSuccess" />
    <el-dialog :close-on-click-modal="false" :close-on-press-escape="true"
               title="预览" width="80%" top="50px" :visible.sync="preview.isVisible" v-el-drag-dialog>
      <el-scrollbar>
        <div v-html="preview.context"></div>
      </el-scrollbar>
    </el-dialog> -->
  </div>
</template>

<script>
import Pagination from "@/components/Pagination";
import elDragDialog from '@/directive/el-drag-dialog'
import ChannelEdit from "./Edit";
import patientApi from "@/api/Patient.js";
import ChannelImport from "@/components/caring/Import"
import {convertEnum} from '@/utils/utils'
import {downloadFile, loadEnums, initDicts, initQueryParams} from '@/utils/commons'

export default {
  name: "ChannelManage",
  directives: { elDragDialog },
  components: { Pagination, ChannelEdit, ChannelImport},
  filters: {

  },
  data() {
    return {
      activeName:'first',
      sugarTypes: [
        {name: '凌晨', value: 0},
        {name: '空腹', value: 1},
        {name: '早餐后', value: 2},
        {name: '午餐前', value: 3},
        {name: '午餐后', value: 4},
        {name: '晚餐前', value: 5},
        {name: '晚餐后', value: 6},
        {name: '睡前', value: 7}
      ],
      id:'',
      baseData:{},
      healthData:[],
      testNumberData:[],
      loading:true,
      monitorData:[],
      healthCalendarData:[],
      medicineData:{},
      remindData:[],
      pressureData:[],
      sugarData:[]
    };
  },
  computed: {
  },
  watch: {
  },
  mounted() {
    const that = this
    if(that.$route.query&&that.$route.query.id){
        that.id = that.$route.query.id
        that.getBaseInfo()
    }
  },
  methods: {
    
    handleClick() {
        if(this.activeName==='first'){
            this.getBaseInfo()
        }else if(this.activeName==='second'){
            this.healthInfo()
        }else if(this.activeName==='third'){
            this.testNumberInfo()
        }else if(this.activeName==='fourth'){
            this.monitorInfo()
        }else if(this.activeName==='fifth'){
            this.healthCalendarInfo()
        }else if(this.activeName==='sixth'){
            this.medicineInfo()
        }else if(this.activeName==='seventh'){
            this.remindInfo()
        }
    },
    getBaseInfo(){
        patientApi.getContent({id:this.id}).then(response => {
            const res = response.data;
            if (res.isSuccess) {
                this.baseData = res.data;
            }
        });
    },
    healthInfo(){
        patientApi.gethealthform({formEnum:'HEALTH_RECORD',patientId:this.id}).then(response => {
            const res = response.data;
            if (res.isSuccess) {
                var result = [];
                var data = res.data.fieldList;
                var chunk = 2; //每3个分一组
                for (var i = 0, j = data.length; i < j; i += chunk) {
                    result.push(data.slice(i, i + chunk));
                }
                this.healthData = result;

            }
        });
    },
    testNumberInfo(){
        this.loading = true
         patientApi.getCheckData({type:3,patientId:this.id}).then(response => {
            const res = response.data;
            if (res.isSuccess) {
                this.loading = false
                res.data.records.forEach(element => {
                    let thisArray = JSON.parse(element.jsonContent)
                    var result = [];
                    var data = thisArray;
                    var chunk = 2; //每3个分一组
                    data.forEach((el,z) => {
                        if(!el.label){
                            data.splice(z,1)
                        }
                    });
                    for (var i = 0, j = data.length; i < j; i += chunk) {
                        result.push(data.slice(i, i + chunk));
                    }
                    element.list = result

                });
                this.testNumberData = res.data.records
            }
        });

    },
    monitorInfo(){
        this.loading = true
        const params = {
            planType: 1
        }
        patientApi.getPlanByType(params).then((res) => {
            if (res.data.code === 0) {
            const params = {
                businessId: res.data.data.id,
                userId: this.id
            }
            patientApi.getformResultquery(params).then((res) => {
                if (res.data.code === 0) {
                // this.baseData = res.data.data
                this.pressureData =res.data.data
                // console.log(res.data.data)
                //   that.fields = JSON.parse(res.data.data[0].fieldsJson)
                }
            })
            }
        })
        const paramsT = {
            patientId: this.id
        }
        patientApi.findSugarByTime(paramsT).then((res) => {
            if (res.data.code === 0) {
                this.sugarData = res.data.data
            }
        })
    },
    getymd(dateStr) {
      var d = new Date(dateStr);
      // console.log(d.getMonth() + '555')
      var resDate = (d.getMonth() + 1) + '月' + d.getDate() + '日';
      return resDate;
    },
    setVal(jsonData) {
      const myData = JSON.parse(jsonData)
      let resData = ''
      myData.forEach(el => {
        // console.log(el.values[0].attrValue||'0')
        // el.values = eval("(" + el.values + ")")
        // resData = resData + el.values[0].attrValue
        if (typeof el.values !== 'object') {
          el.values = eval("(" + el.values + ")")
        }
        
        if (el.label === '高压') {
          const thisData = el.values[0].attrValue===undefined?'0':el.values[0].attrValue
          resData = resData + thisData
        }
        if (el.label === '低压') {
          const thisData = el.values[0].attrValue===undefined?'0':el.values[0].attrValue
          resData = resData + '/' + thisData
        }
        if (el.label === '心率') {
          const thisData = el.values[0].attrValue===undefined?'0bpm':el.values[0].attrValue + 'bpm'
          resData = resData + 'mmHg   ' + thisData
        }
      });
      return resData;
    },
    changeTeps(type) {
      let thisAll = ''
      this.sugarTypes.forEach(el => {
        if (el.value === type) {
          thisAll = el.name
        }

      });
      return thisAll
    },
    changeDate(time) {
      var date = new Date(time.replace(/-/g, '/'));
      var month = date.getMonth();
      var day = date.getDate();
      month = month + 1
      month = month < 10 ? ('0' + month) : month // 判断日期是否大于10
      day = day < 10 ? ('0' + day) : day // 判断日期是否大于10
      return month + '月' + day + '日'
    },
    healthCalendarInfo(){
        this.loading = true
        patientApi.getCheckData({type:5,patientId:this.id}).then(response => {
            const res = response.data;
            if (res.isSuccess) {
                this.loading = false
                res.data.records.forEach(element => {
                    let thisArray = JSON.parse(element.jsonContent)
                    var result = [];
                    var data = thisArray;
                    var chunk = 2; //每3个分一组
                    data.forEach((el,z) => {
                        if(!el.label){
                            data.splice(z,1)
                        }
                    });
                    for (var i = 0, j = data.length; i < j; i += chunk) {
                        result.push(data.slice(i, i + chunk));
                    }
                    element.list = result

                });
                this.healthCalendarData = res.data.records
            }
        });
    },
    medicineInfo(){
        this.loading = true
         patientApi.patientDrugsListAndHistory({patientId:this.id}).then(response => {
             const res = response.data;
            if (res.isSuccess) {
                this.loading = false
                // console.log(res.data)
                this.medicineData = res.data
            }
         })
        
    },
    remindInfo(){
         this.loading = true
         patientApi.getMyNursingPlans({patientId:this.id}).then(response => {
             const res = response.data;
            if (res.isSuccess) {
                this.loading = false
                // console.log(res.data)
                this.remindData = res.data.subscribeList
            }
         })

    }
  }
};
</script>
<style lang="scss" scoped>
.app-container{
    background: #f8f8f8;
    padding: 0px;
    .filter-container{
        background: #fff;
        padding: 20px;
        .topheader{
            display: flex;
            justify-content: flex-start;
            img{
                margin-right: 20px;
            }
            .inner{
                p{
                    margin: 0px;
                    line-height: 30px;
                    color: rgba(0,0,0,0.65);
                }
                .name{
                    font-size: 18px;
                    font-weight: 600;
                }
            }
        }
    }
    .content{
        margin-top: 10px;
        background: #fff;
        padding: 20px;
        .contentInner{
            padding: 0px 20px;
            .detail{
                display: flex;
                justify-content: space-between;
                p{
                    margin-right: 40px;
                }
                .myinner{
                    width: 50%;
                    .item{
                        padding: 0px 20px;
                        display: flex;
                        justify-content: space-between;
                        font-size: 16px;
                        line-height: 30px;
                        
                    }
                    .itemT{
                        padding: 0px 20px;
                        font-size: 16px;
                        line-height: 30px;
                        .docs{
                            display: flex;
                            justify-content: space-between;
                        }
                    }
                    
                }
            }
        }
    }

}

</style>
