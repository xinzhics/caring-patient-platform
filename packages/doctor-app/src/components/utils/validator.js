import Vue from "vue";
import VeeValidate, { Validator } from 'vee-validate';


Validator.extend('alpha_zh', {
    getMessage: field => field + '只能填写中文',
    validate: value => {
        return /^[\u4e00-\u9fa5]+$/.test(value)
    }
});
Validator.extend('phone', {
    getMessage: field => '请填写正确的手机号',
    validate: value => {
        return /^1[3456789]\d{9}$/.test(value)
    }
});

Validator.extend('idcard', {
    getMessage: field => '请填写正确的身份证号',
    validate: value => {
        return /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/.test(value)
    }
});
Validator.extend('bankCard', {
    getMessage: field => '请填写正确的银行卡号',
    validate: value => {
        return /^([1-9]{1})(\d{14}|\d{18})$/.test(value)
    }
});

Validator.extend('tel', {
    getMessage: field => '请填写正确的座机号码',
    validate: value => {
        return /0\d{2,3}-\d{7,8}/.test(value)
    }
});

const formatFileSize = function (size) {
    let units = ['Byte', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    let threshold = 1024;
    size = Number(size) * threshold;
    let i = size === 0 ? 0 : Math.floor(Math.log(size) / Math.log(threshold));
    return (((size / Math.pow(threshold, i)).toFixed(2) * 1) + " " + (units[i]));
}
const fieldName = '' // 原来的i18n的fieldName会显示绑定的name值，就是英文，实际项目中不需要

// Override and merge the dictionaries
const dictionary = {
    name: "zh_CN",
    attributes: {},
    messages: {
        _default: () => `${fieldName}无效`,
        after: (field, [target]) => `${fieldName}必须在${target}之后`,
        alpha_dash: () => `${fieldName}能够包含字母数字字符、破折号和下划线`,
        alpha_num: () => `${fieldName}只能包含字母数字字符`,
        alpha_spaces: () => `${fieldName}只能包含字母字符和空格`,
        alpha: () => `${fieldName}只能包含字母字符`,
        before: (field, [target]) => `${fieldName}必须在${target}之前`,
        between: (field, [min, max]) => `${fieldName}必须在${min}与${max}之间`,
        confirmed: (field, [confirmedField]) => `${fieldName}不能和${confirmedField}匹配`,
        credit_card: () => `${fieldName}格式错误`,
        date_between: (field, [min, max]) => `${fieldName}必须在${min}和${max}之间`,
        date_format: (field, [format]) => `${fieldName}必须符合${format}格式`,
        decimal: (field, [decimals = '*'] = []) => `${fieldName}必须是数字，且能够保留${decimals === '*' ? '' : decimals}位小数`,
        digits: (field, [length]) => `${fieldName}必须是数字，且精确到${length}位数`,
        dimensions: (field, [width, height]) => `${fieldName}必须在${width}像素与${height}像素之间`,
        email: () => `${fieldName}不是一个有效的邮箱`,
        ext: () => `${fieldName}不是一个有效的文件`,
        image: () => `${fieldName}不是一张有效的图片`,
        included: () => `${fieldName}不是一个有效值`,
        integer: () => `${fieldName}必须是整数`,
        ip: () => `${fieldName}不是一个有效的地址`,
        length: (field, [length, max]) => {
            if (max) {
                return `${fieldName}长度必须在${length}到${max}之间`
            }
            return `${fieldName}长度必须为${length}`
        },
        max: (field, [length]) => `${fieldName}不能超过${length}个字符`,
        max_value: (field, [max]) => `${fieldName}必须小于或等于${max}`,
        mimes: () => `${fieldName}不是一个有效的文件类型`,
        min: (field, [length]) => `${fieldName}必须至少有${length}个字符`,
        min_value: (field, [min]) => `${fieldName}必须大于或等于${min}`,
        excluded: () => `${fieldName}不是一个有效值`,
        numeric: () => `${fieldName}只能包含数字字符`,
        regex: () => `${fieldName}格式无效`,
        required: () => `${fieldName}不能为空`,
        size: (field, [size]) => `${fieldName}必须小于${formatFileSize(size)}`,
        url: () => `${fieldName}不是一个有效的url`,
        alpha_zh: () => `${fieldName}只能填写中文`,
        phone: () =>'请填写正确的手机号',
        tel: () =>'请填写正确的座机号码',
        idcard:()=>'请填写正确的身份证号码',
        bankCard:()=>'请填写正确的银行卡号码',
    }
};

Validator.localize("zh_CN", dictionary);
const config = {
    locale: 'zh_CN',
};

Vue.use(VeeValidate, config)