function pageTitleUtil(currentPath) {
  const ruter = JSON.parse(localStorage.getItem("routerData"))
  let routerData =[]
  if (ruter.patientMyFeatures && ruter.patientMyFeatures.length > 0) {
    routerData = routerData.concat(ruter.patientMyFeatures);
  }
  if (ruter.patientMyFile && ruter.patientMyFile.length > 0) {
    routerData = routerData.concat(ruter.patientMyFile);
  }
  console.log('pageTitleUtil', routerData)
  if (currentPath === '/baseinfo/index/history' || currentPath === '/baseinfo/index/history/detailsHistory') {
    return
  }
  if (routerData && routerData.length > 0) {
    const routerDataArray = routerData;
    let tempTitle = ''
    let matchingNumber = 0
    let resultTitle = ''
    let maxMatchingNumber = 0;
    const currentPathList = currentPath.split('/')
    for (let i = 0; i < routerDataArray.length; i++) {
      if (currentPath.startsWith(routerDataArray[i].path)) {
        resultTitle = routerDataArray[i].name;
      } else {
        matchingNumber = 0
        const routerPath = routerDataArray[i].path
        for (let j = 0; j < currentPathList.length; j++) {
          if (currentPathList[j] === '' || currentPathList[j] === '/') {
            continue
          }
          if (routerPath.indexOf(currentPathList[j]) > -1) {
            tempTitle = routerDataArray[i].name
            matchingNumber++
          }
        }
        if (matchingNumber > maxMatchingNumber) {
          maxMatchingNumber = matchingNumber
          resultTitle = tempTitle
        }
      }
    }
    if (resultTitle) {
      return resultTitle;
    }
  }
}

//结尾处将该方法暴露出来供外部调用
export default {
  pageTitleUtil
}
