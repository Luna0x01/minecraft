package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DoorBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	public static final BooleanProperty OPEN = BooleanProperty.of("open");
	public static final EnumProperty<DoorBlock.DoorType> HINGE = EnumProperty.of("hinge", DoorBlock.DoorType.class);
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final EnumProperty<DoorBlock.HalfType> HALF = EnumProperty.of("half", DoorBlock.HalfType.class);

	protected DoorBlock(Material material) {
		super(material);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(OPEN, false)
				.with(HINGE, DoorBlock.DoorType.LEFT)
				.with(POWERED, false)
				.with(HALF, DoorBlock.HalfType.LOWER)
		);
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate((this.getTranslationKey() + ".name").replaceAll("tile", "item"));
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return isOpen(getTotalData(view, pos));
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
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
		this.setBoundingBoxWithData(getTotalData(view, pos));
	}

	private void setBoundingBoxWithData(int totalData) {
		float f = 0.1875F;
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		Direction direction = getDirection(totalData);
		boolean bl = isOpen(totalData);
		boolean bl2 = isHingeOnLeft(totalData);
		if (bl) {
			if (direction == Direction.EAST) {
				if (!bl2) {
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
				} else {
					this.setBoundingBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
				}
			} else if (direction == Direction.SOUTH) {
				if (!bl2) {
					this.setBoundingBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				} else {
					this.setBoundingBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
				}
			} else if (direction == Direction.WEST) {
				if (!bl2) {
					this.setBoundingBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
				} else {
					this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
				}
			} else if (direction == Direction.NORTH) {
				if (!bl2) {
					this.setBoundingBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
				} else {
					this.setBoundingBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				}
			}
		} else if (direction == Direction.EAST) {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		} else if (direction == Direction.SOUTH) {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		} else if (direction == Direction.WEST) {
			this.setBoundingBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else if (direction == Direction.NORTH) {
			this.setBoundingBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (this.material == Material.IRON) {
			return true;
		} else {
			BlockPos blockPos = state.get(HALF) == DoorBlock.HalfType.LOWER ? pos : pos.down();
			BlockState blockState = pos.equals(blockPos) ? state : world.getBlockState(blockPos);
			if (blockState.getBlock() != this) {
				return false;
			} else {
				state = blockState.withDefaultValue(OPEN);
				world.setBlockState(blockPos, state, 2);
				world.onRenderRegionUpdate(blockPos, pos);
				world.syncWorldEvent(player, state.get(OPEN) ? 1003 : 1006, pos, 0);
				return true;
			}
		}
	}

	public void activateDoor(World world, BlockPos pos, boolean isOpen) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == this) {
			BlockPos blockPos = blockState.get(HALF) == DoorBlock.HalfType.LOWER ? pos : pos.down();
			BlockState blockState2 = pos == blockPos ? blockState : world.getBlockState(blockPos);
			if (blockState2.getBlock() == this && (Boolean)blockState2.get(OPEN) != isOpen) {
				world.setBlockState(blockPos, blockState2.with(OPEN, isOpen), 2);
				world.onRenderRegionUpdate(blockPos, pos);
				world.syncWorldEvent(null, isOpen ? 1003 : 1006, pos, 0);
			}
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (state.get(HALF) == DoorBlock.HalfType.UPPER) {
			BlockPos blockPos = pos.down();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() != this) {
				world.setAir(pos);
			} else if (block != this) {
				this.neighborUpdate(world, blockPos, blockState, block);
			}
		} else {
			boolean bl = false;
			BlockPos blockPos2 = pos.up();
			BlockState blockState2 = world.getBlockState(blockPos2);
			if (blockState2.getBlock() != this) {
				world.setAir(pos);
				bl = true;
			}

			if (!World.isOpaque(world, pos.down())) {
				world.setAir(pos);
				bl = true;
				if (blockState2.getBlock() == this) {
					world.setAir(blockPos2);
				}
			}

			if (bl) {
				if (!world.isClient) {
					this.dropAsItem(world, pos, state, 0);
				}
			} else {
				boolean bl2 = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(blockPos2);
				if ((bl2 || block.emitsRedstonePower()) && block != this && bl2 != (Boolean)blockState2.get(POWERED)) {
					world.setBlockState(blockPos2, blockState2.with(POWERED, bl2), 2);
					if (bl2 != (Boolean)state.get(OPEN)) {
						world.setBlockState(pos, state.with(OPEN, bl2), 2);
						world.onRenderRegionUpdate(pos, pos);
						world.syncWorldEvent(null, bl2 ? 1003 : 1006, pos, 0);
					}
				}
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return state.get(HALF) == DoorBlock.HalfType.UPPER ? null : this.getItem();
	}

	@Override
	public BlockHitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
		this.setBoundingBox(world, pos);
		return super.rayTrace(world, pos, start, end);
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return pos.getY() >= 255 ? false : World.isOpaque(world, pos.down()) && super.canBePlacedAtPos(world, pos) && super.canBePlacedAtPos(world, pos.up());
	}

	@Override
	public int getPistonInteractionType() {
		return 1;
	}

	public static int getTotalData(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		int i = blockState.getBlock().getData(blockState);
		boolean bl = isTopHalf(i);
		BlockState blockState2 = view.getBlockState(pos.down());
		int j = blockState2.getBlock().getData(blockState2);
		int k = bl ? j : i;
		BlockState blockState3 = view.getBlockState(pos.up());
		int l = blockState3.getBlock().getData(blockState3);
		int m = bl ? i : l;
		boolean bl2 = (m & 1) != 0;
		boolean bl3 = (m & 2) != 0;
		return removeHalf(k) | (bl ? 8 : 0) | (bl2 ? 16 : 0) | (bl3 ? 32 : 0);
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return this.getItem();
	}

	private Item getItem() {
		if (this == Blocks.IRON_DOOR) {
			return Items.IRON_DOOR;
		} else if (this == Blocks.SPRUCE_DOOR) {
			return Items.SPRUCE_DOOR;
		} else if (this == Blocks.BIRCH_DOOR) {
			return Items.BIRCH_DOOR;
		} else if (this == Blocks.JUNGLE_DOOR) {
			return Items.JUNGLE_DOOR;
		} else if (this == Blocks.ACACIA_DOOR) {
			return Items.ACACIA_DOOR;
		} else {
			return this == Blocks.DARK_OAK_DOOR ? Items.DARK_OAK_DOOR : Items.OAK_DOOR;
		}
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockPos blockPos = pos.down();
		if (player.abilities.creativeMode && state.get(HALF) == DoorBlock.HalfType.UPPER && world.getBlockState(blockPos).getBlock() == this) {
			world.setAir(blockPos);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		if (state.get(HALF) == DoorBlock.HalfType.LOWER) {
			BlockState blockState = view.getBlockState(pos.up());
			if (blockState.getBlock() == this) {
				state = state.with(HINGE, blockState.get(HINGE)).with(POWERED, blockState.get(POWERED));
			}
		} else {
			BlockState blockState2 = view.getBlockState(pos.down());
			if (blockState2.getBlock() == this) {
				state = state.with(FACING, blockState2.get(FACING)).with(OPEN, blockState2.get(OPEN));
			}
		}

		return state;
	}

	@Override
	public BlockState stateFromData(int data) {
		return (data & 8) > 0
			? this.getDefaultState()
				.with(HALF, DoorBlock.HalfType.UPPER)
				.with(HINGE, (data & 1) > 0 ? DoorBlock.DoorType.RIGHT : DoorBlock.DoorType.LEFT)
				.with(POWERED, (data & 2) > 0)
			: this.getDefaultState()
				.with(HALF, DoorBlock.HalfType.LOWER)
				.with(FACING, Direction.fromHorizontal(data & 3).rotateYCounterclockwise())
				.with(OPEN, (data & 4) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if (state.get(HALF) == DoorBlock.HalfType.UPPER) {
			i |= 8;
			if (state.get(HINGE) == DoorBlock.DoorType.RIGHT) {
				i |= 1;
			}

			if ((Boolean)state.get(POWERED)) {
				i |= 2;
			}
		} else {
			i |= ((Direction)state.get(FACING)).rotateYClockwise().getHorizontal();
			if ((Boolean)state.get(OPEN)) {
				i |= 4;
			}
		}

		return i;
	}

	protected static int removeHalf(int data) {
		return data & 7;
	}

	public static boolean isOpen(BlockView view, BlockPos pos) {
		return isOpen(getTotalData(view, pos));
	}

	public static Direction getDirection(BlockView view, BlockPos pos) {
		return getDirection(getTotalData(view, pos));
	}

	public static Direction getDirection(int data) {
		return Direction.fromHorizontal(data & 3).rotateYCounterclockwise();
	}

	protected static boolean isOpen(int totalData) {
		return (totalData & 4) != 0;
	}

	protected static boolean isTopHalf(int data) {
		return (data & 8) != 0;
	}

	protected static boolean isHingeOnLeft(int totalData) {
		return (totalData & 16) != 0;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, HALF, FACING, OPEN, HINGE, POWERED);
	}

	public static enum DoorType implements StringIdentifiable {
		LEFT,
		RIGHT;

		public String toString() {
			return this.asString();
		}

		@Override
		public String asString() {
			return this == LEFT ? "left" : "right";
		}
	}

	public static enum HalfType implements StringIdentifiable {
		UPPER,
		LOWER;

		public String toString() {
			return this.asString();
		}

		@Override
		public String asString() {
			return this == UPPER ? "upper" : "lower";
		}
	}
}
