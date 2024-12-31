package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemSchema;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemEntity extends Entity {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private int age;
	private int pickupDelay;
	private int health = 5;
	private String thrower;
	private String owner;
	public float hoverHeight = (float)(Math.random() * Math.PI * 2.0);

	public ItemEntity(World world, double d, double e, double f) {
		super(world);
		this.setBounds(0.25F, 0.25F);
		this.updatePosition(d, e, f);
		this.yaw = (float)(Math.random() * 360.0);
		this.velocityX = (double)((float)(Math.random() * 0.2F - 0.1F));
		this.velocityY = 0.2F;
		this.velocityZ = (double)((float)(Math.random() * 0.2F - 0.1F));
	}

	public ItemEntity(World world, double d, double e, double f, ItemStack itemStack) {
		this(world, d, e, f);
		this.setItemStack(itemStack);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	public ItemEntity(World world) {
		super(world);
		this.setBounds(0.25F, 0.25F);
		this.setItemStack(ItemStack.EMPTY);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		if (this.getItemStack().isEmpty()) {
			this.remove();
		} else {
			super.tick();
			if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
				this.pickupDelay--;
			}

			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			double d = this.velocityX;
			double e = this.velocityY;
			double f = this.velocityZ;
			if (!this.hasNoGravity()) {
				this.velocityY -= 0.04F;
			}

			if (this.world.isClient) {
				this.noClip = false;
			} else {
				this.noClip = this.pushOutOfBlocks(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
			}

			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			boolean bl = (int)this.prevX != (int)this.x || (int)this.prevY != (int)this.y || (int)this.prevZ != (int)this.z;
			if (bl || this.ticksAlive % 25 == 0) {
				if (this.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) {
					this.velocityY = 0.2F;
					this.velocityX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
					this.velocityZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
					this.playSound(Sounds.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
				}

				if (!this.world.isClient) {
					this.tryMerge();
				}
			}

			float g = 0.98F;
			if (this.onGround) {
				g = this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z))).getBlock().slipperiness
					* 0.98F;
			}

			this.velocityX *= (double)g;
			this.velocityY *= 0.98F;
			this.velocityZ *= (double)g;
			if (this.onGround) {
				this.velocityY *= -0.5;
			}

			if (this.age != -32768) {
				this.age++;
			}

			this.updateWaterState();
			if (!this.world.isClient) {
				double h = this.velocityX - d;
				double i = this.velocityY - e;
				double j = this.velocityZ - f;
				double k = h * h + i * i + j * j;
				if (k > 0.01) {
					this.velocityDirty = true;
				}
			}

			if (!this.world.isClient && this.age >= 6000) {
				this.remove();
			}
		}
	}

	private void tryMerge() {
		for (ItemEntity itemEntity : this.world.getEntitiesInBox(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5))) {
			this.tryMerge(itemEntity);
		}
	}

	private boolean tryMerge(ItemEntity other) {
		if (other == this) {
			return false;
		} else if (other.isAlive() && this.isAlive()) {
			ItemStack itemStack = this.getItemStack();
			ItemStack itemStack2 = other.getItemStack();
			if (this.pickupDelay == 32767 || other.pickupDelay == 32767) {
				return false;
			} else if (this.age != -32768 && other.age != -32768) {
				if (itemStack2.getItem() != itemStack.getItem()) {
					return false;
				} else if (itemStack2.hasNbt() ^ itemStack.hasNbt()) {
					return false;
				} else if (itemStack2.hasNbt() && !itemStack2.getNbt().equals(itemStack.getNbt())) {
					return false;
				} else if (itemStack2.getItem() == null) {
					return false;
				} else if (itemStack2.getItem().isUnbreakable() && itemStack2.getData() != itemStack.getData()) {
					return false;
				} else if (itemStack2.getCount() < itemStack.getCount()) {
					return other.tryMerge(this);
				} else if (itemStack2.getCount() + itemStack.getCount() > itemStack2.getMaxCount()) {
					return false;
				} else {
					itemStack2.increment(itemStack.getCount());
					other.pickupDelay = Math.max(other.pickupDelay, this.pickupDelay);
					other.age = Math.min(other.age, this.age);
					other.setItemStack(itemStack2);
					this.remove();
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void method_3058() {
		this.age = 4800;
	}

	@Override
	public boolean updateWaterState() {
		if (this.world.method_3610(this.getBoundingBox(), Material.WATER, this)) {
			if (!this.touchingWater && !this.firstUpdate) {
				this.onSwimmingStart();
			}

			this.touchingWater = true;
		} else {
			this.touchingWater = false;
		}

		return this.touchingWater;
	}

	@Override
	protected void burn(int time) {
		this.damage(DamageSource.FIRE, (float)time);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!this.getItemStack().isEmpty() && this.getItemStack().getItem() == Items.NETHER_STAR && source.isExplosive()) {
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

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemSchema(ItemEntity.class, "Item"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("Health", (short)this.health);
		nbt.putShort("Age", (short)this.age);
		nbt.putShort("PickupDelay", (short)this.pickupDelay);
		if (this.getThrower() != null) {
			nbt.putString("Thrower", this.thrower);
		}

		if (this.getOwner() != null) {
			nbt.putString("Owner", this.owner);
		}

		if (!this.getItemStack().isEmpty()) {
			nbt.put("Item", this.getItemStack().toNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.health = nbt.getShort("Health");
		this.age = nbt.getShort("Age");
		if (nbt.contains("PickupDelay")) {
			this.pickupDelay = nbt.getShort("PickupDelay");
		}

		if (nbt.contains("Owner")) {
			this.owner = nbt.getString("Owner");
		}

		if (nbt.contains("Thrower")) {
			this.thrower = nbt.getString("Thrower");
		}

		NbtCompound nbtCompound = nbt.getCompound("Item");
		this.setItemStack(new ItemStack(nbtCompound));
		if (this.getItemStack().isEmpty()) {
			this.remove();
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient) {
			ItemStack itemStack = this.getItemStack();
			Item item = itemStack.getItem();
			int i = itemStack.getCount();
			if (this.pickupDelay == 0
				&& (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(player.getTranslationKey()))
				&& player.inventory.insertStack(itemStack)) {
				player.sendPickup(this, i);
				if (itemStack.isEmpty()) {
					this.remove();
					itemStack.setCount(i);
				}

				player.incrementStat(Stats.picked(item), i);
			}
		}
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.getCustomName() : CommonI18n.translate("item." + this.getItemStack().getTranslationKey());
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Nullable
	@Override
	public Entity changeDimension(int newDimension) {
		Entity entity = super.changeDimension(newDimension);
		if (!this.world.isClient && entity instanceof ItemEntity) {
			((ItemEntity)entity).tryMerge();
		}

		return entity;
	}

	public ItemStack getItemStack() {
		return this.getDataTracker().get(STACK);
	}

	public void setItemStack(ItemStack itemStack) {
		this.getDataTracker().set(STACK, itemStack);
		this.getDataTracker().method_12754(STACK);
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getThrower() {
		return this.thrower;
	}

	public void setThrower(String thrower) {
		this.thrower = thrower;
	}

	public int getAge() {
		return this.age;
	}

	public void setToDefaultPickupDelay() {
		this.pickupDelay = 10;
	}

	public void resetPickupDelay() {
		this.pickupDelay = 0;
	}

	public void setPickupDelayInfinite() {
		this.pickupDelay = 32767;
	}

	public void setPickupDelay(int pickupDelay) {
		this.pickupDelay = pickupDelay;
	}

	public boolean cannotPickup() {
		return this.pickupDelay > 0;
	}

	public void setCovetedItem() {
		this.age = -6000;
	}

	public void setDespawnImmediately() {
		this.setPickupDelayInfinite();
		this.age = 5999;
	}
}
