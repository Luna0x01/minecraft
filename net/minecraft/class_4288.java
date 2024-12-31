package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModelShapes;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4288 {
	public static final Identifier field_21064 = new Identifier("block/fire_0");
	public static final Identifier field_21065 = new Identifier("block/fire_1");
	public static final Identifier field_21066 = new Identifier("block/lava_flow");
	public static final Identifier field_21067 = new Identifier("block/water_flow");
	public static final Identifier field_21068 = new Identifier("block/water_overlay");
	public static final Identifier field_21069 = new Identifier("block/destroy_stage_0");
	public static final Identifier field_21070 = new Identifier("block/destroy_stage_1");
	public static final Identifier field_21071 = new Identifier("block/destroy_stage_2");
	public static final Identifier field_21072 = new Identifier("block/destroy_stage_3");
	public static final Identifier field_21073 = new Identifier("block/destroy_stage_4");
	public static final Identifier field_21074 = new Identifier("block/destroy_stage_5");
	public static final Identifier field_21075 = new Identifier("block/destroy_stage_6");
	public static final Identifier field_21076 = new Identifier("block/destroy_stage_7");
	public static final Identifier field_21077 = new Identifier("block/destroy_stage_8");
	public static final Identifier field_21078 = new Identifier("block/destroy_stage_9");
	private static final Set<Identifier> field_21083 = Sets.newHashSet(
		new Identifier[]{
			field_21067,
			field_21066,
			field_21068,
			field_21064,
			field_21065,
			field_21069,
			field_21070,
			field_21071,
			field_21072,
			field_21073,
			field_21074,
			field_21075,
			field_21076,
			field_21077,
			field_21078,
			new Identifier("item/empty_armor_slot_helmet"),
			new Identifier("item/empty_armor_slot_chestplate"),
			new Identifier("item/empty_armor_slot_leggings"),
			new Identifier("item/empty_armor_slot_boots"),
			new Identifier("item/empty_armor_slot_shield")
		}
	);
	private static final Logger field_21084 = LogManager.getLogger();
	public static final class_4290 field_21079 = new class_4290("builtin/missing", "missing");
	@VisibleForTesting
	public static final String field_21080 = ("{    'textures': {       'particle': '"
			+ class_4276.method_19454().method_5348().getPath()
			+ "',       'missingno': '"
			+ class_4276.method_19454().method_5348().getPath()
			+ "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}")
		.replace('\'', '"');
	private static final Map<String, String> field_21085 = Maps.newHashMap(ImmutableMap.of("missing", field_21080));
	private static final Splitter field_21086 = Splitter.on(',');
	private static final Splitter field_21087 = Splitter.on('=').limit(2);
	public static final class_4231 field_21081 = Util.make(class_4231.method_19217("{}"), arg -> arg.field_20785 = "generation marker");
	public static final class_4231 field_21082 = Util.make(class_4231.method_19217("{}"), arg -> arg.field_20785 = "block entity marker");
	private static final StateManager<Block, BlockState> field_21088 = new StateManager.Builder<Block, BlockState>(Blocks.AIR)
		.method_16928(BooleanProperty.of("map"))
		.build(class_3756::new);
	private final ResourceManager field_21089;
	private final SpriteAtlasTexture field_21058;
	private final Map<class_4290, BakedModel> field_21059 = Maps.newHashMap();
	private static final Map<Identifier, StateManager<Block, BlockState>> field_21060 = ImmutableMap.of(new Identifier("item_frame"), field_21088);
	private final Map<Identifier, class_4291> field_21061 = Maps.newHashMap();
	private final Set<Identifier> field_21062 = Sets.newHashSet();
	private final ModelVariantMap.class_4232 field_21063 = new ModelVariantMap.class_4232();

	public class_4288(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture) {
		this.field_21089 = resourceManager;
		this.field_21058 = spriteAtlasTexture;
	}

	private static Predicate<BlockState> method_19572(StateManager<Block, BlockState> stateManager, String string) {
		Map<Property<?>, Comparable<?>> map = Maps.newHashMap();

		for (String string2 : field_21086.split(string)) {
			Iterator<String> iterator = field_21087.split(string2).iterator();
			if (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				Property<?> property = stateManager.getProperty(string3);
				if (property != null && iterator.hasNext()) {
					String string4 = (String)iterator.next();
					Comparable<?> comparable = method_19573((Property<Comparable<?>>)property, string4);
					if (comparable == null) {
						throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + property.getValues());
					}

					map.put(property, comparable);
				} else if (!string3.isEmpty()) {
					throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
				}
			}
		}

		Block block = stateManager.method_16924();
		return blockState -> {
			if (blockState != null && block == blockState.getBlock()) {
				for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
					if (!Objects.equals(blockState.getProperty((Property)entry.getKey()), entry.getValue())) {
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
	static <T extends Comparable<T>> T method_19573(Property<T> property, String string) {
		return (T)property.getValueAsString(string).orElse(null);
	}

	public class_4291 method_19586(Identifier identifier) {
		if (this.field_21061.containsKey(identifier)) {
			return (class_4291)this.field_21061.get(identifier);
		} else if (this.field_21062.contains(identifier)) {
			throw new IllegalStateException("Circular reference while loading " + identifier);
		} else {
			this.field_21062.add(identifier);
			class_4291 lv = (class_4291)this.field_21061.get(field_21079);

			while (!this.field_21062.isEmpty()) {
				Identifier identifier2 = (Identifier)this.field_21062.iterator().next();

				try {
					if (!this.field_21061.containsKey(identifier2)) {
						this.method_19591(identifier2);
					}
				} catch (class_4288.class_4289 var9) {
					field_21084.warn(var9.getMessage());
					this.field_21061.put(identifier2, lv);
				} catch (Exception var10) {
					field_21084.warn("Unable to load model: '{}' referenced from: {}: {}", identifier2, identifier, var10);
					this.field_21061.put(identifier2, lv);
				} finally {
					this.field_21062.remove(identifier2);
				}
			}

			return (class_4291)this.field_21061.getOrDefault(identifier, lv);
		}
	}

	private void method_19591(Identifier identifier) throws Exception {
		if (!(identifier instanceof class_4290)) {
			this.method_19587(identifier, this.method_19592(identifier));
		} else {
			class_4290 lv = (class_4290)identifier;
			if (Objects.equals(lv.method_19596(), "inventory")) {
				Identifier identifier2 = new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
				class_4231 lv2 = this.method_19592(identifier2);
				this.method_19587(lv, lv2);
				this.field_21061.put(identifier2, lv2);
			} else {
				Identifier identifier3 = new Identifier(identifier.getNamespace(), identifier.getPath());
				StateManager<Block, BlockState> stateManager = (StateManager<Block, BlockState>)Optional.ofNullable(field_21060.get(identifier3))
					.orElseGet(() -> Registry.BLOCK.get(identifier3).getStateManager());
				this.field_21063.method_19235(stateManager);
				ImmutableList<BlockState> immutableList = stateManager.getBlockStates();
				Map<class_4290, BlockState> map = Maps.newHashMap();
				immutableList.forEach(blockState -> {
					BlockState var10000 = (BlockState)map.put(BlockModelShapes.method_19185(identifier3, blockState), blockState);
				});
				Map<BlockState, class_4291> map2 = Maps.newHashMap();
				Identifier identifier4 = new Identifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json");

				try {
					List<Pair<String, ModelVariantMap>> list;
					try {
						list = (List<Pair<String, ModelVariantMap>>)this.field_21089
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
												resource.getResourcePackName(), ModelVariantMap.method_19233(this.field_21063, new InputStreamReader(inputStream, StandardCharsets.UTF_8))
											);
										} catch (Throwable var14x) {
											var3x = var14x;
											throw var14x;
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
									} catch (Exception var16) {
										throw new class_4288.class_4289(
											String.format(
												"Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), var16.getMessage()
											)
										);
									}
								}
							)
							.collect(Collectors.toList());
					} catch (IOException var22) {
						field_21084.warn("Exception loading blockstate definition: {}: {}", identifier4, var22);
						return;
					}

					for (Pair<String, ModelVariantMap> pair : list) {
						ModelVariantMap modelVariantMap = (ModelVariantMap)pair.getSecond();
						Map<BlockState, class_4291> map3 = Maps.newIdentityHashMap();
						class_4291 lv3;
						if (modelVariantMap.method_12357()) {
							lv3 = modelVariantMap.method_12359();
							immutableList.forEach(blockState -> {
								class_4291 var10000 = (class_4291)map3.put(blockState, lv3);
							});
						} else {
							lv3 = null;
						}

						modelVariantMap.method_19232()
							.forEach(
								(string, arg2) -> {
									try {
										immutableList.stream()
											.filter(method_19572(stateManager, string))
											.forEach(
												blockState -> {
													class_4291 lvx = (class_4291)map3.put(blockState, arg2);
													if (lvx != null && lvx != lv3) {
														map3.put(blockState, this.field_21061.get(field_21079));
														throw new RuntimeException(
															"Overlapping definition with: "
																+ (String)((Entry)modelVariantMap.method_19232().entrySet().stream().filter(entry -> entry.getValue() == lvx).findFirst().get()).getKey()
														);
													}
												}
											);
									} catch (Exception var11x) {
										field_21084.warn(
											"Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}",
											identifier4,
											pair.getFirst(),
											string,
											var11x.getMessage()
										);
									}
								}
							);
						map2.putAll(map3);
					}
				} catch (class_4288.class_4289 var23) {
					throw var23;
				} catch (Exception var24) {
					throw new class_4288.class_4289(String.format("Exception loading blockstate definition: '%s': %s", identifier4, var24));
				} finally {
					for (Entry<class_4290, BlockState> entry3 : map.entrySet()) {
						this.method_19587((Identifier)entry3.getKey(), (class_4291)map2.getOrDefault(entry3.getValue(), this.field_21061.get(field_21079)));
					}
				}
			}
		}
	}

	private void method_19587(Identifier identifier, class_4291 arg) {
		this.field_21061.put(identifier, arg);
		this.field_21062.addAll(arg.method_19600());
	}

	private void method_19581(Map<class_4290, class_4291> map, class_4290 arg) {
		map.put(arg, this.method_19586(arg));
	}

	public Map<class_4290, BakedModel> method_19570() {
		Map<class_4290, class_4291> map = Maps.newHashMap();

		try {
			this.field_21061.put(field_21079, this.method_19592(field_21079));
			this.method_19581(map, field_21079);
		} catch (IOException var4) {
			field_21084.error("Error loading missing model, should never happen :(", var4);
			throw new RuntimeException(var4);
		}

		field_21060.forEach(
			(identifier, stateManager) -> stateManager.getBlockStates()
					.forEach(blockState -> this.method_19581(map, BlockModelShapes.method_19185(identifier, blockState)))
		);

		for (Block block : Registry.BLOCK) {
			block.getStateManager().getBlockStates().forEach(blockState -> this.method_19581(map, BlockModelShapes.method_19186(blockState)));
		}

		for (Identifier identifier : Registry.ITEM.getKeySet()) {
			this.method_19581(map, new class_4290(identifier, "inventory"));
		}

		this.method_19581(map, new class_4290("minecraft:trident_in_hand#inventory"));
		Set<String> set = Sets.newLinkedHashSet();
		Set<Identifier> set2 = (Set<Identifier>)map.values().stream().flatMap(arg -> arg.method_19598(this::method_19586, set).stream()).collect(Collectors.toSet());
		set2.addAll(field_21083);
		set.forEach(string -> field_21084.warn("Unable to resolve texture reference: {}", string));
		this.field_21058.method_19510(this.field_21089, set2);
		map.forEach((arg, arg2) -> {
			BakedModel bakedModel = null;

			try {
				bakedModel = arg2.method_19599(this::method_19586, this.field_21058::method_19509, ModelRotation.X0_Y0, false);
			} catch (Exception var5) {
				field_21084.warn("Unable to bake model: '{}': {}", arg, var5);
			}

			if (bakedModel != null) {
				this.field_21059.put(arg, bakedModel);
			}
		});
		return this.field_21059;
	}

	private class_4231 method_19592(Identifier identifier) throws IOException {
		Reader reader = null;
		Resource resource = null;

		class_4231 lv;
		try {
			String string = identifier.getPath();
			if ("builtin/generated".equals(string)) {
				return field_21081;
			}

			if (!"builtin/entity".equals(string)) {
				if (string.startsWith("builtin/")) {
					String string2 = string.substring("builtin/".length());
					String string3 = (String)field_21085.get(string2);
					if (string3 == null) {
						throw new FileNotFoundException(identifier.toString());
					}

					reader = new StringReader(string3);
				} else {
					resource = this.field_21089.getResource(new Identifier(identifier.getNamespace(), "models/" + identifier.getPath() + ".json"));
					reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
				}

				lv = class_4231.method_19216(reader);
				lv.field_20785 = identifier.toString();
				return lv;
			}

			lv = field_21082;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(resource);
		}

		return lv;
	}

	static class class_4289 extends RuntimeException {
		public class_4289(String string) {
			super(string);
		}
	}
}
