package com.caring.sass.ai.know.model;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.entity.know.KnowledgeDailyCollection;
import com.caring.sass.ai.entity.know.KnowledgeFile;
import com.caring.sass.ai.entity.know.KnowledgeType;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 知识库文章查询参数
 *
 * @author leizhi
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@AllArgsConstructor
public class KnowledgeFileQuery {

    private String language;
    private String releaseTime;

    // 期刊会议名称
    private String conferenceJournalName;

    // 诊断结果
    private String diagnosticResults;
    private String treatmentPlan;
    private String treatmentOutcome;
    private String caseType;
    private String caseSource;
    private String postStatus;
    private String followUpResults;

    // 公用的
    private String keyWord;
    private String keySymptoms;
    private String sort_;
    private String startTime;
    private String endTime;
    private String title;

    private String author;
    // 个人成果的类型
    private String personalAchievementType;
    private String researchType;

    /**
     * 判断AI返回的条件是否为空
     * @return
     */
    public boolean isEmpty() {
        if (StrUtil.isEmpty(language) && StrUtil.isEmpty(conferenceJournalName)
            && StrUtil.isEmpty(diagnosticResults) && StrUtil.isEmpty(treatmentPlan) && StrUtil.isEmpty(treatmentOutcome)
            && StrUtil.isEmpty(caseType) && StrUtil.isEmpty(caseSource) && StrUtil.isEmpty(postStatus)
            && StrUtil.isEmpty(followUpResults) && StrUtil.isEmpty(keyWord) && StrUtil.isEmpty(keySymptoms)
            && StrUtil.isEmpty(sort_) && StrUtil.isEmpty(startTime) && StrUtil.isEmpty(endTime) && StrUtil.isEmpty(title)
            && StrUtil.isEmpty(author) && StrUtil.isEmpty(personalAchievementType) && StrUtil.isEmpty(researchType)) {
            return true;
        }
        return false;
    }

    /**
     * 将日期字符串转换为 LocalDate
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return 转换后的 LocalDate 或 null 如果日期格式不正确
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public void buildWrapper(KnowledgeType knowledgeType, Long userId, LbqWrapper<KnowledgeFile> wrapper) {

        StringBuilder languageSql = null;
        StringBuilder authorSql = null;
        StringBuilder keyWordSql = null;
        if (StrUtil.isNotEmpty(language)) {
            languageSql = new StringBuilder(" or ");
            String[] split = language.split(",");
            for (int i = 0; i < split.length; i++) {
                languageSql.append(" language_ like '%").append(split[i]).append("%'");
                if (i < split.length - 1 ) {
                    languageSql.append(" or ");
                }
            }
        }
        if (StrUtil.isNotEmpty(author)) {
            authorSql = new StringBuilder(" or ");
            String[] split = author.split(",");
            for (int i = 0; i < split.length; i++) {
                authorSql.append(" author_ like '%").append(split[i]).append("%'");
                if (i < split.length - 1 ) {
                    authorSql.append(" or ");
                }
            }
        }
        if (StrUtil.isNotEmpty(keyWord)) {
            keyWordSql = new StringBuilder(" or ");
            String[] split = keyWord.split(",");
            for (int i = 0; i < split.length; i++) {
                keyWordSql.append(" key_word like '%").append(split[i]).append("%'");
                if (i < split.length - 1 ) {
                    keyWordSql.append(" or ");
                }
            }
        }

        if (StrUtil.isNotEmpty(sort_)) {
            if (sort_.equals("DESC")) {
                wrapper.orderByDesc(SuperEntity::getCreateTime);
            }
            if (sort_.equals("ASC")) {
                wrapper.orderByAsc(SuperEntity::getCreateTime);
            }
        }

        if (StrUtil.isNotEmpty(startTime)) {
            // 判断字符串是否符合 yyyy-MM-dd 格式
            LocalDate localDate = parseDate(startTime, "yyyy-MM-dd");
            wrapper.ge(KnowledgeFile::getFileUploadTime, localDate);
        }

        if (StrUtil.isNotEmpty(endTime)) {
            LocalDate localDate = parseDate(endTime, "yyyy-MM-dd");
            wrapper.le(KnowledgeFile::getFileUploadTime, localDate);
        }
        boolean hasChilern = false;
        // 专业学术的查询条件
        if (knowledgeType.equals(KnowledgeType.ACADEMIC_MATERIALS)) {
            // 语言条件
            StringBuilder sql = new StringBuilder("SELECT file_id FROM m_ai_knowledge_file_academic_materials_label where 1 = 1 ");
            sql.append(" and know_user_id in ('").append(userId).append("'").append(" ,'1')");

            StringBuilder orSql = new StringBuilder();
            if (languageSql != null) {
                orSql.append(languageSql);
            }
            if (authorSql != null) {
                orSql.append(authorSql);
            }
            if (keyWordSql != null) {
                orSql.append(keyWordSql);
            }
            // 期刊会议名称
            if (StrUtil.isNotEmpty(conferenceJournalName)) {
                String[] split = conferenceJournalName.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" conference_journal_name like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }

            // 研究类型
            if (StrUtil.isNotEmpty(researchType)) {
                String[] split = researchType.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" research_type like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (orSql.length() > 0) {
                hasChilern = true;
                String substring = orSql.substring(3);
                sql.append(" and ( ").append(substring).append(" ) ");
            }
            if (StrUtil.isNotEmpty(startTime)) {
                hasChilern = true;
                sql.append(" and release_time >= ").append(startTime);
            }
            if (StrUtil.isNotEmpty(endTime)) {
                hasChilern = true;
                sql.append(" and release_time <= ").append(endTime);
            }

            if (hasChilern) {
                wrapper.and(w -> {
                    if (StrUtil.isNotEmpty(title)) {
                        w.like(KnowledgeFile::getFileName, title).or().apply(true, "id in (" + sql + ")");
                    } else {
                        w.apply(true, "id in (" + sql + ")");
                    }
                });
            } else {
                if (StrUtil.isNotBlank(title)) {
                    wrapper.like(KnowledgeFile::getFileName, title);
                }
            }

        }

        // 个人成果的查询条件
        if (knowledgeType.equals(KnowledgeType.PERSONAL_ACHIEVEMENTS)) {
            // 语言条件
            StringBuilder sql = new StringBuilder("SELECT file_id FROM m_ai_knowledge_file_personal_achievements_label where 1 = 1 ");
            sql.append(" and know_user_id = '").append(userId).append("'");

            StringBuilder orSql = new StringBuilder();
            if (languageSql != null) {
                orSql.append(languageSql);
            }
            if (authorSql != null) {
                orSql.append(authorSql);
            }
            if (keyWordSql != null) {
                orSql.append(keyWordSql);
            }
            if (StrUtil.isNotEmpty(personalAchievementType)) {
                String[] split = personalAchievementType.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    sql.append(" personal_achievement_type like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        sql.append(" or ");
                    }
                }
            }
            if (orSql.length() > 0) {
                hasChilern = true;
                String substring = orSql.substring(3);
                sql.append(" and ( ").append(substring).append(" ) ");
            }
            if (StrUtil.isNotEmpty(startTime)) {
                hasChilern = true;
                sql.append(" and release_time >= ").append(startTime);
            }
            if (StrUtil.isNotEmpty(endTime)) {
                hasChilern = true;
                sql.append(" and release_time <= ").append(endTime);
            }
            if (hasChilern) {
                wrapper.and(w -> {
                    if (StrUtil.isNotEmpty(title)) {
                        w.like(KnowledgeFile::getFileName, title).or().apply(true, "id in (" + sql + ")");
                    } else {
                        w.apply(true, "id in (" + sql + ")");
                    }
                });
            } else {
                if (StrUtil.isNotBlank(title)) {
                    wrapper.like(KnowledgeFile::getFileName, title);
                }
            }
        }

        // 病例库的查询条件
        if (knowledgeType.equals(KnowledgeType.CASE_DATABASE)) {
            StringBuilder sql = new StringBuilder("SELECT file_id FROM m_ai_knowledge_file_case_database_label where 1 = 1 ");
            if (StrUtil.isNotEmpty(startTime)) {
                sql.append(" and ji_lu_time >= ").append(startTime);
            }
            if (StrUtil.isNotEmpty(endTime)) {
                sql.append(" and ji_lu_time <= ").append(endTime);
            }
            StringBuilder orSql = new StringBuilder();
            if (languageSql != null) {
                orSql.append(languageSql);
            }
            if (authorSql != null) {
                orSql.append(authorSql);
            }
            if (StrUtil.isNotEmpty(keySymptoms)) {
                String[] split = keySymptoms.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" key_symptoms like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (StrUtil.isNotEmpty(treatmentPlan)) {
                String[] split = treatmentPlan.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" treatment_plan like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (StrUtil.isNotEmpty(diagnosticResults)) {
                String[] split = diagnosticResults.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" diagnostic_results like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (StrUtil.isNotEmpty(caseType)) {
                String[] split = caseType.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" case_type like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (StrUtil.isNotEmpty(treatmentOutcome)) {
                String[] split = treatmentOutcome.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" treatment_outcome like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (StrUtil.isNotEmpty(caseSource)) {
                String[] split = caseSource.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" case_source like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (StrUtil.isNotEmpty(postStatus)) {
                String[] split = postStatus.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" post_status like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (StrUtil.isNotEmpty(followUpResults)) {
                String[] split = followUpResults.split(",");
                if (split.length > 0) {
                    orSql.append(" or ");
                }
                for (int i = 0; i < split.length; i++) {
                    orSql.append(" follow_up_results like '%").append(split[i]).append("%'");
                    if (i < split.length - 1 ) {
                        orSql.append(" or ");
                    }
                }
            }
            if (orSql.length() > 0) {
                hasChilern = true;
                String substring = orSql.substring(3);
                sql.append(" and ( ").append(substring).append(" ) ");
            }
            if (hasChilern) {
                wrapper.and(w -> {
                    if (StrUtil.isNotEmpty(title)) {
                        w.like(KnowledgeFile::getFileName, title).or().apply(true, "id in (" + sql + ")");
                    } else {
                        w.apply(true, "id in (" + sql + ")");
                    }
                });
            } else {
                if (StrUtil.isNotBlank(title)) {
                    wrapper.like(KnowledgeFile::getFileName, title);
                }
            }
        }

    }

    /**
     * 构造日常收集的查询条件
     * @param wrapper
     */
    public void buildWrapper(LbqWrapper<KnowledgeDailyCollection> wrapper) {

        // 语言条件
        if (StrUtil.isNotEmpty(keyWord)) {
            String[] split = keyWord.split(",");
            wrapper.and(w -> {
                for (int i = 0; i < split.length; i++) {
                    if (i < split.length - 1) {
                        w.like(KnowledgeDailyCollection::getKeyWord, split[i]);
                    } else {
                        w.like(KnowledgeDailyCollection::getKeyWord, split[i]).or();
                    }
                }
            });
        }
        if (StrUtil.isNotEmpty(title)) {
            wrapper.like(KnowledgeDailyCollection::getTextTitle, title);
        }
        if (StrUtil.isNotEmpty(startTime)) {
            // 判断字符串是否符合 yyyy-MM-dd 格式
            LocalDate localDate = parseDate(startTime, "yyyy-MM-dd");
            wrapper.ge(KnowledgeDailyCollection::getFileUploadTime, localDate);
        }

        if (StrUtil.isNotEmpty(endTime)) {
            LocalDate localDate = parseDate(endTime, "yyyy-MM-dd");
            wrapper.le(KnowledgeDailyCollection::getFileUploadTime, localDate);
        }
        if (StrUtil.isNotEmpty(sort_)) {
            if (sort_.equals("DESC")) {
                wrapper.orderByDesc(SuperEntity::getCreateTime);
            }
            if (sort_.equals("ASC")) {
                wrapper.orderByAsc(SuperEntity::getCreateTime);
            }
        }

    }

    /**
     * AI没有分析出用户的意图，直接将用户的输入，丢入查询条件模糊匹配
     * @param knowledgeType
     * @param userId
     * @param wrapper
     * @param query
     */
    public void buildLikeWrapper(KnowledgeType knowledgeType, Long userId, LbqWrapper<KnowledgeFile> wrapper, String query) {

        StringBuilder authorSql = null;
        StringBuilder keyWordSql = null;
        authorSql = new StringBuilder();
        authorSql.append(" author_ like '%").append(query).append("%'");
        keyWordSql = new StringBuilder();
        keyWordSql.append(" or key_word like '%").append(query).append("%'");

        // 专业学术的查询条件
        if (knowledgeType.equals(KnowledgeType.ACADEMIC_MATERIALS)) {
            StringBuilder sql = new StringBuilder("SELECT file_id FROM m_ai_knowledge_file_academic_materials_label where 1 = 1 ");
            sql.append(" and know_user_id in ('").append(userId).append("'").append(" ,'1')");

            StringBuilder orSql = new StringBuilder();
            orSql.append(authorSql);
            orSql.append(keyWordSql);
            // 期刊会议名称
            orSql.append(" or conference_journal_name like '%").append(query).append("%'");

            // 研究类型
//            orSql.append(" or research_type like '%").append(query).append("%'");

            if (orSql.length() > 0) {
                sql.append(" and ( ").append(orSql).append(" ) ");
            }
            wrapper.and(w -> w.like(KnowledgeFile::getFileName, query).or().apply(true, "id in (" + sql + ")"));

        }

        // 个人成果的查询条件
        if (knowledgeType.equals(KnowledgeType.PERSONAL_ACHIEVEMENTS)) {
            // 语言条件
            StringBuilder sql = new StringBuilder("SELECT file_id FROM m_ai_knowledge_file_personal_achievements_label where 1 = 1 ");
            sql.append(" and know_user_id = '").append(userId).append("'");

            StringBuilder orSql = new StringBuilder();
            orSql.append(authorSql);
            orSql.append(keyWordSql);

            orSql.append(" or personal_achievement_type like '%").append(query).append("%'");

            if (orSql.length() > 0) {
                sql.append(" and ( ").append(orSql).append(" ) ");
            }
            wrapper.and(w -> w.like(KnowledgeFile::getFileName, query).or().apply(true, "id in (" + sql + ")"));
        }

        // 病例库的查询条件
        if (knowledgeType.equals(KnowledgeType.CASE_DATABASE)) {
            StringBuilder sql = new StringBuilder("SELECT file_id FROM m_ai_knowledge_file_case_database_label where 1 = 1 ");
            StringBuilder orSql = new StringBuilder();
            orSql.append(authorSql);
            orSql.append(" or key_symptoms like '%").append(query).append("%'");
            orSql.append(" or treatment_plan like '%").append(query).append("%'");
            orSql.append(" or diagnostic_results like '%").append(query).append("%'");
            orSql.append(" or case_type like '%").append(query).append("%'");
            orSql.append(" or treatment_outcome like '%").append(query).append("%'");
            orSql.append(" or case_source like '%").append(query).append("%'");
//            orSql.append(" or post_status like '%").append(query).append("%'");
//            orSql.append(" or follow_up_results like '%").append(query).append("%'");

            if (orSql.length() > 0) {
                sql.append(" and ( ").append(orSql).append(" ) ");
            }
            wrapper.and(w -> w.like(KnowledgeFile::getFileName, query).or().apply(true, "id in (" + sql + ")"));
        }


    }


    // Builder类
    public static class Builder {
        private KnowledgeFileQuery knowledgeFileQuery;

        public Builder() {
            this.knowledgeFileQuery = new KnowledgeFileQuery();
        }

        public Builder keyWord(String keyWord) {
            if (keyWord != null && !keyWord.isEmpty()) {
                this.knowledgeFileQuery.setKeyWord(keyWord);
            }
            return this;
        }

        public Builder personalAchievementType(String personalAchievementType) {
            if (personalAchievementType != null && !personalAchievementType.isEmpty()) {
                this.knowledgeFileQuery.setPersonalAchievementType(personalAchievementType);
            }
            return this;
        }

        public Builder diagnosticResults(String diagnosticResults) {
            if (diagnosticResults != null && !diagnosticResults.isEmpty()) {
                this.knowledgeFileQuery.setDiagnosticResults(diagnosticResults);
            }
            return this;
        }

        public Builder treatmentPlan(String treatmentPlan) {
            if (treatmentPlan != null && !treatmentPlan.isEmpty()) {
                this.knowledgeFileQuery.setTreatmentPlan(treatmentPlan);
            }
            return this;
        }

        public Builder treatmentOutcome(String treatmentOutcome) {
            if (treatmentOutcome != null && !treatmentOutcome.isEmpty()) {
                this.knowledgeFileQuery.setTreatmentOutcome(treatmentOutcome);
            }
            return this;
        }

        public Builder caseType(String caseType) {
            if (caseType != null && !caseType.isEmpty()) {
                this.knowledgeFileQuery.setCaseType(caseType);
            }
            return this;
        }

        public Builder caseSource(String caseSource) {
            if (caseSource != null && !caseSource.isEmpty()) {
                this.knowledgeFileQuery.setCaseSource(caseSource);
            }
            return this;
        }

        public Builder postStatus(String postStatus) {
            if (postStatus != null && !postStatus.isEmpty()) {
                this.knowledgeFileQuery.setPostStatus(postStatus);
            }
            return this;
        }

        public Builder followUpResults(String followUpResults) {
            if (followUpResults != null && !followUpResults.isEmpty()) {
                this.knowledgeFileQuery.setFollowUpResults(followUpResults);
            }
            return this;
        }


        public Builder language(String language) {
            if (language != null && !language.isEmpty()) {
                this.knowledgeFileQuery.setLanguage(language);
            }
            return this;
        }

        public Builder researchType(String researchType) {
            if (researchType != null && !researchType.isEmpty()) {
                this.knowledgeFileQuery.setResearchType(researchType);
            }
            return this;
        }

        public Builder releaseTime(String releaseTime) {
            if (releaseTime != null && !releaseTime.isEmpty()) {
                this.knowledgeFileQuery.setReleaseTime(releaseTime);
            }
            return this;
        }

        public Builder author(String author) {
            if (author != null && !author.isEmpty()) {
                this.knowledgeFileQuery.setAuthor(author);
            }
            return this;
        }

        public Builder conferenceJournalName(String conferenceJournalName) {
            if (conferenceJournalName != null && !conferenceJournalName.isEmpty()) {
                this.knowledgeFileQuery.setConferenceJournalName(conferenceJournalName);
            }
            return this;
        }

        public KnowledgeFileQuery build() {
            return this.knowledgeFileQuery;
        }


    }
}
