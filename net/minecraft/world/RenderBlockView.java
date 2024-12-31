package net.minecraft.world;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.class_4081;
import net.minecraft.class_4101;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;

public interface RenderBlockView extends BlockView {
	boolean method_8579(BlockPos blockPos);

	Biome method_8577(BlockPos blockPos);

	int method_16370(LightType lightType, BlockPos blockPos);

	default boolean method_16394(BlockPos blockPos) {
		if (blockPos.getY() >= this.method_8483()) {
			return this.method_8555(blockPos);
		} else {
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), this.method_8483(), blockPos.getZ());
			if (!this.method_8555(blockPos2)) {
				return false;
			} else {
				for (BlockPos var4 = blockPos2.down(); var4.getY() > blockPos.getY(); var4 = var4.down()) {
					BlockState blockState = this.getBlockState(var4);
					if (blockState.method_16885(this, var4) > 0 && !blockState.getMaterial().isFluid()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	int method_16379(BlockPos blockPos, int i);

	boolean method_8487(int i, int j, boolean bl);

	boolean method_8555(BlockPos blockPos);

	default BlockPos method_16373(class_3804.class_3805 arg, BlockPos blockPos) {
		return new BlockPos(blockPos.getX(), this.method_16372(arg, blockPos.getX(), blockPos.getZ()), blockPos.getZ());
	}

	int method_16372(class_3804.class_3805 arg, int i, int j);

	default float method_16356(BlockPos blockPos) {
		return this.method_16393().getLightLevelToBrightness()[this.method_16358(blockPos)];
	}

	@Nullable
	default PlayerEntity method_16364(Entity entity, double d) {
		return this.method_16361(entity.x, entity.y, entity.z, d, false);
	}

	@Nullable
	default PlayerEntity method_16383(Entity entity, double d) {
		return this.method_16361(entity.x, entity.y, entity.z, d, true);
	}

	@Nullable
	default PlayerEntity method_16361(double d, double e, double f, double g, boolean bl) {
		Predicate<Entity> predicate = bl ? EntityPredicate.field_16704 : EntityPredicate.field_16705;
		return this.method_16360(d, e, f, g, predicate);
	}

	@Nullable
	PlayerEntity method_16360(double d, double e, double f, double g, Predicate<Entity> predicate);

	int method_8520();

	WorldBorder method_8524();

	boolean method_16368(@Nullable Entity entity, VoxelShape voxelShape);

	int method_8576(BlockPos blockPos, Direction direction);

	boolean method_16390();

	int method_8483();

	default boolean method_16371(BlockState blockState, BlockPos blockPos) {
		VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
		return voxelShape.isEmpty() || this.method_16368(null, voxelShape.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
	}

	default boolean method_16382(@Nullable Entity entity, Box box) {
		return this.method_16368(entity, VoxelShapes.method_18049(box));
	}

	default Stream<VoxelShape> method_16378(VoxelShape voxelShape, VoxelShape voxelShape2, boolean bl) {
		int i = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.X)) - 1;
		int j = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.X)) + 1;
		int k = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Y)) - 1;
		int l = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.Y)) + 1;
		int m = MathHelper.floor(voxelShape.getMinimum(Direction.Axis.Z)) - 1;
		int n = MathHelper.ceil(voxelShape.getMaximum(Direction.Axis.Z)) + 1;
		WorldBorder worldBorder = this.method_8524();
		boolean bl2 = worldBorder.getBoundWest() < (double)i
			&& (double)j < worldBorder.getBoundEast()
			&& worldBorder.getBoundNorth() < (double)m
			&& (double)n < worldBorder.getBoundSouth();
		VoxelSet voxelSet = new class_4081(j - i, l - k, n - m);
		Predicate<VoxelShape> predicate = voxelShape2x -> !voxelShape2x.isEmpty() && VoxelShapes.matchesAnywhere(voxelShape, voxelShape2x, BooleanBiFunction.AND);
		Stream<VoxelShape> stream = StreamSupport.stream(BlockPos.Mutable.mutableIterate(i, k, m, j - 1, l - 1, n - 1).spliterator(), false).map(mutable -> {
			int o = mutable.getX();
			int p = mutable.getY();
			int q = mutable.getZ();
			boolean bl3 = o == i || o == j - 1;
			boolean bl4 = p == k || p == l - 1;
			boolean bl5 = q == m || q == n - 1;
			if ((!bl3 || !bl4) && (!bl4 || !bl5) && (!bl5 || !bl3) && this.method_16359(mutable)) {
				VoxelShape voxelShape3;
				if (bl && !bl2 && !worldBorder.contains(mutable)) {
					voxelShape3 = VoxelShapes.matchesAnywhere();
				} else {
					voxelShape3 = this.getBlockState(mutable).getCollisionShape(this, mutable);
				}

				VoxelShape voxelShape4 = voxelShape2.offset((double)(-o), (double)(-p), (double)(-q));
				if (VoxelShapes.matchesAnywhere(voxelShape4, voxelShape3, BooleanBiFunction.AND)) {
					return VoxelShapes.empty();
				} else if (voxelShape3 == VoxelShapes.matchesAnywhere()) {
					voxelSet.method_18022(o - i, p - k, q - m, true, true);
					return VoxelShapes.empty();
				} else {
					return voxelShape3.offset((double)o, (double)p, (double)q);
				}
			} else {
				return VoxelShapes.empty();
			}
		}).filter(predicate);
		return Stream.concat(stream, Stream.generate(() -> new class_4101(voxelSet, i, k, m)).limit(1L).filter(predicate));
	}

	default Stream<VoxelShape> method_16365(@Nullable Entity entity, Box box, double d, double e, double f) {
		return this.method_16367(entity, box, Collections.emptySet(), d, e, f);
	}

	default Stream<VoxelShape> method_16367(@Nullable Entity entity, Box box, Set<Entity> set, double d, double e, double f) {
		double g = 1.0E-7;
		VoxelShape voxelShape = VoxelShapes.method_18049(box);
		VoxelShape voxelShape2 = VoxelShapes.method_18049(box.offset(d > 0.0 ? -1.0E-7 : 1.0E-7, e > 0.0 ? -1.0E-7 : 1.0E-7, f > 0.0 ? -1.0E-7 : 1.0E-7));
		VoxelShape voxelShape3 = VoxelShapes.combine(VoxelShapes.method_18049(box.stretch(d, e, f).expand(1.0E-7)), voxelShape2, BooleanBiFunction.ONLY_FIRST);
		return this.method_16369(entity, voxelShape3, voxelShape, set);
	}

	default Stream<VoxelShape> method_16384(@Nullable Entity entity, Box box) {
		return this.method_16369(entity, VoxelShapes.method_18049(box), VoxelShapes.empty(), Collections.emptySet());
	}

	default Stream<VoxelShape> method_16369(@Nullable Entity entity, VoxelShape voxelShape, VoxelShape voxelShape2, Set<Entity> set) {
		boolean bl = entity != null && entity.isOutsideWorldBorder();
		boolean bl2 = entity != null && this.method_16392(entity);
		if (entity != null && bl == bl2) {
			entity.setOutsideWorldBorder(!bl2);
		}

		return this.method_16378(voxelShape, voxelShape2, bl2);
	}

	default boolean method_16392(Entity entity) {
		WorldBorder worldBorder = this.method_8524();
		double d = worldBorder.getBoundWest();
		double e = worldBorder.getBoundNorth();
		double f = worldBorder.getBoundEast();
		double g = worldBorder.getBoundSouth();
		if (entity.isOutsideWorldBorder()) {
			d++;
			e++;
			f--;
			g--;
		} else {
			d--;
			e--;
			f++;
			g++;
		}

		return entity.x > d && entity.x < f && entity.z > e && entity.z < g;
	}

	default boolean method_16366(@Nullable Entity entity, Box box, Set<Entity> set) {
		return this.method_16369(entity, VoxelShapes.method_18049(box), VoxelShapes.empty(), set).allMatch(VoxelShape::isEmpty);
	}

	default boolean method_16387(@Nullable Entity entity, Box box) {
		return this.method_16366(entity, box, Collections.emptySet());
	}

	default boolean method_16357(BlockPos blockPos) {
		return this.getFluidState(blockPos).matches(FluidTags.WATER);
	}

	default boolean method_16388(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						BlockState blockState = this.getBlockState(pooled.setPosition(o, p, q));
						if (!blockState.getFluidState().isEmpty()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	default int method_16358(BlockPos blockPos) {
		return this.method_16389(blockPos, this.method_8520());
	}

	default int method_16389(BlockPos blockPos, int i) {
		if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
			return 15;
		} else if (this.getBlockState(blockPos).method_16889(this, blockPos)) {
			int j = this.method_16379(blockPos.up(), i);
			int k = this.method_16379(blockPos.east(), i);
			int l = this.method_16379(blockPos.west(), i);
			int m = this.method_16379(blockPos.south(), i);
			int n = this.method_16379(blockPos.north(), i);
			if (k > j) {
				j = k;
			}

			if (l > j) {
				j = l;
			}

			if (m > j) {
				j = m;
			}

			if (n > j) {
				j = n;
			}

			return j;
		} else {
			return this.method_16379(blockPos, i);
		}
	}

	default boolean method_16359(BlockPos blockPos) {
		return this.method_16386(blockPos, true);
	}

	default boolean method_16386(BlockPos blockPos, boolean bl) {
		return this.method_8487(blockPos.getX() >> 4, blockPos.getZ() >> 4, bl);
	}

	default boolean method_16391(BlockPos blockPos, int i) {
		return this.method_16380(blockPos, i, true);
	}

	default boolean method_16380(BlockPos blockPos, int i, boolean bl) {
		return this.method_16362(blockPos.getX() - i, blockPos.getY() - i, blockPos.getZ() - i, blockPos.getX() + i, blockPos.getY() + i, blockPos.getZ() + i, bl);
	}

	default boolean method_16385(BlockPos blockPos, BlockPos blockPos2) {
		return this.method_16381(blockPos, blockPos2, true);
	}

	default boolean method_16381(BlockPos blockPos, BlockPos blockPos2, boolean bl) {
		return this.method_16362(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), bl);
	}

	default boolean method_16374(BlockBox blockBox) {
		return this.method_16375(blockBox, true);
	}

	default boolean method_16375(BlockBox blockBox, boolean bl) {
		return this.method_16362(blockBox.minX, blockBox.minY, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ, bl);
	}

	default boolean method_16362(int i, int j, int k, int l, int m, int n, boolean bl) {
		if (m >= 0 && j < 256) {
			i >>= 4;
			k >>= 4;
			l >>= 4;
			n >>= 4;

			for (int o = i; o <= l; o++) {
				for (int p = k; p <= n; p++) {
					if (!this.method_8487(o, p, bl)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	Dimension method_16393();
}
