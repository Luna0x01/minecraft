package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.StrongholdPieces;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3901 extends class_3902<class_3900> {
	private boolean field_19257;
	private ChunkPos[] field_19258;
	private long field_19259;

	@Override
	protected boolean method_17431(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		if (this.field_19259 != chunkGenerator.method_17024()) {
			this.method_17422();
		}

		if (!this.field_19257) {
			this.method_17421(chunkGenerator);
			this.field_19257 = true;
		}

		for (ChunkPos chunkPos : this.field_19258) {
			if (i == chunkPos.x && j == chunkPos.z) {
				return true;
			}
		}

		return false;
	}

	private void method_17422() {
		this.field_19257 = false;
		this.field_19258 = null;
	}

	@Override
	protected boolean method_17426(IWorld iWorld) {
		return iWorld.method_3588().hasStructures();
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
		int k = 0;
		class_3901.class_10 lv = new class_3901.class_10(iWorld, arg, i, j, biome, k++);

		while (lv.method_17665().isEmpty() || ((StrongholdPieces.StartPiece)lv.method_17665().get(0)).portalRoom == null) {
			lv = new class_3901.class_10(iWorld, arg, i, j, biome, k++);
		}

		return lv;
	}

	@Override
	protected String method_17423() {
		return "Stronghold";
	}

	@Override
	public int method_17433() {
		return 8;
	}

	@Nullable
	@Override
	public BlockPos method_17425(World world, ChunkGenerator<? extends class_3798> chunkGenerator, BlockPos blockPos, int i, boolean bl) {
		if (!chunkGenerator.method_17020().method_16479(this)) {
			return null;
		} else {
			if (this.field_19259 != world.method_3581()) {
				this.method_17422();
			}

			if (!this.field_19257) {
				this.method_17421(chunkGenerator);
				this.field_19257 = true;
			}

			BlockPos blockPos2 = null;
			BlockPos.Mutable mutable = new BlockPos.Mutable(0, 0, 0);
			double d = Double.MAX_VALUE;

			for (ChunkPos chunkPos : this.field_19258) {
				mutable.setPosition((chunkPos.x << 4) + 8, 32, (chunkPos.z << 4) + 8);
				double e = mutable.getSquaredDistance(blockPos);
				if (blockPos2 == null) {
					blockPos2 = new BlockPos(mutable);
					d = e;
				} else if (e < d) {
					blockPos2 = new BlockPos(mutable);
					d = e;
				}
			}

			return blockPos2;
		}
	}

	private void method_17421(ChunkGenerator<?> chunkGenerator) {
		this.field_19259 = chunkGenerator.method_17024();
		List<Biome> list = Lists.newArrayList();

		for (Biome biome : Registry.BIOME) {
			if (biome != null && chunkGenerator.method_17015(biome, class_3844.field_19189)) {
				list.add(biome);
			}
		}

		int i = chunkGenerator.method_17013().method_17218();
		int j = chunkGenerator.method_17013().method_17219();
		int k = chunkGenerator.method_17013().method_17220();
		this.field_19258 = new ChunkPos[j];
		int l = 0;
		Long2ObjectMap<class_3992> long2ObjectMap = chunkGenerator.method_17017(this);
		synchronized (long2ObjectMap) {
			ObjectIterator d = long2ObjectMap.values().iterator();

			while (d.hasNext()) {
				class_3992 lv = (class_3992)d.next();
				if (l < this.field_19258.length) {
					this.field_19258[l++] = new ChunkPos(lv.method_17666(), lv.method_17667());
				}
			}
		}

		Random random = new Random();
		random.setSeed(chunkGenerator.method_17024());
		double d = random.nextDouble() * Math.PI * 2.0;
		int m = long2ObjectMap.size();
		if (m < this.field_19258.length) {
			int n = 0;
			int o = 0;

			for (int p = 0; p < this.field_19258.length; p++) {
				double e = (double)(4 * i + i * o * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
				int q = (int)Math.round(Math.cos(d) * e);
				int r = (int)Math.round(Math.sin(d) * e);
				BlockPos blockPos = chunkGenerator.method_17020().method_16478((q << 4) + 8, (r << 4) + 8, 112, list, random);
				if (blockPos != null) {
					q = blockPos.getX() >> 4;
					r = blockPos.getZ() >> 4;
				}

				if (p >= m) {
					this.field_19258[p] = new ChunkPos(q, r);
				}

				d += (Math.PI * 2) / (double)k;
				if (++n == k) {
					o++;
					n = 0;
					k += 2 * k / (o + 1);
					k = Math.min(k, this.field_19258.length - p);
					d += random.nextDouble() * Math.PI * 2.0;
				}
			}
		}
	}

	public static class class_10 extends class_3992 {
		public class_10() {
		}

		public class_10(IWorld iWorld, class_3812 arg, int i, int j, Biome biome, int k) {
			super(i, j, biome, arg, iWorld.method_3581() + (long)k);
			StrongholdPieces.init();
			StrongholdPieces.StartPiece startPiece = new StrongholdPieces.StartPiece(0, arg, (i << 4) + 2, (j << 4) + 2);
			this.field_19407.add(startPiece);
			startPiece.fillOpenings(startPiece, this.field_19407, arg);
			List<StructurePiece> list = startPiece.pieces;

			while (!list.isEmpty()) {
				int l = arg.nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(l);
				structurePiece.fillOpenings(startPiece, this.field_19407, arg);
			}

			this.method_17660(iWorld);
			this.method_17663(iWorld, arg, 10);
		}
	}
}
