package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelLoader {
	public static final SpriteIdentifier FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/fire_0"));
	public static final SpriteIdentifier FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/fire_1"));
	public static final SpriteIdentifier LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/lava_flow"));
	public static final SpriteIdentifier WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/water_flow"));
	public static final SpriteIdentifier WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/water_overlay"));
	public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/banner_base"));
	public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/shield_base"));
	public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(
		SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/shield_base_nopattern")
	);
	public static final int field_32983 = 10;
	public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = (List<Identifier>)IntStream.range(0, 10)
		.mapToObj(i -> new Identifier("block/destroy_stage_" + i))
		.collect(Collectors.toList());
	public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = (List<Identifier>)BLOCK_DESTRUCTION_STAGES.stream()
		.map(identifier -> new Identifier("textures/" + identifier.getPath() + ".png"))
		.collect(Collectors.toList());
	public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = (List<RenderLayer>)BLOCK_DESTRUCTION_STAGE_TEXTURES.stream()
		.map(RenderLayer::getBlockBreaking)
		.collect(Collectors.toList());
	private static final Set<SpriteIdentifier> DEFAULT_TEXTURES = Util.make(Sets.newHashSet(), hashSet -> {
		hashSet.add(WATER_FLOW);
		hashSet.add(LAVA_FLOW);
		hashSet.add(WATER_OVERLAY);
		hashSet.add(FIRE_0);
		hashSet.add(FIRE_1);
		hashSet.add(BellBlockEntityRenderer.BELL_BODY_TEXTURE);
		hashSet.add(ConduitBlockEntityRenderer.BASE_TEXTURE);
		hashSet.add(ConduitBlockEntityRenderer.CAGE_TEXTURE);
		hashSet.add(ConduitBlockEntityRenderer.WIND_TEXTURE);
		hashSet.add(ConduitBlockEntityRenderer.WIND_VERTICAL_TEXTURE);
		hashSet.add(ConduitBlockEntityRenderer.OPEN_EYE_TEXTURE);
		hashSet.add(ConduitBlockEntityRenderer.CLOSED_EYE_TEXTURE);
		hashSet.add(EnchantingTableBlockEntityRenderer.BOOK_TEXTURE);
		hashSet.add(BANNER_BASE);
		hashSet.add(SHIELD_BASE);
		hashSet.add(SHIELD_BASE_NO_PATTERN);

		for (Identifier identifier : BLOCK_DESTRUCTION_STAGES) {
			hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier));
		}

		hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE));
		hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE));
		hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE));
		hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE));
		hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT));
		TexturedRenderLayers.addDefaultTextures(hashSet::add);
	});
	static final int field_32984 = -1;
	private static final int field_32985 = 0;
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String BUILTIN = "builtin/";
	private static final String BUILTIN_GENERATED = "builtin/generated";
	private static final String BUILTIN_ENTITY = "builtin/entity";
	private static final String MISSING = "missing";
	public static final ModelIdentifier MISSING_ID = new ModelIdentifier("builtin/missing", "missing");
	private static final String field_21773 = MISSING_ID.toString();
	@VisibleForTesting
	public static final String MISSING_DEFINITION = ("{    'textures': {       'particle': '"
			+ MissingSprite.getMissingSpriteId().getPath()
			+ "',       'missingno': '"
			+ MissingSprite.getMissingSpriteId().getPath()
			+ "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}")
		.replace('\'', '"');
	private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_DEFINITION));
	private static final Splitter COMMA_SPLITTER = Splitter.on(',');
	private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').limit(2);
	public static final JsonUnbakedModel GENERATION_MARKER = Util.make(
		JsonUnbakedModel.deserialize("{\"gui_light\": \"front\"}"), jsonUnbakedModel -> jsonUnbakedModel.id = "generation marker"
	);
	public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = Util.make(
		JsonUnbakedModel.deserialize("{\"gui_light\": \"side\"}"), jsonUnbakedModel -> jsonUnbakedModel.id = "block entity marker"
	);
	private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateManager.Builder<Block, BlockState>(Blocks.AIR)
		.add(BooleanProperty.of("map"))
		.build(Block::getDefaultState, BlockState::new);
	private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(
		new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY, new Identifier("glow_item_frame"), ITEM_FRAME_STATE_FACTORY
	);
	private final ResourceManager resourceManager;
	@Nullable
	private SpriteAtlasManager spriteAtlasManager;
	private final BlockColors blockColors;
	private final Set<Identifier> modelsToLoad = Sets.newHashSet();
	private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
	private final Map<Identifier, UnbakedModel> unbakedModels = Maps.newHashMap();
	private final Map<Triple<Identifier, AffineTransformation, Boolean>, BakedModel> bakedModelCache = Maps.newHashMap();
	private final Map<Identifier, UnbakedModel> modelsToBake = Maps.newHashMap();
	private final Map<Identifier, BakedModel> bakedModels = Maps.newHashMap();
	private final Map<Identifier, Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>> spriteAtlasData;
	private int nextStateId = 1;
	private final Object2IntMap<BlockState> stateLookup = Util.make(
		new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
	);

	public ModelLoader(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i) {
		this.resourceManager = resourceManager;
		this.blockColors = blockColors;
		profiler.push("missing_model");

		try {
			this.unbakedModels.put(MISSING_ID, this.loadModelFromJson(MISSING_ID));
			this.addModel(MISSING_ID);
		} catch (IOException var12) {
			LOGGER.error("Error loading missing model, should never happen :(", var12);
			throw new RuntimeException(var12);
		}

		profiler.swap("static_definitions");
		STATIC_DEFINITIONS.forEach(
			(identifier, stateManager) -> stateManager.getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(identifier, blockState)))
		);
		profiler.swap("blocks");

		for (Block block : Registry.BLOCK) {
			block.getStateManager().getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(blockState)));
		}

		profiler.swap("items");

		for (Identifier identifier : Registry.ITEM.getIds()) {
			this.addModel(new ModelIdentifier(identifier, "inventory"));
		}

		profiler.swap("special");
		this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		this.addModel(new ModelIdentifier("minecraft:spyglass_in_hand#inventory"));
		profiler.swap("textures");
		Set<Pair<String, String>> set = Sets.newLinkedHashSet();
		Set<SpriteIdentifier> set2 = (Set<SpriteIdentifier>)this.modelsToBake
			.values()
			.stream()
			.flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream())
			.collect(Collectors.toSet());
		set2.addAll(DEFAULT_TEXTURES);
		set.stream()
			.filter(pair -> !((String)pair.getSecond()).equals(field_21773))
			.forEach(pair -> LOGGER.warn("Unable to resolve texture reference: {} in {}", pair.getFirst(), pair.getSecond()));
		Map<Identifier, List<SpriteIdentifier>> map = (Map<Identifier, List<SpriteIdentifier>>)set2.stream()
			.collect(Collectors.groupingBy(SpriteIdentifier::getAtlasId));
		profiler.swap("stitching");
		this.spriteAtlasData = Maps.newHashMap();

		for (Entry<Identifier, List<SpriteIdentifier>> entry : map.entrySet()) {
			SpriteAtlasTexture spriteAtlasTexture = new SpriteAtlasTexture((Identifier)entry.getKey());
			SpriteAtlasTexture.Data data = spriteAtlasTexture.stitch(
				this.resourceManager, ((List)entry.getValue()).stream().map(SpriteIdentifier::getTextureId), profiler, i
			);
			this.spriteAtlasData.put((Identifier)entry.getKey(), Pair.of(spriteAtlasTexture, data));
		}

		profiler.pop();
	}

	public SpriteAtlasManager upload(TextureManager textureManager, Profiler profiler) {
		profiler.push("atlas");

		for (Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data> pair : this.spriteAtlasData.values()) {
			SpriteAtlasTexture spriteAtlasTexture = (SpriteAtlasTexture)pair.getFirst();
			SpriteAtlasTexture.Data data = (SpriteAtlasTexture.Data)pair.getSecond();
			spriteAtlasTexture.upload(data);
			textureManager.registerTexture(spriteAtlasTexture.getId(), spriteAtlasTexture);
			textureManager.bindTexture(spriteAtlasTexture.getId());
			spriteAtlasTexture.applyTextureFilter(data);
		}

		this.spriteAtlasManager = new SpriteAtlasManager(
			(Collection<SpriteAtlasTexture>)this.spriteAtlasData.values().stream().map(Pair::getFirst).collect(Collectors.toList())
		);
		profiler.swap("baking");
		this.modelsToBake.keySet().forEach(identifier -> {
			BakedModel bakedModel = null;

			try {
				bakedModel = this.bake(identifier, ModelRotation.X0_Y0);
			} catch (Exception var4x) {
				LOGGER.warn("Unable to bake model: '{}': {}", identifier, var4x);
			}

			if (bakedModel != null) {
				this.bakedModels.put(identifier, bakedModel);
			}
		});
		profiler.pop();
		return this.spriteAtlasManager;
	}

	private static Predicate<BlockState> stateKeyToPredicate(StateManager<Block, BlockState> stateFactory, String key) {
		Map<Property<?>, Comparable<?>> map = Maps.newHashMap();

		for (String string : COMMA_SPLITTER.split(key)) {
			Iterator<String> iterator = KEY_VALUE_SPLITTER.split(string).iterator();
			if (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				Property<?> property = stateFactory.getProperty(string2);
				if (property != null && iterator.hasNext()) {
					String string3 = (String)iterator.next();
					Comparable<?> comparable = getPropertyValue((Property<Comparable<?>>)property, string3);
					if (comparable == null) {
						throw new RuntimeException("Unknown value: '" + string3 + "' for blockstate property: '" + string2 + "' " + property.getValues());
					}

					map.put(property, comparable);
				} else if (!string2.isEmpty()) {
					throw new RuntimeException("Unknown blockstate property: '" + string2 + "'");
				}
			}
		}

		Block block = stateFactory.getOwner();
		return blockState -> {
			if (blockState != null && blockState.isOf(block)) {
				for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
					if (!Objects.equals(blockState.get((Property)entry.getKey()), entry.getValue())) {
						return false;
					}
				}

				return true;
			} else {
				return false;
			}
		};
	}

	@Nullable
	static <T extends Comparable<T>> T getPropertyValue(Property<T> property, String string) {
		return (T)property.parse(string).orElse(null);
	}

	public UnbakedModel getOrLoadModel(Identifier id) {
		if (this.unbakedModels.containsKey(id)) {
			return (UnbakedModel)this.unbakedModels.get(id);
		} else if (this.modelsToLoad.contains(id)) {
			throw new IllegalStateException("Circular reference while loading " + id);
		} else {
			this.modelsToLoad.add(id);
			UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING_ID);

			while (!this.modelsToLoad.isEmpty()) {
				Identifier identifier = (Identifier)this.modelsToLoad.iterator().next();

				try {
					if (!this.unbakedModels.containsKey(identifier)) {
						this.loadModel(identifier);
					}
				} catch (ModelLoader.ModelLoaderException var9) {
					LOGGER.warn(var9.getMessage());
					this.unbakedModels.put(identifier, unbakedModel);
				} catch (Exception var10) {
					LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", identifier, id, var10);
					this.unbakedModels.put(identifier, unbakedModel);
				} finally {
					this.modelsToLoad.remove(identifier);
				}
			}

			return (UnbakedModel)this.unbakedModels.getOrDefault(id, unbakedModel);
		}
	}

	private void loadModel(Identifier id) throws Exception {
		if (!(id instanceof ModelIdentifier modelIdentifier)) {
			this.putModel(id, this.loadModelFromJson(id));
		} else if (Objects.equals(modelIdentifier.getVariant(), "inventory")) {
			Identifier identifier = new Identifier(id.getNamespace(), "item/" + id.getPath());
			JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier);
			this.putModel(modelIdentifier, jsonUnbakedModel);
			this.unbakedModels.put(identifier, jsonUnbakedModel);
		} else {
			Identifier identifier2 = new Identifier(id.getNamespace(), id.getPath());
			StateManager<Block, BlockState> stateManager = (StateManager<Block, BlockState>)Optional.ofNullable((StateManager)STATIC_DEFINITIONS.get(identifier2))
				.orElseGet(() -> Registry.BLOCK.get(identifier2).getStateManager());
			this.variantMapDeserializationContext.setStateFactory(stateManager);
			List<Property<?>> list = ImmutableList.copyOf(this.blockColors.getProperties(stateManager.getOwner()));
			ImmutableList<BlockState> immutableList = stateManager.getStates();
			Map<ModelIdentifier, BlockState> map = Maps.newHashMap();
			immutableList.forEach(blockState -> map.put(BlockModels.getModelId(identifier2, blockState), blockState));
			Map<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>> map2 = Maps.newHashMap();
			Identifier identifier3 = new Identifier(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
			UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING_ID);
			ModelLoader.ModelDefinition modelDefinition = new ModelLoader.ModelDefinition(ImmutableList.of(unbakedModel), ImmutableList.of());
			Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>> pair = Pair.of(unbakedModel, (Supplier)() -> modelDefinition);

			try {
				List<Pair<String, ModelVariantMap>> list2;
				try {
					list2 = (List<Pair<String, ModelVariantMap>>)this.resourceManager
						.getAllResources(identifier3)
						.stream()
						.map(
							resource -> {
								try {
									InputStream inputStream = resource.getInputStream();

									Pair var3x;
									try {
										var3x = Pair.of(
											resource.getResourcePackName(),
											ModelVariantMap.fromJson(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8))
										);
									} catch (Throwable var6x) {
										if (inputStream != null) {
											try {
												inputStream.close();
											} catch (Throwable var5x) {
												var6x.addSuppressed(var5x);
											}
										}

										throw var6x;
									}

									if (inputStream != null) {
										inputStream.close();
									}

									return var3x;
								} catch (Exception var7x) {
									throw new ModelLoader.ModelLoaderException(
										String.format(
											"Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), var7x.getMessage()
										)
									);
								}
							}
						)
						.collect(Collectors.toList());
				} catch (IOException var25) {
					LOGGER.warn("Exception loading blockstate definition: {}: {}", identifier3, var25);
					return;
				}

				for (Pair<String, ModelVariantMap> pair2 : list2) {
					ModelVariantMap modelVariantMap = (ModelVariantMap)pair2.getSecond();
					Map<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>> map4 = Maps.newIdentityHashMap();
					MultipartUnbakedModel multipartUnbakedModel;
					if (modelVariantMap.hasMultipartModel()) {
						multipartUnbakedModel = modelVariantMap.getMultipartModel();
						immutableList.forEach(
							blockState -> map4.put(
									blockState, Pair.of(multipartUnbakedModel, (Supplier)() -> ModelLoader.ModelDefinition.create(blockState, multipartUnbakedModel, list))
								)
						);
					} else {
						multipartUnbakedModel = null;
					}

					modelVariantMap.getVariantMap()
						.forEach(
							(string, weightedUnbakedModel) -> {
								try {
									immutableList.stream()
										.filter(stateKeyToPredicate(stateManager, string))
										.forEach(
											blockState -> {
												Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>> pair2xx = (Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>)map4.put(
													blockState, Pair.of(weightedUnbakedModel, (Supplier)() -> ModelLoader.ModelDefinition.create(blockState, weightedUnbakedModel, list))
												);
												if (pair2xx != null && pair2xx.getFirst() != multipartUnbakedModel) {
													map4.put(blockState, pair);
													throw new RuntimeException(
														"Overlapping definition with: "
															+ (String)((Entry)modelVariantMap.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == pair2xx.getFirst()).findFirst().get())
																.getKey()
													);
												}
											}
										);
								} catch (Exception var12x) {
									LOGGER.warn(
										"Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}",
										identifier3,
										pair2.getFirst(),
										string,
										var12x.getMessage()
									);
								}
							}
						);
					map2.putAll(map4);
				}
			} catch (ModelLoader.ModelLoaderException var26) {
				throw var26;
			} catch (Exception var27) {
				throw new ModelLoader.ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", identifier3, var27));
			} finally {
				Map<ModelLoader.ModelDefinition, Set<BlockState>> map6 = Maps.newHashMap();
				map.forEach((modelIdentifierx, blockState) -> {
					Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>> pair2x = (Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>)map2.get(blockState);
					if (pair2x == null) {
						LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", identifier3, modelIdentifierx);
						pair2x = pair;
					}

					this.putModel(modelIdentifierx, (UnbakedModel)pair2x.getFirst());

					try {
						ModelLoader.ModelDefinition modelDefinitionx = (ModelLoader.ModelDefinition)((Supplier)pair2x.getSecond()).get();
						((Set)map6.computeIfAbsent(modelDefinitionx, modelDefinitionxx -> Sets.newIdentityHashSet())).add(blockState);
					} catch (Exception var9x) {
						LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifierx, var9x);
					}
				});
				map6.forEach((modelDefinitionx, set) -> {
					Iterator<BlockState> iterator = set.iterator();

					while (iterator.hasNext()) {
						BlockState blockState = (BlockState)iterator.next();
						if (blockState.getRenderType() != BlockRenderType.MODEL) {
							iterator.remove();
							this.stateLookup.put(blockState, 0);
						}
					}

					if (set.size() > 1) {
						this.addStates(set);
					}
				});
			}
		}
	}

	private void putModel(Identifier id, UnbakedModel unbakedModel) {
		this.unbakedModels.put(id, unbakedModel);
		this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
	}

	private void addModel(ModelIdentifier modelId) {
		UnbakedModel unbakedModel = this.getOrLoadModel(modelId);
		this.unbakedModels.put(modelId, unbakedModel);
		this.modelsToBake.put(modelId, unbakedModel);
	}

	private void addStates(Iterable<BlockState> states) {
		int i = this.nextStateId++;
		states.forEach(blockState -> this.stateLookup.put(blockState, i));
	}

	@Nullable
	public BakedModel bake(Identifier id, ModelBakeSettings settings) {
		Triple<Identifier, AffineTransformation, Boolean> triple = Triple.of(id, settings.getRotation(), settings.isUvLocked());
		if (this.bakedModelCache.containsKey(triple)) {
			return (BakedModel)this.bakedModelCache.get(triple);
		} else if (this.spriteAtlasManager == null) {
			throw new IllegalStateException("bake called too early");
		} else {
			UnbakedModel unbakedModel = this.getOrLoadModel(id);
			if (unbakedModel instanceof JsonUnbakedModel jsonUnbakedModel && jsonUnbakedModel.getRootModel() == GENERATION_MARKER) {
				return ITEM_MODEL_GENERATOR.create(this.spriteAtlasManager::getSprite, jsonUnbakedModel)
					.bake(this, jsonUnbakedModel, this.spriteAtlasManager::getSprite, settings, id, false);
			}

			BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlasManager::getSprite, settings, id);
			this.bakedModelCache.put(triple, bakedModel);
			return bakedModel;
		}
	}

	private JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException {
		Reader reader = null;
		Resource resource = null;

		JsonUnbakedModel jsonUnbakedModel;
		try {
			String string = id.getPath();
			if ("builtin/generated".equals(string)) {
				return GENERATION_MARKER;
			}

			if (!"builtin/entity".equals(string)) {
				if (string.startsWith("builtin/")) {
					String string2 = string.substring("builtin/".length());
					String string3 = (String)BUILTIN_MODEL_DEFINITIONS.get(string2);
					if (string3 == null) {
						throw new FileNotFoundException(id.toString());
					}

					reader = new StringReader(string3);
				} else {
					resource = this.resourceManager.getResource(new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json"));
					reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
				}

				jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
				jsonUnbakedModel.id = id.toString();
				return jsonUnbakedModel;
			}

			jsonUnbakedModel = BLOCK_ENTITY_MARKER;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(resource);
		}

		return jsonUnbakedModel;
	}

	public Map<Identifier, BakedModel> getBakedModelMap() {
		return this.bakedModels;
	}

	public Object2IntMap<BlockState> getStateLookup() {
		return this.stateLookup;
	}

	static class ModelDefinition {
		private final List<UnbakedModel> components;
		private final List<Object> values;

		public ModelDefinition(List<UnbakedModel> components, List<Object> values) {
			this.components = components;
			this.values = values;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				return !(o instanceof ModelLoader.ModelDefinition modelDefinition)
					? false
					: Objects.equals(this.components, modelDefinition.components) && Objects.equals(this.values, modelDefinition.values);
			}
		}

		public int hashCode() {
			return 31 * this.components.hashCode() + this.values.hashCode();
		}

		public static ModelLoader.ModelDefinition create(BlockState state, MultipartUnbakedModel rawModel, Collection<Property<?>> properties) {
			StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();
			List<UnbakedModel> list = (List<UnbakedModel>)rawModel.getComponents()
				.stream()
				.filter(multipartModelComponent -> multipartModelComponent.getPredicate(stateManager).test(state))
				.map(MultipartModelComponent::getModel)
				.collect(ImmutableList.toImmutableList());
			List<Object> list2 = getStateValues(state, properties);
			return new ModelLoader.ModelDefinition(list, list2);
		}

		public static ModelLoader.ModelDefinition create(BlockState state, UnbakedModel rawModel, Collection<Property<?>> properties) {
			List<Object> list = getStateValues(state, properties);
			return new ModelLoader.ModelDefinition(ImmutableList.of(rawModel), list);
		}

		private static List<Object> getStateValues(BlockState state, Collection<Property<?>> properties) {
			return (List<Object>)properties.stream().map(state::get).collect(ImmutableList.toImmutableList());
		}
	}

	static class ModelLoaderException extends RuntimeException {
		public ModelLoaderException(String message) {
			super(message);
		}
	}
}
