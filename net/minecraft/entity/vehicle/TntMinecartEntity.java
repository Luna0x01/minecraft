package net.minecraft.entity.vehicle;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntMinecartEntity extends AbstractMinecartEntity {
	private int fuseTicks = -1;

	public TntMinecartEntity(World world) {
		super(world);
	}

	public TntMinecartEntity(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		AbstractMinecartEntity.method_13302(dataFixer, "MinecartTNT");
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
			this.world.addParticle(ParticleType.SMOKE, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
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
		super.dropItems(damageSource);
		double d = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
		if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.dropItem(new ItemStack(Blocks.TNT, 1), 0.0F);
		}

		if (damageSource.isFire() || damageSource.isExplosive() || d >= 0.01F) {
			this.explode(d);
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
	public float getBlastResistance(Explosion explosion, World world, BlockPos pos, BlockState state) {
		return !this.isPrimed() || !AbstractRailBlock.isRail(state) && !AbstractRailBlock.isRail(world, pos.up())
			? super.getBlastResistance(explosion, world, pos, state)
			: 0.0F;
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosion, World world, BlockPos pos, BlockState state, float explosionPower) {
		return !this.isPrimed() || !AbstractRailBlock.isRail(state) && !AbstractRailBlock.isRail(world, pos.up())
			? super.canExplosionDestroyBlock(explosion, world, pos, state, explosionPower)
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
