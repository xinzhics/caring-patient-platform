
export default{
	$vm = "",
	install (Vue) {
	    if (!$vm) {
	      $vm = createVM(Vue)
	    }
	    
	    Vue.createComponent = function(conf){
	    	
	    	
	    };
	}
}


function createVM (Vue) {
	  if (typeof document === 'undefined') {
	    console.error('[VUX] DynamicComponent plugin cannot be used in ssr.')
	    return
	  }
	  const Alert = Vue.extend(AlertComponent)
	  const $vm = new Alert({
	    el: document.createElement('div')
	  })
	  document.body.appendChild($vm.$el)
	  return $vm
	}