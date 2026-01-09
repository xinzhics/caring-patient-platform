<template>
  <div class="messagebox">
    <navBar :isIm="isRecording" :pageTitle="pageTitle ? pageTitle : '健康服务小组'"
            backUrl="/home"
            @toHistoryPage="independence == 0 ? JumpPatient() : isImGroup()" :rightIcon="headerImg"
            :showRightIcon="independence == 0 ?   true : independence == 1 && patientImGroup.length > 1 ? true : false"/>
    <div>
      <div style="background-color: white" v-if="independence !== 0 && imGroupStatus === 1">
        <div v-if="patientImGroup.length > 1"
             style="width: 100%; height: 90px; background-color: white;">
          <flexbox style="height: 100%; ">
            <flexbox-item v-for="(item, index) in this.patientImGroup" :key="index">
              <div @click="JumpPatient(item)">
                <div style="display: flex; justify-content: center; align-items: center;">
                  <img width="44px" height="44px"
                       style="border-radius: 50%"
                       v-bind:src=loadingAvatar(item.avatar,item.type)
                  />
                </div>
                <div style="display: flex; justify-content: center; align-items: center; margin-top: 5px">
            <span style="text-align: center; display: inline-block; font-size: 12px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
              margin-left: 5px; margin-right: 5px">{{
                item.name
              }} {{
                item.type === "NursingStaff" ? "(" + appUserCall + ")" : item.type === "doctor" ? "(" + wxUserCall + ")" : item.type === "patient" ? "(我)" : ""
              }}</span>
                </div>
              </div>
            </flexbox-item>
          </flexbox>
        </div>
      </div>
      <!--聊天列表页面-->
      <div class="messagebox-content"
           id="resultScroll" ref="msgContent" @click="closePopover()"
           @touchmove="msgBoxContentTouchmove"
           :style="independence !== 0 && imGroupStatus === 1 ? 'height: calc(100vh - ' + (160 + footerHeight)+'px)' : 'height: calc(100vh - '+(70 + footerHeight ) + 'px)'">
        <van-pull-refresh v-model="isLoading" @refresh="loadMoreMsgs" style="min-height: 100%">
          <div>
            <div
                v-for="(item,i) in messageList"
                :key="i"
                class="message-group">
              <!--第一条信息显示时间 || 大于5分组显示时间-->
              <div
                  v-if="(!item.deleteThisMessageUserIdJsonArrayString || item.deleteThisMessageUserIdJsonArrayString.indexOf(patientInfo.id) == -1) && (  i === 0 || isShowDate(item.createTime, messageList[i - 1].createTime))"
                  class="time">
                {{ renderTime(item.createTime) }}
              </div>
              <!--是否是自己删除的消息-->
              <div
                  v-if="item.deleteThisMessageUserIdJsonArrayString && item.deleteThisMessageUserIdJsonArrayString.indexOf(patientInfo.id) != -1">
              </div>
              <!-- 是否是自己撤回的消息 或开启或者退出  -->
              <div v-else-if="item.type === 'openChat' || item.type === 'exitChat'" class="time">
                {{ getChatContent(item) }}
              </div>
              <div v-else-if="item.withdrawChatStatus === 1" class="time">
                {{
                  patientInfo.id === item.withdrawChatUserId ? '你撤回了一条消息' : getWithdrawContent(item.withdrawChatUserId)
                }}
              </div>
              <div v-else-if="item.senderRoleType !== 'system_im'"
                   :style="{'font-size': getIos() ? '16px' : '15px'}">
                <div v-if="!item.senderImAccount || item.senderImAccount !== imAccount"
                     style="display: inline-block; margin-left: 15px; margin-top: 8px">
                  <div style="display: flex; float: left;">
                    <div>
                      <div style="position: relative; width: 50px; height: 50px">
                        <img width="40px" height="40px"
                             @touchstart="gotouchstartImage(item)"
                             v-bind:src=loadingAvatar(item.senderAvatar,item.senderRoleType)
                             @touchend="gotouchchendImage()"
                             style="border-radius: 50%; margin-top: 5px"/>

                        <img v-if="item.aiHostedSend" :src="require('@/assets/my/consult_ai.png')"
                             style="width: 20px; position: absolute; bottom: 0px; right: 5px"/>
                      </div>

                    </div>
                    <div>
                      <div class="sendName">{{
                          item.senderName
                        }}
                      </div>
                      <c-popover placement="bottom"
                                 width="60"
                                 trigger="manual"
                                 v-model="item.showPopover">
                        <template>
                          <div style="width: 60px">
                            <van-cell v-for="(item, aIndex) in actions" :key="aIndex" :title="item.text"
                                      @click="onSelect(item, i)"/>
                          </div>
                        </template>
                        <div slot="reference" style="display: flex; user-select: none; outline: 0 !important;"
                             @touchstart="popoverGotouchstart(item, i)" @touchmove="popoverGotouchmove"
                             @touchend="popoverGotouchend(item, i, 'left')">
                          <div
                              style="margin-top: 6px; right: -16px; width:0;height:0; font-size:0; border:solid 8px;border-color:#F5F5F9 #ffffff #F5F5F9 #F5F5F9">
                          </div>
                          <div v-if="'text' === item.type && item.recommendationFunction"
                               style="background: #FFF; border-radius: 5px; height: fit-content">
                            <div
                                style="display: flex; align-items: center; border-bottom: 1px solid #EEE; padding: 10px 30px 10px 10px;">
                              <van-image
                                  :src="require('@/assets/my/im_recommend_edit.png')"
                                  style="width: 31px;"
                              />
                              <span
                                  style="color: #333; font-size: 14px; margin-left: 5px; font-weight: bold">医生邀请您{{
                                  JSON.parse(item.recommendationFunctionParams).isLink ? '查看' : '完善'
                                }}以下内容</span>
                            </div>
                            <div
                                style="color: #67E0A7; font-size: 15px; padding-left: 10px; padding-top: 10px; font-weight: bold">
                              {{ JSON.parse(item.recommendationFunctionParams).name }}
                            </div>
                            <div
                                style="color: #999; font-size: 13px; padding-left: 10px; padding-top: 8px; display: flex; align-items: center">
                              点此处可进入
                              <van-image
                                  :src="require('@/assets/my/im_recommend_arrow.png')"
                                  style="height: 11px; margin-left: 5px;"
                              />
                            </div>
                            <div
                                style="display: flex; justify-content: right; padding-bottom: 10px; padding-top: 10px; padding-right: 10px;">
                              <div style="width: 72px; background: #67E0A7; color: #FFF; display: flex; align-items: center;
                                   justify-content: center; height: 23px; font-size: 13px;"
                                   v-if='JSON.parse(item.recommendationFunctionParams).isLink'>
                                查看详情
                              </div>

                              <div style="width: 72px; background: #999; color: #FFF; display: flex; align-items: center;
                                justify-content: center; height: 23px; font-size: 13px;" v-else
                                   :style="{background: JSON.parse(item.recommendationFunctionParams).fillOut === 1 ? '#67E0A7' : '#999'}">
                                {{ JSON.parse(item.recommendationFunctionParams).fillOut === 1 ? '已填写' : '未填写' }}
                              </div>
                            </div>
                          </div>
                          <div v-else-if="'text' === item.type" style="text-align: left; margin-right: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #FFF; padding: 10px; border-radius: 5px; height: fit-content">
                            <div v-if="item.isTemp" style="padding: 7px">
                              <div class="container">
                                <div class="dot"/>
                              </div>
                            </div>
                            <div v-else style="white-space: pre-line"> {{ item.content }}</div>
                            <div v-if="item.aiHostedSend" style="display: flex; justify-content: space-between; align-items: center; margin-top: 10px; border-top: 1px solid #EEE">
                              <div style="font-size: 12px; color: #8C90A5; margin-top: 10px">
                                上述内容由AI提供
                              </div>

                              <div v-if="isDoctorAiHosted"
                                   style="color: #4562BA; font-size: 16px; gap: 3px; display: flex; align-items: center; padding-top: 6px"
                                   @click="changeAiHosted">
                                <van-image width="22px" height="22px" :src="require('@/assets/my/consult_manual.png')"/>
                                <span>
                                  {{ !aiHosted ? '转人工' : 'AI回答' }}
                                </span>
                              </div>

                            </div>
                          </div>
                          <div v-else-if="'image' ===item.type" class="cssLeftImage">
                            <van-image width="100" height="100" :src="item.content" fit="cover"/>
                            <div class="imgbox"></div>
                          </div>
                          <div
                              v-if="'cms' ===item.type" class="cssLeftCMS">
                            <div style="display: flex; width: 100%">
                              <div style="width: 200px; height: 100%">
                                <div style="margin-left: 7px; margin-right: 7px;
                          display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp:1; overflow: hidden">
                                  {{ JSON.parse(item.content).title }}
                                </div>
                                <div style="margin-left: 7px; margin-right: 7px; font-size: 14px; color: #999999; margin-top: 5px;
                          display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp:1; overflow: hidden">
                                  {{ JSON.parse(item.content).summary ? JSON.parse(item.content).summary : '' }}
                                </div>
                              </div>

                              <div style="width: 45px; height: 45px">
                                <van-image
                                    width="45"
                                    height="45"
                                    fit="cover"
                                    :src="JSON.parse(item.content).icon"/>
                              </div>
                            </div>
                          </div>
                          <div v-else-if="'voice' === item.type" class="cssLeftVoice"
                               :style="'width:'+ getWidth(JSON.parse(item.content).timeLength)+'px'">
                            <img :src="require('@/assets/my/im_voice_playing.png')" width="20" height="20"
                                 :id="'img'+ i"/>
                            <span style="color: #555555; font-size: 12px">{{
                                JSON.parse(item.content).timeLength === 'NaN' ? '2' : JSON.parse(item.content).timeLength
                              }}s</span>
                            <audio :id="'audio' + i" @ended="overAudio" @play="onPlay"/>
                            <div class="imgbox"></div>
                          </div>
                        </div>
                      </c-popover>
                    </div>
                  </div>
                </div>
                <div v-else
                     style="display: flex; float: right; margin-right: 15px; margin-top: 8px">
                  <c-popover placement="bottom"
                             width="60"
                             trigger="manual"
                             v-model="item.showPopover">
                    <template>
                      <div style="width: 60px">
                        <van-cell v-for="(item, aIndex) in actions" :key="aIndex" :title="item.text"
                                  @click="onSelect(item, i)"/>
                      </div>
                    </template>
                    <div slot="reference" style="user-select: none; display: flex; outline: 0 !important;"
                         @touchstart="popoverGotouchstart(item, i, 'my')" @touchmove="popoverGotouchmove"
                         @touchend="popoverGotouchend(item, i, 'right')">
                      <div v-if="'text' ===item.type" style="text-align: left; margin-left: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #3F86FF; padding: 10px; border-radius: 5px; color: white">
                        <div style="white-space: pre-line"> {{ item.content }}</div>
                      </div>

                      <div v-if="'image' ===item.type" class="cssRightImage">
                        <van-image width="100" height="100" :src="item.content" fit="cover"/>
                        <div class="imgbox"></div>
                      </div>
                      <div v-if="'cms' ===item.type" class="cssRightCMS">
                        <div style="background: #ffffff; align-items: center; display: flex; padding: 10px"
                             @click="seeCMS(JSON.parse(item.content))">
                          <div>
                            <div style="margin-right: 7px;
                          display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp:1; overflow: hidden">
                              {{ JSON.parse(item.content).title }}
                            </div>
                            <div style="margin-right: 7px; font-size: 12px; color: #999999; margin-top: 5px;
                          display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp:1; overflow: hidden">
                              {{ JSON.parse(item.content).summary ? JSON.parse(item.content).summary : '' }}
                            </div>
                          </div>

                          <div style="width: 45px; height: 45px; position: relative">
                            <van-image
                                width="45"
                                height="45"
                                fit="cover"
                                :src="JSON.parse(item.content).icon"/>
                            <div class="imgbox"></div>
                          </div>
                        </div>
                      </div>

                      <div v-if="'voice' ===item.type" class="cssRightVoice"
                           :style="'width:'+ getWidth(JSON.parse(item.content).timeLength)+'px'">
                      <span style="color: #fff; font-size: 12px">{{
                          JSON.parse(item.content).timeLength === 'NaN' ? '2' : JSON.parse(item.content).timeLength
                        }}s</span>
                        <img :src="require('@/assets/my/im_right_voice_playing.png')" width="20" height="20"
                             :id="'img'+ i"/>
                        <audio :id="'audio' + i" @ended="overAudio" @play="onPlay"/>
                        <div class="imgbox"></div>
                      </div>

                      <div
                          style="text-align: right; margin-top: 6px; right: -16px; width:0;height:0; font-size:0; border:solid 8px;border-color: #F5F5F9 #F5F5F9 #F5F5F9 #3F86FF;">
                      </div>
                    </div>
                  </c-popover>
                  <img width="35px" height="35px"
                       style="border-radius: 50%"
                       v-bind:src=loadingAvatar(item.senderAvatar,item.senderRoleType)
                  />
                </div>
              </div>

              <div v-else style="margin-top: 15px"
                   :style="{'font-size': getIos() ? '16px' : '15px'}">
                <div
                    v-if="'text' ===item.type"
                    style="text-align: left; word-wrap: break-word; word-break: break-all; overflow: hidden; margin-right: 20px; margin-left: 20px;
                background: #FFF; padding: 10px; border-radius: 5px; height: fit-content">
                  <div style="white-space: pre-line"> {{ item.content }}</div>
                </div>

                <div
                    v-if="'cms' ===item.type"
                    style="text-align: left;margin-right: 20px; margin-left: 20px; background: #FFF; padding: 10px; border-radius: 5px;">
                  <div style="display: flex; width: 100%; justify-content: space-between"
                       @click="seeCMS(JSON.parse(item.content))">
                    <div>
                      <div style="margin-left: 7px; margin-right: 7px;
                          display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp:1; overflow: hidden">
                        {{ JSON.parse(item.content).title }}
                      </div>
                      <div style="margin-left: 7px; margin-right: 7px; font-size: 12px; color: #999999; margin-top: 5px;
                          display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp:1; overflow: hidden">
                        {{ JSON.parse(item.content).summary ? JSON.parse(item.content).summary : '' }}
                      </div>
                    </div>

                    <div style="width: 45px; height: 45px; position: relative">
                      <van-image
                          width="45"
                          height="45"
                          fit="cover"
                          :src="JSON.parse(item.content).icon"/>
                      <div class="imgbox"></div>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </van-pull-refresh>
      </div>
      <!--聊天输入框-->
      <div class="messagebox-footer" ref="footer">
        <van-row style="padding: 0px 10px; display: flex; align-items: center; "
                 :style="{'padding-bottom': isApple ? '20px' : '5px'}">
          <van-col span="4" v-if="officialAccountType !== 'PERSONAL_SERVICE_NUMBER'"
                   style="display: flex; align-items: center; justify-content: center">
            <img :src="voiceImg"
                 @click='VoiceChange()'
                 width="32px" height="32px">
          </van-col>
          <van-col :span="officialAccountType !== 'PERSONAL_SERVICE_NUMBER' ? 14 : 18">
            <div class="fotter-send">
              <van-field
                  v-if="!this.isVoice"
                  ref="txtDom"
                  id="caring_im_input"
                  v-model="message"
                  rows="1"
                  autosize
                  :style="{'font-size': getIos() ? '16px' : '15px'}"
                  @input="inputChange()"
                  class="sengTxt"
                  type="textarea"
                  placeholder="请输入"
              />
              <div v-else
                   @touchstart="gotouchstart" @touchend="gotouchend"
                   class="recording"
                   :style="{'background-color': isRecording ? '#D6D6D6' : '#FFF'}">
                <a>按住说话</a>
              </div>
            </div>
          </van-col>

          <van-col span="7"
                   style="display: flex; align-items: center; justify-content: center">
            <van-uploader style="display: flex; align-items: center; justify-content: center; margin-right: 7px"
                          :after-read="onUpload">
              <img :src="require('@/assets/my/photo.png')"
                   width="32px" height="28px">
            </van-uploader>

            <img :src="sendImg"
                 style="margin-left: 8px"
                 @click="onSendTextMsg('text', '')"
                 width="32px" height="32px">
          </van-col>
        </van-row>
      </div>
    </div>

    <div class="im-play" v-if="isPlay">
      <img :src="require('@/assets/my/im_play.gif')" width="130" height="130">
    </div>

    <div class="im-recording" v-if="isRecording" @click="stopRecord()">
      <img :src="require('@/assets/my/im_recording.gif')" width="140" height="70">
    </div>

    <loading :show="isSend" @click="isSend = true"/>

    <van-image-preview v-model="imagePreviewShow" :images="images" :showIndex="false">
    </van-image-preview>

    <div v-if="imagePreviewShow" style="position: absolute;top: 50%;z-index: 9999;width: 100%;">
      <div style="width: 40px; float: left; display: flex; justify-content: center" @click="loadImageLeft"
           v-if="isImageLeftShow">
        <van-icon name="arrow-left" size="25px"/>
      </div>

      <div @click="loadImageRight" style="width: 40px; float: right; display: flex; justify-content: center"
           v-if="isImageRightShow">
        <van-icon name="arrow" size="25px"/>
      </div>
    </div>

    <popup v-model="AITShow" position="bottom" max-height="60%" @on-hide="AITType = 0">
      <group>
        <cell v-for="(item, i) in popupList" :key="i" :title="item.name" is-link @click.native="popupItem(item)">
          <img slot="icon" width="40" style="display:block;margin-right:5px;"
               :src="(item.avatar? item.avatar : require('@/assets/my/doctor_avatar.png'))">
        </cell>
      </group>
      <div style="padding: 15px;">
        <x-button @click.native="AITShow = false">取消</x-button>
      </div>
    </popup>
  </div>
</template>

<script src="@/view/im/indexJS.js">

</script>

<style lang="less" scoped src="./imIndex.less">

</style>
