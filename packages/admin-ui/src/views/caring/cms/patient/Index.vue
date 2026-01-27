<template>
  <div class="app-container">
    <div class="filter-container">
    
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
      <el-button @click="add" class="filter-item" plain type="danger" v-has-permission="['patient:add']">
        {{ $t("table.add") }}
      </el-button>
      <el-dropdown class="filter-item" trigger="click" v-has-any-permission="['patient:delete', 'patient:export',
        'patient:import']">
        <el-button>
          {{ $t("table.more") }}<i class="el-icon-arrow-down el-icon--right" />
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="batchDelete" v-has-permission="['patient:delete']">
            {{ $t("table.delete") }}
          </el-dropdown-item>
          <el-dropdown-item @click.native="exportExcel" v-has-permission="['patient:export']">
            {{ $t("table.export") }}
          </el-dropdown-item>
          <el-dropdown-item @click.native="exportExcelPreview" v-has-permission="['patient:export']">
            {{ $t("table.exportPreview") }}
          </el-dropdown-item>
          <el-dropdown-item @click.native="importExcel" v-has-permission="['patient:import']">
            {{ $t("table.import") }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <el-table :data="tableData.records" :key="tableKey" @cell-click="cellClick"
              @filter-change="filterChange" @selection-change="onSelectChange" @sort-change="sortChange"
              border fit row-key="id" ref="table" style="width: 100%;" v-loading="loading">
      <el-table-column align="center" type="selection" width="40px" :reserve-selection="true"/>
      <el-table-column :label="$t('table.patient.serviceAdvisorId')" :show-overflow-tooltip="true" align="center" prop="serviceAdvisorId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.serviceAdvisorId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.serviceAdvisorName')" :show-overflow-tooltip="true" align="center" prop="serviceAdvisorName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.serviceAdvisorName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.nickName')" :show-overflow-tooltip="true" align="center" prop="nickName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.nickName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.mobile')" :show-overflow-tooltip="true" align="center" prop="mobile"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.mobile }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.sex')" :show-overflow-tooltip="true" align="center" prop="sex"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.sex }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.name')" :show-overflow-tooltip="true" align="center" prop="name"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.birthday')" :show-overflow-tooltip="true" align="center" prop="birthday"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.birthday }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.groupId')" :show-overflow-tooltip="true" align="center" prop="groupId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.groupId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.groupName')" :show-overflow-tooltip="true" align="center" prop="groupName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.groupName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.doctorId')" :show-overflow-tooltip="true" align="center" prop="doctorId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.doctorId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.doctorName')" :show-overflow-tooltip="true" align="center" prop="doctorName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.doctorName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.status')" :show-overflow-tooltip="true" align="center" prop="status"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.diagnosisId')" :show-overflow-tooltip="true" align="center" prop="diagnosisId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.diagnosisId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.diagnosisName')" :show-overflow-tooltip="true" align="center" prop="diagnosisName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.diagnosisName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.wxAppId')" :show-overflow-tooltip="true" align="center" prop="wxAppId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.wxAppId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.openId')" :show-overflow-tooltip="true" align="center" prop="openId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.openId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.avatar')" :show-overflow-tooltip="true" align="center" prop="avatar"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.avatar }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.imAccount')" :show-overflow-tooltip="true" align="center" prop="imAccount"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.imAccount }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.remark')" :show-overflow-tooltip="true" align="center" prop="remark"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.remark }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.isCompleteEnterGroup')" :show-overflow-tooltip="true" align="center" prop="isCompleteEnterGroup"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.isCompleteEnterGroup }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.examineCount')" :show-overflow-tooltip="true" align="center" prop="examineCount"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.examineCount }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.ckd')" :show-overflow-tooltip="true" align="center" prop="ckd"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.ckd }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.nursingTime')" :show-overflow-tooltip="true" align="center" prop="nursingTime"
                        width="170">
        <template slot-scope="scope">
          <span>{{ scope.row.nursingTime }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.hospitalName')" :show-overflow-tooltip="true" align="center" prop="hospitalName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.hospitalName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.certificateNo')" :show-overflow-tooltip="true" align="center" prop="certificateNo"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.certificateNo }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.code')" :show-overflow-tooltip="true" align="center" prop="code"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.code }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.agreeAgreement')" :show-overflow-tooltip="true" align="center" prop="agreeAgreement"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.agreeAgreement }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.grandTotalCheck')" :show-overflow-tooltip="true" align="center" prop="grandTotalCheck"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.grandTotalCheck }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.successiveCheck')" :show-overflow-tooltip="true" align="center" prop="successiveCheck"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.successiveCheck }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.height')" :show-overflow-tooltip="true" align="center" prop="height"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.height }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.weight')" :show-overflow-tooltip="true" align="center" prop="weight"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.weight }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.organId')" :show-overflow-tooltip="true" align="center" prop="organId"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.organId }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.organCode')" :show-overflow-tooltip="true" align="center" prop="organCode"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.organCode }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.organName')" :show-overflow-tooltip="true" align="center" prop="organName"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.organName }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.lastLoginIp')" :show-overflow-tooltip="true" align="center" prop="lastLoginIp"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.lastLoginIp }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.lastLoginTime')" :show-overflow-tooltip="true" align="center" prop="lastLoginTime"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.lastLoginTime }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.totalLoginTimes')" :show-overflow-tooltip="true" align="center" prop="totalLoginTimes"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.totalLoginTimes }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.extraInfo')" :show-overflow-tooltip="true" align="center" prop="extraInfo"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.extraInfo }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.completeEnterGroupTime')" :show-overflow-tooltip="true" align="center" prop="completeEnterGroupTime"
                        width="170">
        <template slot-scope="scope">
          <span>{{ scope.row.completeEnterGroupTime }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('table.patient.classCode')" :show-overflow-tooltip="true" align="center" prop="classCode"
                        width="">
        <template slot-scope="scope">
          <span>{{ scope.row.classCode }}</span>
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
             style="color: #2db7f5;" v-hasPermission="['patient:add']"/>
          <i @click="edit(row)" class="el-icon-edit table-operation" :title="$t('common.delete')"
             style="color: #2db7f5;" v-hasPermission="['patient:update']"/>
          <i @click="singleDelete(row)" class="el-icon-delete table-operation" :title="$t('common.delete')"
             style="color: #f50;" v-hasPermission="['patient:delete']"/>
          <el-link class="no-perm" v-has-no-permission="['patient:update', 'patient:copy', 'patient:delete']">
            {{ $t("tips.noPermission") }}
          </el-link>
        </template>
      </el-table-column>
    </el-table>
    <pagination :limit.sync="queryParams.size" :page.sync="queryParams.current"
      :total="Number(tableData.total)" @pagination="fetch" v-show="tableData.total > 0"/>
    <patient-edit :dialog-visible="dialog.isVisible" :type="dialog.type"
            @close="editClose" @success="editSuccess" ref="edit"/>
    <patient-import ref="import" :dialog-visible="fileImport.isVisible" :type="fileImport.type"
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
import PatientEdit from "./Edit";
import patientApi from "@/api/Patient.js";
import PatientImport from "@/components/caring/Import"
import {convertEnum} from '@/utils/utils'
import {downloadFile, loadEnums, initDicts, initQueryParams} from '@/utils/commons'

export default {
  name: "PatientManage",
  directives: { elDragDialog },
  components: { Pagination, PatientEdit, PatientImport},
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
        action: `${process.env.VUE_APP_BASE_API}/ucenter/patient/import`
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
      }
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
    loadEnums(enumList, this.enums, 'ucenter');
    initDicts(dictList, this.dicts);
  },
  methods: {
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
      patientApi.preview(this.queryParams).then(response => {
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
      patientApi.export(this.queryParams).then(response => {
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
      patientApi.delete({ ids: ids }).then(response => {
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
      this.$refs.edit.setPatient({ enums: this.enums, dicts: this.dicts});
    },
    copy(row) {
      this.$refs.edit.setPatient({row, enums: this.enums, dicts: this.dicts});
      this.dialog.type = "copy";
      this.dialog.isVisible = true;
    },
    edit(row) {
      this.$refs.edit.setPatient({row, enums: this.enums, dicts: this.dicts});
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

      patientApi.page(this.queryParams).then(response => {
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
