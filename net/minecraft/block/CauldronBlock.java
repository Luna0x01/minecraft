package net.minecraft.block;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CauldronBlock extends Block {
	public static final IntProperty LEVEL = Properties.LEVEL_3;
	protected static final VoxelShape RAYCAST_SHAPE = Block.createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
	protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.matchesAnywhere(), RAYCAST_SHAPE, BooleanBiFunction.ONLY_FIRST);

	public CauldronBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(LEVEL, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return OUTLINE_SHAPE;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
		return RAYCAST_SHAPE;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		int i = (Integer)state.getProperty(LEVEL);
		float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;
		if (!world.isClient && entity.isOnFire() && i > 0 && entity.getBoundingBox().minY <= (double)f) {
			entity.extinguish();
			this.setLevel(world, pos, state, i - 1);
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isEmpty()) {
			return true;
		} else {
			int i = (Integer)state.getProperty(LEVEL);
			Item item = itemStack.getItem();
			if (item == Items.WATER_BUCKET) {
				if (i < 3 && !world.isClient) {
					if (!player.abilities.creativeMode) {
						player.equipStack(hand, new ItemStack(Items.BUCKET));
					}

					player.method_15928(Stats.FILL_CAULDRON);
					this.setLevel(world, pos, state, 3);
					world.playSound(null, pos, Sounds.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.BUCKET) {
				if (i == 3 && !world.isClient) {
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							player.equipStack(hand, new ItemStack(Items.WATER_BUCKET));
						} else if (!player.inventory.insertStack(new ItemStack(Items.WATER_BUCKET))) {
							player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
						}
					}

					player.method_15928(Stats.USE_CAULDRON);
					this.setLevel(world, pos, state, 0);
					world.playSound(null, pos, Sounds.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.GLASS_BOTTLE) {
				if (i > 0 && !world.isClient) {
					if (!player.abilities.creativeMode) {
						ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
						player.method_15928(Stats.USE_CAULDRON);
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							player.equipStack(hand, itemStack2);
						} else if (!player.inventory.insertStack(itemStack2)) {
							player.dropItem(itemStack2, false);
						} else if (player instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
						}
					}

					world.playSound(null, pos, Sounds.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.setLevel(world, pos, state, i - 1);
				}

				return true;
			} else if (item == Items.POTION && PotionUtil.getPotion(itemStack) == Potions.WATER) {
				if (i < 3 && !world.isClient) {
					if (!player.abilities.creativeMode) {
						ItemStack itemStack3 = new ItemStack(Items.GLASS_BOTTLE);
						player.method_15928(Stats.USE_CAULDRON);
						player.equipStack(hand, itemStack3);
						if (player instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
						}
					}

					world.playSound(null, pos, Sounds.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.setLevel(world, pos, state, i + 1);
				}

				return true;
			} else {
				if (i > 0 && item instanceof DyeableArmorItem) {
					DyeableArmorItem dyeableArmorItem = (DyeableArmorItem)item;
					if (dyeableArmorItem.method_16049(itemStack) && !world.isClient) {
						dyeableArmorItem.method_16051(itemStack);
						this.setLevel(world, pos, state, i - 1);
						player.method_15928(Stats.CLEAN_ARMOR);
						return true;
					}
				}

				if (i > 0 && item instanceof BannerItem) {
					if (BannerBlockEntity.getPatternCount(itemStack) > 0 && !world.isClient) {
						ItemStack itemStack4 = itemStack.copy();
						itemStack4.setCount(1);
						BannerBlockEntity.loadFromItemStack(itemStack4);
						player.method_15928(Stats.CLEAN_BANNER);
						if (!player.abilities.creativeMode) {
							itemStack.decrement(1);
							this.setLevel(world, pos, state, i - 1);
						}

						if (itemStack.isEmpty()) {
							player.equipStack(hand, itemStack4);
						} else if (!player.inventory.insertStack(itemStack4)) {
							player.dropItem(itemStack4, false);
						} else if (player instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
						}
					}

					return true;
				} else if (i > 0 && item instanceof BlockItem) {
					Block block = ((BlockItem)item).getBlock();
					if (block instanceof ShulkerBoxBlock && !world.method_16390()) {
						ItemStack itemStack5 = new ItemStack(Blocks.SHULKER_BOX, 1);
						if (itemStack.hasNbt()) {
							itemStack5.setNbt(itemStack.getNbt().copy());
						}

						player.equipStack(hand, itemStack5);
						this.setLevel(world, pos, state, i - 1);
						player.method_15928(Stats.CLEAN_SHULKER_BOX);
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void setLevel(World world, BlockPos pos, BlockState state, int level) {
		world.setBlockState(pos, state.withProperty(LEVEL, Integer.valueOf(MathHelper.clamp(level, 0, 3))), 2);
		world.updateHorizontalAdjacent(pos, this);
	}

	@Override
	public void onRainTick(World world, BlockPos pos) {
		if (world.random.nextInt(20) == 1) {
			float f = world.method_8577(pos).getTemperature(pos);
			if (!(f < 0.15F)) {
				BlockState blockState = world.getBlockState(pos);
				if ((Integer)blockState.getProperty(LEVEL) < 3) {
					world.setBlockState(pos, blockState.method_16930(LEVEL), 2);
				}
			}
		}
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (Integer)state.getProperty(LEVEL);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(LEVEL);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		if (direction == Direction.UP) {
			return BlockRenderLayer.BOWL;
		} else {
			return direction == Direction.DOWN ? BlockRenderLayer.UNDEFINED : BlockRenderLayer.SOLID;
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
