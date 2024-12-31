package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LadderBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);

	protected LadderBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		this.setBoundingBox(world, pos);
		return super.getCollisionBox(world, pos, state);
	}

	@Override
	public Box getSelectionBox(World world, BlockPos pos) {
		this.setBoundingBox(world, pos);
		return super.getSelectionBox(world, pos);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		if (blockState.getBlock() == this) {
			float f = 0.125F;
			switch ((Direction)blockState.get(FACING)) {
				case NORTH:
					this.setBoundingBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
					break;
				case SOUTH:
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
					break;
				case WEST:
					this.setBoundingBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
					break;
				case EAST:
				default:
					this.setBoundingBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
			}
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
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		if (world.getBlockState(pos.west()).getBlock().isFullCube()) {
			return true;
		} else if (world.getBlockState(pos.east()).getBlock().isFullCube()) {
			return true;
		} else {
			return world.getBlockState(pos.north()).getBlock().isFullCube() ? true : world.getBlockState(pos.south()).getBlock().isFullCube();
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		if (dir.getAxis().isHorizontal() && this.isOppositeFull(world, pos, dir)) {
			return this.getDefaultState().with(FACING, dir);
		} else {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (this.isOppositeFull(world, pos, direction)) {
					return this.getDefaultState().with(FACING, direction);
				}
			}

			return this.getDefaultState();
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		Direction direction = state.get(FACING);
		if (!this.isOppositeFull(world, pos, direction)) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}

		super.neighborUpdate(world, pos, state, block);
	}

	protected boolean isOppositeFull(World world, BlockPos pos, Direction dir) {
		return world.getBlockState(pos.offset(dir.getOpposite())).getBlock().isFullCube();
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.getById(data);
		if (direction.getAxis() == Direction.Axis.Y) {
			direction = Direction.NORTH;
		}

		return this.getDefaultState().with(FACING, direction);
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}
}
