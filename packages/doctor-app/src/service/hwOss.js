var HWOssUtil = {};
import ObsClient from 'esdk-obs-browserjs'

const key = 'e0fafd7cfb9740f8'
/**
 *
 * @param file 上传的文件
 * @param progressCallback 上传的进度信息
 * @param resultCallback 上传的结果信息 (返回的文件信息， 错误信息 )
 */
export const uploadHwFile = async function(iv, config, file, progressCallback, resultCallback) {

  const decrypted = await decryptAES(config, key, iv);
  const hwConfig = JSON.parse(decrypted)
  const obsClient = new ObsClient({
    access_key_id: hwConfig.accessKey,
    secret_access_key: hwConfig.securityKey,
    server: 'https://obs.cn-north-4.myhuaweicloud.com'
  })

  const fileName = file.name
  let name = fileName.substring(0, fileName.lastIndexOf('.'))
  name = name.replace(/\s*/g, '')
  name = name.replace(/-/g, '')
  name = name.replace(/\+/g, '')
  const suffix = fileName.substring(fileName.lastIndexOf('.'))
  let objectname = name + 'D' + new Date().getTime() + suffix
  if (!/^[0-9a-zA-Z._-]+$/.test(name)) {
    // 正则校验，如果名称中有特殊符号，则重命名
    objectname = 'D' + new Date().getTime() + suffix
  }
  const fileHwUrl = 'https://caring.obs.cn-north-4.myhuaweicloud.com/cms/' + objectname

  obsClient.putObject({
    Bucket: 'caring',
    Key: 'cms/' + objectname,
    SourceFile: file,
    ProgressCallback: progressCallback
  }, (err, result) => {
    if (err) {
      resultCallback(null, '上传失败')
    } else {
      if (result.CommonMsg.Status === 200) {
        const params = { bucket: 'caring', fileName: objectname, object: 'cms/' + objectname, fileHwUrl: fileHwUrl }
        resultCallback(params)
      }
    }
  })
}


// 工具函数：将 UTF-8 字符串转为 Uint8Array
function utf8ToUint8Array(str) {
  return new TextEncoder().encode(str);
}

// 工具函数：将 Base64 字符串转为 Uint8Array
function base64ToUint8Array(base64) {
  const binaryString = atob(base64);
  const bytes = new Uint8Array(binaryString.length);
  for (let i = 0; i < binaryString.length; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }
  return bytes;
}

// 工具函数：将 Uint8Array 转为 Base64 字符串
function uint8ArrayToBase64(bytes) {
  let binary = '';
  bytes.forEach(byte => binary += String.fromCharCode(byte));
  return btoa(binary);
}

// 导入密钥（必须与 Java 一致）
async function importKey(keyStr) {
  const keyBytes = utf8ToUint8Array(keyStr); // UTF-8 编码
  return await crypto.subtle.importKey(
    'raw',
    keyBytes,
    { name: 'AES-CBC' },
    false,
    ['encrypt', 'decrypt']
  );
}

// 加密
async function encryptAES(plainText, keyStr, ivStr) {
  const key = await importKey(keyStr);
  const iv = utf8ToUint8Array(ivStr);
  const data = utf8ToUint8Array(plainText);

  const encrypted = await crypto.subtle.encrypt(
    { name: 'AES-CBC', iv: iv },
    key,
    data
  );

  return uint8ArrayToBase64(new Uint8Array(encrypted));
}

// 解密
async function decryptAES(encryptedBase64, keyStr, ivStr) {
  const key = await importKey(keyStr);
  const iv = utf8ToUint8Array(ivStr);
  const encryptedData = base64ToUint8Array(encryptedBase64);

  const decrypted = await crypto.subtle.decrypt(
    { name: 'AES-CBC', iv: iv },
    key,
    encryptedData
  );

  return new TextDecoder().decode(decrypted);
}



