package tv.anypoint.kafka.producer;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Created by Administrator on 2015-09-28.
 */
public class RoundRobinPartitioner implements Partitioner {

    public RoundRobinPartitioner(VerifiableProperties props) {}

    @Override
    public int partition(Object key, int numPartitions) {

        String keyStr = key.toString();

        return Integer.parseInt(keyStr) % numPartitions;
    }
}
