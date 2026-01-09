package com.caring.sass.nursing.service.exoprt.minning;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.caring.sass.nursing.dto.form.FormFieldDto;
import com.caring.sass.nursing.dto.form.FormFieldExactType;
import com.caring.sass.nursing.dto.form.FormOptions;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.entity.form.FormResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @className: MinNingUrticariaExport
 * @author: 杨帅
 * @date: 2023/11/23
 *
 * 敏宁荨麻疹活动评分
 * 筛选
 * 治疗
 * 随访
 * 三种业务数据按 患者。 评估时间。 导入对应的csv中
 */
@Slf4j
public class MinNingUrticariaExport {


    public static void writeTitleToCsv(OutputStream outputStream) {
        try {
            outputStream.write(String.join(",", new String[]{"序号", "筛选号", "医生名称", "随访期", "评估时间", "量表", "项目", "结果"}).getBytes(StandardCharsets.UTF_8));
            outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("write title error");
        }

    }

    private static Float getQuestionScore(FormFieldDto fieldDto) {
        List<String> resultValues = FormFieldDto.parseResultValues(fieldDto.getValues());
        if (resultValues == null) {
            return null;
        }
        List<FormOptions> formOptions = fieldDto.getOptions();
        Float score = null;
        Map<String, Float> floatMap = formOptions.stream().collect(Collectors.toMap(FormOptions::getId, FormOptions::getScore));
        for (String value : resultValues) {
            if (StrUtil.isEmpty(value)) {
                continue;
            }
            score = floatMap.get(value);
        }
        return score;
    }

    /**
     * 组装 筛选期。 治疗期 的成绩对象
     * @param results
     */
    public static List<UrticariaActivityScoreSelfEvaluation> buildScoreSelfEvaluation(List<FormResult> results) {

        List<UrticariaActivityScoreSelfEvaluation> selfEvaluations = new ArrayList<>();
        UrticariaActivityScoreSelfEvaluation scoreSelfEvaluation;
        for (FormResult result : results) {
            scoreSelfEvaluation = new UrticariaActivityScoreSelfEvaluation();
            scoreSelfEvaluation.setAaluationDate(result.getCreateTime().toLocalDate());
            scoreSelfEvaluation.setAaluationDateTime(result.getCreateTime().withNano(0));
            String jsonContent = result.getJsonContent();
            List<FormFieldDto> fieldDtos = JSONArray.parseArray(jsonContent, FormFieldDto.class);
            // 获取他的评估时间组件
            for (FormFieldDto fieldDto : fieldDtos) {
                if ((FormFieldExactType.SCORING_SINGLE_CHOICE.equals(fieldDto.getExactType()))) {
                    String label = fieldDto.getLabel();
                    if (label.contains("风团数量")) {
                        Float score = getQuestionScore(fieldDto);
                        if (Objects.nonNull(score)){
                            scoreSelfEvaluation.setNumberWindMasses(score.intValue());
                        }
                    } else if (label.contains("瘙痒程度")) {
                        Float score = getQuestionScore(fieldDto);
                        if (Objects.nonNull(score)) {
                            scoreSelfEvaluation.setItchingDegree(score.intValue());
                        }
                    }
                } else if (FormWidgetType.RADIO.equals(fieldDto.getWidgetType())) {
                    String label = fieldDto.getLabel();
                    if (label.contains("治疗期第几针")) {
                        String string = fieldDto.parseResultValues();
                        scoreSelfEvaluation.setNeedleCount(string);
                    }
                }
            }
            selfEvaluations.add(scoreSelfEvaluation);
        }
        return selfEvaluations;
    }





    /**
     * 写入随访数据
     * @param outputStream
     * @param doctorName
     * @param shaiXuanHao
     * @param lifeQuestionnaireList
     */
    public static Integer writeSuiFangDataToCsv(OutputStream outputStream,  String fangShi, String doctorName, String shaiXuanHao, Integer serialNumber, List<ChronicUrticariaQualityLifeQuestionnaire> lifeQuestionnaireList ) throws IOException {
        if (CollUtil.isEmpty(lifeQuestionnaireList)) {
            return serialNumber;
        }
        List<String[]> data = new ArrayList<>();
        String liangbiao = "荨麻疹活动性评分（UAS）自我记录及评价";
        String liangBiao1 = "慢性荨麻疹生活质量问卷（CU-Q2oL ）";
        for (ChronicUrticariaQualityLifeQuestionnaire questionnaire : lifeQuestionnaireList) {
            String string = serialNumber.toString();
            UrticariaActivityScoreSelfEvaluation selfEvaluation = questionnaire.getScoreSelfEvaluation();
            LocalDateTime aaluationDateTime;
            String pingGuTime;
            if (Objects.nonNull(selfEvaluation)) {
                aaluationDateTime = selfEvaluation.getAaluationDateTime();
                String newPingGuTime = aaluationDateTime.toString().replace("T", " ");
                data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, newPingGuTime, liangbiao, "风团数量/24h",
                        selfEvaluation.getNumberWindMasses() != null ? selfEvaluation.getNumberWindMasses().toString() : ""});

                data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, newPingGuTime, liangbiao, "瘙痒程度",
                        selfEvaluation.getItchingDegree() != null ? selfEvaluation.getItchingDegree().toString() : ""});

                data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, newPingGuTime, liangbiao, "总分",
                        selfEvaluation.getTotal() != null ? selfEvaluation.getTotal().toString() : ""});
            } else {
                pingGuTime = questionnaire.getAaluationDate().toString();
                data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangbiao, "风团数量/24h", ""});
                data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangbiao, "瘙痒程度", ""});
                data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangbiao, "总分", ""});
            }
            aaluationDateTime = questionnaire.getAaluationDateTime();
            if (Objects.isNull(aaluationDateTime)) {
                pingGuTime = questionnaire.getAaluationDate().toString();
            } else {
                pingGuTime = aaluationDateTime.toString().replace("T", " ");
            }
            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "1、瘙痒",
                    questionnaire.getItch() != null ? questionnaire.getItch().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "2、风团",
                    questionnaire.getWheal() != null ? questionnaire.getWheal().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "3、眼睛肿胀",
                    questionnaire.getSwollenEyes() != null ? questionnaire.getSwollenEyes().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "4、口唇肿胀",
                    questionnaire.getLipSwelling() != null ? questionnaire.getLipSwelling().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "5、工作",
                    questionnaire.getWork() != null ? questionnaire.getWork().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "6、运动",
                    questionnaire.getMotion() != null ? questionnaire.getMotion().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "7、睡眠",
                    questionnaire.getSleep() != null ? questionnaire.getSleep().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "8、闲暇",
                    questionnaire.getLeisure() != null ? questionnaire.getLeisure().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "9、社交",
                    questionnaire.getSocialize() != null ? questionnaire.getSocialize().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "10、饮食",
                    questionnaire.getDiet() != null ? questionnaire.getDiet().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "11、是否入睡困难",
                    questionnaire.getDifficultyFallingAsleep() != null ? questionnaire.getDifficultyFallingAsleep().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "12、是否会夜间醒来",
                    questionnaire.getWakeUpAtNight() != null ? questionnaire.getWakeUpAtNight().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "13、是否晚上睡眠不佳而白天困乏",
                    questionnaire.getDaytimeFatigue() != null ? questionnaire.getDaytimeFatigue().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "14、是否注意力难以集中",
                    questionnaire.getDifficultyConcentrating() != null ? questionnaire.getDifficultyConcentrating().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "15、是否感觉紧张",
                    questionnaire.getFeelNervous() != null ? questionnaire.getFeelNervous().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "16、是否情绪低落",
                    questionnaire.getDepression() != null ? questionnaire.getDepression().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "17.是否需要限制饮食",
                    questionnaire.getRestrictedDiet() != null ? questionnaire.getRestrictedDiet().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "18、是否身体出现的荨麻疹症状而感到困扰",
                    questionnaire.getFeelingTroubledUrticaria() != null ? questionnaire.getFeelingTroubledUrticaria().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "19、在公共场所是否感到尴尬",
                    questionnaire.getFeelingEmbarrassedPublicPlaces() != null ? questionnaire.getFeelingEmbarrassedPublicPlaces().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "20、是否可以使用化妆品",
                    questionnaire.getUsingCosmetics() != null ? questionnaire.getUsingCosmetics().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "21、是否限制了您对服装类型选择",
                    questionnaire.getRestrictClothingChoices() != null ? questionnaire.getRestrictClothingChoices().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "22、是否因荨麻疹限制了体育运动",
                    questionnaire.getRestrictSportsActivities() != null ? questionnaire.getRestrictSportsActivities().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangBiao1, "23、是否受荨麻疹治疗的不良反应影响",
                    questionnaire.getAdverseEffectsUrticaria() != null ? questionnaire.getAdverseEffectsUrticaria().toString() : ""});

            serialNumber++;
        }
        if (CollUtil.isNotEmpty(data)) {
            for (String[] row : data) {
                outputStream.write(String.join(",", row).getBytes(StandardCharsets.UTF_8));
                outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
        }
        return serialNumber;
    }


    /**
     * 写入筛选或者治疗数据
     * @param outputStream
     * @param doctorName
     * @param shaiXuanHao
     * @param scoreSelfEvaluations
     */
    public static Integer writeDataToCsv(OutputStream outputStream, String fangShi,
                                         String doctorName, String shaiXuanHao,
                                         Integer serialNumber, List<UrticariaActivityScoreSelfEvaluation> scoreSelfEvaluations ) throws IOException {

        if (CollUtil.isEmpty(scoreSelfEvaluations)) {
            return serialNumber;
        }
        String liangbiao = "荨麻疹活动性评分（UAS）自我记录及评价";
        List<String[]> data = new ArrayList<>();
        for (UrticariaActivityScoreSelfEvaluation evaluation : scoreSelfEvaluations) {
            LocalDateTime aaluationDate = evaluation.getAaluationDateTime();
            String pingGuTime = aaluationDate.toString().replace("T", " ");
            String string = serialNumber.toString();
            if (fangShi.equals("治疗期")) {
                data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangbiao, "治疗期第几针",
                        evaluation.getNeedleCount() != null ? evaluation.getNeedleCount() : ""});
            }

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangbiao, "风团数量/24h",
                    evaluation.getNumberWindMasses() != null ? evaluation.getNumberWindMasses().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangbiao, "瘙痒程度",
                    evaluation.getItchingDegree() != null ? evaluation.getItchingDegree().toString() : ""});

            data.add(new String[]{string, shaiXuanHao, doctorName, fangShi, pingGuTime, liangbiao, "总分",
                    evaluation.getTotal() != null ? evaluation.getTotal().toString() : ""});
            serialNumber++;
        }
        if (CollUtil.isNotEmpty(data)) {
            for (String[] row : data) {
                outputStream.write(String.join(",", row).getBytes(StandardCharsets.UTF_8));
                outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
        }
        return serialNumber;


    }


    /**
     * 组装随访期 导出的数据
     * @param results
     * @return
     */
    public static List<ChronicUrticariaQualityLifeQuestionnaire> buildQualityLifeQuestionnaire(List<FormResult> results) {

        List<LocalDate> localDates = new ArrayList<>();
        Map<LocalDate, List<ChronicUrticariaQualityLifeQuestionnaire>> questionMaps = new HashMap<>();
        Map<LocalDate, List<UrticariaActivityScoreSelfEvaluation>> selfEvaluationsMap = new HashMap<>();

        List<ChronicUrticariaQualityLifeQuestionnaire> lifeQuestionnaires;
        List<UrticariaActivityScoreSelfEvaluation> selfEvaluations ;

        ChronicUrticariaQualityLifeQuestionnaire lifeQuestionnaire;
        UrticariaActivityScoreSelfEvaluation selfEvaluation;

        for (FormResult result : results) {
            LocalDate localDate = result.getCreateTime().toLocalDate();
            LocalDateTime localDateTime = result.getCreateTime().withNano(0);
            if (!localDates.contains(localDate)) {
                localDates.add(localDate);
            }
            String jsonContent = result.getJsonContent();
            List<FormFieldDto> fieldDtos = JSONArray.parseArray(jsonContent, FormFieldDto.class);
            if (jsonContent.contains("风团数量/24h") && jsonContent.contains("瘙痒程度")) {
                // 表示这个问卷是 随访期D30-D60 的自我评价
                selfEvaluations = selfEvaluationsMap.get(localDate);
                if (CollUtil.isEmpty(selfEvaluations)) {
                    selfEvaluations = new ArrayList<>();
                    selfEvaluationsMap.put(localDate, selfEvaluations);
                }
                selfEvaluation = new UrticariaActivityScoreSelfEvaluation();
                selfEvaluation.setAaluationDate(localDate);
                selfEvaluation.setAaluationDateTime(localDateTime);
                for (FormFieldDto fieldDto : fieldDtos) {
                    if ((FormFieldExactType.SCORING_SINGLE_CHOICE.equals(fieldDto.getExactType()))) {
                        String label = fieldDto.getLabel();
                        if (label.contains("风团数量")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                selfEvaluation.setNumberWindMasses(score.intValue());
                            }
                        } else if (label.contains("瘙痒程度")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                selfEvaluation.setItchingDegree(score.intValue());
                            }
                        }
                    }
                }
                selfEvaluations.add(selfEvaluation);
            } else {
                lifeQuestionnaires = questionMaps.get(localDate);
                if (CollUtil.isEmpty(lifeQuestionnaires)) {
                    lifeQuestionnaires = new ArrayList<>();
                    questionMaps.put(localDate, lifeQuestionnaires);
                }
                lifeQuestionnaire = new ChronicUrticariaQualityLifeQuestionnaire();
                lifeQuestionnaire.setAaluationDate(localDate);
                lifeQuestionnaire.setAaluationDateTime(localDateTime);
                for (FormFieldDto fieldDto : fieldDtos) {
                    if ((FormFieldExactType.SCORING_SINGLE_CHOICE.equals(fieldDto.getExactType()))) {
                        String label = fieldDto.getLabel();
                        if (label.contains("瘙痒")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setItch(score.intValue());
                            }
                        } else if (label.contains("风团")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setWheal(score.intValue());
                            }
                        } else if (label.contains("眼睛肿胀")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setSwollenEyes(score.intValue());
                            }
                        } else if (label.contains("口唇肿胀")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setLipSwelling(score.intValue());
                            }
                        } else if (label.contains("工作")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setWork(score.intValue());
                            }
                        } else if (label.contains("运动") && !label.contains("体育")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setMotion(score.intValue());
                            }
                        } else if (label.contains("睡眠") && !label.contains("白天困乏")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setSleep(score.intValue());
                            }
                        } else if (label.contains("闲暇")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setLeisure(score.intValue());
                            }
                        } else if (label.contains("社交")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setSocialize(score.intValue());
                            }
                        } else if (label.contains("饮食") && !label.contains("限制")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setDiet(score.intValue());
                            }
                        } else if (label.contains("是否入睡困难")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setDifficultyFallingAsleep(score.intValue());
                            }
                        } else if (label.contains("是否会夜间醒来")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setWakeUpAtNight(score.intValue());
                            }
                        } else if (label.contains("睡眠不佳而白天困乏")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setDaytimeFatigue(score.intValue());
                            }
                        } else if (label.contains("注意力难以集中")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setDifficultyConcentrating(score.intValue());
                            }
                        } else if (label.contains("是否感觉紧张")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setFeelNervous(score.intValue());
                            }
                        } else if (label.contains("是否情绪低落")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setDepression(score.intValue());
                            }
                        } else if (label.contains("需要限制饮食")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setRestrictedDiet(score.intValue());
                            }
                        } else if (label.contains("身体出现的荨麻疹症状")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setFeelingTroubledUrticaria(score.intValue());
                            }
                        } else if (label.contains("公共场所是否感到尴尬")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setFeelingEmbarrassedPublicPlaces(score.intValue());
                            }
                        } else if (label.contains("使用化妆品")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setUsingCosmetics(score.intValue());
                            }
                        } else if (label.contains("服装类型的选择")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setRestrictClothingChoices(score.intValue());
                            }
                        } else if (label.contains("限制了体育运动")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setRestrictSportsActivities(score.intValue());
                            }
                        } else if (label.contains("不良反应影响")) {
                            Float score = getQuestionScore(fieldDto);
                            if (Objects.nonNull(score)) {
                                lifeQuestionnaire.setAdverseEffectsUrticaria(score.intValue());
                            }
                        }
                    }
                }
                lifeQuestionnaires.add(lifeQuestionnaire);
            }
        }

        List<ChronicUrticariaQualityLifeQuestionnaire> resultList = new ArrayList<>();
        for (LocalDate localDate : localDates) {
            do {
                // 这一天的 自我评价
                selfEvaluations = selfEvaluationsMap.get(localDate);

                // 这一天的生活质量问卷
                lifeQuestionnaires = questionMaps.get(localDate);

                // 组合的结果
                lifeQuestionnaire = new ChronicUrticariaQualityLifeQuestionnaire();

                if (CollUtil.isNotEmpty(lifeQuestionnaires)) {
                    BeanUtils.copyProperties(lifeQuestionnaires.get(0), lifeQuestionnaire);
                    // 组合过后。从数组中移除
                    lifeQuestionnaires.remove(0);
                }

                if (CollUtil.isNotEmpty(selfEvaluations)) {
                    lifeQuestionnaire.setScoreSelfEvaluation(selfEvaluations.get(0));
                    // 组合过后。从数组中移除
                    selfEvaluations.remove(0);
                }
                lifeQuestionnaire.setAaluationDate(localDate);
                resultList.add(lifeQuestionnaire);

                // 当 这一天 的 随访 自我评价 和 生活质量问卷。都收集完毕。则退出这一天的处理。
            } while (!CollUtil.isEmpty(lifeQuestionnaires) || !CollUtil.isEmpty(selfEvaluations));
        }
        return resultList;

    }
}
