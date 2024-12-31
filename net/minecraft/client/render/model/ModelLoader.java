package net.minecraft.client.render.model;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModelShapes;
import net.minecraft.client.render.model.json.BlockModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureCreator;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelLoader {
	private static final Set<Identifier> DEFAULT_TEXTURES = Sets.newHashSet(
		new Identifier[]{
			new Identifier("blocks/water_flow"),
			new Identifier("blocks/water_still"),
			new Identifier("blocks/lava_flow"),
			new Identifier("blocks/lava_still"),
			new Identifier("blocks/destroy_stage_0"),
			new Identifier("blocks/destroy_stage_1"),
			new Identifier("blocks/destroy_stage_2"),
			new Identifier("blocks/destroy_stage_3"),
			new Identifier("blocks/destroy_stage_4"),
			new Identifier("blocks/destroy_stage_5"),
			new Identifier("blocks/destroy_stage_6"),
			new Identifier("blocks/destroy_stage_7"),
			new Identifier("blocks/destroy_stage_8"),
			new Identifier("blocks/destroy_stage_9"),
			new Identifier("items/empty_armor_slot_helmet"),
			new Identifier("items/empty_armor_slot_chestplate"),
			new Identifier("items/empty_armor_slot_leggings"),
			new Identifier("items/empty_armor_slot_boots")
		}
	);
	private static final Logger LOGGER = LogManager.getLogger();
	protected static final ModelIdentifier MISSING_ID = new ModelIdentifier("builtin/missing", "missing");
	private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.newHashMap();
	private static final Joiner JOINER = Joiner.on(" -> ");
	private final ResourceManager resourceManager;
	private final Map<Identifier, Sprite> sprites = Maps.newHashMap();
	private final Map<Identifier, BlockModel> bakedModels = Maps.newLinkedHashMap();
	private final Map<ModelIdentifier, ModelVariantMap.Variant> variants = Maps.newLinkedHashMap();
	private final SpriteAtlasTexture atlas;
	private final BlockModelShapes BLOCK_MODEL_SHAPES;
	private final BakedQuadFactory BAKED_QUAD_FACTORY = new BakedQuadFactory();
	private final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private MutableRegistry<ModelIdentifier, BakedModel> field_11309 = new MutableRegistry<>();
	private static final BlockModel BUILTIN_GENERATED = BlockModel.create(
		"{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
	);
	private static final BlockModel BUILTIN_COMPASS = BlockModel.create(
		"{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
	);
	private static final BlockModel BUILTIN_CLOCK = BlockModel.create(
		"{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
	);
	private static final BlockModel BUILTIN_ENTITY = BlockModel.create(
		"{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
	);
	private Map<String, Identifier> modelsToBake = Maps.newLinkedHashMap();
	private final Map<Identifier, ModelVariantMap> modelVariants = Maps.newHashMap();
	private Map<Item, List<String>> modelVariantNames = Maps.newIdentityHashMap();

	public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture, BlockModelShapes blockModelShapes) {
		this.resourceManager = resourceManager;
		this.atlas = spriteAtlasTexture;
		this.BLOCK_MODEL_SHAPES = blockModelShapes;
	}

	public Registry<ModelIdentifier, BakedModel> method_10383() {
		this.method_10393();
		this.method_10407();
		this.method_10409();
		this.method_10411();
		this.method_10404();
		return this.field_11309;
	}

	private void method_10393() {
		this.method_10390(this.BLOCK_MODEL_SHAPES.getBlockStateMapper().getBlockStateMap().values());
		this.variants
			.put(
				MISSING_ID,
				new ModelVariantMap.Variant(
					MISSING_ID.getVariant(),
					Lists.newArrayList(new ModelVariantMap.Entry[]{new ModelVariantMap.Entry(new Identifier(MISSING_ID.getPath()), ModelRotation.X0_Y0, false, 1)})
				)
			);
		Identifier identifier = new Identifier("item_frame");
		ModelVariantMap modelVariantMap = this.method_10391(identifier);
		this.method_10387(modelVariantMap, new ModelIdentifier(identifier, "normal"));
		this.method_10387(modelVariantMap, new ModelIdentifier(identifier, "map"));
		this.method_10396();
		this.load();
	}

	private void method_10390(Collection<ModelIdentifier> collection) {
		for (ModelIdentifier modelIdentifier : collection) {
			try {
				ModelVariantMap modelVariantMap = this.method_10391(modelIdentifier);

				try {
					this.method_10387(modelVariantMap, modelIdentifier);
				} catch (Exception var6) {
					LOGGER.warn("Unable to load variant: " + modelIdentifier.getVariant() + " from " + modelIdentifier);
				}
			} catch (Exception var7) {
				LOGGER.warn("Unable to load definition " + modelIdentifier, var7);
			}
		}
	}

	private void method_10387(ModelVariantMap modelVariantMap, ModelIdentifier modelIdentifier) {
		this.variants.put(modelIdentifier, modelVariantMap.getVariant(modelIdentifier.getVariant()));
	}

	private ModelVariantMap method_10391(Identifier identifier) {
		Identifier identifier2 = this.method_10395(identifier);
		ModelVariantMap modelVariantMap = (ModelVariantMap)this.modelVariants.get(identifier2);
		if (modelVariantMap == null) {
			List<ModelVariantMap> list = Lists.newArrayList();

			try {
				for (Resource resource : this.resourceManager.getAllResources(identifier2)) {
					InputStream inputStream = null;

					try {
						inputStream = resource.getInputStream();
						ModelVariantMap modelVariantMap2 = ModelVariantMap.fromReader(new InputStreamReader(inputStream, Charsets.UTF_8));
						list.add(modelVariantMap2);
					} catch (Exception var13) {
						throw new RuntimeException(
							"Encountered an exception when loading model definition of '"
								+ identifier
								+ "' from: '"
								+ resource.getId()
								+ "' in resourcepack: '"
								+ resource.getResourcePackName()
								+ "'",
							var13
						);
					} finally {
						IOUtils.closeQuietly(inputStream);
					}
				}
			} catch (IOException var15) {
				throw new RuntimeException("Encountered an exception when loading model definition of model " + identifier2.toString(), var15);
			}

			modelVariantMap = new ModelVariantMap(list);
			this.modelVariants.put(identifier2, modelVariantMap);
		}

		return modelVariantMap;
	}

	private Identifier method_10395(Identifier identifier) {
		return new Identifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json");
	}

	private void method_10396() {
		for (ModelIdentifier modelIdentifier : this.variants.keySet()) {
			for (ModelVariantMap.Entry entry : ((ModelVariantMap.Variant)this.variants.get(modelIdentifier)).getEntries()) {
				Identifier identifier = entry.getId();
				if (this.bakedModels.get(identifier) == null) {
					try {
						BlockModel blockModel = this.getModel(identifier);
						this.bakedModels.put(identifier, blockModel);
					} catch (Exception var7) {
						LOGGER.warn("Unable to load block model: '" + identifier + "' for variant: '" + modelIdentifier + "'", var7);
					}
				}
			}
		}
	}

	private BlockModel getModel(Identifier id) throws IOException {
		String string = id.getPath();
		if ("builtin/generated".equals(string)) {
			return BUILTIN_GENERATED;
		} else if ("builtin/compass".equals(string)) {
			return BUILTIN_COMPASS;
		} else if ("builtin/clock".equals(string)) {
			return BUILTIN_CLOCK;
		} else if ("builtin/entity".equals(string)) {
			return BUILTIN_ENTITY;
		} else {
			Reader reader;
			if (string.startsWith("builtin/")) {
				String string2 = string.substring("builtin/".length());
				String string3 = (String)BUILTIN_MODEL_DEFINITIONS.get(string2);
				if (string3 == null) {
					throw new FileNotFoundException(id.toString());
				}

				reader = new StringReader(string3);
			} else {
				Resource resource = this.resourceManager.getResource(this.derelativizeId(id));
				reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
			}

			BlockModel var11;
			try {
				BlockModel blockModel = BlockModel.getFromReader(reader);
				blockModel.field_10928 = id.toString();
				var11 = blockModel;
			} finally {
				reader.close();
			}

			return var11;
		}
	}

	private Identifier derelativizeId(Identifier id) {
		return new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json");
	}

	private void load() {
		this.method_10402();

		for (Item item : Item.REGISTRY) {
			for (String string : this.method_10392(item)) {
				Identifier identifier = this.method_10389(string);
				this.modelsToBake.put(string, identifier);
				if (this.bakedModels.get(identifier) == null) {
					try {
						BlockModel blockModel = this.getModel(identifier);
						this.bakedModels.put(identifier, blockModel);
					} catch (Exception var8) {
						LOGGER.warn("Unable to load item model: '" + identifier + "' for item: '" + Item.REGISTRY.getIdentifier(item) + "'", var8);
					}
				}
			}
		}
	}

	private void method_10402() {
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.STONE),
				Lists.newArrayList(new String[]{"stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth"})
			);
		this.modelVariantNames.put(Item.fromBlock(Blocks.DIRT), Lists.newArrayList(new String[]{"dirt", "coarse_dirt", "podzol"}));
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.PLANKS),
				Lists.newArrayList(new String[]{"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"})
			);
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.SAPLING),
				Lists.newArrayList(new String[]{"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"})
			);
		this.modelVariantNames.put(Item.fromBlock(Blocks.SAND), Lists.newArrayList(new String[]{"sand", "red_sand"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.LOG), Lists.newArrayList(new String[]{"oak_log", "spruce_log", "birch_log", "jungle_log"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.LEAVES), Lists.newArrayList(new String[]{"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.SPONGE), Lists.newArrayList(new String[]{"sponge", "sponge_wet"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.SANDSTONE), Lists.newArrayList(new String[]{"sandstone", "chiseled_sandstone", "smooth_sandstone"}));
		this.modelVariantNames
			.put(Item.fromBlock(Blocks.RED_SANDSTONE), Lists.newArrayList(new String[]{"red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.TALLGRASS), Lists.newArrayList(new String[]{"dead_bush", "tall_grass", "fern"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.DEADBUSH), Lists.newArrayList(new String[]{"dead_bush"}));
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.WOOL),
				Lists.newArrayList(
					new String[]{
						"black_wool",
						"red_wool",
						"green_wool",
						"brown_wool",
						"blue_wool",
						"purple_wool",
						"cyan_wool",
						"silver_wool",
						"gray_wool",
						"pink_wool",
						"lime_wool",
						"yellow_wool",
						"light_blue_wool",
						"magenta_wool",
						"orange_wool",
						"white_wool"
					}
				)
			);
		this.modelVariantNames.put(Item.fromBlock(Blocks.YELLOW_FLOWER), Lists.newArrayList(new String[]{"dandelion"}));
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.RED_FLOWER),
				Lists.newArrayList(new String[]{"poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"})
			);
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.STONE_SLAB),
				Lists.newArrayList(new String[]{"stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"})
			);
		this.modelVariantNames.put(Item.fromBlock(Blocks.STONE_SLAB2), Lists.newArrayList(new String[]{"red_sandstone_slab"}));
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.STAINED_GLASS),
				Lists.newArrayList(
					new String[]{
						"black_stained_glass",
						"red_stained_glass",
						"green_stained_glass",
						"brown_stained_glass",
						"blue_stained_glass",
						"purple_stained_glass",
						"cyan_stained_glass",
						"silver_stained_glass",
						"gray_stained_glass",
						"pink_stained_glass",
						"lime_stained_glass",
						"yellow_stained_glass",
						"light_blue_stained_glass",
						"magenta_stained_glass",
						"orange_stained_glass",
						"white_stained_glass"
					}
				)
			);
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.MONSTER_EGG),
				Lists.newArrayList(
					new String[]{
						"stone_monster_egg",
						"cobblestone_monster_egg",
						"stone_brick_monster_egg",
						"mossy_brick_monster_egg",
						"cracked_brick_monster_egg",
						"chiseled_brick_monster_egg"
					}
				)
			);
		this.modelVariantNames
			.put(Item.fromBlock(Blocks.STONE_BRICKS), Lists.newArrayList(new String[]{"stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"}));
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.WOODEN_SLAB),
				Lists.newArrayList(new String[]{"oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab"})
			);
		this.modelVariantNames.put(Item.fromBlock(Blocks.COBBLESTONE_WALL), Lists.newArrayList(new String[]{"cobblestone_wall", "mossy_cobblestone_wall"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.ANVIL), Lists.newArrayList(new String[]{"anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.QUARTZ_BLOCK), Lists.newArrayList(new String[]{"quartz_block", "chiseled_quartz_block", "quartz_column"}));
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.STAINED_TERRACOTTA),
				Lists.newArrayList(
					new String[]{
						"black_stained_hardened_clay",
						"red_stained_hardened_clay",
						"green_stained_hardened_clay",
						"brown_stained_hardened_clay",
						"blue_stained_hardened_clay",
						"purple_stained_hardened_clay",
						"cyan_stained_hardened_clay",
						"silver_stained_hardened_clay",
						"gray_stained_hardened_clay",
						"pink_stained_hardened_clay",
						"lime_stained_hardened_clay",
						"yellow_stained_hardened_clay",
						"light_blue_stained_hardened_clay",
						"magenta_stained_hardened_clay",
						"orange_stained_hardened_clay",
						"white_stained_hardened_clay"
					}
				)
			);
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.STAINED_GLASS_PANE),
				Lists.newArrayList(
					new String[]{
						"black_stained_glass_pane",
						"red_stained_glass_pane",
						"green_stained_glass_pane",
						"brown_stained_glass_pane",
						"blue_stained_glass_pane",
						"purple_stained_glass_pane",
						"cyan_stained_glass_pane",
						"silver_stained_glass_pane",
						"gray_stained_glass_pane",
						"pink_stained_glass_pane",
						"lime_stained_glass_pane",
						"yellow_stained_glass_pane",
						"light_blue_stained_glass_pane",
						"magenta_stained_glass_pane",
						"orange_stained_glass_pane",
						"white_stained_glass_pane"
					}
				)
			);
		this.modelVariantNames.put(Item.fromBlock(Blocks.LEAVES2), Lists.newArrayList(new String[]{"acacia_leaves", "dark_oak_leaves"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.LOG2), Lists.newArrayList(new String[]{"acacia_log", "dark_oak_log"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.PRISMARINE), Lists.newArrayList(new String[]{"prismarine", "prismarine_bricks", "dark_prismarine"}));
		this.modelVariantNames
			.put(
				Item.fromBlock(Blocks.CARPET),
				Lists.newArrayList(
					new String[]{
						"black_carpet",
						"red_carpet",
						"green_carpet",
						"brown_carpet",
						"blue_carpet",
						"purple_carpet",
						"cyan_carpet",
						"silver_carpet",
						"gray_carpet",
						"pink_carpet",
						"lime_carpet",
						"yellow_carpet",
						"light_blue_carpet",
						"magenta_carpet",
						"orange_carpet",
						"white_carpet"
					}
				)
			);
		this.modelVariantNames
			.put(Item.fromBlock(Blocks.DOUBLE_PLANT), Lists.newArrayList(new String[]{"sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"}));
		this.modelVariantNames.put(Items.BOW, Lists.newArrayList(new String[]{"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"}));
		this.modelVariantNames.put(Items.COAL, Lists.newArrayList(new String[]{"coal", "charcoal"}));
		this.modelVariantNames.put(Items.FISHING_ROD, Lists.newArrayList(new String[]{"fishing_rod", "fishing_rod_cast"}));
		this.modelVariantNames.put(Items.RAW_FISH, Lists.newArrayList(new String[]{"cod", "salmon", "clownfish", "pufferfish"}));
		this.modelVariantNames.put(Items.COOKED_FISH, Lists.newArrayList(new String[]{"cooked_cod", "cooked_salmon"}));
		this.modelVariantNames
			.put(
				Items.DYE,
				Lists.newArrayList(
					new String[]{
						"dye_black",
						"dye_red",
						"dye_green",
						"dye_brown",
						"dye_blue",
						"dye_purple",
						"dye_cyan",
						"dye_silver",
						"dye_gray",
						"dye_pink",
						"dye_lime",
						"dye_yellow",
						"dye_light_blue",
						"dye_magenta",
						"dye_orange",
						"dye_white"
					}
				)
			);
		this.modelVariantNames.put(Items.POTION, Lists.newArrayList(new String[]{"bottle_drinkable", "bottle_splash"}));
		this.modelVariantNames.put(Items.SKULL, Lists.newArrayList(new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.OAK_FENCE_GATE), Lists.newArrayList(new String[]{"oak_fence_gate"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.OAK_FENCE), Lists.newArrayList(new String[]{"oak_fence"}));
		this.modelVariantNames.put(Items.OAK_DOOR, Lists.newArrayList(new String[]{"oak_door"}));
	}

	private List<String> method_10392(Item item) {
		List<String> list = (List<String>)this.modelVariantNames.get(item);
		if (list == null) {
			list = Collections.singletonList(Item.REGISTRY.getIdentifier(item).toString());
		}

		return list;
	}

	private Identifier method_10389(String string) {
		Identifier identifier = new Identifier(string);
		return new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
	}

	private void method_10404() {
		for (ModelIdentifier modelIdentifier : this.variants.keySet()) {
			WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
			int i = 0;

			for (ModelVariantMap.Entry entry : ((ModelVariantMap.Variant)this.variants.get(modelIdentifier)).getEntries()) {
				BlockModel blockModel = (BlockModel)this.bakedModels.get(entry.getId());
				if (blockModel != null && blockModel.method_10018()) {
					i++;
					builder.add(this.method_10386(blockModel, entry.getRotation(), entry.hasUvLock()), entry.getWeight());
				} else {
					LOGGER.warn("Missing model for: " + modelIdentifier);
				}
			}

			if (i == 0) {
				LOGGER.warn("No weighted models for: " + modelIdentifier);
			} else if (i == 1) {
				this.field_11309.put(modelIdentifier, builder.getFirst());
			} else {
				this.field_11309.put(modelIdentifier, builder.build());
			}
		}

		for (Entry<String, Identifier> entry2 : this.modelsToBake.entrySet()) {
			Identifier identifier = (Identifier)entry2.getValue();
			ModelIdentifier modelIdentifier2 = new ModelIdentifier((String)entry2.getKey(), "inventory");
			BlockModel blockModel2 = (BlockModel)this.bakedModels.get(identifier);
			if (blockModel2 == null || !blockModel2.method_10018()) {
				LOGGER.warn("Missing model for: " + identifier);
			} else if (this.method_10397(blockModel2)) {
				this.field_11309.put(modelIdentifier2, new BuiltinBakedModel(blockModel2.getTransformation()));
			} else {
				this.field_11309.put(modelIdentifier2, this.method_10386(blockModel2, ModelRotation.X0_Y0, false));
			}
		}
	}

	private Set<Identifier> method_10406() {
		Set<Identifier> set = Sets.newHashSet();
		List<ModelIdentifier> list = Lists.newArrayList(this.variants.keySet());
		Collections.sort(list, new Comparator<ModelIdentifier>() {
			public int compare(ModelIdentifier modelIdentifier, ModelIdentifier modelIdentifier2) {
				return modelIdentifier.toString().compareTo(modelIdentifier2.toString());
			}
		});

		for (ModelIdentifier modelIdentifier : list) {
			ModelVariantMap.Variant variant = (ModelVariantMap.Variant)this.variants.get(modelIdentifier);

			for (ModelVariantMap.Entry entry : variant.getEntries()) {
				BlockModel blockModel = (BlockModel)this.bakedModels.get(entry.getId());
				if (blockModel == null) {
					LOGGER.warn("Missing model for: " + modelIdentifier);
				} else {
					set.addAll(this.method_10385(blockModel));
				}
			}
		}

		set.addAll(DEFAULT_TEXTURES);
		return set;
	}

	private BakedModel method_10386(BlockModel blockModel, ModelRotation modelRotation, boolean bl) {
		Sprite sprite = (Sprite)this.sprites.get(new Identifier(blockModel.resolveTexture("particle")));
		BasicBakedModel.Builder builder = new BasicBakedModel.Builder(blockModel).setParticle(sprite);

		for (ModelElement modelElement : blockModel.getElements()) {
			for (Direction direction : modelElement.faces.keySet()) {
				ModelElementFace modelElementFace = (ModelElementFace)modelElement.faces.get(direction);
				Sprite sprite2 = (Sprite)this.sprites.get(new Identifier(blockModel.resolveTexture(modelElementFace.textureId)));
				if (modelElementFace.cullFace == null) {
					builder.addQuad(this.method_10384(modelElement, modelElementFace, sprite2, direction, modelRotation, bl));
				} else {
					builder.addQuad(modelRotation.rotate(modelElementFace.cullFace), this.method_10384(modelElement, modelElementFace, sprite2, direction, modelRotation, bl));
				}
			}
		}

		return builder.build();
	}

	private BakedQuad method_10384(
		ModelElement modelElement, ModelElementFace modelElementFace, Sprite sprite, Direction direction, ModelRotation modelRotation, boolean bl
	) {
		return this.BAKED_QUAD_FACTORY
			.bake(modelElement.from, modelElement.to, modelElementFace, sprite, direction, modelRotation, modelElement.rotation, bl, modelElement.shade);
	}

	private void method_10407() {
		this.method_10408();

		for (BlockModel blockModel : this.bakedModels.values()) {
			blockModel.refreshParent(this.bakedModels);
		}

		BlockModel.method_10015(this.bakedModels);
	}

	private void method_10408() {
		Deque<Identifier> deque = Queues.newArrayDeque();
		Set<Identifier> set = Sets.newHashSet();

		for (Identifier identifier : this.bakedModels.keySet()) {
			set.add(identifier);
			Identifier identifier2 = ((BlockModel)this.bakedModels.get(identifier)).getId();
			if (identifier2 != null) {
				deque.add(identifier2);
			}
		}

		while (!deque.isEmpty()) {
			Identifier identifier3 = (Identifier)deque.pop();

			try {
				if (this.bakedModels.get(identifier3) != null) {
					continue;
				}

				BlockModel blockModel = this.getModel(identifier3);
				this.bakedModels.put(identifier3, blockModel);
				Identifier identifier4 = blockModel.getId();
				if (identifier4 != null && !set.contains(identifier4)) {
					deque.add(identifier4);
				}
			} catch (Exception var6) {
				LOGGER.warn("In parent chain: " + JOINER.join(this.method_10403(identifier3)) + "; unable to load model: '" + identifier3 + "'", var6);
			}

			set.add(identifier3);
		}
	}

	private List<Identifier> method_10403(Identifier identifier) {
		List<Identifier> list = Lists.newArrayList(new Identifier[]{identifier});
		Identifier identifier2 = identifier;

		while ((identifier2 = this.method_10405(identifier2)) != null) {
			list.add(0, identifier2);
		}

		return list;
	}

	private Identifier method_10405(Identifier identifier) {
		for (Entry<Identifier, BlockModel> entry : this.bakedModels.entrySet()) {
			BlockModel blockModel = (BlockModel)entry.getValue();
			if (blockModel != null && identifier.equals(blockModel.getId())) {
				return (Identifier)entry.getKey();
			}
		}

		return null;
	}

	private Set<Identifier> method_10385(BlockModel blockModel) {
		Set<Identifier> set = Sets.newHashSet();

		for (ModelElement modelElement : blockModel.getElements()) {
			for (ModelElementFace modelElementFace : modelElement.faces.values()) {
				Identifier identifier = new Identifier(blockModel.resolveTexture(modelElementFace.textureId));
				set.add(identifier);
			}
		}

		set.add(new Identifier(blockModel.resolveTexture("particle")));
		return set;
	}

	private void method_10409() {
		final Set<Identifier> set = this.method_10406();
		set.addAll(this.method_10410());
		set.remove(SpriteAtlasTexture.MISSING);
		TextureCreator textureCreator = new TextureCreator() {
			@Override
			public void create(SpriteAtlasTexture atlasTexture) {
				for (Identifier identifier : set) {
					Sprite sprite = atlasTexture.getSprite(identifier);
					ModelLoader.this.sprites.put(identifier, sprite);
				}
			}
		};
		this.atlas.method_10315(this.resourceManager, textureCreator);
		this.sprites.put(new Identifier("missingno"), this.atlas.getTexture());
	}

	private Set<Identifier> method_10410() {
		Set<Identifier> set = Sets.newHashSet();

		for (Identifier identifier : this.modelsToBake.values()) {
			BlockModel blockModel = (BlockModel)this.bakedModels.get(identifier);
			if (blockModel != null) {
				set.add(new Identifier(blockModel.resolveTexture("particle")));
				if (this.method_10394(blockModel)) {
					for (String string : ItemModelGenerator.LAYERS) {
						Identifier identifier2 = new Identifier(blockModel.resolveTexture(string));
						if (blockModel.getRootModel() == BUILTIN_COMPASS && !SpriteAtlasTexture.MISSING.equals(identifier2)) {
							Sprite.setCompassTex(identifier2.toString());
						} else if (blockModel.getRootModel() == BUILTIN_CLOCK && !SpriteAtlasTexture.MISSING.equals(identifier2)) {
							Sprite.setClockTex(identifier2.toString());
						}

						set.add(identifier2);
					}
				} else if (!this.method_10397(blockModel)) {
					for (ModelElement modelElement : blockModel.getElements()) {
						for (ModelElementFace modelElementFace : modelElement.faces.values()) {
							Identifier identifier3 = new Identifier(blockModel.resolveTexture(modelElementFace.textureId));
							set.add(identifier3);
						}
					}
				}
			}
		}

		return set;
	}

	private boolean method_10394(BlockModel blockModel) {
		if (blockModel == null) {
			return false;
		} else {
			BlockModel blockModel2 = blockModel.getRootModel();
			return blockModel2 == BUILTIN_GENERATED || blockModel2 == BUILTIN_COMPASS || blockModel2 == BUILTIN_CLOCK;
		}
	}

	private boolean method_10397(BlockModel blockModel) {
		if (blockModel == null) {
			return false;
		} else {
			BlockModel blockModel2 = blockModel.getRootModel();
			return blockModel2 == BUILTIN_ENTITY;
		}
	}

	private void method_10411() {
		for (Identifier identifier : this.modelsToBake.values()) {
			BlockModel blockModel = (BlockModel)this.bakedModels.get(identifier);
			if (this.method_10394(blockModel)) {
				BlockModel blockModel2 = this.method_10400(blockModel);
				if (blockModel2 != null) {
					blockModel2.field_10928 = identifier.toString();
				}

				this.bakedModels.put(identifier, blockModel2);
			} else if (this.method_10397(blockModel)) {
				this.bakedModels.put(identifier, blockModel);
			}
		}

		for (Sprite sprite : this.sprites.values()) {
			if (!sprite.hasMeta()) {
				sprite.clearFrames();
			}
		}
	}

	private BlockModel method_10400(BlockModel blockModel) {
		return this.ITEM_MODEL_GENERATOR.method_10065(this.atlas, blockModel);
	}

	static {
		BUILTIN_MODEL_DEFINITIONS.put(
			"missing",
			"{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}"
		);
		BUILTIN_GENERATED.field_10928 = "generation marker";
		BUILTIN_COMPASS.field_10928 = "compass generation marker";
		BUILTIN_CLOCK.field_10928 = "class generation marker";
		BUILTIN_ENTITY.field_10928 = "block entity marker";
	}
}
