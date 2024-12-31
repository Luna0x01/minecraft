package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FenceGateBlock extends FacingBlock {
	public static final BooleanProperty OPEN = BooleanProperty.of("open");
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final BooleanProperty IN_WALL = BooleanProperty.of("in_wall");

	public FenceGateBlock(PlanksBlock.WoodType woodType) {
		super(Material.WOOD, woodType.getMaterialColor());
		this.setDefaultState(this.stateManager.getDefaultState().with(OPEN, false).with(POWERED, false).with(IN_WALL, false));
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		Direction.Axis axis = ((Direction)state.get(FACING)).getAxis();
		if (axis == Direction.Axis.Z
				&& (view.getBlockState(pos.west()).getBlock() == Blocks.COBBLESTONE_WALL || view.getBlockState(pos.east()).getBlock() == Blocks.COBBLESTONE_WALL)
			|| axis == Direction.Axis.X
				&& (view.getBlockState(pos.north()).getBlock() == Blocks.COBBLESTONE_WALL || view.getBlockState(pos.south()).getBlock() == Blocks.COBBLESTONE_WALL)) {
			state = state.with(IN_WALL, true);
		}

		return state;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).getBlock().getMaterial().isSolid() ? super.canBePlacedAtPos(world, pos) : false;
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		if ((Boolean)state.get(OPEN)) {
			return null;
		} else {
			Direction.Axis axis = ((Direction)state.get(FACING)).getAxis();
			return axis == Direction.Axis.Z
				? new Box(
					(double)pos.getX(),
					(double)pos.getY(),
					(double)((float)pos.getZ() + 0.375F),
					(double)(pos.getX() + 1),
					(double)((float)pos.getY() + 1.5F),
					(double)((float)pos.getZ() + 0.625F)
				)
				: new Box(
					(double)((float)pos.getX() + 0.375F),
					(double)pos.getY(),
					(double)pos.getZ(),
					(double)((float)pos.getX() + 0.625F),
					(double)((float)pos.getY() + 1.5F),
					(double)(pos.getZ() + 1)
				);
		}
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		Direction.Axis axis = ((Direction)view.getBlockState(pos).get(FACING)).getAxis();
		if (axis == Direction.Axis.Z) {
			this.setBoundingBox(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
		} else {
			this.setBoundingBox(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
		}
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
		return (Boolean)view.getBlockState(pos).get(OPEN);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, entity.getHorizontalDirection()).with(OPEN, false).with(POWERED, false).with(IN_WALL, false);
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if ((Boolean)state.get(OPEN)) {
			state = state.with(OPEN, false);
			world.setBlockState(pos, state, 2);
		} else {
			Direction direction2 = Direction.fromRotation((double)player.yaw);
			if (state.get(FACING) == direction2.getOpposite()) {
				state = state.with(FACING, direction2);
			}

			state = state.with(OPEN, true);
			world.setBlockState(pos, state, 2);
		}

		world.syncWorldEvent(player, state.get(OPEN) ? 1003 : 1006, pos, 0);
		return true;
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(pos);
			if (bl || block.emitsRedstonePower()) {
				if (bl && !(Boolean)state.get(OPEN) && !(Boolean)state.get(POWERED)) {
					world.setBlockState(pos, state.with(OPEN, true).with(POWERED, true), 2);
					world.syncWorldEvent(null, 1003, pos, 0);
				} else if (!bl && (Boolean)state.get(OPEN) && (Boolean)state.get(POWERED)) {
					world.setBlockState(pos, state.with(OPEN, false).with(POWERED, false), 2);
					world.syncWorldEvent(null, 1006, pos, 0);
				} else if (bl != (Boolean)state.get(POWERED)) {
					world.setBlockState(pos, state.with(POWERED, bl), 2);
				}
			}
		}
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return true;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, Direction.fromHorizontal(data)).with(OPEN, (data & 4) != 0).with(POWERED, (data & 8) != 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getHorizontal();
		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		if ((Boolean)state.get(OPEN)) {
			i |= 4;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, OPEN, POWERED, IN_WALL);
	}
}
