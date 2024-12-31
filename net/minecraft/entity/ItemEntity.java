package net.minecraft.entity;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ItemEntity extends Entity {
	private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private int age;
	private int pickupDelay;
	private int health = 5;
	private UUID field_17024;
	private UUID field_17025;
	public float hoverHeight = (float)(Math.random() * Math.PI * 2.0);

	public ItemEntity(World world) {
		super(EntityType.ITEM, world);
		this.setBounds(0.25F, 0.25F);
	}

	public ItemEntity(World world, double d, double e, double f) {
		this(world);
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
			if (this.method_15567(FluidTags.WATER)) {
				this.method_15849();
			} else if (!this.hasNoGravity()) {
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
				if (this.world.getFluidState(new BlockPos(this)).matches(FluidTags.LAVA)) {
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
				g = this.world
						.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
						.getBlock()
						.getSlipperiness()
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

			this.velocityDirty = this.velocityDirty | this.updateWaterState();
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

	private void method_15849() {
		if (this.velocityY < 0.06F) {
			this.velocityY += 5.0E-4F;
		}

		this.velocityX *= 0.99F;
		this.velocityZ *= 0.99F;
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
			ItemStack itemStack2 = other.getItemStack().copy();
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

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("Health", (short)this.health);
		nbt.putShort("Age", (short)this.age);
		nbt.putShort("PickupDelay", (short)this.pickupDelay);
		if (this.method_8401() != null) {
			nbt.put("Thrower", NbtHelper.fromUuid(this.method_8401()));
		}

		if (this.method_8400() != null) {
			nbt.put("Owner", NbtHelper.fromUuid(this.method_8400()));
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

		if (nbt.contains("Owner", 10)) {
			this.field_17025 = NbtHelper.toUuid(nbt.getCompound("Owner"));
		}

		if (nbt.contains("Thrower", 10)) {
			this.field_17024 = NbtHelper.toUuid(nbt.getCompound("Thrower"));
		}

		NbtCompound nbtCompound = nbt.getCompound("Item");
		this.setItemStack(ItemStack.from(nbtCompound));
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
				&& (this.field_17025 == null || 6000 - this.age <= 200 || this.field_17025.equals(player.getUuid()))
				&& player.inventory.insertStack(itemStack)) {
				player.sendPickup(this, i);
				if (itemStack.isEmpty()) {
					this.remove();
					itemStack.setCount(i);
				}

				player.method_15930(Stats.PICKED_UP.method_21429(item), i);
			}
		}
	}

	@Override
	public Text method_15540() {
		Text text = this.method_15541();
		return (Text)(text != null ? text : new TranslatableText(this.getItemStack().getTranslationKey()));
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Nullable
	@Override
	public Entity method_15562(DimensionType dimensionType) {
		Entity entity = super.method_15562(dimensionType);
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
	}

	@Nullable
	public UUID method_8400() {
		return this.field_17025;
	}

	public void method_15847(@Nullable UUID uUID) {
		this.field_17025 = uUID;
	}

	@Nullable
	public UUID method_8401() {
		return this.field_17024;
	}

	public void method_15848(@Nullable UUID uUID) {
		this.field_17024 = uUID;
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
