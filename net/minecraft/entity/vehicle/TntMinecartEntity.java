package net.minecraft.entity.vehicle;

import net.minecraft.class_4342;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntMinecartEntity extends AbstractMinecartEntity {
	private int fuseTicks = -1;

	public TntMinecartEntity(World world) {
		super(EntityType.TNT_MINECART, world);
	}

	public TntMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.TNT_MINECART, world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.TNT;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.TNT.getDefaultState();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.fuseTicks > 0) {
			this.fuseTicks--;
			this.world.method_16343(class_4342.field_21363, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
		} else if (this.fuseTicks == 0) {
			this.explode(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		}

		if (this.horizontalCollision) {
			double d = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
			if (d >= 0.01F) {
				this.explode(d);
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		Entity entity = source.getSource();
		if (entity instanceof AbstractArrowEntity) {
			AbstractArrowEntity abstractArrowEntity = (AbstractArrowEntity)entity;
			if (abstractArrowEntity.isOnFire()) {
				this.explode(
					abstractArrowEntity.velocityX * abstractArrowEntity.velocityX
						+ abstractArrowEntity.velocityY * abstractArrowEntity.velocityY
						+ abstractArrowEntity.velocityZ * abstractArrowEntity.velocityZ
				);
			}
		}

		return super.damage(source, amount);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		double d = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
		if (!damageSource.isFire() && !damageSource.isExplosive() && !(d >= 0.01F)) {
			super.dropItems(damageSource);
			if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean("doEntityDrops")) {
				this.method_15560(Blocks.TNT);
			}
		} else {
			if (this.fuseTicks < 0) {
				this.prime();
				this.fuseTicks = this.random.nextInt(20) + this.random.nextInt(20);
			}
		}
	}

	protected void explode(double velocity) {
		if (!this.world.isClient) {
			double d = Math.sqrt(velocity);
			if (d > 5.0) {
				d = 5.0;
			}

			this.world.createExplosion(this, this.x, this.y, this.z, (float)(4.0 + this.random.nextDouble() * 1.5 * d), true);
			this.remove();
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		if (fallDistance >= 3.0F) {
			float f = fallDistance / 10.0F;
			this.explode((double)(f * f));
		}

		super.handleFallDamage(fallDistance, damageMultiplier);
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		if (powered && this.fuseTicks < 0) {
			this.prime();
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 10) {
			this.prime();
		} else {
			super.handleStatus(status);
		}
	}

	public void prime() {
		this.fuseTicks = 80;
		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte)10);
			if (!this.isSilent()) {
				this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	public int getFuseTicks() {
		return this.fuseTicks;
	}

	public boolean isPrimed() {
		return this.fuseTicks > -1;
	}

	@Override
	public float method_10932(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
		return !this.isPrimed() || !blockState.isIn(BlockTags.RAILS) && !blockView.getBlockState(blockPos.up()).isIn(BlockTags.RAILS)
			? super.method_10932(explosion, blockView, blockPos, blockState, fluidState, f)
			: 0.0F;
	}

	@Override
	public boolean method_10933(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
		return !this.isPrimed() || !blockState.isIn(BlockTags.RAILS) && !blockView.getBlockState(blockPos.up()).isIn(BlockTags.RAILS)
			? super.method_10933(explosion, blockView, blockPos, blockState, f)
			: false;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("TNTFuse", 99)) {
			this.fuseTicks = nbt.getInt("TNTFuse");
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("TNTFuse", this.fuseTicks);
	}
}
