package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.decorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;
import net.minecraft.world.gen.placer.ColumnPlacer;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.ForestFlowerStateProvider;
import net.minecraft.world.gen.stateprovider.PlainsFlowerStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedStateProvider;

public class DefaultBiomeFeatures {
	private static final BlockState GRASS = Blocks.field_10479.getDefaultState();
	private static final BlockState FERN = Blocks.field_10112.getDefaultState();
	private static final BlockState PODZOL = Blocks.field_10520.getDefaultState();
	private static final BlockState OAK_LOG = Blocks.field_10431.getDefaultState();
	private static final BlockState OAK_LEAVES = Blocks.field_10503.getDefaultState();
	private static final BlockState JUNGLE_LOG = Blocks.field_10306.getDefaultState();
	private static final BlockState JUNGLE_LEAVES = Blocks.field_10335.getDefaultState();
	private static final BlockState SPRUCE_LOG = Blocks.field_10037.getDefaultState();
	private static final BlockState SPRUCE_LEAVES = Blocks.field_9988.getDefaultState();
	private static final BlockState ACACIA_LOG = Blocks.field_10533.getDefaultState();
	private static final BlockState ACACIA_LEAVES = Blocks.field_10098.getDefaultState();
	private static final BlockState BIRCH_LOG = Blocks.field_10511.getDefaultState();
	private static final BlockState BIRCH_LEAVES = Blocks.field_10539.getDefaultState();
	private static final BlockState DARK_OAK_LOG = Blocks.field_10010.getDefaultState();
	private static final BlockState DARK_OAK_LEAVES = Blocks.field_10035.getDefaultState();
	private static final BlockState WATER = Blocks.field_10382.getDefaultState();
	private static final BlockState LAVA = Blocks.field_10164.getDefaultState();
	private static final BlockState DIRT = Blocks.field_10566.getDefaultState();
	private static final BlockState GRAVEL = Blocks.field_10255.getDefaultState();
	private static final BlockState GRANITE = Blocks.field_10474.getDefaultState();
	private static final BlockState DIORITE = Blocks.field_10508.getDefaultState();
	private static final BlockState ANDESITE = Blocks.field_10115.getDefaultState();
	private static final BlockState COAL_ORE = Blocks.field_10418.getDefaultState();
	private static final BlockState IRON_ORE = Blocks.field_10212.getDefaultState();
	private static final BlockState GOLD_ORE = Blocks.field_10571.getDefaultState();
	private static final BlockState REDSTONE_ORE = Blocks.field_10080.getDefaultState();
	private static final BlockState DIAMOND_ORE = Blocks.field_10442.getDefaultState();
	private static final BlockState LAPIS_ORE = Blocks.field_10090.getDefaultState();
	private static final BlockState STONE = Blocks.field_10340.getDefaultState();
	private static final BlockState EMERALD_ORE = Blocks.field_10013.getDefaultState();
	private static final BlockState INFESTED_STONE = Blocks.field_10277.getDefaultState();
	private static final BlockState SAND = Blocks.field_10102.getDefaultState();
	private static final BlockState CLAY = Blocks.field_10460.getDefaultState();
	private static final BlockState GRASS_BLOCK = Blocks.field_10219.getDefaultState();
	private static final BlockState MOSSY_COBBLESTONE = Blocks.field_9989.getDefaultState();
	private static final BlockState LARGE_FERN = Blocks.field_10313.getDefaultState();
	private static final BlockState TALL_GRASS = Blocks.field_10214.getDefaultState();
	private static final BlockState LILAC = Blocks.field_10378.getDefaultState();
	private static final BlockState ROSE_BUSH = Blocks.field_10430.getDefaultState();
	private static final BlockState PEONY = Blocks.field_10003.getDefaultState();
	private static final BlockState BROWN_MUSHROOM = Blocks.field_10251.getDefaultState();
	private static final BlockState RED_MUSHROOM = Blocks.field_10559.getDefaultState();
	private static final BlockState SEAGRASS = Blocks.field_10376.getDefaultState();
	private static final BlockState PACKED_ICE = Blocks.field_10225.getDefaultState();
	private static final BlockState BLUE_ICE = Blocks.field_10384.getDefaultState();
	private static final BlockState LILY_OF_THE_VALLEY = Blocks.field_10548.getDefaultState();
	private static final BlockState BLUE_ORCHID = Blocks.field_10086.getDefaultState();
	private static final BlockState POPPY = Blocks.field_10449.getDefaultState();
	private static final BlockState DANDELION = Blocks.field_10182.getDefaultState();
	private static final BlockState DEAD_BUSH = Blocks.field_10428.getDefaultState();
	private static final BlockState MELON = Blocks.field_10545.getDefaultState();
	private static final BlockState PUMPKIN = Blocks.field_10261.getDefaultState();
	private static final BlockState SWEET_BERRY_BUSH = Blocks.field_16999.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3));
	private static final BlockState FIRE = Blocks.field_10036.getDefaultState();
	private static final BlockState NETHERRACK = Blocks.field_10515.getDefaultState();
	private static final BlockState LILY_PAD = Blocks.field_10588.getDefaultState();
	private static final BlockState SNOW = Blocks.field_10477.getDefaultState();
	private static final BlockState JACK_O_LANTERN = Blocks.field_10009.getDefaultState();
	private static final BlockState SUNFLOWER = Blocks.field_10583.getDefaultState();
	private static final BlockState CACTUS = Blocks.field_10029.getDefaultState();
	private static final BlockState SUGAR_CANE = Blocks.field_10424.getDefaultState();
	private static final BlockState RED_MUSHROOM_BLOCK = Blocks.field_10240.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false));
	private static final BlockState BROWN_MUSHROOM_BLOCK = Blocks.field_10580
		.getDefaultState()
		.with(MushroomBlock.UP, Boolean.valueOf(true))
		.with(MushroomBlock.DOWN, Boolean.valueOf(false));
	private static final BlockState MUSHROOM_BLOCK = Blocks.field_10556
		.getDefaultState()
		.with(MushroomBlock.UP, Boolean.valueOf(false))
		.with(MushroomBlock.DOWN, Boolean.valueOf(false));
	public static final BranchedTreeFeatureConfig OAK_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig JUNGLE_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(8)
		.foliageHeight(3)
		.treeDecorators(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), new TrunkVineTreeDecorator(), new LeaveVineTreeDecorator()))
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig JUNGLE_SAPLING_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(8)
		.foliageHeight(3)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig PINE_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES), new PineFoliagePlacer(1, 0)
		)
		.baseHeight(7)
		.heightRandA(4)
		.trunkTopOffset(1)
		.foliageHeight(3)
		.foliageHeightRandom(1)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig SPRUCE_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES), new SpruceFoliagePlacer(2, 1)
		)
		.baseHeight(6)
		.heightRandA(3)
		.trunkHeight(1)
		.trunkHeightRandom(1)
		.trunkTopOffsetRandom(2)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig ACACIA_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(ACACIA_LOG), new SimpleStateProvider(ACACIA_LEAVES), new AcaciaFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.heightRandB(2)
		.trunkHeight(0)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig BIRCH_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.build();
	public static final BranchedTreeFeatureConfig field_21833 = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.002F)))
		.build();
	public static final BranchedTreeFeatureConfig LARGE_BIRCH_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.heightRandB(6)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.002F)))
		.build();
	public static final BranchedTreeFeatureConfig SWAMP_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(3, 0)
		)
		.baseHeight(5)
		.heightRandA(3)
		.foliageHeight(3)
		.maxWaterDepth(1)
		.treeDecorators(ImmutableList.of(new LeaveVineTreeDecorator()))
		.build();
	public static final BranchedTreeFeatureConfig FANCY_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0)
		)
		.build();
	public static final BranchedTreeFeatureConfig OAK_TREE_WITH_MORE_BEEHIVES_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
		.build();
	public static final BranchedTreeFeatureConfig field_21834 = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0)
		)
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.002F)))
		.build();
	public static final BranchedTreeFeatureConfig FANCY_TREE_WITH_MORE_BEEHIVES_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0)
		)
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
		.build();
	public static final BranchedTreeFeatureConfig field_21835 = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.002F)))
		.build();
	public static final BranchedTreeFeatureConfig OAK_TREE_WITH_BEEHIVES_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(4)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.02F)))
		.build();
	public static final BranchedTreeFeatureConfig FANCY_TREE_WITH_BEEHIVES_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0)
		)
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.02F)))
		.build();
	public static final BranchedTreeFeatureConfig BIRCH_TREE_WITH_BEEHIVES_CONFIG = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.02F)))
		.build();
	public static final BranchedTreeFeatureConfig field_21836 = new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0)
		)
		.baseHeight(5)
		.heightRandA(2)
		.foliageHeight(3)
		.noVines()
		.treeDecorators(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
		.build();
	public static final TreeFeatureConfig JUNGLE_GROUND_BUSH_CONFIG = new TreeFeatureConfig.Builder(
			new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(OAK_LEAVES)
		)
		.baseHeight(4)
		.build();
	public static final MegaTreeFeatureConfig DARK_OAK_TREE_CONFIG = new MegaTreeFeatureConfig.Builder(
			new SimpleStateProvider(DARK_OAK_LOG), new SimpleStateProvider(DARK_OAK_LEAVES)
		)
		.baseHeight(6)
		.build();
	public static final MegaTreeFeatureConfig MEGA_SPRUCE_TREE_CONFIG = new MegaTreeFeatureConfig.Builder(
			new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES)
		)
		.baseHeight(13)
		.heightInterval(15)
		.crownHeight(13)
		.treeDecorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleStateProvider(PODZOL))))
		.build();
	public static final MegaTreeFeatureConfig MEGA_PINE_TREE_CONFIG = new MegaTreeFeatureConfig.Builder(
			new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES)
		)
		.baseHeight(13)
		.heightInterval(15)
		.crownHeight(3)
		.treeDecorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleStateProvider(PODZOL))))
		.build();
	public static final MegaTreeFeatureConfig MEGA_JUNGLE_TREE_CONFIG = new MegaTreeFeatureConfig.Builder(
			new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES)
		)
		.baseHeight(10)
		.heightInterval(20)
		.treeDecorators(ImmutableList.of(new TrunkVineTreeDecorator(), new LeaveVineTreeDecorator()))
		.build();
	public static final RandomPatchFeatureConfig GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(GRASS), new SimpleBlockPlacer())
		.tries(32)
		.build();
	public static final RandomPatchFeatureConfig TAIGA_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
			new WeightedStateProvider().addState(GRASS, 1).addState(FERN, 4), new SimpleBlockPlacer()
		)
		.tries(32)
		.build();
	public static final RandomPatchFeatureConfig LUSH_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
			new WeightedStateProvider().addState(GRASS, 3).addState(FERN, 1), new SimpleBlockPlacer()
		)
		.blacklist(ImmutableSet.of(PODZOL))
		.tries(32)
		.build();
	public static final RandomPatchFeatureConfig LILY_OF_THE_VALLEY_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(LILY_OF_THE_VALLEY), new SimpleBlockPlacer()
		)
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig BLUE_ORCHID_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(BLUE_ORCHID), new SimpleBlockPlacer()
		)
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig DEFAULT_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(
			new WeightedStateProvider().addState(POPPY, 2).addState(DANDELION, 1), new SimpleBlockPlacer()
		)
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig PLAINS_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(
			new PlainsFlowerStateProvider(), new SimpleBlockPlacer()
		)
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig FOREST_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(
			new ForestFlowerStateProvider(), new SimpleBlockPlacer()
		)
		.tries(64)
		.build();
	public static final RandomPatchFeatureConfig DEAD_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(DEAD_BUSH), new SimpleBlockPlacer()
		)
		.tries(4)
		.build();
	public static final RandomPatchFeatureConfig MELON_PATCH_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(MELON), new SimpleBlockPlacer())
		.tries(64)
		.whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock()))
		.canReplace()
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig PUMPKIN_PATCH_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(PUMPKIN), new SimpleBlockPlacer()
		)
		.tries(64)
		.whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock()))
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig SWEET_BERRY_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(SWEET_BERRY_BUSH), new SimpleBlockPlacer()
		)
		.tries(64)
		.whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock()))
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig NETHER_FIRE_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(FIRE), new SimpleBlockPlacer())
		.tries(64)
		.whitelist(ImmutableSet.of(NETHERRACK.getBlock()))
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig LILY_PAD_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(LILY_PAD), new SimpleBlockPlacer())
		.tries(10)
		.build();
	public static final RandomPatchFeatureConfig RED_MUSHROOM_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(RED_MUSHROOM), new SimpleBlockPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig BROWN_MUSHROOM_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(BROWN_MUSHROOM), new SimpleBlockPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig LILAC_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(LILAC), new DoublePlantPlacer())
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig ROSE_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(ROSE_BUSH), new DoublePlantPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig PEONY_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(PEONY), new DoublePlantPlacer())
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig SUNFLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(SUNFLOWER), new DoublePlantPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig TALL_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(TALL_GRASS), new DoublePlantPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig LARGE_FERN_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(LARGE_FERN), new DoublePlantPlacer()
		)
		.tries(64)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig CACTUS_CONFIG = new RandomPatchFeatureConfig.Builder(new SimpleStateProvider(CACTUS), new ColumnPlacer(1, 2))
		.tries(10)
		.cannotProject()
		.build();
	public static final RandomPatchFeatureConfig SUGAR_CANE_CONFIG = new RandomPatchFeatureConfig.Builder(
			new SimpleStateProvider(SUGAR_CANE), new ColumnPlacer(2, 2)
		)
		.tries(20)
		.spreadX(4)
		.spreadY(0)
		.spreadZ(4)
		.cannotProject()
		.needsWater()
		.build();
	public static final BlockPileFeatureConfig HAY_PILE_CONFIG = new BlockPileFeatureConfig(new BlockStateProvider(Blocks.field_10359));
	public static final BlockPileFeatureConfig SNOW_PILE_CONFIG = new BlockPileFeatureConfig(new SimpleStateProvider(SNOW));
	public static final BlockPileFeatureConfig MELON_PILE_CONFIG = new BlockPileFeatureConfig(new SimpleStateProvider(MELON));
	public static final BlockPileFeatureConfig PUMPKIN_PILE_CONFIG = new BlockPileFeatureConfig(
		new WeightedStateProvider().addState(PUMPKIN, 19).addState(JACK_O_LANTERN, 1)
	);
	public static final BlockPileFeatureConfig BLUE_ICE_PILE_CONFIG = new BlockPileFeatureConfig(
		new WeightedStateProvider().addState(BLUE_ICE, 1).addState(PACKED_ICE, 5)
	);
	public static final SpringFeatureConfig WATER_SPRING_CONFIG = new SpringFeatureConfig(
		Fluids.WATER.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.field_10340, Blocks.field_10474, Blocks.field_10508, Blocks.field_10115)
	);
	public static final SpringFeatureConfig LAVA_SPRING_CONFIG = new SpringFeatureConfig(
		Fluids.LAVA.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.field_10340, Blocks.field_10474, Blocks.field_10508, Blocks.field_10115)
	);
	public static final SpringFeatureConfig NETHER_SPRING_CONFIG = new SpringFeatureConfig(
		Fluids.LAVA.getDefaultState(), false, 4, 1, ImmutableSet.of(Blocks.field_10515)
	);
	public static final SpringFeatureConfig ENCLOSED_NETHER_SPRING_CONFIG = new SpringFeatureConfig(
		Fluids.LAVA.getDefaultState(), false, 5, 0, ImmutableSet.of(Blocks.field_10515)
	);
	public static final HugeMushroomFeatureConfig HUGE_RED_MUSHROOM_CONFIG = new HugeMushroomFeatureConfig(
		new SimpleStateProvider(RED_MUSHROOM_BLOCK), new SimpleStateProvider(MUSHROOM_BLOCK), 2
	);
	public static final HugeMushroomFeatureConfig HUGE_BROWN_MUSHROOM_CONFIG = new HugeMushroomFeatureConfig(
		new SimpleStateProvider(BROWN_MUSHROOM_BLOCK), new SimpleStateProvider(MUSHROOM_BLOCK), 3
	);

	public static void addLandCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.field_13169, Biome.configureCarver(Carver.field_13304, new ProbabilityConfig(0.14285715F)));
		biome.addCarver(GenerationStep.Carver.field_13169, Biome.configureCarver(Carver.field_13295, new ProbabilityConfig(0.02F)));
	}

	public static void addOceanCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.field_13169, Biome.configureCarver(Carver.field_13304, new ProbabilityConfig(0.06666667F)));
		biome.addCarver(GenerationStep.Carver.field_13169, Biome.configureCarver(Carver.field_13295, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.field_13166, Biome.configureCarver(Carver.field_13303, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.field_13166, Biome.configureCarver(Carver.field_13300, new ProbabilityConfig(0.06666667F)));
	}

	public static void addDefaultStructures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13172,
			Feature.MINESHAFT
				.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.field_13692))
				.createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.PILLAGER_OUTPOST.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13172,
			Feature.STRONGHOLD.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.SWAMP_HUT.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.DESERT_PYRAMID.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.JUNGLE_TEMPLE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.IGLOO.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false)).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.OCEAN_MONUMENT.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.WOODLAND_MANSION.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.OCEAN_RUIN
				.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.field_14528, 0.3F, 0.9F))
				.createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13172,
			Feature.BURIED_TREASURE.configure(new BuriedTreasureFeatureConfig(0.01F)).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.VILLAGE
				.configure(new VillageFeatureConfig("village/plains/town_centers", 6))
				.createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addDefaultLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13171,
			Feature.field_13573.configure(new SingleStateFeatureConfig(WATER)).createDecoratedFeature(Decorator.field_14242.configure(new ChanceDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13171,
			Feature.field_13573.configure(new SingleStateFeatureConfig(LAVA)).createDecoratedFeature(Decorator.field_14237.configure(new ChanceDecoratorConfig(80)))
		);
	}

	public static void addDesertLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13171,
			Feature.field_13573.configure(new SingleStateFeatureConfig(LAVA)).createDecoratedFeature(Decorator.field_14237.configure(new ChanceDecoratorConfig(80)))
		);
	}

	public static void addDungeons(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13172,
			Feature.field_13579.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14265.configure(new ChanceDecoratorConfig(8)))
		);
	}

	public static void addMineables(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, DIRT, 33))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(10, 0, 0, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, GRAVEL, 33))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(8, 0, 0, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, GRANITE, 33))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, DIORITE, 33))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, ANDESITE, 33))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(10, 0, 0, 80)))
		);
	}

	public static void addDefaultOres(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, COAL_ORE, 17))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(20, 0, 0, 128)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, IRON_ORE, 9))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(20, 0, 0, 64)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, GOLD_ORE, 9))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(2, 0, 0, 32)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, REDSTONE_ORE, 8))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(8, 0, 0, 16)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, DIAMOND_ORE, 8))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(1, 0, 0, 16)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, LAPIS_ORE, 7))
				.createDecoratedFeature(Decorator.field_14252.configure(new CountDepthDecoratorConfig(1, 16, 16)))
		);
	}

	public static void addExtraGoldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, GOLD_ORE, 9))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(20, 32, 32, 80)))
		);
	}

	public static void addEmeraldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13594
				.configure(new EmeraldOreFeatureConfig(STONE, EMERALD_ORE))
				.createDecoratedFeature(Decorator.field_14268.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addInfestedStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13177,
			Feature.field_13517
				.configure(new OreFeatureConfig(OreFeatureConfig.Target.field_13730, INFESTED_STONE, 9))
				.createDecoratedFeature(Decorator.field_14241.configure(new RangeDecoratorConfig(7, 0, 0, 64)))
		);
	}

	public static void addDefaultDisks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13509
				.configure(new DiskFeatureConfig(SAND, 7, 2, Lists.newArrayList(new BlockState[]{DIRT, GRASS_BLOCK})))
				.createDecoratedFeature(Decorator.field_14245.configure(new CountDecoratorConfig(3)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13509
				.configure(new DiskFeatureConfig(CLAY, 4, 1, Lists.newArrayList(new BlockState[]{DIRT, CLAY})))
				.createDecoratedFeature(Decorator.field_14245.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13509
				.configure(new DiskFeatureConfig(GRAVEL, 6, 2, Lists.newArrayList(new BlockState[]{DIRT, GRASS_BLOCK})))
				.createDecoratedFeature(Decorator.field_14245.configure(new CountDecoratorConfig(1)))
		);
	}

	public static void addClay(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13176,
			Feature.field_13509
				.configure(new DiskFeatureConfig(CLAY, 4, 1, Lists.newArrayList(new BlockState[]{DIRT, CLAY})))
				.createDecoratedFeature(Decorator.field_14245.configure(new CountDecoratorConfig(1)))
		);
	}

	public static void addMossyRocks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13171,
			Feature.field_13584
				.configure(new BoulderFeatureConfig(MOSSY_COBBLESTONE, 0))
				.createDecoratedFeature(Decorator.field_14264.configure(new CountDecoratorConfig(3)))
		);
	}

	public static void addLargeFerns(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(LARGE_FERN_CONFIG).createDecoratedFeature(Decorator.field_14253.configure(new CountDecoratorConfig(7)))
		);
	}

	public static void addSweetBerryBushesSnowy(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(SWEET_BERRY_BUSH_CONFIG).createDecoratedFeature(Decorator.field_14263.configure(new ChanceDecoratorConfig(12)))
		);
	}

	public static void addSweetBerryBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(SWEET_BERRY_BUSH_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(1)))
		);
	}

	public static void addBamboo(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13540.configure(new ProbabilityConfig(0.0F)).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(16)))
		);
	}

	public static void addBambooJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13540
				.configure(new ProbabilityConfig(0.2F))
				.createDecoratedFeature(Decorator.field_14247.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3, Heightmap.Type.field_13194)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.field_13529.configure(FANCY_TREE_CONFIG).withChance(0.05F),
							Feature.field_13537.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.15F),
							Feature.field_13558.configure(MEGA_JUNGLE_TREE_CONFIG).withChance(0.7F)
						),
						Feature.field_21220.configure(LUSH_GRASS_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(30, 0.1F, 1)))
		);
	}

	public static void addTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_13510.configure(PINE_TREE_CONFIG).withChance(0.33333334F)), Feature.field_13510.configure(SPRUCE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addWaterBiomeOakTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_13529.configure(FANCY_TREE_CONFIG).withChance(0.1F)), Feature.field_13510.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13510.configure(field_21833).createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addForestTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_13510.configure(field_21833).withChance(0.2F), Feature.field_13529.configure(field_21834).withChance(0.1F)),
						Feature.field_13510.configure(field_21835)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addTallBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_13510.configure(LARGE_BIRCH_TREE_CONFIG).withChance(0.5F)), Feature.field_13510.configure(field_21833)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_21218.configure(ACACIA_TREE_CONFIG).withChance(0.8F)), Feature.field_13510.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(1, 0.1F, 1)))
		);
	}

	public static void addExtraSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_21218.configure(ACACIA_TREE_CONFIG).withChance(0.8F)), Feature.field_13510.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
	}

	public static void addMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_13510.configure(SPRUCE_TREE_CONFIG).withChance(0.666F), Feature.field_13529.configure(FANCY_TREE_CONFIG).withChance(0.1F)),
						Feature.field_13510.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addExtraMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_13510.configure(SPRUCE_TREE_CONFIG).withChance(0.666F), Feature.field_13529.configure(FANCY_TREE_CONFIG).withChance(0.1F)),
						Feature.field_13510.configure(OAK_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(3, 0.1F, 1)))
		);
	}

	public static void addJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.field_13529.configure(FANCY_TREE_CONFIG).withChance(0.1F),
							Feature.field_13537.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.5F),
							Feature.field_13558.configure(MEGA_JUNGLE_TREE_CONFIG).withChance(0.33333334F)
						),
						Feature.field_13510.configure(JUNGLE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(50, 0.1F, 1)))
		);
	}

	public static void addJungleEdgeTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.field_13529.configure(FANCY_TREE_CONFIG).withChance(0.1F), Feature.field_13537.configure(JUNGLE_GROUND_BUSH_CONFIG).withChance(0.5F)
						),
						Feature.field_13510.configure(JUNGLE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
	}

	public static void addBadlandsPlateauTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13510.configure(OAK_TREE_CONFIG).createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(5, 0.1F, 1)))
		);
	}

	public static void addSnowySpruceTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13510.configure(SPRUCE_TREE_CONFIG).createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
		);
	}

	public static void addGiantSpruceTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.field_13580.configure(MEGA_SPRUCE_TREE_CONFIG).withChance(0.33333334F), Feature.field_13510.configure(PINE_TREE_CONFIG).withChance(0.33333334F)
						),
						Feature.field_13510.configure(SPRUCE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addGiantTreeTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.field_13580.configure(MEGA_SPRUCE_TREE_CONFIG).withChance(0.025641026F),
							Feature.field_13580.configure(MEGA_PINE_TREE_CONFIG).withChance(0.30769232F),
							Feature.field_13510.configure(PINE_TREE_CONFIG).withChance(0.33333334F)
						),
						Feature.field_13510.configure(SPRUCE_TREE_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
		);
	}

	public static void addJungleGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(LUSH_GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(25)))
		);
	}

	public static void addSavannaTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(TALL_GRASS_CONFIG).createDecoratedFeature(Decorator.field_14253.configure(new CountDecoratorConfig(7)))
		);
	}

	public static void addShatteredSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(5)))
		);
	}

	public static void addSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(20)))
		);
	}

	public static void addBadlandsGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(DEAD_BUSH_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(20)))
		);
	}

	public static void addForestFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13512
				.configure(
					new RandomRandomFeatureConfig(
						ImmutableList.of(
							Feature.field_21220.configure(LILAC_CONFIG),
							Feature.field_21220.configure(ROSE_BUSH_CONFIG),
							Feature.field_21220.configure(PEONY_CONFIG),
							Feature.FLOWER.configure(LILY_OF_THE_VALLEY_CONFIG)
						),
						0
					)
				)
				.createDecoratedFeature(Decorator.field_14253.configure(new CountDecoratorConfig(5)))
		);
	}

	public static void addForestGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(2)))
		);
	}

	public static void addSwampFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13510.configure(SWAMP_TREE_CONFIG).createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.FLOWER.configure(BLUE_ORCHID_CONFIG).createDecoratedFeature(Decorator.field_14253.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(5)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(DEAD_BUSH_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(LILY_PAD_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14234.configure(new CountChanceDecoratorConfig(8, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14261.configure(new CountChanceDecoratorConfig(8, 0.125F)))
		);
	}

	public static void addMushroomFieldsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13550
				.configure(
					new RandomBooleanFeatureConfig(Feature.field_13571.configure(HUGE_RED_MUSHROOM_CONFIG), Feature.field_13531.configure(HUGE_BROWN_MUSHROOM_CONFIG))
				)
				.createDecoratedFeature(Decorator.field_14238.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14234.configure(new CountChanceDecoratorConfig(1, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14261.configure(new CountChanceDecoratorConfig(1, 0.125F)))
		);
	}

	public static void addPlainsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13593
				.configure(
					new RandomFeatureConfig(
						ImmutableList.of(Feature.field_13529.configure(FANCY_TREE_WITH_MORE_BEEHIVES_CONFIG).withChance(0.33333334F)),
						Feature.field_13510.configure(OAK_TREE_WITH_MORE_BEEHIVES_CONFIG)
					)
				)
				.createDecoratedFeature(Decorator.field_14267.configure(new CountExtraChanceDecoratorConfig(0, 0.05F, 1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.FLOWER.configure(PLAINS_FLOWER_CONFIG).createDecoratedFeature(Decorator.field_14254.configure(new NoiseHeightmapDecoratorConfig(-0.8, 15, 4)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.field_14236.configure(new NoiseHeightmapDecoratorConfig(-0.8, 5, 10)))
		);
	}

	public static void addDesertDeadBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(DEAD_BUSH_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(2)))
		);
	}

	public static void addGiantTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(TAIGA_GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(7)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(DEAD_BUSH_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14234.configure(new CountChanceDecoratorConfig(3, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14261.configure(new CountChanceDecoratorConfig(3, 0.125F)))
		);
	}

	public static void addDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.FLOWER.configure(DEFAULT_FLOWER_CONFIG).createDecoratedFeature(Decorator.field_14253.configure(new CountDecoratorConfig(2)))
		);
	}

	public static void addExtraDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.FLOWER.configure(DEFAULT_FLOWER_CONFIG).createDecoratedFeature(Decorator.field_14253.configure(new CountDecoratorConfig(4)))
		);
	}

	public static void addDefaultGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(1)))
		);
	}

	public static void addTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(TAIGA_GRASS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14234.configure(new CountChanceDecoratorConfig(1, 0.25F)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14261.configure(new CountChanceDecoratorConfig(1, 0.125F)))
		);
	}

	public static void addPlainsTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(TALL_GRASS_CONFIG).createDecoratedFeature(Decorator.field_14254.configure(new NoiseHeightmapDecoratorConfig(-0.8, 0, 7)))
		);
	}

	public static void addDefaultMushrooms(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(BROWN_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14263.configure(new ChanceDecoratorConfig(4)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(RED_MUSHROOM_CONFIG).createDecoratedFeature(Decorator.field_14263.configure(new ChanceDecoratorConfig(8)))
		);
	}

	public static void addDefaultVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(10)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(PUMPKIN_PATCH_CONFIG).createDecoratedFeature(Decorator.field_14263.configure(new ChanceDecoratorConfig(32)))
		);
	}

	public static void addBadlandsVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(13)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(PUMPKIN_PATCH_CONFIG).createDecoratedFeature(Decorator.field_14263.configure(new ChanceDecoratorConfig(32)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(CACTUS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(5)))
		);
	}

	public static void addJungleVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(MELON_PATCH_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(1)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13559.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14249.configure(new CountDecoratorConfig(50)))
		);
	}

	public static void addDesertVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(60)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(PUMPKIN_PATCH_CONFIG).createDecoratedFeature(Decorator.field_14263.configure(new ChanceDecoratorConfig(32)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(CACTUS_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(10)))
		);
	}

	public static void addSwampVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(SUGAR_CANE_CONFIG).createDecoratedFeature(Decorator.field_14240.configure(new CountDecoratorConfig(20)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_21220.configure(PUMPKIN_PATCH_CONFIG).createDecoratedFeature(Decorator.field_14263.configure(new ChanceDecoratorConfig(32)))
		);
	}

	public static void addDesertFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.field_13592.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14259.configure(new ChanceDecoratorConfig(1000)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13177,
			Feature.field_13516.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14246.configure(new ChanceDecoratorConfig(64)))
		);
	}

	public static void addFossils(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13177,
			Feature.field_13516.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14246.configure(new ChanceDecoratorConfig(64)))
		);
	}

	public static void addKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13535
				.configure(FeatureConfig.DEFAULT)
				.createDecoratedFeature(Decorator.field_14247.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0, Heightmap.Type.field_13195)))
		);
	}

	public static void addSeagrassOnStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13518
				.configure(new SimpleBlockFeatureConfig(SEAGRASS, new BlockState[]{STONE}, new BlockState[]{WATER}, new BlockState[]{WATER}))
				.createDecoratedFeature(Decorator.field_14229.configure(new CarvingMaskDecoratorConfig(GenerationStep.Carver.field_13166, 0.1F)))
		);
	}

	public static void addSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13567.configure(new SeagrassFeatureConfig(80, 0.3)).createDecoratedFeature(Decorator.field_14231.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addMoreSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13567.configure(new SeagrassFeatureConfig(80, 0.8)).createDecoratedFeature(Decorator.field_14231.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addLessKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13535
				.configure(FeatureConfig.DEFAULT)
				.createDecoratedFeature(Decorator.field_14247.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0, Heightmap.Type.field_13195)))
		);
	}

	public static void addSprings(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13513.configure(WATER_SPRING_CONFIG).createDecoratedFeature(Decorator.field_14255.configure(new RangeDecoratorConfig(50, 8, 8, 256)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13178,
			Feature.field_13513.configure(LAVA_SPRING_CONFIG).createDecoratedFeature(Decorator.field_14266.configure(new RangeDecoratorConfig(20, 8, 16, 256)))
		);
	}

	public static void addIcebergs(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13171,
			Feature.field_13544
				.configure(new SingleStateFeatureConfig(PACKED_ICE))
				.createDecoratedFeature(Decorator.field_14243.configure(new ChanceDecoratorConfig(16)))
		);
		biome.addFeature(
			GenerationStep.Feature.field_13171,
			Feature.field_13544
				.configure(new SingleStateFeatureConfig(BLUE_ICE))
				.createDecoratedFeature(Decorator.field_14243.configure(new ChanceDecoratorConfig(200)))
		);
	}

	public static void addBlueIce(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.field_13560.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14260.configure(new RangeDecoratorConfig(20, 30, 32, 64)))
		);
	}

	public static void addFrozenTopLayer(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13179,
			Feature.field_13539.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
	}

	public static void addEndCities(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.field_13173,
			Feature.END_CITY.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.field_14250.configure(DecoratorConfig.DEFAULT))
		);
	}
}
