<template>
  <div class="app-container">
    <div class="filter-container">
      <div class="leftContent">
        <span @click="selectBtn" :style="{borderBottomColor:selectEd?'#1890FF':'#fff',color:selectEd?'#1890FF':'rgba(0,0,0,0.65)'}">查看</span>
        <span @click="selectBtn" :style="{borderBottomColor:!selectEd?'#1890FF':'#fff',color:!selectEd?'#1890FF':'rgba(0,0,0,0.65)'}">数据统计</span>
      </div>
      <div>
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
        <el-button @click="add" class="filter-item" plain type="danger" v-has-permission="['doctor:add']">
          {{ $t("table.add") }}
        </el-button>
        <el-dropdown class="filter-item" trigger="click" v-has-any-permission="['doctor:delete', 'doctor:export',
          'doctor:import']">
          <el-button>
            {{ $t("table.more") }}<i class="el-icon-arrow-down el-icon--right" />
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="batchDelete" v-has-permission="['doctor:delete']">
              {{ $t("table.delete") }}
            </el-dropdown-item>
            <el-dropdown-item @click.native="exportExcel" v-has-permission="['doctor:export']">
              {{ $t("table.export") }}
            </el-dropdown-item>
            <el-dropdown-item @click.native="exportExcelPreview" v-has-permission="['doctor:export']">
              {{ $t("table.exportPreview") }}
            </el-dropdown-item>
            <el-dropdown-item @click.native="importExcel" v-has-permission="['doctor:import']">
              {{ $t("table.import") }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

    </div>

    <el-table :data="tableData.records" :key="tableKey" @cell-click="cellClick"
              @filter-change="filterChange" @selection-change="onSelectChange" @sort-change="sortChange"
              border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
      <el-table-column align="center" type="selection" width="40px" :reserve-selection="true"/>
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
      <el-table-column :label="$t('table.doctor.avatar')" :show-overflow-tooltip="true" align="center" prop="avatar"
                        width="">
        <template slot-scope="scope">
          <!-- <span>{{ scope.row.avatar }}</span> -->
          <el-avatar
            :key="scope.row.avatar"
            :src="(scope.row.avatar)"
            fit="fill"
            icon="el-icon-user-solid"
          ></el-avatar>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.name')" :show-overflow-tooltip="true" align="center" prop="name"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
       <el-table-column :label="$t('table.doctor.mobile')" :show-overflow-tooltip="true" align="center" prop="mobile"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.mobile }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.hospitalId')" :show-overflow-tooltip="true" align="center" prop="hospitalId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.hospitalName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.nursingName')" :show-overflow-tooltip="true" align="center" prop="nursingName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.nursingName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会员" :show-overflow-tooltip="true" align="center"
                        width="">
         <template slot-scope="scope">
           <span>{{ scope.row.totalPatientCount }}</span>
         </template>
      </el-table-column>
      <!-- <el-table-column :label="$t('table.doctor.hospitalName')" :show-overflow-tooltip="true" align="center" prop="hospitalName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.hospitalName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.hospitalName')" :show-overflow-tooltip="true" align="center" prop="hospitalName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.hospitalName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.groupId')" :show-overflow-tooltip="true" align="center" prop="groupId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.groupId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.remarks')" :show-overflow-tooltip="true" align="center" prop="remarks"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.remarks }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.groupName')" :show-overflow-tooltip="true" align="center" prop="groupName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.groupName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.nursingId')" :show-overflow-tooltip="true" align="center" prop="nursingId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.nursingId }}</span>
        </template>
      </el-table-column>

      <el-table-column :label="$t('table.doctor.organId')" :show-overflow-tooltip="true" align="center" prop="organId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.organId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.province')" :show-overflow-tooltip="true" align="center" prop="province"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.province }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.city')" :show-overflow-tooltip="true" align="center" prop="city"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.city }}</span>
        </template>
      </el-table-column>

      <el-table-column :label="$t('table.doctor.departmentId')" :show-overflow-tooltip="true" align="center" prop="departmentId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.departmentId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.title')" :show-overflow-tooltip="true" align="center" prop="title"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>


      <el-table-column :label="$t('table.doctor.nickName')" :show-overflow-tooltip="true" align="center" prop="nickName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.nickName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.idCard')" :show-overflow-tooltip="true" align="center" prop="idCard"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.idCard }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.idCardImgF')" :show-overflow-tooltip="true" align="center" prop="idCardImgF"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.idCardImgF }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.idCardImgB')" :show-overflow-tooltip="true" align="center" prop="idCardImgB"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.idCardImgB }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.wxAppId')" :show-overflow-tooltip="true" align="center" prop="wxAppId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.wxAppId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.openId')" :show-overflow-tooltip="true" align="center" prop="openId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.openId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.imAccount')" :show-overflow-tooltip="true" align="center" prop="imAccount"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.imAccount }}</span>
        </template>
      </el-table-column>

      <el-table-column :label="$t('table.doctor.qrCode')" :show-overflow-tooltip="true" align="center" prop="qrCode"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.qrCode }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.businessCardQrCode')" :show-overflow-tooltip="true" align="center" prop="businessCardQrCode"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.businessCardQrCode }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.deptartmentName')" :show-overflow-tooltip="true" align="center" prop="deptartmentName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.deptartmentName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.organCode')" :show-overflow-tooltip="true" align="center" prop="organCode"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.organCode }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.organName')" :show-overflow-tooltip="true" align="center" prop="organName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.organName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.birthday')" :show-overflow-tooltip="true" align="center" prop="birthday"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.birthday }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.lastLoginIp')" :show-overflow-tooltip="true" align="center" prop="lastLoginIp"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.lastLoginIp }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.totalLoginTimes')" :show-overflow-tooltip="true" align="center" prop="totalLoginTimes"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.totalLoginTimes }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.sex')" :show-overflow-tooltip="true" align="center" prop="sex"
                        width="">
        <template slot-scope="scope">
          <span v-if="scope.row.sex===0">男</span>
          <span v-else>女</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.lastLoginTime')" :show-overflow-tooltip="true" align="center" prop="lastLoginTime"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.lastLoginTime }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.extraInfo')" :show-overflow-tooltip="true" align="center" prop="extraInfo"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.extraInfo }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.doctor.loginName')" :show-overflow-tooltip="true" align="center" prop="loginName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.loginName }}</span>
        </template>
      </el-table-column>

       -->
      <el-table-column
        :label="$t('table.operation')" align="center" column-key="operation" class-name="small-padding fixed-width" width="100px">
        <template slot-scope="{ row }">
          <i @click="copy(row)" class="el-icon-copy-document table-operation" :title="$t('common.delete')"
             style="color: #2db7f5;" v-hasPermission="['doctor:add']"/>
          <i @click="edit(row)" class="el-icon-edit table-operation" :title="$t('common.delete')"
             style="color: #2db7f5;" v-hasPermission="['doctor:update']"/>
          <i @click="singleDelete(row)" class="el-icon-delete table-operation" :title="$t('common.delete')"
             style="color: #f50;" v-hasPermission="['doctor:delete']"/>
          <el-link class="no-perm" v-has-no-permission="['doctor:update', 'doctor:copy', 'doctor:delete']">
            {{ $t("tips.noPermission") }}
          </el-link>
        </template>
      </el-table-column>
    </el-table>
    <pagination :limit.sync="queryParams.size" :page.sync="queryParams.current"
      :total="Number(tableData.total)" @pagination="fetch" v-show="tableData.total > 0"/>
    <doctor-edit :dialog-visible="dialog.isVisible" :type="dialog.type"
            @close="editClose" @success="editSuccess" ref="edit"/>
    <doctor-import ref="import" :dialog-visible="fileImport.isVisible" :type="fileImport.type"
            :action="fileImport.action" accept=".xls,.xlsx" @close="importClose" @success="importSuccess" />
    <el-dialog :close-on-click-modal="false" :close-on-press-escape="true"
               title="预览" width="80%" top="50px" :visible.sync="preview.isVisible" v-el-drag-dialog>
      <el-scrollbar>
        <div v-html="preview.context"></div>
      </el-scrollbar>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from "@/components/Pagination";
import elDragDialog from '@/directive/el-drag-dialog'
import DoctorEdit from "./Edit";
import doctorApi from "@/api/Doctor.js";
import DoctorImport from "@/components/caring/Import"
import {convertEnum} from '@/utils/utils'
import {downloadFile, loadEnums, initDicts, initQueryParams} from '@/utils/commons'

export default {
  name: "DoctorManage",
  directives: { elDragDialog },
  components: { Pagination, DoctorEdit, DoctorImport},
  filters: {

  },
  data() {
    return {
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
        action: `${process.env.VUE_APP_BASE_API}/用户中心/doctor/import`
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
      selectEd:true
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
    loadEnums(enumList, this.enums, '用户中心');
    initDicts(dictList, this.dicts);
  },
  methods: {
    selectBtn(){
      this.selectEd = !this.selectEd
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
      doctorApi.preview(this.queryParams).then(response => {
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
      doctorApi.export(this.queryParams).then(response => {
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
      doctorApi.delete({ ids: ids }).then(response => {
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
      this.$refs.edit.setDoctor({ enums: this.enums, dicts: this.dicts});
    },
    copy(row) {
      this.$refs.edit.setDoctor({row, enums: this.enums, dicts: this.dicts});
      this.dialog.type = "copy";
      this.dialog.isVisible = true;
    },
    edit(row) {
      this.$refs.edit.setDoctor({row, enums: this.enums, dicts: this.dicts});
      this.dialog.type = "edit";
      this.dialog.isVisible = true;
    },
    fetch(params = {}) {
      this.loading = true;
      if (this.queryParams.timeRange) {
        this.queryParams.map.createTime_st = this.queryParams.timeRange[0];
        this.queryParams.map.createTime_ed = this.queryParams.timeRange[1];
      }

      this.queryParams.current = params.current ? params.current : this.queryParams.current;
      this.queryParams.size = params.size ? params.size : this.queryParams.size;

      doctorApi.page(this.queryParams).then(response => {
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
<style lang="scss" scoped>
.filter-container{
  display: flex;
  justify-content: space-between;
  .leftContent{
    line-height: 40px;
    color: rgba(0,0,0,0.65);
    span{
      padding: 10px 20px;
      font-size: 18px;
      border-bottom:3px solid #fff;
      cursor: pointer;
      // border-bottom: 50%;
      // border-bottom-radius: 50%;
    }
  }
}
</style>
