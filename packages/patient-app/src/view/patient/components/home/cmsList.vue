<template>
  <div>
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" v-if="contentList.length > 0">
      <van-list
        v-model="loading"
        :finished="finished"
        :finished-text="finishedText"
        @load="onLoad"
      >
        <div
          v-for="cms in contentList"
          :key="cms.id"
          class="cms-item"
          @click="onCmsClick(cms.studioCms)"
        >
          <div class="cms-header">
            <div style="display: flex; align-items: center; gap: 7px; min-width: 0">
              <span class="cms-type">{{ getCmsTypeText(cms.studioCms.cmsType) }}</span>
              <div class="cms-title">{{ cms.studioCms.cmsTitle }}</div>
            </div>

            <van-icon name="arrow" color="#888888"/>
          </div>

          <!-- 文章内容预览 -->
          <div v-if="cms.studioCms.cmsType === 'CMS_TYPE_TEXT' && cms.studioCms.cmsContent" class="cms-content" style="margin-top: 8px;">
            <div class="cms-preview" v-html="cms.studioCms.cmsContent"></div>
          </div>
        </div>

      </van-list>
    </van-pull-refresh>
    <div v-if="contentList.length === 0 && !loading" class="empty-state">
      <div class="empty-icon">
        <img src="../../../../assets/cms_empty_icon_.png" style="width: 160px;"/>
      </div>
      <div class="empty-text">当前暂无内容</div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue';
import {myStudioContentReply, studioCollectPage} from '@/api/cms'
import {List, PullRefresh, Lazyload} from 'vant';
Vue.use(Lazyload);
Vue.use(List);
Vue.use(PullRefresh);

export default {
  name: "cmsList",
  props: {
    types: {
      type: String,
    }
  },
  data() {
    return {
      params: {
        "map": {},
        "model": {
          "userId": localStorage.getItem('userId'),
          collectStatus:1,
          "roleType": 'patient'
        },
        "order": "descending",
        "size": 20,
        "sort": "id"
      },
      current: 1,
      contentList: [],
      loading: false,
      finishedText: this.$lang('langurage.no_more', '没有更多了'),
      finished: false,
      refreshing: false,
    }
  },
  mounted() {
    this.onRefresh()
  },
  methods: {
    getCmsTypeText(cmsType) {
      switch (cmsType) {
        case 'CMS_TYPE_TEXT':
          return '文章'
        case 'CMS_TYPE_VOICE':
          return '音频'
        case 'CMS_TYPE_VIDEO':
          return '视频'
        default:
          return '未知'
      }
    },
    onCmsClick(cms) {
      let routePath = '/studio/cms/textDetail';
      if (cms.cmsType === 'CMS_TYPE_VIDEO') {
        // 视频
        routePath = '/studio/cms/videoDetail';
      } else if (cms.cmsType === 'CMS_TYPE_VOICE') {
        // 视频
        routePath = '/studio/cms/voiceDetail';
      }

      this.$router.push({
        path: routePath,
        query: {
          cmsId: cms.id
        }
      })
    },
    onLoad() {
      console.log('this.refreshing',this.refreshing)
      setTimeout(()=>{
        if (this.refreshing) {
          this.contentList = [];
          this.current = 1
          this.refreshing = false;
        }
        if (this.types === 'collect') {
          console.log('我是收藏')
          this.getInfo()
        } else {
          console.log('我是评论',this.current)
          this.getComment()
        }
      },500)
    },
    onRefresh() {
      this.current = 1
      this.loading = true;
      this.finished = false;
      this.onLoad();
    },
    // 收藏
    getInfo() {
      this.params.current = this.current
      studioCollectPage(this.params).then((res) => {
        if (res.data.code === 0) {
          console.log('this.current',this.current)
          if (this.current === 1) {
            this.contentList = res.data.data.records
          } else {
            this.contentList = this.contentList.concat(res.data.data.records)
          }
          console.log('this.contentList', this.contentList)
          this.current++
          this.loading = false;
          console.log(this.current >=res.data.data.pages)
          if (this.current >= res.data.data.pages) {
            this.finished = true;
          }
        }
      })
    },
    // 点赞
    getComment() {
      const params = {
        "current": this.current,
        "map": {},
        "model": {
          "creplierId": localStorage.getItem('userId')
        },
        "order": "descending",
        "size": 20,
        "sort": "id"
      }
      myStudioContentReply(params).then(res => {
        if (res.data.code === 0) {
          if (this.current === 1) {
            this.contentList = res.data.data.records
          } else {
            this.contentList = this.contentList.concat(res.data.data.records)
          }
          this.current++
          this.loading = false;
          console.log('this.contentList', this.contentList)
          if (this.current >= res.data.data.pages) {
            this.finished = true;
          }
        }
      })
    },
  }
}
</script>

<style scoped lang="less">
.card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #F5F5F5;
  padding: 11px 0;
}

.text {
  width: 171px;
  overflow: hidden;
  text-overflow: ellipsis; //文本溢出显示省略号
  white-space: nowrap
}

.cms-item {
  border-radius: 12px;
  padding: 12px 12px;
  margin-bottom: 12px;
  border: 1px solid #E8EAF2;
}

/* 空数据状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 20px;
  text-align: center;
}

.empty-icon {
  margin-bottom: 5px;
}

.empty-text {
  height: 21px;
  font-family: PingFangSC, PingFang SC;
  font-weight: 400;
  font-size: 15px;
  color: #939393;
  line-height: 21px;
  text-align: center;
  font-style: normal;
}

.cms-header {
  display: flex;
  align-items: center;
  width: 100%;
  justify-content: space-between;
  gap:5px;
}

.cms-title {
  font-size: 14px;
  color: #1A1A1A;
  margin: 0;
  flex: 1;
  min-width: 0;
  line-height: 1.4;
  white-space: nowrap;        /* 不换行 */
  overflow: hidden;           /* 超出部分隐藏 */
  text-overflow: ellipsis;    /* 超出部分显示省略号（可选） */
}

.cms-type {
  color: #4562BA;
  border: 1px solid #4562BA;
  padding: 1px 4px;
  border-radius: 6px;
  font-size: 11px;
  white-space: nowrap;
}

.cms-content {
}

.cms-preview {
  font-size: 12px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  margin-top: 10px;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
