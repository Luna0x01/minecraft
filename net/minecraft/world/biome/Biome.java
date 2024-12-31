package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.gen.feature.BigTreeFeature;
import net.minecraft.world.gen.feature.DoublePlantFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.JungleTreeFeature;
import net.minecraft.world.gen.feature.OakTreeFeature;
import net.minecraft.world.gen.feature.TallGrassFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
	private static final Logger LOGGER = LogManager.getLogger();
	protected static final BlockState stoneBlockState = Blocks.STONE.getDefaultState();
	protected static final BlockState airBlockState = Blocks.AIR.getDefaultState();
	protected static final BlockState bedrockBlockState = Blocks.BEDROCK.getDefaultState();
	protected static final BlockState gravelBlockState = Blocks.GRAVEL.getDefaultState();
	protected static final BlockState redSandstoneBlockState = Blocks.RED_SANDSTONE.getDefaultState();
	protected static final BlockState sandstoneBlockState = Blocks.SANDSTONE.getDefaultState();
	protected static final BlockState iceBlockState = Blocks.ICE.getDefaultState();
	protected static final BlockState waterBlockState = Blocks.WATER.getDefaultState();
	public static final IdList<Biome> biomeList = new IdList<>();
	protected static final PerlinNoiseGenerator TEMPERATURE_NOISE = new PerlinNoiseGenerator(new Random(1234L), 1);
	protected static final PerlinNoiseGenerator FOLIAGE_NOISE = new PerlinNoiseGenerator(new Random(2345L), 1);
	protected static final DoublePlantFeature DOUBLE_PLANT_FEATURE = new DoublePlantFeature();
	protected static final JungleTreeFeature JUNGLE_TREE_FEATURE = new JungleTreeFeature(false);
	protected static final BigTreeFeature field_4631 = new BigTreeFeature(false);
	protected static final OakTreeFeature OAK_TREE_FEATURE = new OakTreeFeature();
	public static final SimpleRegistry<Identifier, Biome> REGISTRY = new SimpleRegistry<>();
	private final String name;
	private final float depth;
	private final float variationModifier;
	private final float temperature;
	private final float downfall;
	private final int waterColor;
	private final boolean mutated;
	private final boolean field_4635;
	@Nullable
	private final String parent;
	public BlockState topBlock = Blocks.GRASS.getDefaultState();
	public BlockState baseBlock = Blocks.DIRT.getDefaultState();
	public BiomeDecorator biomeDecorator;
	protected List<Biome.SpawnEntry> monsterEntries = Lists.newArrayList();
	protected List<Biome.SpawnEntry> passiveEntries = Lists.newArrayList();
	protected List<Biome.SpawnEntry> waterEntries = Lists.newArrayList();
	protected List<Biome.SpawnEntry> flyingEntries = Lists.newArrayList();

	public static int getBiomeIndex(Biome biome) {
		return REGISTRY.getRawId(biome);
	}

	@Nullable
	public static Biome getBiomeFromIndex(int id) {
		return REGISTRY.getByRawId(id);
	}

	@Nullable
	public static Biome getBiomeFromList(Biome biome) {
		return biomeList.fromId(getBiomeIndex(biome));
	}

	protected Biome(Biome.Settings settings) {
		this.name = settings.name;
		this.depth = settings.baseHeightModifier;
		this.variationModifier = settings.variationModifier;
		this.temperature = settings.temperature;
		this.downfall = settings.downfall;
		this.waterColor = settings.waterColor;
		this.mutated = settings.hasParent;
		this.field_4635 = settings.field_12458;
		this.parent = settings.parent;
		this.biomeDecorator = this.createBiomePopulator();
		this.passiveEntries.add(new Biome.SpawnEntry(SheepEntity.class, 12, 4, 4));
		this.passiveEntries.add(new Biome.SpawnEntry(PigEntity.class, 10, 4, 4));
		this.passiveEntries.add(new Biome.SpawnEntry(ChickenEntity.class, 10, 4, 4));
		this.passiveEntries.add(new Biome.SpawnEntry(CowEntity.class, 8, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(SpiderEntity.class, 100, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(ZombieEntity.class, 95, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(ZombieVillagerEntity.class, 5, 1, 1));
		this.monsterEntries.add(new Biome.SpawnEntry(SkeletonEntity.class, 100, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(CreeperEntity.class, 100, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(SlimeEntity.class, 100, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(EndermanEntity.class, 10, 1, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(WitchEntity.class, 5, 1, 1));
		this.waterEntries.add(new Biome.SpawnEntry(SquidEntity.class, 10, 4, 4));
		this.flyingEntries.add(new Biome.SpawnEntry(BatEntity.class, 10, 8, 8));
	}

	protected BiomeDecorator createBiomePopulator() {
		return new BiomeDecorator();
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public FoliageFeature method_3822(Random random) {
		return (FoliageFeature)(random.nextInt(10) == 0 ? field_4631 : JUNGLE_TREE_FEATURE);
	}

	public Feature method_3828(Random random) {
		return new TallGrassFeature(TallPlantBlock.GrassType.GRASS);
	}

	public FlowerBlock.FlowerType pickFlower(Random random, BlockPos pos) {
		return random.nextInt(3) > 0 ? FlowerBlock.FlowerType.DANDELION : FlowerBlock.FlowerType.POPPY;
	}

	public int getSkyColor(float temperature) {
		temperature /= 3.0F;
		temperature = MathHelper.clamp(temperature, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - temperature * 0.05F, 0.5F + temperature * 0.1F, 1.0F);
	}

	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category) {
		switch (category) {
			case MONSTER:
				return this.monsterEntries;
			case PASSIVE:
				return this.passiveEntries;
			case AQUATIC:
				return this.waterEntries;
			case AMBIENT:
				return this.flyingEntries;
			default:
				return Collections.emptyList();
		}
	}

	public boolean isMutated() {
		return this.isMutatedBiome();
	}

	public boolean method_3830() {
		return this.isMutatedBiome() ? false : this.field_4635;
	}

	public boolean hasHighHumidity() {
		return this.getRainfall() > 0.85F;
	}

	public float getMaxSpawnLimit() {
		return 0.1F;
	}

	public final float getTemperature(BlockPos pos) {
		if (pos.getY() > 64) {
			float f = (float)(TEMPERATURE_NOISE.noise((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F)) * 4.0);
			return this.getTemperature() - (f + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return this.getTemperature();
		}
	}

	public void decorate(World world, Random random, BlockPos pos) {
		this.biomeDecorator.decorate(world, random, this, pos);
	}

	public int getGrassColor(BlockPos pos) {
		double d = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
		return GrassColors.getColor(d, e);
	}

	public int getFoliageColor(BlockPos pos) {
		double d = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
		double e = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
		return FoliageColors.getColor(d, e);
	}

	public void method_6420(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		this.method_8590(world, random, chunkStorage, i, j, d);
	}

	public final void method_8590(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		int k = world.getSeaLevel();
		BlockState blockState = this.topBlock;
		BlockState blockState2 = this.baseBlock;
		int l = -1;
		int m = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int n = i & 15;
		int o = j & 15;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int p = 255; p >= 0; p--) {
			if (p <= random.nextInt(5)) {
				chunkStorage.set(o, p, n, bedrockBlockState);
			} else {
				BlockState blockState3 = chunkStorage.get(o, p, n);
				if (blockState3.getMaterial() == Material.AIR) {
					l = -1;
				} else if (blockState3.getBlock() == Blocks.STONE) {
					if (l == -1) {
						if (m <= 0) {
							blockState = airBlockState;
							blockState2 = stoneBlockState;
						} else if (p >= k - 4 && p <= k + 1) {
							blockState = this.topBlock;
							blockState2 = this.baseBlock;
						}

						if (p < k && (blockState == null || blockState.getMaterial() == Material.AIR)) {
							if (this.getTemperature(mutable.setPosition(i, p, j)) < 0.15F) {
								blockState = iceBlockState;
							} else {
								blockState = waterBlockState;
							}
						}

						l = m;
						if (p >= k - 1) {
							chunkStorage.set(o, p, n, blockState);
						} else if (p < k - 7 - m) {
							blockState = airBlockState;
							blockState2 = stoneBlockState;
							chunkStorage.set(o, p, n, gravelBlockState);
						} else {
							chunkStorage.set(o, p, n, blockState2);
						}
					} else if (l > 0) {
						l--;
						chunkStorage.set(o, p, n, blockState2);
						if (l == 0 && blockState2.getBlock() == Blocks.SAND && m > 1) {
							l = random.nextInt(4) + Math.max(0, p - 63);
							blockState2 = blockState2.get(SandBlock.sandType) == SandBlock.SandType.RED_SAND ? redSandstoneBlockState : sandstoneBlockState;
						}
					}
				}
			}
		}
	}

	public Class<? extends Biome> asClass() {
		return this.getClass();
	}

	public Biome.Temperature getBiomeTemperature() {
		if ((double)this.getTemperature() < 0.2) {
			return Biome.Temperature.COLD;
		} else {
			return (double)this.getTemperature() < 1.0 ? Biome.Temperature.MEDIUM : Biome.Temperature.WARM;
		}
	}

	@Nullable
	public static Biome byId(int id) {
		return getByRawIdOrDefault(id, null);
	}

	public static Biome getByRawIdOrDefault(int id, Biome biome) {
		Biome biome2 = getBiomeFromIndex(id);
		return biome2 == null ? biome : biome2;
	}

	public boolean method_11504() {
		return false;
	}

	public final float getDepth() {
		return this.depth;
	}

	public final float getRainfall() {
		return this.downfall;
	}

	public final String getName() {
		return this.name;
	}

	public final float getVariationModifier() {
		return this.variationModifier;
	}

	public final float getTemperature() {
		return this.temperature;
	}

	public final int getWaterColor() {
		return this.waterColor;
	}

	public final boolean isMutatedBiome() {
		return this.mutated;
	}

	public static void register() {
		register(0, "ocean", new OceanBiome(new Biome.Settings("Ocean").setBaseHeightModifier(-1.0F).setVariationModifier(0.1F)));
		register(
			1,
			"plains",
			new PlainsBiome(false, new Biome.Settings("Plains").setBaseHeightModifier(0.125F).setVariationModifier(0.05F).setTemperature(0.8F).setDownfall(0.4F))
		);
		register(
			2,
			"desert",
			new DesertBiome(new Biome.Settings("Desert").setBaseHeightModifier(0.125F).setVariationModifier(0.05F).setTemperature(2.0F).setDownfall(0.0F).method_11511())
		);
		register(
			3,
			"extreme_hills",
			new ExtremeHillsBiome(
				ExtremeHillsBiome.Type.NORMAL,
				new Biome.Settings("Extreme Hills").setBaseHeightModifier(1.0F).setVariationModifier(0.5F).setTemperature(0.2F).setDownfall(0.3F)
			)
		);
		register(4, "forest", new ForestBiome(ForestBiome.Type.NORMAL, new Biome.Settings("Forest").setTemperature(0.7F).setDownfall(0.8F)));
		register(
			5,
			"taiga",
			new TaigaBiome(
				TaigaBiome.Type.NORMAL, new Biome.Settings("Taiga").setBaseHeightModifier(0.2F).setVariationModifier(0.2F).setTemperature(0.25F).setDownfall(0.8F)
			)
		);
		register(
			6,
			"swampland",
			new SwampBiome(
				new Biome.Settings("Swampland").setBaseHeightModifier(-0.2F).setVariationModifier(0.1F).setTemperature(0.8F).setDownfall(0.9F).setWaterColor(14745518)
			)
		);
		register(7, "river", new RiverBiome(new Biome.Settings("River").setBaseHeightModifier(-0.5F).setVariationModifier(0.0F)));
		register(8, "hell", new NetherBiome(new Biome.Settings("Hell").setTemperature(2.0F).setDownfall(0.0F).method_11511()));
		register(9, "sky", new EndBiome(new Biome.Settings("The End").method_11511()));
		register(
			10,
			"frozen_ocean",
			new OceanBiome(new Biome.Settings("FrozenOcean").setBaseHeightModifier(-1.0F).setVariationModifier(0.1F).setTemperature(0.0F).setDownfall(0.5F).hasParent())
		);
		register(
			11,
			"frozen_river",
			new RiverBiome(new Biome.Settings("FrozenRiver").setBaseHeightModifier(-0.5F).setVariationModifier(0.0F).setTemperature(0.0F).setDownfall(0.5F).hasParent())
		);
		register(
			12,
			"ice_flats",
			new IceBiome(
				false, new Biome.Settings("Ice Plains").setBaseHeightModifier(0.125F).setVariationModifier(0.05F).setTemperature(0.0F).setDownfall(0.5F).hasParent()
			)
		);
		register(
			13,
			"ice_mountains",
			new IceBiome(
				false, new Biome.Settings("Ice Mountains").setBaseHeightModifier(0.45F).setVariationModifier(0.3F).setTemperature(0.0F).setDownfall(0.5F).hasParent()
			)
		);
		register(
			14,
			"mushroom_island",
			new MushroomBiome(new Biome.Settings("MushroomIsland").setBaseHeightModifier(0.2F).setVariationModifier(0.3F).setTemperature(0.9F).setDownfall(1.0F))
		);
		register(
			15,
			"mushroom_island_shore",
			new MushroomBiome(new Biome.Settings("MushroomIslandShore").setBaseHeightModifier(0.0F).setVariationModifier(0.025F).setTemperature(0.9F).setDownfall(1.0F))
		);
		register(
			16, "beaches", new BeachBiome(new Biome.Settings("Beach").setBaseHeightModifier(0.0F).setVariationModifier(0.025F).setTemperature(0.8F).setDownfall(0.4F))
		);
		register(
			17,
			"desert_hills",
			new DesertBiome(
				new Biome.Settings("DesertHills").setBaseHeightModifier(0.45F).setVariationModifier(0.3F).setTemperature(2.0F).setDownfall(0.0F).method_11511()
			)
		);
		register(
			18,
			"forest_hills",
			new ForestBiome(
				ForestBiome.Type.NORMAL, new Biome.Settings("ForestHills").setBaseHeightModifier(0.45F).setVariationModifier(0.3F).setTemperature(0.7F).setDownfall(0.8F)
			)
		);
		register(
			19,
			"taiga_hills",
			new TaigaBiome(
				TaigaBiome.Type.NORMAL, new Biome.Settings("TaigaHills").setTemperature(0.25F).setDownfall(0.8F).setBaseHeightModifier(0.45F).setVariationModifier(0.3F)
			)
		);
		register(
			20,
			"smaller_extreme_hills",
			new ExtremeHillsBiome(
				ExtremeHillsBiome.Type.EXTRA_TREES,
				new Biome.Settings("Extreme Hills Edge").setBaseHeightModifier(0.8F).setVariationModifier(0.3F).setTemperature(0.2F).setDownfall(0.3F)
			)
		);
		register(21, "jungle", new JungleBiome(false, new Biome.Settings("Jungle").setTemperature(0.95F).setDownfall(0.9F)));
		register(
			22,
			"jungle_hills",
			new JungleBiome(false, new Biome.Settings("JungleHills").setBaseHeightModifier(0.45F).setVariationModifier(0.3F).setTemperature(0.95F).setDownfall(0.9F))
		);
		register(23, "jungle_edge", new JungleBiome(true, new Biome.Settings("JungleEdge").setTemperature(0.95F).setDownfall(0.8F)));
		register(24, "deep_ocean", new OceanBiome(new Biome.Settings("Deep Ocean").setBaseHeightModifier(-1.8F).setVariationModifier(0.1F)));
		register(
			25,
			"stone_beach",
			new StoneBeachBiome(new Biome.Settings("Stone Beach").setBaseHeightModifier(0.1F).setVariationModifier(0.8F).setTemperature(0.2F).setDownfall(0.3F))
		);
		register(
			26,
			"cold_beach",
			new BeachBiome(new Biome.Settings("Cold Beach").setBaseHeightModifier(0.0F).setVariationModifier(0.025F).setTemperature(0.05F).setDownfall(0.3F).hasParent())
		);
		register(27, "birch_forest", new ForestBiome(ForestBiome.Type.BIRCH, new Biome.Settings("Birch Forest").setTemperature(0.6F).setDownfall(0.6F)));
		register(
			28,
			"birch_forest_hills",
			new ForestBiome(
				ForestBiome.Type.BIRCH,
				new Biome.Settings("Birch Forest Hills").setBaseHeightModifier(0.45F).setVariationModifier(0.3F).setTemperature(0.6F).setDownfall(0.6F)
			)
		);
		register(29, "roofed_forest", new ForestBiome(ForestBiome.Type.ROOFED, new Biome.Settings("Roofed Forest").setTemperature(0.7F).setDownfall(0.8F)));
		register(
			30,
			"taiga_cold",
			new TaigaBiome(
				TaigaBiome.Type.NORMAL,
				new Biome.Settings("Cold Taiga").setBaseHeightModifier(0.2F).setVariationModifier(0.2F).setTemperature(-0.5F).setDownfall(0.4F).hasParent()
			)
		);
		register(
			31,
			"taiga_cold_hills",
			new TaigaBiome(
				TaigaBiome.Type.NORMAL,
				new Biome.Settings("Cold Taiga Hills").setBaseHeightModifier(0.45F).setVariationModifier(0.3F).setTemperature(-0.5F).setDownfall(0.4F).hasParent()
			)
		);
		register(
			32,
			"redwood_taiga",
			new TaigaBiome(
				TaigaBiome.Type.MEGA, new Biome.Settings("Mega Taiga").setTemperature(0.3F).setDownfall(0.8F).setBaseHeightModifier(0.2F).setVariationModifier(0.2F)
			)
		);
		register(
			33,
			"redwood_taiga_hills",
			new TaigaBiome(
				TaigaBiome.Type.MEGA, new Biome.Settings("Mega Taiga Hills").setBaseHeightModifier(0.45F).setVariationModifier(0.3F).setTemperature(0.3F).setDownfall(0.8F)
			)
		);
		register(
			34,
			"extreme_hills_with_trees",
			new ExtremeHillsBiome(
				ExtremeHillsBiome.Type.EXTRA_TREES,
				new Biome.Settings("Extreme Hills+").setBaseHeightModifier(1.0F).setVariationModifier(0.5F).setTemperature(0.2F).setDownfall(0.3F)
			)
		);
		register(
			35,
			"savanna",
			new SavannaBiome(
				new Biome.Settings("Savanna").setBaseHeightModifier(0.125F).setVariationModifier(0.05F).setTemperature(1.2F).setDownfall(0.0F).method_11511()
			)
		);
		register(
			36,
			"savanna_rock",
			new SavannaBiome(
				new Biome.Settings("Savanna Plateau").setBaseHeightModifier(1.5F).setVariationModifier(0.025F).setTemperature(1.0F).setDownfall(0.0F).method_11511()
			)
		);
		register(37, "mesa", new MesaBiome(false, false, new Biome.Settings("Mesa").setTemperature(2.0F).setDownfall(0.0F).method_11511()));
		register(
			38,
			"mesa_rock",
			new MesaBiome(
				false,
				true,
				new Biome.Settings("Mesa Plateau F").setBaseHeightModifier(1.5F).setVariationModifier(0.025F).setTemperature(2.0F).setDownfall(0.0F).method_11511()
			)
		);
		register(
			39,
			"mesa_clear_rock",
			new MesaBiome(
				false,
				false,
				new Biome.Settings("Mesa Plateau").setBaseHeightModifier(1.5F).setVariationModifier(0.025F).setTemperature(2.0F).setDownfall(0.0F).method_11511()
			)
		);
		register(127, "void", new VoidBiome(new Biome.Settings("The Void").method_11511()));
		register(
			129,
			"mutated_plains",
			new PlainsBiome(
				true,
				new Biome.Settings("Sunflower Plains").setParent("plains").setBaseHeightModifier(0.125F).setVariationModifier(0.05F).setTemperature(0.8F).setDownfall(0.4F)
			)
		);
		register(
			130,
			"mutated_desert",
			new DesertBiome(
				new Biome.Settings("Desert M")
					.setParent("desert")
					.setBaseHeightModifier(0.225F)
					.setVariationModifier(0.25F)
					.setTemperature(2.0F)
					.setDownfall(0.0F)
					.method_11511()
			)
		);
		register(
			131,
			"mutated_extreme_hills",
			new ExtremeHillsBiome(
				ExtremeHillsBiome.Type.MUTATED,
				new Biome.Settings("Extreme Hills M")
					.setParent("extreme_hills")
					.setBaseHeightModifier(1.0F)
					.setVariationModifier(0.5F)
					.setTemperature(0.2F)
					.setDownfall(0.3F)
			)
		);
		register(
			132,
			"mutated_forest",
			new ForestBiome(
				ForestBiome.Type.FLOWER, new Biome.Settings("Flower Forest").setParent("forest").setVariationModifier(0.4F).setTemperature(0.7F).setDownfall(0.8F)
			)
		);
		register(
			133,
			"mutated_taiga",
			new TaigaBiome(
				TaigaBiome.Type.NORMAL,
				new Biome.Settings("Taiga M").setParent("taiga").setBaseHeightModifier(0.3F).setVariationModifier(0.4F).setTemperature(0.25F).setDownfall(0.8F)
			)
		);
		register(
			134,
			"mutated_swampland",
			new SwampBiome(
				new Biome.Settings("Swampland M")
					.setParent("swampland")
					.setBaseHeightModifier(-0.1F)
					.setVariationModifier(0.3F)
					.setTemperature(0.8F)
					.setDownfall(0.9F)
					.setWaterColor(14745518)
			)
		);
		register(
			140,
			"mutated_ice_flats",
			new IceBiome(
				true,
				new Biome.Settings("Ice Plains Spikes")
					.setParent("ice_flats")
					.setBaseHeightModifier(0.425F)
					.setVariationModifier(0.45000002F)
					.setTemperature(0.0F)
					.setDownfall(0.5F)
					.hasParent()
			)
		);
		register(
			149,
			"mutated_jungle",
			new JungleBiome(
				false, new Biome.Settings("Jungle M").setParent("jungle").setBaseHeightModifier(0.2F).setVariationModifier(0.4F).setTemperature(0.95F).setDownfall(0.9F)
			)
		);
		register(
			151,
			"mutated_jungle_edge",
			new JungleBiome(
				true,
				new Biome.Settings("JungleEdge M").setParent("jungle_edge").setBaseHeightModifier(0.2F).setVariationModifier(0.4F).setTemperature(0.95F).setDownfall(0.8F)
			)
		);
		register(
			155,
			"mutated_birch_forest",
			new BirchForestBiome(
				new Biome.Settings("Birch Forest M")
					.setParent("birch_forest")
					.setBaseHeightModifier(0.2F)
					.setVariationModifier(0.4F)
					.setTemperature(0.6F)
					.setDownfall(0.6F)
			)
		);
		register(
			156,
			"mutated_birch_forest_hills",
			new BirchForestBiome(
				new Biome.Settings("Birch Forest Hills M")
					.setParent("birch_forest_hills")
					.setBaseHeightModifier(0.55F)
					.setVariationModifier(0.5F)
					.setTemperature(0.6F)
					.setDownfall(0.6F)
			)
		);
		register(
			157,
			"mutated_roofed_forest",
			new ForestBiome(
				ForestBiome.Type.ROOFED,
				new Biome.Settings("Roofed Forest M")
					.setParent("roofed_forest")
					.setBaseHeightModifier(0.2F)
					.setVariationModifier(0.4F)
					.setTemperature(0.7F)
					.setDownfall(0.8F)
			)
		);
		register(
			158,
			"mutated_taiga_cold",
			new TaigaBiome(
				TaigaBiome.Type.NORMAL,
				new Biome.Settings("Cold Taiga M")
					.setParent("taiga_cold")
					.setBaseHeightModifier(0.3F)
					.setVariationModifier(0.4F)
					.setTemperature(-0.5F)
					.setDownfall(0.4F)
					.hasParent()
			)
		);
		register(
			160,
			"mutated_redwood_taiga",
			new TaigaBiome(
				TaigaBiome.Type.MEGA_SPRUCE,
				new Biome.Settings("Mega Spruce Taiga")
					.setParent("redwood_taiga")
					.setBaseHeightModifier(0.2F)
					.setVariationModifier(0.2F)
					.setTemperature(0.25F)
					.setDownfall(0.8F)
			)
		);
		register(
			161,
			"mutated_redwood_taiga_hills",
			new TaigaBiome(
				TaigaBiome.Type.MEGA_SPRUCE,
				new Biome.Settings("Redwood Taiga Hills M")
					.setParent("redwood_taiga_hills")
					.setBaseHeightModifier(0.2F)
					.setVariationModifier(0.2F)
					.setTemperature(0.25F)
					.setDownfall(0.8F)
			)
		);
		register(
			162,
			"mutated_extreme_hills_with_trees",
			new ExtremeHillsBiome(
				ExtremeHillsBiome.Type.MUTATED,
				new Biome.Settings("Extreme Hills+ M")
					.setParent("extreme_hills_with_trees")
					.setBaseHeightModifier(1.0F)
					.setVariationModifier(0.5F)
					.setTemperature(0.2F)
					.setDownfall(0.3F)
			)
		);
		register(
			163,
			"mutated_savanna",
			new ShatteredSavannaBiome(
				new Biome.Settings("Savanna M")
					.setParent("savanna")
					.setBaseHeightModifier(0.3625F)
					.setVariationModifier(1.225F)
					.setTemperature(1.1F)
					.setDownfall(0.0F)
					.method_11511()
			)
		);
		register(
			164,
			"mutated_savanna_rock",
			new ShatteredSavannaBiome(
				new Biome.Settings("Savanna Plateau M")
					.setParent("savanna_rock")
					.setBaseHeightModifier(1.05F)
					.setVariationModifier(1.2125001F)
					.setTemperature(1.0F)
					.setDownfall(0.0F)
					.method_11511()
			)
		);
		register(
			165, "mutated_mesa", new MesaBiome(true, false, new Biome.Settings("Mesa (Bryce)").setParent("mesa").setTemperature(2.0F).setDownfall(0.0F).method_11511())
		);
		register(
			166,
			"mutated_mesa_rock",
			new MesaBiome(
				false,
				true,
				new Biome.Settings("Mesa Plateau F M")
					.setParent("mesa_rock")
					.setBaseHeightModifier(0.45F)
					.setVariationModifier(0.3F)
					.setTemperature(2.0F)
					.setDownfall(0.0F)
					.method_11511()
			)
		);
		register(
			167,
			"mutated_mesa_clear_rock",
			new MesaBiome(
				false,
				false,
				new Biome.Settings("Mesa Plateau M")
					.setParent("mesa_clear_rock")
					.setBaseHeightModifier(0.45F)
					.setVariationModifier(0.3F)
					.setTemperature(2.0F)
					.setDownfall(0.0F)
					.method_11511()
			)
		);
	}

	private static void register(int numId, String stringId, Biome biome) {
		REGISTRY.add(numId, new Identifier(stringId), biome);
		if (biome.hasParent()) {
			biomeList.set(biome, getBiomeIndex(REGISTRY.get(new Identifier(biome.parent))));
		}
	}

	public static class Settings {
		private final String name;
		private float baseHeightModifier = 0.1F;
		private float variationModifier = 0.2F;
		private float temperature = 0.5F;
		private float downfall = 0.5F;
		private int waterColor = 16777215;
		private boolean hasParent;
		private boolean field_12458 = true;
		@Nullable
		private String parent;

		public Settings(String string) {
			this.name = string;
		}

		protected Biome.Settings setTemperature(float temperature) {
			if (temperature > 0.1F && temperature < 0.2F) {
				throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
			} else {
				this.temperature = temperature;
				return this;
			}
		}

		protected Biome.Settings setDownfall(float downfall) {
			this.downfall = downfall;
			return this;
		}

		protected Biome.Settings setBaseHeightModifier(float baseHeightModifier) {
			this.baseHeightModifier = baseHeightModifier;
			return this;
		}

		protected Biome.Settings setVariationModifier(float variationModifier) {
			this.variationModifier = variationModifier;
			return this;
		}

		protected Biome.Settings method_11511() {
			this.field_12458 = false;
			return this;
		}

		protected Biome.Settings hasParent() {
			this.hasParent = true;
			return this;
		}

		protected Biome.Settings setWaterColor(int shade) {
			this.waterColor = shade;
			return this;
		}

		protected Biome.Settings setParent(String parent) {
			this.parent = parent;
			return this;
		}
	}

	public static class SpawnEntry extends Weighting.Weight {
		public Class<? extends MobEntity> entity;
		public int minGroupSize;
		public int maxGroupSize;

		public SpawnEntry(Class<? extends MobEntity> class_, int i, int j, int k) {
			super(i);
			this.entity = class_;
			this.minGroupSize = j;
			this.maxGroupSize = k;
		}

		public String toString() {
			return this.entity.getSimpleName() + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
		}
	}

	public static enum Temperature {
		OCEAN,
		COLD,
		MEDIUM,
		WARM;
	}
}
