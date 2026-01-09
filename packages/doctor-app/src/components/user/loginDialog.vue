<script>
import Vue from 'vue'
import LoginComponent from './login'

function createDialogVM(Vue){
	if (typeof document === 'undefined') {
	    console.error('[VUX] Alert plugin cannot be used in ssr.')
	    return
	  }
	
	  const Login = Vue.extend(LoginComponent);

	  const $vm = new Login({
		    el: document.createElement('div')
		  })
	  document.body.appendChild($vm.$el)
	  return $vm
}

let $vm
export default {
	install(Vue) {
	    if (!$vm) {
	    	$vm = createDialogVM(Vue)
	    }
		Vue.toLogin = function(){
			$vm.show = true
		}
	}
}
 
export const test = "asd";


</script>