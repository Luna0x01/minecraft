package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Leaves1Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Log1Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.GiantJungleTreeFeature;
import net.minecraft.world.gen.feature.JungleBushFeature;
import net.minecraft.world.gen.feature.JungleTreeFeature;
import net.minecraft.world.gen.feature.MelonFeature;
import net.minecraft.world.gen.feature.TallGrassFeature;
import net.minecraft.world.gen.feature.VineFeature;

public class JungleBiome extends Biome {
	private boolean field_7243;
	private static final BlockState JUNGLE_LOGS = Blocks.LOG.getDefaultState().with(Log1Block.VARIANT, PlanksBlock.WoodType.JUNGLE);
	private static final BlockState JUNGLE_LEAVES = Blocks.LEAVES
		.getDefaultState()
		.with(Leaves1Block.VARIANT, PlanksBlock.WoodType.JUNGLE)
		.with(LeavesBlock.CHECK_DECAY, false);
	private static final BlockState OAK_LEAVES = Blocks.LEAVES
		.getDefaultState()
		.with(Leaves1Block.VARIANT, PlanksBlock.WoodType.OAK)
		.with(LeavesBlock.CHECK_DECAY, false);

	public JungleBiome(int i, boolean bl) {
		super(i);
		this.field_7243 = bl;
		if (bl) {
			this.biomeDecorator.treesPerChunk = 2;
		} else {
			this.biomeDecorator.treesPerChunk = 50;
		}

		this.biomeDecorator.grassPerChunk = 25;
		this.biomeDecorator.flowersPerChunk = 4;
		if (!bl) {
			this.monsterEntries.add(new Biome.SpawnEntry(OcelotEntity.class, 2, 1, 1));
		}

		this.passiveEntries.add(new Biome.SpawnEntry(ChickenEntity.class, 10, 4, 4));
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		if (random.nextInt(10) == 0) {
			return this.field_4631;
		} else if (random.nextInt(2) == 0) {
			return new JungleBushFeature(JUNGLE_LOGS, OAK_LEAVES);
		} else {
			return (FoliageFeature)(!this.field_7243 && random.nextInt(3) == 0
				? new GiantJungleTreeFeature(false, 10, 20, JUNGLE_LOGS, JUNGLE_LEAVES)
				: new JungleTreeFeature(false, 4 + random.nextInt(7), JUNGLE_LOGS, JUNGLE_LEAVES, true));
		}
	}

	@Override
	public Feature method_3828(Random random) {
		return random.nextInt(4) == 0 ? new TallGrassFeature(TallPlantBlock.GrassType.FERN) : new TallGrassFeature(TallPlantBlock.GrassType.GRASS);
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		super.decorate(world, random, pos);
		int i = random.nextInt(16) + 8;
		int j = random.nextInt(16) + 8;
		int k = random.nextInt(world.getHighestBlock(pos.add(i, 0, j)).getY() * 2);
		new MelonFeature().generate(world, random, pos.add(i, k, j));
		VineFeature vineFeature = new VineFeature();

		for (int l = 0; l < 50; l++) {
			k = random.nextInt(16) + 8;
			int n = 128;
			int o = random.nextInt(16) + 8;
			vineFeature.generate(world, random, pos.add(k, 128, o));
		}
	}
}
