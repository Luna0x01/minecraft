package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmallFireballEntity extends ExplosiveProjectileEntity {
	public SmallFireballEntity(World world) {
		super(world);
		this.setBounds(0.3125F, 0.3125F);
	}

	public SmallFireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(world, livingEntity, d, e, f);
		this.setBounds(0.3125F, 0.3125F);
	}

	public SmallFireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.setBounds(0.3125F, 0.3125F);
	}

	@Override
	protected void onEntityHit(BlockHitResult hitResult) {
		if (!this.world.isClient) {
			if (hitResult.entity != null) {
				if (!hitResult.entity.isFireImmune()) {
					boolean bl = hitResult.entity.damage(DamageSource.fire(this, this.target), 5.0F);
					if (bl) {
						this.dealDamage(this.target, hitResult.entity);
						hitResult.entity.setOnFireFor(5);
					}
				}
			} else {
				boolean bl2 = true;
				if (this.target != null && this.target instanceof MobEntity) {
					bl2 = this.world.getGameRules().getBoolean("mobGriefing");
				}

				if (bl2) {
					BlockPos blockPos = hitResult.getBlockPos().offset(hitResult.direction);
					if (this.world.isAir(blockPos)) {
						this.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
					}
				}
			}

			this.remove();
		}
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}
}
