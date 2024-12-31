package net.minecraft.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElderGuardianEntity extends GuardianEntity {
	public ElderGuardianEntity(World world) {
		super(EntityType.ELDER_GUARDIAN, world);
		this.setBounds(this.width * 2.35F, this.height * 2.35F);
		this.setPersistent();
		if (this.wanderAroundGoal != null) {
			this.wanderAroundGoal.setChance(400);
		}
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3F);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(80.0);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ELDER_GUARDIAN_ENTITIE;
	}

	@Override
	public int getWarmupTime() {
		return 60;
	}

	public void method_14064() {
		this.tailAngle = 1.0F;
		this.prevTailAngle = this.tailAngle;
	}

	@Override
	protected Sound ambientSound() {
		return this.method_15575() ? Sounds.ENTITY_ELDER_GUARDIAN_AMBIENT : Sounds.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return this.method_15575() ? Sounds.ENTITY_ELDER_GUARDIAN_HURT : Sounds.ENTITY_ELDER_GUARDIAN_HURT_LAND;
	}

	@Override
	protected Sound deathSound() {
		return this.method_15575() ? Sounds.ENTITY_ELDER_GUARDIAN_DEATH : Sounds.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
	}

	@Override
	protected Sound method_14091() {
		return Sounds.ENTITY_ELDER_GUARDIAN_FLOP;
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		int i = 1200;
		if ((this.ticksAlive + this.getEntityId()) % 1200 == 0) {
			StatusEffect statusEffect = StatusEffects.MINING_FATIGUE;
			List<ServerPlayerEntity> list = this.world
				.method_16334(
					ServerPlayerEntity.class,
					serverPlayerEntityx -> this.squaredDistanceTo(serverPlayerEntityx) < 2500.0 && serverPlayerEntityx.interactionManager.isSurvival()
				);
			int j = 2;
			int k = 6000;
			int l = 1200;

			for (ServerPlayerEntity serverPlayerEntity : list) {
				if (!serverPlayerEntity.hasStatusEffect(statusEffect)
					|| serverPlayerEntity.getEffectInstance(statusEffect).getAmplifier() < 2
					|| serverPlayerEntity.getEffectInstance(statusEffect).getDuration() < 1200) {
					serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(10, 0.0F));
					serverPlayerEntity.method_2654(new StatusEffectInstance(statusEffect, 6000, 2));
				}
			}
		}

		if (!this.hasPositionTarget()) {
			this.setPositionTarget(new BlockPos(this), 16);
		}
	}
}
