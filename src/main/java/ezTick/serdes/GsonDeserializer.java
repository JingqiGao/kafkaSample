package ezTick.serdes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class GsonDeserializer<T> implements Deserializer<T> {

    public static final String CONFIG_VALUE_CLASS = "value.class.name";
    public static final String CONFIG_KEY_CLASS = "key.class.name";
    private Class<T> className;

    private Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();


    @Override
    public void configure(Map<String, ?> config, boolean isKey) {
        if (isKey)
            className = (Class<T>) config.get(CONFIG_KEY_CLASS);
        else
            className = (Class<T>) config.get(CONFIG_VALUE_CLASS);
    }


    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return (T) gson.fromJson(new String(bytes), className);
    }


    @Override
    public void close() {}
}
