package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_3558;
import net.minecraft.class_4342;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class PassiveEntity extends PathAwareEntity {
	private static final TrackedData<Boolean> field_14476 = DataTracker.registerData(PassiveEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected int field_11896;
	protected int forcedAge;
	protected int field_11898;
	private float field_6114 = -1.0F;
	private float field_6115;

	protected PassiveEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Nullable
	public abstract PassiveEntity breed(PassiveEntity entity);

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (item instanceof class_3558 && ((class_3558)item).method_16127(itemStack.getNbt(), this.method_15557())) {
			if (!this.world.isClient) {
				PassiveEntity passiveEntity = this.breed(this);
				if (passiveEntity != null) {
					passiveEntity.setAge(-24000);
					passiveEntity.refreshPositionAndAngles(this.x, this.y, this.z, 0.0F, 0.0F);
					this.world.method_3686(passiveEntity);
					if (itemStack.hasCustomName()) {
						passiveEntity.method_15578(itemStack.getName());
					}

					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
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
		this.dataTracker.startTracking(field_14476, false);
	}

	public int age() {
		if (this.world.isClient) {
			return this.dataTracker.get(field_14476) ? -1 : 1;
		} else {
			return this.field_11896;
		}
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
		this.dataTracker.set(field_14476, i < 0);
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
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14476.equals(data)) {
			this.method_5377(this.isBaby());
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient) {
			if (this.field_11898 > 0) {
				if (this.field_11898 % 4 == 0) {
					this.world
						.method_16343(
							class_4342.field_21400,
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
		this.field_6114 = width;
		this.field_6115 = height;
		this.method_5378(1.0F);
	}

	protected final void method_5378(float f) {
		super.setBounds(this.field_6114 * f, this.field_6115 * f);
	}
}
