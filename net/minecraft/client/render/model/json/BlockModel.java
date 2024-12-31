package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockModel {
	private static final Logger LOGGER = LogManager.getLogger();
	static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer())
		.registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
		.registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
		.registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
		.registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
		.registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
		.create();
	private final List<ModelElement> elements;
	private final boolean depth;
	private final boolean ambientOcclusion;
	private ModelTransformation transformation;
	public String field_10928 = "";
	protected final Map<String, String> textureMap;
	protected BlockModel parent;
	protected Identifier id;

	public static BlockModel getFromReader(Reader reader) {
		return (BlockModel)GSON.fromJson(reader, BlockModel.class);
	}

	public static BlockModel create(String value) {
		return getFromReader(new StringReader(value));
	}

	protected BlockModel(List<ModelElement> list, Map<String, String> map, boolean bl, boolean bl2, ModelTransformation modelTransformation) {
		this(null, list, map, bl, bl2, modelTransformation);
	}

	protected BlockModel(Identifier identifier, Map<String, String> map, boolean bl, boolean bl2, ModelTransformation modelTransformation) {
		this(identifier, Collections.emptyList(), map, bl, bl2, modelTransformation);
	}

	private BlockModel(Identifier identifier, List<ModelElement> list, Map<String, String> map, boolean bl, boolean bl2, ModelTransformation modelTransformation) {
		this.elements = list;
		this.ambientOcclusion = bl;
		this.depth = bl2;
		this.textureMap = map;
		this.id = identifier;
		this.transformation = modelTransformation;
	}

	public List<ModelElement> getElements() {
		return this.hasParent() ? this.parent.getElements() : this.elements;
	}

	private boolean hasParent() {
		return this.parent != null;
	}

	public boolean hasAmbientOcclusion() {
		return this.hasParent() ? this.parent.hasAmbientOcclusion() : this.ambientOcclusion;
	}

	public boolean hasDepth() {
		return this.depth;
	}

	public boolean method_10018() {
		return this.id == null || this.parent != null && this.parent.method_10018();
	}

	public void refreshParent(Map<Identifier, BlockModel> models) {
		if (this.id != null) {
			this.parent = (BlockModel)models.get(this.id);
		}
	}

	public boolean isValidTexture(String texture) {
		return !"missingno".equals(this.resolveTexture(texture));
	}

	public String resolveTexture(String texture) {
		if (!this.isValidTextureReference(texture)) {
			texture = '#' + texture;
		}

		return this.resolveTexture(texture, new BlockModel.ModelHolder(this));
	}

	private String resolveTexture(String texture, BlockModel.ModelHolder modelHolder) {
		if (this.isValidTextureReference(texture)) {
			if (this == modelHolder.parent) {
				LOGGER.warn("Unable to resolve texture due to upward reference: " + texture + " in " + this.field_10928);
				return "missingno";
			} else {
				String string = (String)this.textureMap.get(texture.substring(1));
				if (string == null && this.hasParent()) {
					string = this.parent.resolveTexture(texture, modelHolder);
				}

				modelHolder.parent = this;
				if (string != null && this.isValidTextureReference(string)) {
					string = modelHolder.model.resolveTexture(string, modelHolder);
				}

				return string != null && !this.isValidTextureReference(string) ? string : "missingno";
			}
		} else {
			return texture;
		}
	}

	private boolean isValidTextureReference(String texture) {
		return texture.charAt(0) == '#';
	}

	public Identifier getId() {
		return this.id;
	}

	public BlockModel getRootModel() {
		return this.hasParent() ? this.parent.getRootModel() : this;
	}

	public ModelTransformation getTransformation() {
		Transformation transformation = this.getTransformation(ModelTransformation.Mode.THIRD_PERSON);
		Transformation transformation2 = this.getTransformation(ModelTransformation.Mode.FIRST_PERSON);
		Transformation transformation3 = this.getTransformation(ModelTransformation.Mode.HEAD);
		Transformation transformation4 = this.getTransformation(ModelTransformation.Mode.GUI);
		Transformation transformation5 = this.getTransformation(ModelTransformation.Mode.GROUND);
		Transformation transformation6 = this.getTransformation(ModelTransformation.Mode.FIXED);
		return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6);
	}

	private Transformation getTransformation(ModelTransformation.Mode mode) {
		return this.parent != null && !this.transformation.isTransformationDefined(mode)
			? this.parent.getTransformation(mode)
			: this.transformation.getTransformation(mode);
	}

	public static void method_10015(Map<Identifier, BlockModel> map) {
		for (BlockModel blockModel : map.values()) {
			try {
				BlockModel blockModel2 = blockModel.parent;

				for (BlockModel blockModel3 = blockModel2.parent; blockModel2 != blockModel3; blockModel3 = blockModel3.parent.parent) {
					blockModel2 = blockModel2.parent;
				}

				throw new BlockModel.LoopException();
			} catch (NullPointerException var5) {
			}
		}
	}

	public static class Deserializer implements JsonDeserializer<BlockModel> {
		public BlockModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			List<ModelElement> list = this.getElements(jsonDeserializationContext, jsonObject);
			String string = this.getParentId(jsonObject);
			boolean bl = StringUtils.isEmpty(string);
			boolean bl2 = list.isEmpty();
			if (bl2 && bl) {
				throw new JsonParseException("BlockModel requires either elements or parent, found neither");
			} else if (!bl && !bl2) {
				throw new JsonParseException("BlockModel requires either elements or parent, found both");
			} else {
				Map<String, String> map = this.getTextureMap(jsonObject);
				boolean bl3 = this.getAmbientOcclusion(jsonObject);
				ModelTransformation modelTransformation = ModelTransformation.NONE;
				if (jsonObject.has("display")) {
					JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
					modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize(jsonObject2, ModelTransformation.class);
				}

				return bl2 ? new BlockModel(new Identifier(string), map, bl3, true, modelTransformation) : new BlockModel(list, map, bl3, true, modelTransformation);
			}
		}

		private Map<String, String> getTextureMap(JsonObject json) {
			Map<String, String> map = Maps.newHashMap();
			if (json.has("textures")) {
				JsonObject jsonObject = json.getAsJsonObject("textures");

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					map.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
				}
			}

			return map;
		}

		private String getParentId(JsonObject json) {
			return JsonHelper.getString(json, "parent", "");
		}

		protected boolean getAmbientOcclusion(JsonObject json) {
			return JsonHelper.getBoolean(json, "ambientocclusion", true);
		}

		protected List<ModelElement> getElements(JsonDeserializationContext ctx, JsonObject json) {
			List<ModelElement> list = Lists.newArrayList();
			if (json.has("elements")) {
				for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
					list.add((ModelElement)ctx.deserialize(jsonElement, ModelElement.class));
				}
			}

			return list;
		}
	}

	public static class LoopException extends RuntimeException {
	}

	static final class ModelHolder {
		public final BlockModel model;
		public BlockModel parent;

		private ModelHolder(BlockModel blockModel) {
			this.model = blockModel;
		}
	}
}
