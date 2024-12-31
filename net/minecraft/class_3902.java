package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3902<C extends class_3845> extends class_3844<C> {
	private static final Logger field_19261 = LogManager.getLogger();
	public static final class_3992 field_19260 = new class_3992() {
		@Override
		public boolean method_85() {
			return false;
		}
	};

	@Override
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, C arg) {
		if (!this.method_17426(iWorld)) {
			return false;
		} else {
			int i = this.method_17433();
			int j = blockPos.getX() >> 4;
			int k = blockPos.getZ() >> 4;
			int l = j << 4;
			int m = k << 4;
			long n = ChunkPos.getIdFromCoords(j, k);
			boolean bl = false;

			for (int o = j - i; o <= j + i; o++) {
				for (int p = k - i; p <= k + i; p++) {
					long q = ChunkPos.getIdFromCoords(o, p);
					class_3992 lv = this.method_17429(iWorld, chunkGenerator, (class_3812)random, q);
					if (lv != field_19260 && lv.method_17664().intersectsXZ(l, m, l + 15, m + 15)) {
						((LongSet)chunkGenerator.method_17022(this).computeIfAbsent(n, lx -> new LongOpenHashSet())).add(q);
						iWorld.method_3586().method_17043(j, k, true).method_16997(this.method_17423(), q);
						lv.method_82(iWorld, random, new BlockBox(l, m, l + 15, m + 15), new ChunkPos(j, k));
						lv.method_9278(new ChunkPos(j, k));
						bl = true;
					}
				}
			}

			return bl;
		}
	}

	protected class_3992 method_17430(IWorld iWorld, BlockPos blockPos) {
		for (class_3992 lv : this.method_17427(iWorld, blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
			if (lv.method_85() && lv.method_17664().contains(blockPos)) {
				for (StructurePiece structurePiece : lv.method_17665()) {
					if (structurePiece.getBoundingBox().contains(blockPos)) {
						return lv;
					}
				}
			}
		}

		return field_19260;
	}

	public boolean method_17434(IWorld iWorld, BlockPos blockPos) {
		for (class_3992 lv : this.method_17427(iWorld, blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
			if (lv.method_85() && lv.method_17664().contains(blockPos)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_17435(IWorld iWorld, BlockPos blockPos) {
		return this.method_17430(iWorld, blockPos).method_85();
	}

	@Nullable
	public BlockPos method_17425(World world, ChunkGenerator<? extends class_3798> chunkGenerator, BlockPos blockPos, int i, boolean bl) {
		if (!chunkGenerator.method_17020().method_16479(this)) {
			return null;
		} else {
			int j = blockPos.getX() >> 4;
			int k = blockPos.getZ() >> 4;
			int l = 0;

			for (class_3812 lv = new class_3812(); l <= i; l++) {
				for (int m = -l; m <= l; m++) {
					boolean bl2 = m == -l || m == l;

					for (int n = -l; n <= l; n++) {
						boolean bl3 = n == -l || n == l;
						if (bl2 || bl3) {
							ChunkPos chunkPos = this.method_17432(chunkGenerator, lv, j, k, m, n);
							class_3992 lv2 = this.method_17429(world, chunkGenerator, lv, chunkPos.method_16281());
							if (lv2 != field_19260) {
								if (bl && lv2.method_17668()) {
									lv2.method_17669();
									return lv2.method_17658();
								}

								if (!bl) {
									return lv2.method_17658();
								}
							}

							if (l == 0) {
								break;
							}
						}
					}

					if (l == 0) {
						break;
					}
				}
			}

			return null;
		}
	}

	private List<class_3992> method_17427(IWorld iWorld, int i, int j) {
		List<class_3992> list = Lists.newArrayList();
		Long2ObjectMap<class_3992> long2ObjectMap = iWorld.method_3586().method_17046().method_17017(this);
		Long2ObjectMap<LongSet> long2ObjectMap2 = iWorld.method_3586().method_17046().method_17022(this);
		long l = ChunkPos.getIdFromCoords(i, j);
		LongSet longSet = (LongSet)long2ObjectMap2.get(l);
		if (longSet == null) {
			longSet = iWorld.method_3586().method_17043(i, j, true).method_17002(this.method_17423());
			long2ObjectMap2.put(l, longSet);
		}

		LongIterator var10 = longSet.iterator();

		while (var10.hasNext()) {
			Long long_ = (Long)var10.next();
			class_3992 lv = (class_3992)long2ObjectMap.get(long_);
			if (lv != null) {
				list.add(lv);
			} else {
				ChunkPos chunkPos = new ChunkPos(long_);
				class_3781 lv2 = iWorld.method_3586().method_17043(chunkPos.x, chunkPos.z, true);
				lv = lv2.method_16996(this.method_17423());
				if (lv != null) {
					long2ObjectMap.put(long_, lv);
					list.add(lv);
				}
			}
		}

		return list;
	}

	private class_3992 method_17429(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, class_3812 arg, long l) {
		if (!chunkGenerator.method_17020().method_16479(this)) {
			return field_19260;
		} else {
			Long2ObjectMap<class_3992> long2ObjectMap = chunkGenerator.method_17017(this);
			class_3992 lv = (class_3992)long2ObjectMap.get(l);
			if (lv != null) {
				return lv;
			} else {
				ChunkPos chunkPos = new ChunkPos(l);
				class_3781 lv2 = iWorld.method_3586().method_17043(chunkPos.x, chunkPos.z, false);
				if (lv2 != null) {
					lv = lv2.method_16996(this.method_17423());
					if (lv != null) {
						long2ObjectMap.put(l, lv);
						return lv;
					}
				}

				if (this.method_17431(chunkGenerator, arg, chunkPos.x, chunkPos.z)) {
					class_3992 lv3 = this.method_17428(iWorld, chunkGenerator, arg, chunkPos.x, chunkPos.z);
					lv = lv3.method_85() ? lv3 : field_19260;
				} else {
					lv = field_19260;
				}

				if (lv.method_85()) {
					iWorld.method_3586().method_17043(chunkPos.x, chunkPos.z, true).method_16998(this.method_17423(), lv);
				}

				long2ObjectMap.put(l, lv);
				return lv;
			}
		}
	}

	protected ChunkPos method_17432(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		return new ChunkPos(i + k, j + l);
	}

	protected abstract boolean method_17431(ChunkGenerator<?> chunkGenerator, Random random, int i, int j);

	protected abstract boolean method_17426(IWorld iWorld);

	protected abstract class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j);

	protected abstract String method_17423();

	public abstract int method_17433();
}
