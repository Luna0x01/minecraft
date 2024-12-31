package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3146;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class StrayEntity extends class_3146 {
	public StrayEntity(World world) {
		super(EntityType.STRAY, world);
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		return super.method_15652(iWorld, bl) && (bl || iWorld.method_8555(new BlockPos(this)));
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.STRAY_ENTITIE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_STRAY_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_STRAY_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_STRAY_DEATH;
	}

	@Override
	Sound method_14060() {
		return Sounds.ENTITY_STRAY_STEP;
	}

	@Override
	protected AbstractArrowEntity method_14056(float f) {
		AbstractArrowEntity abstractArrowEntity = super.method_14056(f);
		if (abstractArrowEntity instanceof ArrowEntity) {
			((ArrowEntity)abstractArrowEntity).addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600));
		}

		return abstractArrowEntity;
	}
}
