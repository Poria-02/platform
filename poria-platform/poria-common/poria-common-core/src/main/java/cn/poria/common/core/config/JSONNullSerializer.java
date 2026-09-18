package cn.poria.common.core.config;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class JSONNullSerializer extends JsonSerializer<JSONNull> {
    @Override
    public void serialize(JSONNull value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeNull();
    }
}
