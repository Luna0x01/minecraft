package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.class_2874;
import net.minecraft.client.class_2876;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4231 implements class_4291 {
	private static final Logger field_20789 = LogManager.getLogger();
	private static final ItemModelGenerator field_20790 = new ItemModelGenerator();
	private static final class_4233 field_20791 = new class_4233();
	@VisibleForTesting
	static final Gson field_20784 = new GsonBuilder()
		.registerTypeAdapter(class_4231.class, new class_4231.class_2447())
		.registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
		.registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
		.registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
		.registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
		.registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
		.registerTypeAdapter(class_2874.class, new class_2874.class_2875())
		.create();
	private final List<ModelElement> field_20792;
	private final boolean field_20793;
	private final boolean field_20794;
	private final ModelTransformation field_20795;
	private final List<class_2874> field_20796;
	public String field_20785 = "";
	@VisibleForTesting
	protected final Map<String, String> field_20786;
	@VisibleForTesting
	class_4231 field_20787;
	@VisibleForTesting
	Identifier field_20788;

	public static class_4231 method_19216(Reader reader) {
		return JsonHelper.deserialize(field_20784, reader, class_4231.class);
	}

	public static class_4231 method_19217(String string) {
		return method_19216(new StringReader(string));
	}

	public class_4231(
		@Nullable Identifier identifier,
		List<ModelElement> list,
		Map<String, String> map,
		boolean bl,
		boolean bl2,
		ModelTransformation modelTransformation,
		List<class_2874> list2
	) {
		this.field_20792 = list;
		this.field_20794 = bl;
		this.field_20793 = bl2;
		this.field_20786 = map;
		this.field_20788 = identifier;
		this.field_20795 = modelTransformation;
		this.field_20796 = list2;
	}

	public List<ModelElement> method_19210() {
		return this.field_20792.isEmpty() && this.method_19231() ? this.field_20787.method_19210() : this.field_20792;
	}

	private boolean method_19231() {
		return this.field_20787 != null;
	}

	public boolean method_19222() {
		return this.method_19231() ? this.field_20787.method_19222() : this.field_20794;
	}

	public boolean method_19224() {
		return this.field_20793;
	}

	public boolean method_19226() {
		return this.field_20788 == null || this.field_20787 != null && this.field_20787.method_19226();
	}

	private void method_19220(Function<Identifier, class_4291> function) {
		if (this.field_20788 != null) {
			class_4291 lv = (class_4291)function.apply(this.field_20788);
			if (lv != null) {
				if (!(lv instanceof class_4231)) {
					throw new IllegalStateException("BlockModel parent has to be a block model.");
				}

				this.field_20787 = (class_4231)lv;
			}
		}
	}

	public List<class_2874> method_19228() {
		return this.field_20796;
	}

	private class_2876 method_19213(class_4231 arg, Function<Identifier, class_4291> function, Function<Identifier, Sprite> function2) {
		return this.field_20796.isEmpty() ? class_2876.field_13564 : new class_2876(arg, function, function2, this.field_20796);
	}

	@Override
	public Collection<Identifier> method_19600() {
		Set<Identifier> set = Sets.newHashSet();

		for (class_2874 lv : this.field_20796) {
			set.add(lv.method_12368());
		}

		if (this.field_20788 != null) {
			set.add(this.field_20788);
		}

		return set;
	}

	@Override
	public Collection<Identifier> method_19598(Function<Identifier, class_4291> function, Set<String> set) {
		if (!this.method_19226()) {
			Set<class_4231> set2 = Sets.newLinkedHashSet();
			class_4231 lv = this;

			do {
				set2.add(lv);
				lv.method_19220(function);
				if (set2.contains(lv.field_20787)) {
					field_20789.warn(
						"Found 'parent' loop while loading model '{}' in chain: {} -> {}",
						lv.field_20785,
						set2.stream().map(arg -> arg.field_20785).collect(Collectors.joining(" -> ")),
						lv.field_20787.field_20785
					);
					lv.field_20788 = class_4288.field_21079;
					lv.method_19220(function);
				}

				lv = lv.field_20787;
			} while (!lv.method_19226());
		}

		Set<Identifier> set3 = Sets.newHashSet(new Identifier[]{new Identifier(this.method_19225("particle"))});

		for (ModelElement modelElement : this.method_19210()) {
			for (ModelElementFace modelElementFace : modelElement.faces.values()) {
				String string = this.method_19225(modelElementFace.textureId);
				if (Objects.equals(string, class_4276.method_19454().method_5348().toString())) {
					set.add(String.format("%s in %s", modelElementFace.textureId, this.field_20785));
				}

				set3.add(new Identifier(string));
			}
		}

		this.field_20796.forEach(arg -> {
			class_4291 lvx = (class_4291)function.apply(arg.method_12368());
			if (!Objects.equals(lvx, this)) {
				set3.addAll(lvx.method_19598(function, set));
			}
		});
		if (this.method_19229() == class_4288.field_21081) {
			ItemModelGenerator.LAYERS.forEach(stringx -> set3.add(new Identifier(this.method_19225(stringx))));
		}

		return set3;
	}

	@Override
	public BakedModel method_19599(Function<Identifier, class_4291> function, Function<Identifier, Sprite> function2, ModelRotation modelRotation, boolean bl) {
		return this.method_19214(this, function, function2, modelRotation, bl);
	}

	private BakedModel method_19214(
		class_4231 arg, Function<Identifier, class_4291> function, Function<Identifier, Sprite> function2, ModelRotation modelRotation, boolean bl
	) {
		class_4231 lv = this.method_19229();
		if (lv == class_4288.field_21081) {
			return field_20790.method_19252(function2, this).method_19214(arg, function, function2, modelRotation, bl);
		} else if (lv == class_4288.field_21082) {
			return new BuiltinBakedModel(this.method_19230(), this.method_19213(arg, function, function2));
		} else {
			Sprite sprite = (Sprite)function2.apply(new Identifier(this.method_19225("particle")));
			BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.method_19213(arg, function, function2)).setParticle(sprite);

			for (ModelElement modelElement : this.method_19210()) {
				for (Direction direction : modelElement.faces.keySet()) {
					ModelElementFace modelElementFace = (ModelElementFace)modelElement.faces.get(direction);
					Sprite sprite2 = (Sprite)function2.apply(new Identifier(this.method_19225(modelElementFace.textureId)));
					if (modelElementFace.cullFace == null) {
						builder.addQuad(method_19211(modelElement, modelElementFace, sprite2, direction, modelRotation, bl));
					} else {
						builder.addQuad(modelRotation.rotate(modelElementFace.cullFace), method_19211(modelElement, modelElementFace, sprite2, direction, modelRotation, bl));
					}
				}
			}

			return builder.build();
		}
	}

	private static BakedQuad method_19211(
		ModelElement modelElement, ModelElementFace modelElementFace, Sprite sprite, Direction direction, ModelRotation modelRotation, boolean bl
	) {
		return field_20791.method_19242(
			modelElement.field_20777, modelElement.field_20778, modelElementFace, sprite, direction, modelRotation, modelElement.field_10913, bl, modelElement.shade
		);
	}

	public boolean method_19223(String string) {
		return !class_4276.method_19454().method_5348().toString().equals(this.method_19225(string));
	}

	public String method_19225(String string) {
		if (!this.method_19227(string)) {
			string = '#' + string;
		}

		return this.method_19218(string, new class_4231.class_2446(this));
	}

	private String method_19218(String string, class_4231.class_2446 arg) {
		if (this.method_19227(string)) {
			if (this == arg.field_10938) {
				field_20789.warn("Unable to resolve texture due to upward reference: {} in {}", string, this.field_20785);
				return class_4276.method_19454().method_5348().toString();
			} else {
				String string2 = (String)this.field_20786.get(string.substring(1));
				if (string2 == null && this.method_19231()) {
					string2 = this.field_20787.method_19218(string, arg);
				}

				arg.field_10938 = this;
				if (string2 != null && this.method_19227(string2)) {
					string2 = arg.field_10937.method_19218(string2, arg);
				}

				return string2 != null && !this.method_19227(string2) ? string2 : class_4276.method_19454().method_5348().toString();
			}
		} else {
			return string;
		}
	}

	private boolean method_19227(String string) {
		return string.charAt(0) == '#';
	}

	public class_4231 method_19229() {
		return this.method_19231() ? this.field_20787.method_19229() : this;
	}

	public ModelTransformation method_19230() {
		Transformation transformation = this.method_19215(ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND);
		Transformation transformation2 = this.method_19215(ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
		Transformation transformation3 = this.method_19215(ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND);
		Transformation transformation4 = this.method_19215(ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND);
		Transformation transformation5 = this.method_19215(ModelTransformation.Mode.HEAD);
		Transformation transformation6 = this.method_19215(ModelTransformation.Mode.GUI);
		Transformation transformation7 = this.method_19215(ModelTransformation.Mode.GROUND);
		Transformation transformation8 = this.method_19215(ModelTransformation.Mode.FIXED);
		return new ModelTransformation(
			transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8
		);
	}

	private Transformation method_19215(ModelTransformation.Mode mode) {
		return this.field_20787 != null && !this.field_20795.isTransformationDefined(mode)
			? this.field_20787.method_19215(mode)
			: this.field_20795.getTransformation(mode);
	}

	static final class class_2446 {
		public final class_4231 field_10937;
		public class_4231 field_10938;

		private class_2446(class_4231 arg) {
			this.field_10937 = arg;
		}
	}

	public static class class_2447 implements JsonDeserializer<class_4231> {
		public class_4231 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			List<ModelElement> list = this.method_10024(jsonDeserializationContext, jsonObject);
			String string = this.method_10028(jsonObject);
			Map<String, String> map = this.method_10027(jsonObject);
			boolean bl = this.method_10026(jsonObject);
			ModelTransformation modelTransformation = ModelTransformation.NONE;
			if (jsonObject.has("display")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
				modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize(jsonObject2, ModelTransformation.class);
			}

			List<class_2874> list2 = this.method_12355(jsonDeserializationContext, jsonObject);
			Identifier identifier = string.isEmpty() ? null : new Identifier(string);
			return new class_4231(identifier, list, map, bl, true, modelTransformation, list2);
		}

		protected List<class_2874> method_12355(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<class_2874> list = Lists.newArrayList();
			if (jsonObject.has("overrides")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "overrides")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, class_2874.class));
				}
			}

			return list;
		}

		private Map<String, String> method_10027(JsonObject jsonObject) {
			Map<String, String> map = Maps.newHashMap();
			if (jsonObject.has("textures")) {
				JsonObject jsonObject2 = jsonObject.getAsJsonObject("textures");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
				}
			}

			return map;
		}

		private String method_10028(JsonObject jsonObject) {
			return JsonHelper.getString(jsonObject, "parent", "");
		}

		protected boolean method_10026(JsonObject jsonObject) {
			return JsonHelper.getBoolean(jsonObject, "ambientocclusion", true);
		}

		protected List<ModelElement> method_10024(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<ModelElement> list = Lists.newArrayList();
			if (jsonObject.has("elements")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "elements")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, ModelElement.class));
				}
			}

			return list;
		}
	}
}
