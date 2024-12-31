package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.OceanMonumentPieces;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_3873 extends class_3902<class_3872> {
	private static final List<Biome.SpawnEntry> field_19222 = Lists.newArrayList(new Biome.SpawnEntry[]{new Biome.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4)});

	@Override
	protected ChunkPos method_17432(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		int m = chunkGenerator.method_17013().method_17216();
		int n = chunkGenerator.method_17013().method_17217();
		int o = i + m * k;
		int p = j + m * l;
		int q = o < 0 ? o - m + 1 : o;
		int r = p < 0 ? p - m + 1 : p;
		int s = q / m;
		int t = r / m;
		((class_3812)random).method_17289(chunkGenerator.method_17024(), s, t, 10387313);
		s *= m;
		t *= m;
		s += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		t += (random.nextInt(m - n) + random.nextInt(m - n)) / 2;
		return new ChunkPos(s, t);
	}

	@Override
	protected boolean method_17431(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		ChunkPos chunkPos = this.method_17432(chunkGenerator, random, i, j, 0, 0);
		if (i == chunkPos.x && j == chunkPos.z) {
			for (Biome biome : chunkGenerator.method_17020().method_16475(i * 16 + 9, j * 16 + 9, 16)) {
				if (!chunkGenerator.method_17015(biome, class_3844.field_19190)) {
					return false;
				}
			}

			for (Biome biome2 : chunkGenerator.method_17020().method_16475(i * 16 + 9, j * 16 + 9, 29)) {
				if (biome2.getCategory() != Biome.Category.OCEAN && biome2.getCategory() != Biome.Category.RIVER) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean method_17426(IWorld iWorld) {
		return iWorld.method_3588().hasStructures();
	}

	@Override
	protected class_3992 method_17428(IWorld iWorld, ChunkGenerator<?> chunkGenerator, class_3812 arg, int i, int j) {
		Biome biome = chunkGenerator.method_17020().method_16480(new BlockPos((i << 4) + 9, 0, (j << 4) + 9), Biomes.DEFAULT);
		return new class_3873.class_2260(iWorld, arg, i, j, biome);
	}

	@Override
	protected String method_17423() {
		return "Monument";
	}

	@Override
	public int method_17433() {
		return 8;
	}

	@Override
	public List<Biome.SpawnEntry> method_17347() {
		return field_19222;
	}

	public static class class_2260 extends class_3992 {
		private final Set<ChunkPos> field_10192 = Sets.newHashSet();
		private boolean field_10193;

		public class_2260() {
		}

		public class_2260(IWorld iWorld, class_3812 arg, int i, int j, Biome biome) {
			super(i, j, biome, arg, iWorld.method_3581());
			this.method_9241(iWorld, arg, i, j);
		}

		private void method_9241(BlockView blockView, Random random, int i, int j) {
			int k = i * 16 - 29;
			int l = j * 16 - 29;
			Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
			this.field_19407.add(new OceanMonumentPieces.MainBuilding(random, k, l, direction));
			this.method_17660(blockView);
			this.field_10193 = true;
		}

		@Override
		public void method_82(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			if (!this.field_10193) {
				this.field_19407.clear();
				this.method_9241(iWorld, random, this.method_17666(), this.method_17667());
			}

			super.method_82(iWorld, random, blockBox, chunkPos);
		}

		@Override
		public void method_9278(ChunkPos chunkPos) {
			super.method_9278(chunkPos);
			this.field_10192.add(chunkPos);
		}

		@Override
		public void method_5533(NbtCompound nbtCompound) {
			super.method_5533(nbtCompound);
			NbtList nbtList = new NbtList();

			for (ChunkPos chunkPos : this.field_10192) {
				NbtCompound nbtCompound2 = new NbtCompound();
				nbtCompound2.putInt("X", chunkPos.x);
				nbtCompound2.putInt("Z", chunkPos.z);
				nbtList.add((NbtElement)nbtCompound2);
			}

			nbtCompound.put("Processed", nbtList);
		}

		@Override
		public void method_5534(NbtCompound nbtCompound) {
			super.method_5534(nbtCompound);
			if (nbtCompound.contains("Processed", 9)) {
				NbtList nbtList = nbtCompound.getList("Processed", 10);

				for (int i = 0; i < nbtList.size(); i++) {
					NbtCompound nbtCompound2 = nbtList.getCompound(i);
					this.field_10192.add(new ChunkPos(nbtCompound2.getInt("X"), nbtCompound2.getInt("Z")));
				}
			}
		}
	}
}
