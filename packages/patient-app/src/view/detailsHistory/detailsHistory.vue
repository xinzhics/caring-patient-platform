<template>
  <section style="background:#fff;line-height: 1.1875rem /* 19/16 */">
    <navBar pageTitle="修改详情"/>
    <van-row gutter="20" type="flex" align='center' style=" height: 65px;
  background: rgba(51, 126, 255, 0.05);margin-top: 10px">
      <van-col span="3"><img class="careful" src="../../images/careful.png"></van-col>
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
    <div>

    </div>
  </section>
</template>


<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>
