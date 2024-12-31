package net.minecraft.entity.thrown;

import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class EnderPearlEntity extends ThrowableEntity {
	private LivingEntity owner;

	public EnderPearlEntity(World world) {
		super(EntityType.ENDER_PEARL, world);
	}

	public EnderPearlEntity(World world, LivingEntity livingEntity) {
		super(EntityType.ENDER_PEARL, livingEntity, world);
		this.owner = livingEntity;
	}

	public EnderPearlEntity(World world, double d, double e, double f) {
		super(EntityType.ENDER_PEARL, d, e, f, world);
	}

	@Override
	protected void onCollision(BlockHitResult result) {
		LivingEntity livingEntity = this.getOwner();
		if (result.entity != null) {
			if (result.entity == this.owner) {
				return;
			}

			result.entity.damage(DamageSource.thrownProjectile(this, livingEntity), 0.0F);
		}

		if (result.type == BlockHitResult.Type.BLOCK) {
			BlockPos blockPos = result.getBlockPos();
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			if (blockEntity instanceof EndGatewayBlockEntity) {
				EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
				if (livingEntity != null) {
					if (livingEntity instanceof ServerPlayerEntity) {
						AchievementsAndCriterions.field_16332.method_14212((ServerPlayerEntity)livingEntity, this.world.getBlockState(blockPos));
					}

					endGatewayBlockEntity.teleport(livingEntity);
					this.remove();
					return;
				}

				endGatewayBlockEntity.teleport(this);
				return;
			}
		}

		for (int i = 0; i < 32; i++) {
			this.world
				.method_16343(class_4342.field_21361, this.x, this.y + this.random.nextDouble() * 2.0, this.z, this.random.nextGaussian(), 0.0, this.random.nextGaussian());
		}

		if (!this.world.isClient) {
			if (livingEntity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
				if (serverPlayerEntity.networkHandler.getConnection().isOpen() && serverPlayerEntity.world == this.world && !serverPlayerEntity.isSleeping()) {
					if (this.random.nextFloat() < 0.05F && this.world.getGameRules().getBoolean("doMobSpawning")) {
						EndermiteEntity endermiteEntity = new EndermiteEntity(this.world);
						endermiteEntity.setPlayerSpawned(true);
						endermiteEntity.refreshPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
						this.world.method_3686(endermiteEntity);
					}

					if (livingEntity.hasMount()) {
						livingEntity.stopRiding();
					}

					livingEntity.refreshPositionAfterTeleport(this.x, this.y, this.z);
					livingEntity.fallDistance = 0.0F;
					livingEntity.damage(DamageSource.FALL, 5.0F);
				}
			} else if (livingEntity != null) {
				livingEntity.refreshPositionAfterTeleport(this.x, this.y, this.z);
				livingEntity.fallDistance = 0.0F;
			}

			this.remove();
		}
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.getOwner();
		if (livingEntity != null && livingEntity instanceof PlayerEntity && !livingEntity.isAlive()) {
			this.remove();
		} else {
			super.tick();
		}
	}

	@Nullable
	@Override
	public Entity method_15562(DimensionType dimensionType) {
		if (this.field_6932.field_16696 != dimensionType) {
			this.field_6932 = null;
		}

		return super.method_15562(dimensionType);
	}
}
