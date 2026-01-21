<template>
  <div class="app-container">
    <div class="filter-container">
      <el-radio-group v-model="doctorOwner" @change="fetch" style="margin-right:30px;margin-bottom: 10px;">
        <el-radio-button :label="false" border>患者cms管理</el-radio-button>
        <el-radio-button :label="true" border >医生cms管理</el-radio-button>
      </el-radio-group>
      <el-date-picker :range-separator="null" class="filter-item search-item date-range-item"
                      end-placeholder="结束日期" format="yyyy-MM-dd HH:mm:ss" start-placeholder="开始日期"
                      type="daterange" v-model="queryParams.timeRange" value-format="yyyy-MM-dd HH:mm:ss"
      />
      <el-button @click="search" class="filter-item" plain type="primary">
        {{ $t("table.search") }}
      </el-button>
      <el-button @click="reset" class="filter-item" plain type="warning">
        {{ $t("table.reset") }}
      </el-button>
      <el-button @click="add" class="filter-item" plain type="danger" v-has-permission="['channelContent:add']">
        {{ $t("table.add") }}
      </el-button>
      <el-button @click="Canlist" class="filter-item" plain type="primary">
        批量导入
      </el-button>
      <el-dropdown class="filter-item" trigger="click" v-has-any-permission="['channelContent:delete', 'channelContent:export',
        'channelContent:import']">
        <el-button>
          {{ $t("table.more") }}<i class="el-icon-arrow-down el-icon--right" />
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="batchDelete" v-has-permission="['channelContent:delete']">
            {{ $t("table.delete") }}
          </el-dropdown-item>
          <el-dropdown-item @click.native="exportExcel" v-has-permission="['channelContent:export']">
            {{ $t("table.export") }}
          </el-dropdown-item>
          <el-dropdown-item @click.native="exportExcelPreview" v-has-permission="['channelContent:export']">
            {{ $t("table.exportPreview") }}
          </el-dropdown-item>
          <el-dropdown-item @click.native="importExcel" v-has-permission="['channelContent:import']">
            {{ $t("table.import") }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <el-table :data="tableData.records" :key="tableKey" @cell-click="cellClick"
              @filter-change="filterChange" @selection-change="onSelectChange" @sort-change="sortChange"
              border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
      <el-table-column align="center" type="selection" width="40px" :reserve-selection="true"/>
      <!-- <el-table-column :label="$t('table.channelContent.channelId')" :show-overflow-tooltip="true" align="center" prop="channelId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.channelId }}</span>
        </template>
      </el-table-column> -->
      <el-table-column :label="$t('table.channelContent.icon')" :show-overflow-tooltip="true" align="center" prop="icon"
                        width="">
        <template slot-scope="scope">
          <!-- <span>{{ scope.row.icon }}</span> -->
          <!-- <el-avatar
            :key="scope.row.icon"
            :src="(scope.row.icon)"
            fit="fill"
          ></el-avatar> -->
          <img :src="scope.row.icon" alt="" srcset="" width="15%">
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.channelContent.title')" :show-overflow-tooltip="true" align="center" prop="title"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>
       <el-table-column :label="$t('table.channelContent.channelType')" :show-overflow-tooltip="true" align="center" prop="channelType"
                        width="">
        <template slot-scope="scope">
          <!-- <span>{{ scope.row.channelType }}</span> -->
           <span>{{ scope.row.channelType===typeArray[0].val?typeArray[0].name:(scope.row.channelType===typeArray[1].val?typeArray[1].name:'无') }}</span>
        </template>
      </el-table-column>
       <el-table-column
        label="所属栏目"
        align="center"
        prop="channelName"
        width="170px">
        <template slot-scope="scope">
          <span>{{ scope.row.channelName }}</span>
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
      
      <!-- <el-table-column :label="$t('table.channelContent.summary')" :show-overflow-tooltip="true" align="center" prop="summary"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.summary }}</span>
        </template>
      </el-table-column>  -->
      
      
<!--      <el-table-column :label="$t('table.channelContent.sourceId')" :show-overflow-tooltip="true" align="center" prop="sourceId"-->
<!--                        width="">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ scope.row.sourceId }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column :label="$t('table.channelContent.cSourceEntityClass')" :show-overflow-tooltip="true" align="center" prop="cSourceEntityClass"-->
<!--                        width="">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ scope.row.cSourceEntityClass }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <!-- <el-table-column :label="$t('table.channelContent.content')" :show-overflow-tooltip="true" align="center" prop="content"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.channelContent.link')" :show-overflow-tooltip="true" align="center" prop="link"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.link }}</span>
        </template>
      </el-table-column> -->
      <el-table-column  :label="$t('table.channelContent.sort')" :show-overflow-tooltip="true" align="center" prop="sort"
                        width="80">
        <template slot-scope="scope">
          <span>{{ scope.row.sort }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column :label="$t('table.channelContent.isTop')" :show-overflow-tooltip="true" align="center" prop="isTop"
                        width="">
        <template slot-scope="scope">
          <span v-if="scope.row.isTop===1">是</span>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.channelContent.hitCount')" :show-overflow-tooltip="true" align="center" prop="hitCount"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.hitCount }}</span>
        </template>
      </el-table-column> -->
      
      <!-- <el-table-column :label="$t('table.channelContent.canComment')" :show-overflow-tooltip="true" align="center" prop="canComment"
                        width="">
        <template slot-scope="scope">
          <span v-if="scope.row.canComment===1">允许</span>
          <span v-else>不允许</span>
        </template>
      </el-table-column> -->
       <el-table-column :label="$t('table.channelContent.hitCount')" :show-overflow-tooltip="true" align="center" prop="hitCount"
                        width="80">
        <template slot-scope="scope">
          <span>{{ scope.row.hitCount }}</span>
        </template>
      </el-table-column>
     
     
      <el-table-column :label="$t('table.channelContent.messageNum')" :show-overflow-tooltip="true" align="center" prop="messageNum"
                        width="80">
        <template slot-scope="scope">
          <span>{{ scope.row.messageNum?scope.row.messageNum:0 }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.operation')" align="center" column-key="operation" class-name="small-padding fixed-width" width="100px">
        <template slot-scope="{ row }">
          <i @click="copy(row)" class="el-icon-copy-document table-operation" :title="$t('common.delete')"
             style="color: #2db7f5;" v-hasPermission="['channelContent:add']"/>
          <i @click="edit(row)" class="el-icon-edit table-operation" :title="$t('common.delete')"
             style="color: #2db7f5;" v-hasPermission="['channelContent:update']"/>
          <i @click="singleDelete(row)" class="el-icon-delete table-operation" :title="$t('common.delete')"
             style="color: #f50;" v-hasPermission="['channelContent:delete']"/>
          <el-link class="no-perm" v-has-no-permission="['channelContent:update', 'channelContent:copy', 'channelContent:delete']">
            {{ $t("tips.noPermission") }}
          </el-link>
        </template>
      </el-table-column>
    </el-table>
    <pagination :limit.sync="queryParams.size" :page.sync="queryParams.current"
      :total="Number(tableData.total)" @pagination="fetch" v-show="tableData.total > 0"/>
    <channelContent-edit v-if="dialog.isVisible" :dialog-visible="dialog.isVisible" :type="dialog.type"
            @close="editClose" @success="editSuccess" :doctorOwner="doctorOwner"  :channelContent="myData" :dicts="dicts" :enums="enums" @uploadBtnT="uploadBtn"/>
    <channelContent-import ref="import" :dialog-visible="fileImport.isVisible" :type="fileImport.type"
            :action="fileImport.action" accept=".xls,.xlsx" @close="importClose" @success="importSuccess" />
    <el-dialog :close-on-click-modal="false" :close-on-press-escape="true"
               title="预览" width="80%" top="50px" :visible.sync="preview.isVisible" >
      <el-scrollbar>
        <div v-html="preview.context"></div>
      </el-scrollbar>
    </el-dialog>

    <el-dialog :close-on-click-modal="false" :close-on-press-escape="true"
                width="1000px" top="50px" :visible.sync="showlist" >
      <div slot="title" style="display:flex;justify-content: flex-start;">
        <p style="width:30%">批量导入</p>
      </div>
      <div style="display:flex;justify-content: flex-start;">
        <div style="width:70%">
          <downSelect :header-array="headerArray" @selectBtn="selectBtn" />
        </div>
        <div style="width:30%;margin-top:10px;text-align:right">
          <el-input  style="width:50%;margin-right:10px;" v-model="serachVal" size="mini" placeholder="请输入内容"></el-input>
          <el-button @click="searchBtn"  size="mini" plain type="primary">
            {{ $t("table.search") }}
          </el-button>
        </div>
      </div>
      <el-table
        ref="multipleTable"
        :data="selectTableData"
        tooltip-effect="dark"
        style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <el-table-column
          label="标题">
          <template slot-scope="scope">
            <img :src="scope.row.icon" alt="" style="width:40px;vertical-align: middle;">
            {{ scope.row.title }}
          </template>
        </el-table-column>
        <el-table-column
          prop="channelName"
          label="分类"
          width="120">
        </el-table-column>
      </el-table>
      <pagination :limit.sync="queryParamsT.size" :page.sync="queryParamsT.current"
      :total="Number(queryParamsT.total)" @pagination="Canlist" v-show="queryParamsT.total > 0"/>
      <div style="text-align:center">
        <el-button style="margin-right:50px" type="warning" plain @click="selectTableData=[];showlist=false">取消</el-button>
        <el-button  type="primary" plain @click="sureSelectBtn">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog :close-on-click-modal="false" :close-on-press-escape="true"
               title="选择分类" width="30%" top="50px" :visible.sync="selectType" >
      <el-select v-model="selectTypeVal" placeholder="请选择" style="margin:0 auto">
        <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.channelName"
          :value="item.id">
        </el-option>
      </el-select>
      <div style="text-align:center;margin-top:20px">
        <el-button style="margin-right:50px" type="warning" plain @click="selectTypeVal='';selectType=false">取消</el-button>
        <el-button  type="primary" plain @click="surechannel">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import Pagination from "@/components/Pagination";
import elDragDialog from '@/directive/el-drag-dialog'
import ChannelContentEdit from "./Edit";
import channelContentApi from "@/api/ChannelContent.js";
import ChannelContentImport from "@/components/caring/Import"
// import {convertEnum} from '@/utils/utils'
import downSelect from './downSelect'
import {downloadFile, loadEnums, initDicts, initQueryParams} from '@/utils/commons'
import db from '@/utils/localstorage'
import { Base64 } from 'js-base64'

export default {
  name: "ChannelContentManage",
  directives: { elDragDialog },
  components: { Pagination, downSelect, ChannelContentEdit, ChannelContentImport},
  filters: {

  },
  data() {
    return {
      showlist:false,
      // 编辑
      dialog: {
          isVisible: false,
          type: "add"
      },
      // 预览
      preview: {
        isVisible: false,
        context: ''
      },
      // 导入
      fileImport: {
        isVisible: false,
        type: "import",
        action: `${process.env.VUE_APP_BASE_API}/cms/channelContent/import`
      },
      tableKey: 0,
      queryParams: initQueryParams(),
      selection: [],
      loading: false,
      tableData: {
          total: 0
      },
      // 枚举
      enums: {
      },
      // 字典
      dicts: {
      },
      myData:{
        ownerType: 'TENANT',
        icon:''
      },
      typeArray:[{name:'文章',val:'Article'},{name:'轮播图',val:'Banner'}],
      doctorOwner:false,
      inputVal:'',
      typeId:'',
      queryParamsT: {
        size: 10,
        current: 1,
        total: 0
      },
      selectTableData:[],
      selectType:false,
      selectTypeVal:'',
      options:[],
      contentId:[],
      serachVal:'',
      headerArray:[]
    };
  },
  computed: {
  },
  watch: {
  },
  mounted() {
    this.fetch();

    // 初始化字典和枚举
    const enumList = [];
    const dictList = [];
    loadEnums(enumList, this.enums, 'cms');
    initDicts(dictList, this.dicts);
  },
  methods: {
    selectBtn(z){
      console.log(z)
      this.typeId = z.id
      this.Canlist()
    },
    getheaderData() {
      const params = {
        'current': 1,
        'size': 1000,
        'sort': 'sort',
        'model': {}
      }
      // channelContentApi.pageChannel(params).then((res) => {
      //   if (res.status === 200) {
      //     // console.log(res.data.data.records)
      //     this.headerArray = res.data.data.records
      //   }
      // })
      const clientId = process.env.VUE_APP_CLIENT_ID
      const clientSecret = process.env.VUE_APP_CLIENT_SECRET
      axios.post('http://192.168.31.32:8760/api/tenant/cmsConfig/channel/page',params,{headers: {
          'token': 'Bearer ' + db.get('TOKEN', ''),
          'Authorization':`Basic ${Base64.encode(`${clientId}:${clientSecret}`)}`,
          'tenant':null
        }
      }).then(res=>{
        if (res.status === 200) {
          // console.log(res.data.data.records)
          this.headerArray = res.data.data.records
        }
      })
    },
    searchBtn(){
      this.Canlist()
    },
    handleSelectionChange(val){
      // console.log(val)
      const Idarray = []
      val.forEach((res)=>{
        Idarray.push(res.id)
      })
      this.contentId = Idarray
    },
    sureSelectBtn(){
      channelContentApi.channelpage({doctorOwner:this.doctorOwner}).then(response => {
        const res = response.data;
        if (res.isSuccess) {
          this.options = res.data
          this.selectType=true
        }
      })
    },
    surechannel(){
      if(!this.selectTypeVal){
        this.$message({
          message: '请选择栏目',
          type: "error"
        });
      }
      if(this.contentId.length<1){
        this.$message({
          message: '请选择文章',
          type: "error"
        });
      }
       const params = {
        'allCopy': false,
        'libContentIds':this.contentId,
        'targetChannelId': this.selectTypeVal,
        'doctorOwner':this.doctorOwner
        // 'oldChannelId': this.contentId
      }
      
      channelContentApi.copyContent(params).then(response => {
        const res = response.data;
        if (res.isSuccess) {
          // console.log(res)
          this.selectType = false
          this.contentId=[]
          this.selectTypeVal=''
          this.showlist = false
          this.fetch();
        }
      })
    },
    Canlist(){
      this.showlist = true
      // this.loading = true
      // let param = new URLSearchParams()
      // const token = db.get('TOKEN', '')
      const clientId = process.env.VUE_APP_CLIENT_ID
      const clientSecret = process.env.VUE_APP_CLIENT_SECRET
      this.getheaderData()
      // if (token) {
      //   param.append('token', 'Bearer ' + db.get('TOKEN', ''))
      // }
      // param.append('Authorization', `Basic ${Base64.encode(`${clientId}:${clientSecret}`)}`)
   
      // axios({
      //     method: 'post',
      //     url: 'http://api-test.caringsaas.cn/api/tenant/cmsConfig/channel/page',
      //     data: param
      // })

      const params = {
        'current': this.queryParamsT.current,
        'map': {},
        'model': {
          'channelId': this.typeId,
          'title':this.serachVal
        },
        'order': 'descending',
        'size': this.queryParamsT.size,
        'sort': 'updateTime'
      }
      // console.log(axios.post)
      axios.post('http://192.168.31.32:8760/api/tenant/cmsConfig/content/page',params,{headers: {
          'token': 'Bearer ' + db.get('TOKEN', ''),
          'Authorization':`Basic ${Base64.encode(`${clientId}:${clientSecret}`)}`,
          'tenant':null
        }
      }).then(res=>{
          if (res.status === 200) {
            this.selectTableData = res.data.data.records
            this.queryParamsT.size = res.data.data.size
            this.queryParamsT.current = res.data.data.current
            this.queryParamsT.total = res.data.data.total
          }
      })

      // axios.get('http://api-test.caringsaas.cn/api/tenant/cmsConfig/channel/page').then(res => {

      // })
      
      // channelContentApi.pageContent(params).then((res) => {
      //   // this.loading = false
      //   if (res.status === 200) {
      //     this.selectTableData = res.data.data.records
      //     this.queryParamsT.size = res.data.data.size
      //     this.queryParamsT.current = res.data.data.current
      //     this.queryParamsT.total = res.data.data.total
      //   }
      // })
    
    },
    getallList(){

    },
    uploadBtn(i){
      this.$set(this.myData,'icon',i)
    },
    editClose() {
      this.dialog.isVisible = false;
    },
    editSuccess() {
      this.search();
    },
    onSelectChange(selection) {
      this.selection = selection;
    },
    search() {
      this.fetch({
          ...this.queryParams
      });
    },
    reset() {
      this.queryParams = initQueryParams();
      this.$refs.table.clearSort();
      this.$refs.table.clearFilter();
      this.search();
    },
    exportExcelPreview() {
      if (this.queryParams.timeRange) {
        this.queryParams.map.createTime_st = this.queryParams.timeRange[0];
        this.queryParams.map.createTime_ed = this.queryParams.timeRange[1];
      }
      this.queryParams.map.fileName = '导出数据';
      channelContentApi.preview(this.queryParams).then(response => {
        const res = response.data;
        this.preview.isVisible = true;
        this.preview.context = res.data;
      });
    },
    exportExcel() {
      if (this.queryParams.timeRange) {
        this.queryParams.map.createTime_st = this.queryParams.timeRange[0];
        this.queryParams.map.createTime_ed = this.queryParams.timeRange[1];
      }
      this.queryParams.map.fileName = '导出数据';
      channelContentApi.export(this.queryParams).then(response => {
        downloadFile(response);
      });
    },
    importExcel() {
      this.fileImport.type = "upload";
      this.fileImport.isVisible = true;
      this.$refs.import.setModel(false);
    },
    importSuccess() {
      this.search();
    },
    importClose() {
      this.fileImport.isVisible = false;
    },
    singleDelete(row) {
      this.$refs.table.clearSelection()
      this.$refs.table.toggleRowSelection(row, true);
      this.batchDelete();
    },
    batchDelete() {
      if (!this.selection.length) {
        this.$message({
            message: this.$t("tips.noDataSelected"),
            type: "warning"
        });
        return;
      }
      this.$confirm(this.$t("tips.confirmDelete"), this.$t("common.tips"), {
          confirmButtonText: this.$t("common.confirm"),
          cancelButtonText: this.$t("common.cancel"),
          type: "warning"
      })
      .then(() => {
        const ids = this.selection.map(u => u.id);
        this.delete(ids);
      })
      .catch(() => {
        this.clearSelections();
      });
    },
    clearSelections() {
      this.$refs.table.clearSelection();
    },
    delete(ids) {
      channelContentApi.delete({ ids: ids }).then(response => {
        const res = response.data;
        if (res.isSuccess) {
          this.$message({
              message: this.$t("tips.deleteSuccess"),
              type: "success"
          });
          this.search();
        }
      });
    },
    add() {
      this.dialog.type = "add";
      this.dialog.isVisible = true;
      this.myData = {"ownerType": "TENANT"}
      // this.$refs.edit.setChannelContent({ enums: this.enums, dicts: this.dicts});
    },
    copy(row) {
      this.myData = row
      // this.$refs.edit.setChannelContent({row, enums: this.enums, dicts: this.dicts});
      this.dialog.type = "copy";
      this.dialog.isVisible = true;
    },
    edit(k) {
      const that = this
       channelContentApi.getdetail({ id: k.id }).then(response => {
        const res = response.data;
        if (res.isSuccess) {
          this.myData = res.data
          that.dialog.type = "edit";
          that.dialog.isVisible = true;
          
          // that.$refs.edit.setChannelContent({row:res.data, enums: this.enums, dicts: this.dicts});
          
        }
      });
      
    },
    fetch(params = {}) {
      this.loading = true;
      if (this.queryParams.timeRange) {
        this.queryParams.map.createTime_st = this.queryParams.timeRange[0];
        this.queryParams.map.createTime_ed = this.queryParams.timeRange[1];
      }
      this.queryParams.model.ownerType = 'TENANT';
      this.queryParams.current = params.current ? params.current : this.queryParams.current;
      this.queryParams.size = params.size ? params.size : this.queryParams.size;
      this.queryParams.model.doctorOwner = this.doctorOwner
      channelContentApi.page(this.queryParams).then(response => {
        const res = response.data;
        if (res.isSuccess) {
          this.tableData = res.data;
        }
      }).finally(() => this.loading = false);
    },
    sortChange(val) {
      this.queryParams.sort = val.prop;
      this.queryParams.order = val.order;
      if (this.queryParams.sort) {
        this.search();
      }
    },
    filterChange (filters) {
      for (const key in filters) {
        if(key.includes('.')) {
          const val = { };
          val[key.split('.')[1]] = filters[key][0];
          this.queryParams.model[key.split('.')[0]] = val;
        } else {
          this.queryParams.model[key] = filters[key][0]
        }
      }
      this.search()
    },
    cellClick (row, column) {
      if (column['columnKey'] === "operation") {
        return;
      }
      let flag = false;
      this.selection.forEach((item)=>{
        if(item.id === row.id) {
          flag = true;
          this.$refs.table.toggleRowSelection(row);
        }
      })

      if(!flag){
        this.$refs.table.toggleRowSelection(row, true);
      }
    },
  }
};
</script>
<style lang="scss" scoped></style>
