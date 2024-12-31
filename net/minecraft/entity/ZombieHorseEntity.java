package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ZombieHorseEntity extends AbstractHorseEntity {
	public ZombieHorseEntity(World world) {
		super(world);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		AbstractHorseEntity.registerDataFixes(dataFixer, ZombieHorseEntity.class);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(15.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
		this.initializeAttribute(field_15508).setBaseValue(this.method_13982());
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected Sound ambientSound() {
		super.ambientSound();
		return Sounds.ENTITY_ZOMBIE_HORSE_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		super.deathSound();
		return Sounds.ENTITY_ZOMBIE_HORSE_DEATH;
	}

	@Override
	protected Sound method_13048() {
		super.method_13048();
		return Sounds.ENTITY_ZOMBIE_HORSE_HURT;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ZOMBIE_HORSE_ENTITIE;
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
				if (!this.method_13975() && itemStack.getItem() == Items.SADDLE) {
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
