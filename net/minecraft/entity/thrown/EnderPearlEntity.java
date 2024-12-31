package net.minecraft.entity.thrown;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnderPearlEntity extends ThrowableEntity {
	private LivingEntity owner;

	public EnderPearlEntity(World world) {
		super(world);
	}

	public EnderPearlEntity(World world, LivingEntity livingEntity) {
		super(world, livingEntity);
		this.owner = livingEntity;
	}

	public EnderPearlEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
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
				.addParticle(
					ParticleType.NETHER_PORTAL, this.x, this.y + this.random.nextDouble() * 2.0, this.z, this.random.nextGaussian(), 0.0, this.random.nextGaussian()
				);
		}

		if (!this.world.isClient) {
			if (livingEntity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
				if (serverPlayerEntity.networkHandler.getConnection().isOpen() && serverPlayerEntity.world == this.world && !serverPlayerEntity.isSleeping()) {
					if (this.random.nextFloat() < 0.05F && this.world.getGameRules().getBoolean("doMobSpawning")) {
						EndermiteEntity endermiteEntity = new EndermiteEntity(this.world);
						endermiteEntity.setPlayerSpawned(true);
						endermiteEntity.refreshPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
						this.world.spawnEntity(endermiteEntity);
					}

					if (livingEntity.hasMount()) {
						this.stopRiding();
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
}
