package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3462;
import net.minecraft.class_3558;
import net.minecraft.entity.ai.goal.class_2978;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SkeletonHorseEntity extends AbstractHorseEntity {
	private final class_2978 field_15518 = new class_2978(this);
	private boolean field_15519;
	private int field_15520;

	public SkeletonHorseEntity(World world) {
		super(EntityType.SKELETON_HORSE, world);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(15.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
		this.initializeAttribute(field_15508).setBaseValue(this.method_13982());
	}

	@Override
	protected void method_15829() {
	}

	@Override
	protected Sound ambientSound() {
		super.ambientSound();
		return this.method_15567(FluidTags.WATER) ? Sounds.ENTITY_SKELETON_HORSE_AMBIENT_WATER : Sounds.ENTITY_SKELETON_HORSE_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		super.deathSound();
		return Sounds.ENTITY_SKELETON_HORSE_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return Sounds.ENTITY_SKELETON_HORSE_HURT;
	}

	@Override
	protected Sound method_12984() {
		if (this.onGround) {
			if (!this.hasPassengers()) {
				return Sounds.ENTITY_SKELETON_HORSE_STEP_WATER;
			}

			this.field_15494++;
			if (this.field_15494 > 5 && this.field_15494 % 3 == 0) {
				return Sounds.ENTITY_SKELETON_HORSE_GALLOP_WATER;
			}

			if (this.field_15494 <= 5) {
				return Sounds.ENTITY_SKELETON_HORSE_STEP_WATER;
			}
		}

		return Sounds.ENTITY_SKELETON_HORSE_SWIM;
	}

	@Override
	protected void method_15588(float f) {
		if (this.onGround) {
			super.method_15588(0.3F);
		} else {
			super.method_15588(Math.min(0.1F, f * 25.0F));
		}
	}

	@Override
	protected void method_15830() {
		if (this.isTouchingWater()) {
			this.playSound(Sounds.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
		} else {
			super.method_15830();
		}
	}

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16819;
	}

	@Override
	public double getMountedHeightOffset() {
		return super.getMountedHeightOffset() - 0.1875;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.SKELETON_HORSE_ENTITIE;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.method_14040() && this.field_15520++ >= 18000) {
			this.remove();
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("SkeletonTrap", this.method_14040());
		nbt.putInt("SkeletonTrapTime", this.field_15520);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_14041(nbt.getBoolean("SkeletonTrap"));
		this.field_15520 = nbt.getInt("SkeletonTrapTime");
	}

	@Override
	public boolean method_15570() {
		return true;
	}

	@Override
	protected float method_13494() {
		return 0.96F;
	}

	public boolean method_14040() {
		return this.field_15519;
	}

	public void method_14041(boolean bl) {
		if (bl != this.field_15519) {
			this.field_15519 = bl;
			if (bl) {
				this.goals.add(1, this.field_15518);
			} else {
				this.goals.method_4497(this.field_15518);
			}
		}
	}

	@Nullable
	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		return new SkeletonHorseEntity(this.world);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() instanceof class_3558) {
			return super.interactMob(playerEntity, hand);
		} else if (!this.method_13990()) {
			return false;
		} else if (this.isBaby()) {
			return super.interactMob(playerEntity, hand);
		} else if (playerEntity.isSneaking()) {
			this.method_14000(playerEntity);
			return true;
		} else if (this.hasPassengers()) {
			return super.interactMob(playerEntity, hand);
		} else {
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() == Items.SADDLE && !this.method_13975()) {
					this.method_14000(playerEntity);
					return true;
				}

				if (itemStack.method_6329(playerEntity, this, hand)) {
					return true;
				}
			}

			this.method_14003(playerEntity);
			return true;
		}
	}
}
