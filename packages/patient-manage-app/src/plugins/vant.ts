import { App } from 'vue';
import { 
  Button, 
  Cell, 
  Field, 
  Popup, 
  Picker, 
  DatetimePicker,
  List,
  Toast,
  Dialog,
  Sticky,
  Search
  // 根据实际需要添加更多组件
} from 'vant';
import 'vant/lib/index.less'; // 或者使用 'vant/lib/index.css'

export const vantPlugins = {
  install(app: App) {
    app.use(Button);
    app.use(Cell);
    app.use(Field);
    app.use(Popup);
    app.use(Picker);
    app.use(DatetimePicker);
    app.use(List);
    app.use(Toast);
    app.use(Dialog);
    app.use(Sticky);
    app.use(Search);
    // 添加更多组件...
  },
};

// 单独导出特定组件，以便在需要时单独导入
export {
  Button,
  Cell,
  Field,
  Popup,
  Picker,
  DatetimePicker,
  List,
  Toast,
  Dialog,
  Sticky,
  Search
};