package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TrapdoorBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	public static final BooleanProperty OPEN = BooleanProperty.of("open");
	public static final EnumProperty<TrapdoorBlock.TrapdoorType> HALF = EnumProperty.of("half", TrapdoorBlock.TrapdoorType.class);

	protected TrapdoorBlock(Material material) {
		super(material);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false).with(HALF, TrapdoorBlock.TrapdoorType.BOTTOM));
		float f = 0.5F;
		float g = 1.0F;
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setItemGroup(ItemGroup.REDSTONE);
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
		return !(Boolean)view.getBlockState(pos).get(OPEN);
	}

	@Override
	public Box getSelectionBox(World world, BlockPos pos) {
		this.setBoundingBox(world, pos);
		return super.getSelectionBox(world, pos);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		this.setBoundingBox(world, pos);
		return super.getCollisionBox(world, pos, state);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.setTrapdoorBoundingBox(view.getBlockState(pos));
	}

	@Override
	public void setBlockItemBounds() {
		float f = 0.1875F;
		this.setBoundingBox(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
	}

	public void setTrapdoorBoundingBox(BlockState state) {
		if (state.getBlock() == this) {
			boolean bl = state.get(HALF) == TrapdoorBlock.TrapdoorType.TOP;
			Boolean boolean_ = state.get(OPEN);
			Direction direction = state.get(FACING);
			float f = 0.1875F;
			if (bl) {
				this.setBoundingBox(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
			} else {
				this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
			}

			if (boolean_) {
				if (direction == Direction.NORTH) {
					this.setBoundingBox(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
				}

				if (direction == Direction.SOUTH) {
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
				}

				if (direction == Direction.WEST) {
					this.setBoundingBox(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				}

				if (direction == Direction.EAST) {
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
				}
			}
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (this.material == Material.IRON) {
			return true;
		} else {
			state = state.withDefaultValue(OPEN);
			world.setBlockState(pos, state, 2);
			world.syncWorldEvent(player, state.get(OPEN) ? 1003 : 1006, pos, 0);
			return true;
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!world.isClient) {
			BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
			if (!canBePlacedAdjacent(world.getBlockState(blockPos).getBlock())) {
				world.setAir(pos);
				this.dropAsItem(world, pos, state, 0);
			} else {
				boolean bl = world.isReceivingRedstonePower(pos);
				if (bl || block.emitsRedstonePower()) {
					boolean bl2 = (Boolean)state.get(OPEN);
					if (bl2 != bl) {
						world.setBlockState(pos, state.with(OPEN, bl), 2);
						world.syncWorldEvent(null, bl ? 1003 : 1006, pos, 0);
					}
				}
			}
		}
	}

	@Override
	public BlockHitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
		this.setBoundingBox(world, pos);
		return super.rayTrace(world, pos, start, end);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = this.getDefaultState();
		if (dir.getAxis().isHorizontal()) {
			blockState = blockState.with(FACING, dir).with(OPEN, false);
			blockState = blockState.with(HALF, y > 0.5F ? TrapdoorBlock.TrapdoorType.TOP : TrapdoorBlock.TrapdoorType.BOTTOM);
		}

		return blockState;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return !direction.getAxis().isVertical() && canBePlacedAdjacent(world.getBlockState(pos.offset(direction.getOpposite())).getBlock());
	}

	protected static Direction getDirection(int data) {
		switch (data & 3) {
			case 0:
				return Direction.NORTH;
			case 1:
				return Direction.SOUTH;
			case 2:
				return Direction.WEST;
			case 3:
			default:
				return Direction.EAST;
		}
	}

	protected static int getDirectionData(Direction dir) {
		switch (dir) {
			case NORTH:
				return 0;
			case SOUTH:
				return 1;
			case WEST:
				return 2;
			case EAST:
			default:
				return 3;
		}
	}

	private static boolean canBePlacedAdjacent(Block block) {
		return block.material.isOpaque() && block.renderAsNormalBlock() || block == Blocks.GLOWSTONE || block instanceof SlabBlock || block instanceof StairsBlock;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState()
			.with(FACING, getDirection(data))
			.with(OPEN, (data & 4) != 0)
			.with(HALF, (data & 8) == 0 ? TrapdoorBlock.TrapdoorType.BOTTOM : TrapdoorBlock.TrapdoorType.TOP);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= getDirectionData(state.get(FACING));
		if ((Boolean)state.get(OPEN)) {
			i |= 4;
		}

		if (state.get(HALF) == TrapdoorBlock.TrapdoorType.TOP) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, OPEN, HALF);
	}

	public static enum TrapdoorType implements StringIdentifiable {
		TOP("top"),
		BOTTOM("bottom");

		private final String name;

		private TrapdoorType(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
