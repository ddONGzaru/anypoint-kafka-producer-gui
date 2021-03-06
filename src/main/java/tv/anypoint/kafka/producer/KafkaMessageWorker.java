package tv.anypoint.kafka.producer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Lists;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import tv.anypoint.domain.ImpressionLog;
import tv.anypoint.kafka.DataSetReader;
import tv.anypoint.utils.DateUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by twjang on 15. 10. 7.
 */
@Slf4j
public class KafkaMessageWorker implements Runnable {

    private String topic;

    private Producer<String, byte[]> producer;

    private JdbcTemplate jdbcTemplate;

    private int page;

    private int size;

    private boolean enableCSVFileWriteJob = false;

    private boolean enableDataSetWriteJob = false;

    private String datasetDir = "2015-10-28";

    private int msgMaxRows;

    NumberFormat numberFormat = NumberFormat.getNumberInstance();

    KafkaMessageWorker(String topic, JdbcTemplate jdbcTemplate, int page, int size,
           boolean decreaseIndex, String datasetDir, int msgMaxRows) {

        this.topic = topic;
        this.jdbcTemplate = jdbcTemplate;

        this.page = (decreaseIndex) ? --page * size : page * size;
        this.size = size;

        this.datasetDir = datasetDir;
        this.msgMaxRows = msgMaxRows;

        producer = ProducerFactory.getInstance();
    }

    @Override
    public void run() {

        DataSetReader reader = new DataSetReader();

        List<ImpressionLog> messageList = reader.read(datasetDir, page, size);


        List<List<KeyedMessage<String, byte[]>>> sendMsgList = Lists.newArrayList();
        List<KeyedMessage<String, byte[]>> sendMsgUnitList = Lists.newArrayList();

        int index = 0;

        for (ImpressionLog message : messageList) {

            index++;

            sendMsgUnitList.add(new KeyedMessage<String, byte[]>(topic, generateKey(), toByteArray(message)));

            if (index % msgMaxRows == 0) {

                sendMsgList.add(sendMsgUnitList);
                sendMsgUnitList = Lists.newArrayList();

                continue;
            }

            if (index == messageList.size()) {
                sendMsgList.add(sendMsgUnitList);
            }
        }

        sendMsgList.forEach(msgList -> producer.send(msgList));

        log.debug("Dataset 레코드 총계: {}", numberFormat.format(messageList.size()));

        producer.close();

    }

    private ImpressionLog convertMapToMessage(Map resultMap) {

        ImpressionLog message = new ImpressionLog();

        message.setId(String.valueOf(resultMap.get("id")));
        message.setAsset((Long) resultMap.get("asset"));
        message.setCampaign((Long) resultMap.get("campaign"));
        message.setCpv((Integer) resultMap.get("cpv"));
        message.setCueOwner((Integer) resultMap.get("cueOwner"));
        message.setDevice((Long) resultMap.get("device"));
        message.setImpressionTime((Date) resultMap.get("impressionTime"));
        message.setError((Boolean) resultMap.get("isError"));
        message.setPlayTime((Integer) resultMap.get("playTime"));
        message.setProgramProvider((Integer) resultMap.get("programProvider"));
        message.setRegion1((Integer) resultMap.get("region1"));
        message.setRegion2((Integer) resultMap.get("region2"));
        message.setRegion3((Integer) resultMap.get("region3"));
        message.setRegion4((Integer) resultMap.get("region4"));
        message.setServiceOperator((Integer) resultMap.get("serviceOperator"));
        message.setVtr((Float) resultMap.get("vtr"));
        message.setZipCode((Integer) resultMap.get("zipCode"));

        /*message.setId(String.valueOf(resultMap.get("id")));
        message.setAsset((Long) resultMap.get("asset"));
        message.setCampaign((Long) resultMap.get("campaign"));
        message.setCpv((Integer) resultMap.get("cpv"));
        message.setCueOwner((Boolean) resultMap.get("cue_owner") ? 1 : 0);
        message.setDevice((Long) resultMap.get("device"));
        message.setImpressionTime((Date) resultMap.get("impression_time"));
        message.setError((Boolean) resultMap.get("is_error"));
        message.setPlayTime((Integer) resultMap.get("play_time"));
        message.setProgramProvider((Integer) resultMap.get("program_provider"));
        message.setRegion1((Integer) resultMap.get("region1"));
        message.setRegion2((Integer) resultMap.get("region2"));
        message.setRegion3((Integer) resultMap.get("region3"));
        message.setRegion4((Integer) resultMap.get("region4"));
        message.setServiceOperator((Integer) resultMap.get("service_operator"));
        message.setVtr((Float) resultMap.get("vtr"));
        message.setZipCode((Integer) resultMap.get("zip_code"));*/

        return message;

    }

    private Kryo kryo = new Kryo();

    private byte[] toByteArray(ImpressionLog impressionLog) {

        kryo.register(ImpressionLog.class);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(16384);
             Output output = new Output(byteArrayOutputStream)) {

            kryo.writeObject(output, impressionLog);
            output.close();

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            log.error("[KafkaMessageWorker toByteArray ERROR] : {} ", e.getMessage());
            return null;
        }
    }

    private AtomicInteger keyCounter = new AtomicInteger(0);

    private String generateKey() {

        int keyIndex = keyCounter.getAndIncrement();

        if (keyIndex == Integer.MAX_VALUE) {
            keyCounter.set(0);
            return "0";
        }

        return Integer.toString(keyIndex);
    }

}
