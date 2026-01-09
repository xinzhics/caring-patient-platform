package com.caring.sass.ai.article.service.impl;

import com.caring.sass.ai.article.dao.ArticleTaskMapper;
import com.caring.sass.ai.article.service.ArticleOutlineService;
import com.caring.sass.ai.article.service.ArticlePodcastJoinService;
import com.caring.sass.ai.article.service.ArticleUserVoiceTaskService;
import com.caring.sass.ai.entity.article.*;
import com.caring.sass.ai.entity.humanVideo.HumanVideoTaskStatus;
import com.caring.sass.ai.entity.podcast.Podcast;
import com.caring.sass.ai.entity.podcast.PodcastInputType;
import com.caring.sass.ai.entity.podcast.PodcastStyle;
import com.caring.sass.ai.entity.podcast.PodcastTaskStatus;
import com.caring.sass.ai.podcast.dao.PodcastMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class WorksPresetService {

    @Autowired
    private ArticleUserVoiceTaskService voiceTaskService;

    @Autowired
    private PodcastMapper podcastMapper;

    @Autowired
    private ArticlePodcastJoinService podcastJoinService;

    @Autowired
    private ArticleTaskMapper articleTaskMapper;

    @Autowired
    private ArticleOutlineService articleOutlineService;

    /**
     * 预设我的作品数据
     *
     * @param userId 用户id
     * @return true: 预设成功，false: 预设失败
     */
    @Async
    public void presetMyWorks(Long userId) {
        try {
            // 预设我的视频数据
            ArticleUserVoiceTask videoClone = new ArticleUserVoiceTask()
                    .setUserId(userId)
                    .setTalkText("让你的真人形象动起来")
                    .setTaskStatus(HumanVideoTaskStatus.SUCCESS).setTaskName("让你的真人形象动起来")
                    .setHumanVideoCover("https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/video/videoclone.png")
                    .setGenerateAudioUrl("https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/video/videoclone.mp4")
                    .setWaitMergeCover(false)
                    .setShowAIKnowledge(false);

//            ArticleUserVoiceTask image2video = new ArticleUserVoiceTask()
//                    .setUserId(userId)
//                    .setTalkText("让你的动画形象动起来")
//                    .setTaskStatus(HumanVideoTaskStatus.SUCCESS).setTaskName("让你的动画形象动起来")
//                    .setHumanVideoCover(" https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/video/image2video.png")
//                    .setGenerateAudioUrl("https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/video/image2video.mp4")
//                    .setShowAIKnowledge(false);
            voiceTaskService.saveBatch(Arrays.asList(videoClone));

            // 预设我的音频数据
            Podcast podcast = new Podcast()
                    .setUserId(userId)
                    .setPodcastName("什么是过敏呢?")
                    .setTaskStatus(PodcastTaskStatus.TASK_FINISH)
                    .setPodcastInputType(PodcastInputType.TEXT)
                    .setStep(4)
                    .setPodcastInputTextContent("啥是过敏呢？\n" +
                            "简单讲，就是咱们身体的免疫系统太 “敏感” 啦，把那些本来对身体没啥危害的东西，当成了敌人。" +
                            "像花粉、尘螨，还有一些常见的食物，这些东西我们叫它过敏原。" +
                            "一旦这些过敏原进到身体里，免疫系统就误判了，觉得它们会搞破坏，马上启动防御。" +
                            "在这个过程中，身体会释放出一种叫组胺的东西，这一释放，过敏症状就出现啦。" +
                            "比如说皮肤会痒痒，长出皮疹；呼吸道这边呢，就会打喷嚏、流鼻涕、咳嗽；还有消化道，会出现肚子疼、拉肚子的情况。" +
                            "过敏的程度有轻有重，严重的时候，可是会威胁到生命的。")
                    .setPodcastStyle(PodcastStyle.DEFAULT_STYLE)
                    .setPodcastPreviewContents("各位听众大家好，我是小雷医师，主任医师。今天咱们来聊聊一个特别常见的问题——过敏！其实啊，过敏就是咱们的身体有点“太敏感”了，把一些本来没啥危害的东西当成了敌人。\n" +
                            "\n" +
                            "比如说花粉、尘螨，还有某些食物，这些东西我们叫它们“过敏原”。一旦这些过敏原进了身体，免疫系统就误判了，觉得它们要搞破坏，于是马上启动防御模式。在这个过程中，身体会释放一种叫组胺的东西，这一释放，过敏症状就来了。\n" +
                            "\n" +
                            "比如皮肤会痒痒，还可能长出皮疹；呼吸道那边呢，就会打喷嚏、流鼻涕、咳嗽；消化道也不消停，可能会肚子疼、拉肚子。而且啊，过敏的程度有轻有重，严重的甚至会威胁到生命！\n" +
                            "\n" +
                            "所以啊，了解过敏的原因和症状，对我们来说真的很重要。希望今天的分享对大家有所帮助！下次再聊更多健康话题，拜拜啦！\n" +
                            "\n")
                    .setPodcastAudioTaskNumber(4)
                    .setPodcastFinalAudioUrl("https://caring-saas-video.oss-cn-beijing.aliyuncs.com/human/video/allergy.mp3");
            podcastMapper.insert(podcast);

            ArticlePodcastJoin podcastJoin = new ArticlePodcastJoin()
                    .setUserId(userId)
                    .setTaskName("什么是过敏呢?")
                    .setTaskType(TaskType.AUDIO)
                    .setTaskStatus(TaskStatus.FINISHED)
                    .setTaskId(podcast.getId())
                    .setShowAIKnowledge(false);
            podcastJoinService.save(podcastJoin);

            // 预设我的文章数据
            ArticleTask articleTask = new ArticleTask()
                    .setTitle("全球4亿人受困的过敏性鼻炎如何治疗？")
                    .setTaskStatus(2)
                    .setStep(5)
                    .setWritingStyle("严谨")
                    .setAudience("患者")
                    .setArticleWordCount(500)
                    .setAutomaticPictureMatching(0)
                    .setShowAIKnowledge(false);
            articleTask.setCreateUser(userId);
            articleTaskMapper.insert(articleTask);

            ArticlePodcastJoin articlePodcastJoin = new ArticlePodcastJoin()
                    .setUserId(userId).setTaskStatus(TaskStatus.FINISHED)
                    .setTaskType(TaskType.ARTICLE)
                    .setTaskId(articleTask.getId())
                    .setShowAIKnowledge(false)
                    .setTaskName("全球4亿人受困的过敏性鼻炎如何治疗?");
            podcastJoinService.save(articlePodcastJoin);

            ArticleOutline outline = new ArticleOutline()
                    .setArticleOutline("#### 模块一:过敏性鼻炎的基本认识与常见误区  \n" +
                            "- **摘要**:过敏性鼻炎是一种常见的慢性炎症性疾病,主要表现为鼻痒、喷嚏、鼻塞和流涕等症状。然而,许多患者对过敏性鼻炎存在认知误区,如认为只是\"小感冒\"而忽视治疗,或者过度依赖某些药物导致副作用。本模块将详细介绍过敏性鼻炎的定义、病因及常见误区,帮助患者正确认识这一疾病。  \n" +
                            "\n" +
                            "#### 模块二:过敏性鼻炎的药物治疗原则与常用药物  \n" +
                            "- **摘要**:药物治疗是控制过敏性鼻炎症状的重要手段,但如何选择和使用药物却是一门学问。本模块将系统讲解过敏性鼻炎的药物治疗原则,包括阶梯治疗理念和个体化治疗方案。同时,将介绍常用药物的作用机制、适应症及注意事项,如抗组胺药、鼻用糖皮质激素和白三烯受体拮抗剂等。  \n" +
                            "\n" +
                            "#### 模块三:药物治疗中的常见问题与应对策略  \n" +
                            "- **摘要**:在实际用药过程中,患者常常会遇到各种问题,如药物效果不佳、副作用明显等。本模块将针对这些常见问题进行深入分析,并提供相应的应对策略。例如,如何正确使用鼻喷剂以发挥最大疗效,如何处理药物引起的口干等副作用,以及何时需要调整治疗方案等。  \n" +
                            "\n" +
                            "#### 模块四:长期管理与综合治疗的重要性  \n" +
                            "- **摘要**:过敏性鼻炎是一种慢性疾病,需要长期管理和综合治疗才能达到最佳控制效果。本模块将强调患者教育的重要性,包括如何识别和避免过敏原,如何进行鼻腔护理等。同时,还将介绍其他辅助治疗方法,如免疫治疗和环境控制措施,帮助患者制定全面的管理计划。")
                    .setArticleContent("> \"阿嚏！阿嚏！阿嚏！\"——这是小王每天早上起床的\"三重奏\"。作为一名资深过敏性鼻炎患者，他早已习惯了这种\"特殊的闹钟\"。但是最近，他发现自己的症状越来越严重了，甚至连最喜欢的火锅都不敢吃了。这到底是怎么回事？过敏性鼻炎真的就只是\"打喷嚏\"这么简单吗？\n" +
                            "\n" +
                            "### 别把过敏性鼻炎当\"小感冒\"\n" +
                            "\n" +
                            "很多人像小王一样，把过敏性鼻炎当作是\"小感冒\"，认为只要忍一忍就过去了。其实不然！过敏性鼻炎是一种慢性炎症性疾病，它会导致鼻痒、喷嚏、鼻塞和流涕等症状。更糟糕的是，如果不及时治疗，还可能引发哮喘、鼻窦炎等并发症。\n" +
                            "\n" +
                            "根据世界卫生组织的数据显示，全球约有4亿人患有过敏性鼻炎\\[1\\]。在中国，过敏性鼻炎的患病率约为17.6%，且呈逐年上升趋势\\[2\\]。这个数字意味着，在我们身边，每6个人中就有1个人正在与过敏性鼻炎作斗争。\n" +
                            "\n" +
                            "### 用药也是一门学问\n" +
                            "\n" +
                            "说到治疗，很多人第一反应就是吃药。但是你知道吗？用药也是一门学问！目前常用的药物包括抗组胺药、鼻用糖皮质激素和白三烯受体拮抗剂等。每种药物都有其独特的作用机制和适应症。\n" +
                            "\n" +
                            "举个例子，抗组胺药可以有效缓解打喷嚏和流鼻涕的症状，但对于鼻塞的效果就不太理想。而鼻用糖皮质激素虽然见效较慢（通常需要1-2周），但可以有效控制鼻腔炎症。所以，医生通常会根据患者的具体情况制定个体化的治疗方案。\n" +
                            "\n" +
                            "### 用药有烦恼？这些技巧要记牢\n" +
                            "\n" +
                            "在实际用药过程中，很多人都会遇到各种问题。比如，\"为什么我的鼻喷剂总感觉没效果？\"这可能是因为你没有掌握正确的使用方法。\n" +
                            "\n" +
                            "这里分享一个小技巧：使用鼻喷剂时，应该保持头部直立，将喷嘴轻轻插入鼻孔，喷药时用另一侧的鼻孔呼吸。这样可以让药物更好地分布到鼻腔各个部位。\n" +
                            "\n" +
                            "再比如，\"吃了药后总是口干舌燥怎么办？\"这时可以多喝水或者选择使用无镇静作用的第二代抗组胺药（如西替利嗪、氯雷他定等），它们引起口干的几率较低。\n" +
                            "\n" +
                            "### 长期管理才是王道\n" +
                            "\n" +
                            "最后要强调的是，过敏性鼻炎是一种慢性疾病，需要长期管理和综合治疗。除了药物治疗外，我们还需要注意以下几点：\n" +
                            "\n" +
                            "1.  识别和避免过敏原：常见的过敏原包括尘螨、花粉、宠物皮屑等。可以通过皮肤点刺试验或血清特异性IgE检测来明确过敏原。\n" +
                            "2.  做好鼻腔护理：可以使用生理盐水或海盐水清洗鼻腔，保持鼻腔湿润。\n" +
                            "3.  改善生活环境：定期清洁房间，使用防螨床品，保持室内空气流通。\n" +
                            "4.  考虑免疫治疗：对于中重度持续性过敏性鼻炎患者，可以考虑进行特异性免疫治疗（俗称\"脱敏治疗\"）。\n" +
                            "\n" +
                            "正如著名呼吸病学家钟南山院士所说：\"对于过敏性鼻炎患者来说，预防比治疗更重要。\"\\[3\\]希望通过这篇文章的分享，能够帮助大家更好地认识和应对这个\"恼人的小毛病\"。让我们一起努力，告别\"阿嚏\"，拥抱清新生活！\n" +
                            "\n" +
                            "\\[1\\] World Health Organization. Allergic rhinitis and its impact on asthma (ARIA) guidelines: 2019 revision.  \n" +
                            "\\[2\\] Zhang Y, et al. Prevalence of self-reported allergic rhinitis in eleven major cities in China. International Archives of Allergy and Immunology, 2009.  \n" +
                            "\\[3\\] 钟南山院士在2019年中华医学会呼吸病学年会上的发言")
                    .setTaskId(articleTask.getId());
            outline.setUpdateUser(userId);
            articleOutlineService.save(outline);

            ArticleTask articleTask2 = new ArticleTask()
                    .setTitle("高血压患者必知：从餐桌到餐馆的健康饮食指南？")
                    .setTaskStatus(2)
                    .setStep(5)
                    .setWritingStyle("有趣")
                    .setAudience("患者")
                    .setKeyWords("高血压，预防，饮食")
                    .setArticleWordCount(1500)
                    .setAutomaticPictureMatching(0)
                    .setShowAIKnowledge(false);
            articleTask2.setCreateUser(userId);
            articleTaskMapper.insert(articleTask2);

            ArticlePodcastJoin articlePodcastJoin2 = new ArticlePodcastJoin()
                    .setUserId(userId).setTaskStatus(TaskStatus.FINISHED)
                    .setTaskType(TaskType.ARTICLE)
                    .setTaskId(articleTask2.getId())
                    .setShowAIKnowledge(false)
                    .setTaskName("高血压患者必知：从餐桌到餐馆的健康饮食指南？");
            podcastJoinService.save(articlePodcastJoin2);

            ArticleOutline outline2 = new ArticleOutline()
                    .setArticleOutline("#### **模块一: \"高血压的隐形杀手——饮食中的盐\"**\n" +
                            "**摘要**:  \n" +
                            "你知道每天摄入的盐分可能正在悄悄升高你的血压吗？高血压被称为“无声的杀手”，而饮食中的高盐摄入是其主要诱因之一。本模块将揭示盐与高血压之间的密切关系，并通过有趣的案例展示如何在日常生活中减少盐的摄入。比如，“一碗泡面=一天盐量”这样的比喻会让你瞬间明白高盐饮食的危害。最后还会提供一些简单易行的低盐饮食小技巧。\n" +
                            "\n" +
                            "#### **模块二: \"餐桌上的降压神器——这些食物你吃对了吗？\"**\n" +
                            "**摘要**:  \n" +
                            "除了少吃盐，“吃什么”同样重要！本模块将介绍一些天然降压的食物明星——比如富含钾的香蕉、富含纤维的燕麦以及富含抗氧化剂的黑巧克力。通过生动的例子（比如“每天一根香蕉=给血管做SPA”）让读者轻松记住这些食物的好处。还会分享一些创意食谱（比如“降压沙拉”），让健康饮食变得美味又简单。\n" +
                            "\n" +
                            "#### **模块三: \"吃饭顺序也有讲究——这样吃血压更听话\"**\n" +
                            "**摘要**:  \n" +
                            "你知道吗？吃饭的顺序也能影响血压！本模块将揭秘“先吃蔬菜再吃肉”的科学原理——这种顺序不仅能延缓血糖上升还能帮助控制血压。通过一个有趣的故事（比如“老张靠调整吃饭顺序成功降压”），让读者感受到这一方法的实用性。最后还会给出具体的用餐建议（比如“20分钟吃完一顿饭”），帮助读者养成健康的饮食习惯。\n" +
                            "\n" +
                            "#### **模块四: \"外食族的降压攻略——如何在餐馆吃得健康？\"**\n" +
                            "**摘要**:  \n" +
                            "对于经常外食的高血压患者来说，“如何选择健康的餐馆食物”是个大难题。本模块将提供一份实用的外食指南——比如“避开红烧肉选择清蒸鱼”“少点酱料多要柠檬汁”。通过一个真实的案例（比如“小李靠这份攻略成功控制血压”）让读者看到外食也能吃得健康。最后还会分享一些点餐小技巧（比如“提前查看菜单”“自带调味品”），帮助外食族轻松应对高血压挑战。")
                    .setArticleContent("高血压患者的饮食指南：从餐桌到餐馆的健康选择\n" +
                            "======================\n" +
                            "\n" +
                            "> \"你每天吃的盐可能正在悄悄升高你的血压。\"——这听起来像是危言耸听吗？但事实上，高血压这个\"无声的杀手\"正潜伏在我们的日常生活中，而饮食中的高盐摄入就是它的主要帮凶之一。让我们一起来揭开这个隐形杀手的面纱，看看如何在餐桌上打赢这场健康保卫战。\n" +
                            "\n" +
                            "### 高血压的隐形杀手——饮食中的盐\n" +
                            "\n" +
                            "让我们从一个真实的案例开始：40岁的张先生是一名普通的白领，平时工作繁忙，经常靠外卖和泡面解决三餐。最近他总觉得头晕乏力，去医院一查才发现血压已经高达160/100mmHg。医生告诉他：\"你每天摄入的盐量严重超标了！\"\n" +
                            "\n" +
                            "你可能不知道，一碗普通的泡面就含有约5克盐，这已经达到了世界卫生组织建议的每日盐摄入量上限（5克）。而根据《中国居民营养与慢性病状况报告（2020年）》显示，我国居民平均每日盐摄入量高达10.5克，是推荐量的两倍还多！\n" +
                            "\n" +
                            "那么，如何在日常生活中减少盐的摄入呢？这里有几个简单实用的小技巧：\n" +
                            "\n" +
                            "1.  **巧用调味品**：用香草、香料、柠檬汁等代替部分盐来调味。\n" +
                            "2.  **阅读食品标签**：选择钠含量较低的食品。\n" +
                            "3.  **自己做饭**：这样你可以完全控制用盐量。\n" +
                            "4.  **循序渐进**：逐渐减少用盐量，让你的味蕾慢慢适应。\n" +
                            "\n" +
                            "记住，\"少盐不等于无味\"，只要我们善用其他调味方式，一样可以享受美味。\n" +
                            "\n" +
                            "### 餐桌上的降压神器——这些食物你吃对了吗？\n" +
                            "\n" +
                            "除了少吃盐，\"吃什么\"同样重要！让我们来看看那些天然的降压食物明星：\n" +
                            "\n" +
                            "1.  **香蕉**：富含钾元素，能帮助平衡体内的钠水平。每天一根香蕉就相当于给血管做了一次SPA。\n" +
                            "2.  **燕麦**：富含膳食纤维，《美国临床营养学杂志》研究表明，每天食用3克燕麦纤维可降低收缩压2mmHg。\n" +
                            "3.  **黑巧克力**：含有丰富的抗氧化剂，《英国医学杂志》研究显示，适量食用黑巧克力可降低心血管疾病风险。\n" +
                            "\n" +
                            "这里分享一个简单的\"降压沙拉\"食谱：\n" +
                            "\n" +
                            "    材料：生菜、番茄、黄瓜、牛油果、烤鸡胸肉、少许橄榄油和柠檬汁\n" +
                            "    做法：将所有食材切块拌匀即可\n" +
                            "    \n" +
                            "\n" +
                            "这道沙拉不仅美味健康，还能帮助控制血压。\n" +
                            "\n" +
                            "### 吃饭顺序也有讲究——这样吃血压更听话\n" +
                            "\n" +
                            "老张是一名高血压患者，最近他尝试了一种新的吃饭方法：先吃蔬菜再吃肉。没想到一个月后，他的血压竟然下降了10mmHg！这背后的科学原理是什么呢？\n" +
                            "\n" +
                            "1.  **延缓血糖上升**：先吃富含纤维的蔬菜可以延缓碳水化合物吸收。\n" +
                            "2.  **减少总热量摄入**：先吃蔬菜能增加饱腹感，减少后续高热量食物的摄入。\n" +
                            "3.  **营养吸收更佳**：先吃蔬菜可以促进后续蛋白质的吸收。\n" +
                            "\n" +
                            "以下是一些具体的用餐建议：\n" +
                            "\n" +
                            "1.  **细嚼慢咽**：每顿饭至少花20分钟吃完。\n" +
                            "2.  **合理搭配**：蔬菜占一半，蛋白质占四分之一，碳水化合物占四分之一。\n" +
                            "3.  **控制总量**：吃到七分饱就停止。\n" +
                            "\n" +
                            "### 外食族的降压攻略——如何在餐馆吃得健康？\n" +
                            "\n" +
                            "对于经常外食的高血压患者来说，\"如何选择健康的餐馆食物\"确实是个大难题。小李就是一个典型的例子。作为一名销售经理，他每周至少有五天要在外面应酬吃饭。后来他制定了一套外食策略：\n" +
                            "\n" +
                            "1.  **避开红烧肉选择清蒸鱼**：减少脂肪和盐的摄入。\n" +
                            "2.  **少点酱料多要柠檬汁**：用天然酸味代替重口味酱料。\n" +
                            "3.  **提前查看菜单**：选择清淡的菜品。\n" +
                            "4.  **自带调味品**：携带低钠酱油或其他低盐调味品。\n" +
                            "\n" +
                            "这里还有一些实用的点餐技巧：\n" +
                            "\n" +
                            "1.  **选择烹饪方式**：优先选择蒸、煮、炖等烹饪方式。\n" +
                            "2.  **要求分开上酱料**：这样可以自己控制用量。\n" +
                            "3.  **多喝水**：帮助身体排出多余的钠。\n" +
                            "4.  **分享菜肴**：与他人分享可以减少摄入量。\n" +
                            "\n" +
                            "正如美国著名心脏病专家Dr. Dean Ornish所说：\"预防是最好的治疗。\"通过调整我们的饮食习惯，我们完全可以掌控自己的血压健康。记住，每一次选择都是对自己健康的投资。让我们从今天开始，做出更明智的饮食选择吧！\n" +
                            "\n" +
                            "> \"Let food be thy medicine and medicine be thy food.\" —— Hippocrates")
                    .setTaskId(articleTask2.getId());
            outline2.setCreateUser(userId);
            articleOutlineService.save(outline2);

            ArticleTask articleTask3 = new ArticleTask()
                    .setTitle("预防胜于治疗：儿童甲流防护手册")
                    .setTaskStatus(2)
                    .setStep(5)
                    .setWritingStyle("严谨")
                    .setAudience("患者")
                    .setKeyWords("甲流，预防，治疗，儿童")
                    .setArticleWordCount(1500)
                    .setAutomaticPictureMatching(0)
                    .setShowAIKnowledge(false);
            articleTask3.setCreateUser(userId);
            articleTaskMapper.insert(articleTask3);

            ArticlePodcastJoin articlePodcastJoin3 = new ArticlePodcastJoin()
                    .setUserId(userId).setTaskStatus(TaskStatus.FINISHED)
                    .setTaskType(TaskType.ARTICLE)
                    .setTaskId(articleTask3.getId())
                    .setTaskName("预防胜于治疗：儿童甲流防护手册")
                    .setShowAIKnowledge(false);
            podcastJoinService.save(articlePodcastJoin3);

            ArticleOutline outline3 = new ArticleOutline()
                    .setArticleContent("\"妈妈，我好难受…\"上周五晚上8点，5岁的小明突然开始发高烧，体温直奔39.5°C。这个平时活蹦乱跳的小家伙此刻蜷缩在床上，脸颊通红，嘴唇发干。妈妈摸了摸他的额头，\"天哪，怎么这么烫！\"更让人担心的是，小明还出现了剧烈的咳嗽和全身酸痛的症状。\n" +
                            "\n" +
                            "### 小明的甲流历险记\n" +
                            "\n" +
                            "这可不是普通的感冒！小明妈妈立刻带他去了医院。经过检查，医生确诊小明感染了甲型流感病毒。在接下来的几天里，小明经历了高烧反复、剧烈咳嗽和食欲不振的煎熬。看着平时活泼可爱的孩子变得如此虚弱，全家人都揪心不已。\n" +
                            "\n" +
                            "幸运的是，经过及时的治疗和护理，小明在一周后终于康复了。但是这次经历给全家人敲响了警钟：原来甲流对儿童的威胁如此之大！\n" +
                            "\n" +
                            "### 甲流病毒大揭秘\n" +
                            "\n" +
                            "甲流病毒就像一个狡猾的小偷，它总是寻找机会入侵我们的身体。它的\"作案手法\"主要有两种：\n" +
                            "\n" +
                            "1. **飞沫传播**：当感染者咳嗽或打喷嚏时，病毒会随着飞沫在空气中传播。\n" +
                            "2. **接触传播**：病毒可以附着在门把手、玩具等物体表面长达数小时。\n" +
                            "\n" +
                            "根据世界卫生组织(WHO)的数据显示[1]，儿童是流感的高危人群之一。特别是5岁以下的儿童，由于免疫系统尚未完全发育，更容易受到感染。\n" +
                            "\n" +
                            "### 超级英雄的防护盾\n" +
                            "\n" +
                            "既然知道了这个\"小偷\"的作案手法，我们就要给孩子们装备上\"超级英雄的防护盾\"！以下是五种有效的预防方法：\n" +
                            "\n" +
                            "1. 接种疫苗：这是最有效的防护措施。根据美国疾病控制与预防中心(CDC)的建议[2]，6个月以上的儿童都应该每年接种流感疫苗。\n" +
                            "2. 勤洗手：教会孩子正确的洗手方法，使用肥皂和流动水洗手至少20秒。\n" +
                            "3. 戴口罩：在流感高发季节或人多的地方，让孩子戴上口罩。\n" +
                            "4. 保持社交距离：尽量避免带孩子去人群密集的场所。\n" +
                            "5. 增强免疫力：保证孩子充足的睡眠、均衡的饮食和适量的运动。\n" +
                            "\n" +
                            "### 家庭防甲流大作战\n" +
                            "\n" +
                            "让我们把预防甲流变成一场有趣的\"家庭大作战\"！以下是一些实用的家庭防护小贴士：\n" +
                            "\n" +
                            "1. 玩具消毒大行动：每周定期用75%的酒精或消毒湿巾擦拭孩子的玩具。\n" +
                            "2. 营养战士计划：准备富含维生素C的水果蔬菜，如橙子、猕猴桃和西兰花。\n" +
                            "3. 睡眠保卫战：确保学龄前儿童每天有10-13小时的睡眠时间[3]。\n" +
                            "4. 家庭运动日：每周安排一次全家户外活动，增强体质。\n" +
                            "5. 健康习惯打卡：制作一张健康习惯打卡表，每完成一项就贴一个小星星。\n" +
                            "\n" +
                            "记住著名医学家希波克拉底说过的一句话：\"预防胜于治疗。\"让我们携手为孩子筑起一道坚实的健康防线！\n" +
                            "\n" +
                            "[1] World Health Organization. (2023). Influenza (Seasonal). https://www.who.int/news-room/fact-sheets/detail/influenza-(seasonal)\n" +
                            "\n" +
                            "[2] Centers for Disease Control and Prevention. (2023). Seasonal Flu Vaccines. https://www.cdc.gov/flu/prevent/vaccinations.htm\n" +
                            "\n" +
                            "[3] American Academy of Pediatrics. (2023). Healthy Sleep Habits: How Many Hours Does Your Child Need? https://www.healthychildren.org/English/healthy-living/sleep/Pages/Healthy-Sleep-Habits-How-Many-Hours-Does-Your-Child-Need.aspx")
                    .setTaskId(articleTask3.getId());
            outline3.setCreateUser(userId);
            articleOutlineService.save(outline3);
        } catch (Exception e) {
            log.error("预设我的作品数据失败", e);
        }
    }
}
