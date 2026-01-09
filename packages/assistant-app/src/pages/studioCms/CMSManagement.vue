<template>
  <div class="doctor-management-page">
    <van-nav-bar
        title="选择医生"
        left-arrow
        fixed
        placeholder
    >
      <template slot="left">
        <van-icon name="arrow-left" @click="onBack" style="color: #2c3e50"/>
      </template>
    </van-nav-bar>

    <div class="doctor-list-container">
      <van-list
          v-model="loading"
          :finished="finished"
          @load="onLoadMore"
      >
        <div
            v-for="doctor in doctorList"
            :key="doctor.id"
            class="doctor-item"
            @click="onDoctorClick(doctor)"
        >
          <van-image
              round
              width="40px"
              height="40px"
              :src="doctor.avatar || defaultAvatar"
              class="doctor-avatar-img"
          />
          <div class="doctor-info">
            <div class="doctor-name">{{ doctor.name }}</div>
          </div>
          <van-icon name="arrow" class="arrow-icon"/>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script>
import { doctorPage } from '@/api/doctorApi.js'
import Vue from 'vue'
import { NavBar, List, Image as VanImage } from 'vant'
Vue.use(NavBar)
Vue.use(List)
Vue.use(VanImage)

export default {
  name: 'CMSManagement',
  data () {
    return {
      searchQuery: '',
      doctorList: [],
      loading: false,
      finished: false,
      page: 0,
      pageSize: 500,
      nursingId: localStorage.getItem('caringNursingId'),
      debounceTimer: null,
      defaultAvatar: 'https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/doctor_default_avator.png',
      params: {
        current: 1,
        model: {
          nursingId: localStorage.getItem('caringNursingId'),
          noNeedCountPatient: true,
          currentUserType: 'NursingStaff'
        }
      }
    }
  },

  watch: {
    searchQuery: {
      handler (newVal) {
        if (newVal === '') {
          this.performSearch()
        } else {
          this.debouncedPerformSearch()
        }
      },
      immediate: false
    }
  },
  methods: {
    // 防抖函数（作为方法实现）
    debouncedPerformSearch () {
      if (this.debounceTimer) {
        clearTimeout(this.debounceTimer)
      }
      this.debounceTimer = setTimeout(() => {
        this.performSearch()
      }, 500)
    },

    performSearch () {
      this.finished = false
      this.loading = true
      this.page = 1
      this.onLoadMore()
    },

    onDoctorClick (doctor) {
      this.$router.push({
        path: '/studio/cms/list',
        query: { doctorId: doctor.id }
      })
    },

    onLoadMore () {
      this.loading = true

      if (this.params.current === 1) {
        this.doctorList = []
      }

      doctorPage(this.params)
        .then(res => {
          if (res.data.records.length < 1) {
            this.show = false
            this.finished = true
          } else {
            if (res.data) {
              this.doctorList.push(...res.data.records)
              if (res.data.pages === 0 || res.data.pages === this.params.current) {
                this.finished = true
              } else {
                this.finished = false
                this.params.current++
              }
            }
          }
          this.loading = false
          this.refreshing = false
        })
        .catch(() => {
          this.finished = true
        })
        .finally(() => {
          this.loading = false
        })
    },

    onBack () {
      this.$h5Close()
    }
  },

  mounted () {
    // 如果需要初始加载，可以在这里调用
    this.performSearch()
    localStorage.setItem('cmsReleaseStatus', 1)
  }
}
</script>

<style scoped>
.doctor-management-page {
  background-color: #f7f8fa;
  min-height: 100vh;
}

.search-container {
  padding: 10px 16px;
  background-color: #fff; /* As per image, search is on white bg */
}

/* Style van-search to match the image more closely if needed */
:deep(.van-search) {
  padding: 0; /* Remove default padding of van-search itself */
}

:deep(.van-search__content) {
  background-color: #f7f8fa; /* Search input background */
}

.doctor-list-container {
  padding: 0 16px;
}

.doctor-item {
  display: flex;
  align-items: center;
  padding: 12px 0; /* Vertical padding for items */
  background-color: #fff;
  border-radius: 8px; /* Rounded corners for each item card */
  margin-bottom: 15px; /* Space between items */
  padding-left: 16px; /* Padding inside the card */
  padding-right: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05); /* Subtle shadow */
}

.doctor-item:first-child {
  margin-top: 16px; /* More space for the first item from search bar */
}

.doctor-avatar-img {
  margin-right: 12px;
}

.doctor-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.doctor-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.doctor-signup-time {
  font-size: 12px;
  color: #969799;
}

.arrow-icon {
  color: #969799; /* Arrow color */
  font-size: 16px;
}

:deep(.van-nav-bar .van-icon) {
  color: #000000; /* 返回箭头颜色 */
  font-size: 20px; /* 返回箭头大小 */
}

:deep(.van-hairline--bottom:after) {
    border-bottom-width: 0px;
}

:deep(.van-nav-bar) {
    background: #f7f8fa;
}

/deep/ .van-nav-bar .van-icon {
  color: #000000;
  font-size: 20px;
}
</style>
