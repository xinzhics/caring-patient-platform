<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">编辑消息</x-header>

    <div style="padding-top: 50px;">
      <tab active-color='#119CFF'>
        <tab-item selected @on-item-click="onItemClick">文字</tab-item>
        <tab-item @on-item-click="onItemClick">图片</tab-item>
        <tab-item @on-item-click="onItemClick">文章</tab-item>
      </tab>
      <div v-if="selectEd===0" style="background:#fff">
        <x-textarea style=" margin: 10px 0px;padding: 0px 20px!important;" :rows="7" autosize :max="200" placeholder="请输入"
                    v-model="content"></x-textarea>
      </div>
      <div v-if="selectEd===1">
        <div v-if="!this.imgurl"
             style="display: flex; align-items: center; justify-content: center; background: white; height: 50px; margin-top: 10px">
          <van-icon name="add-o" style="font-size: 18px"/>
          <van-uploader
            :after-read="onUpload">

            <div style="margin-left: 5px; font-size: 15px; color: #666666">从相册选取</div>
          </van-uploader>
        </div>

        <div v-else style="height: 280px; width: 100%; background: white;  margin-top: 10px; justify-content: center; display: grid;">
          <div style="display: flex; align-items: center;">
            <img :src="this.imgurl" alt="" width="180px" height="180px">
          </div>
          <van-uploader
            style="display: flex; justify-content: center"
            :after-read="onUpload">
            <div class="docs">
              <span >重新选择</span>
            </div>
          </van-uploader>
        </div>

      </div>
      <!--先不需要发送文章-->
      <div v-if="selectEd===2" >
        <div style="border-top: #d9d9d9 solid 1px">
          <tab :line-width=2 active-color='#119CFF' v-model="indexTab">
            <tab-item class="vux-center" :selected="demo2 === item.id" v-for="(item, index) in list2" @click.native="click(item.id)" :key="index">{{item.channelName}}</tab-item>
          </tab>
        </div>

        <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;">
          <van-list
            v-model="loading"
            :finished="finished"
            finished-text="已经没有文章啦"
            @load="onLoad"
          >
            <div class="itemContent" v-for="(x,y) in contentList" :key="y" @click="goItem(x)">
              <div class="contentInner">
                <p class="title">{{x.title}}</p>
                <div class="decs">
                  <p class="time">{{(/\d{4}-\d{1,2}-\d{1,2}/g.exec(x.createTime)[0]).replace(/-/g, '.')}}</p>
                  <div class="watch" v-if="false">
                    <span class="watchOne"><img :src="watch"/>{{x.hitCount?x.hitCount:0}}</span>
                    <span class="watchTwo"><img :src="say"/>{{x.messageNum?messageNum:0}}</span>
                  </div>
                </div>
              </div>
              <img class="itemImg"  :src="x.icon"  width="70px" height="70px"/>
            </div>
          </van-list>
        </van-pull-refresh>
      </div>

      <x-button style="margin-top:40px;width:50%;background-color:#3F86FF" type="primary"
                v-if="selectEd !== 2"
                :disabled="isDisabled"
                @click.native="onCommit">确定
      </x-button>
    </div>

  </section>
</template>
<script>
import {Tab, TabItem, Sticky, Divider, XButton, Swiper, SwiperItem, XTextarea} from 'vux'
import Api from '@/api/Content.js'
import UploadImg from "../../components/utils/UploadImg";
import {Toast, Dialog} from "vant";

export default {
  name: 'edit',
  components: {
    Tab,
    TabItem,
    Sticky,
    Divider,
    XButton,
    Swiper,
    SwiperItem,
    XTextarea,
    [Dialog.name]: Dialog,
  },
  data() {
    return {
      isDisabled: false,
      content: "",
      imgurl: "",
      files: {},
      selectEd: 0,
      patientName: '',
      indexTab: 0,
      list2: [],
      demo2: '',
      patient: this.$getDictItem('patient'),
      myImg: require('@/assets/drawable-xhdpi/setInfo.png'),
      loading: false,
      finished: false,
      refreshing: false,
      contentList: [],
      params: {
        model: {
          ownerType: 'TENANT',
          channelType: "Article",
          doctorOwner: Number(this.$route.query.doctorOwner),
          channelGroupId: this.$route.query.channelGroupId
        },
        order: "descending",
        size: 20,
        current: 1,
        sort: "id"
      }
    }
  },
  mounted() {
    this.params.model.doctorOwner = 0
    this.getChannelList()
  },
  methods: {
    getChannelList() {
      Api.channelpage(this.params).then((res) => {
        if (res.data.code === 0) {
          const thisObj = {
            channelName: '全部'
          }
          res.data.data.records.unshift(thisObj)
          this.list2 = res.data.data.records
        }
      })
    },
    /*onCancel() {

    },
    onSearch() {

    },*/
    goItem(i) {
      if (i.link) {
        Dialog.confirm({
          title: '群发消息',
          message: '您确定要群发此文章吗？',
        })
          .then(() => {
            let cmsContent = {
              icon: i.icon,
              title: i.title,
              summary: i.summary,
              id: i.id,
              link: i.link,
            }
            this.groupChat(JSON.stringify(cmsContent), 'cms')
          })
          .catch(() => {
            // on cancel
          });
      } else {
        this.$router.push({name: 'cms文章详情', params: {id: i.id, patientId: this.$route.params.patientId}})
      }
    },
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.contentList = [];
          this.params.current = 1;
          this.refreshing = false;
        }
        this.getChannelContentList(this.params)
      }, 500);
    },
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.onLoad();
    },
    getChannelContentList(params) {
      Api.channelContentpage(params).then((res) => {
        if (res.data.code === 0) {
          if (this.params.current === 1) {
            this.contentList = res.data.data.records
          } else {
            this.contentList.push(...res.data.data.records)
          }
          this.params.current++;
          this.loading = false;
          if (res.data.data.records && res.data.data.records.length < 20) {
            this.finished = true;
          }
        }
      })
    },
    click(channelId) {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      if (channelId) {
        this.params.model.channelId = channelId
      }else {
        this.params.model.channelId = 0
      }
      this.refreshing = true
      this.onLoad();
    },
    onItemClick(i) {
      this.selectEd = i
    },
    onCommit() {
      this.isDisabled = true
      //0发送文字
      if (this.selectEd == 0) {
        if (this.content) {
          this.groupChat(this.content, 'text')
        } else {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '请输入群发消息内容'
          })
        }
      } else {
        if (!this.imgurl) {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '请选择图片'
          })
          return
        }

        this.$vux.loading.show({
          text: '上传中'
        })
        let formData = new FormData()
        const isLt2M = this.files.file.size / 1024 / 1024 < 2
        if (!isLt2M) {
          let maxSize = 100 * 1024;
          UploadImg.imageHandle(this.files.file, maxSize).then(file => {
            formData.append('file', file)
            formData.append('folderId', 1308295372556206080)
            Api.updateImg(formData).then((res) => {
              if (res.data.code === 0) {
                this.groupChat(res.data.data.url, 'image')
              } else {
                this.$vux.toast.show({
                  type: 'text',
                  position: 'center',
                  text: '图片上传失败'
                })
              }
              this.$vux.loading.hide()
            })
          })
        } else {
          formData.append('file', this.files.file)
          formData.append('folderId', 1308295372556206080)
          Api.updateImg(formData).then((res) => {
            if (res.data.code === 0) {
              this.groupChat(res.data.data.url, 'image')
            } else {
              this.$vux.toast.show({
                type: 'text',
                position: 'center',
                text: '图片上传失败'
              })
            }
            this.$vux.loading.hide()
          })
        }
      }
      this.isDisabled = false
    },
    //上传图片
    async onUpload(files) {
      if (files) {
        const isJPG = (files.file.type === 'image/jpeg' || files.file.type === 'image/png')
        if (!isJPG) {
          this.$vux.loading.hide()
          Toast('上传图片只能为jpg或者png!')
          return
        }
        this.files = files
        this.imgurl = files.content
      }
    },
    groupChat(content, type) {
      console.log('======', content)
      const params = {
        content: content,
        senderId: localStorage.getItem('caring_doctor_id'),
        receiverId: this.$route.params.patientId,
        type: type,
      }
      Api.sendMoreMessage(params).then(response => {
        this.$vux.toast.show({
          type: 'text',
          position: 'center',
          text: '群发消息成功'
        })
        this.$router.replace('/index')
      })
    },
  }
}

</script>
<style lang="less" scoped>
.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
}

.docs{
  display: flex;
  justify-content: center;
  align-items: center;
  height: 35px;
  span{
    color: #666666;
    font-size: 15px;
    padding: 2px 15px;
    text-align: center;
    border-radius: 20px;
    border:1px solid #B8B8B8
  }
}

.itemContent {
  display: flex;
  line-height: 30px;
  padding: 15px 0px;
  display: flex;
  padding: 15px;
  background: #FFFFFF;
  border-bottom: 1px solid rgba(102, 102, 102, 0.1);

  .contentInner {
    width: 62%;
    margin-right: 3%;

    .title {
      font-size: 15px;
      font-weight: 600;
      line-height: 25px;
      margin-bottom: 16px;
      height: 50px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .decs {
      line-height: 14px;
      font-size: 14px;
      display: flex;
      justify-content: space-between;
      color: #999;

      .watch {
        .watchOne {
          margin-right: 10px;

          img {
            width: 15px;
            vertical-align: middle;
            margin-right: 5px
          }
        }

        .watchTwo {
          img {
            width: 13px;
            vertical-align: middle;
            margin-right: 5px
          }
        }
      }
    }
  }

  .lastContentPages {
    width: 100%;
    text-align: center;
  }

  .itemImg {
    width: 35%;
    height: 80px;
  }
}

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}
</style>
