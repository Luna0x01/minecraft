package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;

public class class_4245 implements class_3600 {
	protected final int field_20869;
	protected final int field_20870;
	protected final BlockPos field_20871;
	protected final int field_20872;
	protected final int field_20873;
	protected final int field_20874;
	protected final Chunk[][] field_20875;
	protected final class_4245.class_4246[] field_20876;
	protected final World field_20877;

	@Nullable
	public static class_4245 method_19346(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
		int j = blockPos.getX() - i >> 4;
		int k = blockPos.getZ() - i >> 4;
		int l = blockPos2.getX() + i >> 4;
		int m = blockPos2.getZ() + i >> 4;
		Chunk[][] chunks = new Chunk[l - j + 1][m - k + 1];

		for (int n = j; n <= l; n++) {
			for (int o = k; o <= m; o++) {
				chunks[n - j][o - k] = world.method_16347(n, o);
			}
		}

		boolean bl = true;

		for (int p = blockPos.getX() >> 4; p <= blockPos2.getX() >> 4; p++) {
			for (int q = blockPos.getZ() >> 4; q <= blockPos2.getZ() >> 4; q++) {
				Chunk chunk = chunks[p - j][q - k];
				if (!chunk.areSectionsEmptyBetween(blockPos.getY(), blockPos2.getY())) {
					bl = false;
				}
			}
		}

		if (bl) {
			return null;
		} else {
			int r = 1;
			BlockPos blockPos3 = blockPos.add(-1, -1, -1);
			BlockPos blockPos4 = blockPos2.add(1, 1, 1);
			return new class_4245(world, j, k, chunks, blockPos3, blockPos4);
		}
	}

	public class_4245(World world, int i, int j, Chunk[][] chunks, BlockPos blockPos, BlockPos blockPos2) {
		this.field_20877 = world;
		this.field_20869 = i;
		this.field_20870 = j;
		this.field_20875 = chunks;
		this.field_20871 = blockPos;
		this.field_20872 = blockPos2.getX() - blockPos.getX() + 1;
		this.field_20873 = blockPos2.getY() - blockPos.getY() + 1;
		this.field_20874 = blockPos2.getZ() - blockPos.getZ() + 1;
		this.field_20876 = new class_4245.class_4246[this.field_20872 * this.field_20873 * this.field_20874];

		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos, blockPos2)) {
			this.field_20876[this.method_19347(mutable)] = new class_4245.class_4246(world, mutable);
		}
	}

	protected int method_19347(BlockPos blockPos) {
		int i = blockPos.getX() - this.field_20871.getX();
		int j = blockPos.getY() - this.field_20871.getY();
		int k = blockPos.getZ() - this.field_20871.getZ();
		return k * this.field_20872 * this.field_20873 + j * this.field_20872 + i;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.field_20876[this.method_19347(pos)].field_20878;
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.field_20876[this.method_19347(pos)].field_20879;
	}

	@Override
	public Biome method_8577(BlockPos blockPos) {
		int i = (blockPos.getX() >> 4) - this.field_20869;
		int j = (blockPos.getZ() >> 4) - this.field_20870;
		return this.field_20875[i][j].method_17088(blockPos);
	}

	private int method_19349(LightType lightType, BlockPos blockPos) {
		return this.field_20876[this.method_19347(blockPos)].method_19350(lightType, blockPos);
	}

	@Override
	public int method_8578(BlockPos blockPos, int i) {
		int j = this.method_19349(LightType.SKY, blockPos);
		int k = this.method_19349(LightType.BLOCK, blockPos);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.method_19348(pos, Chunk.Status.IMMEDIATE);
	}

	@Nullable
	public BlockEntity method_19348(BlockPos blockPos, Chunk.Status status) {
		int i = (blockPos.getX() >> 4) - this.field_20869;
		int j = (blockPos.getZ() >> 4) - this.field_20870;
		return this.field_20875[i][j].getBlockEntity(blockPos, status);
	}

	@Override
	public float method_16356(BlockPos blockPos) {
		return this.field_20877.dimension.getLightLevelToBrightness()[this.method_16358(blockPos)];
	}

	@Override
	public int method_16389(BlockPos blockPos, int i) {
		if (this.getBlockState(blockPos).method_16889(this, blockPos)) {
			int j = 0;

			for (Direction direction : Direction.values()) {
				int k = this.method_16379(blockPos.offset(direction), i);
				if (k > j) {
					j = k;
				}

				if (j >= 15) {
					return j;
				}
			}

			return j;
		} else {
			return this.method_16379(blockPos, i);
		}
	}

	@Override
	public Dimension method_16393() {
		return this.field_20877.method_16393();
	}

	@Override
	public int method_16379(BlockPos blockPos, int i) {
		if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() > 30000000) {
			return 15;
		} else if (blockPos.getY() < 0) {
			return 0;
		} else if (blockPos.getY() >= 256) {
			int j = 15 - i;
			if (j < 0) {
				j = 0;
			}

			return j;
		} else {
			int k = (blockPos.getX() >> 4) - this.field_20869;
			int l = (blockPos.getZ() >> 4) - this.field_20870;
			return this.field_20875[k][l].getLightLevel(blockPos, i);
		}
	}

	@Override
	public boolean method_8487(int i, int j, boolean bl) {
		return this.method_19345(i, j);
	}

	@Override
	public boolean method_8555(BlockPos blockPos) {
		return false;
	}

	public boolean method_19345(int i, int j) {
		int k = i - this.field_20869;
		int l = j - this.field_20870;
		return k >= 0 && k < this.field_20875.length && l >= 0 && l < this.field_20875[k].length;
	}

	@Override
	public int method_16372(class_3804.class_3805 arg, int i, int j) {
		throw new RuntimeException("NOT IMPLEMENTED!");
	}

	@Override
	public WorldBorder method_8524() {
		return this.field_20877.method_8524();
	}

	@Override
	public boolean method_16368(@Nullable Entity entity, VoxelShape voxelShape) {
		throw new RuntimeException("This method should never be called here. No entity logic inside Region");
	}

	@Nullable
	@Override
	public PlayerEntity method_16360(double d, double e, double f, double g, Predicate<Entity> predicate) {
		throw new RuntimeException("This method should never be called here. No entity logic inside Region");
	}

	@Override
	public int method_8520() {
		return 0;
	}

	@Override
	public boolean method_8579(BlockPos blockPos) {
		return this.getBlockState(blockPos).isAir();
	}

	@Override
	public int method_16370(LightType lightType, BlockPos blockPos) {
		if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
			int i = (blockPos.getX() >> 4) - this.field_20869;
			int j = (blockPos.getZ() >> 4) - this.field_20870;
			return this.field_20875[i][j].method_17071(lightType, blockPos);
		} else {
			return lightType.defaultValue;
		}
	}

	@Override
	public int method_8576(BlockPos blockPos, Direction direction) {
		return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
	}

	@Override
	public boolean method_16390() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public int method_8483() {
		throw new RuntimeException("Not yet implemented");
	}

	public class class_4246 {
		protected final BlockState field_20878;
		protected final FluidState field_20879;
		private int[] field_20881;

		protected class_4246(World world, BlockPos blockPos) {
			this.field_20878 = world.getBlockState(blockPos);
			this.field_20879 = world.getFluidState(blockPos);
		}

		protected int method_19350(LightType lightType, BlockPos blockPos) {
			if (this.field_20881 == null) {
				this.method_19351(blockPos);
			}

			return this.field_20881[lightType.ordinal()];
		}

		private void method_19351(BlockPos blockPos) {
			this.field_20881 = new int[LightType.values().length];

			for (LightType lightType : LightType.values()) {
				this.field_20881[lightType.ordinal()] = this.method_19352(lightType, blockPos);
			}
		}

		private int method_19352(LightType lightType, BlockPos blockPos) {
			if (lightType == LightType.SKY && !class_4245.this.field_20877.method_16393().isOverworld()) {
				return 0;
			} else if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
				if (this.field_20878.method_16889(class_4245.this, blockPos)) {
					int i = 0;

					for (Direction direction : Direction.values()) {
						int j = class_4245.this.method_16370(lightType, blockPos.offset(direction));
						if (j > i) {
							i = j;
						}

						if (i >= 15) {
							return i;
						}
					}

					return i;
				} else {
					int k = (blockPos.getX() >> 4) - class_4245.this.field_20869;
					int l = (blockPos.getZ() >> 4) - class_4245.this.field_20870;
					return class_4245.this.field_20875[k][l].method_17071(lightType, blockPos);
				}
			} else {
				return lightType.defaultValue;
			}
		}
	}
}
