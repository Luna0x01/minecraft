package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.SkeletonHorseEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;

public class class_2978 extends Goal {
	private final SkeletonHorseEntity field_15521;

	public class_2978(SkeletonHorseEntity skeletonHorseEntity) {
		this.field_15521 = skeletonHorseEntity;
	}

	@Override
	public boolean canStart() {
		return this.field_15521.world.isPlayerInRange(this.field_15521.x, this.field_15521.y, this.field_15521.z, 10.0);
	}

	@Override
	public void tick() {
		LocalDifficulty localDifficulty = this.field_15521.world.method_8482(new BlockPos(this.field_15521));
		this.field_15521.method_14041(false);
		this.field_15521.method_14007(true);
		this.field_15521.setAge(0);
		this.field_15521.world.addEntity(new LightningBoltEntity(this.field_15521.world, this.field_15521.x, this.field_15521.y, this.field_15521.z, true));
		SkeletonEntity skeletonEntity = this.method_13156(localDifficulty, this.field_15521);
		skeletonEntity.ride(this.field_15521);

		for (int i = 0; i < 3; i++) {
			AbstractHorseEntity abstractHorseEntity = this.method_14042(localDifficulty);
			SkeletonEntity skeletonEntity2 = this.method_13156(localDifficulty, abstractHorseEntity);
			skeletonEntity2.ride(abstractHorseEntity);
			abstractHorseEntity.addVelocity(this.field_15521.getRandom().nextGaussian() * 0.5, 0.0, this.field_15521.getRandom().nextGaussian() * 0.5);
		}
	}

	private AbstractHorseEntity method_14042(LocalDifficulty localDifficulty) {
		SkeletonHorseEntity skeletonHorseEntity = new SkeletonHorseEntity(this.field_15521.world);
		skeletonHorseEntity.initialize(localDifficulty, null, null);
		skeletonHorseEntity.updatePosition(this.field_15521.x, this.field_15521.y, this.field_15521.z);
		skeletonHorseEntity.timeUntilRegen = 60;
		skeletonHorseEntity.setPersistent();
		skeletonHorseEntity.method_14007(true);
		skeletonHorseEntity.setAge(0);
		skeletonHorseEntity.world.method_3686(skeletonHorseEntity);
		return skeletonHorseEntity;
	}

	private SkeletonEntity method_13156(LocalDifficulty localDifficulty, AbstractHorseEntity abstractHorseEntity) {
		SkeletonEntity skeletonEntity = new SkeletonEntity(abstractHorseEntity.world);
		skeletonEntity.initialize(localDifficulty, null, null);
		skeletonEntity.updatePosition(abstractHorseEntity.x, abstractHorseEntity.y, abstractHorseEntity.z);
		skeletonEntity.timeUntilRegen = 60;
		skeletonEntity.setPersistent();
		if (skeletonEntity.getStack(EquipmentSlot.HEAD).isEmpty()) {
			skeletonEntity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
		}

		skeletonEntity.equipStack(
			EquipmentSlot.MAINHAND,
			EnchantmentHelper.enchant(
				skeletonEntity.getRandom(),
				skeletonEntity.getMainHandStack(),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRandom().nextInt(18)),
				false
			)
		);
		skeletonEntity.equipStack(
			EquipmentSlot.HEAD,
			EnchantmentHelper.enchant(
				skeletonEntity.getRandom(),
				skeletonEntity.getStack(EquipmentSlot.HEAD),
				(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRandom().nextInt(18)),
				false
			)
		);
		skeletonEntity.world.method_3686(skeletonEntity);
		return skeletonEntity;
	}
}
