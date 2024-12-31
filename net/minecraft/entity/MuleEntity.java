package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3135;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MuleEntity extends class_3135 {
	public MuleEntity(World world) {
		super(EntityType.MULE, world);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.MULE_ENTITIE;
	}

	@Override
	protected Sound ambientSound() {
		super.ambientSound();
		return Sounds.ENTITY_MULE_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		super.deathSound();
		return Sounds.ENTITY_MULE_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return Sounds.ENTITY_MULE_HURT;
	}

	@Override
	protected void method_13964() {
		this.playSound(Sounds.ENTITY_MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}
}
