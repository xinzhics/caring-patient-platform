
import {
	Button,
	Field,
	Tab,
	Tabs,
	CellGroup,
	Uploader,
	Sticky,
	Step,
	Steps,
	Icon,
	Dialog,
	Checkbox,
	CheckboxGroup,
	Empty,
	Divider,
	List,
	Cell,
	PullRefresh,
	ActionSheet,
	Badge,
	Loading,
	Toast,
	Popup,
	ImagePreview
} from 'vant'

const vant = {
	install(Vue: any) {
		Vue.use(Button)
			.use(Field)
			.use(Tab)
			.use(Tabs)
			.use(CellGroup)
			.use(Uploader)
			.use(Sticky)
			.use(Step)
			.use(Steps)
			.use(Icon)
			.use(Checkbox)
			.use(CheckboxGroup)
			.use(Empty)
			.use(Divider)
			.use(List)
			.use(Cell)
			.use(Badge)
			.use(ActionSheet)
			.use(PullRefresh)
			.use(Loading)
			.use(Toast)
			.use(Popup)
			.use(ImagePreview)

			.use(Dialog)
	},
}

export default vant
