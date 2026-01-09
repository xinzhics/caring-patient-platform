package com.caring.sass.tenant.utils;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.tenant.dao.SequenceMapper;
import com.caring.sass.tenant.enumeration.SequenceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 序列数生成器
 * 考虑redis实现方式
 *
 * @author xinzh
 */
@Component
public class SequenceNumUtil {

    private static SequenceMapper sequenceMapper;

    @Autowired
    public SequenceNumUtil(SequenceMapper sequenceMapper) {
        SequenceNumUtil.sequenceMapper = sequenceMapper;
    }

    /**
     * 生成不重复递增的序号
     *
     * @param sequenceEnum 序号类型
     * @param length       序号长度
     * @return 序号
     */
    public static String incrSequenceNum(SequenceEnum sequenceEnum, int length) {
        return incrSequenceNum(sequenceEnum, length, "0");
    }

    /**
     * 生成不重复递增的序号
     *
     * @param sequenceEnum 序号类型
     * @param length       序号长度
     * @return 序号
     */
    public static String incrSequenceNum(SequenceEnum sequenceEnum, int length, String fillChar) {
        if (sequenceEnum == null) {
            sequenceEnum = SequenceEnum.OTHER_CODE;
        }
        if (length == 0) {
            length = 1;
        }

        if (StrUtil.isBlank(fillChar)) {
            fillChar = "0";
        }

        Long longCode = sequenceMapper.selectSequenceVar(sequenceEnum);
        String code = Convert.toStr(longCode);
        int codeLength = code.length();
        if (length <= codeLength) {
            return code;
        }

        // 填充0
        int fillLength = length - codeLength;
        String[] s = new String[fillLength];
        for (int i = 0; i < fillLength; i++) {
            s[i] = fillChar;
        }
        return StrUtil.concat(true, s).concat(code);
    }


}
