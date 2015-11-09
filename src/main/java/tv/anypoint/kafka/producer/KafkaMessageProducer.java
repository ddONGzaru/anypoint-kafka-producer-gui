package tv.anypoint.kafka.producer;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import tv.anypoint.kafka.TestResultReporter;
import tv.anypoint.utils.DateUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by twjang on 15. 9. 30.
 */
@Slf4j
@Component
public class KafkaMessageProducer {

    @Value("${kafka.topic.log.collector}")
    private String topic;

    @Value("${kafka.msg.max.rows}")
    private int msgMaxRows;

    @Value("${dataset.dir}")
    private String datasetDir;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void process(TextArea console, int page, int size, boolean enableTruncateTableJob) {

        console.clear();

        if (enableTruncateTableJob) {
            TestResultReporter.truncateTables(jdbcTemplate.getDataSource());
        }

        log.debug("===================================");
        log.debug("Application :: Start..." );
        log.debug("Kafka Producer :: Topic -> " + topic);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        int producerThreadNums = 10;

        if (size == 300000) {
            producerThreadNums = 30;
            size = 100000;
        }

        for (int i = 0; i < producerThreadNums; i++) {

            Runnable worker = new KafkaMessageWorker(topic, jdbcTemplate, page + i, size, page == 1, datasetDir, msgMaxRows);

            executor.execute(worker);
        }

        executor.shutdown();

        while (!executor.isTerminated()) {}

        stopWatch.stop();

        //console.write(logTimestamp + "Application :: End...\n");
        //console.write(logTimestamp + "Kafka Producer :: elapsed time  -> " + stopWatch.getTotalTimeMillis() + "\n");

        log.debug("Application :: End...");
        log.debug("Kafka Producer :: elapsed time  -> "
                + Double.parseDouble(stopWatch.getTotalTimeMillis() / 1000 + "." + stopWatch.getTotalTimeMillis() % 1000));
        log.debug("===================================");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Anypoint Kafka Producer ver-1.0.3");
        alert.setHeaderText("작업이 완료되었습니다.");
        alert.setContentText("Kafka Producer :: elapsed time  -> "
                + Double.parseDouble(stopWatch.getTotalTimeMillis() / 1000 + "." + stopWatch.getTotalTimeMillis() % 1000));

        alert.showAndWait();

    }



}