package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.HorseType;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;

public class class_2978 extends Goal {
	private final HorseBaseEntity horse;

	public class_2978(HorseBaseEntity horseBaseEntity) {
		this.horse = horseBaseEntity;
	}

	@Override
	public boolean canStart() {
		return this.horse.world.isPlayerInRange(this.horse.x, this.horse.y, this.horse.z, 10.0);
	}

	@Override
	public void tick() {
		LocalDifficulty localDifficulty = this.horse.world.getLocalDifficulty(new BlockPos(this.horse));
		this.horse.method_13133(false);
		this.horse.method_13126(HorseType.SKELETON);
		this.horse.setTame(true);
		this.horse.setAge(0);
		this.horse.world.addEntity(new LightningBoltEntity(this.horse.world, this.horse.x, this.horse.y, this.horse.z, true));
		SkeletonEntity skeletonEntity = this.method_13156(localDifficulty, this.horse);
		skeletonEntity.ride(this.horse);

		for (int i = 0; i < 3; i++) {
			HorseBaseEntity horseBaseEntity = this.method_13155(localDifficulty);
			SkeletonEntity skeletonEntity2 = this.method_13156(localDifficulty, horseBaseEntity);
			skeletonEntity2.ride(horseBaseEntity);
			horseBaseEntity.addVelocity(this.horse.getRandom().nextGaussian() * 0.5, 0.0, this.horse.getRandom().nextGaussian() * 0.5);
		}
	}

	private HorseBaseEntity method_13155(LocalDifficulty localDifficulty) {
		HorseBaseEntity horseBaseEntity = new HorseBaseEntity(this.horse.world);
		horseBaseEntity.initialize(localDifficulty, null);
		horseBaseEntity.updatePosition(this.horse.x, this.horse.y, this.horse.z);
		horseBaseEntity.timeUntilRegen = 60;
		horseBaseEntity.setPersistent();
		horseBaseEntity.method_13126(HorseType.SKELETON);
		horseBaseEntity.setTame(true);
		horseBaseEntity.setAge(0);
		horseBaseEntity.world.spawnEntity(horseBaseEntity);
		return horseBaseEntity;
	}

	private SkeletonEntity method_13156(LocalDifficulty localDifficulty, HorseBaseEntity horseBaseEntity) {
		SkeletonEntity skeletonEntity = new SkeletonEntity(horseBaseEntity.world);
		skeletonEntity.initialize(localDifficulty, null);
		skeletonEntity.updatePosition(horseBaseEntity.x, horseBaseEntity.y, horseBaseEntity.z);
		skeletonEntity.timeUntilRegen = 60;
		skeletonEntity.setPersistent();
		if (skeletonEntity.getStack(EquipmentSlot.HEAD) == null) {
			skeletonEntity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
		}

		EnchantmentHelper.enchant(
			skeletonEntity.getRandom(),
			skeletonEntity.getMainHandStack(),
			(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRandom().nextInt(18)),
			false
		);
		EnchantmentHelper.enchant(
			skeletonEntity.getRandom(),
			skeletonEntity.getStack(EquipmentSlot.HEAD),
			(int)(5.0F + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRandom().nextInt(18)),
			false
		);
		skeletonEntity.world.spawnEntity(skeletonEntity);
		return skeletonEntity;
	}
}
