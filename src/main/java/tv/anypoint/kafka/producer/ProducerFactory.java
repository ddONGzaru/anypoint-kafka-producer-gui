package tv.anypoint.kafka.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Created by twjang on 15. 10. 26.
 */
@Component
public class ProducerFactory {

    private static Producer<String, byte[]> producer;

    public static Producer<String, byte[]> getInstance() {

        if (producer == null) {
            return buildProducer();
        } else {
            return producer;
        }
    }


    private static String metadataBrokerList;

    @Value("${kafka.metadata.broker.list}")
    public void setMetadataBrokerList(String metadataBrokerList) {
        ProducerFactory.metadataBrokerList = metadataBrokerList;

        System.out.println("broker list: " + metadataBrokerList);
    }

    private static Producer<String, byte[]> buildProducer() {

        Properties props = new Properties();

        props.put("metadata.broker.list", metadataBrokerList);
        props.put("partitioner.class", RoundRobinPartitioner.class.getName());
        props.put("compression.codec", "2");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");

        ProducerConfig producerConfig = new ProducerConfig(props);

        return new Producer<>(producerConfig);
    }
}
