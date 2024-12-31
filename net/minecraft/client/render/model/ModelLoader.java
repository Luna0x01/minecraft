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
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelLoader {
	public static final Identifier FIRE_0 = new Identifier("block/fire_0");
	public static final Identifier FIRE_1 = new Identifier("block/fire_1");
	public static final Identifier LAVA_FLOW = new Identifier("block/lava_flow");
	public static final Identifier WATER_FLOW = new Identifier("block/water_flow");
	public static final Identifier WATER_OVERLAY = new Identifier("block/water_overlay");
	public static final Identifier DESTROY_STAGE_0 = new Identifier("block/destroy_stage_0");
	public static final Identifier DESTROY_STAGE_1 = new Identifier("block/destroy_stage_1");
	public static final Identifier DESTROY_STAGE_2 = new Identifier("block/destroy_stage_2");
	public static final Identifier DESTROY_STAGE_3 = new Identifier("block/destroy_stage_3");
	public static final Identifier DESTROY_STAGE_4 = new Identifier("block/destroy_stage_4");
	public static final Identifier DESTROY_STAGE_5 = new Identifier("block/destroy_stage_5");
	public static final Identifier DESTROY_STAGE_6 = new Identifier("block/destroy_stage_6");
	public static final Identifier DESTROY_STAGE_7 = new Identifier("block/destroy_stage_7");
	public static final Identifier DESTROY_STAGE_8 = new Identifier("block/destroy_stage_8");
	public static final Identifier DESTROY_STAGE_9 = new Identifier("block/destroy_stage_9");
	private static final Set<Identifier> DEFAULT_TEXTURES = Sets.newHashSet(
		new Identifier[]{
			WATER_FLOW,
			LAVA_FLOW,
			WATER_OVERLAY,
			FIRE_0,
			FIRE_1,
			DESTROY_STAGE_0,
			DESTROY_STAGE_1,
			DESTROY_STAGE_2,
			DESTROY_STAGE_3,
			DESTROY_STAGE_4,
			DESTROY_STAGE_5,
			DESTROY_STAGE_6,
			DESTROY_STAGE_7,
			DESTROY_STAGE_8,
			DESTROY_STAGE_9,
			new Identifier("item/empty_armor_slot_helmet"),
			new Identifier("item/empty_armor_slot_chestplate"),
			new Identifier("item/empty_armor_slot_leggings"),
			new Identifier("item/empty_armor_slot_boots"),
			new Identifier("item/empty_armor_slot_shield")
		}
	);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ModelIdentifier MISSING = new ModelIdentifier("builtin/missing", "missing");
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
	public static final JsonUnbakedModel GENERATION_MARKER = SystemUtil.consume(
		JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> jsonUnbakedModel.id = "generation marker"
	);
	public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = SystemUtil.consume(
		JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> jsonUnbakedModel.id = "block entity marker"
	);
	private static final StateFactory<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateFactory.Builder<Block, BlockState>(Blocks.field_10124)
		.add(BooleanProperty.of("map"))
		.build(BlockState::new);
	private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final Map<Identifier, StateFactory<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(
		new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY
	);
	private final ResourceManager resourceManager;
	private final SpriteAtlasTexture spriteAtlas;
	private final BlockColors field_20272;
	private final Set<Identifier> modelsToLoad = Sets.newHashSet();
	private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
	private final Map<Identifier, UnbakedModel> unbakedModels = Maps.newHashMap();
	private final Map<Triple<Identifier, ModelRotation, Boolean>, BakedModel> bakedModelCache = Maps.newHashMap();
	private final Map<Identifier, UnbakedModel> modelsToBake = Maps.newHashMap();
	private final Map<Identifier, BakedModel> bakedModels = Maps.newHashMap();
	private final SpriteAtlasTexture.Data spriteAtlasData;
	private int field_20273 = 1;
	private final Object2IntMap<BlockState> field_20274 = SystemUtil.consume(
		new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
	);

	public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture, BlockColors blockColors, Profiler profiler) {
		this.resourceManager = resourceManager;
		this.spriteAtlas = spriteAtlasTexture;
		this.field_20272 = blockColors;
		profiler.push("missing_model");

		try {
			this.unbakedModels.put(MISSING, this.loadModelFromJson(MISSING));
			this.addModel(MISSING);
		} catch (IOException var7) {
			LOGGER.error("Error loading missing model, should never happen :(", var7);
			throw new RuntimeException(var7);
		}

		profiler.swap("static_definitions");
		STATIC_DEFINITIONS.forEach(
			(identifier, stateFactory) -> stateFactory.getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(identifier, blockState)))
		);
		profiler.swap("blocks");

		for (Block block : Registry.BLOCK) {
			block.getStateFactory().getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(blockState)));
		}

		profiler.swap("items");

		for (Identifier identifier : Registry.ITEM.getIds()) {
			this.addModel(new ModelIdentifier(identifier, "inventory"));
		}

		profiler.swap("special");
		this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		profiler.swap("textures");
		Set<String> set = Sets.newLinkedHashSet();
		Set<Identifier> set2 = (Set<Identifier>)this.modelsToBake
			.values()
			.stream()
			.flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream())
			.collect(Collectors.toSet());
		set2.addAll(DEFAULT_TEXTURES);
		set.forEach(string -> LOGGER.warn("Unable to resolve texture reference: {}", string));
		profiler.swap("stitching");
		this.spriteAtlasData = this.spriteAtlas.stitch(this.resourceManager, set2, profiler);
		profiler.pop();
	}

	public void upload(Profiler profiler) {
		profiler.push("atlas");
		this.spriteAtlas.upload(this.spriteAtlasData);
		profiler.swap("baking");
		this.modelsToBake.keySet().forEach(identifier -> {
			BakedModel bakedModel = null;

			try {
				bakedModel = this.bake(identifier, ModelRotation.field_5350);
			} catch (Exception var4) {
				LOGGER.warn("Unable to bake model: '{}': {}", identifier, var4);
			}

			if (bakedModel != null) {
				this.bakedModels.put(identifier, bakedModel);
			}
		});
		profiler.pop();
	}

	private static Predicate<BlockState> stateKeyToPredicate(StateFactory<Block, BlockState> stateFactory, String string) {
		Map<Property<?>, Comparable<?>> map = Maps.newHashMap();

		for (String string2 : COMMA_SPLITTER.split(string)) {
			Iterator<String> iterator = KEY_VALUE_SPLITTER.split(string2).iterator();
			if (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				Property<?> property = stateFactory.getProperty(string3);
				if (property != null && iterator.hasNext()) {
					String string4 = (String)iterator.next();
					Comparable<?> comparable = getPropertyValue((Property<Comparable<?>>)property, string4);
					if (comparable == null) {
						throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + property.getValues());
					}

					map.put(property, comparable);
				} else if (!string3.isEmpty()) {
					throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
				}
			}
		}

		Block block = stateFactory.getBaseObject();
		return blockState -> {
			if (blockState != null && block == blockState.getBlock()) {
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
		return (T)property.getValue(string).orElse(null);
	}

	public UnbakedModel getOrLoadModel(Identifier identifier) {
		if (this.unbakedModels.containsKey(identifier)) {
			return (UnbakedModel)this.unbakedModels.get(identifier);
		} else if (this.modelsToLoad.contains(identifier)) {
			throw new IllegalStateException("Circular reference while loading " + identifier);
		} else {
			this.modelsToLoad.add(identifier);
			UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING);

			while (!this.modelsToLoad.isEmpty()) {
				Identifier identifier2 = (Identifier)this.modelsToLoad.iterator().next();

				try {
					if (!this.unbakedModels.containsKey(identifier2)) {
						this.loadModel(identifier2);
					}
				} catch (ModelLoader.ModelLoaderException var9) {
					LOGGER.warn(var9.getMessage());
					this.unbakedModels.put(identifier2, unbakedModel);
				} catch (Exception var10) {
					LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", identifier2, identifier, var10);
					this.unbakedModels.put(identifier2, unbakedModel);
				} finally {
					this.modelsToLoad.remove(identifier2);
				}
			}

			return (UnbakedModel)this.unbakedModels.getOrDefault(identifier, unbakedModel);
		}
	}

	private void loadModel(Identifier identifier) throws Exception {
		if (!(identifier instanceof ModelIdentifier)) {
			this.putModel(identifier, this.loadModelFromJson(identifier));
		} else {
			ModelIdentifier modelIdentifier = (ModelIdentifier)identifier;
			if (Objects.equals(modelIdentifier.getVariant(), "inventory")) {
				Identifier identifier2 = new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
				JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier2);
				this.putModel(modelIdentifier, jsonUnbakedModel);
				this.unbakedModels.put(identifier2, jsonUnbakedModel);
			} else {
				Identifier identifier3 = new Identifier(identifier.getNamespace(), identifier.getPath());
				StateFactory<Block, BlockState> stateFactory = (StateFactory<Block, BlockState>)Optional.ofNullable(STATIC_DEFINITIONS.get(identifier3))
					.orElseGet(() -> Registry.BLOCK.get(identifier3).getStateFactory());
				this.variantMapDeserializationContext.setStateFactory(stateFactory);
				List<Property<?>> list = ImmutableList.copyOf(this.field_20272.method_21592(stateFactory.getBaseObject()));
				ImmutableList<BlockState> immutableList = stateFactory.getStates();
				Map<ModelIdentifier, BlockState> map = Maps.newHashMap();
				immutableList.forEach(blockState -> {
					BlockState var10000 = (BlockState)map.put(BlockModels.getModelId(identifier3, blockState), blockState);
				});
				Map<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.class_4455>>> map2 = Maps.newHashMap();
				Identifier identifier4 = new Identifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json");
				UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING);
				ModelLoader.class_4455 lv = new ModelLoader.class_4455(ImmutableList.of(unbakedModel), ImmutableList.of());
				Pair<UnbakedModel, Supplier<ModelLoader.class_4455>> pair = Pair.of(unbakedModel, (Supplier)() -> lv);

				try {
					List<Pair<String, ModelVariantMap>> list2;
					try {
						list2 = (List<Pair<String, ModelVariantMap>>)this.resourceManager
							.getAllResources(identifier4)
							.stream()
							.map(
								resource -> {
									try {
										InputStream inputStream = resource.getInputStream();
										Throwable var3x = null;

										Pair var4x;
										try {
											var4x = Pair.of(
												resource.getResourcePackName(),
												ModelVariantMap.deserialize(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8))
											);
										} catch (Throwable var14) {
											var3x = var14;
											throw var14;
										} finally {
											if (inputStream != null) {
												if (var3x != null) {
													try {
														inputStream.close();
													} catch (Throwable var13x) {
														var3x.addSuppressed(var13x);
													}
												} else {
													inputStream.close();
												}
											}
										}

										return var4x;
									} catch (Exception var16x) {
										throw new ModelLoader.ModelLoaderException(
											String.format(
												"Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), var16x.getMessage()
											)
										);
									}
								}
							)
							.collect(Collectors.toList());
					} catch (IOException var25) {
						LOGGER.warn("Exception loading blockstate definition: {}: {}", identifier4, var25);
						return;
					}

					for (Pair<String, ModelVariantMap> pair2 : list2) {
						ModelVariantMap modelVariantMap = (ModelVariantMap)pair2.getSecond();
						Map<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.class_4455>>> map4 = Maps.newIdentityHashMap();
						MultipartUnbakedModel multipartUnbakedModel;
						if (modelVariantMap.hasMultipartModel()) {
							multipartUnbakedModel = modelVariantMap.getMultipartModel();
							immutableList.forEach(
								blockState -> {
									Pair var10000 = (Pair)map4.put(
										blockState, Pair.of(multipartUnbakedModel, (Supplier)() -> ModelLoader.class_4455.method_21607(blockState, multipartUnbakedModel, list))
									);
								}
							);
						} else {
							multipartUnbakedModel = null;
						}

						modelVariantMap.getVariantMap()
							.forEach(
								(string, weightedUnbakedModel) -> {
									try {
										immutableList.stream()
											.filter(stateKeyToPredicate(stateFactory, string))
											.forEach(
												blockState -> {
													Pair<UnbakedModel, Supplier<ModelLoader.class_4455>> pair2xx = (Pair<UnbakedModel, Supplier<ModelLoader.class_4455>>)map4.put(
														blockState, Pair.of(weightedUnbakedModel, (Supplier)() -> ModelLoader.class_4455.method_21608(blockState, weightedUnbakedModel, list))
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
											identifier4,
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
					throw new ModelLoader.ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", identifier4, var27));
				} finally {
					Map<ModelLoader.class_4455, Set<BlockState>> map6 = Maps.newHashMap();
					map.forEach((modelIdentifierx, blockState) -> {
						Pair<UnbakedModel, Supplier<ModelLoader.class_4455>> pair2x = (Pair<UnbakedModel, Supplier<ModelLoader.class_4455>>)map2.get(blockState);
						if (pair2x == null) {
							LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", identifier4, modelIdentifierx);
							pair2x = pair;
						}

						this.putModel(modelIdentifierx, (UnbakedModel)pair2x.getFirst());

						try {
							ModelLoader.class_4455 lvx = (ModelLoader.class_4455)((Supplier)pair2x.getSecond()).get();
							((Set)map6.computeIfAbsent(lvx, arg -> Sets.newIdentityHashSet())).add(blockState);
						} catch (Exception var9x) {
							LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifierx, var9x);
						}
					});
					map6.forEach((arg, set) -> {
						Iterator<BlockState> iterator = set.iterator();

						while (iterator.hasNext()) {
							BlockState blockState = (BlockState)iterator.next();
							if (blockState.getRenderType() != BlockRenderType.field_11458) {
								iterator.remove();
								this.field_20274.put(blockState, 0);
							}
						}

						if (set.size() > 1) {
							this.method_21603(set);
						}
					});
				}
			}
		}
	}

	private void putModel(Identifier identifier, UnbakedModel unbakedModel) {
		this.unbakedModels.put(identifier, unbakedModel);
		this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
	}

	private void addModel(ModelIdentifier modelIdentifier) {
		UnbakedModel unbakedModel = this.getOrLoadModel(modelIdentifier);
		this.unbakedModels.put(modelIdentifier, unbakedModel);
		this.modelsToBake.put(modelIdentifier, unbakedModel);
	}

	private void method_21603(Iterable<BlockState> iterable) {
		int i = this.field_20273++;
		iterable.forEach(blockState -> this.field_20274.put(blockState, i));
	}

	@Nullable
	public BakedModel bake(Identifier identifier, ModelBakeSettings modelBakeSettings) {
		Triple<Identifier, ModelRotation, Boolean> triple = Triple.of(identifier, modelBakeSettings.getRotation(), modelBakeSettings.isUvLocked());
		if (this.bakedModelCache.containsKey(triple)) {
			return (BakedModel)this.bakedModelCache.get(triple);
		} else {
			UnbakedModel unbakedModel = this.getOrLoadModel(identifier);
			if (unbakedModel instanceof JsonUnbakedModel) {
				JsonUnbakedModel jsonUnbakedModel = (JsonUnbakedModel)unbakedModel;
				if (jsonUnbakedModel.getRootModel() == GENERATION_MARKER) {
					return ITEM_MODEL_GENERATOR.create(this.spriteAtlas::getSprite, jsonUnbakedModel)
						.bake(this, jsonUnbakedModel, this.spriteAtlas::getSprite, modelBakeSettings);
				}
			}

			BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlas::getSprite, modelBakeSettings);
			this.bakedModelCache.put(triple, bakedModel);
			return bakedModel;
		}
	}

	private JsonUnbakedModel loadModelFromJson(Identifier identifier) throws IOException {
		Reader reader = null;
		Resource resource = null;

		JsonUnbakedModel jsonUnbakedModel;
		try {
			String string = identifier.getPath();
			if ("builtin/generated".equals(string)) {
				return GENERATION_MARKER;
			}

			if (!"builtin/entity".equals(string)) {
				if (string.startsWith("builtin/")) {
					String string2 = string.substring("builtin/".length());
					String string3 = (String)BUILTIN_MODEL_DEFINITIONS.get(string2);
					if (string3 == null) {
						throw new FileNotFoundException(identifier.toString());
					}

					reader = new StringReader(string3);
				} else {
					resource = this.resourceManager.getResource(new Identifier(identifier.getNamespace(), "models/" + identifier.getPath() + ".json"));
					reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
				}

				jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
				jsonUnbakedModel.id = identifier.toString();
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

	public Object2IntMap<BlockState> method_21605() {
		return this.field_20274;
	}

	static class ModelLoaderException extends RuntimeException {
		public ModelLoaderException(String string) {
			super(string);
		}
	}

	static class class_4455 {
		private final List<UnbakedModel> field_20275;
		private final List<Object> field_20276;

		public class_4455(List<UnbakedModel> list, List<Object> list2) {
			this.field_20275 = list;
			this.field_20276 = list2;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof ModelLoader.class_4455)) {
				return false;
			} else {
				ModelLoader.class_4455 lv = (ModelLoader.class_4455)object;
				return Objects.equals(this.field_20275, lv.field_20275) && Objects.equals(this.field_20276, lv.field_20276);
			}
		}

		public int hashCode() {
			return 31 * this.field_20275.hashCode() + this.field_20276.hashCode();
		}

		public static ModelLoader.class_4455 method_21607(BlockState blockState, MultipartUnbakedModel multipartUnbakedModel, Collection<Property<?>> collection) {
			StateFactory<Block, BlockState> stateFactory = blockState.getBlock().getStateFactory();
			List<UnbakedModel> list = (List<UnbakedModel>)multipartUnbakedModel.getComponents()
				.stream()
				.filter(multipartModelComponent -> multipartModelComponent.getPredicate(stateFactory).test(blockState))
				.map(MultipartModelComponent::getModel)
				.collect(ImmutableList.toImmutableList());
			List<Object> list2 = method_21609(blockState, collection);
			return new ModelLoader.class_4455(list, list2);
		}

		public static ModelLoader.class_4455 method_21608(BlockState blockState, UnbakedModel unbakedModel, Collection<Property<?>> collection) {
			List<Object> list = method_21609(blockState, collection);
			return new ModelLoader.class_4455(ImmutableList.of(unbakedModel), list);
		}

		private static List<Object> method_21609(BlockState blockState, Collection<Property<?>> collection) {
			return (List<Object>)collection.stream().map(blockState::get).collect(ImmutableList.toImmutableList());
		}
	}
}
