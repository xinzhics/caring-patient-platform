<template>
  <div class="info_content" v-if="list.length > 0">
    <h3>{{ title }}</h3>
    <van-divider />
    <div v-for="(item, index) in list" :key="index">
      <!-- FullName -->
      <div class="i_item" v-if="item.fieldWidgetType === 'FullName'">
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div v-if="item.fieldValues && JSON.parse(item.fieldValues)">
          <div
            class="i_content"
            v-for="(item, index) in JSON.parse(item.fieldValues)"
            :key="index"
          >
            {{ item?.attrValue }}
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>

      <!-- SingleLineText单行文字 -->
      <div class="i_item" v-if="item.fieldWidgetType === 'SingleLineText'">
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div v-if="item.fieldValues && item.fieldValues.length > 0 && JSON.parse(item.fieldValues).length > 0">
          <div
            class="i_content"
            v-for="(result, index) in JSON.parse(item.fieldValues)"
            :key="index"
          >
            <span v-if="'hospital' === item.fieldExactType">{{ result.valueText }}</span>
            <span v-else>{{ result.attrValue }}</span>
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>
      <!-- MultiLineText 多行文字 -->
      <div class="i_item" v-if="item.fieldWidgetType === 'MultiLineText'">
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div v-if="item.fieldValues && JSON.parse(item.fieldValues)">
          <div
            class="i_content"
            v-for="(item, index) in JSON.parse(item.fieldValues)"
            :key="index"
          >
            {{ item.attrValue }}
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>
      <!-- Radio 多选 -->
      <div
        class="i_item"
        v-if="
          item.fieldWidgetType === 'CheckBox' ||
          item.fieldWidgetType === 'DropdownSelect' ||
          item.fieldWidgetType === 'Radio'
        "
      >
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div class="i_c_c_box" v-if="item.fieldValues && JSON.parse(item.fieldValues)">
          <div
            v-for="(items, index) in JSON.parse(item.fieldValues)"
            :key="index"
          >
            <div class="i_content_checkBox" v-if="items.valueText">
              <div class="i_c_title">
                <div><span></span> {{ items.valueText }}</div>
                <img src="../../../assets/images/info_back.png" />
              </div>
              <p v-if="items.valueText === '其他'">{{ item.otherValue }}</p>
              <p v-else>{{ items.desc }}</p>
            </div>
            <van-divider
              v-if="JSON.parse(item.fieldValues).length > index + 1"
            />
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>
      <!-- Select 下拉 -->
      <div class="i_item" v-if="item.fieldWidgetType === 'Select'">
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div v-if="item.fieldValues && JSON.parse(item.fieldValues)">
          <div
            class="i_content"
            v-for="(item, index) in JSON.parse(item.fieldValues)"
            :key="index"
          >
            {{ item.valueText }}
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>
      <!-- DropdownSelect 下拉 -->
      <!-- MultiImageUpload 图片多选 -->
      <div
        class="i_item i_imgs"
        v-if="
          item.fieldWidgetType === 'MultiImageUpload' ||
          item.fieldWidgetType === 'SingleImageUpload'
        "
      >
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div v-if="item.fieldValues && JSON.parse(item.fieldValues).length > 0">
          <div
            class="i_img"
            v-for="(item, index) in JSON.parse(item.fieldValues)"
            :key="index"
            @click="handleShow"
          >
            <!-- <img :src="item.attrValue" @click="handelImg(item.attrValue)" /> -->
            <van-image
              width="100%"
              height="200"
              :src="item.attrValue"
              fit="fill"
              @click="handelImg(item.attrValue)"
            />
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>

      <!-- Number 数字 -->
      <div class="i_item" v-if="item.fieldWidgetType === 'Number'">
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div v-if="item.fieldValues && JSON.parse(item.fieldValues)">
          <div
            class="i_content"
            v-for="(item, index) in JSON.parse(item.fieldValues)"
            :key="index"
          >
            {{ item.attrValue }}
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>

      <!-- Time 时间  日期-->
      <div
        class="i_item"
        v-if="
          item.fieldWidgetType === 'Time' || item.fieldWidgetType === 'Date'
        "
      >
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div v-if="item.fieldValues && JSON.parse(item.fieldValues)">
          <div
            class="i_content"
            v-for="(item, index) in JSON.parse(item.fieldValues)"
            :key="index"
          >
            {{ item.attrValue }}
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>

      <!-- 地址 Address-->
      <div class="i_item" v-if="item.fieldWidgetType === 'Address'">
        <div class="i_item_right">
          <p>{{ item.fieldLabel }}</p>
          <div v-if="item.fieldWrite == 0">待完善</div>
        </div>
        <div v-if="item.fieldValues && JSON.parse(item?.fieldValues)">
          <div
            class="i_content"
            v-for="(item, index) in JSON.parse(item?.fieldValues)"
            :key="index"
          >
            {{ item?.attrValue && item?.attrValue[0] }}
            {{ item?.attrValue && item?.attrValue[1] && item?.attrValue[1] }}
            {{
              item?.attrValue &&
              item?.attrValue[1] &&
              item?.attrValue[2] &&
              item?.attrValue[2]
            }}
          </div>
        </div>
        <div v-else>
          <div class="i_content"></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from "vue";
import { ImagePreview } from "vant";
const { list, title } = defineProps(["list", "title"]);
const handelImg = (item: any) => {
  ImagePreview([item]);
};
</script>

<style lang="less" scoped>
.info_content {
  padding-bottom: 50px;
  :deep(.van-divider) {
    margin: 0px 13px;
    background: #d6d6d6;
  }
  h3 {
    font-size: 16px;
    font-weight: 500;
    line-height: 27px;
    color: #337eff;
    margin: 30px 13px 13px;
    display: flex;
    align-items: center;
    &::before {
      width: 3px;
      height: 13px;
      background: #337eff;
      border-radius: 4px;
      content: "";
      display: inline-block;
      margin-right: 8px;
    }
  }
  .i_item {
    // border-top: 1px solid red;
    padding: 25px 13px 0px 15px;
    p {
      font-size: 16px;
      font-family: Source Han Sans CN;
      font-weight: 500;
      line-height: 27px;
      color: #333333;
      span {
        margin-left: 3px;
        color: #ff5555;
      }
    }
    .i_content {
      // height: 46px;
      background: rgba(245, 245, 245, 0.39);
      border: 1px solid #d6d6d6;
      opacity: 1;
      border-radius: 4px;
      margin-top: 17px;
      padding: 15px 13px;
      font-size: 16px;
      color: #333333;
    }
    .i_c_c_box {
      border: 1px solid #d6d6d6;
      background: rgba(245, 245, 245, 0.39);

      margin-top: 17px;
      padding: 15px 13px;
      :deep(.van-divider) {
        margin: 12px 0px !important;
        background: #d6d6d6;
      }
      .i_content_checkBox {
        .i_c_title {
          line-height: 23px;
          color: #333333;
          font-size: 16px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          img {
            width: 21px;
            height: 21px;
          }
          div {
            display: flex;
            align-items: center;
          }
          span {
            width: 6px;
            height: 6px;
            background: rgba(51, 51, 51, 1);
            border-radius: 50%;
            display: inline-block;
            margin-right: 5px;
          }
        }
        p {
          margin-top: 7px;
          font-size: 13px;
          font-weight: 400;
          line-height: 21px;
          color: #666666;
        }
        opacity: 1;
        border-radius: 4px;
        font-size: 16px;
        color: #333333;
      }
    }
    .i_item_right {
      display: flex;
      justify-content: space-between;
      align-items: center;
      div {
        width: 66px;
        height: 25px;
        background: #ff7777;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 13px;
        font-weight: 400;
        line-height: 21px;
        color: #ffffff;
        border-radius: 20px;
      }
    }
    .i_sex {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 17px;
      .i_male {
        width: 169px;
        height: 46px;
        background: rgba(255, 255, 255, 0.39);
        border: 1px solid #6f7d97;
        opacity: 1;
        border-radius: 42px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
      .i_girl {
        width: 166px;
        height: 46px;

        background: rgba(245, 245, 245, 0.39);
        border: 1px solid #d6d6d6;
        opacity: 1;
        border-radius: 42px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
}
.i_imgs {
  border: none;
  overflow: hidden;
  .i_img {
    //img {
    //  width: 350px !important;
    //  height: 175px;
    //}
  }
}
</style>
