<template>
  <section style="background:#fff;line-height: 1.1875rem /* 19/16 */">

    <x-header :left-options="{backText: ''}">修改详情</x-header>
    <img class="delicon" src="../../images/del.png" @click="showPopup">
    <van-row gutter="20" type="flex" align='center' style=" height: 65px;
  background: rgba(51, 126, 255, 0.05);">
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
    <van-dialog @confirm="confirm" v-model="show" show-cancel-button>
      <div>
        <div class="be-careful"><img src="../../images/be-careful.png" alt=""></div>
        <div class="mess">确定删除本次记录?</div>
        <!-- <div class="choose">
          <div class="option" @click="cancel">取消</div>
          <div class="option1" @click="determine">确定</div>
        </div> -->
      </div>
    </van-dialog>
    <!-- 弹出层 -->
    <!-- <van-popup v-model="show">
      <div>
        <div class="be-careful"><img src="../../images/be-careful.png" alt=""></div>
        <div class="mess">确定删除本次记录?</div>
        <div class="choose">
          <div class="option" @click="cancel">取消</div>
          <div class="option1" @click="determine">确定</div>
        </div>
      </div>
    </van-popup> -->
    <attrPage v-if="currentFields && currentFields.length > 0" :current-fields="currentFields"></attrPage>
    <div>

    </div>
  </section>
</template>


<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>
