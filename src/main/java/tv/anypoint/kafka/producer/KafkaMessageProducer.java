package tv.anypoint.kafka.producer;

import com.zaxxer.hikari.HikariDataSource;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import tv.anypoint.kafka.TestResultReporter;
import tv.anypoint.utils.DateUtils;
import tv.anypoint.utils.FileUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by twjang on 15. 9. 30.
 */
@Slf4j
@Component
public class KafkaMessageProducer {

    private String topic;

    private String datasetDir;

    private static JdbcTemplate jdbcTemplate;


    private static YamlPropertiesFactoryBean yamlFactory;

    private static Properties props;

    private static HikariDataSource dataSource;

    static {

        String configFilePath;

        String userDir = System.getProperty("user.dir");

        String configFile = "/build/classes/main/application.yml";

        if (FileUtils.existsFile(userDir + configFile)) {
            configFilePath = userDir + configFile;
        } else {
            configFilePath = userDir + "/anypoint-kafka-producer.conf";
        }

        yamlFactory = new YamlPropertiesFactoryBean();

        yamlFactory.setResources(new FileSystemResource(configFilePath));

        props = yamlFactory.getObject();

        dataSource = new HikariDataSource();

        dataSource.setDriverClassName(props.getProperty("datasource.driver-class-name"));
        dataSource.setJdbcUrl(props.getProperty("datasource.jdbc-url"));
        dataSource.setUsername(props.getProperty("datasource.username"));
        dataSource.setPassword(props.getProperty("datasource.password"));

        jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

    }

    public KafkaMessageProducer() {

            topic = props.getProperty("kafka.topic.log.collector");
            datasetDir =  props.getProperty("dataset.dir");

            /*if (jdbcTemplate == null) {

                HikariDataSource dataSource = new HikariDataSource();

                dataSource.setDriverClassName(props.getProperty("datasource.driver-class-name"));
                dataSource.setJdbcUrl(props.getProperty("datasource.jdbc-url"));
                dataSource.setUsername(props.getProperty("datasource.username"));
                dataSource.setPassword(props.getProperty("datasource.password"));

                jdbcTemplate.setDataSource(dataSource);
            }*/

    }

    private String convertKSTDateFormat(String target) {

        SimpleDateFormat receFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        SimpleDateFormat tranFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

        Date date;

        try {
            date = receFormat.parse(target);
            target = tranFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return target;
    }

    private Console console = new Console();

    public void process(TextArea textArea, int page, int size, boolean enableTruncateTableJob) {

        console.setTextArea(textArea);
        PrintStream ps = new PrintStream(console, true);

        System.setOut(ps);
        System.setErr(ps);

        String now = DateUtils.getCurrentTimestampAsString();
        String logTimestamp = MessageFormat.format("[{0}] ", now);

        if (enableTruncateTableJob) {

            console.write(logTimestamp + "DB Table Truncate Job :: Start...\n");

            TestResultReporter.truncateTables(jdbcTemplate.getDataSource());

            console.write(logTimestamp + "DB Table Truncate Job :: Done...\n");
        }

        console.write(logTimestamp + "Application :: Start...\n");
        console.write(logTimestamp + "Kafka Producer :: Topic -> " + topic + "\n");

        log.debug(logTimestamp + "Application :: Start...");
        log.debug(logTimestamp + "Kafka Producer :: Topic -> " + topic);

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

        console.write(logTimestamp + "Application :: End...\n");
        console.write(logTimestamp + "Kafka Producer :: elapsed time  -> " + stopWatch.getTotalTimeMillis() + "\n");

        log.debug(logTimestamp + "Application :: End...");
        log.debug(logTimestamp + "Kafka Producer :: elapsed time  -> " + stopWatch.getTotalTimeMillis());

        ps.close();

    }

    public static class Console extends OutputStream {

        private TextArea output;

        public void setTextArea(TextArea textArea) {
            this.output = textArea;
        }

        @Override
        public void write(int i) throws IOException {
            output.appendText(String.valueOf((char) i));
        }

        public void write(String string) {

            for (char c : string.toCharArray()) {
                output.appendText(String.valueOf((c)));
            }
        }
    }



   /* public static void main2(String[] args){

        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("jetstream-spring-test.xml");

        JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");

        TestResultReporter.truncateTables(jdbcTemplate.getDataSource());

        log.debug("KafkaMessageProducer :: Start...");
        log.debug("KafkaMessageProducer :: Topic -> {}", topic);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println(args[0]);
        System.out.println(args[1]);

        int page = StringUtils.isEmpty(args[0]) ?
                1 : Integer.valueOf(args[0].replace("page=", ""));

        int size = StringUtils.isEmpty(args[1]) ?
                1000 : Integer.valueOf(args[1].replace("size=", ""));

        for (int i = 0; i < 10; i++) {

            Runnable worker = new KafkaMessageWorker(topic, jdbcTemplate, page + i, size, page == 1);

            executor.execute(worker);
        }

        executor.shutdown();

        while (!executor.isTerminated()) {}

        stopWatch.stop();

        log.debug("KafkaMessageProducer :: End...");

        log.debug("KafkaMessageProducer :: 수행시간 -> " + stopWatch.getTotalTimeMillis());
    }*/

}