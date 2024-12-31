package net.minecraft;

import net.minecraft.block.Blocks;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class class_3135 extends AbstractHorseEntity {
	private static final TrackedData<Boolean> field_15488 = DataTracker.registerData(class_3135.class, TrackedDataHandlerRegistry.BOOLEAN);

	protected class_3135(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.field_15493 = false;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15488, false);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)this.method_13981());
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.175F);
		this.initializeAttribute(field_15508).setBaseValue(0.5);
	}

	public boolean method_13963() {
		return this.dataTracker.get(field_15488);
	}

	public void method_13966(boolean bl) {
		this.dataTracker.set(field_15488, bl);
	}

	@Override
	protected int method_13987() {
		return this.method_13963() ? 17 : super.method_13987();
	}

	@Override
	public double getMountedHeightOffset() {
		return super.getMountedHeightOffset() - 0.25;
	}

	@Override
	protected Sound method_13132() {
		super.method_13132();
		return Sounds.ENTITY_DONKEY_ANGRY;
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (this.method_13963()) {
			if (!this.world.isClient) {
				this.method_15560(Blocks.CHEST);
			}

			this.method_13966(false);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("ChestedHorse", this.method_13963());
		if (this.method_13963()) {
			NbtList nbtList = new NbtList();

			for (int i = 2; i < this.animalInventory.getInvSize(); i++) {
				ItemStack itemStack = this.animalInventory.getInvStack(i);
				if (!itemStack.isEmpty()) {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.putByte("Slot", (byte)i);
					itemStack.toNbt(nbtCompound);
					nbtList.add((NbtElement)nbtCompound);
				}
			}

			nbt.put("Items", nbtList);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_13966(nbt.getBoolean("ChestedHorse"));
		if (this.method_13963()) {
			NbtList nbtList = nbt.getList("Items", 10);
			this.method_13998();

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				int j = nbtCompound.getByte("Slot") & 255;
				if (j >= 2 && j < this.animalInventory.getInvSize()) {
					this.animalInventory.setInvStack(j, ItemStack.from(nbtCompound));
				}
			}
		}

		this.method_6244();
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (slot == 499) {
			if (this.method_13963() && item.isEmpty()) {
				this.method_13966(false);
				this.method_13998();
				return true;
			}

			if (!this.method_13963() && item.getItem() == Blocks.CHEST.getItem()) {
				this.method_13966(true);
				this.method_13998();
				return true;
			}
		}

		return super.equip(slot, item);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() instanceof class_3558) {
			return super.interactMob(playerEntity, hand);
		} else {
			if (!this.isBaby()) {
				if (this.method_13990() && playerEntity.isSneaking()) {
					this.method_14000(playerEntity);
					return true;
				}

				if (this.hasPassengers()) {
					return super.interactMob(playerEntity, hand);
				}
			}

			if (!itemStack.isEmpty()) {
				boolean bl = this.method_13970(playerEntity, itemStack);
				if (!bl) {
					if (!this.method_13990() || itemStack.getItem() == Items.NAME_TAG) {
						if (itemStack.method_6329(playerEntity, this, hand)) {
							return true;
						} else {
							this.method_13979();
							return true;
						}
					}

					if (!this.method_13963() && itemStack.getItem() == Blocks.CHEST.getItem()) {
						this.method_13966(true);
						this.method_13964();
						bl = true;
						this.method_13998();
					}

					if (!this.isBaby() && !this.method_13975() && itemStack.getItem() == Items.SADDLE) {
						this.method_14000(playerEntity);
						return true;
					}
				}

				if (bl) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					return true;
				}
			}

			if (this.isBaby()) {
				return super.interactMob(playerEntity, hand);
			} else {
				this.method_14003(playerEntity);
				return true;
			}
		}
	}

	protected void method_13964() {
		this.playSound(Sounds.ENTITY_DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	public int method_13965() {
		return 5;
	}
}
