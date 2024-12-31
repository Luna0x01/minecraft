package net.minecraft.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class DoubleBlockProperties {
	public static <S extends BlockEntity> DoubleBlockProperties.PropertySource<S> toPropertySource(
		BlockEntityType<S> blockEntityType,
		Function<BlockState, DoubleBlockProperties.Type> function,
		Function<BlockState, Direction> function2,
		DirectionProperty directionProperty,
		BlockState blockState,
		IWorld iWorld,
		BlockPos blockPos,
		BiPredicate<IWorld, BlockPos> biPredicate
	) {
		S blockEntity = blockEntityType.get(iWorld, blockPos);
		if (blockEntity == null) {
			return DoubleBlockProperties.PropertyRetriever::getFallback;
		} else if (biPredicate.test(iWorld, blockPos)) {
			return DoubleBlockProperties.PropertyRetriever::getFallback;
		} else {
			DoubleBlockProperties.Type type = (DoubleBlockProperties.Type)function.apply(blockState);
			boolean bl = type == DoubleBlockProperties.Type.field_21783;
			boolean bl2 = type == DoubleBlockProperties.Type.field_21784;
			if (bl) {
				return new DoubleBlockProperties.PropertySource.Single<>(blockEntity);
			} else {
				BlockPos blockPos2 = blockPos.offset((Direction)function2.apply(blockState));
				BlockState blockState2 = iWorld.getBlockState(blockPos2);
				if (blockState2.getBlock() == blockState.getBlock()) {
					DoubleBlockProperties.Type type2 = (DoubleBlockProperties.Type)function.apply(blockState2);
					if (type2 != DoubleBlockProperties.Type.field_21783 && type != type2 && blockState2.get(directionProperty) == blockState.get(directionProperty)) {
						if (biPredicate.test(iWorld, blockPos2)) {
							return DoubleBlockProperties.PropertyRetriever::getFallback;
						}

						S blockEntity2 = blockEntityType.get(iWorld, blockPos2);
						if (blockEntity2 != null) {
							S blockEntity3 = bl2 ? blockEntity : blockEntity2;
							S blockEntity4 = bl2 ? blockEntity2 : blockEntity;
							return new DoubleBlockProperties.PropertySource.Pair<>(blockEntity3, blockEntity4);
						}
					}
				}

				return new DoubleBlockProperties.PropertySource.Single<>(blockEntity);
			}
		}
	}

	public interface PropertyRetriever<S, T> {
		T getFromBoth(S object, S object2);

		T getFrom(S object);

		T getFallback();
	}

	public interface PropertySource<S> {
		<T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever);

		public static final class Pair<S> implements DoubleBlockProperties.PropertySource<S> {
			private final S first;
			private final S second;

			public Pair(S object, S object2) {
				this.first = object;
				this.second = object2;
			}

			@Override
			public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
				return propertyRetriever.getFromBoth(this.first, this.second);
			}
		}

		public static final class Single<S> implements DoubleBlockProperties.PropertySource<S> {
			private final S single;

			public Single(S object) {
				this.single = object;
			}

			@Override
			public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
				return propertyRetriever.getFrom(this.single);
			}
		}
	}

	public static enum Type {
		field_21783,
		field_21784,
		field_21785;
	}
}
