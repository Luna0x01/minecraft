package net.minecraft.entity.thrown;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ThrownEnderpearlEntity extends ThrownItemEntity {
	private LivingEntity owner;

	public ThrownEnderpearlEntity(EntityType<? extends ThrownEnderpearlEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownEnderpearlEntity(World world, LivingEntity livingEntity) {
		super(EntityType.field_6082, livingEntity, world);
		this.owner = livingEntity;
	}

	public ThrownEnderpearlEntity(World world, double d, double e, double f) {
		super(EntityType.field_6082, d, e, f, world);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.field_8634;
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		LivingEntity livingEntity = this.getOwner();
		if (hitResult.getType() == HitResult.Type.field_1331) {
			Entity entity = ((EntityHitResult)hitResult).getEntity();
			if (entity == this.owner) {
				return;
			}

			entity.damage(DamageSource.thrownProjectile(this, livingEntity), 0.0F);
		}

		if (hitResult.getType() == HitResult.Type.field_1332) {
			BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			if (blockEntity instanceof EndGatewayBlockEntity) {
				EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
				if (livingEntity != null) {
					if (livingEntity instanceof ServerPlayerEntity) {
						Criterions.ENTER_BLOCK.trigger((ServerPlayerEntity)livingEntity, this.world.getBlockState(blockPos));
					}

					endGatewayBlockEntity.tryTeleportingEntity(livingEntity);
					this.remove();
					return;
				}

				endGatewayBlockEntity.tryTeleportingEntity(this);
				return;
			}
		}

		for (int i = 0; i < 32; i++) {
			this.world
				.addParticle(
					ParticleTypes.field_11214,
					this.getX(),
					this.getY() + this.random.nextDouble() * 2.0,
					this.getZ(),
					this.random.nextGaussian(),
					0.0,
					this.random.nextGaussian()
				);
		}

		if (!this.world.isClient) {
			if (livingEntity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
				if (serverPlayerEntity.networkHandler.getConnection().isOpen() && serverPlayerEntity.world == this.world && !serverPlayerEntity.isSleeping()) {
					if (this.random.nextFloat() < 0.05F && this.world.getGameRules().getBoolean(GameRules.field_19390)) {
						EndermiteEntity endermiteEntity = EntityType.field_6128.create(this.world);
						endermiteEntity.setPlayerSpawned(true);
						endermiteEntity.refreshPositionAndAngles(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.yaw, livingEntity.pitch);
						this.world.spawnEntity(endermiteEntity);
					}

					if (livingEntity.hasVehicle()) {
						livingEntity.stopRiding();
					}

					livingEntity.requestTeleport(this.getX(), this.getY(), this.getZ());
					livingEntity.fallDistance = 0.0F;
					livingEntity.damage(DamageSource.FALL, 5.0F);
				}
			} else if (livingEntity != null) {
				livingEntity.requestTeleport(this.getX(), this.getY(), this.getZ());
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
	public Entity changeDimension(DimensionType dimensionType) {
		if (this.owner.dimension != dimensionType) {
			this.owner = null;
		}

		return super.changeDimension(dimensionType);
	}
}
