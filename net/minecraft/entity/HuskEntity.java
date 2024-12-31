package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HuskEntity extends ZombieEntity {
	public HuskEntity(World world) {
		super(EntityType.HUSK, world);
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		return super.method_15652(iWorld, bl) && (bl || iWorld.method_8555(new BlockPos(this)));
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
	protected Sound getHurtSound(DamageSource damageSource) {
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
			float f = this.world.method_8482(new BlockPos(this)).getLocalDifficulty();
			((LivingEntity)target).method_2654(new StatusEffectInstance(StatusEffects.HUNGER, 140 * (int)f));
		}

		return bl;
	}

	@Override
	protected boolean method_15900() {
		return true;
	}

	@Override
	protected void method_15901() {
		this.method_15897(new ZombieEntity(this.world));
		this.world.syncWorldEvent(null, 1041, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}
}
