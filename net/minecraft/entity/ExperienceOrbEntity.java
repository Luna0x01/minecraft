package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ExperienceOrbEntity extends Entity {
	public int renderTicks;
	public int orbAge;
	public int pickupDelay;
	private int health = 5;
	private int amount;
	private PlayerEntity target;
	private int lastTargetUpdateTick;

	public ExperienceOrbEntity(World world, double d, double e, double f, int i) {
		super(world);
		this.setBounds(0.5F, 0.5F);
		this.updatePosition(d, e, f);
		this.yaw = (float)(Math.random() * 360.0);
		this.velocityX = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
		this.velocityY = (double)((float)(Math.random() * 0.2) * 2.0F);
		this.velocityZ = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
		this.amount = i;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	public ExperienceOrbEntity(World world) {
		super(world);
		this.setBounds(0.25F, 0.25F);
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	public int getLightmapCoordinates(float f) {
		float g = 0.5F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		int i = super.getLightmapCoordinates(f);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(g * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.pickupDelay > 0) {
			this.pickupDelay--;
		}

		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.velocityY -= 0.03F;
		if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
			this.velocityY = 0.2F;
			this.velocityX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
			this.velocityZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
			this.playSound(Sounds.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
		}

		this.pushOutOfBlocks(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
		double d = 8.0;
		if (this.lastTargetUpdateTick < this.renderTicks - 20 + this.getEntityId() % 100) {
			if (this.target == null || this.target.squaredDistanceTo(this) > d * d) {
				this.target = this.world.getClosestPlayer(this, d);
			}

			this.lastTargetUpdateTick = this.renderTicks;
		}

		if (this.target != null && this.target.isSpectator()) {
			this.target = null;
		}

		if (this.target != null) {
			double e = (this.target.x - this.x) / d;
			double f = (this.target.y + (double)this.target.getEyeHeight() / 2.0 - this.y) / d;
			double g = (this.target.z - this.z) / d;
			double h = Math.sqrt(e * e + f * f + g * g);
			double i = 1.0 - h;
			if (i > 0.0) {
				i *= i;
				this.velocityX += e / h * i * 0.1;
				this.velocityY += f / h * i * 0.1;
				this.velocityZ += g / h * i * 0.1;
			}
		}

		this.move(this.velocityX, this.velocityY, this.velocityZ);
		float j = 0.98F;
		if (this.onGround) {
			j = this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z))).getBlock().slipperiness
				* 0.98F;
		}

		this.velocityX *= (double)j;
		this.velocityY *= 0.98F;
		this.velocityZ *= (double)j;
		if (this.onGround) {
			this.velocityY *= -0.9F;
		}

		this.renderTicks++;
		this.orbAge++;
		if (this.orbAge >= 6000) {
			this.remove();
		}
	}

	@Override
	public boolean updateWaterState() {
		return this.world.method_3610(this.getBoundingBox(), Material.WATER, this);
	}

	@Override
	protected void burn(int time) {
		this.damage(DamageSource.FIRE, (float)time);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			this.health = (int)((float)this.health - amount);
			if (this.health <= 0) {
				this.remove();
			}

			return false;
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("Health", (short)this.health);
		nbt.putShort("Age", (short)this.orbAge);
		nbt.putShort("Value", (short)this.amount);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.health = nbt.getShort("Health");
		this.orbAge = nbt.getShort("Age");
		this.amount = nbt.getShort("Value");
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient) {
			if (this.pickupDelay == 0 && player.experiencePickUpDelay == 0) {
				player.experiencePickUpDelay = 2;
				this.world
					.playSound(
						null,
						player.x,
						player.y,
						player.z,
						Sounds.ENTITY_EXPERIENCE_ORB_TOUCH,
						SoundCategory.PLAYERS,
						0.1F,
						0.5F * ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.8F)
					);
				player.sendPickup(this, 1);
				ItemStack itemStack = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, player);
				if (itemStack != null && itemStack.isDamaged()) {
					int i = Math.min(this.getMendingRepairAmount(this.amount), itemStack.getDamage());
					this.amount = this.amount - this.getMendingRepairCost(i);
					itemStack.setDamage(itemStack.getDamage() - i);
				}

				if (this.amount > 0) {
					player.addExperience(this.amount);
				}

				this.remove();
			}
		}
	}

	private int getMendingRepairCost(int repairAmount) {
		return repairAmount / 2;
	}

	private int getMendingRepairAmount(int experienceAmount) {
		return experienceAmount * 2;
	}

	public int getExperienceAmount() {
		return this.amount;
	}

	public int getOrbSize() {
		if (this.amount >= 2477) {
			return 10;
		} else if (this.amount >= 1237) {
			return 9;
		} else if (this.amount >= 617) {
			return 8;
		} else if (this.amount >= 307) {
			return 7;
		} else if (this.amount >= 149) {
			return 6;
		} else if (this.amount >= 73) {
			return 5;
		} else if (this.amount >= 37) {
			return 4;
		} else if (this.amount >= 17) {
			return 3;
		} else if (this.amount >= 7) {
			return 2;
		} else {
			return this.amount >= 3 ? 1 : 0;
		}
	}

	public static int roundToOrbSize(int value) {
		if (value >= 2477) {
			return 2477;
		} else if (value >= 1237) {
			return 1237;
		} else if (value >= 617) {
			return 617;
		} else if (value >= 307) {
			return 307;
		} else if (value >= 149) {
			return 149;
		} else if (value >= 73) {
			return 73;
		} else if (value >= 37) {
			return 37;
		} else if (value >= 17) {
			return 17;
		} else if (value >= 7) {
			return 7;
		} else {
			return value >= 3 ? 3 : 1;
		}
	}

	@Override
	public boolean isAttackable() {
		return false;
	}
}
