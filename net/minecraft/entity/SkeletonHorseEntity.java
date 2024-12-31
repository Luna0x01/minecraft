package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.ai.goal.class_2978;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SkeletonHorseEntity extends AbstractHorseEntity {
	private final class_2978 field_15518 = new class_2978(this);
	private boolean field_15519;
	private int field_15520;

	public SkeletonHorseEntity(World world) {
		super(world);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(15.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
		this.initializeAttribute(field_15508).setBaseValue(this.method_13982());
	}

	@Override
	protected Sound ambientSound() {
		super.ambientSound();
		return Sounds.ENTITY_SKELETON_HORSE_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		super.deathSound();
		return Sounds.ENTITY_SKELETON_HORSE_DEATH;
	}

	@Override
	protected Sound method_13048() {
		super.method_13048();
		return Sounds.ENTITY_SKELETON_HORSE_HURT;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
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

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		AbstractHorseEntity.registerDataFixes(dataFixer, SkeletonHorseEntity.class);
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

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = !itemStack.isEmpty();
		if (bl && itemStack.getItem() == Items.SPAWN_EGG) {
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
			if (bl) {
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
