package cn.poria.commom.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("kafka")
public class KafkaAutoConfiguration {


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    @Value("${spring.kafka.poria-producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.poria-producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.poria-producer.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.poria-producer.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.poria-producer.properties.sasl.jaas.config}")
    private String saslJaasConfig;

    @Bean
    public ProducerFactory<String, String> poriaProducerFactory() {
        Map<String,Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        configProps.put("security.protocol", securityProtocol);
        configProps.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        configProps.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "poriaKafkaTemplate")
    public KafkaTemplate<String, String> poriaKafkaTemplate() {
        return new KafkaTemplate<>(poriaProducerFactory());
    }

}
