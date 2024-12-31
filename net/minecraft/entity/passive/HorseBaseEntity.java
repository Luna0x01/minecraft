package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
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
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class HorseBaseEntity extends AnimalEntity implements SimpleInventoryListener {
	private static final Predicate<Entity> EATING_GRASS_ENTITY_PREDICATE = new Predicate<Entity>() {
		public boolean apply(Entity entity) {
			return entity instanceof HorseBaseEntity && ((HorseBaseEntity)entity).isReadyToBreed();
		}
	};
	private static final EntityAttribute JUMP_STRENGTH_ATTRIBUTE = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0)
		.setName("Jump Strength")
		.setTracked(true);
	private static final String[] HORSE_ARMOR_TEXTURES = new String[]{
		null,
		"textures/entity/horse/armor/horse_armor_iron.png",
		"textures/entity/horse/armor/horse_armor_gold.png",
		"textures/entity/horse/armor/horse_armor_diamond.png"
	};
	private static final String[] field_6905 = new String[]{"", "meo", "goo", "dio"};
	private static final int[] field_6906 = new int[]{0, 5, 7, 11};
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
	private float eatingGrassAnimationProgress;
	private float lastEatingGrassAnimationProgress;
	private float angryAnimationProgress;
	private float lastAngryAnimationProgress;
	private float eatingAnimationProgress;
	private float lastEatingAnimationProgress;
	private int soundTicks;
	private String field_6895;
	private String[] field_6896 = new String[3];
	private boolean field_11974 = false;

	public HorseBaseEntity(World world) {
		super(world);
		this.setBounds(1.4F, 1.6F);
		this.isFireImmune = false;
		this.setHasChest(false);
		((MobNavigation)this.getNavigation()).method_11027(true);
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 1.2));
		this.goals.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goals.add(2, new BreedGoal(this, 1.0));
		this.goals.add(4, new FollowParentGoal(this, 1.0));
		this.goals.add(6, new WanderAroundGoal(this, 0.7));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.method_6243();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, 0);
		this.dataTracker.track(19, (byte)0);
		this.dataTracker.track(20, 0);
		this.dataTracker.track(21, String.valueOf(""));
		this.dataTracker.track(22, 0);
	}

	public void setType(int type) {
		this.dataTracker.setProperty(19, (byte)type);
		this.method_6245();
	}

	public int getType() {
		return this.dataTracker.getByte(19);
	}

	public void setVariant(int variant) {
		this.dataTracker.setProperty(20, variant);
		this.method_6245();
	}

	public int getVariant() {
		return this.dataTracker.getInt(20);
	}

	@Override
	public String getTranslationKey() {
		if (this.hasCustomName()) {
			return this.getCustomName();
		} else {
			int i = this.getType();
			switch (i) {
				case 0:
				default:
					return CommonI18n.translate("entity.horse.name");
				case 1:
					return CommonI18n.translate("entity.donkey.name");
				case 2:
					return CommonI18n.translate("entity.mule.name");
				case 3:
					return CommonI18n.translate("entity.zombiehorse.name");
				case 4:
					return CommonI18n.translate("entity.skeletonhorse.name");
			}
		}
	}

	private boolean getHorseFlag(int bitMask) {
		return (this.dataTracker.getInt(16) & bitMask) != 0;
	}

	private void setHorseFlag(int bitMask, boolean flag) {
		int i = this.dataTracker.getInt(16);
		if (flag) {
			this.dataTracker.setProperty(16, i | bitMask);
		} else {
			this.dataTracker.setProperty(16, i & ~bitMask);
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

	public String getOwnerUuid() {
		return this.dataTracker.getString(21);
	}

	public void method_6234(String string) {
		this.dataTracker.setProperty(21, string);
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
	public boolean isTameable() {
		return !this.method_6276() && super.isTameable();
	}

	@Override
	protected void method_6175(float f) {
		if (f > 6.0F && this.isEating()) {
			this.setEatingGrass(false);
		}
	}

	public boolean hasChest() {
		return this.getHorseFlag(8);
	}

	public int method_6259() {
		return this.dataTracker.getInt(22);
	}

	private int method_8391(ItemStack itemStack) {
		if (itemStack == null) {
			return 0;
		} else {
			Item item = itemStack.getItem();
			if (item == Items.IRON_HORSE_ARMOR) {
				return 1;
			} else if (item == Items.GOLDEN_HORSE_ARMOR) {
				return 2;
			} else {
				return item == Items.DIAMOND_HORSE_ARMOR ? 3 : 0;
			}
		}
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
		this.dataTracker.setProperty(22, this.method_8391(itemStack));
		this.method_6245();
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
		return this.rider != null && this.rider.equals(entity) ? false : super.damage(source, amount);
	}

	@Override
	public int getArmorProtectionValue() {
		return field_6906[this.method_6259()];
	}

	@Override
	public boolean isPushable() {
		return this.rider == null;
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
			this.world.playSound(this, "eating", 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		if (fallDistance > 1.0F) {
			this.playSound("mob.horse.land", 0.4F, 1.0F);
		}

		int i = MathHelper.ceil((fallDistance * 0.5F - 3.0F) * damageMultiplier);
		if (i > 0) {
			this.damage(DamageSource.FALL, (float)i);
			if (this.rider != null) {
				this.rider.damage(DamageSource.FALL, (float)i);
			}

			Block block = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z)).getBlock();
			if (block.getMaterial() != Material.AIR && !this.isSilent()) {
				Block.Sound sound = block.sound;
				this.world.playSound(this, sound.getStepSound(), sound.getVolume() * 0.5F, sound.getPitch() * 0.75F);
			}
		}
	}

	private int method_6242() {
		int i = this.getType();
		return !this.hasChest() || i != 1 && i != 2 ? 2 : 17;
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
			if (this.drawHoverEffect()) {
				this.method_8390(this.inventory.getInvStack(1));
			}
		}
	}

	@Override
	public void onChanged(SimpleInventory inventory) {
		int i = this.method_6259();
		boolean bl = this.isSaddled();
		this.updateSaddle();
		if (this.ticksAlive > 20) {
			if (i == 0 && i != this.method_6259()) {
				this.playSound("mob.horse.armor", 0.5F, 1.0F);
			} else if (i != this.method_6259()) {
				this.playSound("mob.horse.armor", 0.5F, 1.0F);
			}

			if (!bl && this.isSaddled()) {
				this.playSound("mob.horse.leather", 0.5F, 1.0F);
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
	protected String getDeathSound() {
		this.setEating();
		int i = this.getType();
		if (i == 3) {
			return "mob.horse.zombie.death";
		} else if (i == 4) {
			return "mob.horse.skeleton.death";
		} else {
			return i != 1 && i != 2 ? "mob.horse.death" : "mob.horse.donkey.death";
		}
	}

	@Override
	protected Item getDefaultDrop() {
		boolean bl = this.random.nextInt(4) == 0;
		int i = this.getType();
		if (i == 4) {
			return Items.BONE;
		} else if (i == 3) {
			return bl ? null : Items.ROTTEN_FLESH;
		} else {
			return Items.LEATHER;
		}
	}

	@Override
	protected String getHurtSound() {
		this.setEating();
		if (this.random.nextInt(3) == 0) {
			this.updateAnger();
		}

		int i = this.getType();
		if (i == 3) {
			return "mob.horse.zombie.hit";
		} else if (i == 4) {
			return "mob.horse.skeleton.hit";
		} else {
			return i != 1 && i != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit";
		}
	}

	public boolean isSaddled() {
		return this.getHorseFlag(4);
	}

	@Override
	protected String getAmbientSound() {
		this.setEating();
		if (this.random.nextInt(10) == 0 && !this.method_2610()) {
			this.updateAnger();
		}

		int i = this.getType();
		if (i == 3) {
			return "mob.horse.zombie.idle";
		} else if (i == 4) {
			return "mob.horse.skeleton.idle";
		} else {
			return i != 1 && i != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle";
		}
	}

	protected String method_6269() {
		this.setEating();
		this.updateAnger();
		int i = this.getType();
		if (i == 3 || i == 4) {
			return null;
		} else {
			return i != 1 && i != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry";
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		Block.Sound sound = block.sound;
		if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER) {
			sound = Blocks.SNOW_LAYER.sound;
		}

		if (!block.getMaterial().isFluid()) {
			int i = this.getType();
			if (this.rider != null && i != 1 && i != 2) {
				this.soundTicks++;
				if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
					this.playSound("mob.horse.gallop", sound.getVolume() * 0.15F, sound.getPitch());
					if (i == 0 && this.random.nextInt(10) == 0) {
						this.playSound("mob.horse.breathe", sound.getVolume() * 0.6F, sound.getPitch());
					}
				} else if (this.soundTicks <= 5) {
					this.playSound("mob.horse.wood", sound.getVolume() * 0.15F, sound.getPitch());
				}
			} else if (sound == Block.WOOD) {
				this.playSound("mob.horse.wood", sound.getVolume() * 0.15F, sound.getPitch());
			} else {
				this.playSound("mob.horse.soft", sound.getVolume() * 0.15F, sound.getPitch());
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
		return this.getType() == 0 || this.method_6259() > 0;
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
		int i = this.getType();
		int j = this.getVariant();
		if (i == 0) {
			int k = j & 0xFF;
			int l = (j & 0xFF00) >> 8;
			if (k >= HORSE_TEXTURES.length) {
				this.field_11974 = false;
				return;
			}

			this.field_6896[0] = HORSE_TEXTURES[k];
			this.field_6895 = this.field_6895 + field_6879[k];
			if (l >= HORSE_MARKINGS_TEXTURES.length) {
				this.field_11974 = false;
				return;
			}

			this.field_6896[1] = HORSE_MARKINGS_TEXTURES[l];
			this.field_6895 = this.field_6895 + field_6881[l];
		} else {
			this.field_6896[0] = "";
			this.field_6895 = this.field_6895 + "_" + i + "_";
		}

		int m = this.method_6259();
		if (m >= HORSE_ARMOR_TEXTURES.length) {
			this.field_11974 = false;
		} else {
			this.field_6896[2] = HORSE_ARMOR_TEXTURES[m];
			this.field_6895 = this.field_6895 + field_6905[m];
			this.field_11974 = true;
		}
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
		if (!this.world.isClient && (this.rider == null || this.rider == player) && this.isTame()) {
			this.inventory.setName(this.getTranslationKey());
			player.openHorseInventory(this, this.inventory);
		}
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.getMainHandStack();
		if (itemStack != null && itemStack.getItem() == Items.SPAWN_EGG) {
			return super.method_2537(playerEntity);
		} else if (!this.isTame() && this.method_6276()) {
			return false;
		} else if (this.isTame() && this.method_6237() && playerEntity.isSneaking()) {
			this.openInventory(playerEntity);
			return true;
		} else if (this.method_6254() && this.rider != null) {
			return super.method_2537(playerEntity);
		} else {
			if (itemStack != null) {
				boolean bl = false;
				if (this.drawHoverEffect()) {
					int i = -1;
					if (itemStack.getItem() == Items.IRON_HORSE_ARMOR) {
						i = 1;
					} else if (itemStack.getItem() == Items.GOLDEN_HORSE_ARMOR) {
						i = 2;
					} else if (itemStack.getItem() == Items.DIAMOND_HORSE_ARMOR) {
						i = 3;
					}

					if (i >= 0) {
						if (!this.isTame()) {
							this.playAngrySound();
							return true;
						}

						this.openInventory(playerEntity);
						return true;
					}
				}

				if (!bl && !this.method_6276()) {
					float f = 0.0F;
					int j = 0;
					int k = 0;
					if (itemStack.getItem() == Items.WHEAT) {
						f = 2.0F;
						j = 20;
						k = 3;
					} else if (itemStack.getItem() == Items.SUGAR) {
						f = 1.0F;
						j = 30;
						k = 3;
					} else if (Block.getBlockFromItem(itemStack.getItem()) == Blocks.HAY_BALE) {
						f = 20.0F;
						j = 180;
					} else if (itemStack.getItem() == Items.APPLE) {
						f = 3.0F;
						j = 60;
						k = 3;
					} else if (itemStack.getItem() == Items.GOLDEN_CARROT) {
						f = 4.0F;
						j = 60;
						k = 5;
						if (this.isTame() && this.age() == 0) {
							bl = true;
							this.lovePlayer(playerEntity);
						}
					} else if (itemStack.getItem() == Items.GOLDEN_APPLE) {
						f = 10.0F;
						j = 240;
						k = 10;
						if (this.isTame() && this.age() == 0) {
							bl = true;
							this.lovePlayer(playerEntity);
						}
					}

					if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
						this.heal(f);
						bl = true;
					}

					if (!this.method_6237() && j > 0) {
						this.method_6095(j);
						bl = true;
					}

					if (k > 0 && (bl || !this.isTame()) && k < this.getMaxTemper()) {
						bl = true;
						this.addTemper(k);
					}

					if (bl) {
						this.playEatingAnimation();
					}
				}

				if (!this.isTame() && !bl) {
					if (itemStack != null && itemStack.canUseOnEntity(playerEntity, this)) {
						return true;
					}

					this.playAngrySound();
					return true;
				}

				if (!bl && this.method_6275() && !this.hasChest() && itemStack.getItem() == Item.fromBlock(Blocks.CHEST)) {
					this.setHasChest(true);
					this.playSound("mob.chickenplop", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
					bl = true;
					this.method_6243();
				}

				if (!bl && this.method_6254() && !this.isSaddled() && itemStack.getItem() == Items.SADDLE) {
					this.openInventory(playerEntity);
					return true;
				}

				if (bl) {
					if (!playerEntity.abilities.creativeMode && --itemStack.count == 0) {
						playerEntity.inventory.setInvStack(playerEntity.inventory.selectedSlot, null);
					}

					return true;
				}
			}

			if (!this.method_6254() || this.rider != null) {
				return super.method_2537(playerEntity);
			} else if (itemStack != null && itemStack.canUseOnEntity(playerEntity, this)) {
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
			player.startRiding(this);
		}
	}

	public boolean drawHoverEffect() {
		return this.getType() == 0;
	}

	public boolean method_6275() {
		int i = this.getType();
		return i == 2 || i == 1;
	}

	@Override
	protected boolean method_2610() {
		return this.rider != null && this.isSaddled() ? true : this.isEating() || this.isAngry();
	}

	public boolean method_6276() {
		int i = this.getType();
		return i == 3 || i == 4;
	}

	public boolean method_6277() {
		return this.method_6276() || this.getType() == 2;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
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
				&& this.rider == null
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
					this.navigation.findPathTo(horseBaseEntity);
				}
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

		if (!this.world.isClient && this.angryTicks > 0 && ++this.angryTicks > 20) {
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
			this.lastEatingGrassAnimationProgress = this.eatingGrassAnimationProgress = 0.0F;
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
		return this.rider == null
			&& this.vehicle == null
			&& this.isTame()
			&& this.method_6237()
			&& !this.method_6277()
			&& this.getHealth() >= this.getMaxHealth()
			&& this.isInLove();
	}

	@Override
	public void setSwimming(boolean swimming) {
		this.setHorseFlag(32, swimming);
	}

	public void setEatingGrass(boolean eatingGrass) {
		this.setSwimming(eatingGrass);
	}

	public void setAngry(boolean angry) {
		if (angry) {
			this.setEatingGrass(false);
		}

		this.setHorseFlag(64, angry);
	}

	private void updateAnger() {
		if (!this.world.isClient) {
			this.angryTicks = 1;
			this.setAngry(true);
		}
	}

	public void playAngrySound() {
		this.updateAnger();
		String string = this.method_6269();
		if (string != null) {
			this.playSound(string, this.getSoundVolume(), this.getSoundPitch());
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
		this.method_6234(player.getUuid().toString());
		this.setTame(true);
		return true;
	}

	@Override
	public void travel(float f, float g) {
		if (this.rider != null && this.rider instanceof LivingEntity && this.isSaddled()) {
			this.prevYaw = this.yaw = this.rider.yaw;
			this.pitch = this.rider.pitch * 0.5F;
			this.setRotation(this.yaw, this.pitch);
			this.headYaw = this.bodyYaw = this.yaw;
			f = ((LivingEntity)this.rider).sidewaysSpeed * 0.5F;
			g = ((LivingEntity)this.rider).forwardSpeed;
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
				if (this.hasStatusEffect(StatusEffect.JUMP_BOOST)) {
					this.velocityY = this.velocityY + (double)((float)(this.getEffectInstance(StatusEffect.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
				}

				this.setInAir(true);
				this.velocityDirty = true;
				if (g > 0.0F) {
					float h = MathHelper.sin(this.yaw * (float) Math.PI / 180.0F);
					float i = MathHelper.cos(this.yaw * (float) Math.PI / 180.0F);
					this.velocityX = this.velocityX + (double)(-0.4F * h * this.jumpStrength);
					this.velocityZ = this.velocityZ + (double)(0.4F * i * this.jumpStrength);
					this.playSound("mob.horse.jump", 0.4F, 1.0F);
				}

				this.jumpStrength = 0.0F;
			}

			this.stepHeight = 1.0F;
			this.flyingSpeed = this.getMovementSpeed() * 0.1F;
			if (!this.world.isClient) {
				this.setMovementSpeed((float)this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
				super.travel(f, g);
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
			this.stepHeight = 0.5F;
			this.flyingSpeed = 0.02F;
			super.travel(f, g);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("EatingHaystack", this.isEating());
		nbt.putBoolean("ChestedHorse", this.hasChest());
		nbt.putBoolean("HasReproduced", this.hasBred());
		nbt.putBoolean("Bred", this.isReadyToBreed());
		nbt.putInt("Type", this.getType());
		nbt.putInt("Variant", this.getVariant());
		nbt.putInt("Temper", this.getTemper());
		nbt.putBoolean("Tame", this.isTame());
		nbt.putString("OwnerUUID", this.getOwnerUuid());
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
		this.setType(nbt.getInt("Type"));
		this.setVariant(nbt.getInt("Variant"));
		this.setTemper(nbt.getInt("Temper"));
		this.setTame(nbt.getBoolean("Tame"));
		String string = "";
		if (nbt.contains("OwnerUUID", 8)) {
			string = nbt.getString("OwnerUUID");
		} else {
			String string2 = nbt.getString("Owner");
			string = ServerConfigHandler.getPlayerUuidByName(string2);
		}

		if (string.length() > 0) {
			this.method_6234(string);
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
			if (itemStack != null && isHorseArmor(itemStack.getItem())) {
				this.inventory.setInvStack(1, itemStack);
			}
		}

		if (nbt.contains("SaddleItem", 10)) {
			ItemStack itemStack2 = ItemStack.fromNbt(nbt.getCompound("SaddleItem"));
			if (itemStack2 != null && itemStack2.getItem() == Items.SADDLE) {
				this.inventory.setInvStack(0, itemStack2);
			}
		} else if (nbt.getBoolean("Saddle")) {
			this.inventory.setInvStack(0, new ItemStack(Items.SADDLE));
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
				int i = this.getType();
				int j = horseBaseEntity.getType();
				return i == j || i == 0 && j == 1 || i == 1 && j == 0;
			} else {
				return false;
			}
		}
	}

	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		HorseBaseEntity horseBaseEntity = (HorseBaseEntity)entity;
		HorseBaseEntity horseBaseEntity2 = new HorseBaseEntity(this.world);
		int i = this.getType();
		int j = horseBaseEntity.getType();
		int k = 0;
		if (i == j) {
			k = i;
		} else if (i == 0 && j == 1 || i == 1 && j == 0) {
			k = 2;
		}

		if (k == 0) {
			int l = this.random.nextInt(9);
			int m;
			if (l < 4) {
				m = this.getVariant() & 0xFF;
			} else if (l < 8) {
				m = horseBaseEntity.getVariant() & 0xFF;
			} else {
				m = this.random.nextInt(7);
			}

			int p = this.random.nextInt(5);
			if (p < 2) {
				m |= this.getVariant() & 0xFF00;
			} else if (p < 4) {
				m |= horseBaseEntity.getVariant() & 0xFF00;
			} else {
				m |= this.random.nextInt(5) << 8 & 0xFF00;
			}

			horseBaseEntity2.setVariant(m);
		}

		horseBaseEntity2.setType(k);
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

	@Override
	public EntityData initialize(LocalDifficulty difficulty, EntityData data) {
		data = super.initialize(difficulty, data);
		int i = 0;
		int j = 0;
		if (data instanceof HorseBaseEntity.Data) {
			i = ((HorseBaseEntity.Data)data).field_6908;
			j = ((HorseBaseEntity.Data)data).field_6909 & 0xFF | this.random.nextInt(5) << 8;
		} else {
			if (this.random.nextInt(10) == 0) {
				i = 1;
			} else {
				int k = this.random.nextInt(7);
				int l = this.random.nextInt(5);
				i = 0;
				j = k | l << 8;
			}

			data = new HorseBaseEntity.Data(i, j);
		}

		this.setType(i);
		this.setVariant(j);
		if (this.random.nextInt(5) == 0) {
			this.setAge(-24000);
		}

		if (i != 4 && i != 3) {
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)this.getChildHealthBonus());
			if (i == 0) {
				this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.method_6253());
			} else {
				this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.175F);
			}
		} else {
			this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(15.0);
			this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
		}

		if (i != 2 && i != 1) {
			this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).setBaseValue(this.getChildJumpStrengthBonus());
		} else {
			this.initializeAttribute(JUMP_STRENGTH_ATTRIBUTE).setBaseValue(0.5);
		}

		this.setHealth(this.getMaxHealth());
		return data;
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
	public void updatePassengerPosition() {
		super.updatePassengerPosition();
		if (this.lastAngryAnimationProgress > 0.0F) {
			float f = MathHelper.sin(this.bodyYaw * (float) Math.PI / 180.0F);
			float g = MathHelper.cos(this.bodyYaw * (float) Math.PI / 180.0F);
			float h = 0.7F * this.lastAngryAnimationProgress;
			float i = 0.15F * this.lastAngryAnimationProgress;
			this.rider
				.updatePosition(this.x + (double)(h * f), this.y + this.getMountedHeightOffset() + this.rider.getHeightOffset() + (double)i, this.z - (double)(h * g));
			if (this.rider instanceof LivingEntity) {
				((LivingEntity)this.rider).bodyYaw = this.bodyYaw;
			}
		}
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

	public static boolean isHorseArmor(Item item) {
		return item == Items.IRON_HORSE_ARMOR || item == Items.GOLDEN_HORSE_ARMOR || item == Items.DIAMOND_HORSE_ARMOR;
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
	public boolean equip(int slot, ItemStack item) {
		if (slot == 499 && this.method_6275()) {
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
			} else if (i != 1 || (item == null || isHorseArmor(item.getItem())) && this.drawHoverEffect()) {
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

	public static class Data implements EntityData {
		public int field_6908;
		public int field_6909;

		public Data(int i, int j) {
			this.field_6908 = i;
			this.field_6909 = j;
		}
	}
}
