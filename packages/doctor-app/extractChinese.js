const fs = require('fs');
const path = require('path');
const chineseRegex = /[\u4e00-\u9fa5]+/g;
const chineseRegex1 = /["']([\u4e00-\u9fa5]+)["']/g;

function extractChineseFromFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf-8');
  // 使用正则表达式匹配内容，并提取中文字符
  const matches = content.match(chineseRegex1) || [];

  // 去除匹配结果中的引号
  const chineseWords = matches.map(word => word.replace(/^["'](.*)["']$/, '$1'));
  return chineseWords || [];
}

function processDirectory(directoryPath) {
  const files = fs.readdirSync(directoryPath);
  const chineseSentences = [];

  files.forEach((fileName) => {
    const filePath = path.join(directoryPath, fileName);
    const stats = fs.statSync(filePath);

    if (stats.isDirectory()) {
      chineseSentences.push(...processDirectory(filePath));
    } else if (stats.isFile() && ['.js', '.vue'].indexOf(path.extname(filePath)) > -1) {
      const chineseWords = extractChineseFromFile(filePath);
      chineseSentences.push(...chineseWords);
    }
  });

  return chineseSentences;
}

function main() {
  const srcDirectory = path.join(__dirname, 'src');
  const componentsDirectory = path.join(srcDirectory, 'components');
  const pagesDirectory = path.join(srcDirectory, 'view');

  const componentsChineseSentences = processDirectory(componentsDirectory);
  const pagesChineseSentences = processDirectory(pagesDirectory);
  const allChineseSentences = [...componentsChineseSentences, ...pagesChineseSentences];

  //const allChineseSentences = componentsChineseSentences;

  const outputPath = path.join(__dirname, 'output.json');
  // 使用 Set 对象来去重
  let backString = Array.from(new Set(allChineseSentences));
  // 对去重后的数组进行排序
  backString.sort();
  fs.writeFileSync(outputPath, JSON.stringify(backString, null, 2), 'utf-8');

  console.log('提取到的中文单词或语句已保存到output.json文件中。');
}

main();
