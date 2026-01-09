package com.caring.sass.ai.know.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.ai.know.model.KnowledgeFileQuery;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.ai.entity.know.KnowledgeFile;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * dify知识库文档关联表
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
@Repository
public interface KnowledgeFileMapper extends SuperMapper<KnowledgeFile> {


    /**
     * 查询学术资料列表
     *
     * @param query 动态拼接查询条件
     * @return 文章id列表
     */
    @Select("<script>\n" +
            "    SELECT DISTINCT fk.id\n" +
            "    FROM m_ai_knowledge_file AS fk\n" +
            "    INNER JOIN m_ai_knowledge_file_label AS fkl ON fk.id = fkl.file_id\n" +
            "    WHERE fkl.know_type = 'ACADEMIC_MATERIALS' AND ( \n" +
            "    <choose>\n" +
            "        <when test='keyWord != null and keyWord.trim() != \"\"'>\n" +
            "            OR (fkl.label_type = 'KEY_WORD' AND fkl.label_content LIKE CONCAT('%', #{keyWord}, '%'))\n" +
            "        </when>\n" +
            "        <when test='language != null and language.trim() != \"\"'>\n" +
            "            OR (fkl.label_type = 'LANGUAGE' AND fkl.label_content = #{language})\n" +
            "        </when>\n" +
            "        <when test='researchType != null and researchType.trim() != \"\"'>\n" +
            "            OR (fkl.label_type = 'RESEARCH_TYPE' AND fkl.label_content = #{researchType})\n" +
            "        </when>\n" +
            "        <when test='releaseTime != null and releaseTime.trim() != \"\"'>\n" +
            "            OR (fkl.label_type = 'RELEASE_TIME' AND fkl.label_content = #{releaseTime})\n" +
            "        </when>\n" +
            "        <when test='author != null and author.trim() != \"\"'>\n" +
            "            OR (fkl.label_type = 'AUTHOR' AND fkl.label_content LIKE CONCAT('%', #{author}, '%'))\n" +
            "        </when>\n" +
            "        <when test='conferenceJournalName != null and conferenceJournalName.trim() != \"\"'>\n" +
            "            OR (fkl.label_type = 'CONFERENCE_JOURNAL_NAME' AND fkl.label_content LIKE CONCAT('%', #{conferenceJournalName}, '%'))\n" +
            "        </when>\n" +
            "        <otherwise>\n" +
            "            OR 1=0 \n" +
            "        </otherwise>\n" +
            "    </choose>\n" +
            " ) " +
            "</script>")
    List<Long> selectIdsByTag(KnowledgeFileQuery query);


}
