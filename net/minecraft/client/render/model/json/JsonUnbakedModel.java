package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
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
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonUnbakedModel implements UnbakedModel {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
	@VisibleForTesting
	static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(JsonUnbakedModel.class, new JsonUnbakedModel.Deserializer())
		.registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
		.registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
		.registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
		.registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
		.registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
		.registerTypeAdapter(ModelItemOverride.class, new ModelItemOverride.Deserializer())
		.create();
	private final List<ModelElement> elements;
	@Nullable
	private final JsonUnbakedModel.GuiLight guiLight;
	private final boolean ambientOcclusion;
	private final ModelTransformation transformations;
	private final List<ModelItemOverride> overrides;
	public String id = "";
	@VisibleForTesting
	protected final Map<String, Either<SpriteIdentifier, String>> textureMap;
	@Nullable
	protected JsonUnbakedModel parent;
	@Nullable
	protected Identifier parentId;

	public static JsonUnbakedModel deserialize(Reader reader) {
		return JsonHelper.deserialize(GSON, reader, JsonUnbakedModel.class);
	}

	public static JsonUnbakedModel deserialize(String string) {
		return deserialize(new StringReader(string));
	}

	public JsonUnbakedModel(
		@Nullable Identifier identifier,
		List<ModelElement> list,
		Map<String, Either<SpriteIdentifier, String>> map,
		boolean bl,
		@Nullable JsonUnbakedModel.GuiLight guiLight,
		ModelTransformation modelTransformation,
		List<ModelItemOverride> list2
	) {
		this.elements = list;
		this.ambientOcclusion = bl;
		this.guiLight = guiLight;
		this.textureMap = map;
		this.parentId = identifier;
		this.transformations = modelTransformation;
		this.overrides = list2;
	}

	public List<ModelElement> getElements() {
		return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
	}

	public boolean useAmbientOcclusion() {
		return this.parent != null ? this.parent.useAmbientOcclusion() : this.ambientOcclusion;
	}

	public JsonUnbakedModel.GuiLight getGuiLight() {
		if (this.guiLight != null) {
			return this.guiLight;
		} else {
			return this.parent != null ? this.parent.getGuiLight() : JsonUnbakedModel.GuiLight.field_21859;
		}
	}

	public List<ModelItemOverride> getOverrides() {
		return this.overrides;
	}

	private ModelItemPropertyOverrideList compileOverrides(ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel) {
		return this.overrides.isEmpty()
			? ModelItemPropertyOverrideList.EMPTY
			: new ModelItemPropertyOverrideList(modelLoader, jsonUnbakedModel, modelLoader::getOrLoadModel, this.overrides);
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		Set<Identifier> set = Sets.newHashSet();

		for (ModelItemOverride modelItemOverride : this.overrides) {
			set.add(modelItemOverride.getModelId());
		}

		if (this.parentId != null) {
			set.add(this.parentId);
		}

		return set;
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> set) {
		Set<UnbakedModel> set2 = Sets.newLinkedHashSet();

		for (JsonUnbakedModel jsonUnbakedModel = this;
			jsonUnbakedModel.parentId != null && jsonUnbakedModel.parent == null;
			jsonUnbakedModel = jsonUnbakedModel.parent
		) {
			set2.add(jsonUnbakedModel);
			UnbakedModel unbakedModel = (UnbakedModel)function.apply(jsonUnbakedModel.parentId);
			if (unbakedModel == null) {
				LOGGER.warn("No parent '{}' while loading model '{}'", this.parentId, jsonUnbakedModel);
			}

			if (set2.contains(unbakedModel)) {
				LOGGER.warn(
					"Found 'parent' loop while loading model '{}' in chain: {} -> {}",
					jsonUnbakedModel,
					set2.stream().map(Object::toString).collect(Collectors.joining(" -> ")),
					this.parentId
				);
				unbakedModel = null;
			}

			if (unbakedModel == null) {
				jsonUnbakedModel.parentId = ModelLoader.MISSING;
				unbakedModel = (UnbakedModel)function.apply(jsonUnbakedModel.parentId);
			}

			if (!(unbakedModel instanceof JsonUnbakedModel)) {
				throw new IllegalStateException("BlockModel parent has to be a block model.");
			}

			jsonUnbakedModel.parent = (JsonUnbakedModel)unbakedModel;
		}

		Set<SpriteIdentifier> set3 = Sets.newHashSet(new SpriteIdentifier[]{this.resolveSprite("particle")});

		for (ModelElement modelElement : this.getElements()) {
			for (ModelElementFace modelElementFace : modelElement.faces.values()) {
				SpriteIdentifier spriteIdentifier = this.resolveSprite(modelElementFace.textureId);
				if (Objects.equals(spriteIdentifier.getTextureId(), MissingSprite.getMissingSpriteId())) {
					set.add(Pair.of(modelElementFace.textureId, this.id));
				}

				set3.add(spriteIdentifier);
			}
		}

		this.overrides.forEach(modelItemOverride -> {
			UnbakedModel unbakedModelx = (UnbakedModel)function.apply(modelItemOverride.getModelId());
			if (!Objects.equals(unbakedModelx, this)) {
				set3.addAll(unbakedModelx.getTextureDependencies(function, set));
			}
		});
		if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
			ItemModelGenerator.LAYERS.forEach(string -> set3.add(this.resolveSprite(string)));
		}

		return set3;
	}

	@Override
	public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		return this.bake(modelLoader, this, function, modelBakeSettings, identifier, true);
	}

	public BakedModel bake(
		ModelLoader modelLoader,
		JsonUnbakedModel jsonUnbakedModel,
		Function<SpriteIdentifier, Sprite> function,
		ModelBakeSettings modelBakeSettings,
		Identifier identifier,
		boolean bl
	) {
		Sprite sprite = (Sprite)function.apply(this.resolveSprite("particle"));
		if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
			return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(modelLoader, jsonUnbakedModel), sprite, this.getGuiLight().isSide());
		} else {
			BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.compileOverrides(modelLoader, jsonUnbakedModel), bl).setParticle(sprite);

			for (ModelElement modelElement : this.getElements()) {
				for (Direction direction : modelElement.faces.keySet()) {
					ModelElementFace modelElementFace = (ModelElementFace)modelElement.faces.get(direction);
					Sprite sprite2 = (Sprite)function.apply(this.resolveSprite(modelElementFace.textureId));
					if (modelElementFace.cullFace == null) {
						builder.addQuad(createQuad(modelElement, modelElementFace, sprite2, direction, modelBakeSettings, identifier));
					} else {
						builder.addQuad(
							Direction.transform(modelBakeSettings.getRotation().getMatrix(), modelElementFace.cullFace),
							createQuad(modelElement, modelElementFace, sprite2, direction, modelBakeSettings, identifier)
						);
					}
				}
			}

			return builder.build();
		}
	}

	private static BakedQuad createQuad(
		ModelElement modelElement, ModelElementFace modelElementFace, Sprite sprite, Direction direction, ModelBakeSettings modelBakeSettings, Identifier identifier
	) {
		return QUAD_FACTORY.bake(
			modelElement.from, modelElement.to, modelElementFace, sprite, direction, modelBakeSettings, modelElement.rotation, modelElement.shade, identifier
		);
	}

	public boolean textureExists(String string) {
		return !MissingSprite.getMissingSpriteId().equals(this.resolveSprite(string).getTextureId());
	}

	public SpriteIdentifier resolveSprite(String string) {
		if (isTextureReference(string)) {
			string = string.substring(1);
		}

		List<String> list = Lists.newArrayList();

		while (true) {
			Either<SpriteIdentifier, String> either = this.resolveTexture(string);
			Optional<SpriteIdentifier> optional = either.left();
			if (optional.isPresent()) {
				return (SpriteIdentifier)optional.get();
			}

			string = (String)either.right().get();
			if (list.contains(string)) {
				LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(list), string, this.id);
				return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId());
			}

			list.add(string);
		}
	}

	private Either<SpriteIdentifier, String> resolveTexture(String string) {
		for (JsonUnbakedModel jsonUnbakedModel = this; jsonUnbakedModel != null; jsonUnbakedModel = jsonUnbakedModel.parent) {
			Either<SpriteIdentifier, String> either = (Either<SpriteIdentifier, String>)jsonUnbakedModel.textureMap.get(string);
			if (either != null) {
				return either;
			}
		}

		return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId()));
	}

	private static boolean isTextureReference(String string) {
		return string.charAt(0) == '#';
	}

	public JsonUnbakedModel getRootModel() {
		return this.parent == null ? this : this.parent.getRootModel();
	}

	public ModelTransformation getTransformations() {
		Transformation transformation = this.getTransformation(ModelTransformation.Mode.field_4323);
		Transformation transformation2 = this.getTransformation(ModelTransformation.Mode.field_4320);
		Transformation transformation3 = this.getTransformation(ModelTransformation.Mode.field_4321);
		Transformation transformation4 = this.getTransformation(ModelTransformation.Mode.field_4322);
		Transformation transformation5 = this.getTransformation(ModelTransformation.Mode.field_4316);
		Transformation transformation6 = this.getTransformation(ModelTransformation.Mode.field_4317);
		Transformation transformation7 = this.getTransformation(ModelTransformation.Mode.field_4318);
		Transformation transformation8 = this.getTransformation(ModelTransformation.Mode.field_4319);
		return new ModelTransformation(
			transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8
		);
	}

	private Transformation getTransformation(ModelTransformation.Mode mode) {
		return this.parent != null && !this.transformations.isTransformationDefined(mode)
			? this.parent.getTransformation(mode)
			: this.transformations.getTransformation(mode);
	}

	public String toString() {
		return this.id;
	}

	public static class Deserializer implements JsonDeserializer<JsonUnbakedModel> {
		public JsonUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			List<ModelElement> list = this.deserializeElements(jsonDeserializationContext, jsonObject);
			String string = this.deserializeParent(jsonObject);
			Map<String, Either<SpriteIdentifier, String>> map = this.deserializeTextures(jsonObject);
			boolean bl = this.deserializeAmbientOcclusion(jsonObject);
			ModelTransformation modelTransformation = ModelTransformation.NONE;
			if (jsonObject.has("display")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
				modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize(jsonObject2, ModelTransformation.class);
			}

			List<ModelItemOverride> list2 = this.deserializeOverrides(jsonDeserializationContext, jsonObject);
			JsonUnbakedModel.GuiLight guiLight = null;
			if (jsonObject.has("gui_light")) {
				guiLight = JsonUnbakedModel.GuiLight.deserialize(JsonHelper.getString(jsonObject, "gui_light"));
			}

			Identifier identifier = string.isEmpty() ? null : new Identifier(string);
			return new JsonUnbakedModel(identifier, list, map, bl, guiLight, modelTransformation, list2);
		}

		protected List<ModelItemOverride> deserializeOverrides(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<ModelItemOverride> list = Lists.newArrayList();
			if (jsonObject.has("overrides")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "overrides")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, ModelItemOverride.class));
				}
			}

			return list;
		}

		private Map<String, Either<SpriteIdentifier, String>> deserializeTextures(JsonObject jsonObject) {
			Identifier identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEX;
			Map<String, Either<SpriteIdentifier, String>> map = Maps.newHashMap();
			if (jsonObject.has("textures")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "textures");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), resolveReference(identifier, ((JsonElement)entry.getValue()).getAsString()));
				}
			}

			return map;
		}

		private static Either<SpriteIdentifier, String> resolveReference(Identifier identifier, String string) {
			if (JsonUnbakedModel.isTextureReference(string)) {
				return Either.right(string.substring(1));
			} else {
				Identifier identifier2 = Identifier.tryParse(string);
				if (identifier2 == null) {
					throw new JsonParseException(string + " is not valid resource location");
				} else {
					return Either.left(new SpriteIdentifier(identifier, identifier2));
				}
			}
		}

		private String deserializeParent(JsonObject jsonObject) {
			return JsonHelper.getString(jsonObject, "parent", "");
		}

		protected boolean deserializeAmbientOcclusion(JsonObject jsonObject) {
			return JsonHelper.getBoolean(jsonObject, "ambientocclusion", true);
		}

		protected List<ModelElement> deserializeElements(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<ModelElement> list = Lists.newArrayList();
			if (jsonObject.has("elements")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "elements")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, ModelElement.class));
				}
			}

			return list;
		}
	}

	public static enum GuiLight {
		field_21858("front"),
		field_21859("side");

		private final String name;

		private GuiLight(String string2) {
			this.name = string2;
		}

		public static JsonUnbakedModel.GuiLight deserialize(String string) {
			for (JsonUnbakedModel.GuiLight guiLight : values()) {
				if (guiLight.name.equals(string)) {
					return guiLight;
				}
			}

			throw new IllegalArgumentException("Invalid gui light: " + string);
		}

		public boolean isSide() {
			return this == field_21859;
		}
	}
}
