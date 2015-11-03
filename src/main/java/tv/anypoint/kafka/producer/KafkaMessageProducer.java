package tv.anypoint.kafka.producer;

import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import tv.anypoint.kafka.TestResultReporter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by twjang on 15. 9. 30.
 */
@Slf4j
@Component
public class KafkaMessageProducer {

    @Value("kafka.topic.log.collector")
    private String topic;

    @Value("dataset.dir")
    private String datasetDir;

    @Autowired
    private static JdbcTemplate jdbcTemplate;

    public void process(TextArea textArea, int page, int size, boolean enableTruncateTableJob) {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (enableTruncateTableJob) {
            //TestResultReporter.truncateTables(jdbcTemplate.getDataSource());
        }

        log.debug("Application :: Start..." );
        log.debug("Kafka Producer :: Topic -> " + topic);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {

            Runnable worker = new KafkaMessageWorker(topic, jdbcTemplate, page + i, size, page == 1, datasetDir, textArea);

            executor.execute(worker);
        }

        executor.shutdown();

        while (!executor.isTerminated()) {}

        stopWatch.stop();

        //console.write(logTimestamp + "Application :: End...\n");
        //console.write(logTimestamp + "Kafka Producer :: elapsed time  -> " + stopWatch.getTotalTimeMillis() + "\n");

        log.debug("Application :: End...");
        log.debug("Kafka Producer :: elapsed time  -> " + stopWatch.getTotalTimeMillis());


    }



}