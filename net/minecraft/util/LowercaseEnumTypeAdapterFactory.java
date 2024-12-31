package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public class LowercaseEnumTypeAdapterFactory implements TypeAdapterFactory {
	@Nullable
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
		Class<T> class_ = typeToken.getRawType();
		if (!class_.isEnum()) {
			return null;
		} else {
			final Map<String, T> map = Maps.newHashMap();

			for (T object : class_.getEnumConstants()) {
				map.put(this.getKey(object), object);
			}

			return new TypeAdapter<T>() {
				public void write(JsonWriter jsonWriter, T object) throws IOException {
					if (object == null) {
						jsonWriter.nullValue();
					} else {
						jsonWriter.value(LowercaseEnumTypeAdapterFactory.this.getKey(object));
					}
				}

				@Nullable
				public T read(JsonReader jsonReader) throws IOException {
					if (jsonReader.peek() == JsonToken.NULL) {
						jsonReader.nextNull();
						return null;
					} else {
						return (T)map.get(jsonReader.nextString());
					}
				}
			};
		}
	}

	private String getKey(Object object) {
		return object instanceof Enum ? ((Enum)object).name().toLowerCase(Locale.ROOT) : object.toString().toLowerCase(Locale.ROOT);
	}
}
