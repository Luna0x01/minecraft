package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3146;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class WhitherSkeletonEntity extends class_3146 {
	public WhitherSkeletonEntity(World world) {
		super(world);
		this.setBounds(0.7F, 2.4F);
		this.isFireImmune = true;
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, WhitherSkeletonEntity.class);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.WITHER_SKELETON_ENTITIE;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_WITHER_SKELETON_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_WITHER_SKELETON_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_WITHER_SKELETON_DEATH;
	}

	@Override
	Sound method_14060() {
		return Sounds.ENTITY_WITHER_SKELETON_STEP;
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (source.getAttacker() instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)source.getAttacker();
			if (creeperEntity.method_3074() && creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				this.dropItem(new ItemStack(Items.SKULL, 1, 1), 0.0F);
			}
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
	}

	@Override
	protected void updateEnchantments(LocalDifficulty difficulty) {
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		EntityData entityData = super.initialize(difficulty, data);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
		this.method_14058();
		return entityData;
	}

	@Override
	public float getEyeHeight() {
		return 2.1F;
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (!super.tryAttack(target)) {
			return false;
		} else {
			if (target instanceof LivingEntity) {
				((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200));
			}

			return true;
		}
	}

	@Override
	protected AbstractArrowEntity method_14056(float f) {
		AbstractArrowEntity abstractArrowEntity = super.method_14056(f);
		abstractArrowEntity.setOnFireFor(100);
		return abstractArrowEntity;
	}
}
