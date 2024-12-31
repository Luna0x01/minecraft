package net.minecraft.enchantment;

import net.minecraft.class_3710;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FrostWalkerEnchantment extends Enchantment {
	public FrostWalkerEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.FEET, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int level) {
		return level * 10;
	}

	@Override
	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 15;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public int getMaximumLevel() {
		return 2;
	}

	public static void method_11464(LivingEntity livingEntity, World world, BlockPos blockPos, int i) {
		if (livingEntity.onGround) {
			BlockState blockState = Blocks.FROSTED_ICE.getDefaultState();
			float f = (float)Math.min(16, 2 + i);
			BlockPos.Mutable mutable = new BlockPos.Mutable(0, 0, 0);

			for (BlockPos.Mutable mutable2 : BlockPos.mutableIterate(blockPos.add((double)(-f), -1.0, (double)(-f)), blockPos.add((double)f, -1.0, (double)f))) {
				if (mutable2.squaredDistanceToCenter(livingEntity.x, livingEntity.y, livingEntity.z) <= (double)(f * f)) {
					mutable.setPosition(mutable2.getX(), mutable2.getY() + 1, mutable2.getZ());
					BlockState blockState2 = world.getBlockState(mutable);
					if (blockState2.isAir()) {
						BlockState blockState3 = world.getBlockState(mutable2);
						if (blockState3.getMaterial() == Material.WATER
							&& (Integer)blockState3.getProperty(class_3710.field_18402) == 0
							&& blockState.canPlaceAt(world, mutable2)
							&& world.method_16371(blockState, mutable2)) {
							world.setBlockState(mutable2, blockState);
							world.getBlockTickScheduler().schedule(mutable2.toImmutable(), Blocks.FROSTED_ICE, MathHelper.nextInt(livingEntity.getRandom(), 60, 120));
						}
					}
				}
			}
		}
	}

	@Override
	public boolean differs(Enchantment other) {
		return super.differs(other) && other != Enchantments.DEPTH_STRIDER;
	}
}
