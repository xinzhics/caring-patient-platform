package com.caring.sass.ai.assistant.tool;

import com.caring.sass.ai.know.model.KnowledgeFileQuery;
import dev.langchain4j.agent.tool.P;
import org.springframework.stereotype.Service;

/**
 * 搜索工具类
 *
 * @author leizhi
 */
@Service
public class KnowledgeSearchTool {


    /**
     * 查询学术资料
     * @param keyWord
     * @param language
     * @param researchType
     * @param releaseTime
     * @param author
     * @param conferenceJournalName
     * @return
     */
    public KnowledgeFileQuery analyzeAcademicMaterials(@P("search keyWord, it can be empty") String keyWord,
                                            @P("knowledge file language, it can be empty, value is one of [中文、英文、其他]") String language,
                                            @P("knowledge file researchType, it can be empty, " +
                                                    " value is one of [综述、资讯、研究论文、Research Article、News、Review、Case Report、 Report、 Editorial、Clinical Trial、 Commentary]") String researchType,
                                            @P("knowledge file releaseTime, it can be empty") String releaseTime,
                                            @P("knowledge file author, it can be empty") String author,
                                            @P("knowledge file conferenceJournalName, it can be empty") String conferenceJournalName
    ) {

        KnowledgeFileQuery query = new KnowledgeFileQuery.Builder()
                .keyWord(keyWord)
                .language(language)
                .researchType(researchType)
                .releaseTime(releaseTime)
                .author(author)
                .conferenceJournalName(conferenceJournalName)
                .build();
        return query;
    }








}
