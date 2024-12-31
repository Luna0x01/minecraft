package net.minecraft.entity.decoration;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public class ArmorStandEntity extends LivingEntity {
	private static final EulerAngle DEFAULT_HEAD_ANGLE = new EulerAngle(0.0F, 0.0F, 0.0F);
	private static final EulerAngle DEFAULT_BODY_ANGLE = new EulerAngle(0.0F, 0.0F, 0.0F);
	private static final EulerAngle DEFAULT_LEFT_ARM_ANGLE = new EulerAngle(-10.0F, 0.0F, -10.0F);
	private static final EulerAngle DEFAULT_RIGHT_ARM_ANGLE = new EulerAngle(-15.0F, 0.0F, 10.0F);
	private static final EulerAngle DEFAULT_LEFT_LEG_ANGLE = new EulerAngle(-1.0F, 0.0F, -1.0F);
	private static final EulerAngle DEFAULT_RIGHT_LEG_ANGLE = new EulerAngle(1.0F, 0.0F, 1.0F);
	public static final TrackedData<Byte> field_14724 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.BYTE);
	public static final TrackedData<EulerAngle> field_14725 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> field_14729 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> field_14730 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> field_14731 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> field_14732 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	public static final TrackedData<EulerAngle> field_14733 = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
	private static final Predicate<Entity> field_14726 = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity instanceof AbstractMinecartEntity && ((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE;
		}
	};
	private final DefaultedList<ItemStack> field_15522 = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> field_15523 = DefaultedList.ofSize(4, ItemStack.EMPTY);
	private boolean invisible;
	public long lastHitTime;
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
		this.noClip = this.hasNoGravity();
		this.setBounds(0.5F, 1.975F);
	}

	public ArmorStandEntity(World world, double d, double e, double f) {
		this(world);
		this.updatePosition(d, e, f);
	}

	@Override
	protected final void setBounds(float width, float height) {
		double d = this.x;
		double e = this.y;
		double f = this.z;
		float g = this.shouldShowName() ? 0.0F : (this.isBaby() ? 0.5F : 1.0F);
		super.setBounds(width * g, height * g);
		this.updatePosition(d, e, f);
	}

	@Override
	public boolean canMoveVoluntarily() {
		return super.canMoveVoluntarily() && !this.hasNoGravity();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14724, (byte)0);
		this.dataTracker.startTracking(field_14725, DEFAULT_HEAD_ANGLE);
		this.dataTracker.startTracking(field_14729, DEFAULT_BODY_ANGLE);
		this.dataTracker.startTracking(field_14730, DEFAULT_LEFT_ARM_ANGLE);
		this.dataTracker.startTracking(field_14731, DEFAULT_RIGHT_ARM_ANGLE);
		this.dataTracker.startTracking(field_14732, DEFAULT_LEFT_LEG_ANGLE);
		this.dataTracker.startTracking(field_14733, DEFAULT_RIGHT_LEG_ANGLE);
	}

	@Override
	public Iterable<ItemStack> getItemsHand() {
		return this.field_15522;
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return this.field_15523;
	}

	@Override
	public ItemStack getStack(EquipmentSlot slot) {
		switch (slot.getType()) {
			case HAND:
				return this.field_15522.get(slot.method_13032());
			case ARMOR:
				return this.field_15523.get(slot.method_13032());
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		switch (slot.getType()) {
			case HAND:
				this.method_13045(stack);
				this.field_15522.set(slot.method_13032(), stack);
				break;
			case ARMOR:
				this.method_13045(stack);
				this.field_15523.set(slot.method_13032(), stack);
		}
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		EquipmentSlot equipmentSlot;
		if (slot == 98) {
			equipmentSlot = EquipmentSlot.MAINHAND;
		} else if (slot == 99) {
			equipmentSlot = EquipmentSlot.OFFHAND;
		} else if (slot == 100 + EquipmentSlot.HEAD.method_13032()) {
			equipmentSlot = EquipmentSlot.HEAD;
		} else if (slot == 100 + EquipmentSlot.CHEST.method_13032()) {
			equipmentSlot = EquipmentSlot.CHEST;
		} else if (slot == 100 + EquipmentSlot.LEGS.method_13032()) {
			equipmentSlot = EquipmentSlot.LEGS;
		} else {
			if (slot != 100 + EquipmentSlot.FEET.method_13032()) {
				return false;
			}

			equipmentSlot = EquipmentSlot.FEET;
		}

		if (!item.isEmpty() && !MobEntity.method_13080(equipmentSlot, item) && equipmentSlot != EquipmentSlot.HEAD) {
			return false;
		} else {
			this.equipStack(equipmentSlot, item);
			return true;
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemListSchema(ArmorStandEntity.class, "ArmorItems", "HandItems"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		NbtList nbtList = new NbtList();

		for (ItemStack itemStack : this.field_15523) {
			NbtCompound nbtCompound = new NbtCompound();
			if (!itemStack.isEmpty()) {
				itemStack.toNbt(nbtCompound);
			}

			nbtList.add(nbtCompound);
		}

		nbt.put("ArmorItems", nbtList);
		NbtList nbtList2 = new NbtList();

		for (ItemStack itemStack2 : this.field_15522) {
			NbtCompound nbtCompound2 = new NbtCompound();
			if (!itemStack2.isEmpty()) {
				itemStack2.toNbt(nbtCompound2);
			}

			nbtList2.add(nbtCompound2);
		}

		nbt.put("HandItems", nbtList2);
		nbt.putBoolean("Invisible", this.isInvisible());
		nbt.putBoolean("Small", this.isSmall());
		nbt.putBoolean("ShowArms", this.shouldShowArms());
		nbt.putInt("DisabledSlots", this.disabledSlots);
		nbt.putBoolean("NoBasePlate", this.hasNoBasePlate());
		if (this.shouldShowName()) {
			nbt.putBoolean("Marker", this.shouldShowName());
		}

		nbt.put("Pose", this.getPose());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("ArmorItems", 9)) {
			NbtList nbtList = nbt.getList("ArmorItems", 10);

			for (int i = 0; i < this.field_15523.size(); i++) {
				this.field_15523.set(i, new ItemStack(nbtList.getCompound(i)));
			}
		}

		if (nbt.contains("HandItems", 9)) {
			NbtList nbtList2 = nbt.getList("HandItems", 10);

			for (int j = 0; j < this.field_15522.size(); j++) {
				this.field_15522.set(j, new ItemStack(nbtList2.getCompound(j)));
			}
		}

		this.setInvisible(nbt.getBoolean("Invisible"));
		this.setSmall(nbt.getBoolean("Small"));
		this.setShowArms(nbt.getBoolean("ShowArms"));
		this.disabledSlots = nbt.getInt("DisabledSlots");
		this.setNoBasePlate(nbt.getBoolean("NoBasePlate"));
		this.setShouldShowName(nbt.getBoolean("Marker"));
		this.marker = !this.shouldShowName();
		this.noClip = this.hasNoGravity();
		NbtCompound nbtCompound = nbt.getCompound("Pose");
		this.setPose(nbtCompound);
	}

	private void setPose(NbtCompound nbt) {
		NbtList nbtList = nbt.getList("Head", 5);
		this.setHeadAngle(nbtList.isEmpty() ? DEFAULT_HEAD_ANGLE : new EulerAngle(nbtList));
		NbtList nbtList2 = nbt.getList("Body", 5);
		this.setBodyAngle(nbtList2.isEmpty() ? DEFAULT_BODY_ANGLE : new EulerAngle(nbtList2));
		NbtList nbtList3 = nbt.getList("LeftArm", 5);
		this.setLeftArmAngle(nbtList3.isEmpty() ? DEFAULT_LEFT_ARM_ANGLE : new EulerAngle(nbtList3));
		NbtList nbtList4 = nbt.getList("RightArm", 5);
		this.setRightArmAngle(nbtList4.isEmpty() ? DEFAULT_RIGHT_ARM_ANGLE : new EulerAngle(nbtList4));
		NbtList nbtList5 = nbt.getList("LeftLeg", 5);
		this.setLeftLegAngle(nbtList5.isEmpty() ? DEFAULT_LEFT_LEG_ANGLE : new EulerAngle(nbtList5));
		NbtList nbtList6 = nbt.getList("RightLeg", 5);
		this.setRightLegAngle(nbtList6.isEmpty() ? DEFAULT_RIGHT_LEG_ANGLE : new EulerAngle(nbtList6));
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
		List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox(), field_14726);

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity)list.get(i);
			if (this.squaredDistanceTo(entity) <= 0.2) {
				entity.pushAwayFrom(this);
			}
		}
	}

	@Override
	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.shouldShowName() || itemStack.getItem() == Items.NAME_TAG) {
			return ActionResult.PASS;
		} else if (!this.world.isClient && !player.isSpectator()) {
			EquipmentSlot equipmentSlot = MobEntity.method_13083(itemStack);
			if (itemStack.isEmpty()) {
				EquipmentSlot equipmentSlot2 = this.method_14045(hitPos);
				EquipmentSlot equipmentSlot3 = this.method_13207(equipmentSlot2) ? equipmentSlot : equipmentSlot2;
				if (this.method_13946(equipmentSlot3)) {
					this.method_13206(player, equipmentSlot3, itemStack, hand);
				}
			} else {
				if (this.method_13207(equipmentSlot)) {
					return ActionResult.FAIL;
				}

				if (equipmentSlot.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms()) {
					return ActionResult.FAIL;
				}

				this.method_13206(player, equipmentSlot, itemStack, hand);
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.SUCCESS;
		}
	}

	protected EquipmentSlot method_14045(Vec3d vec3d) {
		EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
		boolean bl = this.isSmall();
		double d = bl ? vec3d.y * 2.0 : vec3d.y;
		EquipmentSlot equipmentSlot2 = EquipmentSlot.FEET;
		if (d >= 0.1 && d < 0.1 + (bl ? 0.8 : 0.45) && this.method_13946(equipmentSlot2)) {
			equipmentSlot = EquipmentSlot.FEET;
		} else if (d >= 0.9 + (bl ? 0.3 : 0.0) && d < 0.9 + (bl ? 1.0 : 0.7) && this.method_13946(EquipmentSlot.CHEST)) {
			equipmentSlot = EquipmentSlot.CHEST;
		} else if (d >= 0.4 && d < 0.4 + (bl ? 1.0 : 0.8) && this.method_13946(EquipmentSlot.LEGS)) {
			equipmentSlot = EquipmentSlot.LEGS;
		} else if (d >= 1.6 && this.method_13946(EquipmentSlot.HEAD)) {
			equipmentSlot = EquipmentSlot.HEAD;
		}

		return equipmentSlot;
	}

	private boolean method_13207(EquipmentSlot equipmentSlot) {
		return (this.disabledSlots & 1 << equipmentSlot.method_13033()) != 0;
	}

	private void method_13206(PlayerEntity playerEntity, EquipmentSlot equipmentSlot, ItemStack itemStack, Hand hand) {
		ItemStack itemStack2 = this.getStack(equipmentSlot);
		if (itemStack2.isEmpty() || (this.disabledSlots & 1 << equipmentSlot.method_13033() + 8) == 0) {
			if (!itemStack2.isEmpty() || (this.disabledSlots & 1 << equipmentSlot.method_13033() + 16) == 0) {
				if (playerEntity.abilities.creativeMode && itemStack2.isEmpty() && !itemStack.isEmpty()) {
					ItemStack itemStack3 = itemStack.copy();
					itemStack3.setCount(1);
					this.equipStack(equipmentSlot, itemStack3);
				} else if (itemStack.isEmpty() || itemStack.getCount() <= 1) {
					this.equipStack(equipmentSlot, itemStack);
					playerEntity.equipStack(hand, itemStack2);
				} else if (itemStack2.isEmpty()) {
					ItemStack itemStack4 = itemStack.copy();
					itemStack4.setCount(1);
					this.equipStack(equipmentSlot, itemStack4);
					itemStack.decrement(1);
				}
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.world.isClient || this.removed) {
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
			if (this.isOnFire()) {
				this.updateHealth(0.15F);
			} else {
				this.setOnFireFor(5);
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
					this.method_14044();
					this.spawnBreakParticles();
					this.remove();
					return false;
				} else {
					long l = this.world.getLastUpdateTime();
					if (l - this.lastHitTime > 5L && !bl) {
						this.world.sendEntityStatus(this, (byte)32);
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
	public void handleStatus(byte status) {
		if (status == 32) {
			if (this.world.isClient) {
				this.world.playSound(this.x, this.y, this.z, Sounds.ENTITY_ARMORSTAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
				this.lastHitTime = this.world.getLastUpdateTime();
			}
		} else {
			super.handleStatus(status);
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
		this.method_14044();

		for (int i = 0; i < this.field_15522.size(); i++) {
			ItemStack itemStack = this.field_15522.get(i);
			if (!itemStack.isEmpty()) {
				Block.onBlockBreak(this.world, new BlockPos(this).up(), itemStack);
				this.field_15522.set(i, ItemStack.EMPTY);
			}
		}

		for (int j = 0; j < this.field_15523.size(); j++) {
			ItemStack itemStack2 = this.field_15523.get(j);
			if (!itemStack2.isEmpty()) {
				Block.onBlockBreak(this.world, new BlockPos(this).up(), itemStack2);
				this.field_15523.set(j, ItemStack.EMPTY);
			}
		}
	}

	private void method_14044() {
		this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_ARMORSTAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
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
	public double getHeightOffset() {
		return this.shouldShowName() ? 0.0 : 0.1F;
	}

	@Override
	public void method_2657(float f, float g, float h) {
		if (!this.hasNoGravity()) {
			super.method_2657(f, g, h);
		}
	}

	@Override
	public void setYaw(float yaw) {
		this.prevBodyYaw = this.prevYaw = yaw;
		this.prevHeadYaw = this.headYaw = yaw;
	}

	@Override
	public void setHeadYaw(float headYaw) {
		this.prevBodyYaw = this.prevYaw = headYaw;
		this.prevHeadYaw = this.headYaw = headYaw;
	}

	@Override
	public void tick() {
		super.tick();
		EulerAngle eulerAngle = this.dataTracker.get(field_14725);
		if (!this.headAngle.equals(eulerAngle)) {
			this.setHeadAngle(eulerAngle);
		}

		EulerAngle eulerAngle2 = this.dataTracker.get(field_14729);
		if (!this.bodyAngle.equals(eulerAngle2)) {
			this.setBodyAngle(eulerAngle2);
		}

		EulerAngle eulerAngle3 = this.dataTracker.get(field_14730);
		if (!this.leftArmAngle.equals(eulerAngle3)) {
			this.setLeftArmAngle(eulerAngle3);
		}

		EulerAngle eulerAngle4 = this.dataTracker.get(field_14731);
		if (!this.rightArmAngle.equals(eulerAngle4)) {
			this.setRightArmAngle(eulerAngle4);
		}

		EulerAngle eulerAngle5 = this.dataTracker.get(field_14732);
		if (!this.leftLegAngle.equals(eulerAngle5)) {
			this.setLeftLegAngle(eulerAngle5);
		}

		EulerAngle eulerAngle6 = this.dataTracker.get(field_14733);
		if (!this.rightLegAngle.equals(eulerAngle6)) {
			this.setRightLegAngle(eulerAngle6);
		}

		boolean bl = this.shouldShowName();
		if (this.marker != bl) {
			this.method_11122(bl);
			this.inanimate = !bl;
			this.marker = bl;
		}
	}

	private void method_11122(boolean bl) {
		if (bl) {
			this.setBounds(0.0F, 0.0F);
		} else {
			this.setBounds(0.5F, 1.975F);
		}
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

	@Override
	public PistonBehavior getPistonBehavior() {
		return this.shouldShowName() ? PistonBehavior.IGNORE : super.getPistonBehavior();
	}

	private void setSmall(boolean value) {
		this.dataTracker.set(field_14724, this.method_13205(this.dataTracker.get(field_14724), 1, value));
		this.setBounds(0.5F, 1.975F);
	}

	public boolean isSmall() {
		return (this.dataTracker.get(field_14724) & 1) != 0;
	}

	private void setShowArms(boolean value) {
		this.dataTracker.set(field_14724, this.method_13205(this.dataTracker.get(field_14724), 4, value));
	}

	public boolean shouldShowArms() {
		return (this.dataTracker.get(field_14724) & 4) != 0;
	}

	private void setNoBasePlate(boolean value) {
		this.dataTracker.set(field_14724, this.method_13205(this.dataTracker.get(field_14724), 8, value));
	}

	public boolean hasNoBasePlate() {
		return (this.dataTracker.get(field_14724) & 8) != 0;
	}

	private void setShouldShowName(boolean value) {
		this.dataTracker.set(field_14724, this.method_13205(this.dataTracker.get(field_14724), 16, value));
		this.setBounds(0.5F, 1.975F);
	}

	public boolean shouldShowName() {
		return (this.dataTracker.get(field_14724) & 16) != 0;
	}

	private byte method_13205(byte b, int i, boolean bl) {
		if (bl) {
			b = (byte)(b | i);
		} else {
			b = (byte)(b & ~i);
		}

		return b;
	}

	public void setHeadAngle(EulerAngle head) {
		this.headAngle = head;
		this.dataTracker.set(field_14725, head);
	}

	public void setBodyAngle(EulerAngle bodyAngle) {
		this.bodyAngle = bodyAngle;
		this.dataTracker.set(field_14729, bodyAngle);
	}

	public void setLeftArmAngle(EulerAngle leftArmAngle) {
		this.leftArmAngle = leftArmAngle;
		this.dataTracker.set(field_14730, leftArmAngle);
	}

	public void setRightArmAngle(EulerAngle rightArmAngle) {
		this.rightArmAngle = rightArmAngle;
		this.dataTracker.set(field_14731, rightArmAngle);
	}

	public void setLeftLegAngle(EulerAngle leftLegAngle) {
		this.leftLegAngle = leftLegAngle;
		this.dataTracker.set(field_14732, leftLegAngle);
	}

	public void setRightLegAngle(EulerAngle rightLegAngle) {
		this.rightLegAngle = rightLegAngle;
		this.dataTracker.set(field_14733, rightLegAngle);
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

	@Override
	public HandOption getDurability() {
		return HandOption.RIGHT;
	}

	@Override
	protected Sound getLandSound(int height) {
		return Sounds.ENTITY_ARMORSTAND_FALL;
	}

	@Nullable
	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_ARMORSTAND_HIT;
	}

	@Nullable
	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_ARMORSTAND_BREAK;
	}

	@Override
	public void onLightningStrike(LightningBoltEntity lightning) {
	}

	@Override
	public boolean method_13057() {
		return false;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_14724.equals(data)) {
			this.setBounds(0.5F, 1.975F);
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public boolean method_13948() {
		return false;
	}
}
