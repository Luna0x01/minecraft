package net.minecraft.entity;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_2971;
import net.minecraft.class_3133;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemSchema;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.AnimalInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public abstract class AbstractHorseEntity extends AnimalEntity implements SimpleInventoryListener, class_2971 {
	private static final Predicate<Entity> field_15495 = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity instanceof AbstractHorseEntity && ((AbstractHorseEntity)entity).method_13996();
		}
	};
	protected static final EntityAttribute field_15508 = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0)
		.setName("Jump Strength")
		.setTracked(true);
	private static final TrackedData<Byte> field_15496 = DataTracker.registerData(AbstractHorseEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Optional<UUID>> field_15497 = DataTracker.registerData(AbstractHorseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private int field_15498;
	private int field_15499;
	private int field_15500;
	public int field_15509;
	public int field_15510;
	protected boolean field_15489;
	protected AnimalInventory animalInventory;
	protected int field_15491;
	protected float field_15492;
	private boolean field_15501;
	private float field_15502;
	private float field_15503;
	private float field_15504;
	private float field_15505;
	private float field_15506;
	private float field_15507;
	protected boolean field_15493 = true;
	protected int field_15494;

	public AbstractHorseEntity(World world) {
		super(world);
		this.setBounds(1.3964844F, 1.6F);
		this.stepHeight = 1.0F;
		this.method_13998();
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 1.2));
		this.goals.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goals.add(2, new BreedGoal(this, 1.0, AbstractHorseEntity.class));
		this.goals.add(4, new FollowParentGoal(this, 1.0));
		this.goals.add(6, new class_3133(this, 0.7));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15496, (byte)0);
		this.dataTracker.startTracking(field_15497, Optional.absent());
	}

	protected boolean method_14002(int i) {
		return (this.dataTracker.get(field_15496) & i) != 0;
	}

	protected void method_13972(int i, boolean bl) {
		byte b = this.dataTracker.get(field_15496);
		if (bl) {
			this.dataTracker.set(field_15496, (byte)(b | i));
		} else {
			this.dataTracker.set(field_15496, (byte)(b & ~i));
		}
	}

	public boolean method_13990() {
		return this.method_14002(2);
	}

	@Nullable
	public UUID method_13991() {
		return (UUID)this.dataTracker.get(field_15497).orNull();
	}

	public void method_13971(@Nullable UUID uUID) {
		this.dataTracker.set(field_15497, Optional.fromNullable(uUID));
	}

	public float method_13992() {
		return 0.5F;
	}

	@Override
	public void method_5377(boolean bl) {
		this.method_5378(bl ? this.method_13992() : 1.0F);
	}

	public boolean method_13993() {
		return this.field_15489;
	}

	public void method_14007(boolean bl) {
		this.method_13972(2, bl);
	}

	public void method_14009(boolean bl) {
		this.field_15489 = bl;
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return super.method_2537(playerEntity) && this.getGroup() != EntityGroup.UNDEAD;
	}

	@Override
	protected void method_6175(float f) {
		if (f > 6.0F && this.method_13994()) {
			this.method_14014(false);
		}
	}

	public boolean method_13994() {
		return this.method_14002(16);
	}

	public boolean method_13995() {
		return this.method_14002(32);
	}

	public boolean method_13996() {
		return this.method_14002(8);
	}

	public void method_14011(boolean bl) {
		this.method_13972(8, bl);
	}

	public void method_14013(boolean bl) {
		this.method_13972(4, bl);
	}

	public int method_13997() {
		return this.field_15491;
	}

	public void method_14005(int i) {
		this.field_15491 = i;
	}

	public int method_14006(int i) {
		int j = MathHelper.clamp(this.method_13997() + i, 0, this.method_13976());
		this.method_14005(j);
		return j;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		Entity entity = source.getAttacker();
		return this.hasPassengers() && entity != null && this.hasPassengerDeep(entity) ? false : super.damage(source, amount);
	}

	@Override
	public boolean isPushable() {
		return !this.hasPassengers();
	}

	private void method_13986() {
		this.method_13989();
		if (!this.isSilent()) {
			this.world
				.playSound(
					null, this.x, this.y, this.z, Sounds.ENTITY_HORSE_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
				);
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		if (fallDistance > 1.0F) {
			this.playSound(Sounds.ENTITY_HORSE_LAND, 0.4F, 1.0F);
		}

		int i = MathHelper.ceil((fallDistance * 0.5F - 3.0F) * damageMultiplier);
		if (i > 0) {
			this.damage(DamageSource.FALL, (float)i);
			if (this.hasPassengers()) {
				for (Entity entity : this.getPassengersDeep()) {
					entity.damage(DamageSource.FALL, (float)i);
				}
			}

			BlockState blockState = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z));
			Block block = blockState.getBlock();
			if (blockState.getMaterial() != Material.AIR && !this.isSilent()) {
				BlockSoundGroup blockSoundGroup = block.getSoundGroup();
				this.world
					.playSound(
						null,
						this.x,
						this.y,
						this.z,
						blockSoundGroup.getStepSound(),
						this.getSoundCategory(),
						blockSoundGroup.getVolume() * 0.5F,
						blockSoundGroup.getPitch() * 0.75F
					);
			}
		}
	}

	protected int method_13987() {
		return 2;
	}

	protected void method_13998() {
		AnimalInventory animalInventory = this.animalInventory;
		this.animalInventory = new AnimalInventory("HorseChest", this.method_13987());
		this.animalInventory.setName(this.getTranslationKey());
		if (animalInventory != null) {
			animalInventory.removeListener(this);
			int i = Math.min(animalInventory.getInvSize(), this.animalInventory.getInvSize());

			for (int j = 0; j < i; j++) {
				ItemStack itemStack = animalInventory.getInvStack(j);
				if (!itemStack.isEmpty()) {
					this.animalInventory.setInvStack(j, itemStack.copy());
				}
			}
		}

		this.animalInventory.addListener(this);
		this.method_6244();
	}

	protected void method_6244() {
		if (!this.world.isClient) {
			this.method_14013(!this.animalInventory.getInvStack(0).isEmpty() && this.method_13974());
		}
	}

	@Override
	public void method_13928(Inventory inventory) {
		boolean bl = this.method_13975();
		this.method_6244();
		if (this.ticksAlive > 20 && !bl && this.method_13975()) {
			this.playSound(Sounds.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
		}
	}

	@Nullable
	protected AbstractHorseEntity method_13969(Entity entity, double d) {
		double e = Double.MAX_VALUE;
		Entity entity2 = null;

		for (Entity entity3 : this.world.getEntitiesIn(entity, entity.getBoundingBox().stretch(d, d, d), field_15495)) {
			double f = entity3.squaredDistanceTo(entity.x, entity.y, entity.z);
			if (f < e) {
				entity2 = entity3;
				e = f;
			}
		}

		return (AbstractHorseEntity)entity2;
	}

	public double method_13999() {
		return this.initializeAttribute(field_15508).getValue();
	}

	@Nullable
	@Override
	protected Sound deathSound() {
		this.method_13989();
		return null;
	}

	@Nullable
	@Override
	protected Sound method_13048() {
		this.method_13989();
		if (this.random.nextInt(3) == 0) {
			this.method_13985();
		}

		return null;
	}

	@Nullable
	@Override
	protected Sound ambientSound() {
		this.method_13989();
		if (this.random.nextInt(10) == 0 && !this.method_2610()) {
			this.method_13985();
		}

		return null;
	}

	public boolean method_13974() {
		return true;
	}

	public boolean method_13975() {
		return this.method_14002(4);
	}

	@Nullable
	protected Sound method_13132() {
		this.method_13989();
		this.method_13985();
		return null;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		if (!block.getDefaultState().getMaterial().isFluid()) {
			BlockSoundGroup blockSoundGroup = block.getSoundGroup();
			if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER) {
				blockSoundGroup = Blocks.SNOW_LAYER.getSoundGroup();
			}

			if (this.hasPassengers() && this.field_15493) {
				this.field_15494++;
				if (this.field_15494 > 5 && this.field_15494 % 3 == 0) {
					this.method_13967(blockSoundGroup);
				} else if (this.field_15494 <= 5) {
					this.playSound(Sounds.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
				}
			} else if (blockSoundGroup == BlockSoundGroup.field_12759) {
				this.playSound(Sounds.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			} else {
				this.playSound(Sounds.ENTITY_HORSE_STEP, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			}
		}
	}

	protected void method_13967(BlockSoundGroup blockSoundGroup) {
		this.playSound(Sounds.ENTITY_HORSE_GALLOP, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(field_15508);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(53.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.225F);
	}

	@Override
	public int getLimitPerChunk() {
		return 6;
	}

	public int method_13976() {
		return 100;
	}

	@Override
	protected float getSoundVolume() {
		return 0.8F;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 400;
	}

	public void method_14000(PlayerEntity playerEntity) {
		if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(playerEntity)) && this.method_13990()) {
			this.animalInventory.setName(this.getTranslationKey());
			playerEntity.method_6317(this, this.animalInventory);
		}
	}

	protected boolean method_13970(PlayerEntity playerEntity, ItemStack itemStack) {
		boolean bl = false;
		float f = 0.0F;
		int i = 0;
		int j = 0;
		Item item = itemStack.getItem();
		if (item == Items.WHEAT) {
			f = 2.0F;
			i = 20;
			j = 3;
		} else if (item == Items.SUGAR) {
			f = 1.0F;
			i = 30;
			j = 3;
		} else if (item == Item.fromBlock(Blocks.HAY_BALE)) {
			f = 20.0F;
			i = 180;
		} else if (item == Items.APPLE) {
			f = 3.0F;
			i = 60;
			j = 3;
		} else if (item == Items.GOLDEN_CARROT) {
			f = 4.0F;
			i = 60;
			j = 5;
			if (this.method_13990() && this.age() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(playerEntity);
			}
		} else if (item == Items.GOLDEN_APPLE) {
			f = 10.0F;
			i = 240;
			j = 10;
			if (this.method_13990() && this.age() == 0 && !this.isInLove()) {
				bl = true;
				this.lovePlayer(playerEntity);
			}
		}

		if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
			this.heal(f);
			bl = true;
		}

		if (this.isBaby() && i > 0) {
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
			if (!this.world.isClient) {
				this.method_6095(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.method_13990()) && this.method_13997() < this.method_13976()) {
			bl = true;
			if (!this.world.isClient) {
				this.method_14006(j);
			}
		}

		if (bl) {
			this.method_13986();
		}

		return bl;
	}

	protected void method_14003(PlayerEntity playerEntity) {
		playerEntity.yaw = this.yaw;
		playerEntity.pitch = this.pitch;
		this.method_14014(false);
		this.method_14015(false);
		if (!this.world.isClient) {
			playerEntity.ride(this);
		}
	}

	@Override
	protected boolean method_2610() {
		return super.method_2610() && this.hasPassengers() && this.method_13975() || this.method_13994() || this.method_13995();
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	private void method_13988() {
		this.field_15509 = 1;
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (!this.world.isClient && this.animalInventory != null) {
			for (int i = 0; i < this.animalInventory.getInvSize(); i++) {
				ItemStack itemStack = this.animalInventory.getInvStack(i);
				if (!itemStack.isEmpty()) {
					this.dropItem(itemStack, 0.0F);
				}
			}
		}
	}

	@Override
	public void tickMovement() {
		if (this.random.nextInt(200) == 0) {
			this.method_13988();
		}

		super.tickMovement();
		if (!this.world.isClient) {
			if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}

			if (this.method_13978()) {
				if (!this.method_13994()
					&& !this.hasPassengers()
					&& this.random.nextInt(300) == 0
					&& this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.y) - 1, MathHelper.floor(this.z))).getBlock() == Blocks.GRASS) {
					this.method_14014(true);
				}

				if (this.method_13994() && ++this.field_15498 > 50) {
					this.field_15498 = 0;
					this.method_14014(false);
				}
			}

			this.method_13977();
		}
	}

	protected void method_13977() {
		if (this.method_13996() && this.isBaby() && !this.method_13994()) {
			AbstractHorseEntity abstractHorseEntity = this.method_13969(this, 16.0);
			if (abstractHorseEntity != null && this.squaredDistanceTo(abstractHorseEntity) > 4.0) {
				this.navigation.method_13109(abstractHorseEntity);
			}
		}
	}

	public boolean method_13978() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.field_15499 > 0 && ++this.field_15499 > 30) {
			this.field_15499 = 0;
			this.method_13972(64, false);
		}

		if (this.method_13003() && this.field_15500 > 0 && ++this.field_15500 > 20) {
			this.field_15500 = 0;
			this.method_14015(false);
		}

		if (this.field_15509 > 0 && ++this.field_15509 > 8) {
			this.field_15509 = 0;
		}

		if (this.field_15510 > 0) {
			this.field_15510++;
			if (this.field_15510 > 300) {
				this.field_15510 = 0;
			}
		}

		this.field_15503 = this.field_15502;
		if (this.method_13994()) {
			this.field_15502 = this.field_15502 + (1.0F - this.field_15502) * 0.4F + 0.05F;
			if (this.field_15502 > 1.0F) {
				this.field_15502 = 1.0F;
			}
		} else {
			this.field_15502 = this.field_15502 + ((0.0F - this.field_15502) * 0.4F - 0.05F);
			if (this.field_15502 < 0.0F) {
				this.field_15502 = 0.0F;
			}
		}

		this.field_15505 = this.field_15504;
		if (this.method_13995()) {
			this.field_15502 = 0.0F;
			this.field_15503 = this.field_15502;
			this.field_15504 = this.field_15504 + (1.0F - this.field_15504) * 0.4F + 0.05F;
			if (this.field_15504 > 1.0F) {
				this.field_15504 = 1.0F;
			}
		} else {
			this.field_15501 = false;
			this.field_15504 = this.field_15504 + ((0.8F * this.field_15504 * this.field_15504 * this.field_15504 - this.field_15504) * 0.6F - 0.05F);
			if (this.field_15504 < 0.0F) {
				this.field_15504 = 0.0F;
			}
		}

		this.field_15507 = this.field_15506;
		if (this.method_14002(64)) {
			this.field_15506 = this.field_15506 + (1.0F - this.field_15506) * 0.7F + 0.05F;
			if (this.field_15506 > 1.0F) {
				this.field_15506 = 1.0F;
			}
		} else {
			this.field_15506 = this.field_15506 + ((0.0F - this.field_15506) * 0.7F - 0.05F);
			if (this.field_15506 < 0.0F) {
				this.field_15506 = 0.0F;
			}
		}
	}

	private void method_13989() {
		if (!this.world.isClient) {
			this.field_15499 = 1;
			this.method_13972(64, true);
		}
	}

	public void method_14014(boolean bl) {
		this.method_13972(16, bl);
	}

	public void method_14015(boolean bl) {
		if (bl) {
			this.method_14014(false);
		}

		this.method_13972(32, bl);
	}

	private void method_13985() {
		if (this.method_13003()) {
			this.field_15500 = 1;
			this.method_14015(true);
		}
	}

	public void method_13979() {
		this.method_13985();
		Sound sound = this.method_13132();
		if (sound != null) {
			this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public boolean method_14004(PlayerEntity playerEntity) {
		this.method_13971(playerEntity.getUuid());
		this.method_14007(true);
		this.world.sendEntityStatus(this, (byte)7);
		return true;
	}

	@Override
	public void travel(float f, float g) {
		if (this.hasPassengers() && this.canBeControlledByRider() && this.method_13975()) {
			LivingEntity livingEntity = (LivingEntity)this.getPrimaryPassenger();
			this.yaw = livingEntity.yaw;
			this.prevYaw = this.yaw;
			this.pitch = livingEntity.pitch * 0.5F;
			this.setRotation(this.yaw, this.pitch);
			this.bodyYaw = this.yaw;
			this.headYaw = this.bodyYaw;
			f = livingEntity.sidewaysSpeed * 0.5F;
			g = livingEntity.forwardSpeed;
			if (g <= 0.0F) {
				g *= 0.25F;
				this.field_15494 = 0;
			}

			if (this.onGround && this.field_15492 == 0.0F && this.method_13995() && !this.field_15501) {
				f = 0.0F;
				g = 0.0F;
			}

			if (this.field_15492 > 0.0F && !this.method_13993() && this.onGround) {
				this.velocityY = this.method_13999() * (double)this.field_15492;
				if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
					this.velocityY = this.velocityY + (double)((float)(this.getEffectInstance(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
				}

				this.method_14009(true);
				this.velocityDirty = true;
				if (g > 0.0F) {
					float h = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
					float i = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
					this.velocityX = this.velocityX + (double)(-0.4F * h * this.field_15492);
					this.velocityZ = this.velocityZ + (double)(0.4F * i * this.field_15492);
					this.playSound(Sounds.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
				}

				this.field_15492 = 0.0F;
			}

			this.flyingSpeed = this.getMovementSpeed() * 0.1F;
			if (this.method_13003()) {
				this.setMovementSpeed((float)this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
				super.travel(f, g);
			} else if (livingEntity instanceof PlayerEntity) {
				this.velocityX = 0.0;
				this.velocityY = 0.0;
				this.velocityZ = 0.0;
			}

			if (this.onGround) {
				this.field_15492 = 0.0F;
				this.method_14009(false);
			}

			this.field_6748 = this.field_6749;
			double d = this.x - this.prevX;
			double e = this.z - this.prevZ;
			float j = MathHelper.sqrt(d * d + e * e) * 4.0F;
			if (j > 1.0F) {
				j = 1.0F;
			}

			this.field_6749 = this.field_6749 + (j - this.field_6749) * 0.4F;
			this.field_6750 = this.field_6750 + this.field_6749;
		} else {
			this.flyingSpeed = 0.02F;
			super.travel(f, g);
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer, Class<?> class_) {
		MobEntity.registerDataFixes(dataFixer, class_);
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemSchema(class_, "SaddleItem"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("EatingHaystack", this.method_13994());
		nbt.putBoolean("Bred", this.method_13996());
		nbt.putInt("Temper", this.method_13997());
		nbt.putBoolean("Tame", this.method_13990());
		if (this.method_13991() != null) {
			nbt.putString("OwnerUUID", this.method_13991().toString());
		}

		if (!this.animalInventory.getInvStack(0).isEmpty()) {
			nbt.put("SaddleItem", this.animalInventory.getInvStack(0).toNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_14014(nbt.getBoolean("EatingHaystack"));
		this.method_14011(nbt.getBoolean("Bred"));
		this.method_14005(nbt.getInt("Temper"));
		this.method_14007(nbt.getBoolean("Tame"));
		String string;
		if (nbt.contains("OwnerUUID", 8)) {
			string = nbt.getString("OwnerUUID");
		} else {
			String string2 = nbt.getString("Owner");
			string = ServerConfigHandler.method_8204(this.getMinecraftServer(), string2);
		}

		if (!string.isEmpty()) {
			this.method_13971(UUID.fromString(string));
		}

		EntityAttributeInstance entityAttributeInstance = this.getAttributeContainer().get("Speed");
		if (entityAttributeInstance != null) {
			this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(entityAttributeInstance.getBaseValue() * 0.25);
		}

		if (nbt.contains("SaddleItem", 10)) {
			ItemStack itemStack = new ItemStack(nbt.getCompound("SaddleItem"));
			if (itemStack.getItem() == Items.SADDLE) {
				this.animalInventory.setInvStack(0, itemStack);
			}
		}

		this.method_6244();
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return false;
	}

	protected boolean method_13980() {
		return !this.hasPassengers() && !this.hasMount() && this.method_13990() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
	}

	@Nullable
	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		return null;
	}

	protected void method_13968(PassiveEntity passiveEntity, AbstractHorseEntity abstractHorseEntity) {
		double d = this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue()
			+ passiveEntity.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue()
			+ (double)this.method_13981();
		abstractHorseEntity.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(d / 3.0);
		double e = this.initializeAttribute(field_15508).getBaseValue() + passiveEntity.initializeAttribute(field_15508).getBaseValue() + this.method_13982();
		abstractHorseEntity.initializeAttribute(field_15508).setBaseValue(e / 3.0);
		double f = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue()
			+ passiveEntity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue()
			+ this.method_13983();
		abstractHorseEntity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(f / 3.0);
	}

	@Override
	public boolean canBeControlledByRider() {
		return this.getPrimaryPassenger() instanceof LivingEntity;
	}

	public float method_14008(float f) {
		return this.field_15503 + (this.field_15502 - this.field_15503) * f;
	}

	public float method_14010(float f) {
		return this.field_15505 + (this.field_15504 - this.field_15505) * f;
	}

	public float method_14012(float f) {
		return this.field_15507 + (this.field_15506 - this.field_15507) * f;
	}

	@Override
	public void method_6299(int i) {
		if (this.method_13975()) {
			if (i < 0) {
				i = 0;
			} else {
				this.field_15501 = true;
				this.method_13985();
			}

			if (i >= 90) {
				this.field_15492 = 1.0F;
			} else {
				this.field_15492 = 0.4F + 0.4F * (float)i / 90.0F;
			}
		}
	}

	@Override
	public boolean method_13089() {
		return this.method_13975();
	}

	@Override
	public void method_13090(int i) {
		this.field_15501 = true;
		this.method_13985();
	}

	@Override
	public void method_13091() {
	}

	protected void method_14016(boolean bl) {
		ParticleType particleType = bl ? ParticleType.HEART : ParticleType.SMOKE;

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleType,
					this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
					this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					d,
					e,
					f
				);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 7) {
			this.method_14016(true);
		} else if (status == 6) {
			this.method_14016(false);
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		super.updatePassengerPosition(passenger);
		if (passenger instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)passenger;
			this.bodyYaw = mobEntity.bodyYaw;
		}

		if (this.field_15505 > 0.0F) {
			float f = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
			float g = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
			float h = 0.7F * this.field_15505;
			float i = 0.15F * this.field_15505;
			passenger.updatePosition(
				this.x + (double)(h * f), this.y + this.getMountedHeightOffset() + passenger.getHeightOffset() + (double)i, this.z - (double)(h * g)
			);
			if (passenger instanceof LivingEntity) {
				((LivingEntity)passenger).bodyYaw = this.bodyYaw;
			}
		}
	}

	protected float method_13981() {
		return 15.0F + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
	}

	protected double method_13982() {
		return 0.4F + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
	}

	protected double method_13983() {
		return (0.45F + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
	}

	@Override
	public boolean isClimbing() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return this.height;
	}

	public boolean method_13984() {
		return false;
	}

	public boolean method_14001(ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		int i = slot - 400;
		if (i >= 0 && i < 2 && i < this.animalInventory.getInvSize()) {
			if (i == 0 && item.getItem() != Items.SADDLE) {
				return false;
			} else if (i != 1 || this.method_13984() && this.method_14001(item)) {
				this.animalInventory.setInvStack(i, item);
				this.method_6244();
				return true;
			} else {
				return false;
			}
		} else {
			int j = slot - 500 + 2;
			if (j >= 2 && j < this.animalInventory.getInvSize()) {
				this.animalInventory.setInvStack(j, item);
				return true;
			} else {
				return false;
			}
		}
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		if (this.random.nextInt(5) == 0) {
			this.setAge(-24000);
		}

		return data;
	}
}
