package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VineBlock extends Block {
	public static final BooleanProperty UP = BooleanProperty.of("up");
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final BooleanProperty[] PROPERTIES = new BooleanProperty[]{UP, NORTH, SOUTH, WEST, EAST};

	public VineBlock() {
		super(Material.REPLACEABLE_PLANT);
		this.setDefaultState(this.stateManager.getDefaultState().with(UP, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(UP, view.getBlockState(pos.up()).getBlock().isNormalBlock());
	}

	@Override
	public void setBlockItemBounds() {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
	public boolean isReplaceable(World world, BlockPos pos) {
		return true;
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		float f = 0.0625F;
		float g = 1.0F;
		float h = 1.0F;
		float i = 1.0F;
		float j = 0.0F;
		float k = 0.0F;
		float l = 0.0F;
		boolean bl = false;
		if ((Boolean)view.getBlockState(pos).get(WEST)) {
			j = Math.max(j, 0.0625F);
			g = 0.0F;
			h = 0.0F;
			k = 1.0F;
			i = 0.0F;
			l = 1.0F;
			bl = true;
		}

		if ((Boolean)view.getBlockState(pos).get(EAST)) {
			g = Math.min(g, 0.9375F);
			j = 1.0F;
			h = 0.0F;
			k = 1.0F;
			i = 0.0F;
			l = 1.0F;
			bl = true;
		}

		if ((Boolean)view.getBlockState(pos).get(NORTH)) {
			l = Math.max(l, 0.0625F);
			i = 0.0F;
			g = 0.0F;
			j = 1.0F;
			h = 0.0F;
			k = 1.0F;
			bl = true;
		}

		if ((Boolean)view.getBlockState(pos).get(SOUTH)) {
			i = Math.min(i, 0.9375F);
			l = 1.0F;
			g = 0.0F;
			j = 1.0F;
			h = 0.0F;
			k = 1.0F;
			bl = true;
		}

		if (!bl && this.canPlaceOn(view.getBlockState(pos.up()).getBlock())) {
			h = Math.min(h, 0.9375F);
			k = 1.0F;
			g = 0.0F;
			j = 1.0F;
			i = 0.0F;
			l = 1.0F;
		}

		this.setBoundingBox(g, h, i, j, k, l);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		switch (direction) {
			case UP:
				return this.canPlaceOn(world.getBlockState(pos.up()).getBlock());
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				return this.canPlaceOn(world.getBlockState(pos.offset(direction.getOpposite())).getBlock());
			default:
				return false;
		}
	}

	private boolean canPlaceOn(Block block) {
		return block.renderAsNormalBlock() && block.material.blocksMovement();
	}

	private boolean recheckGrowth(World world, BlockPos pos, BlockState state) {
		BlockState blockState = state;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BooleanProperty booleanProperty = getByDirection(direction);
			if ((Boolean)state.get(booleanProperty) && !this.canPlaceOn(world.getBlockState(pos.offset(direction)).getBlock())) {
				BlockState blockState2 = world.getBlockState(pos.up());
				if (blockState2.getBlock() != this || !(Boolean)blockState2.get(booleanProperty)) {
					state = state.with(booleanProperty, false);
				}
			}
		}

		if (getBlockStateId(state) == 0) {
			return false;
		} else {
			if (blockState != state) {
				world.setBlockState(pos, state, 2);
			}

			return true;
		}
	}

	@Override
	public int getColor() {
		return FoliageColors.getDefaultColor();
	}

	@Override
	public int getColor(BlockState state) {
		return FoliageColors.getDefaultColor();
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		return view.getBiome(pos).getFoliageColor(pos);
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!world.isClient && !this.recheckGrowth(world, pos, state)) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if (world.random.nextInt(4) == 0) {
				int i = 4;
				int j = 5;
				boolean bl = false;

				label189:
				for (int k = -i; k <= i; k++) {
					for (int l = -i; l <= i; l++) {
						for (int m = -1; m <= 1; m++) {
							if (world.getBlockState(pos.add(k, m, l)).getBlock() == this) {
								if (--j <= 0) {
									bl = true;
									break label189;
								}
							}
						}
					}
				}

				Direction direction = Direction.random(rand);
				BlockPos blockPos = pos.up();
				if (direction == Direction.UP && pos.getY() < 255 && world.isAir(blockPos)) {
					if (!bl) {
						BlockState blockState = state;

						for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
							if (rand.nextBoolean() || !this.canPlaceOn(world.getBlockState(blockPos.offset(direction2)).getBlock())) {
								blockState = blockState.with(getByDirection(direction2), false);
							}
						}

						if ((Boolean)blockState.get(NORTH) || (Boolean)blockState.get(EAST) || (Boolean)blockState.get(SOUTH) || (Boolean)blockState.get(WEST)) {
							world.setBlockState(blockPos, blockState, 2);
						}
					}
				} else if (!direction.getAxis().isHorizontal() || (Boolean)state.get(getByDirection(direction))) {
					if (pos.getY() > 1) {
						BlockPos blockPos5 = pos.down();
						BlockState blockState2 = world.getBlockState(blockPos5);
						Block block2 = blockState2.getBlock();
						if (block2.material == Material.AIR) {
							BlockState blockState3 = state;

							for (Direction direction5 : Direction.DirectionType.HORIZONTAL) {
								if (rand.nextBoolean()) {
									blockState3 = blockState3.with(getByDirection(direction5), false);
								}
							}

							if ((Boolean)blockState3.get(NORTH) || (Boolean)blockState3.get(EAST) || (Boolean)blockState3.get(SOUTH) || (Boolean)blockState3.get(WEST)) {
								world.setBlockState(blockPos5, blockState3, 2);
							}
						} else if (block2 == this) {
							BlockState blockState4 = blockState2;

							for (Direction direction6 : Direction.DirectionType.HORIZONTAL) {
								BooleanProperty booleanProperty = getByDirection(direction6);
								if (rand.nextBoolean() && (Boolean)state.get(booleanProperty)) {
									blockState4 = blockState4.with(booleanProperty, true);
								}
							}

							if ((Boolean)blockState4.get(NORTH) || (Boolean)blockState4.get(EAST) || (Boolean)blockState4.get(SOUTH) || (Boolean)blockState4.get(WEST)) {
								world.setBlockState(blockPos5, blockState4, 2);
							}
						}
					}
				} else if (!bl) {
					BlockPos blockPos2 = pos.offset(direction);
					Block block = world.getBlockState(blockPos2).getBlock();
					if (block.material == Material.AIR) {
						Direction direction3 = direction.rotateYClockwise();
						Direction direction4 = direction.rotateYCounterclockwise();
						boolean bl2 = (Boolean)state.get(getByDirection(direction3));
						boolean bl3 = (Boolean)state.get(getByDirection(direction4));
						BlockPos blockPos3 = blockPos2.offset(direction3);
						BlockPos blockPos4 = blockPos2.offset(direction4);
						if (bl2 && this.canPlaceOn(world.getBlockState(blockPos3).getBlock())) {
							world.setBlockState(blockPos2, this.getDefaultState().with(getByDirection(direction3), true), 2);
						} else if (bl3 && this.canPlaceOn(world.getBlockState(blockPos4).getBlock())) {
							world.setBlockState(blockPos2, this.getDefaultState().with(getByDirection(direction4), true), 2);
						} else if (bl2 && world.isAir(blockPos3) && this.canPlaceOn(world.getBlockState(pos.offset(direction3)).getBlock())) {
							world.setBlockState(blockPos3, this.getDefaultState().with(getByDirection(direction.getOpposite()), true), 2);
						} else if (bl3 && world.isAir(blockPos4) && this.canPlaceOn(world.getBlockState(pos.offset(direction4)).getBlock())) {
							world.setBlockState(blockPos4, this.getDefaultState().with(getByDirection(direction.getOpposite()), true), 2);
						} else if (this.canPlaceOn(world.getBlockState(blockPos2.up()).getBlock())) {
							world.setBlockState(blockPos2, this.getDefaultState(), 2);
						}
					} else if (block.material.isOpaque() && block.renderAsNormalBlock()) {
						world.setBlockState(pos, state.with(getByDirection(direction), true), 2);
					}
				}
			}
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = this.getDefaultState().with(UP, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false);
		return dir.getAxis().isHorizontal() ? blockState.with(getByDirection(dir.getOpposite()), true) : blockState;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void harvest(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
		if (!world.isClient && player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
			player.incrementStat(Stats.BLOCK_STATS[Block.getIdByBlock(this)]);
			onBlockBreak(world, pos, new ItemStack(Blocks.VINE, 1, 0));
		} else {
			super.harvest(world, player, pos, state, be);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(SOUTH, (data & 1) > 0).with(WEST, (data & 2) > 0).with(NORTH, (data & 4) > 0).with(EAST, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if ((Boolean)state.get(SOUTH)) {
			i |= 1;
		}

		if ((Boolean)state.get(WEST)) {
			i |= 2;
		}

		if ((Boolean)state.get(NORTH)) {
			i |= 4;
		}

		if ((Boolean)state.get(EAST)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, UP, NORTH, EAST, SOUTH, WEST);
	}

	public static BooleanProperty getByDirection(Direction dir) {
		switch (dir) {
			case UP:
				return UP;
			case NORTH:
				return NORTH;
			case SOUTH:
				return SOUTH;
			case EAST:
				return EAST;
			case WEST:
				return WEST;
			default:
				throw new IllegalArgumentException(dir + " is an invalid choice");
		}
	}

	public static int getBlockStateId(BlockState state) {
		int i = 0;

		for (BooleanProperty booleanProperty : PROPERTIES) {
			if ((Boolean)state.get(booleanProperty)) {
				i++;
			}
		}

		return i;
	}
}
