package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HuskEntity extends ZombieEntity {
	public HuskEntity(World world) {
		super(world);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, HuskEntity.class);
	}

	@Override
	public boolean canSpawn() {
		return super.canSpawn() && this.world.hasDirectSunlight(new BlockPos(this));
	}

	@Override
	protected boolean method_13605() {
		return false;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_HUSK_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_HUSK_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_HUSK_DEATH;
	}

	@Override
	protected Sound getStepSound() {
		return Sounds.ENTITY_HUSK_STEP;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.HUSK_ENTITIE;
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = super.tryAttack(target);
		if (bl && this.getMainHandStack().isEmpty() && target instanceof LivingEntity) {
			float f = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
			((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 140 * (int)f));
		}

		return bl;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}
}
