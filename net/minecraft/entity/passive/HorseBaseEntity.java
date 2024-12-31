package net.minecraft.entity.passive;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_2971;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.datafixer.schema.ItemSchema;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.HorseType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.class_2978;
import net.minecraft.entity.attribute.AttributeModifier;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.AnimalInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.SimpleInventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.HorseArmorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public class HorseBaseEntity extends AnimalEntity implements SimpleInventoryListener, class_2971 {
	private static final Predicate<Entity> EATING_GRASS_ENTITY_PREDICATE = new Predicate<Entity>() {
		public boolean apply(@Nullable Entity entity) {
			return entity instanceof HorseBaseEntity && ((HorseBaseEntity)entity).isReadyToBreed();
		}
	};
	private static final EntityAttribute JUMP_STRENGTH_ATTRIBUTE = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0)
		.setName("Jump Strength")
		.setTracked(true);
	private static final UUID field_14628 = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
	private static final TrackedData<Byte> field_14629 = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> field_14630 = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_14631 = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<UUID>> field_14632 = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final TrackedData<Integer> field_14633 = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final String[] HORSE_TEXTURES = new String[]{
		"textures/entity/horse/horse_white.png",
		"textures/entity/horse/horse_creamy.png",
		"textures/entity/horse/horse_chestnut.png",
		"textures/entity/horse/horse_brown.png",
		"textures/entity/horse/horse_black.png",
		"textures/entity/horse/horse_gray.png",
		"textures/entity/horse/horse_darkbrown.png"
	};
	private static final String[] field_6879 = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
	private static final String[] HORSE_MARKINGS_TEXTURES = new String[]{
		null,
		"textures/entity/horse/horse_markings_white.png",
		"textures/entity/horse/horse_markings_whitefield.png",
		"textures/entity/horse/horse_markings_whitedots.png",
		"textures/entity/horse/horse_markings_blackdots.png"
	};
	private static final String[] field_6881 = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
	private final class_2978 field_14634 = new class_2978(this);
	private int eatingGrassTicks;
	private int eatingTicks;
	private int angryTicks;
	public int field_6897;
	public int field_6898;
	protected boolean inAir;
	private AnimalInventory inventory;
	private boolean field_6886;
	protected int temper;
	protected float jumpStrength;
	private boolean jumping;
	private boolean field_14635;
	private int field_14636;
	private float eatingGrassAnimationProgress;
	private float lastEatingGrassAnimationProgress;
	private float angryAnimationProgress;
	private float lastAngryAnimationProgress;
	private float eatingAnimationProgress;
	private float lastEatingAnimationProgress;
	private int soundTicks;
	private String field_6895;
	private final String[] field_6896 = new String[3];
	private boolean field_11974;

	public HorseBaseEntity(World world) {
		super(world);
		this.setBounds(1.3964844F, 1.6F);
		this.isFireImmune = false;
		this.setHasChest(false);
		this.stepHeight = 1.0F;
		this.method_6243();
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 1.2));
		this.goals.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goals.add(2, new BreedGoal(this, 1.0));
		this.goals.add(4, new FollowParentGoal(this, 1.0));
		this.goals.add(6, new WanderAroundGoal(this, 0.7));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14629, (byte)0);
		this.dataTracker.startTracking(field_14630, HorseType.HORSE.method_13153());
		this.dataTracker.startTracking(field_14631, 0);
		this.dataTracker.startTracking(field_14632, Optional.absent());
		this.dataTracker.startTracking(field_14633, HorseArmorType.NONE.method_13134());
	}

	public void method_13126(HorseType horseType) {
		this.dataTracker.set(field_14630, horseType.method_13153());
		this.method_6245();
	}

	public HorseType method_13129() {
		return HorseType.method_13143(this.dataTracker.get(field_14630));
	}

	public void setVariant(int variant) {
		this.dataTracker.set(field_14631, variant);
		this.method_6245();
	}

	public int getVariant() {
		return this.dataTracker.get(field_14631);
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.getCustomName() : this.method_13129().getName().asUnformattedString();
	}

	private boolean getHorseFlag(int bitMask) {
		return (this.dataTracker.get(field_14629) & bitMask) != 0;
	}

	private void setHorseFlag(int bitMask, boolean flag) {
		byte b = this.dataTracker.get(field_14629);
		if (flag) {
			this.dataTracker.set(field_14629, (byte)(b | bitMask));
		} else {
			this.dataTracker.set(field_14629, (byte)(b & ~bitMask));
		}
	}

	public boolean method_6237() {
		return !this.isBaby();
	}

	public boolean isTame() {
		return this.getHorseFlag(2);
	}

	public boolean method_6254() {
		return this.method_6237();
	}

	@Nullable
	public UUID method_13130() {
		return (UUID)this.dataTracker.get(field_14632).orNull();
	}

	public void method_13127(@Nullable UUID uUID) {
		this.dataTracker.set(field_14632, Optional.fromNullable(uUID));
	}

	public float method_6256() {
		return 0.5F;
	}

	@Override
	public void method_5377(boolean bl) {
		if (bl) {
			this.method_5378(this.method_6256());
		} else {
			this.method_5378(1.0F);
		}
	}

	public boolean isInAir() {
		return this.inAir;
	}

	public void setTame(boolean tame) {
		this.setHorseFlag(2, tame);
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return !this.method_13129().undead() && super.method_2537(playerEntity);
	}

	@Override
	protected void method_6175(float f) {
		if (f > 6.0F && this.isEating()) {
			this.setEatingGrass(false);
		}
	}

	public boolean hasChest() {
		return this.method_13129().method_13148() && this.getHorseFlag(8);
	}

	public HorseArmorType method_13131() {
		return HorseArmorType.method_13135(this.dataTracker.get(field_14633));
	}

	public boolean isEating() {
		return this.getHorseFlag(32);
	}

	public boolean isAngry() {
		return this.getHorseFlag(64);
	}

	public boolean isReadyToBreed() {
		return this.getHorseFlag(16);
	}

	public boolean hasBred() {
		return this.field_6886;
	}

	public void method_8390(ItemStack itemStack) {
		HorseArmorType horseArmorType = HorseArmorType.method_13137(itemStack);
		this.dataTracker.set(field_14633, horseArmorType.method_13134());
		this.method_6245();
		if (!this.world.isClient) {
			this.initializeAttribute(EntityAttributes.GENERIC_ARMOR).method_13093(field_14628);
			int i = horseArmorType.getBonus();
			if (i != 0) {
				this.initializeAttribute(EntityAttributes.GENERIC_ARMOR)
					.addModifier(new AttributeModifier(field_14628, "Horse armor bonus", (double)i, 0).setSerialized(false));
			}
		}
	}

	public void setBred(boolean bl) {
		this.setHorseFlag(16, bl);
	}

	public void setHasChest(boolean bl) {
		this.setHorseFlag(8, bl);
	}

	public void setHasBred(boolean bl) {
		this.field_6886 = bl;
	}

	public void setSaddled(boolean bl) {
		this.setHorseFlag(4, bl);
	}

	public int getTemper() {
		return this.temper;
	}

	public void setTemper(int temper) {
		this.temper = temper;
	}

	public int addTemper(int difference) {
		int i = MathHelper.clamp(this.getTemper() + difference, 0, this.getMaxTemper());
		this.setTemper(i);
		return i;
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

	public boolean method_6265() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.z);
		this.world.getBiome(new BlockPos(i, 0, j));
		return true;
	}

	public void method_6266() {
		if (!this.world.isClient && this.hasChest()) {
			this.dropItem(Item.fromBlock(Blocks.CHEST), 1);
			this.setHasChest(false);
		}
	}

	private void playEatingAnimation() {
		this.setEating();
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

	private int method_6242() {
		HorseType horseType = this.method_13129();
		return this.hasChest() && horseType.method_13148() ? 17 : 2;
	}

	private void method_6243() {
		AnimalInventory animalInventory = this.inventory;
		this.inventory = new AnimalInventory("HorseChest", this.method_6242());
		this.inventory.setName(this.getTranslationKey());
		if (animalInventory != null) {
			animalInventory.removeListener(this);
			int i = Math.min(animalInventory.getInvSize(), this.inventory.getInvSize());

			for (int j = 0; j < i; j++) {
				ItemStack itemStack = animalInventory.getInvStack(j);
				if (itemStack != null) {
					this.inventory.setInvStack(j, itemStack.copy());
				}
			}
		}

		this.inventory.addListener(this);
		this.updateSaddle();
	}

	private void updateSaddle() {
		if (!this.world.isClient) {
			this.setSaddled(this.inventory.getInvStack(0) != null);
			if (this.method_13129().method_13152()) {
				this.method_8390(this.inventory.getInvStack(1));
			}
		}
	}

	@Override
	public void onChanged(SimpleInventory inventory) {
		HorseArmorType horseArmorType = this.method_13131();
		boolean bl = this.isSaddled();
		this.updateSaddle();
		if (this.ticksAlive > 20) {
			if (horseArmorType == HorseArmorType.NONE && horseArmorType != this.method_13131()) {
				this.playSound(Sounds.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
			} else if (horseArmorType != this.method_13131()) {
				this.playSound(Sounds.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
			}

			if (!bl && this.isSaddled()) {
				this.playSound(Sounds.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
			}
		}
	}

	@Override
	public boolean canSpawn() {
		this.method_6265();
		return super.canSpawn();
	}

	protected HorseBaseEntity method_6231(Entity entity, double d) {
		double e = Double.MAX_VALUE;
		Entity entity2 = null;

		for (Entity entity3 : this.world.getEntitiesIn(entity, entity.getBoundingBox().stretch(d, d, d), EATING_GRASS_ENTITY_PREDICATE)) {
			double f = entity3.squaredDistanceTo(entity.x, entity.y, entity.z);
			if (f < e) {
				entity2 = entity3;
				e = f;
			}
		}

		return (HorseBaseEntity)entity2;
	}

	public double getJumpStrength() {
		return this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).getValue();
	}

	@Override
	protected Sound deathSound() {
		this.setEating();
		return this.method_13129().method_13145();
	}

	@Override
	protected Sound method_13048() {
		this.setEating();
		if (this.random.nextInt(3) == 0) {
			this.updateAnger();
		}

		return this.method_13129().method_13144();
	}

	public boolean isSaddled() {
		return this.getHorseFlag(4);
	}

	@Override
	protected Sound ambientSound() {
		this.setEating();
		if (this.random.nextInt(10) == 0 && !this.method_2610()) {
			this.updateAnger();
		}

		return this.method_13129().method_13142();
	}

	@Nullable
	protected Sound method_13132() {
		this.setEating();
		this.updateAnger();
		HorseType horseType = this.method_13129();
		if (horseType.undead()) {
			return null;
		} else {
			return horseType.method_13149() ? Sounds.ENTITY_DONKEY_ANGRY : Sounds.ENTITY_HORSE_ANGRY;
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		BlockSoundGroup blockSoundGroup = block.getSoundGroup();
		if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER) {
			blockSoundGroup = Blocks.SNOW_LAYER.getSoundGroup();
		}

		if (!block.getDefaultState().getMaterial().isFluid()) {
			HorseType horseType = this.method_13129();
			if (this.hasPassengers() && !horseType.method_13149()) {
				this.soundTicks++;
				if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
					this.playSound(Sounds.ENTITY_HORSE_GALLOP, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
					if (horseType == HorseType.HORSE && this.random.nextInt(10) == 0) {
						this.playSound(Sounds.ENTITY_HORSE_BREATHE, blockSoundGroup.getVolume() * 0.6F, blockSoundGroup.getPitch());
					}
				} else if (this.soundTicks <= 5) {
					this.playSound(Sounds.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
				}
			} else if (blockSoundGroup == BlockSoundGroup.field_12759) {
				this.playSound(Sounds.ENTITY_HORSE_STEP_WOOD, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			} else {
				this.playSound(Sounds.ENTITY_HORSE_STEP, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			}
		}
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(JUMP_STRENGTH_ATTRIBUTE);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(53.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.225F);
	}

	@Override
	public int getLimitPerChunk() {
		return 6;
	}

	public int getMaxTemper() {
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

	public boolean method_6271() {
		return this.method_13129() == HorseType.HORSE || this.method_13131() != HorseArmorType.NONE;
	}

	private void method_6245() {
		this.field_6895 = null;
	}

	public boolean method_11071() {
		return this.field_11974;
	}

	private void method_6246() {
		this.field_6895 = "horse/";
		this.field_6896[0] = null;
		this.field_6896[1] = null;
		this.field_6896[2] = null;
		HorseType horseType = this.method_13129();
		int i = this.getVariant();
		if (horseType == HorseType.HORSE) {
			int j = i & 0xFF;
			int k = (i & 0xFF00) >> 8;
			if (j >= HORSE_TEXTURES.length) {
				this.field_11974 = false;
				return;
			}

			this.field_6896[0] = HORSE_TEXTURES[j];
			this.field_6895 = this.field_6895 + field_6879[j];
			if (k >= HORSE_MARKINGS_TEXTURES.length) {
				this.field_11974 = false;
				return;
			}

			this.field_6896[1] = HORSE_MARKINGS_TEXTURES[k];
			this.field_6895 = this.field_6895 + field_6881[k];
		} else {
			this.field_6896[0] = "";
			this.field_6895 = this.field_6895 + "_" + horseType + "_";
		}

		HorseArmorType horseArmorType = this.method_13131();
		this.field_6896[2] = horseArmorType.getEntityTexture();
		this.field_6895 = this.field_6895 + horseArmorType.method_13138();
		this.field_11974 = true;
	}

	public String method_6272() {
		if (this.field_6895 == null) {
			this.method_6246();
		}

		return this.field_6895;
	}

	public String[] method_6273() {
		if (this.field_6895 == null) {
			this.method_6246();
		}

		return this.field_6896;
	}

	public void openInventory(PlayerEntity player) {
		if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(player)) && this.isTame()) {
			this.inventory.setName(this.getTranslationKey());
			player.openHorseInventory(this, this.inventory);
		}
	}

	@Override
	public boolean method_13079(PlayerEntity playerEntity, Hand hand, @Nullable ItemStack itemStack) {
		if (itemStack != null && itemStack.getItem() == Items.SPAWN_EGG) {
			return super.method_13079(playerEntity, hand, itemStack);
		} else if (!this.isTame() && this.method_13129().undead()) {
			return false;
		} else if (this.isTame() && this.method_6237() && playerEntity.isSneaking()) {
			this.openInventory(playerEntity);
			return true;
		} else if (this.method_6254() && this.hasPassengers()) {
			return super.method_13079(playerEntity, hand, itemStack);
		} else {
			if (itemStack != null) {
				if (this.method_13129().method_13152()) {
					HorseArmorType horseArmorType = HorseArmorType.method_13137(itemStack);
					if (horseArmorType != HorseArmorType.NONE) {
						if (!this.isTame()) {
							this.playAngrySound();
							return true;
						}

						this.openInventory(playerEntity);
						return true;
					}
				}

				boolean bl = false;
				if (!this.method_13129().undead()) {
					float f = 0.0F;
					int i = 0;
					int j = 0;
					if (itemStack.getItem() == Items.WHEAT) {
						f = 2.0F;
						i = 20;
						j = 3;
					} else if (itemStack.getItem() == Items.SUGAR) {
						f = 1.0F;
						i = 30;
						j = 3;
					} else if (Block.getBlockFromItem(itemStack.getItem()) == Blocks.HAY_BALE) {
						f = 20.0F;
						i = 180;
					} else if (itemStack.getItem() == Items.APPLE) {
						f = 3.0F;
						i = 60;
						j = 3;
					} else if (itemStack.getItem() == Items.GOLDEN_CARROT) {
						f = 4.0F;
						i = 60;
						j = 5;
						if (this.isTame() && this.age() == 0) {
							bl = true;
							this.lovePlayer(playerEntity);
						}
					} else if (itemStack.getItem() == Items.GOLDEN_APPLE) {
						f = 10.0F;
						i = 240;
						j = 10;
						if (this.isTame() && this.age() == 0 && !this.isInLove()) {
							bl = true;
							this.lovePlayer(playerEntity);
						}
					}

					if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
						this.heal(f);
						bl = true;
					}

					if (!this.method_6237() && i > 0) {
						if (!this.world.isClient) {
							this.method_6095(i);
						}

						bl = true;
					}

					if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
						bl = true;
						if (!this.world.isClient) {
							this.addTemper(j);
						}
					}

					if (bl) {
						this.playEatingAnimation();
					}
				}

				if (!this.isTame() && !bl) {
					if (itemStack.method_6329(playerEntity, this, hand)) {
						return true;
					}

					this.playAngrySound();
					return true;
				}

				if (!bl && this.method_13129().method_13148() && !this.hasChest() && itemStack.getItem() == Item.fromBlock(Blocks.CHEST)) {
					this.setHasChest(true);
					this.playSound(Sounds.ENTITY_DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
					bl = true;
					this.method_6243();
				}

				if (!bl && this.method_6254() && !this.isSaddled() && itemStack.getItem() == Items.SADDLE) {
					this.openInventory(playerEntity);
					return true;
				}

				if (bl) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.count--;
					}

					return true;
				}
			}

			if (!this.method_6254() || this.hasPassengers()) {
				return super.method_13079(playerEntity, hand, itemStack);
			} else if (itemStack != null && itemStack.method_6329(playerEntity, this, hand)) {
				return true;
			} else {
				this.putPlayerOnBack(playerEntity);
				return true;
			}
		}
	}

	private void putPlayerOnBack(PlayerEntity player) {
		player.yaw = this.yaw;
		player.pitch = this.pitch;
		this.setEatingGrass(false);
		this.setAngry(false);
		if (!this.world.isClient) {
			player.ride(this);
		}
	}

	@Override
	protected boolean method_2610() {
		return this.hasPassengers() && this.isSaddled() ? true : this.isEating() || this.isAngry();
	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack) {
		return false;
	}

	private void method_6247() {
		this.field_6897 = 1;
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (!this.world.isClient) {
			this.method_6240();
		}
	}

	@Override
	public void tickMovement() {
		if (this.random.nextInt(200) == 0) {
			this.method_6247();
		}

		super.tickMovement();
		if (!this.world.isClient) {
			if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}

			if (!this.isEating()
				&& !this.hasPassengers()
				&& this.random.nextInt(300) == 0
				&& this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.y) - 1, MathHelper.floor(this.z))).getBlock() == Blocks.GRASS) {
				this.setEatingGrass(true);
			}

			if (this.isEating() && ++this.eatingGrassTicks > 50) {
				this.eatingGrassTicks = 0;
				this.setEatingGrass(false);
			}

			if (this.isReadyToBreed() && !this.method_6237() && !this.isEating()) {
				HorseBaseEntity horseBaseEntity = this.method_6231(this, 16.0);
				if (horseBaseEntity != null && this.squaredDistanceTo(horseBaseEntity) > 4.0) {
					this.navigation.method_13109(horseBaseEntity);
				}
			}

			if (this.method_13128() && this.field_14636++ >= 18000) {
				this.remove();
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && this.dataTracker.isDirty()) {
			this.dataTracker.clearDirty();
			this.method_6245();
		}

		if (this.eatingTicks > 0 && ++this.eatingTicks > 30) {
			this.eatingTicks = 0;
			this.setHorseFlag(128, false);
		}

		if (this.method_13003() && this.angryTicks > 0 && ++this.angryTicks > 20) {
			this.angryTicks = 0;
			this.setAngry(false);
		}

		if (this.field_6897 > 0 && ++this.field_6897 > 8) {
			this.field_6897 = 0;
		}

		if (this.field_6898 > 0) {
			this.field_6898++;
			if (this.field_6898 > 300) {
				this.field_6898 = 0;
			}
		}

		this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
		if (this.isEating()) {
			this.eatingGrassAnimationProgress = this.eatingGrassAnimationProgress + (1.0F - this.eatingGrassAnimationProgress) * 0.4F + 0.05F;
			if (this.eatingGrassAnimationProgress > 1.0F) {
				this.eatingGrassAnimationProgress = 1.0F;
			}
		} else {
			this.eatingGrassAnimationProgress = this.eatingGrassAnimationProgress + ((0.0F - this.eatingGrassAnimationProgress) * 0.4F - 0.05F);
			if (this.eatingGrassAnimationProgress < 0.0F) {
				this.eatingGrassAnimationProgress = 0.0F;
			}
		}

		this.lastAngryAnimationProgress = this.angryAnimationProgress;
		if (this.isAngry()) {
			this.eatingGrassAnimationProgress = 0.0F;
			this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress;
			this.angryAnimationProgress = this.angryAnimationProgress + (1.0F - this.angryAnimationProgress) * 0.4F + 0.05F;
			if (this.angryAnimationProgress > 1.0F) {
				this.angryAnimationProgress = 1.0F;
			}
		} else {
			this.jumping = false;
			this.angryAnimationProgress = this.angryAnimationProgress
				+ ((0.8F * this.angryAnimationProgress * this.angryAnimationProgress * this.angryAnimationProgress - this.angryAnimationProgress) * 0.6F - 0.05F);
			if (this.angryAnimationProgress < 0.0F) {
				this.angryAnimationProgress = 0.0F;
			}
		}

		this.lastEatingAnimationProgress = this.eatingAnimationProgress;
		if (this.getHorseFlag(128)) {
			this.eatingAnimationProgress = this.eatingAnimationProgress + (1.0F - this.eatingAnimationProgress) * 0.7F + 0.05F;
			if (this.eatingAnimationProgress > 1.0F) {
				this.eatingAnimationProgress = 1.0F;
			}
		} else {
			this.eatingAnimationProgress = this.eatingAnimationProgress + ((0.0F - this.eatingAnimationProgress) * 0.7F - 0.05F);
			if (this.eatingAnimationProgress < 0.0F) {
				this.eatingAnimationProgress = 0.0F;
			}
		}
	}

	private void setEating() {
		if (!this.world.isClient) {
			this.eatingTicks = 1;
			this.setHorseFlag(128, true);
		}
	}

	private boolean canBreed() {
		return !this.hasPassengers()
			&& !this.hasMount()
			&& this.isTame()
			&& this.method_6237()
			&& this.method_13129().method_13151()
			&& this.getHealth() >= this.getMaxHealth()
			&& this.isInLove();
	}

	public void setEatingGrass(boolean eatingGrass) {
		this.setHorseFlag(32, eatingGrass);
	}

	public void setAngry(boolean angry) {
		if (angry) {
			this.setEatingGrass(false);
		}

		this.setHorseFlag(64, angry);
	}

	private void updateAnger() {
		if (this.method_13003()) {
			this.angryTicks = 1;
			this.setAngry(true);
		}
	}

	public void playAngrySound() {
		this.updateAnger();
		Sound sound = this.method_13132();
		if (sound != null) {
			this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public void method_6240() {
		this.method_6232(this, this.inventory);
		this.method_6266();
	}

	private void method_6232(Entity entity, AnimalInventory animalInventory) {
		if (animalInventory != null && !this.world.isClient) {
			for (int i = 0; i < animalInventory.getInvSize(); i++) {
				ItemStack itemStack = animalInventory.getInvStack(i);
				if (itemStack != null) {
					this.dropItem(itemStack, 0.0F);
				}
			}
		}
	}

	public boolean bondWithPlayer(PlayerEntity player) {
		this.method_13127(player.getUuid());
		this.setTame(true);
		return true;
	}

	@Override
	public void travel(float f, float g) {
		if (this.hasPassengers() && this.canBeControlledByRider() && this.isSaddled()) {
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
				this.soundTicks = 0;
			}

			if (this.onGround && this.jumpStrength == 0.0F && this.isAngry() && !this.jumping) {
				f = 0.0F;
				g = 0.0F;
			}

			if (this.jumpStrength > 0.0F && !this.isInAir() && this.onGround) {
				this.velocityY = this.getJumpStrength() * (double)this.jumpStrength;
				if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
					this.velocityY = this.velocityY + (double)((float)(this.getEffectInstance(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
				}

				this.setInAir(true);
				this.velocityDirty = true;
				if (g > 0.0F) {
					float h = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
					float i = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
					this.velocityX = this.velocityX + (double)(-0.4F * h * this.jumpStrength);
					this.velocityZ = this.velocityZ + (double)(0.4F * i * this.jumpStrength);
					this.playSound(Sounds.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
				}

				this.jumpStrength = 0.0F;
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
				this.jumpStrength = 0.0F;
				this.setInAir(false);
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

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "EntityHorse");
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemListSchema("EntityHorse", "Items"));
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemSchema("EntityHorse", "ArmorItem", "SaddleItem"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("EatingHaystack", this.isEating());
		nbt.putBoolean("ChestedHorse", this.hasChest());
		nbt.putBoolean("HasReproduced", this.hasBred());
		nbt.putBoolean("Bred", this.isReadyToBreed());
		nbt.putInt("Type", this.method_13129().method_13153());
		nbt.putInt("Variant", this.getVariant());
		nbt.putInt("Temper", this.getTemper());
		nbt.putBoolean("Tame", this.isTame());
		nbt.putBoolean("SkeletonTrap", this.method_13128());
		nbt.putInt("SkeletonTrapTime", this.field_14636);
		if (this.method_13130() != null) {
			nbt.putString("OwnerUUID", this.method_13130().toString());
		}

		if (this.hasChest()) {
			NbtList nbtList = new NbtList();

			for (int i = 2; i < this.inventory.getInvSize(); i++) {
				ItemStack itemStack = this.inventory.getInvStack(i);
				if (itemStack != null) {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.putByte("Slot", (byte)i);
					itemStack.toNbt(nbtCompound);
					nbtList.add(nbtCompound);
				}
			}

			nbt.put("Items", nbtList);
		}

		if (this.inventory.getInvStack(1) != null) {
			nbt.put("ArmorItem", this.inventory.getInvStack(1).toNbt(new NbtCompound()));
		}

		if (this.inventory.getInvStack(0) != null) {
			nbt.put("SaddleItem", this.inventory.getInvStack(0).toNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setEatingGrass(nbt.getBoolean("EatingHaystack"));
		this.setBred(nbt.getBoolean("Bred"));
		this.setHasChest(nbt.getBoolean("ChestedHorse"));
		this.setHasBred(nbt.getBoolean("HasReproduced"));
		this.method_13126(HorseType.method_13143(nbt.getInt("Type")));
		this.setVariant(nbt.getInt("Variant"));
		this.setTemper(nbt.getInt("Temper"));
		this.setTame(nbt.getBoolean("Tame"));
		this.method_13133(nbt.getBoolean("SkeletonTrap"));
		this.field_14636 = nbt.getInt("SkeletonTrapTime");
		String string;
		if (nbt.contains("OwnerUUID", 8)) {
			string = nbt.getString("OwnerUUID");
		} else {
			String string2 = nbt.getString("Owner");
			string = ServerConfigHandler.method_8204(this.getMinecraftServer(), string2);
		}

		if (!string.isEmpty()) {
			this.method_13127(UUID.fromString(string));
		}

		EntityAttributeInstance entityAttributeInstance = this.getAttributeContainer().get("Speed");
		if (entityAttributeInstance != null) {
			this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(entityAttributeInstance.getBaseValue() * 0.25);
		}

		if (this.hasChest()) {
			NbtList nbtList = nbt.getList("Items", 10);
			this.method_6243();

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				int j = nbtCompound.getByte("Slot") & 255;
				if (j >= 2 && j < this.inventory.getInvSize()) {
					this.inventory.setInvStack(j, ItemStack.fromNbt(nbtCompound));
				}
			}
		}

		if (nbt.contains("ArmorItem", 10)) {
			ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("ArmorItem"));
			if (itemStack != null && HorseArmorType.method_13139(itemStack.getItem())) {
				this.inventory.setInvStack(1, itemStack);
			}
		}

		if (nbt.contains("SaddleItem", 10)) {
			ItemStack itemStack2 = ItemStack.fromNbt(nbt.getCompound("SaddleItem"));
			if (itemStack2 != null && itemStack2.getItem() == Items.SADDLE) {
				this.inventory.setInvStack(0, itemStack2);
			}
		}

		this.updateSaddle();
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else if (other.getClass() != this.getClass()) {
			return false;
		} else {
			HorseBaseEntity horseBaseEntity = (HorseBaseEntity)other;
			if (this.canBreed() && horseBaseEntity.canBreed()) {
				HorseType horseType = this.method_13129();
				HorseType horseType2 = horseBaseEntity.method_13129();
				return horseType == horseType2
					|| horseType == HorseType.HORSE && horseType2 == HorseType.DONKEY
					|| horseType == HorseType.DONKEY && horseType2 == HorseType.HORSE;
			} else {
				return false;
			}
		}
	}

	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		HorseBaseEntity horseBaseEntity = (HorseBaseEntity)entity;
		HorseBaseEntity horseBaseEntity2 = new HorseBaseEntity(this.world);
		HorseType horseType = this.method_13129();
		HorseType horseType2 = horseBaseEntity.method_13129();
		HorseType horseType3 = HorseType.HORSE;
		if (horseType == horseType2) {
			horseType3 = horseType;
		} else if (horseType == HorseType.HORSE && horseType2 == HorseType.DONKEY || horseType == HorseType.DONKEY && horseType2 == HorseType.HORSE) {
			horseType3 = HorseType.MULE;
		}

		if (horseType3 == HorseType.HORSE) {
			int i = this.random.nextInt(9);
			int j;
			if (i < 4) {
				j = this.getVariant() & 0xFF;
			} else if (i < 8) {
				j = horseBaseEntity.getVariant() & 0xFF;
			} else {
				j = this.random.nextInt(7);
			}

			int m = this.random.nextInt(5);
			if (m < 2) {
				j |= this.getVariant() & 0xFF00;
			} else if (m < 4) {
				j |= horseBaseEntity.getVariant() & 0xFF00;
			} else {
				j |= this.random.nextInt(5) << 8 & 0xFF00;
			}

			horseBaseEntity2.setVariant(j);
		}

		horseBaseEntity2.method_13126(horseType3);
		double d = this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue()
			+ entity.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue()
			+ (double)this.getChildHealthBonus();
		horseBaseEntity2.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(d / 3.0);
		double e = this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).getBaseValue()
			+ entity.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).getBaseValue()
			+ this.getChildJumpStrengthBonus();
		horseBaseEntity2.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).setBaseValue(e / 3.0);
		double f = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue()
			+ entity.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue()
			+ this.method_6253();
		horseBaseEntity2.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(f / 3.0);
		return horseBaseEntity2;
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		int i = 0;
		HorseType horseType;
		if (data instanceof HorseBaseEntity.Data) {
			horseType = ((HorseBaseEntity.Data)data).field_14637;
			i = ((HorseBaseEntity.Data)data).field_6909 & 0xFF | this.random.nextInt(5) << 8;
		} else {
			if (this.random.nextInt(10) == 0) {
				horseType = HorseType.DONKEY;
			} else {
				int j = this.random.nextInt(7);
				int k = this.random.nextInt(5);
				horseType = HorseType.HORSE;
				i = j | k << 8;
			}

			data = new HorseBaseEntity.Data(horseType, i);
		}

		this.method_13126(horseType);
		this.setVariant(i);
		if (this.random.nextInt(5) == 0) {
			this.setAge(-24000);
		}

		if (horseType.undead()) {
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(15.0);
			this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
		} else {
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)this.getChildHealthBonus());
			if (horseType == HorseType.HORSE) {
				this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.method_6253());
			} else {
				this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.175F);
			}
		}

		if (horseType.method_13149()) {
			this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).setBaseValue(0.5);
		} else {
			this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).setBaseValue(this.getChildJumpStrengthBonus());
		}

		this.setHealth(this.getMaxHealth());
		return data;
	}

	@Override
	public boolean canBeControlledByRider() {
		Entity entity = this.getPrimaryPassenger();
		return entity instanceof LivingEntity;
	}

	public float method_6289(float f) {
		return this.lastEatingGrassAnimationProgress + (this.eatingGrassAnimationProgress - this.lastEatingGrassAnimationProgress) * f;
	}

	public float getAngryAnimationProgress(float tickDelta) {
		return this.lastAngryAnimationProgress + (this.angryAnimationProgress - this.lastAngryAnimationProgress) * tickDelta;
	}

	public float getEatingAnimationProgress(float tickDelta) {
		return this.lastEatingAnimationProgress + (this.eatingAnimationProgress - this.lastEatingAnimationProgress) * tickDelta;
	}

	@Override
	public void method_6299(int i) {
		if (this.isSaddled()) {
			if (i < 0) {
				i = 0;
			} else {
				this.jumping = true;
				this.updateAnger();
			}

			if (i >= 90) {
				this.jumpStrength = 1.0F;
			} else {
				this.jumpStrength = 0.4F + 0.4F * (float)i / 90.0F;
			}
		}
	}

	@Override
	public boolean method_13089() {
		return this.isSaddled();
	}

	@Override
	public void method_13090(int i) {
		this.jumping = true;
		this.updateAnger();
	}

	@Override
	public void method_13091() {
	}

	protected void spawnPlayerReactionParticles(boolean positive) {
		ParticleType particleType = positive ? ParticleType.HEART : ParticleType.SMOKE;

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
			this.spawnPlayerReactionParticles(true);
		} else if (status == 6) {
			this.spawnPlayerReactionParticles(false);
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

		if (this.lastAngryAnimationProgress > 0.0F) {
			float f = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
			float g = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
			float h = 0.7F * this.lastAngryAnimationProgress;
			float i = 0.15F * this.lastAngryAnimationProgress;
			passenger.updatePosition(
				this.x + (double)(h * f), this.y + this.getMountedHeightOffset() + passenger.getHeightOffset() + (double)i, this.z - (double)(h * g)
			);
			if (passenger instanceof LivingEntity) {
				((LivingEntity)passenger).bodyYaw = this.bodyYaw;
			}
		}
	}

	@Override
	public double getMountedHeightOffset() {
		double d = super.getMountedHeightOffset();
		if (this.method_13129() == HorseType.SKELETON) {
			d -= 0.1875;
		} else if (this.method_13129() == HorseType.DONKEY) {
			d -= 0.25;
		}

		return d;
	}

	private float getChildHealthBonus() {
		return 15.0F + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
	}

	private double getChildJumpStrengthBonus() {
		return 0.4F + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
	}

	private double method_6253() {
		return (0.45F + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
	}

	public boolean method_13128() {
		return this.field_14635;
	}

	public void method_13133(boolean bl) {
		if (bl != this.field_14635) {
			this.field_14635 = bl;
			if (bl) {
				this.goals.add(1, this.field_14634);
			} else {
				this.goals.method_4497(this.field_14634);
			}
		}
	}

	@Override
	public boolean isClimbing() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return this.height;
	}

	@Override
	public boolean equip(int slot, @Nullable ItemStack item) {
		if (slot == 499 && this.method_13129().method_13148()) {
			if (item == null && this.hasChest()) {
				this.setHasChest(false);
				this.method_6243();
				return true;
			}

			if (item != null && item.getItem() == Item.fromBlock(Blocks.CHEST) && !this.hasChest()) {
				this.setHasChest(true);
				this.method_6243();
				return true;
			}
		}

		int i = slot - 400;
		if (i >= 0 && i < 2 && i < this.inventory.getInvSize()) {
			if (i == 0 && item != null && item.getItem() != Items.SADDLE) {
				return false;
			} else if (i != 1 || (item == null || HorseArmorType.method_13139(item.getItem())) && this.method_13129().method_13152()) {
				this.inventory.setInvStack(i, item);
				this.updateSaddle();
				return true;
			} else {
				return false;
			}
		} else {
			int j = slot - 500 + 2;
			if (j >= 2 && j < this.inventory.getInvSize()) {
				this.inventory.setInvStack(j, item);
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

	@Override
	public EntityGroup getGroup() {
		return this.method_13129().undead() ? EntityGroup.UNDEAD : EntityGroup.DEFAULT;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return this.method_13129().getLootTable();
	}

	public static class Data implements EntityData {
		public HorseType field_14637;
		public int field_6909;

		public Data(HorseType horseType, int i) {
			this.field_14637 = horseType;
			this.field_6909 = i;
		}
	}
}
