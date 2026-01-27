<template>
  <div class="app-container">
    <div class="filter-container">
      <div class="leftContent">
        <span @click="selectBtn" :style="{borderBottomColor:selectEd?'#1890FF':'#fff',color:selectEd?'#1890FF':'rgba(0,0,0,0.65)'}">查看</span>
        <span @click="selectBtn" :style="{borderBottomColor:!selectEd?'#1890FF':'#fff',color:!selectEd?'#1890FF':'rgba(0,0,0,0.65)'}">数据统计</span>
      </div>
      <div>
        <el-date-picker
          v-model="queryParams.timeRange"
          :range-separator="null"
          class="filter-item search-item date-range-item"
          end-placeholder="结束日期"
          format="yyyy-MM-dd HH:mm:ss"
          start-placeholder="开始日期"
          type="daterange"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
        <el-button
          class="filter-item"
          plain
          type="primary"
          @click="search"
        >
          {{ $t("table.search") }}
        </el-button>
        <el-button
          class="filter-item"
          plain
          type="warning"
          @click="reset"
        >
          {{ $t("table.reset") }}
        </el-button>
        <el-button
          v-has-permission="['nursingStaff:add']"
          class="filter-item"
          plain
          type="danger"
          @click="add"
        >
          {{ $t("table.add") }}
        </el-button>
        <el-dropdown
          v-has-any-permission="['nursingStaff:delete', 'nursingStaff:export',
                                'nursingStaff:import']"
          class="filter-item"
          trigger="click"
        >
          <el-button>
            {{ $t("table.more") }}<i class="el-icon-arrow-down el-icon--right" />
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-has-permission="['nursingStaff:delete']"
              @click.native="batchDelete"
            >
              {{ $t("table.delete") }}
            </el-dropdown-item>
            <el-dropdown-item
              v-has-permission="['nursingStaff:export']"
              @click.native="exportExcel"
            >
              {{ $t("table.export") }}
            </el-dropdown-item>
            <el-dropdown-item
              v-has-permission="['nursingStaff:export']"
              @click.native="exportExcelPreview"
            >
              {{ $t("table.exportPreview") }}
            </el-dropdown-item>
            <el-dropdown-item
              v-has-permission="['nursingStaff:import']"
              @click.native="importExcel"
            >
              {{ $t("table.import") }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

    </div>

    <el-table
      :key="tableKey"
      :data="tableData.records"
      border
      fit
      row-key="id"
      ref="table"
      @cell-click="cellClick"
      @filter-change="filterChange"
      style="width: 100%;"
      @selection-change="onSelectChange"
      @sort-change="sortChange"
v-loading="loading"
    >

      <el-table-column
        align="center"
        type="selection"
        width="40px"
        :reserve-selection="true"
      />
      <el-table-column
        :label="$t('table.createTime')"
        align="center"
        prop="createTime"
        sortable="custom"
        width="170px"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.avatar')"
        :show-overflow-tooltip="true"
        align="center"
        prop="avatar"
        width=""
      >
        <template slot-scope="scope">
          <el-avatar
            :key="scope.row.avatar"
            :src="(scope.row.avatar)"
            fit="fill"
            icon="el-icon-user-solid"
          ></el-avatar>
        </template>
      </el-table-column>
      <!-- <el-table-column
        :label="$t('table.nursingStaff.loginName')"
        :show-overflow-tooltip="true"
        align="center"
        prop="loginName"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.loginName }}</span>
        </template>
      </el-table-column> -->
<!--      <el-table-column-->
<!--        :label="$t('table.nursingStaff.password')"-->
<!--        :show-overflow-tooltip="true"-->
<!--        align="center"-->
<!--        prop="password"-->
<!--        width=""-->
<!--      >-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ scope.row.password }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column
        :label="$t('table.nursingStaff.name')"
        :show-overflow-tooltip="true"
        align="center"
        prop="name"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column
        :label="$t('table.nursingStaff.mobile')"
        :show-overflow-tooltip="true"
        align="center"
        prop="mobile"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.mobile }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="所属机构"
        :show-overflow-tooltip="true"
        align="center"
        prop="organName"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.organName }}</span>
        </template>
      </el-table-column>
<!--      <el-table-column-->
<!--        :label="$t('table.nursingStaff.passwordStrongLevel')"-->
<!--        :show-overflow-tooltip="true"-->
<!--        align="center"-->
<!--        prop="passwordStrongLevel"-->
<!--        width=""-->
<!--      >-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ scope.row.passwordStrongLevel }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <!-- <el-table-column
        :label="$t('table.nursingStaff.imAccount')"
        :show-overflow-tooltip="true"
        align="center"
        prop="imAccount"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.imAccount }}</span>
        </template>
      </el-table-column> -->
<!--      <el-table-column-->
<!--        :label="$t('table.nursingStaff.gesturePwd')"-->
<!--        :show-overflow-tooltip="true"-->
<!--        align="center"-->
<!--        prop="gesturePwd"-->
<!--        width=""-->
<!--      >-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ scope.row.gesturePwd }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <!-- <el-table-column
        :label="$t('table.nursingStaff.classCode')"
        :show-overflow-tooltip="true"
        align="center"
        prop="classCode"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.classCode }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.certificate')"
        :show-overflow-tooltip="true"
        align="center"
        prop="certificate"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.certificate }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.totalLoginTimes')"
        :show-overflow-tooltip="true"
        align="center"
        prop="totalLoginTimes"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.totalLoginTimes }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.organId')"
        :show-overflow-tooltip="true"
        align="center"
        prop="organId"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.organId }}</span>
        </template>
      </el-table-column>

      <el-table-column
        :label="$t('table.nursingStaff.organCode')"
        :show-overflow-tooltip="true"
        align="center"
        prop="organCode"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.organCode }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.sex')"
        :show-overflow-tooltip="true"
        align="center"
        prop="sex"
        width=""
      >
        <template slot-scope="scope">
          <span v-if="scope.row.sex===0">男</span>
          <span v-else>女</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.birthday')"
        :show-overflow-tooltip="true"
        align="center"
        prop="birthday"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.birthday }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.extraInfo')"
        :show-overflow-tooltip="true"
        align="center"
        prop="extraInfo"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.extraInfo }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.lastLoginIp')"
        :show-overflow-tooltip="true"
        align="center"
        prop="lastLoginIp"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.lastLoginIp }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.nursingStaff.lastLoginTime')"
        :show-overflow-tooltip="true"
        align="center"
        prop="lastLoginTime"
        width=""
      >
        <template slot-scope="scope">
          <span>{{ scope.row.lastLoginTime }}</span>
        </template>
      </el-table-column> -->
      <el-table-column
        label="会员"
        :show-overflow-tooltip="true"
        align="center"
        prop="lastLoginTime"
        width=""
      >
         <template slot-scope="scope">
          <span>{{ scope.row.patientCount }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="医生"
        :show-overflow-tooltip="true"
        align="center"
        prop="lastLoginTime"
        width=""
      >
         <template slot-scope="scope">
          <span>{{ scope.row.doctorCount }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('table.operation')"
        align="center"
        column-key="operation"
        class-name="small-padding fixed-width"
        width="100px"
      >
        <template slot-scope="{ row }">
          <i
            v-hasPermission="['nursingStaff:add']"
            class="el-icon-copy-document table-operation"
            :title="$t('common.delete')"
            style="color: #2db7f5;"
            @click="copy(row)"
          />
          <i
            v-hasPermission="['nursingStaff:update']"
            class="el-icon-edit table-operation"
            :title="$t('common.delete')"
            style="color: #2db7f5;"
            @click="edit(row)"
          />
          <i
            v-hasPermission="['nursingStaff:delete']"
            class="el-icon-delete table-operation"
            :title="$t('common.delete')"
            style="color: #f50;"
            @click="singleDelete(row)"
          />
          <el-link
            v-has-no-permission="['nursingStaff:update', 'nursingStaff:copy', 'nursingStaff:delete']"
            class="no-perm"
          >
            {{ $t("tips.noPermission") }}
          </el-link>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="tableData.total > 0"
      :limit.sync="queryParams.size"
      :page.sync="queryParams.current"
      :total="Number(tableData.total)"
      @pagination="fetch"
    />
    <nursingStaff-edit
      ref="edit"
      :dialog-visible="dialog.isVisible"
      :type="dialog.type"
      @close="editClose"
      @success="editSuccess"
    />
    <nursingStaff-import
      ref="import"
      :dialog-visible="fileImport.isVisible"
      :type="fileImport.type"
      :action="fileImport.action"
      accept=".xls,.xlsx"
      @close="importClose"
      @success="importSuccess"
    />
    <el-dialog
      v-el-drag-dialog
      :close-on-click-modal="false"
      :close-on-press-escape="true"
      title="预览"
      width="80%"
      top="50px"
      :visible.sync="preview.isVisible"
    >
      <el-scrollbar>
        <div v-html="preview.context" />
      </el-scrollbar>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from "@/components/Pagination";
import elDragDialog from '@/directive/el-drag-dialog'
import NursingStaffEdit from "./Edit";
import nursingStaffApi from "@/api/NursingStaff.js";
import NursingStaffImport from "@/components/caring/Import"
import {convertEnum} from '@/utils/utils'
import {downloadFile, loadEnums, initDicts, initQueryParams} from '@/utils/commons'

export default {
  name: "NursingStaffManage",
  directives: { elDragDialog },
  components: { Pagination, NursingStaffEdit, NursingStaffImport},
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
        action: `${process.env.VUE_APP_BASE_API}/用户中心/nursingStaff/import`
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
      nursingStaffApi.preview(this.queryParams).then(response => {
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
      nursingStaffApi.export(this.queryParams).then(response => {
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
      nursingStaffApi.delete({ ids: ids }).then(response => {
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
      this.$refs.edit.setNursingStaff({ enums: this.enums, dicts: this.dicts});
    },
    copy(row) {
      this.$refs.edit.setNursingStaff({row, enums: this.enums, dicts: this.dicts});
      this.dialog.type = "copy";
      this.dialog.isVisible = true;
    },
    edit(row) {
      this.$refs.edit.setNursingStaff({row, enums: this.enums, dicts: this.dicts});
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

      nursingStaffApi.page(this.queryParams).then(response => {
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
