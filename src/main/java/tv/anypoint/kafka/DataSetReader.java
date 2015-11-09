package tv.anypoint.kafka;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.KryoObjectInput;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import tv.anypoint.domain.ImpressionLog;
import tv.anypoint.utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by twjang on 15. 10. 22.
 */
@Slf4j
public class DataSetReader {

    public List<ImpressionLog> read(String datasetDir, int page, int size) {

        String fileDir;
        String userDir = System.getProperty("user.dir");

        if (FileUtils.existsDir(userDir + "/src/main/resources/config/dataset/")) {
            fileDir = userDir + "/src/main/resources/config/dataset/" + datasetDir + "/";
        } else {
            fileDir = userDir + "/config/dataset/" + datasetDir + "/";
        }

        String datasetPrefix = datasetDir.replaceAll("-", "");

        String fileName = datasetPrefix + "_impression-log_offset_" + String.format("%06d", page) + "_size_" + String.format("%06d", size) + ".jdo";

        List<ImpressionLog> objList;

        try(FileInputStream fis = new FileInputStream(fileDir + fileName);
            ByteBufferInput input = new ByteBufferInput(fis)) {

            Kryo kryo = new Kryo();

            KryoObjectInput objectInput = new KryoObjectInput(kryo, input);

            objList = (List<ImpressionLog>) objectInput.readObject();

        } catch(Exception e) {

            log.error(e.getMessage());
            return null;
        }

        return objList;
    }
}