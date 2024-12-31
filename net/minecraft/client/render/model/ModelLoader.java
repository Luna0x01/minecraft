package net.minecraft.client.render.model;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.BlockStateMapper;
import net.minecraft.client.Variant;
import net.minecraft.client.class_2877;
import net.minecraft.client.class_2882;
import net.minecraft.client.class_2885;
import net.minecraft.client.class_2903;
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
			new Identifier("blocks/water_overlay"),
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
			new Identifier("items/empty_armor_slot_boots"),
			new Identifier("items/empty_armor_slot_shield"),
			new Identifier("blocks/shulker_top_white"),
			new Identifier("blocks/shulker_top_orange"),
			new Identifier("blocks/shulker_top_magenta"),
			new Identifier("blocks/shulker_top_light_blue"),
			new Identifier("blocks/shulker_top_yellow"),
			new Identifier("blocks/shulker_top_lime"),
			new Identifier("blocks/shulker_top_pink"),
			new Identifier("blocks/shulker_top_gray"),
			new Identifier("blocks/shulker_top_silver"),
			new Identifier("blocks/shulker_top_cyan"),
			new Identifier("blocks/shulker_top_purple"),
			new Identifier("blocks/shulker_top_blue"),
			new Identifier("blocks/shulker_top_brown"),
			new Identifier("blocks/shulker_top_green"),
			new Identifier("blocks/shulker_top_red"),
			new Identifier("blocks/shulker_top_black")
		}
	);
	private static final Logger LOGGER = LogManager.getLogger();
	protected static final ModelIdentifier MISSING_ID = new ModelIdentifier("builtin/missing", "missing");
	private static final String field_13659 = "{    'textures': {       'particle': 'missingno',       'missingno': 'missingno'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}"
		.replaceAll("'", "\"");
	private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.newHashMap();
	private static final Joiner JOINER = Joiner.on(" -> ");
	private final ResourceManager resourceManager;
	private final Map<Identifier, Sprite> sprites = Maps.newHashMap();
	private final Map<Identifier, BlockModel> bakedModels = Maps.newLinkedHashMap();
	private final Map<ModelIdentifier, class_2877> variants = Maps.newLinkedHashMap();
	private final Map<ModelVariantMap, Collection<ModelIdentifier>> field_13660 = Maps.newLinkedHashMap();
	private final SpriteAtlasTexture atlas;
	private final BlockModelShapes field_13661;
	private final BakedQuadFactory BAKED_QUAD_FACTORY = new BakedQuadFactory();
	private final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private final MutableRegistry<ModelIdentifier, BakedModel> field_11309 = new MutableRegistry<>();
	private static final String field_13662 = "{    'elements': [        {   'from': [0, 0, 0],            'to': [16, 16, 16],            'faces': {                'down': {'uv': [0, 0, 16, 16], 'texture': '' }            }        }    ]}"
		.replaceAll("'", "\"");
	private static final BlockModel field_13663 = BlockModel.create(field_13662);
	private static final BlockModel field_13664 = BlockModel.create(field_13662);
	private final Map<String, Identifier> modelsToBake = Maps.newLinkedHashMap();
	private final Map<Identifier, ModelVariantMap> modelVariants = Maps.newHashMap();
	private final Map<Item, List<String>> modelVariantNames = Maps.newIdentityHashMap();

	public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture, BlockModelShapes blockModelShapes) {
		this.resourceManager = resourceManager;
		this.atlas = spriteAtlasTexture;
		this.field_13661 = blockModelShapes;
	}

	public Registry<ModelIdentifier, BakedModel> method_10383() {
		this.method_12511();
		this.method_10393();
		this.method_10407();
		this.method_10409();
		this.method_10411();
		this.method_12515();
		this.method_10404();
		return this.field_11309;
	}

	private void method_12511() {
		BlockStateMapper blockStateMapper = this.field_13661.getBlockStateMapper();

		for (Block block : Block.REGISTRY) {
			for (final Identifier identifier : blockStateMapper.method_12403(block)) {
				try {
					ModelVariantMap modelVariantMap = this.method_12508(identifier);
					Map<BlockState, ModelIdentifier> map = blockStateMapper.method_12404(block);
					if (modelVariantMap.method_12357()) {
						Collection<ModelIdentifier> collection = Sets.newHashSet(map.values());
						modelVariantMap.method_12359().method_12387(block.getStateManager());
						Collection<ModelIdentifier> collection2 = (Collection<ModelIdentifier>)this.field_13660.get(modelVariantMap);
						if (collection2 == null) {
							collection2 = Lists.newArrayList();
							this.field_13660.put(modelVariantMap, collection2);
						}

						collection2.addAll(Lists.newArrayList(Iterables.filter(collection, new Predicate<ModelIdentifier>() {
							public boolean apply(@Nullable ModelIdentifier modelIdentifier) {
								return identifier.equals(modelIdentifier);
							}
						})));
					}

					for (Entry<BlockState, ModelIdentifier> entry : map.entrySet()) {
						ModelIdentifier modelIdentifier = (ModelIdentifier)entry.getValue();
						if (identifier.equals(modelIdentifier)) {
							try {
								this.variants.put(modelIdentifier, modelVariantMap.method_10030(modelIdentifier.getVariant()));
							} catch (RuntimeException var12) {
								if (!modelVariantMap.method_12357()) {
									LOGGER.warn("Unable to load variant: {} from {}", new Object[]{modelIdentifier.getVariant(), modelIdentifier});
								}
							}
						}
					}
				} catch (Exception var13) {
					LOGGER.warn("Unable to load definition {}", new Object[]{identifier, var13});
				}
			}
		}
	}

	private void method_10393() {
		this.variants
			.put(MISSING_ID, new class_2877(Lists.newArrayList(new Variant[]{new Variant(new Identifier(MISSING_ID.getPath()), ModelRotation.X0_Y0, false, 1)})));
		this.method_13891();
		this.method_12512();
		this.method_12513();
		this.method_12514();
	}

	private void method_13891() {
		Identifier identifier = new Identifier("item_frame");
		ModelVariantMap modelVariantMap = this.method_12508(identifier);
		this.method_10387(modelVariantMap, new ModelIdentifier(identifier, "normal"));
		this.method_10387(modelVariantMap, new ModelIdentifier(identifier, "map"));
	}

	private void method_10387(ModelVariantMap modelVariantMap, ModelIdentifier modelIdentifier) {
		try {
			this.variants.put(modelIdentifier, modelVariantMap.method_10030(modelIdentifier.getVariant()));
		} catch (RuntimeException var4) {
			if (!modelVariantMap.method_12357()) {
				LOGGER.warn("Unable to load variant: {} from {}", new Object[]{modelIdentifier.getVariant(), modelIdentifier});
			}
		}
	}

	private ModelVariantMap method_12508(Identifier identifier) {
		Identifier identifier2 = this.method_10395(identifier);
		ModelVariantMap modelVariantMap = (ModelVariantMap)this.modelVariants.get(identifier2);
		if (modelVariantMap == null) {
			modelVariantMap = this.method_12510(identifier, identifier2);
			this.modelVariants.put(identifier2, modelVariantMap);
		}

		return modelVariantMap;
	}

	private ModelVariantMap method_12510(Identifier identifier, Identifier identifier2) {
		List<ModelVariantMap> list = Lists.newArrayList();

		try {
			for (Resource resource : this.resourceManager.getAllResources(identifier2)) {
				list.add(this.method_12509(identifier, resource));
			}
		} catch (IOException var6) {
			throw new RuntimeException("Encountered an exception when loading model definition of model " + identifier2, var6);
		}

		return new ModelVariantMap(list);
	}

	private ModelVariantMap method_12509(Identifier identifier, Resource resource) {
		InputStream inputStream = null;

		ModelVariantMap exception;
		try {
			inputStream = resource.getInputStream();
			exception = ModelVariantMap.fromReader(new InputStreamReader(inputStream, Charsets.UTF_8));
		} catch (Exception var8) {
			throw new RuntimeException(
				"Encountered an exception when loading model definition of '"
					+ identifier
					+ "' from: '"
					+ resource.getId()
					+ "' in resourcepack: '"
					+ resource.getResourcePackName()
					+ "'",
				var8
			);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return exception;
	}

	private Identifier method_10395(Identifier identifier) {
		return new Identifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json");
	}

	private void method_12512() {
		for (Entry<ModelIdentifier, class_2877> entry : this.variants.entrySet()) {
			this.method_12505((ModelIdentifier)entry.getKey(), (class_2877)entry.getValue());
		}
	}

	private void method_12513() {
		for (Entry<ModelVariantMap, Collection<ModelIdentifier>> entry : this.field_13660.entrySet()) {
			ModelIdentifier modelIdentifier = (ModelIdentifier)((Collection)entry.getValue()).iterator().next();

			for (class_2877 lv : ((ModelVariantMap)entry.getKey()).method_12356()) {
				this.method_12505(modelIdentifier, lv);
			}
		}
	}

	private void method_12505(ModelIdentifier modelIdentifier, class_2877 arg) {
		for (Variant variant : arg.method_12375()) {
			Identifier identifier = variant.getIdentifier();
			if (this.bakedModels.get(identifier) == null) {
				try {
					this.bakedModels.put(identifier, this.getModel(identifier));
				} catch (Exception var7) {
					LOGGER.warn("Unable to load block model: '{}' for variant: '{}': {} ", new Object[]{identifier, modelIdentifier, var7});
				}
			}
		}
	}

	private BlockModel getModel(Identifier id) throws IOException {
		Reader reader = null;
		Resource resource = null;

		BlockModel blockModel;
		try {
			String string = id.getPath();
			if ("builtin/generated".equals(string)) {
				return field_13663;
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
					resource = this.resourceManager.getResource(this.derelativizeId(id));
					reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
				}

				blockModel = BlockModel.getFromReader(reader);
				blockModel.field_10928 = id.toString();
				return blockModel;
			}

			blockModel = field_13664;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(resource);
		}

		return blockModel;
	}

	private Identifier derelativizeId(Identifier id) {
		return new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json");
	}

	private void method_12514() {
		this.method_10402();

		for (Item item : Item.REGISTRY) {
			for (String string : this.method_10392(item)) {
				Identifier identifier = this.method_10389(string);
				Identifier identifier2 = Item.REGISTRY.getIdentifier(item);
				this.method_12506(string, identifier, identifier2);
				if (item.hasProperties()) {
					BlockModel blockModel = (BlockModel)this.bakedModels.get(identifier);
					if (blockModel != null) {
						for (Identifier identifier3 : blockModel.method_12352()) {
							this.method_12506(identifier3.toString(), identifier3, identifier2);
						}
					}
				}
			}
		}
	}

	private void method_12506(String string, Identifier identifier, Identifier identifier2) {
		this.modelsToBake.put(string, identifier);
		if (this.bakedModels.get(identifier) == null) {
			try {
				BlockModel blockModel = this.getModel(identifier);
				this.bakedModels.put(identifier, blockModel);
			} catch (Exception var5) {
				LOGGER.warn("Unable to load item model: '{}' for item: '{}'", new Object[]{identifier, identifier2, var5});
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
		this.modelVariantNames.put(Items.COAL, Lists.newArrayList(new String[]{"coal", "charcoal"}));
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
		this.modelVariantNames.put(Items.POTION, Lists.newArrayList(new String[]{"bottle_drinkable"}));
		this.modelVariantNames
			.put(Items.SKULL, Lists.newArrayList(new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper", "skull_dragon"}));
		this.modelVariantNames.put(Items.SPLASH_POTION, Lists.newArrayList(new String[]{"bottle_splash"}));
		this.modelVariantNames.put(Items.LINGERING_POTION, Lists.newArrayList(new String[]{"bottle_lingering"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.AIR), Collections.emptyList());
		this.modelVariantNames.put(Item.fromBlock(Blocks.OAK_FENCE_GATE), Lists.newArrayList(new String[]{"oak_fence_gate"}));
		this.modelVariantNames.put(Item.fromBlock(Blocks.OAK_FENCE), Lists.newArrayList(new String[]{"oak_fence"}));
		this.modelVariantNames.put(Items.OAK_DOOR, Lists.newArrayList(new String[]{"oak_door"}));
		this.modelVariantNames.put(Items.BOAT, Lists.newArrayList(new String[]{"oak_boat"}));
		this.modelVariantNames.put(Items.TOTEM_OF_UNDYING, Lists.newArrayList(new String[]{"totem"}));
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

	private void method_12515() {
		for (ModelIdentifier modelIdentifier : this.variants.keySet()) {
			BakedModel bakedModel = this.method_12504((class_2877)this.variants.get(modelIdentifier), modelIdentifier.toString());
			if (bakedModel != null) {
				this.field_11309.put(modelIdentifier, bakedModel);
			}
		}

		for (Entry<ModelVariantMap, Collection<ModelIdentifier>> entry : this.field_13660.entrySet()) {
			ModelVariantMap modelVariantMap = (ModelVariantMap)entry.getKey();
			class_2882 lv = modelVariantMap.method_12359();
			String string = Block.REGISTRY.getIdentifier(lv.method_12389().getBlock()).toString();
			class_2903.class_2904 lv2 = new class_2903.class_2904();

			for (class_2885 lv3 : lv.method_12386()) {
				BakedModel bakedModel2 = this.method_12504(lv3.method_12393(), "selector of " + string);
				if (bakedModel2 != null) {
					lv2.method_12518(lv3.method_12394(lv.method_12389()), bakedModel2);
				}
			}

			BakedModel bakedModel3 = lv2.method_12517();

			for (ModelIdentifier modelIdentifier2 : (Collection)entry.getValue()) {
				if (!modelVariantMap.method_12358(modelIdentifier2.getVariant())) {
					this.field_11309.put(modelIdentifier2, bakedModel3);
				}
			}
		}
	}

	@Nullable
	private BakedModel method_12504(class_2877 arg, String string) {
		if (arg.method_12375().isEmpty()) {
			return null;
		} else {
			WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
			int i = 0;

			for (Variant variant : arg.method_12375()) {
				BlockModel blockModel = (BlockModel)this.bakedModels.get(variant.getIdentifier());
				if (blockModel == null || !blockModel.method_10018()) {
					LOGGER.warn("Missing model for: {}", new Object[]{string});
				} else if (blockModel.getElements().isEmpty()) {
					LOGGER.warn("Missing elements for: {}", new Object[]{string});
				} else {
					BakedModel bakedModel = this.method_10386(blockModel, variant.getRotation(), variant.getUvLock());
					if (bakedModel != null) {
						i++;
						builder.add(bakedModel, variant.getWeight());
					}
				}
			}

			BakedModel bakedModel2 = null;
			if (i == 0) {
				LOGGER.warn("No weighted models for: {}", new Object[]{string});
			} else if (i == 1) {
				bakedModel2 = builder.getFirst();
			} else {
				bakedModel2 = builder.build();
			}

			return bakedModel2;
		}
	}

	private void method_10404() {
		for (Entry<String, Identifier> entry : this.modelsToBake.entrySet()) {
			Identifier identifier = (Identifier)entry.getValue();
			ModelIdentifier modelIdentifier = new ModelIdentifier((String)entry.getKey(), "inventory");
			BlockModel blockModel = (BlockModel)this.bakedModels.get(identifier);
			if (blockModel == null || !blockModel.method_10018()) {
				LOGGER.warn("Missing model for: {}", new Object[]{identifier});
			} else if (blockModel.getElements().isEmpty()) {
				LOGGER.warn("Missing elements for: {}", new Object[]{identifier});
			} else if (this.method_10397(blockModel)) {
				this.field_11309.put(modelIdentifier, new BuiltinBakedModel(blockModel.getTransformation(), blockModel.method_12354()));
			} else {
				BakedModel bakedModel = this.method_10386(blockModel, ModelRotation.X0_Y0, false);
				if (bakedModel != null) {
					this.field_11309.put(modelIdentifier, bakedModel);
				}
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
			class_2877 lv = (class_2877)this.variants.get(modelIdentifier);

			for (Variant variant : lv.method_12375()) {
				BlockModel blockModel = (BlockModel)this.bakedModels.get(variant.getIdentifier());
				if (blockModel == null) {
					LOGGER.warn("Missing model for: {}", new Object[]{modelIdentifier});
				} else {
					set.addAll(this.method_10385(blockModel));
				}
			}
		}

		for (ModelVariantMap modelVariantMap : this.field_13660.keySet()) {
			for (class_2877 lv2 : modelVariantMap.method_12359().method_12388()) {
				for (Variant variant2 : lv2.method_12375()) {
					BlockModel blockModel2 = (BlockModel)this.bakedModels.get(variant2.getIdentifier());
					if (blockModel2 == null) {
						LOGGER.warn("Missing model for: {}", new Object[]{Block.REGISTRY.getIdentifier(modelVariantMap.method_12359().method_12389().getBlock())});
					} else {
						set.addAll(this.method_10385(blockModel2));
					}
				}
			}
		}

		set.addAll(DEFAULT_TEXTURES);
		return set;
	}

	@Nullable
	private BakedModel method_10386(BlockModel blockModel, ModelRotation modelRotation, boolean bl) {
		Sprite sprite = (Sprite)this.sprites.get(new Identifier(blockModel.resolveTexture("particle")));
		BasicBakedModel.Builder builder = new BasicBakedModel.Builder(blockModel, blockModel.method_12354()).setParticle(sprite);
		if (blockModel.getElements().isEmpty()) {
			return null;
		} else {
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
			this.method_12507(deque, set, (BlockModel)this.bakedModels.get(identifier));
		}

		while (!deque.isEmpty()) {
			Identifier identifier2 = (Identifier)deque.pop();

			try {
				if (this.bakedModels.get(identifier2) != null) {
					continue;
				}

				BlockModel blockModel = this.getModel(identifier2);
				this.bakedModels.put(identifier2, blockModel);
				this.method_12507(deque, set, blockModel);
			} catch (Exception var5) {
				LOGGER.warn("In parent chain: {}; unable to load model: '{}'", new Object[]{JOINER.join(this.method_10403(identifier2)), identifier2, var5});
			}

			set.add(identifier2);
		}
	}

	private void method_12507(Deque<Identifier> deque, Set<Identifier> set, BlockModel blockModel) {
		Identifier identifier = blockModel.getId();
		if (identifier != null && !set.contains(identifier)) {
			deque.add(identifier);
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

	@Nullable
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
						set.add(new Identifier(blockModel.resolveTexture(string)));
					}
				} else if (!this.method_10397(blockModel)) {
					for (ModelElement modelElement : blockModel.getElements()) {
						for (ModelElementFace modelElementFace : modelElement.faces.values()) {
							Identifier identifier2 = new Identifier(blockModel.resolveTexture(modelElementFace.textureId));
							set.add(identifier2);
						}
					}
				}
			}
		}

		return set;
	}

	private boolean method_10394(@Nullable BlockModel blockModel) {
		return blockModel == null ? false : blockModel.getRootModel() == field_13663;
	}

	private boolean method_10397(@Nullable BlockModel blockModel) {
		if (blockModel == null) {
			return false;
		} else {
			BlockModel blockModel2 = blockModel.getRootModel();
			return blockModel2 == field_13664;
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
		BUILTIN_MODEL_DEFINITIONS.put("missing", field_13659);
		field_13663.field_10928 = "generation marker";
		field_13664.field_10928 = "block entity marker";
	}
}
