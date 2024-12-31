package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3135;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DonkeyEntity extends class_3135 {
	public DonkeyEntity(World world) {
		super(EntityType.DONKEY, world);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.DONKEY_ENTITIE;
	}

	@Override
	protected Sound ambientSound() {
		super.ambientSound();
		return Sounds.ENTITY_DONKEY_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		super.deathSound();
		return Sounds.ENTITY_DONKEY_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return Sounds.ENTITY_DONKEY_HURT;
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else {
			return !(other instanceof DonkeyEntity) && !(other instanceof HorseBaseEntity) ? false : this.method_13980() && ((AbstractHorseEntity)other).method_13980();
		}
	}

	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)(entity instanceof HorseBaseEntity ? new MuleEntity(this.world) : new DonkeyEntity(this.world));
		this.method_13968(entity, abstractHorseEntity);
		return abstractHorseEntity;
	}
}
