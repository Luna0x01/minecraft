package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.sound.SoundCategory;
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
import net.minecraft.sound.Sounds;
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
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity, boolean isActualState) {
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
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isEmpty()) {
			return true;
		} else {
			int i = (Integer)state.get(LEVEL);
			Item item = itemStack.getItem();
			if (item == Items.WATER_BUCKET) {
				if (i < 3 && !world.isClient) {
					if (!player.abilities.creativeMode) {
						player.equipStack(hand, new ItemStack(Items.BUCKET));
					}

					player.incrementStat(Stats.CAULDRONS_FILLED);
					this.setLevel(world, pos, state, 3);
					world.method_11486(null, pos, Sounds.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
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

					player.incrementStat(Stats.CAULDRONS_USED);
					this.setLevel(world, pos, state, 0);
					world.method_11486(null, pos, Sounds.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				return true;
			} else if (item == Items.GLASS_BOTTLE) {
				if (i > 0 && !world.isClient) {
					if (!player.abilities.creativeMode) {
						ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
						player.incrementStat(Stats.CAULDRONS_USED);
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							player.equipStack(hand, itemStack2);
						} else if (!player.inventory.insertStack(itemStack2)) {
							player.dropItem(itemStack2, false);
						} else if (player instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
						}
					}

					world.method_11486(null, pos, Sounds.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.setLevel(world, pos, state, i - 1);
				}

				return true;
			} else if (item == Items.POTION && PotionUtil.getPotion(itemStack) == Potions.WATER) {
				if (i < 3 && !world.isClient) {
					if (!player.abilities.creativeMode) {
						ItemStack itemStack3 = new ItemStack(Items.GLASS_BOTTLE);
						player.incrementStat(Stats.CAULDRONS_USED);
						player.equipStack(hand, itemStack3);
						if (player instanceof ServerPlayerEntity) {
							((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
						}
					}

					world.method_11486(null, pos, Sounds.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.setLevel(world, pos, state, i + 1);
				}

				return true;
			} else {
				if (i > 0 && item instanceof ArmorItem) {
					ArmorItem armorItem = (ArmorItem)item;
					if (armorItem.getMaterial() == ArmorItem.Material.LEATHER && armorItem.hasColor(itemStack) && !world.isClient) {
						armorItem.removeColor(itemStack);
						this.setLevel(world, pos, state, i - 1);
						player.incrementStat(Stats.ARMOR_CLEANED);
						return true;
					}
				}

				if (i > 0 && item instanceof BannerItem) {
					if (BannerBlockEntity.getPatternCount(itemStack) > 0 && !world.isClient) {
						ItemStack itemStack4 = itemStack.copy();
						itemStack4.setCount(1);
						BannerBlockEntity.loadFromItemStack(itemStack4);
						player.incrementStat(Stats.BANNER_CLEANED);
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
