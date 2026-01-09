//package com.caring.sass.nursing.kafka;
//
//import com.caring.sass.kafka.constant.TopicConstants;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
///**
// * 参考文档 {@see https://docs.spring.io/spring-kafka/reference/html/#kafka-listener-annotation}
// */
//@Slf4j
//@Component
//public class Listener {
//
//    @KafkaListener(topics = {TopicConstants.BASE})
//    public void processMessage(String message) {
//        log.info("message is : {}", message);
//    }
//
//    @KafkaListener(topics = {TopicConstants.SMS})
//    public void baseConsumer(ConsumerRecord<?, ?> record, Consumer<?, ?> consumer) {
//        log.info(record.toString());
//    }
//
//
//}
