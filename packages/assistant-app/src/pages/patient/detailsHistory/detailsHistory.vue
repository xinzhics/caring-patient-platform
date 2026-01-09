<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left"  title="修改详情" @onBack="back" ></headNavigation>
    </van-sticky>
    <van-row gutter="20" type="flex" align='center' style=" height: 65px; margin-top: 13px;
  background: rgba(51, 126, 255, 0.05);">
      <van-col span="3"><img class="careful" src="../../../assets/patient/careful.png"></van-col>
      <van-col span="20 ">
        <div>
          <span style="font-size: 13px;color: #333333;">本条记录由</span>
          <span style="font-size: 13px;color:#337EFF;font-weight: 700;">
            {{ $route.query.userType=="DOCTOR"?name:$route.query.userType=="PATIENT"?patient:$route.query.userType=="NURSING_STAFF"?assistant:'' }}

            {{$route.query.name}}
          </span>
        </div>
        <div>
          <!-- 蓝色部分为修改后内容 -->
          <span style="font-size: 13px;color: #333333;">于 {{$route.query.createTime}} 编辑修改,</span>
          <span style="font-size: 13px;color:#337EFF;">蓝色部分</span>
          <span style="font-size: 13px;color: #333333;">为修改后内容</span>
        </div>

      </van-col>
    </van-row>
    <attrPage v-if="currentFields && currentFields.length > 0" :current-fields="currentFields"></attrPage>
  </div>
</template>
<script src="./index.js"></script>
<style lang='less' scoped src="./index.less"></style>
