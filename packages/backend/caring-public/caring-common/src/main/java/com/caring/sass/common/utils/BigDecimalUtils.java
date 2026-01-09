package com.caring.sass.common.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {


    /**
     * 计算 除数和被除数的占比
     * @param divisor 除数
     * @param dividend 被除数
     * @return
     */
    public static int proportion(BigDecimal divisor, BigDecimal dividend) {
        if (divisor == null || divisor.intValue() == 0) {
            return 0;
        } else if (dividend == null || dividend.intValue() == 0) {
            return 0;
        } else {
            // 计算两个值的商
            BigDecimal divide = divisor.divide(dividend, 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal multiply = divide.multiply(BigDecimal.valueOf(100));
            return multiply.intValue();
        }
    }



    public static void main(String[] args) {
        BigDecimal receiptMember =  new BigDecimal(1);
        BigDecimal pushMember = new BigDecimal(3);
        int proportion = proportion(receiptMember, pushMember);
        System.out.println(proportion);

    }

}
