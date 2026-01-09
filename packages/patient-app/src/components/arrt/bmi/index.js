import Vue from 'vue';
import {
  Picker,
  Button
} from 'vant';

Vue.use(Picker);
Vue.use(Button);


export default {
  data() {
    return {
      weight: undefined,
      height: undefined,
    };
  },
  props: {
    field: {
      type: Object,
      default: {}
    }
  },
  
}
