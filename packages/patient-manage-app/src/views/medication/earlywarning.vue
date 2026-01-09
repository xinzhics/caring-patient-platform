<template>
  <div class="list_box">
    <van-sticky>
      <appHeader
        :title="'用药预警'"
        :leftCustomize="true" @handelPage="backIndex"
        rightIcon="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAAA1xJREFUaEPtmYGxTTEQhv9XASpABagAFaACVIAKUAEqQAWoABWgAlSACpjPnJ3ZOW+TbJJz7ps78zJzZ+69J9nst7vZbHJOdOTt5Mj11znAWXvw3AMrD1yRdFnSreV/fv9Yvn+V9FvS5y29toUHrku6L+muJBRuNSDeL58Prc6t5zMAWPmps3Zrrug53nkm6e3IYMaMAFyU9ELSg2BSwoNQ4fPaPb8nCU8BfTMY90nSk2VcF0svAEq8W4XKT0kvJb1ZYtwU+Os08fNgAMLtsaRrrg+hBQRy0q0HAItjeRSg/Vncj/JRKwH4vshk/AX3JyH1PEuQBcD1H53Qb4sVLcOMAjAOg7CofWgBAEizZQAIG5Q3y7PgcD8ur7WMB/x4QodsZo11A1i1tQBQGuWBoJH2iN9M6wVApofAQLdbC7sFgBtJlTTChlBqWd7gRgAYSwazxU12AqLYagBsSt/dyBsta6xmGQVgXiBsYQMASNhqAN6dxH2U92vGGQVApvc8ieJqLwCx/8sNQkAt40TyZwCYn/nMC0XvlzyAtW0nHbE+QDMAjGd/eLRY5tWS+U4ZqgRA+rqz9E6ls8AFswBkvi+L3GIYlQD85Jc6Mo/nmAVAFhmvGkYRgI9/ijOr7TO5f2sAso/t0GE2igB82bAVwKgRWIuW/R5GhV4LoLh4Eu7wIZTo3uwS1kctgHRRFUzPwuN4uVUbApgJIbIIG5IVgTMgGCMsIFsemAGYUTg9tpVGq9t4epYdO5YAfPz2FnE7qntadAnAb+MzC3l3mBIAhxYO77SzCiP2Ds4F1GLFM0itnPbbeLiJ7GhefxapFpM1gHRNvgOILyarIdw6UvrFPLMr9zD68OXqBm8MhRCT+nMBvw8RSv4kyEVX6d7pv1FaHqBP901Bj7mDvraD2+5bFZcBQIC/KTiUJ1J2yAJQz1Cb+7tMXMsCy16zpBTq7ZQFQG50BYjyBtI7N7dwFGjI5bDSe2mQXgNrxdZXgDy3lxZ4idu7yCsoigfJMuuXIcO7fY8HPAi7JPtEdNdv/bCoWZWFWSqruZ5H3sE84EGwJCB+bWRDCcUZ2/U+YC181ANrOfbSwkIjAkJhrGzvx4YsvhdAyer+LWXWM139tvJA16Rbdj4H2NKaI7KO3gP/AJEArjGaojNCAAAAAElFTkSuQmCC"
        @click-right="Tostatistics"
      />
      <div class="list_top">
        <div class="list_top_content">
          <div class="list_top_flex">
            <div class="list_top_left">
              <div class="line"></div>
              <div class="list_num">
                <div class="list_total">{{ "用药预警" }}</div>
                <div class="list_help_icon">
                  以下患者需要您及时跟进
                  <div @click="Showexplain">
                    <img src="../../assets/images/list_help_icon.png" alt="" />
                  </div>
                </div>
              </div>
            </div>
            <div
              class="list_all"
              v-if="
                (active == 1 && deficiencynum !== 0) ||
                (active == 2 && overduenum !== 0) ||
                (active == 3 && handlednum !== 0)
              "
              @click="Showmodal"
            >
              {{ active != 3 ? "全部处理" : "全部清空" }}
            </div>
          </div>
          <div style="margin-top: 26px">
            <van-search
              placeholder="搜索"
              @update:model-value="Seach"
              :maxlength="50"
              v-model="seachval"
            />
          </div>
          <!-- 处理 -->
          <div class="list_status">
            <div
              :class="active == 1 ? 'tab active' : 'tab'"
              @click="CheckTab(1)"
            >
              {{ `余药不足(${deficiencynum})` }}
              <div v-if="active == 1" class="tab_line"></div>
            </div>
            <div
              :class="active == 2 ? 'tab active' : 'tab'"
              @click="CheckTab(2)"
            >
              {{ `购药逾期(${overduenum})` }}
              <div v-if="active == 2" class="tab_line"></div>
            </div>
            <div
              :class="active == 3 ? 'tab active' : 'tab'"
              @click="CheckTab(3)"
            >
              {{ `已处理(${handlednum})` }}
              <div v-if="active == 3" class="tab_line"></div>
            </div>
          </div>
        </div>
      </div>
    </van-sticky>
    <div class="list_title">
      <div class="list_time" style="width: 30%; justify-content: center">
        <p>姓名</p>
      </div>
      <div
        class="list_time"
        style="width: 40%; justify-content: right"
        @click="active != 3 && Sortlist('left')"
      >
        {{ active == 1 ? "剩余药量" : active == 2 ? "逾期天数" : "类型" }}
        <div style="margin-left: 3px" v-if="active != 3">
          <div class="list_item_img_top">
            <img
              class="list_time_img"
              src="../../assets/images/info_up.png"
              v-if="leftisdown"
            />
            <img
              class="list_time_img"
              src="../../assets/images/info_up_check.png"
              v-else
            />
          </div>
          <div>
            <img
              class="list_time_img"
              src="../../assets/images/info_down.png"
              v-if="!leftisdown"
            />
            <img
              class="list_time_img"
              src="../../assets/images/info_down_check.png"
              v-else
            />
          </div>
        </div>
      </div>
      <div class="list_time"
           style="width: 30%; justify-content: right"
           @click="Sortlist('right')">
        {{ active != 3 ? "用完时间" : "处理时间" }}
        <div style="margin-left: 3px">
          <div class="list_item_img_top">
            <img
              class="list_time_img"
              src="../../assets/images/info_up.png"
              v-if="active == 3 ? !isdown : isdown"
            />
            <img
              class="list_time_img"
              src="../../assets/images/info_up_check.png"
              v-else
            />
          </div>
          <div>
            <img
              class="list_time_img"
              src="../../assets/images/info_down.png"
              v-if="active == 3 ? isdown : !isdown"
            />
            <img
              class="list_time_img"
              src="../../assets/images/info_down_check.png"
              v-else
            />
          </div>
        </div>
      </div>
    </div>
    <!-- list -->
    <div class="list_content">
      <!-- 列表 -->
      <div v-if="active == 1" class="listbox" :style="`height:${height}px`">
        <div
          class="list_item"
          v-for="item in deficiencylist"
          :key="item.patientId"
        >
          <div class="list_item_top">
            <div
              class="list_item_top_left"
              @click="Jumppatientinfo(item.patientId)"
            >
              <div class="list_item_img"><img :src="item.avatar" /></div>
              <div class="list_item_info">
                <p>{{ item.patientName }}</p>
                <div>所属医生：{{ item.doctorName }}</div>
              </div>
            </div>
            <div
              class="list_check"
              @click.stop="Handleddeficiency(item.patientId)"
            >
              <img src="../../assets/images/info_no_check.png" />
            </div>
          </div>
          <!-- 文本内容 -->
          <div class="list_item_content">
            <div
              class="list_item_list"
              v-for="v in item.drugsDeficiencyMessageData"
              :key="v.drugsId"
            >
              <p>
                <span
                  v-text="v.drugsName"
                  style="padding-left: 2px"
                  class="fieldlabel"
                ></span>
                <span style="padding-left: 80px"
                  >{{ v.drugsAvailableDay }}天</span
                >
              </p>
              <p
                v-text="v.drugsEndTime && v.drugsEndTime.replace(/-/g, '.')"
              ></p>
            </div>
          </div>
          <!-- 联系他 -->
          <div class="list_contact" @click="Jumppatientchat(item.patientId)">
            <img src="../../assets/images/wx_icon.png" alt="" />
            联系TA
          </div>
        </div>
        <div class="more">
          <p v-if="more[1].loadmore && deficiencynum != 0" @click="Moredata">
            点击加载更多...
          </p>
          <p v-if="more[1].nomore && deficiencynum != 0">没有更多了</p>
        </div>
        <div class="no_data" v-if="deficiencynum == 0">
          <img src="../../assets/images/no_data.png" />
          <p>暂无数据</p>
        </div>
      </div>
      <!-- 列表 -->
      <div v-if="active == 2" class="listbox" :style="`height:${height}px`">
        <div
          class="list_item"
          v-for="item in overduelist"
          :key="item.patientId"
        >
          <div class="list_item_top">
            <div
              class="list_item_top_left"
              @click="Jumppatientinfo(item.patientId)"
            >
              <div class="list_item_img"><img :src="item.avatar" /></div>
              <div class="list_item_info">
                <p>{{ item.patientName }}</p>
                <div>所属医生：{{ item.doctorName }}</div>
              </div>
            </div>
            <div
              class="list_check"
              @click.stop="Handleddeficiency(item.patientId)"
            >
              <img src="../../assets/images/info_no_check.png" />
            </div>
          </div>
          <!-- 文本内容 -->
          <div class="list_item_content">
            <div
              class="list_item_list"
              v-for="v in item.drugsDeficiencyMessageData"
              :key="v.drugsId"
            >
              <p>
                <span
                  v-text="v.drugsName"
                  style="padding-left: 2px"
                  class="fieldlabel"
                ></span>
                <span style="padding-left: 80px"
                  >{{ v.drugsAvailableDay }}天</span
                >
              </p>
              <p
                v-text="v.drugsEndTime && v.drugsEndTime.replace(/-/g, '.')"
              ></p>
            </div>
          </div>
          <!-- 联系他 -->
          <div class="list_contact" @click="Jumppatientchat(item.patientId)">
            <img src="../../assets/images/wx_icon.png" alt="" />
            联系TA
          </div>
        </div>
        <div class="more">
          <p v-if="more[2].loadmore && overduenum != 0" @click="Moredata">
            点击加载更多...
          </p>
          <p v-if="more[2].nomore && overduenum != 0">没有更多了</p>
        </div>
        <div class="no_data" v-if="overduenum == 0">
          <img src="../../assets/images/no_data.png" />
          <p>暂无数据</p>
        </div>
      </div>
      <div v-if="active == 3" class="listbox" :style="`height:${height}px`">
        <div
          class="list_item"
          v-for="item in handledlist"
          :key="item.patientId"
        >
          <div class="list_item_top boder">
            <div
              class="list_item_top_left"
              @click="Jumppatientinfo(item.patientId)"
            >
              <div class="list_item_img">
                <img :src="item.avatar" />
              </div>
              <div class="list_item_info-handle">
                <p>{{ item.patientName }}</p>
                <div>
                  <span style="color: #999999"
                    >所属医生：{{ item.doctorName }}</span
                  >
                </div>
              </div>
            </div>
            <div class="list_item_top_right">
              <div>
                {{ item.warningType == 1 ? "余药不足" : "购药逾期" }}
              </div>
              <div>
                {{
                  item.handleTime &&
                  item.handleTime.replace(/-/g, ".").slice(0, -3)
                }}
              </div>
            </div>
          </div>
          <!-- 联系他 -->
          <div class="list_contact" @click="Jumppatientchat(item.patientId)">
            <img src="../../assets/images/wx_icon.png" alt="" />
            联系TA
          </div>
        </div>
        <div class="more">
          <p v-if="more[3].loadmore && handlednum != 0" @click="Moredata">
            点击加载更多...
          </p>
          <p v-if="more[3].nomore && handlednum != 0">没有更多了</p>
        </div>
        <div class="no_data" v-if="handlednum == 0">
          <img src="../../assets/images/no_data.png" />
          <p>暂无数据</p>
        </div>
      </div>
    </div>
  </div>
  <Dialog.Component
    v-model:show="showTips"
    close-on-click-overlay
    close-on-popstate
    :show-confirm-button="false"
  >
    <div class="tipsbox">
      <div class="tipsbox-title">
        <p>用药预警说明</p>
        <img src="@/assets/images/close.png" alt="" @click="Showexplain" />
      </div>
      <div class="tipsbox-content">
        <p>
          <span>余药不足：患者即将用完的药品</span>
        </p>
        <p><span>购药逾期：药品用尽未及时添加</span></p>
      </div>
    </div>
  </Dialog.Component>
  <Dialog.Component
    v-model:show="model[active]"
    show-cancel-button
    @confirm="Handlelist"
  >
    <div class="modalbox">
      是否全部{{ active != 3 ? "改为" : "清空" }}[<span>已处理</span>]{{
        active != 3 ? "状态" : "记录"
      }}
    </div>
  </Dialog.Component>
</template>
<script  lang="ts" setup>
import usewarnOption from "./usewarnOption";
const {
  Tostatistics,
  Showexplain,
  Showmodal,
  Seach,
  CheckTab,
  Sortlist,
  Handleddeficiency,
  Handlelist,
  deficiencylist,
  deficiencynum,
  overduelist,
  overduenum,
  handledlist,
  handlednum,
  showexplain,
  leftisdown,
  isdown,
  showTips,
  model,
  appHeader,
  Dialog,
  List,
  active,
  Jumppatientinfo,
  Jumppatientchat,
  more,
  Moredata,
  seachval,
  height,
  backIndex,
} = usewarnOption();
</script>

<style lang="less" scoped>
.list_box {
  background: rgba(245, 245, 245, 1);
  position: relative;
  height: 100vh;

  .list_top {
    height: 212px;
    background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
    box-shadow: 0px 2px 2px 1px rgba(51, 126, 255, 0.08);
    .list_top_content {
      padding: 30px 20px 7px 20px;

      .list_top_flex {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .list_top_left {
          display: flex;
          justify-content: center;
        }

        .line {
          width: 4px;
          height: 50px;
          background: rgba(255, 255, 255, 0.4);
          border-radius: 52px 52px 52px 52px;
        }

        .list_num {
          margin-left: 13px;

          .list_total {
            max-width: 220px;
            font-size: 27px;
            font-weight: bold;
            color: #ffffff;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .list_help {
            margin-top: 10px;
            font-size: 13px;
            font-weight: 400;
            color: #ffffff;
            line-height: 16px;
          }

          .list_help_icon {
            margin-top: 10px;
            font-size: 13px;
            font-weight: 400;
            color: #ffffff;
            line-height: 16px;
            display: flex;

            img {
              width: 15px;
              height: 15px;
              margin-left: 3px;
            }
          }
        }

        .list_all {
          position: absolute;
          right: 20px;
          top: 102px;
          width: 88px;
          height: 28px;
          background: rgba(0, 132, 255, 0);
          border-radius: 52px 52px 52px 52px;
          opacity: 1;
          border: 1px solid #ffffff;
          font-size: 12px;
          font-weight: 400;
          color: #ffffff;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }

    .list_status {
      margin-top: 28px;
      display: flex;
      font-size: 17px;
      justify-content: space-around;
      .tab {
        font-size: 16px;
        font-weight: 400;
        color: #ffffff;
        line-height: 0px;
        opacity: 0.7;
      }

      .active {
        font-size: 17px;
        font-weight: 500;
        opacity: 1;
      }

      .tab_line {
        width: 23px;
        height: 6px;
        background: #ffffff;
        border-radius: 4px 4px 4px 4px;
        // margin-top: 10px;
        margin: 15px auto 0px auto;
        text-align: center;
      }
    }
  }

  .list_title {
    position: sticky;
    top: 0;
    margin: 16px 13px -15px 0;
    padding-bottom: 16px;
    // height: 30px;
    z-index: 9;
    font-size: 13px;
    font-weight: 400;
    color: #666666;
    background-color: #f5f5f5;
    // line-height: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    p {
      padding: 0px;
    }

    .list_time {
      display: flex;
      align-items: center;
      padding-right: 14px;

      .list_time_img {
        width: 6px;
        height: 6px;
      }

      .list_item_img_top {
        margin-top: -3px;
        height: 7px;
      }
    }
  }

  .list_content {
    // padding-bottom: ;
    padding: 13px;
    width: 100%;
    position: fixed;
    height: 60%;
    overflow-y: scroll;
    .listbox {
      overflow: auto;
      div:first-child {
        margin-top: 2px;
      }
      .more {
        font-size: 14px;
        color: #666666;
        text-align: center;
        p {
          padding: 5px 22px 10px 0;
        }
      }
      .list_item {
        background: #ffffff;
        border-radius: 4px 4px 4px 4px;
        padding: 5px 13px;
        width: 86%;
        margin: 20px 0;

        .list_item_top {
          display: flex;
          align-items: center;
          justify-content: space-between;
          height: 55px;
        }

        .list_item_img {
          width: 42px;
          height: 42px;
          border-radius: 50%;
          border: 0.5px solid #f5f5f5;
          img {
            width: 42px;
            height: 42px;
            border-radius: 50%;
          }
        }

        .list_item_info {
          margin-left: 8px;
          width: 220px;
          padding-top: 8px;
          p {
            font-size: 16px;
            font-weight: 500;
            color: #333333;
            line-height: 19px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          div {
            font-size: 13px;
            font-weight: 400;
            color: #999999;
            line-height: 25px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            // margin-top: 5px;
          }
        }
        .list_item_info-handle {
          width: 124px;
          margin-left: 8px;
          padding-top: 10px;
          p {
            font-size: 16px;
            font-weight: 500;
            color: #333333;
            line-height: 19px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          div {
            font-size: 13px;
            font-weight: 400;
            color: #999999;
            line-height: 25px;
            width: 124px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            // margin-top: 5px;
          }
        }
        .list_item_top_left {
          display: flex;
          align-items: center;
        }
        .list_item_top_right {
          display: flex;

          font-size: 11px;
          justify-content: space-between;
          color: #666666;
          padding-top: 5px;
          div:first-child {
            margin: 0 10px 0 0;
          }
        }
        .boder {
          border-bottom: 0.5px solid #ebebeb;
          padding-bottom: 10px;
        }
        .list_item_content {
          background: rgba(245, 245, 245, 0.6);
          border-radius: 4px 4px 4px 4px;
          padding: 18px 13px;
          margin-top: 10px;
          div:first-child {
            padding-top: 1px;
          }
          .list_item_list {
            display: flex;
            justify-content: space-between;
            padding-top: 15px;
            p {
              display: flex;
              font-size: 11px;
              color: #666666;

              span {
                font-size: 11px;
                color: #666666;
                margin-left: 3px;
              }
            }

            img {
              width: 13px;
              height: 13px;
            }
            .fieldlabel {
              color: #337eff;
              width: 100px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }
          }
        }
      }
    }
    .list_contact {
      width: 105px;
      height: 28px;
      background: #1890ff;
      border-radius: 42px 42px 42px 42px;
      margin: 14px auto 8px auto;
      font-size: 13px;
      color: #ffffff;
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        width: 17px;
        height: 14px;
        margin-right: 7px;
      }
    }

    .list_check {
      margin-top: 10px;
      img {
        width: 33px;
        height: 33px;
      }
    }
  }

  :deep(.van-search) {
    // margin: 0px 20px;
    height: 44px;
    border-radius: 22px;
  }

  :deep(.van-search__content) {
    background: #ffffff;

    input {
      font-size: 17px;
    }
  }
}

.modalbox {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;

  span {
    font-weight: bold;
  }
}

.tipsbox {
  min-height: 160px;

  .tipsbox-title {
    margin: auto;
    width: 90%;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 50px;
    border-bottom: 1px solid #bababa;
    position: relative;

    img {
      width: 16px;
      height: 16px;
      position: absolute;
      right: 0;
    }
  }

  .tipsbox-content {
    width: 90%;
    margin: 30px auto 0px;
    padding-bottom: 30px;
    p {
      margin-top: 15px;
    }
  }
}

.no_data {
  margin: 125px 21px 0 0 !important;
  text-align: center;

  img {
    width: 146px;
    height: 146px;
  }

  p {
    font-size: 15px;
    line-height: 19px;
    color: #999999;
    margin-top: 13px;
    text-align: center;
  }
}
</style>
