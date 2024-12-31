package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CauldronBlock extends Block {
	public static final IntProperty LEVEL = IntProperty.of("level", 0, 3);
	protected static final Box field_12611 = new Box(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0);
	protected static final Box field_12612 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
	protected static final Box field_12613 = new Box(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);
	protected static final Box field_12614 = new Box(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12615 = new Box(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);

	public CauldronBlock() {
		super(Material.IRON, MaterialColor.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 0));
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
		appendCollisionBoxes(pos, entityBox, boxes, field_12611);
		appendCollisionBoxes(pos, entityBox, boxes, field_12615);
		appendCollisionBoxes(pos, entityBox, boxes, field_12612);
		appendCollisionBoxes(pos, entityBox, boxes, field_12614);
		appendCollisionBoxes(pos, entityBox, boxes, field_12613);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return collisionBox;
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
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		int i = (Integer)state.get(LEVEL);
		float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;
		if (!world.isClient && entity.isOnFire() && i > 0 && entity.getBoundingBox().minY <= (double)f) {
			entity.extinguish();
			this.setLevel(world, pos, state, i - 1);
		}
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (itemStack == null) {
			return true;
		} else {
			int i = (Integer)blockState.get(LEVEL);
			Item item = itemStack.getItem();
			if (item == Items.WATER_BUCKET) {
				if (i < 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						playerEntity.equipStack(hand, new ItemStack(Items.BUCKET));
					}

					playerEntity.incrementStat(Stats.CAULDRONS_FILLED);
					this.setLevel(world, blockPos, blockState, 3);
				}

				return true;
			} else if (item == Items.BUCKET) {
				if (i == 3 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.count--;
						if (itemStack.count == 0) {
							playerEntity.equipStack(hand, new ItemStack(Items.WATER_BUCKET));
						} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.WATER_BUCKET))) {
							playerEntity.dropItem(new ItemStack(Items.WATER_BUCKET), false);
						}
					}

					playerEntity.incrementStat(Stats.CAULDRONS_USED);
					this.setLevel(world, blockPos, blockState, 0);
				}

				return true;
			} else if (item == Items.GLASS_BOTTLE) {
				if (i > 0 && !world.isClient) {
					if (!playerEntity.abilities.creativeMode) {
						ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
						playerEntity.incrementStat(Stats.CAULDRONS_USED);
						if (--itemStack.count == 0) {
							playerEntity.equipStack(hand, itemStack2);
						} else if (!playerEntity.inventory.insertStack(itemStack2)) {
							playerEntity.dropItem(itemStack2, false);
						} else if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).refreshScreenHandler(playerEntity.playerScreenHandler);
						}
					}

					this.setLevel(world, blockPos, blockState, i - 1);
				}

				return true;
			} else {
				if (i > 0 && item instanceof ArmorItem) {
					ArmorItem armorItem = (ArmorItem)item;
					if (armorItem.getMaterial() == ArmorItem.Material.LEATHER && armorItem.hasColor(itemStack) && !world.isClient) {
						armorItem.removeColor(itemStack);
						this.setLevel(world, blockPos, blockState, i - 1);
						playerEntity.incrementStat(Stats.ARMOR_CLEANED);
						return true;
					}
				}

				if (i > 0 && item instanceof BannerItem) {
					if (BannerBlockEntity.getPatternCount(itemStack) > 0 && !world.isClient) {
						ItemStack itemStack3 = itemStack.copy();
						itemStack3.count = 1;
						BannerBlockEntity.loadFromItemStack(itemStack3);
						playerEntity.incrementStat(Stats.BANNER_CLEANED);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.count--;
						}

						if (itemStack.count == 0) {
							playerEntity.equipStack(hand, itemStack3);
						} else if (!playerEntity.inventory.insertStack(itemStack3)) {
							playerEntity.dropItem(itemStack3, false);
						} else if (playerEntity instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)playerEntity).refreshScreenHandler(playerEntity.playerScreenHandler);
						}

						if (!playerEntity.abilities.creativeMode) {
							this.setLevel(world, blockPos, blockState, i - 1);
						}
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void setLevel(World world, BlockPos pos, BlockState state, int level) {
		world.setBlockState(pos, state.with(LEVEL, MathHelper.clamp(level, 0, 3)), 2);
		world.updateHorizontalAdjacent(pos, this);
	}

	@Override
	public void onRainTick(World world, BlockPos pos) {
		if (world.random.nextInt(20) == 1) {
			float f = world.getBiome(pos).getTemperature(pos);
			if (!(world.method_3726().method_11533(f, pos.getY()) < 0.15F)) {
				BlockState blockState = world.getBlockState(pos);
				if ((Integer)blockState.get(LEVEL) < 3) {
					world.setBlockState(pos, blockState.withDefaultValue(LEVEL), 2);
				}
			}
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.CAULDRON;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.CAULDRON);
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (Integer)state.get(LEVEL);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(LEVEL, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(LEVEL);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, LEVEL);
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return true;
	}
}
