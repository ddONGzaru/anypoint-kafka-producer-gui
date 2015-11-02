package tv.anypoint.kafka;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by twjang on 15. 10. 22.
 */
@Slf4j
public class TopicDeleter {

    private static final String ZK_SERVER = "localhost:2181";
    private static final String TOPIC_NAME = "anypoint.test";


    /*public static void main(String[] args) {

        int sessionTimeoutMs = 10000;
        int connectionTimeoutMs = 10000;

        ZkClient zkClient = new ZkClient(ZK_SERVER, sessionTimeoutMs, connectionTimeoutMs, ZKStringSerializer$.MODULE$);

        if (AdminUtils.topicExists(zkClient, TOPIC_NAME)) {
            AdminUtils.deleteTopic(zkClient, TOPIC_NAME);
        } else {
            log.error("해당 토픽[{}]이 존재하지 않습니다.", TOPIC_NAME);
        }

        *//*List<String> fileNames = FileUtils.listFileNames("/home/twjang/dev_home/repo/anypoint.tv/anypoint-log-collector/src/test/resources/dataset/2015-10-26", false);

        fileNames.forEach(fileName -> FileUtils.rename(fileName, fileName.replace("ImpressionLog", "impression-log").replace("PAGE","offset").replace("SIZE","size")));*//*

    }*/

}
