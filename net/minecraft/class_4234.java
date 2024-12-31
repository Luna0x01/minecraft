package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Variant;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class class_4234 implements class_4291 {
	private final List<Variant> field_20822;

	public class_4234(List<Variant> list) {
		this.field_20822 = list;
	}

	public List<Variant> method_19258() {
		return this.field_20822;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof class_4234) {
			class_4234 lv = (class_4234)object;
			return this.field_20822.equals(lv.field_20822);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_20822.hashCode();
	}

	@Override
	public Collection<Identifier> method_19600() {
		return (Collection<Identifier>)this.method_19258().stream().map(Variant::getIdentifier).collect(Collectors.toSet());
	}

	@Override
	public Collection<Identifier> method_19598(Function<Identifier, class_4291> function, Set<String> set) {
		return (Collection<Identifier>)this.method_19258()
			.stream()
			.map(Variant::getIdentifier)
			.distinct()
			.flatMap(identifier -> ((class_4291)function.apply(identifier)).method_19598(function, set).stream())
			.collect(Collectors.toSet());
	}

	@Nullable
	@Override
	public BakedModel method_19599(Function<Identifier, class_4291> function, Function<Identifier, Sprite> function2, ModelRotation modelRotation, boolean bl) {
		if (this.method_19258().isEmpty()) {
			return null;
		} else {
			WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();

			for (Variant variant : this.method_19258()) {
				BakedModel bakedModel = ((class_4291)function.apply(variant.getIdentifier())).method_19599(function, function2, variant.getRotation(), variant.getUvLock());
				builder.add(bakedModel, variant.getWeight());
			}

			return builder.getFirst();
		}
	}

	public static class class_2878 implements JsonDeserializer<class_4234> {
		public class_4234 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			List<Variant> list = Lists.newArrayList();
			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				if (jsonArray.size() == 0) {
					throw new JsonParseException("Empty variant array");
				}

				for (JsonElement jsonElement2 : jsonArray) {
					list.add(jsonDeserializationContext.deserialize(jsonElement2, Variant.class));
				}
			} else {
				list.add(jsonDeserializationContext.deserialize(jsonElement, Variant.class));
			}

			return new class_4234(list);
		}
	}
}
