//压缩图片
function compressImg(file) {
  var fileSize = parseFloat(parseInt(file['size']) / 1024 / 1024).toFixed(2);
  var read = new FileReader();
  read.readAsDataURL(file);
  return new Promise(function (resolve, reject) {
    new Compressor(file, {
      quality:fileSize > 6 ? 0.6 : 0.8, // 压缩质量 可以查看文档获取明细
      success(result) {
        console.log('base64', result)
        let resultFile = new window.File([this.result], file.name, {type: file.type})
        console.log('base64', resultFile)
        resolve(resultFile)
      },
      error(err) {

      }
    })
  })
}

//结尾处将该方法暴露出来供外部调用
export default {
  compressImg
}
