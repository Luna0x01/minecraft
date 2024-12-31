package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractPressurePlateBlock extends Block {
	protected AbstractPressurePlateBlock(Material material) {
		this(material, material.getColor());
	}

	protected AbstractPressurePlateBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.setItemGroup(ItemGroup.REDSTONE);
		this.setTickRandomly(true);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.setBoundingBox(view.getBlockState(pos));
	}

	protected void setBoundingBox(BlockState state) {
		boolean bl = this.getRedstoneOutput(state) > 0;
		float f = 0.0625F;
		if (bl) {
			this.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
		} else {
			this.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
		}
	}

	@Override
	public int getTickRate(World world) {
		return 20;
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
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
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!this.canBePlacedOnBlockBelow(world, pos.down())) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}
	}

	private boolean canBePlacedOnBlockBelow(World world, BlockPos pos) {
		return World.isOpaque(world, pos) || world.getBlockState(pos).getBlock() instanceof FenceBlock;
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(state);
			if (i > 0) {
				this.checkRedstoneOutput(world, pos, state, i);
			}
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(state);
			if (i == 0) {
				this.checkRedstoneOutput(world, pos, state, i);
			}
		}
	}

	protected void checkRedstoneOutput(World world, BlockPos pos, BlockState state, int redstoneOutput) {
		int i = this.getRedstoneOutput(world, pos);
		boolean bl = redstoneOutput > 0;
		boolean bl2 = i > 0;
		if (redstoneOutput != i) {
			state = this.setRedstoneOutput(state, i);
			world.setBlockState(pos, state, 2);
			this.updateNeighbours(world, pos);
			world.onRenderRegionUpdate(pos, pos);
		}

		if (!bl2 && bl) {
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
		} else if (bl2 && !bl) {
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
		}

		if (bl2) {
			world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
		}
	}

	protected Box getPlateHitBox(BlockPos pos) {
		float f = 0.125F;
		return new Box(
			(double)((float)pos.getX() + 0.125F),
			(double)pos.getY(),
			(double)((float)pos.getZ() + 0.125F),
			(double)((float)(pos.getX() + 1) - 0.125F),
			(double)pos.getY() + 0.25,
			(double)((float)(pos.getZ() + 1) - 0.125F)
		);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if (this.getRedstoneOutput(state) > 0) {
			this.updateNeighbours(world, pos);
		}

		super.onBreaking(world, pos, state);
	}

	protected void updateNeighbours(World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.down(), this);
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return this.getRedstoneOutput(state);
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return facing == Direction.UP ? this.getRedstoneOutput(state) : 0;
	}

	@Override
	public boolean emitsRedstonePower() {
		return true;
	}

	@Override
	public void setBlockItemBounds() {
		float f = 0.5F;
		float g = 0.125F;
		float h = 0.5F;
		this.setBoundingBox(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
	}

	@Override
	public int getPistonInteractionType() {
		return 1;
	}

	protected abstract int getRedstoneOutput(World world, BlockPos pos);

	protected abstract int getRedstoneOutput(BlockState state);

	protected abstract BlockState setRedstoneOutput(BlockState state, int value);
}
