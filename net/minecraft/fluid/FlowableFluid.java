package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class FlowableFluid extends Fluid {
	public static final BooleanProperty FALLING = Properties.FALLING;
	public static final IntProperty LEVEL = Properties.LEVEL_1_8;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> field_19482 = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200) {
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});

	@Override
	protected void method_17780(StateManager.Builder<Fluid, FluidState> builder) {
		builder.method_16928(FALLING);
	}

	@Override
	public Vec3d method_17779(RenderBlockView renderBlockView, BlockPos blockPos, FluidState fluidState) {
		double d = 0.0;
		double e = 0.0;

		Vec3d var27;
		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				pooled.set(blockPos).move(direction);
				FluidState fluidState2 = renderBlockView.getFluidState(pooled);
				if (this.isEmptyOrThis(fluidState2)) {
					float f = fluidState2.method_17810();
					float g = 0.0F;
					if (f == 0.0F) {
						if (!renderBlockView.getBlockState(pooled).getMaterial().blocksMovement()) {
							FluidState fluidState3 = renderBlockView.getFluidState(pooled.down());
							if (this.isEmptyOrThis(fluidState3)) {
								f = fluidState3.method_17810();
								if (f > 0.0F) {
									g = fluidState.method_17810() - (f - 0.8888889F);
								}
							}
						}
					} else if (f > 0.0F) {
						g = fluidState.method_17810() - f;
					}

					if (g != 0.0F) {
						d += (double)((float)direction.getOffsetX() * g);
						e += (double)((float)direction.getOffsetZ() * g);
					}
				}
			}

			Vec3d vec3d = new Vec3d(d, 0.0, e);
			if ((Boolean)fluidState.getProperty(FALLING)) {
				for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
					pooled.set(blockPos).move(direction2);
					if (this.method_17749(renderBlockView, pooled, direction2) || this.method_17749(renderBlockView, pooled.up(), direction2)) {
						vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
						break;
					}
				}
			}

			var27 = vec3d.normalize();
		}

		return var27;
	}

	private boolean isEmptyOrThis(FluidState state) {
		return state.isEmpty() || state.getFluid().method_17781(this);
	}

	protected boolean method_17749(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.getBlockState(blockPos);
		Block block = blockState.getBlock();
		FluidState fluidState = blockView.getFluidState(blockPos);
		if (fluidState.getFluid().method_17781(this)) {
			return false;
		} else if (direction == Direction.UP) {
			return true;
		} else if (blockState.getMaterial() == Material.ICE) {
			return false;
		} else {
			boolean bl = Block.method_14309(block) || block instanceof StairsBlock;
			return !bl && blockState.getRenderLayer(blockView, blockPos, direction) == BlockRenderLayer.SOLID;
		}
	}

	protected void method_17753(IWorld iWorld, BlockPos blockPos, FluidState fluidState) {
		if (!fluidState.isEmpty()) {
			BlockState blockState = iWorld.getBlockState(blockPos);
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState2 = iWorld.getBlockState(blockPos2);
			FluidState fluidState2 = this.method_17758(iWorld, blockPos2, blockState2);
			if (this.method_17748(iWorld, blockPos, blockState, Direction.DOWN, blockPos2, blockState2, iWorld.getFluidState(blockPos2), fluidState2.getFluid())) {
				this.method_17752(iWorld, blockPos2, blockState2, Direction.DOWN, fluidState2);
				if (this.method_17755(iWorld, blockPos) >= 3) {
					this.method_17754(iWorld, blockPos, fluidState, blockState);
				}
			} else if (fluidState.isStill() || !this.method_17745(iWorld, fluidState2.getFluid(), blockPos, blockState, blockPos2, blockState2)) {
				this.method_17754(iWorld, blockPos, fluidState, blockState);
			}
		}
	}

	private void method_17754(IWorld iWorld, BlockPos blockPos, FluidState fluidState, BlockState blockState) {
		int i = fluidState.method_17811() - this.method_17767(iWorld);
		if ((Boolean)fluidState.getProperty(FALLING)) {
			i = 7;
		}

		if (i > 0) {
			Map<Direction, FluidState> map = this.method_17766(iWorld, blockPos, blockState);

			for (Entry<Direction, FluidState> entry : map.entrySet()) {
				Direction direction = (Direction)entry.getKey();
				FluidState fluidState2 = (FluidState)entry.getValue();
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState2 = iWorld.getBlockState(blockPos2);
				if (this.method_17748(iWorld, blockPos, blockState, direction, blockPos2, blockState2, iWorld.getFluidState(blockPos2), fluidState2.getFluid())) {
					this.method_17752(iWorld, blockPos2, blockState2, direction, fluidState2);
				}
			}
		}
	}

	protected FluidState method_17758(RenderBlockView renderBlockView, BlockPos blockPos, BlockState blockState) {
		int i = 0;
		int j = 0;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			BlockState blockState2 = renderBlockView.getBlockState(blockPos2);
			FluidState fluidState = blockState2.getFluidState();
			if (fluidState.getFluid().method_17781(this) && this.method_17762(direction, renderBlockView, blockPos, blockState, blockPos2, blockState2)) {
				if (fluidState.isStill()) {
					j++;
				}

				i = Math.max(i, fluidState.method_17811());
			}
		}

		if (this.method_17771() && j >= 2) {
			BlockState blockState3 = renderBlockView.getBlockState(blockPos.down());
			FluidState fluidState2 = blockState3.getFluidState();
			if (blockState3.getMaterial().isSolid() || this.method_17773(fluidState2)) {
				return this.getStill(false);
			}
		}

		BlockPos blockPos3 = blockPos.up();
		BlockState blockState4 = renderBlockView.getBlockState(blockPos3);
		FluidState fluidState3 = blockState4.getFluidState();
		if (!fluidState3.isEmpty()
			&& fluidState3.getFluid().method_17781(this)
			&& this.method_17762(Direction.UP, renderBlockView, blockPos, blockState, blockPos3, blockState4)) {
			return this.method_17744(8, true);
		} else {
			int k = i - this.method_17767(renderBlockView);
			return k <= 0 ? Fluids.EMPTY.getDefaultState() : this.method_17744(k, false);
		}
	}

	private boolean method_17762(Direction direction, BlockView blockView, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap2;
		if (!blockState.getBlock().hasStats() && !blockState2.getBlock().hasStats()) {
			object2ByteLinkedOpenHashMap2 = (Object2ByteLinkedOpenHashMap<Block.NeighborGroup>)field_19482.get();
		} else {
			object2ByteLinkedOpenHashMap2 = null;
		}

		Block.NeighborGroup neighborGroup;
		if (object2ByteLinkedOpenHashMap2 != null) {
			neighborGroup = new Block.NeighborGroup(blockState, blockState2, direction);
			byte b = object2ByteLinkedOpenHashMap2.getAndMoveToFirst(neighborGroup);
			if (b != 127) {
				return b != 0;
			}
		} else {
			neighborGroup = null;
		}

		VoxelShape voxelShape = blockState.getCollisionShape(blockView, blockPos);
		VoxelShape voxelShape2 = blockState2.getCollisionShape(blockView, blockPos2);
		boolean bl = !VoxelShapes.method_18060(voxelShape, voxelShape2, direction);
		if (object2ByteLinkedOpenHashMap2 != null) {
			if (object2ByteLinkedOpenHashMap2.size() == 200) {
				object2ByteLinkedOpenHashMap2.removeLastByte();
			}

			object2ByteLinkedOpenHashMap2.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
		}

		return bl;
	}

	public abstract Fluid method_17768();

	public FluidState method_17744(int i, boolean bl) {
		return this.method_17768().getDefaultState().withProperty(LEVEL, Integer.valueOf(i)).withProperty(FALLING, Boolean.valueOf(bl));
	}

	public abstract Fluid getStill();

	public FluidState getStill(boolean falling) {
		return this.getStill().getDefaultState().withProperty(FALLING, Boolean.valueOf(falling));
	}

	protected abstract boolean method_17771();

	protected void method_17752(IWorld iWorld, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (blockState.getBlock() instanceof FluidFillable) {
			((FluidFillable)blockState.getBlock()).tryFillWithFluid(iWorld, blockPos, blockState, fluidState);
		} else {
			if (!blockState.isAir()) {
				this.method_17751(iWorld, blockPos, blockState);
			}

			iWorld.setBlockState(blockPos, fluidState.method_17813(), 3);
		}
	}

	protected abstract void method_17751(IWorld iWorld, BlockPos blockPos, BlockState blockState);

	private static short method_17761(BlockPos blockPos, BlockPos blockPos2) {
		int i = blockPos2.getX() - blockPos.getX();
		int j = blockPos2.getZ() - blockPos.getZ();
		return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
	}

	protected int method_17757(
		RenderBlockView renderBlockView,
		BlockPos blockPos,
		int i,
		Direction direction,
		BlockState blockState,
		BlockPos blockPos2,
		Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap,
		Short2BooleanMap short2BooleanMap
	) {
		int j = 1000;

		for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
			if (direction2 != direction) {
				BlockPos blockPos3 = blockPos.offset(direction2);
				short s = method_17761(blockPos2, blockPos3);
				Pair<BlockState, FluidState> pair = (Pair<BlockState, FluidState>)short2ObjectMap.computeIfAbsent(s, ix -> {
					BlockState blockStatex = renderBlockView.getBlockState(blockPos3);
					return Pair.of(blockStatex, blockStatex.getFluidState());
				});
				BlockState blockState2 = (BlockState)pair.getFirst();
				FluidState fluidState = (FluidState)pair.getSecond();
				if (this.method_17746(renderBlockView, this.method_17768(), blockPos, blockState, direction2, blockPos3, blockState2, fluidState)) {
					boolean bl = short2BooleanMap.computeIfAbsent(s, ix -> {
						BlockPos blockPos2x = blockPos3.down();
						BlockState blockState2x = renderBlockView.getBlockState(blockPos2x);
						return this.method_17745(renderBlockView, this.method_17768(), blockPos3, blockState2, blockPos2x, blockState2x);
					});
					if (bl) {
						return i;
					}

					if (i < this.method_17764(renderBlockView)) {
						int k = this.method_17757(renderBlockView, blockPos3, i + 1, direction2.getOpposite(), blockState2, blockPos2, short2ObjectMap, short2BooleanMap);
						if (k < j) {
							j = k;
						}
					}
				}
			}
		}

		return j;
	}

	private boolean method_17745(BlockView blockView, Fluid fluid, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
		if (!this.method_17762(Direction.DOWN, blockView, blockPos, blockState, blockPos2, blockState2)) {
			return false;
		} else {
			return blockState2.getFluidState().getFluid().method_17781(this) ? true : this.method_17747(blockView, blockPos2, blockState2, fluid);
		}
	}

	private boolean method_17746(
		BlockView blockView,
		Fluid fluid,
		BlockPos blockPos,
		BlockState blockState,
		Direction direction,
		BlockPos blockPos2,
		BlockState blockState2,
		FluidState fluidState
	) {
		return !this.method_17773(fluidState)
			&& this.method_17762(direction, blockView, blockPos, blockState, blockPos2, blockState2)
			&& this.method_17747(blockView, blockPos2, blockState2, fluid);
	}

	private boolean method_17773(FluidState fluidState) {
		return fluidState.getFluid().method_17781(this) && fluidState.isStill();
	}

	protected abstract int method_17764(RenderBlockView renderBlockView);

	private int method_17755(RenderBlockView renderBlockView, BlockPos blockPos) {
		int i = 0;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			FluidState fluidState = renderBlockView.getFluidState(blockPos2);
			if (this.method_17773(fluidState)) {
				i++;
			}
		}

		return i;
	}

	protected Map<Direction, FluidState> method_17766(RenderBlockView renderBlockView, BlockPos blockPos, BlockState blockState) {
		int i = 1000;
		Map<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
		Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap = new Short2ObjectOpenHashMap();
		Short2BooleanMap short2BooleanMap = new Short2BooleanOpenHashMap();

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			short s = method_17761(blockPos, blockPos2);
			Pair<BlockState, FluidState> pair = (Pair<BlockState, FluidState>)short2ObjectMap.computeIfAbsent(s, ix -> {
				BlockState blockStatex = renderBlockView.getBlockState(blockPos2);
				return Pair.of(blockStatex, blockStatex.getFluidState());
			});
			BlockState blockState2 = (BlockState)pair.getFirst();
			FluidState fluidState = (FluidState)pair.getSecond();
			FluidState fluidState2 = this.method_17758(renderBlockView, blockPos2, blockState2);
			if (this.method_17746(renderBlockView, fluidState2.getFluid(), blockPos, blockState, direction, blockPos2, blockState2, fluidState)) {
				BlockPos blockPos3 = blockPos2.down();
				boolean bl = short2BooleanMap.computeIfAbsent(s, ix -> {
					BlockState blockState2x = renderBlockView.getBlockState(blockPos3);
					return this.method_17745(renderBlockView, this.method_17768(), blockPos2, blockState2, blockPos3, blockState2x);
				});
				int j;
				if (bl) {
					j = 0;
				} else {
					j = this.method_17757(renderBlockView, blockPos2, 1, direction.getOpposite(), blockState2, blockPos, short2ObjectMap, short2BooleanMap);
				}

				if (j < i) {
					map.clear();
				}

				if (j <= i) {
					map.put(direction, fluidState2);
					i = j;
				}
			}
		}

		return map;
	}

	private boolean method_17747(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		Block block = blockState.getBlock();
		if (block instanceof FluidFillable) {
			return ((FluidFillable)block).canFillWithFluid(blockView, blockPos, blockState, fluid);
		} else if (!(block instanceof DoorBlock) && block != Blocks.SIGN && block != Blocks.LADDER && block != Blocks.SUGAR_CANE && block != Blocks.BUBBLE_COLUMN) {
			Material material = blockState.getMaterial();
			return material != Material.PORTAL && material != Material.CAVE_AIR && material != Material.field_19498 && material != Material.field_19499
				? !material.blocksMovement()
				: false;
		} else {
			return false;
		}
	}

	protected boolean method_17748(
		BlockView blockView,
		BlockPos blockPos,
		BlockState blockState,
		Direction direction,
		BlockPos blockPos2,
		BlockState blockState2,
		FluidState fluidState,
		Fluid fluid
	) {
		return fluidState.method_17804(fluid, direction)
			&& this.method_17762(direction, blockView, blockPos, blockState, blockPos2, blockState2)
			&& this.method_17747(blockView, blockPos2, blockState2, fluid);
	}

	protected abstract int method_17767(RenderBlockView renderBlockView);

	protected int method_17750(World world, FluidState fluidState, FluidState fluidState2) {
		return this.method_17778(world);
	}

	@Override
	public void method_17776(World world, BlockPos blockPos, FluidState fluidState) {
		if (!fluidState.isStill()) {
			FluidState fluidState2 = this.method_17758(world, blockPos, world.getBlockState(blockPos));
			int i = this.method_17750(world, fluidState, fluidState2);
			if (fluidState2.isEmpty()) {
				fluidState = fluidState2;
				world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
			} else if (!fluidState2.equals(fluidState)) {
				fluidState = fluidState2;
				BlockState blockState = fluidState2.method_17813();
				world.setBlockState(blockPos, blockState, 2);
				world.method_16340().schedule(blockPos, fluidState2.getFluid(), i);
				world.updateNeighborsAlways(blockPos, blockState.getBlock());
			}
		}

		this.method_17753(world, blockPos, fluidState);
	}

	protected static int method_17769(FluidState fluidState) {
		return fluidState.isStill() ? 0 : 8 - Math.min(fluidState.method_17811(), 8) + (fluidState.getProperty(FALLING) ? 8 : 0);
	}

	@Override
	public float method_17782(FluidState fluidState) {
		return (float)fluidState.method_17811() / 9.0F;
	}
}
