<template>
  <div class='sidebar-logo-container' :class='{"collapse":collapse}'>
    <transition name='sidebarLogoFade'>
      <router-link v-if='collapse' key='collapse' class='sidebar-logo-link' to='/'>
        <img v-if='logo' :src='logo' class='sidebar-logo'/>
        <h1 v-else class='sidebar-title'>{{ title }}</h1>
      </router-link>
      <router-link v-else key='expand' class='sidebar-logo-link' to='/'>
        <img v-if='logo' :src='logo' class='sidebar-logo'/>
        <h1 class='sidebar-title'>
          <!-- {{ $t('system.title') }} -->
          <img style="width:160px" v-if='logo' :src='logoTitle' class='sidebar-logo'/>
        </h1>/
      </router-link>
    </transition>
  </div>
</template>

<script>
import db from "@/utils/localstorage";
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      logo: db.get('logo', require('@/assets/logo.png'))
    }
  },
  created() {

  },
  computed: {

    // logo() {
    //   return require('@/assets/logo.png')
    // },
    logoTitle() {
      return require('@/assets/logoTitle.png')
    }

  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 54px;
  line-height: 54px;
  background: #393e46;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 12px;
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #fff;
      font-weight: 600;
      line-height: 50px;
      font-size: 19px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}
</style>
