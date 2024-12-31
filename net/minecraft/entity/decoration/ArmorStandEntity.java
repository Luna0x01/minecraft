package net.minecraft.entity.decoration;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArmorStandEntity extends LivingEntity {
	private static final EulerAngle DEFAULT_HEAD_ANGLE = new EulerAngle(0.0F, 0.0F, 0.0F);
	private static final EulerAngle DEFAULT_BODY_ANGLE = new EulerAngle(0.0F, 0.0F, 0.0F);
	private static final EulerAngle DEFAULT_LEFT_ARM_ANGLE = new EulerAngle(-10.0F, 0.0F, -10.0F);
	private static final EulerAngle DEFAULT_RIGHT_ARM_ANGLE = new EulerAngle(-15.0F, 0.0F, 10.0F);
	private static final EulerAngle DEFAULT_LEFT_LEG_ANGLE = new EulerAngle(-1.0F, 0.0F, -1.0F);
	private static final EulerAngle DEFAULT_RIGHT_LEG_ANGLE = new EulerAngle(1.0F, 0.0F, 1.0F);
	private final ItemStack[] inventory = new ItemStack[5];
	private boolean invisible;
	private long lastHitTime;
	private int disabledSlots;
	private boolean marker;
	private EulerAngle headAngle = DEFAULT_HEAD_ANGLE;
	private EulerAngle bodyAngle = DEFAULT_BODY_ANGLE;
	private EulerAngle leftArmAngle = DEFAULT_LEFT_ARM_ANGLE;
	private EulerAngle rightArmAngle = DEFAULT_RIGHT_ARM_ANGLE;
	private EulerAngle leftLegAngle = DEFAULT_LEFT_LEG_ANGLE;
	private EulerAngle rightLegAngle = DEFAULT_RIGHT_LEG_ANGLE;

	public ArmorStandEntity(World world) {
		super(world);
		this.setSilent(true);
		this.noClip = this.hasNoGravity();
		this.setBounds(0.5F, 1.975F);
	}

	public ArmorStandEntity(World world, double d, double e, double f) {
		this(world);
		this.updatePosition(d, e, f);
	}

	@Override
	public boolean canMoveVoluntarily() {
		return super.canMoveVoluntarily() && !this.hasNoGravity();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(10, (byte)0);
		this.dataTracker.track(11, DEFAULT_HEAD_ANGLE);
		this.dataTracker.track(12, DEFAULT_BODY_ANGLE);
		this.dataTracker.track(13, DEFAULT_LEFT_ARM_ANGLE);
		this.dataTracker.track(14, DEFAULT_RIGHT_ARM_ANGLE);
		this.dataTracker.track(15, DEFAULT_LEFT_LEG_ANGLE);
		this.dataTracker.track(16, DEFAULT_RIGHT_LEG_ANGLE);
	}

	@Override
	public ItemStack getStackInHand() {
		return this.inventory[0];
	}

	@Override
	public ItemStack getMainSlot(int slot) {
		return this.inventory[slot];
	}

	@Override
	public ItemStack getArmorSlot(int i) {
		return this.inventory[i + 1];
	}

	@Override
	public void setArmorSlot(int armorSlot, ItemStack item) {
		this.inventory[armorSlot] = item;
	}

	@Override
	public ItemStack[] getArmorStacks() {
		return this.inventory;
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		int i;
		if (slot == 99) {
			i = 0;
		} else {
			i = slot - 100 + 1;
			if (i < 0 || i >= this.inventory.length) {
				return false;
			}
		}

		if (item != null && MobEntity.getEquipableSlot(item) != i && (i != 4 || !(item.getItem() instanceof BlockItem))) {
			return false;
		} else {
			this.setArmorSlot(i, item);
			return true;
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.inventory.length; i++) {
			NbtCompound nbtCompound = new NbtCompound();
			if (this.inventory[i] != null) {
				this.inventory[i].toNbt(nbtCompound);
			}

			nbtList.add(nbtCompound);
		}

		nbt.put("Equipment", nbtList);
		if (this.isCustomNameVisible() && (this.getCustomName() == null || this.getCustomName().length() == 0)) {
			nbt.putBoolean("CustomNameVisible", this.isCustomNameVisible());
		}

		nbt.putBoolean("Invisible", this.isInvisible());
		nbt.putBoolean("Small", this.isSmall());
		nbt.putBoolean("ShowArms", this.shouldShowArms());
		nbt.putInt("DisabledSlots", this.disabledSlots);
		nbt.putBoolean("NoGravity", this.hasNoGravity());
		nbt.putBoolean("NoBasePlate", this.hasNoBasePlate());
		if (this.shouldShowName()) {
			nbt.putBoolean("Marker", this.shouldShowName());
		}

		nbt.put("Pose", this.getPose());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Equipment", 9)) {
			NbtList nbtList = nbt.getList("Equipment", 10);

			for (int i = 0; i < this.inventory.length; i++) {
				this.inventory[i] = ItemStack.fromNbt(nbtList.getCompound(i));
			}
		}

		this.setInvisible(nbt.getBoolean("Invisible"));
		this.setSmall(nbt.getBoolean("Small"));
		this.setShowArms(nbt.getBoolean("ShowArms"));
		this.disabledSlots = nbt.getInt("DisabledSlots");
		this.setNoGravity(nbt.getBoolean("NoGravity"));
		this.setNoBasePlate(nbt.getBoolean("NoBasePlate"));
		this.setShouldShowName(nbt.getBoolean("Marker"));
		this.marker = !this.shouldShowName();
		this.noClip = this.hasNoGravity();
		NbtCompound nbtCompound = nbt.getCompound("Pose");
		this.setPose(nbtCompound);
	}

	private void setPose(NbtCompound nbt) {
		NbtList nbtList = nbt.getList("Head", 5);
		if (nbtList.size() > 0) {
			this.setHeadAngle(new EulerAngle(nbtList));
		} else {
			this.setHeadAngle(DEFAULT_HEAD_ANGLE);
		}

		NbtList nbtList2 = nbt.getList("Body", 5);
		if (nbtList2.size() > 0) {
			this.setBodyAngle(new EulerAngle(nbtList2));
		} else {
			this.setBodyAngle(DEFAULT_BODY_ANGLE);
		}

		NbtList nbtList3 = nbt.getList("LeftArm", 5);
		if (nbtList3.size() > 0) {
			this.setLeftArmAngle(new EulerAngle(nbtList3));
		} else {
			this.setLeftArmAngle(DEFAULT_LEFT_ARM_ANGLE);
		}

		NbtList nbtList4 = nbt.getList("RightArm", 5);
		if (nbtList4.size() > 0) {
			this.setRightArmAngle(new EulerAngle(nbtList4));
		} else {
			this.setRightArmAngle(DEFAULT_RIGHT_ARM_ANGLE);
		}

		NbtList nbtList5 = nbt.getList("LeftLeg", 5);
		if (nbtList5.size() > 0) {
			this.setLeftLegAngle(new EulerAngle(nbtList5));
		} else {
			this.setLeftLegAngle(DEFAULT_LEFT_LEG_ANGLE);
		}

		NbtList nbtList6 = nbt.getList("RightLeg", 5);
		if (nbtList6.size() > 0) {
			this.setRightLegAngle(new EulerAngle(nbtList6));
		} else {
			this.setRightLegAngle(DEFAULT_RIGHT_LEG_ANGLE);
		}
	}

	private NbtCompound getPose() {
		NbtCompound nbtCompound = new NbtCompound();
		if (!DEFAULT_HEAD_ANGLE.equals(this.headAngle)) {
			nbtCompound.put("Head", this.headAngle.serialize());
		}

		if (!DEFAULT_BODY_ANGLE.equals(this.bodyAngle)) {
			nbtCompound.put("Body", this.bodyAngle.serialize());
		}

		if (!DEFAULT_LEFT_ARM_ANGLE.equals(this.leftArmAngle)) {
			nbtCompound.put("LeftArm", this.leftArmAngle.serialize());
		}

		if (!DEFAULT_RIGHT_ARM_ANGLE.equals(this.rightArmAngle)) {
			nbtCompound.put("RightArm", this.rightArmAngle.serialize());
		}

		if (!DEFAULT_LEFT_LEG_ANGLE.equals(this.leftLegAngle)) {
			nbtCompound.put("LeftLeg", this.leftLegAngle.serialize());
		}

		if (!DEFAULT_RIGHT_LEG_ANGLE.equals(this.rightLegAngle)) {
			nbtCompound.put("RightLeg", this.rightLegAngle.serialize());
		}

		return nbtCompound;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void pushAway(Entity entity) {
	}

	@Override
	protected void tickCramming() {
		List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox());
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (entity instanceof AbstractMinecartEntity
					&& ((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE
					&& this.squaredDistanceTo(entity) <= 0.2) {
					entity.pushAwayFrom(this);
				}
			}
		}
	}

	@Override
	public boolean interactAt(PlayerEntity player, Vec3d hitPos) {
		if (this.shouldShowName()) {
			return false;
		} else if (!this.world.isClient && !player.isSpectator()) {
			int i = 0;
			ItemStack itemStack = player.getMainHandStack();
			boolean bl = itemStack != null;
			if (bl && itemStack.getItem() instanceof ArmorItem) {
				ArmorItem armorItem = (ArmorItem)itemStack.getItem();
				if (armorItem.slot == 3) {
					i = 1;
				} else if (armorItem.slot == 2) {
					i = 2;
				} else if (armorItem.slot == 1) {
					i = 3;
				} else if (armorItem.slot == 0) {
					i = 4;
				}
			}

			if (bl && (itemStack.getItem() == Items.SKULL || itemStack.getItem() == Item.fromBlock(Blocks.PUMPKIN))) {
				i = 4;
			}

			double d = 0.1;
			double e = 0.9;
			double f = 0.4;
			double g = 1.6;
			int j = 0;
			boolean bl2 = this.isSmall();
			double h = bl2 ? hitPos.y * 2.0 : hitPos.y;
			if (h >= 0.1 && h < 0.1 + (bl2 ? 0.8 : 0.45) && this.inventory[1] != null) {
				j = 1;
			} else if (h >= 0.9 + (bl2 ? 0.3 : 0.0) && h < 0.9 + (bl2 ? 1.0 : 0.7) && this.inventory[3] != null) {
				j = 3;
			} else if (h >= 0.4 && h < 0.4 + (bl2 ? 1.0 : 0.8) && this.inventory[2] != null) {
				j = 2;
			} else if (h >= 1.6 && this.inventory[4] != null) {
				j = 4;
			}

			boolean bl3 = this.inventory[j] != null;
			if ((this.disabledSlots & 1 << j) != 0 || (this.disabledSlots & 1 << i) != 0) {
				j = i;
				if ((this.disabledSlots & 1 << i) != 0) {
					if ((this.disabledSlots & 1) != 0) {
						return true;
					}

					j = 0;
				}
			}

			if (bl && i == 0 && !this.shouldShowArms()) {
				return true;
			} else {
				if (bl) {
					this.method_11121(player, i);
				} else if (bl3) {
					this.method_11121(player, j);
				}

				return true;
			}
		} else {
			return true;
		}
	}

	private void method_11121(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = this.inventory[i];
		if (itemStack == null || (this.disabledSlots & 1 << i + 8) == 0) {
			if (itemStack != null || (this.disabledSlots & 1 << i + 16) == 0) {
				int j = playerEntity.inventory.selectedSlot;
				ItemStack itemStack2 = playerEntity.inventory.getInvStack(j);
				if (playerEntity.abilities.creativeMode && (itemStack == null || itemStack.getItem() == Item.fromBlock(Blocks.AIR)) && itemStack2 != null) {
					ItemStack itemStack3 = itemStack2.copy();
					itemStack3.count = 1;
					this.setArmorSlot(i, itemStack3);
				} else if (itemStack2 == null || itemStack2.count <= 1) {
					this.setArmorSlot(i, itemStack2);
					playerEntity.inventory.setInvStack(j, itemStack);
				} else if (itemStack == null) {
					ItemStack itemStack4 = itemStack2.copy();
					itemStack4.count = 1;
					this.setArmorSlot(i, itemStack4);
					itemStack2.count--;
				}
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.world.isClient) {
			return false;
		} else if (DamageSource.OUT_OF_WORLD.equals(source)) {
			this.remove();
			return false;
		} else if (this.isInvulnerableTo(source) || this.invisible || this.shouldShowName()) {
			return false;
		} else if (source.isExplosive()) {
			this.onBreak();
			this.remove();
			return false;
		} else if (DamageSource.FIRE.equals(source)) {
			if (!this.isOnFire()) {
				this.setOnFireFor(5);
			} else {
				this.updateHealth(0.15F);
			}

			return false;
		} else if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F) {
			this.updateHealth(4.0F);
			return false;
		} else {
			boolean bl = "arrow".equals(source.getName());
			boolean bl2 = "player".equals(source.getName());
			if (!bl2 && !bl) {
				return false;
			} else {
				if (source.getSource() instanceof AbstractArrowEntity) {
					source.getSource().remove();
				}

				if (source.getAttacker() instanceof PlayerEntity && !((PlayerEntity)source.getAttacker()).abilities.allowModifyWorld) {
					return false;
				} else if (source.isSourceCreativePlayer()) {
					this.spawnBreakParticles();
					this.remove();
					return false;
				} else {
					long l = this.world.getLastUpdateTime();
					if (l - this.lastHitTime > 5L && !bl) {
						this.lastHitTime = l;
					} else {
						this.method_11117();
						this.spawnBreakParticles();
						this.remove();
					}

					return false;
				}
			}
		}
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverage() * 4.0;
		if (Double.isNaN(d) || d == 0.0) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	private void spawnBreakParticles() {
		if (this.world instanceof ServerWorld) {
			((ServerWorld)this.world)
				.addParticle(
					ParticleType.BLOCK_DUST,
					this.x,
					this.y + (double)this.height / 1.5,
					this.z,
					10,
					(double)(this.width / 4.0F),
					(double)(this.height / 4.0F),
					(double)(this.width / 4.0F),
					0.05,
					Block.getByBlockState(Blocks.PLANKS.getDefaultState())
				);
		}
	}

	private void updateHealth(float damage) {
		float f = this.getHealth();
		f -= damage;
		if (f <= 0.5F) {
			this.onBreak();
			this.remove();
		} else {
			this.setHealth(f);
		}
	}

	private void method_11117() {
		Block.onBlockBreak(this.world, new BlockPos(this), new ItemStack(Items.ARMOR_STAND));
		this.onBreak();
	}

	private void onBreak() {
		for (int i = 0; i < this.inventory.length; i++) {
			if (this.inventory[i] != null && this.inventory[i].count > 0) {
				if (this.inventory[i] != null) {
					Block.onBlockBreak(this.world, new BlockPos(this).up(), this.inventory[i]);
				}

				this.inventory[i] = null;
			}
		}
	}

	@Override
	protected float turnHead(float bodyRotation, float headRotation) {
		this.prevBodyYaw = this.prevYaw;
		this.bodyYaw = this.yaw;
		return 0.0F;
	}

	@Override
	public float getEyeHeight() {
		return this.isBaby() ? this.height * 0.5F : this.height * 0.9F;
	}

	@Override
	public void travel(float f, float g) {
		if (!this.hasNoGravity()) {
			super.travel(f, g);
		}
	}

	@Override
	public void tick() {
		super.tick();
		EulerAngle eulerAngle = this.dataTracker.method_10992(11);
		if (!this.headAngle.equals(eulerAngle)) {
			this.setHeadAngle(eulerAngle);
		}

		EulerAngle eulerAngle2 = this.dataTracker.method_10992(12);
		if (!this.bodyAngle.equals(eulerAngle2)) {
			this.setBodyAngle(eulerAngle2);
		}

		EulerAngle eulerAngle3 = this.dataTracker.method_10992(13);
		if (!this.leftArmAngle.equals(eulerAngle3)) {
			this.setLeftArmAngle(eulerAngle3);
		}

		EulerAngle eulerAngle4 = this.dataTracker.method_10992(14);
		if (!this.rightArmAngle.equals(eulerAngle4)) {
			this.setRightArmAngle(eulerAngle4);
		}

		EulerAngle eulerAngle5 = this.dataTracker.method_10992(15);
		if (!this.leftLegAngle.equals(eulerAngle5)) {
			this.setLeftLegAngle(eulerAngle5);
		}

		EulerAngle eulerAngle6 = this.dataTracker.method_10992(16);
		if (!this.rightLegAngle.equals(eulerAngle6)) {
			this.setRightLegAngle(eulerAngle6);
		}

		boolean bl = this.shouldShowName();
		if (!this.marker && bl) {
			this.method_11122(false);
		} else {
			if (!this.marker || bl) {
				return;
			}

			this.method_11122(true);
		}

		this.marker = bl;
	}

	private void method_11122(boolean bl) {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		if (bl) {
			this.setBounds(0.5F, 1.975F);
		} else {
			this.setBounds(0.0F, 0.0F);
		}

		this.updatePosition(d, e, f);
	}

	@Override
	protected void updatePotionVisibility() {
		this.setInvisible(this.invisible);
	}

	@Override
	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
		super.setInvisible(invisible);
	}

	@Override
	public boolean isBaby() {
		return this.isSmall();
	}

	@Override
	public void kill() {
		this.remove();
	}

	@Override
	public boolean isImmuneToExplosion() {
		return this.isInvisible();
	}

	private void setSmall(boolean value) {
		byte b = this.dataTracker.getByte(10);
		if (value) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.setProperty(10, b);
	}

	public boolean isSmall() {
		return (this.dataTracker.getByte(10) & 1) != 0;
	}

	private void setNoGravity(boolean value) {
		byte b = this.dataTracker.getByte(10);
		if (value) {
			b = (byte)(b | 2);
		} else {
			b = (byte)(b & -3);
		}

		this.dataTracker.setProperty(10, b);
	}

	public boolean hasNoGravity() {
		return (this.dataTracker.getByte(10) & 2) != 0;
	}

	private void setShowArms(boolean value) {
		byte b = this.dataTracker.getByte(10);
		if (value) {
			b = (byte)(b | 4);
		} else {
			b = (byte)(b & -5);
		}

		this.dataTracker.setProperty(10, b);
	}

	public boolean shouldShowArms() {
		return (this.dataTracker.getByte(10) & 4) != 0;
	}

	private void setNoBasePlate(boolean value) {
		byte b = this.dataTracker.getByte(10);
		if (value) {
			b = (byte)(b | 8);
		} else {
			b = (byte)(b & -9);
		}

		this.dataTracker.setProperty(10, b);
	}

	public boolean hasNoBasePlate() {
		return (this.dataTracker.getByte(10) & 8) != 0;
	}

	private void setShouldShowName(boolean value) {
		byte b = this.dataTracker.getByte(10);
		if (value) {
			b = (byte)(b | 16);
		} else {
			b = (byte)(b & -17);
		}

		this.dataTracker.setProperty(10, b);
	}

	public boolean shouldShowName() {
		return (this.dataTracker.getByte(10) & 16) != 0;
	}

	public void setHeadAngle(EulerAngle head) {
		this.headAngle = head;
		this.dataTracker.setProperty(11, head);
	}

	public void setBodyAngle(EulerAngle bodyAngle) {
		this.bodyAngle = bodyAngle;
		this.dataTracker.setProperty(12, bodyAngle);
	}

	public void setLeftArmAngle(EulerAngle leftArmAngle) {
		this.leftArmAngle = leftArmAngle;
		this.dataTracker.setProperty(13, leftArmAngle);
	}

	public void setRightArmAngle(EulerAngle rightArmAngle) {
		this.rightArmAngle = rightArmAngle;
		this.dataTracker.setProperty(14, rightArmAngle);
	}

	public void setLeftLegAngle(EulerAngle leftLegAngle) {
		this.leftLegAngle = leftLegAngle;
		this.dataTracker.setProperty(15, leftLegAngle);
	}

	public void setRightLegAngle(EulerAngle rightLegAngle) {
		this.rightLegAngle = rightLegAngle;
		this.dataTracker.setProperty(16, rightLegAngle);
	}

	public EulerAngle getHeadAngle() {
		return this.headAngle;
	}

	public EulerAngle getBodyAngle() {
		return this.bodyAngle;
	}

	public EulerAngle getLeftArmAngle() {
		return this.leftArmAngle;
	}

	public EulerAngle getRightArmAngle() {
		return this.rightArmAngle;
	}

	public EulerAngle getLeftLegAngle() {
		return this.leftLegAngle;
	}

	public EulerAngle getRightLegAngle() {
		return this.rightLegAngle;
	}

	@Override
	public boolean collides() {
		return super.collides() && !this.shouldShowName();
	}
}
