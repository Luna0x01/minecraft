package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
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
	protected static final Biome.Height OTHER = new Biome.Height(0.1F, 0.2F);
	protected static final Biome.Height RIVER_HEIGHT = new Biome.Height(-0.5F, 0.0F);
	protected static final Biome.Height OCEAN_BIOME = new Biome.Height(-1.0F, 0.1F);
	protected static final Biome.Height DEEP_OCEAN_HEIGHT = new Biome.Height(-1.8F, 0.1F);
	protected static final Biome.Height PLAINS_HEIGHT = new Biome.Height(0.125F, 0.05F);
	protected static final Biome.Height TAIGA_HEIGHT = new Biome.Height(0.2F, 0.2F);
	protected static final Biome.Height HILLS_HEIGHT = new Biome.Height(0.45F, 0.3F);
	protected static final Biome.Height PLATEAU_HEIGHT = new Biome.Height(1.5F, 0.025F);
	protected static final Biome.Height EXTREME_HILLS_HEIGHT = new Biome.Height(1.0F, 0.5F);
	protected static final Biome.Height MUSHROOM_SHORE_HEIGHT = new Biome.Height(0.0F, 0.025F);
	protected static final Biome.Height STONE_BEACH_HEIGHT = new Biome.Height(0.1F, 0.8F);
	protected static final Biome.Height MUSHROOM_HEIGHT = new Biome.Height(0.2F, 0.3F);
	protected static final Biome.Height SWAMP_HEIGHT = new Biome.Height(-0.2F, 0.1F);
	private static final Biome[] BIOMES = new Biome[256];
	public static final Set<Biome> BIOMESET = Sets.newHashSet();
	public static final Map<String, Biome> MUTATED_BIOMES = Maps.newHashMap();
	public static final Biome OCEAN = new OceanBiome(0).setSeedModifier(112).setName("Ocean").setHeight(OCEAN_BIOME);
	public static final Biome PLAINS = new PlainsBiome(1).setSeedModifier(9286496).setName("Plains");
	public static final Biome DESERT = new DesertBiome(2)
		.setSeedModifier(16421912)
		.setName("Desert")
		.method_3839()
		.setTemperatureAndDownfall(2.0F, 0.0F)
		.setHeight(PLAINS_HEIGHT);
	public static final Biome EXTREME_HILLS = new ExtremeHillsBiome(3, false)
		.setSeedModifier(6316128)
		.setName("Extreme Hills")
		.setHeight(EXTREME_HILLS_HEIGHT)
		.setTemperatureAndDownfall(0.2F, 0.3F);
	public static final Biome FOREST = new ForestBiome(4, 0).setSeedModifier(353825).setName("Forest");
	public static final Biome TAIGA = new TaigaBiome(5, 0)
		.setSeedModifier(747097)
		.setName("Taiga")
		.method_3820(5159473)
		.setTemperatureAndDownfall(0.25F, 0.8F)
		.setHeight(TAIGA_HEIGHT);
	public static final Biome SWAMPLAND = new SwampBiome(6)
		.setSeedModifier(522674)
		.setName("Swampland")
		.method_3820(9154376)
		.setHeight(SWAMP_HEIGHT)
		.setTemperatureAndDownfall(0.8F, 0.9F);
	public static final Biome RIVER = new RiverBiome(7).setSeedModifier(255).setName("River").setHeight(RIVER_HEIGHT);
	public static final Biome HELL = new NetherBiome(8).setSeedModifier(16711680).setName("Hell").method_3839().setTemperatureAndDownfall(2.0F, 0.0F);
	public static final Biome THE_END = new EndBiome(9).setSeedModifier(8421631).setName("The End").method_3839();
	public static final Biome FROZEN_OCEAN = new OceanBiome(10)
		.setSeedModifier(9474208)
		.setName("FrozenOcean")
		.setMutated()
		.setHeight(OCEAN_BIOME)
		.setTemperatureAndDownfall(0.0F, 0.5F);
	public static final Biome FROZEN_RIVER = new RiverBiome(11)
		.setSeedModifier(10526975)
		.setName("FrozenRiver")
		.setMutated()
		.setHeight(RIVER_HEIGHT)
		.setTemperatureAndDownfall(0.0F, 0.5F);
	public static final Biome ICE_PLAINS = new IceBiome(12, false)
		.setSeedModifier(16777215)
		.setName("Ice Plains")
		.setMutated()
		.setTemperatureAndDownfall(0.0F, 0.5F)
		.setHeight(PLAINS_HEIGHT);
	public static final Biome ICE_MOUNTAINS = new IceBiome(13, false)
		.setSeedModifier(10526880)
		.setName("Ice Mountains")
		.setMutated()
		.setHeight(HILLS_HEIGHT)
		.setTemperatureAndDownfall(0.0F, 0.5F);
	public static final Biome MUSHROOM_ISLAND = new MushroomBiome(14)
		.setSeedModifier(16711935)
		.setName("MushroomIsland")
		.setTemperatureAndDownfall(0.9F, 1.0F)
		.setHeight(MUSHROOM_HEIGHT);
	public static final Biome MUSHROOM_ISLAND_SHORE = new MushroomBiome(15)
		.setSeedModifier(10486015)
		.setName("MushroomIslandShore")
		.setTemperatureAndDownfall(0.9F, 1.0F)
		.setHeight(MUSHROOM_SHORE_HEIGHT);
	public static final Biome BEACH = new BeachBiome(16)
		.setSeedModifier(16440917)
		.setName("Beach")
		.setTemperatureAndDownfall(0.8F, 0.4F)
		.setHeight(MUSHROOM_SHORE_HEIGHT);
	public static final Biome DESERT_HILLS = new DesertBiome(17)
		.setSeedModifier(13786898)
		.setName("DesertHills")
		.method_3839()
		.setTemperatureAndDownfall(2.0F, 0.0F)
		.setHeight(HILLS_HEIGHT);
	public static final Biome FOREST_HILLS = new ForestBiome(18, 0).setSeedModifier(2250012).setName("ForestHills").setHeight(HILLS_HEIGHT);
	public static final Biome TAIGA_HILLS = new TaigaBiome(19, 0)
		.setSeedModifier(1456435)
		.setName("TaigaHills")
		.method_3820(5159473)
		.setTemperatureAndDownfall(0.25F, 0.8F)
		.setHeight(HILLS_HEIGHT);
	public static final Biome EXTREME_HILLS_EDGE = new ExtremeHillsBiome(20, true)
		.setSeedModifier(7501978)
		.setName("Extreme Hills Edge")
		.setHeight(EXTREME_HILLS_HEIGHT.diminish())
		.setTemperatureAndDownfall(0.2F, 0.3F);
	public static final Biome JUNGLE = new JungleBiome(21, false)
		.setSeedModifier(5470985)
		.setName("Jungle")
		.method_3820(5470985)
		.setTemperatureAndDownfall(0.95F, 0.9F);
	public static final Biome JUNGLE_HILLS = new JungleBiome(22, false)
		.setSeedModifier(2900485)
		.setName("JungleHills")
		.method_3820(5470985)
		.setTemperatureAndDownfall(0.95F, 0.9F)
		.setHeight(HILLS_HEIGHT);
	public static final Biome JUNGLE_EDGE = new JungleBiome(23, true)
		.setSeedModifier(6458135)
		.setName("JungleEdge")
		.method_3820(5470985)
		.setTemperatureAndDownfall(0.95F, 0.8F);
	public static final Biome DEEP_OCEAN = new OceanBiome(24).setSeedModifier(48).setName("Deep Ocean").setHeight(DEEP_OCEAN_HEIGHT);
	public static final Biome STONE_BEACH = new StoneBeachBiome(25)
		.setSeedModifier(10658436)
		.setName("Stone Beach")
		.setTemperatureAndDownfall(0.2F, 0.3F)
		.setHeight(STONE_BEACH_HEIGHT);
	public static final Biome COLD_BEACH = new BeachBiome(26)
		.setSeedModifier(16445632)
		.setName("Cold Beach")
		.setTemperatureAndDownfall(0.05F, 0.3F)
		.setHeight(MUSHROOM_SHORE_HEIGHT)
		.setMutated();
	public static final Biome BIRCH_FOREST = new ForestBiome(27, 2).setName("Birch Forest").setSeedModifier(3175492);
	public static final Biome BIRCH_FOREST_HILLS = new ForestBiome(28, 2).setName("Birch Forest Hills").setSeedModifier(2055986).setHeight(HILLS_HEIGHT);
	public static final Biome ROOFED_FOREST = new ForestBiome(29, 3).setSeedModifier(4215066).setName("Roofed Forest");
	public static final Biome COLD_TAIGA = new TaigaBiome(30, 0)
		.setSeedModifier(3233098)
		.setName("Cold Taiga")
		.method_3820(5159473)
		.setMutated()
		.setTemperatureAndDownfall(-0.5F, 0.4F)
		.setHeight(TAIGA_HEIGHT)
		.method_3827(16777215);
	public static final Biome COLD_TAIGA_HILLS = new TaigaBiome(31, 0)
		.setSeedModifier(2375478)
		.setName("Cold Taiga Hills")
		.method_3820(5159473)
		.setMutated()
		.setTemperatureAndDownfall(-0.5F, 0.4F)
		.setHeight(HILLS_HEIGHT)
		.method_3827(16777215);
	public static final Biome MEGA_TAIGA = new TaigaBiome(32, 1)
		.setSeedModifier(5858897)
		.setName("Mega Taiga")
		.method_3820(5159473)
		.setTemperatureAndDownfall(0.3F, 0.8F)
		.setHeight(TAIGA_HEIGHT);
	public static final Biome MEGA_TAIGA_HILLS = new TaigaBiome(33, 1)
		.setSeedModifier(4542270)
		.setName("Mega Taiga Hills")
		.method_3820(5159473)
		.setTemperatureAndDownfall(0.3F, 0.8F)
		.setHeight(HILLS_HEIGHT);
	public static final Biome EXTREME_HILLS_PLUS = new ExtremeHillsBiome(34, true)
		.setSeedModifier(5271632)
		.setName("Extreme Hills+")
		.setHeight(EXTREME_HILLS_HEIGHT)
		.setTemperatureAndDownfall(0.2F, 0.3F);
	public static final Biome SAVANNA = new SavannaBiome(35)
		.setSeedModifier(12431967)
		.setName("Savanna")
		.setTemperatureAndDownfall(1.2F, 0.0F)
		.method_3839()
		.setHeight(PLAINS_HEIGHT);
	public static final Biome SAVANNA_PLATEAU = new SavannaBiome(36)
		.setSeedModifier(10984804)
		.setName("Savanna Plateau")
		.setTemperatureAndDownfall(1.0F, 0.0F)
		.method_3839()
		.setHeight(PLATEAU_HEIGHT);
	public static final Biome MESA = new MesaBiome(37, false, false).setSeedModifier(14238997).setName("Mesa");
	public static final Biome MESA_PLATEAU_F = new MesaBiome(38, false, true).setSeedModifier(11573093).setName("Mesa Plateau F").setHeight(PLATEAU_HEIGHT);
	public static final Biome MESA_PLATEAU = new MesaBiome(39, false, false).setSeedModifier(13274213).setName("Mesa Plateau").setHeight(PLATEAU_HEIGHT);
	public static final Biome DEFAULT = OCEAN;
	protected static final PerlinNoiseGenerator TEMPERATURE_NOISE;
	protected static final PerlinNoiseGenerator FOLIAGE_NOISE;
	protected static final DoublePlantFeature DOUBLE_PLANT_FEATURE;
	public String name;
	public int field_4661;
	public int field_7203;
	public BlockState topBlock = Blocks.GRASS.getDefaultState();
	public BlockState baseBlock = Blocks.DIRT.getDefaultState();
	public int field_4619 = 5169201;
	public float depth = OTHER.baseHeightModifier;
	public float variationModifier;
	public float temperature;
	public float downfall;
	public int waterColor;
	public BiomeDecorator biomeDecorator;
	protected List<Biome.SpawnEntry> monsterEntries;
	protected List<Biome.SpawnEntry> passiveEntries;
	protected List<Biome.SpawnEntry> waterEntries;
	protected List<Biome.SpawnEntry> flyingEntries;
	protected boolean mutated;
	protected boolean field_4635;
	public final int id;
	protected JungleTreeFeature JUNGLE_TREE_FEATURE;
	protected BigTreeFeature field_4631;
	protected OakTreeFeature OAK_TREE_FEATURE;

	protected Biome(int i) {
		this.variationModifier = OTHER.variationModifier;
		this.temperature = 0.5F;
		this.downfall = 0.5F;
		this.waterColor = 16777215;
		this.monsterEntries = Lists.newArrayList();
		this.passiveEntries = Lists.newArrayList();
		this.waterEntries = Lists.newArrayList();
		this.flyingEntries = Lists.newArrayList();
		this.field_4635 = true;
		this.JUNGLE_TREE_FEATURE = new JungleTreeFeature(false);
		this.field_4631 = new BigTreeFeature(false);
		this.OAK_TREE_FEATURE = new OakTreeFeature();
		this.id = i;
		BIOMES[i] = this;
		this.biomeDecorator = this.createBiomePopulator();
		this.passiveEntries.add(new Biome.SpawnEntry(SheepEntity.class, 12, 4, 4));
		this.passiveEntries.add(new Biome.SpawnEntry(RabbitEntity.class, 10, 3, 3));
		this.passiveEntries.add(new Biome.SpawnEntry(PigEntity.class, 10, 4, 4));
		this.passiveEntries.add(new Biome.SpawnEntry(ChickenEntity.class, 10, 4, 4));
		this.passiveEntries.add(new Biome.SpawnEntry(CowEntity.class, 8, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(SpiderEntity.class, 100, 4, 4));
		this.monsterEntries.add(new Biome.SpawnEntry(ZombieEntity.class, 100, 4, 4));
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

	protected Biome setTemperatureAndDownfall(float temperature, float downfall) {
		if (temperature > 0.1F && temperature < 0.2F) {
			throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
		} else {
			this.temperature = temperature;
			this.downfall = downfall;
			return this;
		}
	}

	protected final Biome setHeight(Biome.Height height) {
		this.depth = height.baseHeightModifier;
		this.variationModifier = height.variationModifier;
		return this;
	}

	protected Biome method_3839() {
		this.field_4635 = false;
		return this;
	}

	public FoliageFeature method_3822(Random random) {
		return (FoliageFeature)(random.nextInt(10) == 0 ? this.field_4631 : this.JUNGLE_TREE_FEATURE);
	}

	public Feature method_3828(Random random) {
		return new TallGrassFeature(TallPlantBlock.GrassType.GRASS);
	}

	public FlowerBlock.FlowerType pickFlower(Random random, BlockPos pos) {
		return random.nextInt(3) > 0 ? FlowerBlock.FlowerType.DANDELION : FlowerBlock.FlowerType.POPPY;
	}

	protected Biome setMutated() {
		this.mutated = true;
		return this;
	}

	protected Biome setName(String name) {
		this.name = name;
		return this;
	}

	protected Biome method_3820(int i) {
		this.field_4619 = i;
		return this;
	}

	protected Biome setSeedModifier(int modifier) {
		this.seedModifier(modifier, false);
		return this;
	}

	protected Biome method_3827(int i) {
		this.field_7203 = i;
		return this;
	}

	protected Biome seedModifier(int i, boolean bl) {
		this.field_4661 = i;
		if (bl) {
			this.field_7203 = (i & 16711422) >> 1;
		} else {
			this.field_7203 = i;
		}

		return this;
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
		return this.downfall > 0.85F;
	}

	public float getMaxSpawnLimit() {
		return 0.1F;
	}

	public final int getDownfall() {
		return (int)(this.downfall * 65536.0F);
	}

	public final float getRainfall() {
		return this.downfall;
	}

	public final float getTemperature(BlockPos pos) {
		if (pos.getY() > 64) {
			float f = (float)(TEMPERATURE_NOISE.noise((double)pos.getX() * 1.0 / 8.0, (double)pos.getZ() * 1.0 / 8.0) * 4.0);
			return this.temperature - (f + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return this.temperature;
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

	public boolean isMutatedBiome() {
		return this.mutated;
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
				chunkStorage.set(o, p, n, Blocks.BEDROCK.getDefaultState());
			} else {
				BlockState blockState3 = chunkStorage.get(o, p, n);
				if (blockState3.getBlock().getMaterial() == Material.AIR) {
					l = -1;
				} else if (blockState3.getBlock() == Blocks.STONE) {
					if (l == -1) {
						if (m <= 0) {
							blockState = null;
							blockState2 = Blocks.STONE.getDefaultState();
						} else if (p >= k - 4 && p <= k + 1) {
							blockState = this.topBlock;
							blockState2 = this.baseBlock;
						}

						if (p < k && (blockState == null || blockState.getBlock().getMaterial() == Material.AIR)) {
							if (this.getTemperature(mutable.setPosition(i, p, j)) < 0.15F) {
								blockState = Blocks.ICE.getDefaultState();
							} else {
								blockState = Blocks.WATER.getDefaultState();
							}
						}

						l = m;
						if (p >= k - 1) {
							chunkStorage.set(o, p, n, blockState);
						} else if (p < k - 7 - m) {
							blockState = null;
							blockState2 = Blocks.STONE.getDefaultState();
							chunkStorage.set(o, p, n, Blocks.GRAVEL.getDefaultState());
						} else {
							chunkStorage.set(o, p, n, blockState2);
						}
					} else if (l > 0) {
						l--;
						chunkStorage.set(o, p, n, blockState2);
						if (l == 0 && blockState2.getBlock() == Blocks.SAND) {
							l = random.nextInt(4) + Math.max(0, p - 63);
							blockState2 = blockState2.get(SandBlock.sandType) == SandBlock.SandType.RED_SAND
								? Blocks.RED_SANDSTONE.getDefaultState()
								: Blocks.SANDSTONE.getDefaultState();
						}
					}
				}
			}
		}
	}

	protected Biome getMutatedVariant() {
		return this.getMutatedVariant(this.id + 128);
	}

	protected Biome getMutatedVariant(int id) {
		return new MutatedBiome(id, this);
	}

	public Class<? extends Biome> asClass() {
		return this.getClass();
	}

	public boolean method_6421(Biome biome) {
		if (biome == this) {
			return true;
		} else {
			return biome == null ? false : this.asClass() == biome.asClass();
		}
	}

	public Biome.Temperature getBiomeTemperature() {
		if ((double)this.temperature < 0.2) {
			return Biome.Temperature.COLD;
		} else {
			return (double)this.temperature < 1.0 ? Biome.Temperature.MEDIUM : Biome.Temperature.WARM;
		}
	}

	public static Biome[] getBiomes() {
		return BIOMES;
	}

	public static Biome byId(int id) {
		return getBiomeById(id, null);
	}

	public static Biome getBiomeById(int id, Biome fallback) {
		if (id >= 0 && id <= BIOMES.length) {
			Biome biome = BIOMES[id];
			return biome == null ? fallback : biome;
		} else {
			LOGGER.warn("Biome ID is out of bounds: " + id + ", defaulting to 0 (Ocean)");
			return OCEAN;
		}
	}

	static {
		PLAINS.getMutatedVariant();
		DESERT.getMutatedVariant();
		FOREST.getMutatedVariant();
		TAIGA.getMutatedVariant();
		SWAMPLAND.getMutatedVariant();
		ICE_PLAINS.getMutatedVariant();
		JUNGLE.getMutatedVariant();
		JUNGLE_EDGE.getMutatedVariant();
		COLD_TAIGA.getMutatedVariant();
		SAVANNA.getMutatedVariant();
		SAVANNA_PLATEAU.getMutatedVariant();
		MESA.getMutatedVariant();
		MESA_PLATEAU_F.getMutatedVariant();
		MESA_PLATEAU.getMutatedVariant();
		BIRCH_FOREST.getMutatedVariant();
		BIRCH_FOREST_HILLS.getMutatedVariant();
		ROOFED_FOREST.getMutatedVariant();
		MEGA_TAIGA.getMutatedVariant();
		EXTREME_HILLS.getMutatedVariant();
		EXTREME_HILLS_PLUS.getMutatedVariant();
		MEGA_TAIGA.getMutatedVariant(MEGA_TAIGA_HILLS.id + 128).setName("Redwood Taiga Hills M");

		for (Biome biome : BIOMES) {
			if (biome != null) {
				if (MUTATED_BIOMES.containsKey(biome.name)) {
					throw new Error("Biome \"" + biome.name + "\" is defined as both ID " + ((Biome)MUTATED_BIOMES.get(biome.name)).id + " and " + biome.id);
				}

				MUTATED_BIOMES.put(biome.name, biome);
				if (biome.id < 128) {
					BIOMESET.add(biome);
				}
			}
		}

		BIOMESET.remove(HELL);
		BIOMESET.remove(THE_END);
		BIOMESET.remove(FROZEN_OCEAN);
		BIOMESET.remove(EXTREME_HILLS_EDGE);
		TEMPERATURE_NOISE = new PerlinNoiseGenerator(new Random(1234L), 1);
		FOLIAGE_NOISE = new PerlinNoiseGenerator(new Random(2345L), 1);
		DOUBLE_PLANT_FEATURE = new DoublePlantFeature();
	}

	public static class Height {
		public float baseHeightModifier;
		public float variationModifier;

		public Height(float f, float g) {
			this.baseHeightModifier = f;
			this.variationModifier = g;
		}

		public Biome.Height diminish() {
			return new Biome.Height(this.baseHeightModifier * 0.8F, this.variationModifier * 0.6F);
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
