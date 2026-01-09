//package com.caring.sass.nursing.kafka;
//
//import com.caring.sass.kafka.constant.TopicConstants;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class Producer {
//
//    private final KafkaTemplate kafkaObjectTemplate;
//
//    public Producer(KafkaTemplate kafkaObjectTemplate) {
//        this.kafkaObjectTemplate = kafkaObjectTemplate;
//    }
//
//    public void sendByKafkaTemplate(String msg){
//        kafkaObjectTemplate.send(TopicConstants.SMS,msg);
//    }
//}
