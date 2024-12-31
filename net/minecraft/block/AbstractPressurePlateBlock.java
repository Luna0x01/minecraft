package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractPressurePlateBlock extends Block {
	protected static final Box field_12563 = new Box(0.0625, 0.0, 0.0625, 0.9375, 0.03125, 0.9375);
	protected static final Box field_12564 = new Box(0.0625, 0.0, 0.0625, 0.9375, 0.0625, 0.9375);
	protected static final Box BOX = new Box(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

	protected AbstractPressurePlateBlock(Material material) {
		this(material, material.getColor());
	}

	protected AbstractPressurePlateBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.setItemGroup(ItemGroup.REDSTONE);
		this.setTickRandomly(true);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		boolean bl = this.getRedstoneOutput(state) > 0;
		return bl ? field_12563 : field_12564;
	}

	@Override
	public int getTickRate(World world) {
		return 20;
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return this.canBePlacedOnBlockBelow(world, pos.down());
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!this.canBePlacedOnBlockBelow(world, pos.down())) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}
	}

	private boolean canBePlacedOnBlockBelow(World world, BlockPos pos) {
		return world.getBlockState(pos).method_11739() || world.getBlockState(pos).getBlock() instanceof FenceBlock;
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(state);
			if (i > 0) {
				this.updatePlateState(world, pos, state, i);
			}
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(state);
			if (i == 0) {
				this.updatePlateState(world, pos, state, i);
			}
		}
	}

	protected void updatePlateState(World world, BlockPos pos, BlockState state, int output) {
		int i = this.getRedstoneOutput(world, pos);
		boolean bl = output > 0;
		boolean bl2 = i > 0;
		if (output != i) {
			state = this.setRedstoneOutput(state, i);
			world.setBlockState(pos, state, 2);
			this.updateNeighbours(world, pos);
			world.onRenderRegionUpdate(pos, pos);
		}

		if (!bl2 && bl) {
			this.method_11550(world, pos);
		} else if (bl2 && !bl) {
			this.method_11549(world, pos);
		}

		if (bl2) {
			world.createAndScheduleBlockTick(new BlockPos(pos), this, this.getTickRate(world));
		}
	}

	protected abstract void method_11549(World world, BlockPos blockPos);

	protected abstract void method_11550(World world, BlockPos blockPos);

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if (this.getRedstoneOutput(state) > 0) {
			this.updateNeighbours(world, pos);
		}

		super.onBreaking(world, pos, state);
	}

	protected void updateNeighbours(World world, BlockPos pos) {
		world.method_13692(pos, this, false);
		world.method_13692(pos.down(), this, false);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return this.getRedstoneOutput(state);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? this.getRedstoneOutput(state) : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	protected abstract int getRedstoneOutput(World world, BlockPos pos);

	protected abstract int getRedstoneOutput(BlockState state);

	protected abstract BlockState setRedstoneOutput(BlockState state, int value);

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
