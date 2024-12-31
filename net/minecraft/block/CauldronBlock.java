package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CauldronBlock extends Block {
	public static final IntProperty LEVEL = IntProperty.of("level", 0, 3);

	public CauldronBlock() {
		super(Material.IRON, MaterialColor.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 0));
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		float f = 0.125F;
		this.setBoundingBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBoundingBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBoundingBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBlockItemBounds();
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
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		int i = (Integer)state.get(LEVEL);
		float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;
		if (!world.isClient && entity.isOnFire() && i > 0 && entity.getBoundingBox().minY <= (double)f) {
			entity.extinguish();
			this.setLevel(world, pos, state, i - 1);
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			ItemStack itemStack = player.inventory.getMainHandStack();
			if (itemStack == null) {
				return true;
			} else {
				int i = (Integer)state.get(LEVEL);
				Item item = itemStack.getItem();
				if (item == Items.WATER_BUCKET) {
					if (i < 3) {
						if (!player.abilities.creativeMode) {
							player.inventory.setInvStack(player.inventory.selectedSlot, new ItemStack(Items.BUCKET));
						}

						player.incrementStat(Stats.CAULDRONS_FILLED);
						this.setLevel(world, pos, state, 3);
					}

					return true;
				} else if (item == Items.GLASS_BOTTLE) {
					if (i > 0) {
						if (!player.abilities.creativeMode) {
							ItemStack itemStack2 = new ItemStack(Items.POTION, 1, 0);
							if (!player.inventory.insertStack(itemStack2)) {
								world.spawnEntity(new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 1.5, (double)pos.getZ() + 0.5, itemStack2));
							} else if (player instanceof ServerPlayerEntity) {
								((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
							}

							player.incrementStat(Stats.CAULDRONS_USED);
							itemStack.count--;
							if (itemStack.count <= 0) {
								player.inventory.setInvStack(player.inventory.selectedSlot, null);
							}
						}

						this.setLevel(world, pos, state, i - 1);
					}

					return true;
				} else {
					if (i > 0 && item instanceof ArmorItem) {
						ArmorItem armorItem = (ArmorItem)item;
						if (armorItem.getMaterial() == ArmorItem.Material.LEATHER && armorItem.hasColor(itemStack)) {
							armorItem.removeColor(itemStack);
							this.setLevel(world, pos, state, i - 1);
							player.incrementStat(Stats.ARMOR_CLEANED);
							return true;
						}
					}

					if (i > 0 && item instanceof BannerItem && BannerBlockEntity.getPatternCount(itemStack) > 0) {
						ItemStack itemStack3 = itemStack.copy();
						itemStack3.count = 1;
						BannerBlockEntity.loadFromItemStack(itemStack3);
						if (itemStack.count <= 1 && !player.abilities.creativeMode) {
							player.inventory.setInvStack(player.inventory.selectedSlot, itemStack3);
						} else {
							if (!player.inventory.insertStack(itemStack3)) {
								world.spawnEntity(new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 1.5, (double)pos.getZ() + 0.5, itemStack3));
							} else if (player instanceof ServerPlayerEntity) {
								((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
							}

							player.incrementStat(Stats.BANNER_CLEANED);
							if (!player.abilities.creativeMode) {
								itemStack.count--;
							}
						}

						if (!player.abilities.creativeMode) {
							this.setLevel(world, pos, state, i - 1);
						}

						return true;
					} else {
						return false;
					}
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
			BlockState blockState = world.getBlockState(pos);
			if ((Integer)blockState.get(LEVEL) < 3) {
				world.setBlockState(pos, blockState.withDefaultValue(LEVEL), 2);
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.CAULDRON;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.CAULDRON;
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
		return (Integer)world.getBlockState(pos).get(LEVEL);
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
}
