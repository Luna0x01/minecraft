package net.minecraft.entity.passive;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class PassiveEntity extends PathAwareEntity {
	protected int field_11896;
	protected int forcedAge;
	protected int field_11898;
	private float field_6114 = -1.0F;
	private float field_6115;

	public PassiveEntity(World world) {
		super(world);
	}

	public abstract PassiveEntity breed(PassiveEntity entity);

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.getMainHandStack();
		if (itemStack != null && itemStack.getItem() == Items.SPAWN_EGG) {
			if (!this.world.isClient) {
				Class<? extends Entity> class_ = EntityType.getEntityById(itemStack.getData());
				if (class_ != null && this.getClass() == class_) {
					PassiveEntity passiveEntity = this.breed(this);
					if (passiveEntity != null) {
						passiveEntity.setAge(-24000);
						passiveEntity.refreshPositionAndAngles(this.x, this.y, this.z, 0.0F, 0.0F);
						this.world.spawnEntity(passiveEntity);
						if (itemStack.hasCustomName()) {
							passiveEntity.setCustomName(itemStack.getCustomName());
						}

						if (!playerEntity.abilities.creativeMode) {
							itemStack.count--;
							if (itemStack.count <= 0) {
								playerEntity.inventory.setInvStack(playerEntity.inventory.selectedSlot, null);
							}
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(12, (byte)0);
	}

	public int age() {
		return this.world.isClient ? this.dataTracker.getByte(12) : this.field_11896;
	}

	public void method_10925(int i, boolean bl) {
		int j = this.age();
		j += i * 20;
		if (j > 0) {
			j = 0;
			if (j < 0) {
				this.method_10926();
			}
		}

		int l = j - j;
		this.setAge(j);
		if (bl) {
			this.forcedAge += l;
			if (this.field_11898 == 0) {
				this.field_11898 = 40;
			}
		}

		if (this.age() == 0) {
			this.setAge(this.forcedAge);
		}
	}

	public void method_6095(int i) {
		this.method_10925(i, false);
	}

	public void setAge(int i) {
		this.dataTracker.setProperty(12, (byte)MathHelper.clamp(i, -1, 1));
		this.field_11896 = i;
		this.method_5377(this.isBaby());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Age", this.age());
		nbt.putInt("ForcedAge", this.forcedAge);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setAge(nbt.getInt("Age"));
		this.forcedAge = nbt.getInt("ForcedAge");
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient) {
			if (this.field_11898 > 0) {
				if (this.field_11898 % 4 == 0) {
					this.world
						.addParticle(
							ParticleType.HAPPY_VILLAGER,
							this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
							this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
							this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
							0.0,
							0.0,
							0.0
						);
				}

				this.field_11898--;
			}

			this.method_5377(this.isBaby());
		} else {
			int i = this.age();
			if (i < 0) {
				this.setAge(++i);
				if (i == 0) {
					this.method_10926();
				}
			} else if (i > 0) {
				this.setAge(--i);
			}
		}
	}

	protected void method_10926() {
	}

	@Override
	public boolean isBaby() {
		return this.age() < 0;
	}

	public void method_5377(boolean bl) {
		this.method_5378(bl ? 0.5F : 1.0F);
	}

	@Override
	protected final void setBounds(float width, float height) {
		boolean bl = this.field_6114 > 0.0F;
		this.field_6114 = width;
		this.field_6115 = height;
		if (!bl) {
			this.method_5378(1.0F);
		}
	}

	protected final void method_5378(float f) {
		super.setBounds(this.field_6114 * f, this.field_6115 * f);
	}
}
