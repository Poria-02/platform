package cn.poria.commom.kafka;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.poria.common.core.util.Assert;
import cn.poria.common.core.util.SpringContextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Date;

@Slf4j
public class KafkaUtil {

    public static KafkaTemplate<String, String> kafkaTemplate =  SpringContextHolder.getBean("poriaKafkaTemplate");

    public static void send(String topic, String message) {
        kafkaTemplate.send(topic,message);
    }

    /**
     * 如果主题有多个分区,会根据key进行分区,如果key为空,则根据消息的hash值进行分区
     * @param topic
     * @param key
     * @param message
     */
    public static void send(String topic,String key, String message) {
        kafkaTemplate.send(topic,key,message);
    }

    @SneakyThrows
    public static void send(String topic, String message, DateField dateField , int offSet){

        Assert.notNull(offSet,"不支持的偏移量");
        Assert.isTrue(offSet> 0 ,"不支持的偏移量");

        Long delaySeconds = DateUtil.offset(new Date(), dateField,offSet).getTime() / 1000 ;
        Long balanceSeconds = delaySeconds - DateUtil.currentSeconds();
        Assert.isTrue(balanceSeconds < 24 * 60 * 60 * 7 ,"最长支持延时7天");

        ProducerRecord<String,String> producerRecord = new ProducerRecord<>("PORIA_DELAY_QUEUE", message);
        producerRecord.headers().add("delaySeconds",String.valueOf(balanceSeconds).getBytes());
        producerRecord.headers().add("targetTopic",topic.getBytes());
        kafkaTemplate.send(producerRecord);
    }
}
