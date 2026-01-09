package com.caring.sass;

import com.caring.sass.ai.article.service.ArticleUserVoiceTaskService;
import com.caring.sass.ai.face.service.MegviiFusionDiagramService;
import com.caring.sass.utils.SpringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;



@Component
public class ApplicationRunnerImpl implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) {
//        MegviiFusionDiagramService diagramService = SpringUtils.getBean(MegviiFusionDiagramService.class);
//        diagramService.reStartMergeImage();
//
//        ArticleUserVoiceTaskService voiceTaskService = SpringUtils.getBean(ArticleUserVoiceTaskService.class);
//        voiceTaskService.startVideoTask();
    }
}
