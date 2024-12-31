package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.CustomizedWorldProperties;
import net.minecraft.world.gen.feature.CactusFeature;
import net.minecraft.world.gen.feature.ClayFeature;
import net.minecraft.world.gen.feature.DeadbushFeature;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FlowerPatchFeature;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeature;
import net.minecraft.world.gen.feature.LilyPadFeature;
import net.minecraft.world.gen.feature.MushroomFeature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.PumpkinFeature;
import net.minecraft.world.gen.feature.SpringFeature;
import net.minecraft.world.gen.feature.SugarcaneFeature;

public class BiomeDecorator {
	protected boolean field_12461;
	protected BlockPos startPos;
	protected CustomizedWorldProperties worldProperties;
	protected Feature clayFeature = new ClayFeature(4);
	protected Feature sandDiskFeature = new DiskFeature(Blocks.SAND, 7);
	protected Feature gravelDiskFeature = new DiskFeature(Blocks.GRAVEL, 6);
	protected Feature dirtFeature;
	protected Feature gravelFeature;
	protected Feature graniteFeature;
	protected Feature dioriteFeature;
	protected Feature andesiteFeature;
	protected Feature coalOreFeature;
	protected Feature ironOreFeature;
	protected Feature goldOreFeature;
	protected Feature redstoneOreFeature;
	protected Feature diamondOreFeature;
	protected Feature lapisOreFeature;
	protected FlowerPatchFeature dandelionFeature = new FlowerPatchFeature(Blocks.YELLOW_FLOWER, FlowerBlock.FlowerType.DANDELION);
	protected Feature brownMushroomFeature = new MushroomFeature(Blocks.BROWN_MUSHROOM);
	protected Feature redMushroomFeature = new MushroomFeature(Blocks.RED_MUSHROOM);
	protected Feature hugeMushroomFeature = new HugeMushroomFeature();
	protected Feature sugarcaneFeature = new SugarcaneFeature();
	protected Feature cactusFeature = new CactusFeature();
	protected Feature lilyPadFeature = new LilyPadFeature();
	protected int lilyPadsPerChunk;
	protected int treesPerChunk;
	protected int flowersPerChunk = 2;
	protected int grassPerChunk = 1;
	protected int deadBushesPerChunk;
	protected int mushroomsPerChunk;
	protected int sugarcanePerChunk;
	protected int cactusPerChunk;
	protected int gravelDisksPerChunk = 1;
	protected int sandDisksPerChunk = 3;
	protected int clayPerChunk = 1;
	protected int hugeMushroomsPerChunk;
	public boolean generateLakes = true;

	public void decorate(World world, Random random, Biome biome, BlockPos pos) {
		if (this.field_12461) {
			throw new RuntimeException("Already decorating");
		} else {
			this.worldProperties = CustomizedWorldProperties.Builder.fromJson(world.getLevelProperties().getGeneratorOptions()).build();
			this.startPos = pos;
			this.dirtFeature = new OreFeature(Blocks.DIRT.getDefaultState(), this.worldProperties.dirtSize);
			this.gravelFeature = new OreFeature(Blocks.GRAVEL.getDefaultState(), this.worldProperties.gravelSize);
			this.graniteFeature = new OreFeature(Blocks.STONE.getDefaultState().with(StoneBlock.VARIANT, StoneBlock.StoneType.GRANITE), this.worldProperties.graniteSize);
			this.dioriteFeature = new OreFeature(Blocks.STONE.getDefaultState().with(StoneBlock.VARIANT, StoneBlock.StoneType.DIORITE), this.worldProperties.dioriteSize);
			this.andesiteFeature = new OreFeature(
				Blocks.STONE.getDefaultState().with(StoneBlock.VARIANT, StoneBlock.StoneType.ANDESITE), this.worldProperties.andesiteSize
			);
			this.coalOreFeature = new OreFeature(Blocks.COAL_ORE.getDefaultState(), this.worldProperties.coalSize);
			this.ironOreFeature = new OreFeature(Blocks.IRON_ORE.getDefaultState(), this.worldProperties.ironSize);
			this.goldOreFeature = new OreFeature(Blocks.GOLD_ORE.getDefaultState(), this.worldProperties.goldSize);
			this.redstoneOreFeature = new OreFeature(Blocks.REDSTONE_ORE.getDefaultState(), this.worldProperties.redstoneSize);
			this.diamondOreFeature = new OreFeature(Blocks.DIAMOND_ORE.getDefaultState(), this.worldProperties.diamondSize);
			this.lapisOreFeature = new OreFeature(Blocks.LAPIS_LAZULI_ORE.getDefaultState(), this.worldProperties.lapisSize);
			this.method_11530(biome, world, random);
			this.field_12461 = false;
		}
	}

	protected void method_11530(Biome biome, World world, Random random) {
		this.method_11528(world, random);

		for (int i = 0; i < this.sandDisksPerChunk; i++) {
			int j = random.nextInt(16) + 8;
			int k = random.nextInt(16) + 8;
			this.sandDiskFeature.generate(world, random, world.getTopPosition(this.startPos.add(j, 0, k)));
		}

		for (int l = 0; l < this.clayPerChunk; l++) {
			int m = random.nextInt(16) + 8;
			int n = random.nextInt(16) + 8;
			this.clayFeature.generate(world, random, world.getTopPosition(this.startPos.add(m, 0, n)));
		}

		for (int o = 0; o < this.gravelDisksPerChunk; o++) {
			int p = random.nextInt(16) + 8;
			int q = random.nextInt(16) + 8;
			this.gravelDiskFeature.generate(world, random, world.getTopPosition(this.startPos.add(p, 0, q)));
		}

		int r = this.treesPerChunk;
		if (random.nextInt(10) == 0) {
			r++;
		}

		for (int s = 0; s < r; s++) {
			int t = random.nextInt(16) + 8;
			int u = random.nextInt(16) + 8;
			FoliageFeature foliageFeature = biome.method_3822(random);
			foliageFeature.setLeafRadius();
			BlockPos blockPos = world.getHighestBlock(this.startPos.add(t, 0, u));
			if (foliageFeature.generate(world, random, blockPos)) {
				foliageFeature.generateSurroundingFeatures(world, random, blockPos);
			}
		}

		for (int v = 0; v < this.hugeMushroomsPerChunk; v++) {
			int w = random.nextInt(16) + 8;
			int x = random.nextInt(16) + 8;
			this.hugeMushroomFeature.generate(world, random, world.getHighestBlock(this.startPos.add(w, 0, x)));
		}

		for (int y = 0; y < this.flowersPerChunk; y++) {
			int z = random.nextInt(16) + 8;
			int aa = random.nextInt(16) + 8;
			int ab = world.getHighestBlock(this.startPos.add(z, 0, aa)).getY() + 32;
			if (ab > 0) {
				int ac = random.nextInt(ab);
				BlockPos blockPos2 = this.startPos.add(z, ac, aa);
				FlowerBlock.FlowerType flowerType = biome.pickFlower(random, blockPos2);
				FlowerBlock flowerBlock = flowerType.getColor().getBlock();
				if (flowerBlock.getDefaultState().getMaterial() != Material.AIR) {
					this.dandelionFeature.setFlowers(flowerBlock, flowerType);
					this.dandelionFeature.generate(world, random, blockPos2);
				}
			}
		}

		for (int ad = 0; ad < this.grassPerChunk; ad++) {
			int ae = random.nextInt(16) + 8;
			int af = random.nextInt(16) + 8;
			int ag = world.getHighestBlock(this.startPos.add(ae, 0, af)).getY() * 2;
			if (ag > 0) {
				int ah = random.nextInt(ag);
				biome.method_3828(random).generate(world, random, this.startPos.add(ae, ah, af));
			}
		}

		for (int ai = 0; ai < this.deadBushesPerChunk; ai++) {
			int aj = random.nextInt(16) + 8;
			int ak = random.nextInt(16) + 8;
			int al = world.getHighestBlock(this.startPos.add(aj, 0, ak)).getY() * 2;
			if (al > 0) {
				int am = random.nextInt(al);
				new DeadbushFeature().generate(world, random, this.startPos.add(aj, am, ak));
			}
		}

		for (int an = 0; an < this.lilyPadsPerChunk; an++) {
			int ao = random.nextInt(16) + 8;
			int ap = random.nextInt(16) + 8;
			int aq = world.getHighestBlock(this.startPos.add(ao, 0, ap)).getY() * 2;
			if (aq > 0) {
				int ar = random.nextInt(aq);
				BlockPos blockPos3 = this.startPos.add(ao, ar, ap);

				while (blockPos3.getY() > 0) {
					BlockPos blockPos4 = blockPos3.down();
					if (!world.isAir(blockPos4)) {
						break;
					}

					blockPos3 = blockPos4;
				}

				this.lilyPadFeature.generate(world, random, blockPos3);
			}
		}

		for (int as = 0; as < this.mushroomsPerChunk; as++) {
			if (random.nextInt(4) == 0) {
				int at = random.nextInt(16) + 8;
				int au = random.nextInt(16) + 8;
				BlockPos blockPos5 = world.getHighestBlock(this.startPos.add(at, 0, au));
				this.brownMushroomFeature.generate(world, random, blockPos5);
			}

			if (random.nextInt(8) == 0) {
				int av = random.nextInt(16) + 8;
				int aw = random.nextInt(16) + 8;
				int ax = world.getHighestBlock(this.startPos.add(av, 0, aw)).getY() * 2;
				if (ax > 0) {
					int ay = random.nextInt(ax);
					BlockPos blockPos6 = this.startPos.add(av, ay, aw);
					this.redMushroomFeature.generate(world, random, blockPos6);
				}
			}
		}

		if (random.nextInt(4) == 0) {
			int az = random.nextInt(16) + 8;
			int ba = random.nextInt(16) + 8;
			int bb = world.getHighestBlock(this.startPos.add(az, 0, ba)).getY() * 2;
			if (bb > 0) {
				int bc = random.nextInt(bb);
				this.brownMushroomFeature.generate(world, random, this.startPos.add(az, bc, ba));
			}
		}

		if (random.nextInt(8) == 0) {
			int bd = random.nextInt(16) + 8;
			int be = random.nextInt(16) + 8;
			int bf = world.getHighestBlock(this.startPos.add(bd, 0, be)).getY() * 2;
			if (bf > 0) {
				int bg = random.nextInt(bf);
				this.redMushroomFeature.generate(world, random, this.startPos.add(bd, bg, be));
			}
		}

		for (int bh = 0; bh < this.sugarcanePerChunk; bh++) {
			int bi = random.nextInt(16) + 8;
			int bj = random.nextInt(16) + 8;
			int bk = world.getHighestBlock(this.startPos.add(bi, 0, bj)).getY() * 2;
			if (bk > 0) {
				int bl = random.nextInt(bk);
				this.sugarcaneFeature.generate(world, random, this.startPos.add(bi, bl, bj));
			}
		}

		for (int bm = 0; bm < 10; bm++) {
			int bn = random.nextInt(16) + 8;
			int bo = random.nextInt(16) + 8;
			int bp = world.getHighestBlock(this.startPos.add(bn, 0, bo)).getY() * 2;
			if (bp > 0) {
				int bq = random.nextInt(bp);
				this.sugarcaneFeature.generate(world, random, this.startPos.add(bn, bq, bo));
			}
		}

		if (random.nextInt(32) == 0) {
			int br = random.nextInt(16) + 8;
			int bs = random.nextInt(16) + 8;
			int bt = world.getHighestBlock(this.startPos.add(br, 0, bs)).getY() * 2;
			if (bt > 0) {
				int bu = random.nextInt(bt);
				new PumpkinFeature().generate(world, random, this.startPos.add(br, bu, bs));
			}
		}

		for (int bv = 0; bv < this.cactusPerChunk; bv++) {
			int bw = random.nextInt(16) + 8;
			int bx = random.nextInt(16) + 8;
			int by = world.getHighestBlock(this.startPos.add(bw, 0, bx)).getY() * 2;
			if (by > 0) {
				int bz = random.nextInt(by);
				this.cactusFeature.generate(world, random, this.startPos.add(bw, bz, bx));
			}
		}

		if (this.generateLakes) {
			for (int ca = 0; ca < 50; ca++) {
				int cb = random.nextInt(16) + 8;
				int cc = random.nextInt(16) + 8;
				int cd = random.nextInt(248) + 8;
				if (cd > 0) {
					int ce = random.nextInt(cd);
					BlockPos blockPos7 = this.startPos.add(cb, ce, cc);
					new SpringFeature(Blocks.FLOWING_WATER).generate(world, random, blockPos7);
				}
			}

			for (int cf = 0; cf < 20; cf++) {
				int cg = random.nextInt(16) + 8;
				int ch = random.nextInt(16) + 8;
				int ci = random.nextInt(random.nextInt(random.nextInt(240) + 8) + 8);
				BlockPos blockPos8 = this.startPos.add(cg, ci, ch);
				new SpringFeature(Blocks.FLOWING_LAVA).generate(world, random, blockPos8);
			}
		}
	}

	protected void method_11528(World world, Random random) {
		this.method_11529(world, random, this.worldProperties.dirtCount, this.dirtFeature, this.worldProperties.dirtMinHeight, this.worldProperties.dirtMaxHeight);
		this.method_11529(
			world, random, this.worldProperties.gravelCount, this.gravelFeature, this.worldProperties.gravelMinHeight, this.worldProperties.gravelMaxHeight
		);
		this.method_11529(
			world, random, this.worldProperties.dioriteCount, this.dioriteFeature, this.worldProperties.dioriteMinHeight, this.worldProperties.dioriteMaxHeight
		);
		this.method_11529(
			world, random, this.worldProperties.graniteCount, this.graniteFeature, this.worldProperties.graniteMinHeight, this.worldProperties.graniteMaxHeight
		);
		this.method_11529(
			world, random, this.worldProperties.andesiteCount, this.andesiteFeature, this.worldProperties.andesiteMinHeight, this.worldProperties.andesiteMaxHeight
		);
		this.method_11529(world, random, this.worldProperties.coalCount, this.coalOreFeature, this.worldProperties.coalMinHeight, this.worldProperties.coalMaxHeight);
		this.method_11529(world, random, this.worldProperties.ironCount, this.ironOreFeature, this.worldProperties.ironMinHeight, this.worldProperties.ironMaxHeight);
		this.method_11529(world, random, this.worldProperties.goldCount, this.goldOreFeature, this.worldProperties.goldMinHeight, this.worldProperties.goldMaxHeight);
		this.method_11529(
			world, random, this.worldProperties.redstoneCount, this.redstoneOreFeature, this.worldProperties.redstoneMinHeight, this.worldProperties.redstoneMaxHeight
		);
		this.method_11529(
			world, random, this.worldProperties.diamondCount, this.diamondOreFeature, this.worldProperties.diamondMinHeight, this.worldProperties.diamondMaxHeight
		);
		this.method_11531(
			world, random, this.worldProperties.lapisCount, this.lapisOreFeature, this.worldProperties.lapisCenterHeight, this.worldProperties.lapisSpread
		);
	}

	protected void method_11529(World world, Random random, int count, Feature feature, int minHeight, int maxHeight) {
		if (maxHeight < minHeight) {
			int i = minHeight;
			minHeight = maxHeight;
			maxHeight = i;
		} else if (maxHeight == minHeight) {
			if (minHeight < 255) {
				maxHeight++;
			} else {
				minHeight--;
			}
		}

		for (int j = 0; j < count; j++) {
			BlockPos blockPos = this.startPos.add(random.nextInt(16), random.nextInt(maxHeight - minHeight) + minHeight, random.nextInt(16));
			feature.generate(world, random, blockPos);
		}
	}

	protected void method_11531(World world, Random random, int i, Feature feature, int j, int k) {
		for (int l = 0; l < i; l++) {
			BlockPos blockPos = this.startPos.add(random.nextInt(16), random.nextInt(k) + random.nextInt(k) + j - k, random.nextInt(16));
			feature.generate(world, random, blockPos);
		}
	}
}
