package net.minecraft.enchantment;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FrostWalkerEnchantment extends Enchantment {
	public FrostWalkerEnchantment(Enchantment.Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentTarget.FEET, equipmentSlots);
		this.setName("frostWalker");
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
			float f = (float)Math.min(16, 2 + i);
			BlockPos.Mutable mutable = new BlockPos.Mutable(0, 0, 0);

			for (BlockPos.Mutable mutable2 : BlockPos.mutableIterate(blockPos.add((double)(-f), -1.0, (double)(-f)), blockPos.add((double)f, -1.0, (double)f))) {
				if (mutable2.squaredDistanceToCenter(livingEntity.x, livingEntity.y, livingEntity.z) <= (double)(f * f)) {
					mutable.setPosition(mutable2.getX(), mutable2.getY() + 1, mutable2.getZ());
					BlockState blockState = world.getBlockState(mutable);
					if (blockState.getBlock() == Blocks.AIR) {
						BlockState blockState2 = world.getBlockState(mutable2);
						if (blockState2.getMaterial() == Material.WATER
							&& (Integer)blockState2.get(AbstractFluidBlock.LEVEL) == 0
							&& world.canBlockBePlaced(Blocks.FROSTED_ICE, mutable2, false, Direction.DOWN, null, null)) {
							world.setBlockState(mutable2, Blocks.FROSTED_ICE.getDefaultState());
							world.createAndScheduleBlockTick(mutable2.toImmutable(), Blocks.FROSTED_ICE, MathHelper.nextInt(livingEntity.getRandom(), 60, 120));
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
