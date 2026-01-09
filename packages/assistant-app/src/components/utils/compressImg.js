import Compressor from 'compressorjs'
// 压缩图片
export function compressionImg (file) {
  let fileSize = parseFloat(parseInt(file['size']) / 1024 / 1024).toFixed(2)
  let read = new FileReader()
  read.readAsDataURL(file)
  return new Promise(function (resolve, reject) {
    const comp = new Compressor(file, {
      quality: fileSize > 6 ? 0.6 : 0.8, // 压缩质量 可以查看文档获取明细
      success (result) {
        console.log('base64', result)
        let resultFile = new window.File([this.result], file.name, {type: file.type})
        console.log('base64', resultFile)
        resolve(resultFile)
      }
    })
    return comp
  })
}
