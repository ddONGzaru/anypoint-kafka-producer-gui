package tv.anypoint.kafka;

/**
 * Created by twjang on 15. 9. 30.
 */
public class TopicCreator {

    //private static final String TOPIC_NAME = "realtime.analytics.test.0001";
    private static final String TOPIC_NAME = "analytics.reporter.test.0002";
    private static final int NUM_PARTITIONS = 4;
    private static final int REPLICATION_FACTOR = 2;


    /*public static void main(String[] args) {

        int sessionTimeoutMs = 10000;
        int connectionTimeoutMs = 10000;

        String zkServer = "localhost:2181";

        ZkClient zkClient = new ZkClient(zkServer, sessionTimeoutMs, connectionTimeoutMs, ZKStringSerializer$.MODULE$);

        AdminUtils.createTopic(zkClient, TOPIC_NAME, NUM_PARTITIONS, REPLICATION_FACTOR, new Properties());
    }*/

}
