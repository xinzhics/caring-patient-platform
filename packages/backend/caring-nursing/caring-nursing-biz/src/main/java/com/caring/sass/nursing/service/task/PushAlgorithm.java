package com.caring.sass.nursing.service.task;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 推送算法
 *
 * @author xinzh
 */
@Slf4j
public class PushAlgorithm {

    /**
     * 完成入组时间
     */
    private final LocalDateTime completeEnterGroupTime;

    /**
     * 第几天开始执行
     */
    private final Integer execute;

    /**
     * 触发前？触发后？几天？
     */
    private final Integer preDays;

    /**
     * 推送频率(0:单次 )
     */
    private final Integer frequency;

    /**
     * 有效时间（0：长期  N：具体天数）
     */
    private final Integer effectiveTime;

    /**
     * 截止日期
     */
    private final LocalDate currentDay;

    public PushAlgorithm(Builder builder) {
        this.completeEnterGroupTime = builder.completeEnterGroupTime;
        this.execute = builder.execute;
        this.preDays = builder.preDays;
        this.frequency = builder.frequency;
        this.effectiveTime = builder.effectiveTime;
        this.currentDay = builder.currentDay;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        /**
         * 完成入组时间
         */
        private LocalDateTime completeEnterGroupTime;

        /**
         * 第几天开始执行
         */
        private Integer execute;

        /**
         * 触发前？触发后？几天？
         */
        private Integer preDays;

        /**
         * 推送频率(0:单次 )
         */
        private Integer frequency;

        /**
         * 有效时间（0：长期  N：具体天数）
         */
        private Integer effectiveTime;

        private LocalDate currentDay;

        public Builder() {

        }

        public Builder completeEnterGroupTime(LocalDateTime completeEnterGroupTime) {
            this.completeEnterGroupTime = completeEnterGroupTime;
            return this;
        }

        public Builder execute(Integer execute) {
            this.execute = execute;
            return this;
        }

        public Builder preDays(Integer preDays) {
            this.preDays = preDays;
            return this;
        }

        public Builder frequency(Integer frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder effectiveTime(Integer effectiveTime) {
            this.effectiveTime = effectiveTime;
            return this;
        }

        public Builder currentDay(LocalDate currentDay) {
            this.currentDay = currentDay;
            return this;
        }

        //构建一个实体
        public PushAlgorithm build() {
            this.execute = execute == null ? 1 : execute;
            this.preDays = preDays == null ? 0 : preDays;
            this.frequency = frequency == null ? 0 : frequency;
            this.effectiveTime = effectiveTime == null ? 0 : effectiveTime;
            this.currentDay = currentDay == null ? LocalDate.now() : currentDay;
            return new PushAlgorithm(this);
        }
    }

    public boolean whetherPush() {
        if (completeEnterGroupTime == null) {
            return false;
        }

        // 推送日期
        LocalDate pushDay = completeEnterGroupTime.toLocalDate().plusDays(preDays).plusDays(execute);
        LocalDate now;
        if (currentDay == null) {
            now = LocalDate.now();
        } else {
            now = currentDay;
        }
        long days = DateUtils.getDays(now, pushDay);
        // 是否在有效推送时间内
        if (effectiveTime != 0 && days >= effectiveTime) {
            return false;
        }

        try {
            // 单次推送
            if (frequency == 0) {
                boolean isEqual = now.isEqual(pushDay);
                if (isEqual) {
                    log.debug("该护理计划模板消息被设置为单次执行，即在入组后第{}天开始执行，并只执行一次即停止，实际执行日期应为:{}，而今日时间为：{}，正好相匹配，开始进行发送",
                            execute, pushDay.toString(), now.toString());
                } else {
                    log.debug("该护理计划模板消息被设置为单次执行，即在入组后第{}天开始执行，并只执行一次即停止，实际执行日期应为：{}，而今日时间为：{}，日期不匹配，不进行发送",
                            execute, pushDay.toString(), now.toString());
                }
                return isEqual;
            }

            // 多次推送
            long i = days % frequency;
            if (i == 0 && (now.isEqual(pushDay) || now.isAfter(pushDay))) {
                log.debug("该护理计划模板消息被设置为周期性执行，即在入组后第{}天开始执行，每隔{}天执行一次，第一次执行时间为：{}，而今日时间为：{}，期间相差了{}天，正好是{}的倍数，因此今日应该执行。",
                        execute, frequency, pushDay.toString(), now.toString(), days, frequency);
                return true;
            }
            log.debug("该护理计划模板消息被设置为周期性执行，即在入组后第{}天开始执行，每隔{}天执行一次，第一次执行时间为：{}，而今日时间为：{}，期间相差了{}天，不是{}的倍数，因此时不会执行推送",
                    execute, frequency, pushDay.toString(), now.toString(), days, frequency);
            return false;
        } catch (Exception e) {
            log.error("在计算推送时间时出错了，因此将不会继续推送", e);
            return false;
        }
    }

    public static void testZhiLiao() {
        LocalDateTime completeEnterGroupTime = LocalDateTime.of(2023, 7, 1, 12, 50, 50);
        LocalDate testPushTime = LocalDate.of(2023, 7, 1);
        System.out.println("入组时间"+ completeEnterGroupTime.toLocalDate().toString() );
        // 第几天开始执行
        int execute = 7;
        // 有效时间（0：长期  N：具体天数）
        Integer effectiveTime = 30;

        // 触发前？触发后？几天？
        int preDays = 0;
        // 推送频率(0:单次 )
        int frequency = 1;

        for (int i = 0; i < 50; i++) {
            testPushTime = testPushTime.plusDays(1);
            // 推送日期
            LocalDate pushDay = completeEnterGroupTime.toLocalDate().plusDays(preDays).plusDays(execute);
            LocalDate now = testPushTime;
            long days = DateUtils.getDays(now, pushDay);
            // 是否在有效推送时间内
            if (effectiveTime != 0 && days >= effectiveTime) {
                continue;
            }
            try {
                // 单次推送
                if (frequency == 0) {
                    boolean isEqual = now.isEqual(pushDay);
                    if (isEqual) {
                        System.out.println(testPushTime.toString());
                    } else {

                    }
                    continue;
                }

                // 多次推送
                long k = days % frequency;
                if (k == 0 && (now.isEqual(pushDay) || now.isAfter(pushDay))) {
                    System.out.println(testPushTime.toString());
                }
            } catch (Exception e) {
            }

        }
    }

    public static void testShaiXuan() {
        LocalDateTime completeEnterGroupTime = LocalDateTime.of(2023, 7, 1, 12, 50, 50);
        LocalDate testPushTime = LocalDate.of(2023, 7, 1);
        System.out.println("入组时间"+ completeEnterGroupTime.toLocalDate().toString() );
        // 第几天开始执行
        int execute = 1;
        // 有效时间（0：长期  N：具体天数）
        Integer effectiveTime = 7;

        // 触发前？触发后？几天？
        int preDays = 0;
        // 推送频率(0:单次 )
        int frequency = 1;

        for (int i = 0; i < 33; i++) {
            testPushTime = testPushTime.plusDays(1);
            // 推送日期
            LocalDate pushDay = completeEnterGroupTime.toLocalDate().plusDays(preDays).plusDays(execute);
            LocalDate now = testPushTime;
            long days = DateUtils.getDays(now, pushDay);
            // 是否在有效推送时间内
            if (effectiveTime != 0 && days >= effectiveTime) {
                continue;
            }
            try {
                // 单次推送
                if (frequency == 0) {
                    boolean isEqual = now.isEqual(pushDay);
                    if (isEqual) {
                        System.out.println(testPushTime.toString());
                    } else {

                    }
                    continue;
                }
                // 多次推送
                long k = days % frequency;
                if (k == 0 && (now.isEqual(pushDay) || now.isAfter(pushDay))) {
                    System.out.println(testPushTime.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    public static void testSuiFang() {
        LocalDateTime completeEnterGroupTime = LocalDateTime.of(2023, 11, 30, 12, 50, 50);
        LocalDate testPushTime = LocalDate.of(2023, 12, 1);
        System.out.println("入组时间"+ completeEnterGroupTime.toLocalDate().toString() );
        // 第几天开始执行
        int execute = 10;
        // 有效时间（0：长期  N：具体天数）
        Integer effectiveTime = 3;

        // 触发前？触发后？几天？
        int preDays = 0;
        // 推送频率(0:单次 )
        int frequency = 1;
        LocalDate pushDay = completeEnterGroupTime.toLocalDate().plusDays(preDays).plusDays(execute);
        for (int i = 0; i < 30; i++) {
            testPushTime = testPushTime.plusDays(1);
            long days = DateUtils.getDays(testPushTime, pushDay);
            // 是否在有效推送时间内
            if (effectiveTime != 0 && days >= effectiveTime) {
                continue;
            }
            try {
                // 单次推送
                if (frequency == 0) {
                    boolean isEqual = testPushTime.isEqual(pushDay);
                    if (isEqual) {
                        System.out.println(testPushTime.toString());
                    } else {

                    }
                    continue;
                }
                // 多次推送
                long k = days % frequency;
                if (k == 0 && (testPushTime.isEqual(pushDay) || testPushTime.isAfter(pushDay))) {
                    System.out.println(testPushTime.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) {
        testSuiFang();
    }

}
