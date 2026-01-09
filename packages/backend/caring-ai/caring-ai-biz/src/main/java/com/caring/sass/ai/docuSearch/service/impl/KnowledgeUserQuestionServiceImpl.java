package com.caring.sass.ai.docuSearch.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.docuSearch.dao.KnowledgeJcrCasMapper;
import com.caring.sass.ai.docuSearch.dao.KnowledgeQuestionEvidenceMapper;
import com.caring.sass.ai.docuSearch.dao.KnowledgeUserQuestionMapper;
import com.caring.sass.ai.docuSearch.service.KnowledgeUserQuestionService;
import com.caring.sass.ai.dto.docuSearch.KnowledgeQuestionEvidenceModel;
import com.caring.sass.ai.dto.docuSearch.KnowledgeUserQuestionSaveModel;
import com.caring.sass.ai.entity.docuSearch.KnowledgeJcrCas;
import com.caring.sass.ai.entity.docuSearch.KnowledgeQuestionEvidence;
import com.caring.sass.ai.entity.docuSearch.KnowledgeUserQuestion;
import com.caring.sass.ai.entity.docuSearch.QuestionAnalyzeStatus;
import com.caring.sass.ai.know.util.DifyWorkApi;
import com.caring.sass.ai.tools.pubmed.EFetchRequest;
import com.caring.sass.ai.tools.pubmed.ESearchRequest;
import com.caring.sass.ai.tools.searxng.Paper;
import com.caring.sass.ai.tools.searxng.SearxngPaperRequest;
import com.caring.sass.ai.utils.SseEmitterSession;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.SaasLinkedBlockingQueue;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.properties.DatabaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 业务实现类
 * 知识库用户问题
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-17
 */
@Slf4j
@Service

public class KnowledgeUserQuestionServiceImpl extends SuperServiceImpl<KnowledgeUserQuestionMapper, KnowledgeUserQuestion> implements KnowledgeUserQuestionService {


    @Autowired
    DatabaseProperties databaseProperties;


    @Autowired
    DifyWorkApi workApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    KnowledgeQuestionEvidenceMapper evidenceMapper;

    @Autowired
    KnowledgeJcrCasMapper knowledgeJcrCasMapper;


    NamedThreadFactory threadFactory = new NamedThreadFactory("docu-search", false);
    ExecutorService queryExecutor = new ThreadPoolExecutor(2, 4, 0L,
                                      TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(500), threadFactory);

    NamedThreadFactory fanYiFactory = new NamedThreadFactory("docu-search-fanyi", false);
    ExecutorService hanhuaExecutor = new ThreadPoolExecutor(2, 4, 0L,
            TimeUnit.MILLISECONDS, new SaasLinkedBlockingQueue<>(500), fanYiFactory);



    /**
     * 设置证据的 jcr信息
     * @param evidence
     */
    public void setEvidenceJCR(KnowledgeQuestionEvidence evidence) {
        KnowledgeJcrCas jcrCas = null;
        if (StrUtil.isNotEmpty(evidence.getEissn())) {
            jcrCas = knowledgeJcrCasMapper.selectOne(Wraps.<KnowledgeJcrCas>lbQ()
                    .eq(KnowledgeJcrCas::getEissn, evidence.getEissn()).last("limit 1"));
        } else if (StrUtil.isNotEmpty(evidence.getIssn())) {
            jcrCas = knowledgeJcrCasMapper.selectOne(Wraps.<KnowledgeJcrCas>lbQ()
                    .eq(KnowledgeJcrCas::getIssn, evidence.getIssn()).last("limit 1"));
        }
        if (jcrCas != null) {
            // jif
            evidence.setIfValue(jcrCas.getJif());
            evidence.setCasSubclass(jcrCas.getCasSubCategory());
            evidence.setCasLargeCategory(jcrCas.getCasCategory());
            evidence.setCasCategoryQuartile(jcrCas.getCasCategoryQuartile());
            evidence.setCasSubCategoryQuartile(jcrCas.getCasSubCategoryQuartile());
        }
    }

    /**
     * 处理文章
     * @param xmlString
     * @param userQuestion
     * @param uid
     */
    public void handleXml(String xmlString, KnowledgeUserQuestion userQuestion, String uid, List<String> ids) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 使用 StringReader 将字符串包装成输入源
            InputSource is = new InputSource(new StringReader(xmlString));
            Document document = builder.parse(is);

            // 打印根元素的名称
            NodeList pubmedArticles = document.getElementsByTagName("PubmedArticle");

            Map<String, KnowledgeQuestionEvidence> evidenceMap = new HashMap<>();
            for (int i = 0; i < pubmedArticles.getLength(); i++) {
                KnowledgeQuestionEvidence evidence = new KnowledgeQuestionEvidence(userQuestion);
                Node pubmedArticle = pubmedArticles.item(i);
                if (pubmedArticle.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList childNodes = pubmedArticle.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node MedlineCitation = childNodes.item(j);
                        NodeList nodeList = MedlineCitation.getChildNodes();
                        boolean handleArticle = false;
                        boolean handleDoi = false;
                        for (int k = 0; k < nodeList.getLength(); k++) {
                            Node article = nodeList.item(k);
                            if (article.getNodeName().equals("PMID")) {
                                String pmid = article.getTextContent();
                                if (StrUtil.isNotEmpty(pmid)) {
                                    Integer selected = evidenceMapper.selectCount(Wraps.<KnowledgeQuestionEvidence>lbQ()
                                            .eq(KnowledgeQuestionEvidence::getPmid, pmid)
                                            .eq(KnowledgeQuestionEvidence::getQuestionId, userQuestion.getId()));
                                    // 文章如果已经存在。则直接跳过。
                                    if (selected != null && selected > 0) {
                                        handleArticle = true;
                                        break;
                                    }
                                    evidence.setPmid(pmid);
                                }
                            }

                            if (article.getNodeName().equals("Article")) {
                                handleArticle = true;
                                // 获取文章的信息
                                NodeList articleChildNodes = article.getChildNodes();
                                for (int l = 0; l < articleChildNodes.getLength(); l++) {
                                    Node item = articleChildNodes.item(l);
                                    if (item.getNodeName().equals("Journal")) {

                                        // 杂志信息
                                        getJournal(evidence, item);
                                    } else if (item.getNodeName().equals("ArticleTitle")) {

                                        // 文章的标题
                                        evidence.setDocumentName(item.getTextContent());
                                    } else if (item.getNodeName().equals("Abstract")) {

                                        // 摘要信息
                                        Node AbstractText = item.getFirstChild();
                                        if (AbstractText == null) {
                                            evidence.setSummary(item.getTextContent());
                                        } else {
                                            evidence.setSummary(AbstractText.getTextContent());
                                        }
                                    } else if (item.getNodeName().equals("AuthorList")) {
                                        // 解析用户的姓名
                                        getAuthorList(evidence, item);
                                    } else if (item.getNodeName().equals("ArticleDate")) {
                                        getPublicationTime(evidence, item);
                                    } else if (item.getNodeName().equals("PublicationTypeList")) {
                                        NodeList itemChildNodes = item.getChildNodes();
                                        for (int m = 0; m < itemChildNodes.getLength(); m++) {
                                            Node node = itemChildNodes.item(m);
                                            if (node.getNodeName().equals("PublicationType")) {
                                                Element element = (Element) node;
                                                String idType = element.getAttribute("UI");
                                                if ("D016454".equals(idType)) {
                                                    evidence.setStudyType(element.getTextContent());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (MedlineCitation.getNodeName().equals("PubmedData")) {
                            handleDoi = true;
                            NodeList nodes = MedlineCitation.getChildNodes();
                            for (int l = 0; l < nodes.getLength(); l++) {
                                Node item = nodes.item(l);
                                if (item.getNodeName().equals("ArticleIdList")) {
                                    NodeList itemChildNodes = item.getChildNodes();
                                    for (int m = 0; m < itemChildNodes.getLength(); m++) {
                                        Node node = itemChildNodes.item(m);
                                        if (node.getNodeName().equals("ArticleId")) {
                                            Element element = (Element) node;
                                            String idType = element.getAttribute("IdType");
                                            if ("doi".equals(idType)) {
                                                String doiValue = element.getTextContent();
                                                evidence.setDoi(doiValue);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (handleArticle && handleDoi) {
                            break;
                        }
                    }
                }
                evidenceMap.put(evidence.getPmid(), evidence);
            }
            userQuestion.setQuestionQuantityEvidence(pubmedArticles.getLength());
            // 根据ID顺序 取出 证据列表。 设置jcr后。推送给前端
            for (String id : ids) {
                KnowledgeQuestionEvidence evidence = evidenceMap.get(id);
                if (evidence == null) {
                    continue;
                }
                setEvidenceJCR(evidence);
                evidence.setAiSummaryStatus(false);
                evidenceMapper.insert(evidence);
                if (!cancelTask(userQuestion.getId(), uid)) {
                    // 将解析出来的信息，发给前端。
                    sendEvidence(evidence, uid);
                }
                hanhuaExecutor.execute(() -> translationAndSummary(evidence, userQuestion, uid));
            }
            JSONObject object = new JSONObject();
            object.put("type", "questionAnalyzeStatus");
            object.put("content", QuestionAnalyzeStatus.AI_SUMMARY);
            sendMessage(uid, object.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询pubmed
     * @param question
     * @param userQuestion
     * @param uid
     * @return
     */
    public Boolean queryPubmed(String question, KnowledgeUserQuestion userQuestion, String uid) {
        String a = null;
        try {
            a = URLEncoder.encode(question, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        int retStart = 0;
        Integer count = 0;
        String queriedESearch = queryESearch(a, 0, retStart);
        if (queriedESearch == null) {
            return false;
        }

        JSONObject jsonObject = JSONObject.parseObject(queriedESearch);
        JSONObject esearchresult = jsonObject.getJSONObject("esearchresult");
        count = esearchresult.getInteger("count");
        if (count > 0) {
            // 解析queriedESearch，拿到文章的ID列表
            JSONArray jsonArray = esearchresult.getJSONArray("idlist");
            List<String> ids = new ArrayList<>();
            for (Object object : jsonArray) {
                ids.add(object.toString());
            }
            userQuestion.setEvidenceIds(String.join(",", ids));
            if (cancelTask(userQuestion.getId(), uid)) {
                return true;
            }
            // 查询每个文章的内容
            EFetchRequest request = new EFetchRequest("pubmed", ids);
            String xmlString = "";
            try {
                xmlString = request.sendRequest();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            // 处理查询到的xml内容
            handleXml(xmlString, userQuestion, uid, ids);
            return true;
        } else {
            return false;
        }

    }

    /**
     * 获取杂志的期刊名称
     * @param evidence
     * @param item
     */
    public static void getJournal(KnowledgeQuestionEvidence evidence, Node item) {
        if (item == null) {
            return;
        }
        NodeList journalNodeList = item.getChildNodes();
        for (int i = 0; i < journalNodeList.getLength(); i++) {
            Node node = journalNodeList.item(i);
            if (node.getNodeName().equals("ISSN")) {
                Element issnElement = (Element) node;
                String issnType = issnElement.getAttribute("IssnType");
                if ("Electronic".equals(issnType)) {
                    evidence.setEissn(node.getTextContent());
                } else {
                    evidence.setIssn(node.getTextContent());
                }
            } else if (node.getNodeName().equals("Title")) {
                evidence.setJournal(node.getTextContent());
            } else if (node.getNodeName().equals("ISOAbbreviation")) {
                System.out.println("ISOAbbreviation: " + node.getTextContent());
            }
        }
    }

    /**
     * 获取pubmed的文献发表时间
     *
     * @param evidence
     * @param item
     */
    public static void getPublicationTime(KnowledgeQuestionEvidence evidence, Node item) {

        if (item == null) {
            return;
        }
        NodeList publicationDateNodeList = item.getChildNodes();
        StringBuilder date = new StringBuilder();
        for (int i = 0; i < publicationDateNodeList.getLength(); i++) {
            Node node = publicationDateNodeList.item(i);
            if (node.getNodeName().equals("Year")) {
                date.append(node.getTextContent());
            }
            if (node.getNodeName().equals("Month")) {
                date.append("-").append(node.getTextContent());
            }
            if (node.getNodeName().equals("Day")) {
                date.append("-").append(node.getTextContent());
            }
        }
        if (StrUtil.isNotEmpty(date.toString())) {
            evidence.setPublicationTime(date.toString());
        }

    }


    public static boolean isChinese(String str) {
        return str.matches("[\\u4e00-\\u9fa5]+");
    }

    /**
     * 解析作者信息。
     * @param evidence
     * @param item
     */
    public static void getAuthorList(KnowledgeQuestionEvidence evidence, Node item) {
        if (item == null) {
            return;
        }
        NodeList childNodes = item.getChildNodes();

        List<String> authors = new ArrayList<>();

        for (int i = 0; i < childNodes.getLength(); i++) {

            Node author = childNodes.item(i);
            if (author.getNodeName().equals("Author")) {
                NodeList authorChildNodes = author.getChildNodes();
                for (int k = 0; k < authorChildNodes.getLength(); k++) {
                    Node node = authorChildNodes.item(k);
                    String lastName = "";
                    String foreName = "";
                    if (node.getNodeName().equals("LastName")) {
                        // 姓
                        lastName = node.getTextContent();
                    } else if (node.getNodeName().equals("ForeName")) {
                        // 名
                        foreName = node.getTextContent();
                    }
                    // 如果名不存在，则直接跳过
                    if (StrUtil.isEmpty(foreName)) {
                        continue;
                    }
                    // 判断这个人是中国人还是外国人。 外国人名在前，姓在后
                    if (StrUtil.isEmpty(lastName)) {
                        // 没有姓，只有名。
                        if (StrUtil.isNotEmpty(foreName)) {
                            authors.add(foreName);
                        }
                    } else {
                        if (isChinese(lastName)) {
                            // 姓是汉字，则认为是中国人。
                            authors.add(lastName + foreName);
                        } else {
                            authors.add(foreName + " " + lastName);
                        }
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(authors)) {
            evidence.setAuthor(String.join(",", authors));
        }
    }





    /**
     * 查询ESearch
     * @param question
     * @param retryNumber
     * @return
     */
    public String queryESearch(String question, int retryNumber, int retStart) {
        if (retryNumber >= 2) {
            return null;
        }
        try {
            String r = new ESearchRequest(question).setRetMode("JSON")
                    .setSort("relevance")
                    .setUseHistory(true)
                    .setRetStart(retStart).sendRequest();
            System.out.println(r);
            return r;
        } catch (Exception e) {
            queryESearch(question, ++retryNumber, retStart);
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 在分析问题的过程中，将进度发送给前端
     * @param uid
     * @param message
     */
    public void sendMessage(String uid, String message) {

        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter != null) {
            System.out.println("send" + message);
            try {
                emitter.send(SseEmitter.event()
                        .id(UUID.fastUUID().toString())
                        .data(message)
                        .reconnectTime(3000));
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    /**
     * 检测任务是否还要执行
     * @param questionId
     * @return
     */
    public boolean cancelTask(Long questionId, String uid) {
        Object object = redisTemplate.boundHashOps("knowledge_user_question").get(questionId.toString());
        if (object == null) {
            // 任务被取消
            SseEmitter emitter = SseEmitterSession.get(uid);
            if (emitter != null) {
                emitter.complete();
            }
            return true;
        }
        return false;
    }


    /**
     * 调用大模型，翻译标题和内容
     * 并进行AI总结
     * @param evidence
     */
    public void translationAndSummary(KnowledgeQuestionEvidence evidence, KnowledgeUserQuestion userQuestion, String uid) {

        String andSummary = workApi.translationAndSummary(evidence.getDocumentName(), evidence.getSummary(), evidence.getUserId());
        if (andSummary != null) {
            JSONObject jsonObject = JSONObject.parseObject(andSummary);
            String translateTitle = jsonObject.getString("translateTitle");
            String translateAbstract = jsonObject.getString("translateAbstract");
            String summary = jsonObject.getString("summary");
            evidence.setTranslateTitle(translateTitle);
            evidence.setTranslateAbstract(translateAbstract);
            evidence.setAiSummary(summary);
        }
        evidence.setAiSummaryStatus(true);
        evidence.setUpdateTime(LocalDateTime.now());
        evidenceMapper.updateById(evidence);
        if (!cancelTask(userQuestion.getId(), uid)) {
            // 将解析出来的信息，发给前端。
            sendEvidence(evidence, uid);
        }

        // 检查证据是否都已经总结完毕。
        Integer selected = evidenceMapper.selectCount(Wraps.<KnowledgeQuestionEvidence>lbQ()
                .eq(KnowledgeQuestionEvidence::getQuestionId, userQuestion.getId())
                .eq(KnowledgeQuestionEvidence::getAiSummaryStatus, true));

        if (selected >= userQuestion.getQuestionQuantityEvidence()) {
            // 将解析出来的信息，发给前端。
            // 设置key 的过期时间一个月
            userQuestion.setKeywordExpireTime(LocalDateTime.now().plusMonths(1));
            sendFinalResult(userQuestion, uid);
        }

    }



    /**
     * 查询searxng论文
     * @param keyword
     * @param uid
     * @param userQuestion
     */
    public void querySearxngPaper(String keyword, String uid, KnowledgeUserQuestion userQuestion) {
        SearxngPaperRequest example = new SearxngPaperRequest(keyword);
        List<Paper> response = example.sendRequest();
        List<KnowledgeQuestionEvidence> evidences = new ArrayList<>();
        for (Paper paper : response) {
            if (evidences.size() > 20) {
                break;
            }
            userQuestion.setQuestionQuantityEvidence(Math.min(response.size(), 20));
            KnowledgeQuestionEvidence evidence = new KnowledgeQuestionEvidence(userQuestion);
            evidences.add(evidence);
            evidence.setDocumentName(paper.getTitle());
            if (CollUtil.isNotEmpty(paper.getAuthors())) {
                evidence.setAuthor(String.join(",", paper.getAuthors()));
            }
            if (!StrUtil.isBlank(paper.getPublishedDate())) {
                evidence.setPublicationTime(paper.getPublishedDate().substring(0, 10));
            }
            evidence.setSummary(paper.getContent());
            evidence.setJournal(paper.getJournal());
            evidence.setDoi(paper.getDoi());
            if (StrUtil.isNotEmpty(paper.getJournal())) {
                KnowledgeJcrCas jcrCas = knowledgeJcrCasMapper.selectOne(Wraps.<KnowledgeJcrCas>lbQ()
                        .eq(KnowledgeJcrCas::getJournalName, paper.getJournal())
                        .last(" limit 1 "));
                if (jcrCas != null) {
                    // jif
                    evidence.setIfValue(jcrCas.getJif());
                    evidence.setCasSubclass(jcrCas.getCasSubCategory());
                    evidence.setCasLargeCategory(jcrCas.getCasCategory());
                    evidence.setCasCategoryQuartile(jcrCas.getCasCategoryQuartile());
                    evidence.setCasSubCategoryQuartile(jcrCas.getCasSubCategoryQuartile());
                }
            }
            evidence.setAiSummaryStatus(false);
            evidenceMapper.insert(evidence);
            if (!cancelTask(userQuestion.getId(), uid)) {
                // 将解析出来的信息，发给前端。
                sendEvidence(evidence, uid);
            }
            hanhuaExecutor.execute(() -> translationAndSummary(evidence, userQuestion, uid));
        }
        userQuestion.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.AI_SUMMARY);
        baseMapper.updateById(userQuestion);


    }

    /**
     * 检查这个keyword是否有意境完成的证据结果，并在有效期
     * @param keyword
     * @param userQuestion
     * @param uid
     * @return
     */
    public boolean checkEffectiveQuestion(String question, String keyword, KnowledgeUserQuestion userQuestion, String uid) {

        KnowledgeUserQuestion oldQuestion;
        if (StrUtil.isNotBlank(question)) {
            oldQuestion = baseMapper.selectOne(Wraps.<KnowledgeUserQuestion>lbQ()
                    .eq(KnowledgeUserQuestion::getQuestionAnalyzeStatus, QuestionAnalyzeStatus.ANALYZE_COMPLETE)
                    .eq(KnowledgeUserQuestion::getQuestionDesc, question)
                    .ge(KnowledgeUserQuestion::getKeywordExpireTime, LocalDateTime.now())
                    .last(" limit 1 "));
        } else if (StrUtil.isNotBlank(keyword)) {
            oldQuestion = baseMapper.selectOne(Wraps.<KnowledgeUserQuestion>lbQ()
                    .eq(KnowledgeUserQuestion::getQuestionAnalyzeStatus, QuestionAnalyzeStatus.ANALYZE_COMPLETE)
                    .eq(KnowledgeUserQuestion::getQuestionKeyword, keyword)
                    .ge(KnowledgeUserQuestion::getKeywordExpireTime, LocalDateTime.now())
                    .last(" limit 1 "));
        } else {
            return false;
        }

        if (oldQuestion != null) {

            if (StrUtil.isBlank(keyword)) {
                // 给前端推送，已经开始分析问题
                JSONObject object = new JSONObject();
                object.put("type", "questionAnalyzeStatus");
                object.put("content", QuestionAnalyzeStatus.ANALYZING_QUESTION);
                object.put("sendTime", LocalDateTime.now());
                sendMessage(uid, object.toJSONString());
            }

            DatabaseProperties.Id id = databaseProperties.getId();
            Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

            // 查询证据列表。
            List<KnowledgeQuestionEvidence> evidences = evidenceMapper.selectList(Wraps.<KnowledgeQuestionEvidence>lbQ()
                    .eq(KnowledgeQuestionEvidence::getQuestionId, oldQuestion.getId())
                    .orderByAsc(KnowledgeQuestionEvidence::getCreateTime));
            if (CollUtil.isEmpty(evidences)) {
                return false;
            }

            if (!cancelTask(userQuestion.getId(), uid)) {
                // 给前端推送，已经开始分析问题
                JSONObject object = new JSONObject();
                object.put("type", "questionAnalyzeStatus");
                object.put("content", QuestionAnalyzeStatus.FINDING_EVIDENCE);
                object.put("sendTime", LocalDateTime.now());
                sendMessage(uid, object.toJSONString());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    log.error("thread sleep error {}", e.getMessage());
                }
                for (KnowledgeQuestionEvidence evidence : evidences) {

                    evidence.setQuestionId(userQuestion.getId());
                    evidence.setUserId(userQuestion.getUserId());
                    evidence.setId(snowflake.nextId());
                    evidence.setCreateTime(LocalDateTime.now());
                    evidence.setUpdateTime(LocalDateTime.now());
                    sendEvidence(evidence, uid);
                }
                evidenceMapper.insertBatchSomeColumn(evidences);

                // 将解析出来的信息，发给前端。
                userQuestion.setQuestionQuantityEvidence(evidences.size());
                userQuestion.setEvidenceIds(oldQuestion.getEvidenceIds());
                userQuestion.setQuestionKeyword(oldQuestion.getQuestionKeyword());
                userQuestion.setKeywordExpireTime(oldQuestion.getKeywordExpireTime());
                userQuestion.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.ANALYZE_COMPLETE);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("thread sleep error {}", e.getMessage());
                }
                sendFinalResult(userQuestion, uid);
                return true;
            }
        }
        return false;

    }

    /**
     * 发送证据
     * @param evidence
     * @param uid
     */
    public void sendEvidence(KnowledgeQuestionEvidence evidence, String uid) {
        JSONObject object = new JSONObject();
        object.put("type", "evidence");
        KnowledgeQuestionEvidenceModel evidenceModel = new KnowledgeQuestionEvidenceModel();
        BeanUtils.copyProperties(evidence, evidenceModel);
        evidenceModel.setId(evidence.getId());
        object.put("content", evidenceModel);
        object.put("sendTime", LocalDateTime.now());
        sendMessage(uid, object.toJSONString());
    }

    /**
     * 问题处理完毕了。 发送最终信息
     * @param userQuestion
     * @param uid
     */
    public void sendFinalResult(KnowledgeUserQuestion userQuestion, String uid) {
        // 将解析出来的信息，发给前端。
        JSONObject message = new JSONObject();
        message.put("type", "final_result");
        userQuestion.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.ANALYZE_COMPLETE);
        KnowledgeUserQuestionSaveModel questionSaveModel = new KnowledgeUserQuestionSaveModel();
        BeanUtils.copyProperties(userQuestion, questionSaveModel);
        questionSaveModel.setId(userQuestion.getId().toString());
        questionSaveModel.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.ANALYZE_COMPLETE);
        message.put("content", questionSaveModel);
        message.put("sendTime", LocalDateTime.now());
        sendMessage(uid, message.toJSONString());
        userQuestion.setFinishSearchTime(LocalDateTime.now());
        baseMapper.updateById(userQuestion);
        sendEmitterDone(uid);
        redisTemplate.boundHashOps("knowledge_user_question").delete(userQuestion.getId().toString());
    }


    /**
     * 处理问题
     * // 使用dify分析问题
     * // 拿到dify分析的关键词
     * // 查询关键词对应的证据
     * // 分析关键词的证据
     * // 保存 证据
     * // 推送关键词拿到的证据
     * @param uid
     * @param question
     * @param userId
     * @param questionId
     */
    public void handleQuestion(String uid, String question, Long userId, Long questionId) {

        if (cancelTask(questionId, uid)) {
            return;
        }
        KnowledgeUserQuestion userQuestion = baseMapper.selectById(questionId);
        JSONObject object = new JSONObject();
        object.put("type", "userQuestion");
        userQuestion.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.UNDERSTANDING_QUESTION);
        KnowledgeUserQuestionSaveModel questionSaveModel = new KnowledgeUserQuestionSaveModel();
        BeanUtils.copyProperties(userQuestion, questionSaveModel);
        questionSaveModel.setId(userQuestion.getId().toString());
        object.put("content", questionSaveModel);
        sendMessage(uid, object.toJSONString());

        userQuestion.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.ANALYZING_QUESTION);
        userQuestion.setStartSearchTime(LocalDateTime.now());
        baseMapper.updateById(userQuestion);

        // 给前端推送，已经开始理解问题
        object = new JSONObject();
        object.put("type", "questionAnalyzeStatus");
        object.put("content", QuestionAnalyzeStatus.UNDERSTANDING_QUESTION);
        object.put("sendTime", LocalDateTime.now());
        sendMessage(uid, object.toJSONString());

        if (cancelTask(questionId, uid)) {
            return;
        }
        // 检查这个key 是否在 数据库 存在 还在有效期内 已完成最终回复的 相同问题。
        // 如果有，则直接使用这个问题的证据进行回复。
        boolean checked = checkEffectiveQuestion(question, null, userQuestion, uid);
        if (checked) {
            return;
        }
        object = new JSONObject();
        object.put("type", "questionAnalyzeStatus");
        object.put("content", QuestionAnalyzeStatus.ANALYZING_QUESTION);
        object.put("sendTime", LocalDateTime.now());
        sendMessage(uid, object.toJSONString());
        String keyWord = workApi.queryDifyLiJieQuestion(question, userId);
        if (StrUtil.isEmpty(keyWord)) {
            // 问题理解失败
            object = new JSONObject();
            object.put("type", "Service_exception");
            object.put("content", "这个问题我不理解，换个问题吧");
            sendMessage(uid, object.toJSONString());
            return;
        }

        // 给前端推送，已经开始查找证据
        userQuestion.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.FINDING_EVIDENCE);
        userQuestion.setQuestionKeyword(keyWord);
        baseMapper.updateById(userQuestion);

        if (cancelTask(questionId, uid)) {
            return;
        }
        // 检查这个key 是否在 数据库 存在 还在有效期内 已完成最终回复的 相同问题。
        // 如果有，则直接使用这个问题的证据进行回复。
        checked = checkEffectiveQuestion(null, keyWord, userQuestion, uid);
        if (checked) {
            return;
        }

        object = new JSONObject();
        object.put("type", "questionAnalyzeStatus");
        object.put("content", QuestionAnalyzeStatus.FINDING_EVIDENCE);
        object.put("sendTime", LocalDateTime.now());
        sendMessage(uid, object.toJSONString());

        // 先使用 pubmed 进行搜索。 pubmed 在重试次数用完后，还没有正常返回数据时，  使用后续方案 查询。
        Boolean queried = queryPubmed(keyWord, userQuestion, uid);
        if (queried) {
            return;
        }

        if (cancelTask(questionId, uid)) {
            return;
        }
        // 使用备用方案， 查询搜索引擎。
        querySearxngPaper(keyWord, uid, userQuestion);

    }

    /**
     * 发送结束消息
     * @param uid
     */
    public void sendEmitterDone(String uid) {
        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id("[DONE]")
                        .data("[DONE]")
                        .reconnectTime(3000));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                emitter.complete();
            }
        }
    }


    @Override
    public SseEmitter submitQuestion(String uid, String question, String conversation) {
        Long userId = BaseContextHandler.getUserId();
        // 创建问题。
        // 保存问题。
        UpdateWrapper<KnowledgeUserQuestion> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("conversation", conversation);
        wrapper.set("is_new_question", 0);
        baseMapper.update(new KnowledgeUserQuestion(), wrapper);

        KnowledgeUserQuestion userQuestion = new KnowledgeUserQuestion();
        userQuestion.setUserId(userId);
        userQuestion.setUid(uid);
        userQuestion.setQuestionDesc(question);
        userQuestion.setConversation(conversation);
        userQuestion.setIsNewQuestion(1);
        userQuestion.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.UNDERSTANDING_QUESTION);
        baseMapper.insert(userQuestion);



        // 默认5分钟超时,设置为0L则永不超时
        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter != null) {
            return emitter;
        }
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);

        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            SseEmitterSession.remove(uid);
        });

        //超时回调
        sseEmitter.onTimeout(() -> log.info("[{}]连接超时...................", uid));
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data("ERROR")
                                .reconnectTime(3000));
                        SseEmitterSession.put(uid, sseEmitter);
                    } catch (IOException e) {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                    }
                }
        );

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            log.error("", e);
        }
        log.info("[{}]创建sse连接成功！", uid);

        redisTemplate.boundHashOps("knowledge_user_question").put(userQuestion.getId().toString(), "running");
        queryExecutor.execute(() -> handleQuestion(uid, question, userId, userQuestion.getId()));
        return sseEmitter;

    }


    @Override
    public KnowledgeUserQuestion saveQuestion(String question, String conversation, String uid) {
        // 创建问题。
        // 保存问题。
        Long userId = BaseContextHandler.getUserId();
        UpdateWrapper<KnowledgeUserQuestion> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("conversation", conversation);
        wrapper.set("is_new_question", 0);
        baseMapper.update(new KnowledgeUserQuestion(), wrapper);


        KnowledgeUserQuestion userQuestion = new KnowledgeUserQuestion();
        userQuestion.setUserId(userId);
        userQuestion.setUid(uid);
        userQuestion.setQuestionDesc(question);
        userQuestion.setConversation(conversation);
        userQuestion.setIsNewQuestion(1);
        userQuestion.setQuestionAnalyzeStatus(QuestionAnalyzeStatus.UNDERSTANDING_QUESTION);
        baseMapper.insert(userQuestion);

        redisTemplate.boundHashOps("knowledge_user_question").put(userQuestion.getId().toString(), "running");
        queryExecutor.execute(() -> handleQuestion(uid, question, userId, userQuestion.getId()));

        return userQuestion;
    }

    @Override
    public SseEmitter createSse(String uid) {

        // 默认5分钟超时,设置为0L则永不超时
        SseEmitter emitter = SseEmitterSession.get(uid);
        if (emitter != null) {
            return emitter;
        }
        SseEmitter sseEmitter = new SseEmitter(SseEmitterSession.SSE_TIME_OUT);
        SseEmitterSession.put(uid, sseEmitter);

        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            SseEmitterSession.remove(uid);
        });

        //超时回调
        sseEmitter.onTimeout(() -> log.info("[{}]连接超时...................", uid));
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data("ERROR")
                                .reconnectTime(3000));
                        SseEmitterSession.put(uid, sseEmitter);
                    } catch (IOException e) {
                        log.error("[{}]连接异常,{}", uid, throwable.toString());
                    }
                }
        );

        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            log.error("", e);
        }
        log.info("[{}]创建sse连接成功！", uid);

        return sseEmitter;
    }

    @Override
    public void cancelQuestion(String uid) {
        // 找到UID代表
        KnowledgeUserQuestion userQuestion = baseMapper.selectOne(Wraps.<KnowledgeUserQuestion>lbQ()
                .eq(KnowledgeUserQuestion::getUid, uid)
                .last(" limit 1 "));
        redisTemplate.boundHashOps("knowledge_user_question").delete(userQuestion.getId().toString());
    }
}
