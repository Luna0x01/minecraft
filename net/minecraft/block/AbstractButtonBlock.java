package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractButtonBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing");
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	private final boolean wooden;

	protected AbstractButtonBlock(boolean bl) {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.REDSTONE);
		this.wooden = bl;
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public int getTickRate(World world) {
		return this.wooden ? 30 : 20;
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
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return canHoldButton(world, pos, direction.getOpposite());
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (canHoldButton(world, pos, direction)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean canHoldButton(World world, BlockPos pos, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		return dir == Direction.DOWN ? World.isOpaque(world, blockPos) : world.getBlockState(blockPos).getBlock().isFullCube();
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return canHoldButton(world, pos, dir.getOpposite())
			? this.getDefaultState().with(FACING, dir).with(POWERED, false)
			: this.getDefaultState().with(FACING, Direction.DOWN).with(POWERED, false);
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (this.isButtonPlacementValid(world, pos, state) && !canHoldButton(world, pos, ((Direction)state.get(FACING)).getOpposite())) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}
	}

	private boolean isButtonPlacementValid(World world, BlockPos pos, BlockState state) {
		if (this.canBePlacedAtPos(world, pos)) {
			return true;
		} else {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
			return false;
		}
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.setButtonBoundingBox(view.getBlockState(pos));
	}

	private void setButtonBoundingBox(BlockState state) {
		Direction direction = state.get(FACING);
		boolean bl = (Boolean)state.get(POWERED);
		float f = 0.25F;
		float g = 0.375F;
		float h = (float)(bl ? 1 : 2) / 16.0F;
		float i = 0.125F;
		float j = 0.1875F;
		switch (direction) {
			case EAST:
				this.setBoundingBox(0.0F, 0.375F, 0.3125F, h, 0.625F, 0.6875F);
				break;
			case WEST:
				this.setBoundingBox(1.0F - h, 0.375F, 0.3125F, 1.0F, 0.625F, 0.6875F);
				break;
			case SOUTH:
				this.setBoundingBox(0.3125F, 0.375F, 0.0F, 0.6875F, 0.625F, h);
				break;
			case NORTH:
				this.setBoundingBox(0.3125F, 0.375F, 1.0F - h, 0.6875F, 0.625F, 1.0F);
				break;
			case UP:
				this.setBoundingBox(0.3125F, 0.0F, 0.375F, 0.6875F, 0.0F + h, 0.625F);
				break;
			case DOWN:
				this.setBoundingBox(0.3125F, 1.0F - h, 0.375F, 0.6875F, 1.0F, 0.625F);
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if ((Boolean)state.get(POWERED)) {
			return true;
		} else {
			world.setBlockState(pos, state.with(POWERED, true), 3);
			world.onRenderRegionUpdate(pos, pos);
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
			this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
			world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
			return true;
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if ((Boolean)state.get(POWERED)) {
			this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
		}

		super.onBreaking(world, pos, state);
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return state.get(FACING) == facing ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower() {
		return true;
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if ((Boolean)state.get(POWERED)) {
				if (this.wooden) {
					this.onPossibleArrowCollision(world, pos, state);
				} else {
					world.setBlockState(pos, state.with(POWERED, false));
					this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
					world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
					world.onRenderRegionUpdate(pos, pos);
				}
			}
		}
	}

	@Override
	public void setBlockItemBounds() {
		float f = 0.1875F;
		float g = 0.125F;
		float h = 0.125F;
		this.setBoundingBox(0.5F - f, 0.5F - g, 0.5F - h, 0.5F + f, 0.5F + g, 0.5F + h);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient) {
			if (this.wooden) {
				if (!(Boolean)state.get(POWERED)) {
					this.onPossibleArrowCollision(world, pos, state);
				}
			}
		}
	}

	private void onPossibleArrowCollision(World world, BlockPos pos, BlockState state) {
		this.setButtonBoundingBox(state);
		List<? extends Entity> list = world.getEntitiesInBox(
			AbstractArrowEntity.class,
			new Box(
				(double)pos.getX() + this.boundingBoxMinX,
				(double)pos.getY() + this.boundingBoxMinY,
				(double)pos.getZ() + this.boundingBoxMinZ,
				(double)pos.getX() + this.boundingBoxMaxX,
				(double)pos.getY() + this.boundingBoxMaxY,
				(double)pos.getZ() + this.boundingBoxMaxZ
			)
		);
		boolean bl = !list.isEmpty();
		boolean bl2 = (Boolean)state.get(POWERED);
		if (bl && !bl2) {
			world.setBlockState(pos, state.with(POWERED, true));
			this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
			world.onRenderRegionUpdate(pos, pos);
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
		}

		if (!bl && bl2) {
			world.setBlockState(pos, state.with(POWERED, false));
			this.updateNeighborsAfterActivation(world, pos, state.get(FACING));
			world.onRenderRegionUpdate(pos, pos);
			world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
		}

		if (bl) {
			world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
		}
	}

	private void updateNeighborsAfterActivation(World world, BlockPos pos, Direction dir) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.offset(dir.getOpposite()), this);
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction;
		switch (data & 7) {
			case 0:
				direction = Direction.DOWN;
				break;
			case 1:
				direction = Direction.EAST;
				break;
			case 2:
				direction = Direction.WEST;
				break;
			case 3:
				direction = Direction.SOUTH;
				break;
			case 4:
				direction = Direction.NORTH;
				break;
			case 5:
			default:
				direction = Direction.UP;
		}

		return this.getDefaultState().with(FACING, direction).with(POWERED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i;
		switch ((Direction)state.get(FACING)) {
			case EAST:
				i = 1;
				break;
			case WEST:
				i = 2;
				break;
			case SOUTH:
				i = 3;
				break;
			case NORTH:
				i = 4;
				break;
			case UP:
			default:
				i = 5;
				break;
			case DOWN:
				i = 0;
		}

		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, POWERED);
	}
}
