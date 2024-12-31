package net.minecraft.entity.thrown;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class EggEntity extends ThrowableEntity {
	public EggEntity(World world) {
		super(world);
	}

	public EggEntity(World world, LivingEntity livingEntity) {
		super(world, livingEntity);
	}

	public EggEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		ThrowableEntity.registerDataFixes(dataFixer, "ThrownEgg");
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 3) {
			double d = 0.08;

			for (int i = 0; i < 8; i++) {
				this.world
					.addParticle(
						ParticleType.ITEM_CRACK,
						this.x,
						this.y,
						this.z,
						((double)this.random.nextFloat() - 0.5) * 0.08,
						((double)this.random.nextFloat() - 0.5) * 0.08,
						((double)this.random.nextFloat() - 0.5) * 0.08,
						Item.getRawId(Items.EGG)
					);
			}
		}
	}

	@Override
	protected void onCollision(BlockHitResult result) {
		if (result.entity != null) {
			result.entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0F);
		}

		if (!this.world.isClient) {
			if (this.random.nextInt(8) == 0) {
				int i = 1;
				if (this.random.nextInt(32) == 0) {
					i = 4;
				}

				for (int j = 0; j < i; j++) {
					ChickenEntity chickenEntity = new ChickenEntity(this.world);
					chickenEntity.setAge(-24000);
					chickenEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
					this.world.spawnEntity(chickenEntity);
				}
			}

			this.world.sendEntityStatus(this, (byte)3);
			this.remove();
		}
	}
}
