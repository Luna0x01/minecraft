package net.minecraft.entity.player;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MultipartEntityProvider;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ScreenHandlerLock;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;

public abstract class PlayerEntity extends LivingEntity {
	public PlayerInventory inventory = new PlayerInventory(this);
	private EnderChestInventory enderChest = new EnderChestInventory();
	public ScreenHandler playerScreenHandler;
	public ScreenHandler openScreenHandler;
	protected HungerManager hungerManager = new HungerManager();
	protected int abilityResyncCountdown;
	public float prevStrideDistance;
	public float strideDistance;
	public int experiencePickUpDelay;
	public double capeX;
	public double capeY;
	public double capeZ;
	public double prevCapeX;
	public double prevCapeY;
	public double prevCapeZ;
	protected boolean inBed;
	public BlockPos pos;
	private int sleepTimer;
	public float field_3993;
	public float field_4009;
	public float field_3994;
	private BlockPos spawnPos;
	private boolean spawnForced;
	private BlockPos field_4011;
	public PlayerAbilities abilities = new PlayerAbilities();
	public int experienceLevel;
	public int totalExperience;
	public float experienceProgress;
	private int enchantmentTableSeed;
	private ItemStack useItem;
	private int itemUseTicks;
	protected float field_4005 = 0.1F;
	protected float field_4006 = 0.02F;
	private int lastPlayedLevelUpSoundTime;
	private final GameProfile gameProfile;
	private boolean reducedDebugInfo = false;
	public FishingBobberEntity fishHook;

	public PlayerEntity(World world, GameProfile gameProfile) {
		super(world);
		this.playerUuid = getUuidFromProfile(gameProfile);
		this.gameProfile = gameProfile;
		this.playerScreenHandler = new PlayerScreenHandler(this.inventory, !world.isClient, this);
		this.openScreenHandler = this.playerScreenHandler;
		BlockPos blockPos = world.getSpawnPos();
		this.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
		this.field_6776 = 180.0F;
		this.fireResistance = 20;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, (byte)0);
		this.dataTracker.track(17, 0.0F);
		this.dataTracker.track(18, 0);
		this.dataTracker.track(10, (byte)0);
	}

	public ItemStack getUsedItem() {
		return this.useItem;
	}

	public int getItemUseTicks() {
		return this.itemUseTicks;
	}

	public boolean isUsingItem() {
		return this.useItem != null;
	}

	public int getRemainingUseTime() {
		return this.isUsingItem() ? this.useItem.getMaxUseTime() - this.itemUseTicks : 0;
	}

	public void stopUsingItem() {
		if (this.useItem != null) {
			this.useItem.onUseStopped(this.world, this, this.itemUseTicks);
		}

		this.resetUseItem();
	}

	public void resetUseItem() {
		this.useItem = null;
		this.itemUseTicks = 0;
		if (!this.world.isClient) {
			this.setSwimming(false);
		}
	}

	public boolean method_2611() {
		return this.isUsingItem() && this.useItem.getItem().getUseAction(this.useItem) == UseAction.BLOCK;
	}

	@Override
	public void tick() {
		this.noClip = this.isSpectator();
		if (this.isSpectator()) {
			this.onGround = false;
		}

		if (this.useItem != null) {
			ItemStack itemStack = this.inventory.getMainHandStack();
			if (itemStack == this.useItem) {
				if (this.itemUseTicks <= 25 && this.itemUseTicks % 4 == 0) {
					this.drink(itemStack, 5);
				}

				if (--this.itemUseTicks == 0 && !this.world.isClient) {
					this.useItem();
				}
			} else {
				this.resetUseItem();
			}
		}

		if (this.experiencePickUpDelay > 0) {
			this.experiencePickUpDelay--;
		}

		if (this.isSleeping()) {
			this.sleepTimer++;
			if (this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if (!this.world.isClient) {
				if (!this.method_3213()) {
					this.awaken(true, true, false);
				} else if (this.world.isDay()) {
					this.awaken(false, true, true);
				}
			}
		} else if (this.sleepTimer > 0) {
			this.sleepTimer++;
			if (this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		super.tick();
		if (!this.world.isClient && this.openScreenHandler != null && !this.openScreenHandler.canUse(this)) {
			this.closeHandledScreen();
			this.openScreenHandler = this.playerScreenHandler;
		}

		if (this.isOnFire() && this.abilities.invulnerable) {
			this.extinguish();
		}

		this.capeX = this.prevCapeX;
		this.capeY = this.prevCapeY;
		this.capeZ = this.prevCapeZ;
		double d = this.x - this.prevCapeX;
		double e = this.y - this.prevCapeY;
		double f = this.z - this.prevCapeZ;
		double g = 10.0;
		if (d > g) {
			this.capeX = this.prevCapeX = this.x;
		}

		if (f > g) {
			this.capeZ = this.prevCapeZ = this.z;
		}

		if (e > g) {
			this.capeY = this.prevCapeY = this.y;
		}

		if (d < -g) {
			this.capeX = this.prevCapeX = this.x;
		}

		if (f < -g) {
			this.capeZ = this.prevCapeZ = this.z;
		}

		if (e < -g) {
			this.capeY = this.prevCapeY = this.y;
		}

		this.prevCapeX += d * 0.25;
		this.prevCapeZ += f * 0.25;
		this.prevCapeY += e * 0.25;
		if (this.vehicle == null) {
			this.field_4011 = null;
		}

		if (!this.world.isClient) {
			this.hungerManager.update(this);
			this.incrementStat(Stats.MINUTES_PLAYED);
			if (this.isAlive()) {
				this.incrementStat(Stats.TIME_SINCE_DEATH);
			}
		}

		int i = 29999999;
		double h = MathHelper.clamp(this.x, -2.9999999E7, 2.9999999E7);
		double j = MathHelper.clamp(this.z, -2.9999999E7, 2.9999999E7);
		if (h != this.x || j != this.z) {
			this.updatePosition(h, this.y, j);
		}
	}

	@Override
	public int getMaxNetherPortalTime() {
		return this.abilities.invulnerable ? 0 : 80;
	}

	@Override
	protected String getSwimSound() {
		return "game.player.swim";
	}

	@Override
	protected String getSplashSound() {
		return "game.player.swim.splash";
	}

	@Override
	public int getDefaultNetherPortalCooldown() {
		return 10;
	}

	@Override
	public void playSound(String id, float volume, float pitch) {
		this.world.playSound(this, id, volume, pitch);
	}

	protected void drink(ItemStack potion, int i) {
		if (potion.getUseAction() == UseAction.DRINK) {
			this.playSound("random.drink", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}

		if (potion.getUseAction() == UseAction.EAT) {
			for (int j = 0; j < i; j++) {
				Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
				vec3d = vec3d.rotateX(-this.pitch * (float) Math.PI / 180.0F);
				vec3d = vec3d.rotateY(-this.yaw * (float) Math.PI / 180.0F);
				double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
				Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
				vec3d2 = vec3d2.rotateX(-this.pitch * (float) Math.PI / 180.0F);
				vec3d2 = vec3d2.rotateY(-this.yaw * (float) Math.PI / 180.0F);
				vec3d2 = vec3d2.add(this.x, this.y + (double)this.getEyeHeight(), this.z);
				if (potion.isUnbreakable()) {
					this.world
						.addParticle(ParticleType.ITEM_CRACK, vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z, Item.getRawId(potion.getItem()), potion.getData());
				} else {
					this.world.addParticle(ParticleType.ITEM_CRACK, vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z, Item.getRawId(potion.getItem()));
				}
			}

			this.playSound("random.eat", 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
		}
	}

	protected void useItem() {
		if (this.useItem != null) {
			this.drink(this.useItem, 16);
			int i = this.useItem.count;
			ItemStack itemStack = this.useItem.onFinishUse(this.world, this);
			if (itemStack != this.useItem || itemStack != null && itemStack.count != i) {
				this.inventory.main[this.inventory.selectedSlot] = itemStack;
				if (itemStack.count == 0) {
					this.inventory.main[this.inventory.selectedSlot] = null;
				}
			}

			this.resetUseItem();
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 9) {
			this.useItem();
		} else if (status == 23) {
			this.reducedDebugInfo = false;
		} else if (status == 22) {
			this.reducedDebugInfo = true;
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	protected boolean method_2610() {
		return this.getHealth() <= 0.0F || this.isSleeping();
	}

	protected void closeHandledScreen() {
		this.openScreenHandler = this.playerScreenHandler;
	}

	@Override
	public void tickRiding() {
		if (!this.world.isClient && this.isSneaking()) {
			this.startRiding(null);
			this.setSneaking(false);
		} else {
			double d = this.x;
			double e = this.y;
			double f = this.z;
			float g = this.yaw;
			float h = this.pitch;
			super.tickRiding();
			this.prevStrideDistance = this.strideDistance;
			this.strideDistance = 0.0F;
			this.method_3212(this.x - d, this.y - e, this.z - f);
			if (this.vehicle instanceof PigEntity) {
				this.pitch = h;
				this.yaw = g;
				this.bodyYaw = ((PigEntity)this.vehicle).bodyYaw;
			}
		}
	}

	@Override
	public void afterSpawn() {
		this.setBounds(0.6F, 1.8F);
		super.afterSpawn();
		this.setHealth(this.getMaxHealth());
		this.deathTime = 0;
	}

	@Override
	protected void tickNewAi() {
		super.tickNewAi();
		this.tickHandSwing();
		this.headYaw = this.yaw;
	}

	@Override
	public void tickMovement() {
		if (this.abilityResyncCountdown > 0) {
			this.abilityResyncCountdown--;
		}

		if (this.world.getGlobalDifficulty() == Difficulty.PEACEFUL && this.world.getGameRules().getBoolean("naturalRegeneration")) {
			if (this.getHealth() < this.getMaxHealth() && this.ticksAlive % 20 == 0) {
				this.heal(1.0F);
			}

			if (this.hungerManager.isNotFull() && this.ticksAlive % 10 == 0) {
				this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
			}
		}

		this.inventory.updateItems();
		this.prevStrideDistance = this.strideDistance;
		super.tickMovement();
		EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (!this.world.isClient) {
			entityAttributeInstance.setBaseValue((double)this.abilities.getWalkSpeed());
		}

		this.flyingSpeed = this.field_4006;
		if (this.isSprinting()) {
			this.flyingSpeed = (float)((double)this.flyingSpeed + (double)this.field_4006 * 0.3);
		}

		this.setMovementSpeed((float)entityAttributeInstance.getValue());
		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		float g = (float)(Math.atan(-this.velocityY * 0.2F) * 15.0);
		if (f > 0.1F) {
			f = 0.1F;
		}

		if (!this.onGround || this.getHealth() <= 0.0F) {
			f = 0.0F;
		}

		if (this.onGround || this.getHealth() <= 0.0F) {
			g = 0.0F;
		}

		this.strideDistance = this.strideDistance + (f - this.strideDistance) * 0.4F;
		this.field_6753 = this.field_6753 + (g - this.field_6753) * 0.8F;
		if (this.getHealth() > 0.0F && !this.isSpectator()) {
			Box box = null;
			if (this.vehicle != null && !this.vehicle.removed) {
				box = this.getBoundingBox().union(this.vehicle.getBoundingBox()).expand(1.0, 0.0, 1.0);
			} else {
				box = this.getBoundingBox().expand(1.0, 0.5, 1.0);
			}

			List<Entity> list = this.world.getEntitiesIn(this, box);

			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (!entity.removed) {
					this.collideWithEntity(entity);
				}
			}
		}
	}

	private void collideWithEntity(Entity entity) {
		entity.onPlayerCollision(this);
	}

	public int getScore() {
		return this.dataTracker.getInt(18);
	}

	public void setScore(int score) {
		this.dataTracker.setProperty(18, score);
	}

	public void addScore(int score) {
		int i = this.getScore();
		this.dataTracker.setProperty(18, i + score);
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		this.setBounds(0.2F, 0.2F);
		this.updatePosition(this.x, this.y, this.z);
		this.velocityY = 0.1F;
		if (this.getTranslationKey().equals("Notch")) {
			this.dropStack(new ItemStack(Items.APPLE, 1), true, false);
		}

		if (!this.world.getGameRules().getBoolean("keepInventory")) {
			this.inventory.dropAll();
		}

		if (source != null) {
			this.velocityX = (double)(-MathHelper.cos((this.knockbackVelocity + this.yaw) * (float) Math.PI / 180.0F) * 0.1F);
			this.velocityZ = (double)(-MathHelper.sin((this.knockbackVelocity + this.yaw) * (float) Math.PI / 180.0F) * 0.1F);
		} else {
			this.velocityX = this.velocityZ = 0.0;
		}

		this.incrementStat(Stats.DEATHS);
		this.method_11238(Stats.TIME_SINCE_DEATH);
	}

	@Override
	protected String getHurtSound() {
		return "game.player.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "game.player.die";
	}

	@Override
	public void updateKilledAdvancementCriterion(Entity entity, int score) {
		this.addScore(score);
		Collection<ScoreboardObjective> collection = this.getScoreboard().getObjective(ScoreboardCriterion.TOTAL_KILLED);
		if (entity instanceof PlayerEntity) {
			this.incrementStat(Stats.PLAYERS_KILLED);
			collection.addAll(this.getScoreboard().getObjective(ScoreboardCriterion.PLAYERS_KILLED));
			collection.addAll(this.method_11247(entity));
		} else {
			this.incrementStat(Stats.MOB_KILLS);
		}

		for (ScoreboardObjective scoreboardObjective : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = this.getScoreboard().getPlayerScore(this.getTranslationKey(), scoreboardObjective);
			scoreboardPlayerScore.method_4865();
		}
	}

	private Collection<ScoreboardObjective> method_11247(Entity entity) {
		Team team = this.getScoreboard().getPlayerTeam(this.getTranslationKey());
		if (team != null) {
			int i = team.getFormatting().getColorIndex();
			if (i >= 0 && i < ScoreboardCriterion.KILLED_BY_TEAM.length) {
				for (ScoreboardObjective scoreboardObjective : this.getScoreboard().getObjective(ScoreboardCriterion.KILLED_BY_TEAM[i])) {
					ScoreboardPlayerScore scoreboardPlayerScore = this.getScoreboard().getPlayerScore(entity.getTranslationKey(), scoreboardObjective);
					scoreboardPlayerScore.method_4865();
				}
			}
		}

		Team team2 = this.getScoreboard().getPlayerTeam(entity.getTranslationKey());
		if (team2 != null) {
			int j = team2.getFormatting().getColorIndex();
			if (j >= 0 && j < ScoreboardCriterion.TEAM_KILLS.length) {
				return this.getScoreboard().getObjective(ScoreboardCriterion.TEAM_KILLS[j]);
			}
		}

		return Lists.newArrayList();
	}

	public ItemEntity dropSelectedItem(boolean dropAll) {
		return this.dropStack(
			this.inventory.takeInvStack(this.inventory.selectedSlot, dropAll && this.inventory.getMainHandStack() != null ? this.inventory.getMainHandStack().count : 1),
			false,
			true
		);
	}

	public ItemEntity dropItem(ItemStack stack, boolean bl) {
		return this.dropStack(stack, false, false);
	}

	public ItemEntity dropStack(ItemStack stack, boolean bl, boolean incrementStats) {
		if (stack == null) {
			return null;
		} else if (stack.count == 0) {
			return null;
		} else {
			double d = this.y - 0.3F + (double)this.getEyeHeight();
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, d, this.z, stack);
			itemEntity.setPickupDelay(40);
			if (incrementStats) {
				itemEntity.setThrower(this.getTranslationKey());
			}

			if (bl) {
				float f = this.random.nextFloat() * 0.5F;
				float g = this.random.nextFloat() * (float) Math.PI * 2.0F;
				itemEntity.velocityX = (double)(-MathHelper.sin(g) * f);
				itemEntity.velocityZ = (double)(MathHelper.cos(g) * f);
				itemEntity.velocityY = 0.2F;
			} else {
				float h = 0.3F;
				itemEntity.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * h);
				itemEntity.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * h);
				itemEntity.velocityY = (double)(-MathHelper.sin(this.pitch / 180.0F * (float) Math.PI) * h + 0.1F);
				float i = this.random.nextFloat() * (float) Math.PI * 2.0F;
				h = 0.02F * this.random.nextFloat();
				itemEntity.velocityX = itemEntity.velocityX + Math.cos((double)i) * (double)h;
				itemEntity.velocityY = itemEntity.velocityY + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
				itemEntity.velocityZ = itemEntity.velocityZ + Math.sin((double)i) * (double)h;
			}

			this.spawnItemEntity(itemEntity);
			if (incrementStats) {
				this.incrementStat(Stats.DROPS);
			}

			return itemEntity;
		}
	}

	protected void spawnItemEntity(ItemEntity entity) {
		this.world.spawnEntity(entity);
	}

	public float getMiningSpeed(Block block) {
		float f = this.inventory.getMiningSpeed(block);
		if (f > 1.0F) {
			int i = EnchantmentHelper.getEfficiency(this);
			ItemStack itemStack = this.inventory.getMainHandStack();
			if (i > 0 && itemStack != null) {
				f += (float)(i * i + 1);
			}
		}

		if (this.hasStatusEffect(StatusEffect.HASTE)) {
			f *= 1.0F + (float)(this.getEffectInstance(StatusEffect.HASTE).getAmplifier() + 1) * 0.2F;
		}

		if (this.hasStatusEffect(StatusEffect.MINING_FATIGUE)) {
			float g = 1.0F;
			switch (this.getEffectInstance(StatusEffect.MINING_FATIGUE).getAmplifier()) {
				case 0:
					g = 0.3F;
					break;
				case 1:
					g = 0.09F;
					break;
				case 2:
					g = 0.0027F;
					break;
				case 3:
				default:
					g = 8.1E-4F;
			}

			f *= g;
		}

		if (this.isSubmergedIn(Material.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
			f /= 5.0F;
		}

		if (!this.onGround) {
			f /= 5.0F;
		}

		return f;
	}

	public boolean isUsingEffectiveTool(Block block) {
		return this.inventory.canToolBreak(block);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.playerUuid = getUuidFromProfile(this.gameProfile);
		NbtList nbtList = nbt.getList("Inventory", 10);
		this.inventory.deserialize(nbtList);
		this.inventory.selectedSlot = nbt.getInt("SelectedItemSlot");
		this.inBed = nbt.getBoolean("Sleeping");
		this.sleepTimer = nbt.getShort("SleepTimer");
		this.experienceProgress = nbt.getFloat("XpP");
		this.experienceLevel = nbt.getInt("XpLevel");
		this.totalExperience = nbt.getInt("XpTotal");
		this.enchantmentTableSeed = nbt.getInt("XpSeed");
		if (this.enchantmentTableSeed == 0) {
			this.enchantmentTableSeed = this.random.nextInt();
		}

		this.setScore(nbt.getInt("Score"));
		if (this.inBed) {
			this.pos = new BlockPos(this);
			this.awaken(true, true, false);
		}

		if (nbt.contains("SpawnX", 99) && nbt.contains("SpawnY", 99) && nbt.contains("SpawnZ", 99)) {
			this.spawnPos = new BlockPos(nbt.getInt("SpawnX"), nbt.getInt("SpawnY"), nbt.getInt("SpawnZ"));
			this.spawnForced = nbt.getBoolean("SpawnForced");
		}

		this.hungerManager.deserialize(nbt);
		this.abilities.deserialize(nbt);
		if (nbt.contains("EnderItems", 9)) {
			NbtList nbtList2 = nbt.getList("EnderItems", 10);
			this.enderChest.readNbtList(nbtList2);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("Inventory", this.inventory.serialize(new NbtList()));
		nbt.putInt("SelectedItemSlot", this.inventory.selectedSlot);
		nbt.putBoolean("Sleeping", this.inBed);
		nbt.putShort("SleepTimer", (short)this.sleepTimer);
		nbt.putFloat("XpP", this.experienceProgress);
		nbt.putInt("XpLevel", this.experienceLevel);
		nbt.putInt("XpTotal", this.totalExperience);
		nbt.putInt("XpSeed", this.enchantmentTableSeed);
		nbt.putInt("Score", this.getScore());
		if (this.spawnPos != null) {
			nbt.putInt("SpawnX", this.spawnPos.getX());
			nbt.putInt("SpawnY", this.spawnPos.getY());
			nbt.putInt("SpawnZ", this.spawnPos.getZ());
			nbt.putBoolean("SpawnForced", this.spawnForced);
		}

		this.hungerManager.serialize(nbt);
		this.abilities.serialize(nbt);
		nbt.put("EnderItems", this.enderChest.toNbtList());
		ItemStack itemStack = this.inventory.getMainHandStack();
		if (itemStack != null && itemStack.getItem() != null) {
			nbt.put("SelectedItem", itemStack.toNbt(new NbtCompound()));
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (this.abilities.invulnerable && !source.isOutOfWorld()) {
			return false;
		} else {
			this.despawnCounter = 0;
			if (this.getHealth() <= 0.0F) {
				return false;
			} else {
				if (this.isSleeping() && !this.world.isClient) {
					this.awaken(true, true, false);
				}

				if (source.isScaledWithDifficulty()) {
					if (this.world.getGlobalDifficulty() == Difficulty.PEACEFUL) {
						amount = 0.0F;
					}

					if (this.world.getGlobalDifficulty() == Difficulty.EASY) {
						amount = amount / 2.0F + 1.0F;
					}

					if (this.world.getGlobalDifficulty() == Difficulty.HARD) {
						amount = amount * 3.0F / 2.0F;
					}
				}

				if (amount == 0.0F) {
					return false;
				} else {
					Entity entity = source.getAttacker();
					if (entity instanceof AbstractArrowEntity && ((AbstractArrowEntity)entity).owner != null) {
						entity = ((AbstractArrowEntity)entity).owner;
					}

					return super.damage(source, amount);
				}
			}
		}
	}

	public boolean shouldDamagePlayer(PlayerEntity player) {
		AbstractTeam abstractTeam = this.getScoreboardTeam();
		AbstractTeam abstractTeam2 = player.getScoreboardTeam();
		if (abstractTeam == null) {
			return true;
		} else {
			return !abstractTeam.isEqual(abstractTeam2) ? true : abstractTeam.isFriendlyFireAllowed();
		}
	}

	@Override
	protected void damageArmor(float value) {
		this.inventory.damageArmor(value);
	}

	@Override
	public int getArmorProtectionValue() {
		return this.inventory.getArmorProtectionValue();
	}

	public float method_4575() {
		int i = 0;

		for (ItemStack itemStack : this.inventory.armor) {
			if (itemStack != null) {
				i++;
			}
		}

		return (float)i / (float)this.inventory.armor.length;
	}

	@Override
	protected void applyDamage(DamageSource source, float damage) {
		if (!this.isInvulnerableTo(source)) {
			if (!source.bypassesArmor() && this.method_2611() && damage > 0.0F) {
				damage = (1.0F + damage) * 0.5F;
			}

			damage = this.applyArmorDamage(source, damage);
			damage = this.applyEnchantmentsToDamage(source, damage);
			float var7 = Math.max(damage - this.getAbsorption(), 0.0F);
			this.setAbsorption(this.getAbsorption() - (damage - var7));
			if (var7 != 0.0F) {
				this.addExhaustion(source.getExhaustion());
				float g = this.getHealth();
				this.setHealth(this.getHealth() - var7);
				this.getDamageTracker().onDamage(source, g, var7);
				if (var7 < 3.4028235E37F) {
					this.incrementStat(Stats.DAMAGE_TAKEN, Math.round(var7 * 10.0F));
				}
			}
		}
	}

	public void openEditSignScreen(SignBlockEntity sign) {
	}

	public void openCommandBlockScreen(CommandBlockExecutor executor) {
	}

	public void openTradingScreen(Trader trader) {
	}

	public void openInventory(Inventory inventory) {
	}

	public void openHorseInventory(HorseBaseEntity horse, Inventory inventory) {
	}

	public void openHandledScreen(NamedScreenHandlerFactory screenHandlerFactory) {
	}

	public void openBookEditScreen(ItemStack stack) {
	}

	public boolean method_3215(Entity entity) {
		if (this.isSpectator()) {
			if (entity instanceof Inventory) {
				this.openInventory((Inventory)entity);
			}

			return false;
		} else {
			ItemStack itemStack = this.getMainHandStack();
			ItemStack itemStack2 = itemStack != null ? itemStack.copy() : null;
			if (!entity.openInventory(this)) {
				if (itemStack != null && entity instanceof LivingEntity) {
					if (this.abilities.creativeMode) {
						itemStack = itemStack2;
					}

					if (itemStack.canUseOnEntity(this, (LivingEntity)entity)) {
						if (itemStack.count <= 0 && !this.abilities.creativeMode) {
							this.removeSelectedSlotItem();
						}

						return true;
					}
				}

				return false;
			} else {
				if (itemStack != null && itemStack == this.getMainHandStack()) {
					if (itemStack.count <= 0 && !this.abilities.creativeMode) {
						this.removeSelectedSlotItem();
					} else if (itemStack.count < itemStack2.count && this.abilities.creativeMode) {
						itemStack.count = itemStack2.count;
					}
				}

				return true;
			}
		}
	}

	public ItemStack getMainHandStack() {
		return this.inventory.getMainHandStack();
	}

	public void removeSelectedSlotItem() {
		this.inventory.setInvStack(this.inventory.selectedSlot, null);
	}

	@Override
	public double getHeightOffset() {
		return -0.35;
	}

	public void attack(Entity entity) {
		if (entity.isAttackable()) {
			if (!entity.handleAttack(this)) {
				float f = (float)this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
				int i = 0;
				float g = 0.0F;
				if (entity instanceof LivingEntity) {
					g = EnchantmentHelper.getAttackDamage(this.getStackInHand(), ((LivingEntity)entity).getGroup());
				} else {
					g = EnchantmentHelper.getAttackDamage(this.getStackInHand(), EntityGroup.DEFAULT);
				}

				i += EnchantmentHelper.getKnockback(this);
				if (this.isSprinting()) {
					i++;
				}

				if (f > 0.0F || g > 0.0F) {
					boolean bl = this.fallDistance > 0.0F
						&& !this.onGround
						&& !this.isClimbing()
						&& !this.isTouchingWater()
						&& !this.hasStatusEffect(StatusEffect.BLINDNESS)
						&& this.vehicle == null
						&& entity instanceof LivingEntity;
					if (bl && f > 0.0F) {
						f *= 1.5F;
					}

					f += g;
					boolean bl2 = false;
					int j = EnchantmentHelper.getFireAspect(this);
					if (entity instanceof LivingEntity && j > 0 && !entity.isOnFire()) {
						bl2 = true;
						entity.setOnFireFor(1);
					}

					double d = entity.velocityX;
					double e = entity.velocityY;
					double h = entity.velocityZ;
					boolean bl3 = entity.damage(DamageSource.player(this), f);
					if (bl3) {
						if (i > 0) {
							entity.addVelocity(
								(double)(-MathHelper.sin(this.yaw * (float) Math.PI / 180.0F) * (float)i * 0.5F),
								0.1,
								(double)(MathHelper.cos(this.yaw * (float) Math.PI / 180.0F) * (float)i * 0.5F)
							);
							this.velocityX *= 0.6;
							this.velocityZ *= 0.6;
							this.setSprinting(false);
						}

						if (entity instanceof ServerPlayerEntity && entity.velocityModified) {
							((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
							entity.velocityModified = false;
							entity.velocityX = d;
							entity.velocityY = e;
							entity.velocityZ = h;
						}

						if (bl) {
							this.addCritParticles(entity);
						}

						if (g > 0.0F) {
							this.addEnchantedHitParticles(entity);
						}

						if (f >= 18.0F) {
							this.incrementStat(AchievementsAndCriterions.OVERKILL);
						}

						this.method_6150(entity);
						if (entity instanceof LivingEntity) {
							EnchantmentHelper.onUserDamaged((LivingEntity)entity, this);
						}

						EnchantmentHelper.onTargetDamaged(this, entity);
						ItemStack itemStack = this.getMainHandStack();
						Entity entity2 = entity;
						if (entity instanceof EnderDragonPart) {
							MultipartEntityProvider multipartEntityProvider = ((EnderDragonPart)entity).owner;
							if (multipartEntityProvider instanceof LivingEntity) {
								entity2 = (LivingEntity)multipartEntityProvider;
							}
						}

						if (itemStack != null && entity2 instanceof LivingEntity) {
							itemStack.onEntityHit((LivingEntity)entity2, this);
							if (itemStack.count <= 0) {
								this.removeSelectedSlotItem();
							}
						}

						if (entity instanceof LivingEntity) {
							this.incrementStat(Stats.DAMAGE_DEALT, Math.round(f * 10.0F));
							if (j > 0) {
								entity.setOnFireFor(j * 4);
							}
						}

						this.addExhaustion(0.3F);
					} else if (bl2) {
						entity.extinguish();
					}
				}
			}
		}
	}

	public void addCritParticles(Entity target) {
	}

	public void addEnchantedHitParticles(Entity target) {
	}

	public void requestRespawn() {
	}

	@Override
	public void remove() {
		super.remove();
		this.playerScreenHandler.close(this);
		if (this.openScreenHandler != null) {
			this.openScreenHandler.close(this);
		}
	}

	@Override
	public boolean isInsideWall() {
		return !this.inBed && super.isInsideWall();
	}

	public boolean isMainPlayer() {
		return false;
	}

	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public PlayerEntity.SleepStatus attemptSleep(BlockPos pos) {
		if (!this.world.isClient) {
			if (this.isSleeping() || !this.isAlive()) {
				return PlayerEntity.SleepStatus.OTHER;
			}

			if (!this.world.dimension.canPlayersSleep()) {
				return PlayerEntity.SleepStatus.NOT_POSSIBLE_HERE;
			}

			if (this.world.isDay()) {
				return PlayerEntity.SleepStatus.NOT_POSSIBLE_NOW;
			}

			if (Math.abs(this.x - (double)pos.getX()) > 3.0 || Math.abs(this.y - (double)pos.getY()) > 2.0 || Math.abs(this.z - (double)pos.getZ()) > 3.0) {
				return PlayerEntity.SleepStatus.TOO_FAR_AWAY;
			}

			double d = 8.0;
			double e = 5.0;
			List<HostileEntity> list = this.world
				.getEntitiesInBox(
					HostileEntity.class,
					new Box((double)pos.getX() - d, (double)pos.getY() - e, (double)pos.getZ() - d, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + d)
				);
			if (!list.isEmpty()) {
				return PlayerEntity.SleepStatus.NOT_SAFE;
			}
		}

		if (this.hasVehicle()) {
			this.startRiding(null);
		}

		this.setBounds(0.2F, 0.2F);
		if (this.world.blockExists(pos)) {
			Direction direction = this.world.getBlockState(pos).get(FacingBlock.FACING);
			float f = 0.5F;
			float g = 0.5F;
			switch (direction) {
				case SOUTH:
					g = 0.9F;
					break;
				case NORTH:
					g = 0.1F;
					break;
				case WEST:
					f = 0.1F;
					break;
				case EAST:
					f = 0.9F;
			}

			this.method_11237(direction);
			this.updatePosition((double)((float)pos.getX() + f), (double)((float)pos.getY() + 0.6875F), (double)((float)pos.getZ() + g));
		} else {
			this.updatePosition((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.6875F), (double)((float)pos.getZ() + 0.5F));
		}

		this.inBed = true;
		this.sleepTimer = 0;
		this.pos = pos;
		this.velocityX = this.velocityZ = this.velocityY = 0.0;
		if (!this.world.isClient) {
			this.world.updateSleepingStatus();
		}

		return PlayerEntity.SleepStatus.OK;
	}

	private void method_11237(Direction direction) {
		this.field_3993 = 0.0F;
		this.field_3994 = 0.0F;
		switch (direction) {
			case SOUTH:
				this.field_3994 = -1.8F;
				break;
			case NORTH:
				this.field_3994 = 1.8F;
				break;
			case WEST:
				this.field_3993 = 1.8F;
				break;
			case EAST:
				this.field_3993 = -1.8F;
		}
	}

	public void awaken(boolean bl, boolean bl2, boolean setSpawn) {
		this.setBounds(0.6F, 1.8F);
		BlockState blockState = this.world.getBlockState(this.pos);
		if (this.pos != null && blockState.getBlock() == Blocks.BED) {
			this.world.setBlockState(this.pos, blockState.with(BedBlock.OCCUPIED, false), 4);
			BlockPos blockPos = BedBlock.findSpawnablePos(this.world, this.pos, 0);
			if (blockPos == null) {
				blockPos = this.pos.up();
			}

			this.updatePosition((double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.1F), (double)((float)blockPos.getZ() + 0.5F));
		}

		this.inBed = false;
		if (!this.world.isClient && bl2) {
			this.world.updateSleepingStatus();
		}

		this.sleepTimer = bl ? 0 : 100;
		if (setSpawn) {
			this.setPlayerSpawn(this.pos, false);
		}
	}

	private boolean method_3213() {
		return this.world.getBlockState(this.pos).getBlock() == Blocks.BED;
	}

	public static BlockPos findRespawnPosition(World world, BlockPos pos, boolean bl) {
		Block block = world.getBlockState(pos).getBlock();
		if (block != Blocks.BED) {
			if (!bl) {
				return null;
			} else {
				boolean bl2 = block.canMobSpawnInside();
				boolean bl3 = world.getBlockState(pos.up()).getBlock().canMobSpawnInside();
				return bl2 && bl3 ? pos : null;
			}
		} else {
			return BedBlock.findSpawnablePos(world, pos, 0);
		}
	}

	public float method_3183() {
		if (this.pos != null) {
			Direction direction = this.world.getBlockState(this.pos).get(FacingBlock.FACING);
			switch (direction) {
				case SOUTH:
					return 90.0F;
				case NORTH:
					return 270.0F;
				case WEST:
					return 0.0F;
				case EAST:
					return 180.0F;
			}
		}

		return 0.0F;
	}

	@Override
	public boolean isSleeping() {
		return this.inBed;
	}

	public boolean isSleepingLongEnough() {
		return this.inBed && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	public void addMessage(Text text) {
	}

	public BlockPos getSpawnPosition() {
		return this.spawnPos;
	}

	public boolean isSpawnForced() {
		return this.spawnForced;
	}

	public void setPlayerSpawn(BlockPos pos, boolean spawnForced) {
		if (pos != null) {
			this.spawnPos = pos;
			this.spawnForced = spawnForced;
		} else {
			this.spawnPos = null;
			this.spawnForced = false;
		}
	}

	public void incrementStat(Stat stat) {
		this.incrementStat(stat, 1);
	}

	public void incrementStat(Stat stat, int amount) {
	}

	public void method_11238(Stat stat) {
	}

	@Override
	public void jump() {
		super.jump();
		this.incrementStat(Stats.JUMPS);
		if (this.isSprinting()) {
			this.addExhaustion(0.8F);
		} else {
			this.addExhaustion(0.2F);
		}
	}

	@Override
	public void travel(float f, float g) {
		double d = this.x;
		double e = this.y;
		double h = this.z;
		if (this.abilities.flying && this.vehicle == null) {
			double i = this.velocityY;
			float j = this.flyingSpeed;
			this.flyingSpeed = this.abilities.getFlySpeed() * (float)(this.isSprinting() ? 2 : 1);
			super.travel(f, g);
			this.velocityY = i * 0.6;
			this.flyingSpeed = j;
		} else {
			super.travel(f, g);
		}

		this.method_3209(this.x - d, this.y - e, this.z - h);
	}

	@Override
	public float getMovementSpeed() {
		return (float)this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue();
	}

	public void method_3209(double d, double e, double f) {
		if (this.vehicle == null) {
			if (this.isSubmergedIn(Material.WATER)) {
				int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
				if (i > 0) {
					this.incrementStat(Stats.CM_DIVED, i);
					this.addExhaustion(0.015F * (float)i * 0.01F);
				}
			} else if (this.isTouchingWater()) {
				int j = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (j > 0) {
					this.incrementStat(Stats.CM_SWUM, j);
					this.addExhaustion(0.015F * (float)j * 0.01F);
				}
			} else if (this.isClimbing()) {
				if (e > 0.0) {
					this.incrementStat(Stats.CM_CLIMB, (int)Math.round(e * 100.0));
				}
			} else if (this.onGround) {
				int k = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (k > 0) {
					this.incrementStat(Stats.CM_WALKED, k);
					if (this.isSprinting()) {
						this.incrementStat(Stats.CM_SPRINTED, k);
						this.addExhaustion(0.099999994F * (float)k * 0.01F);
					} else {
						if (this.isSneaking()) {
							this.incrementStat(Stats.CM_SNEAKED, k);
						}

						this.addExhaustion(0.01F * (float)k * 0.01F);
					}
				}
			} else {
				int l = Math.round(MathHelper.sqrt(d * d + f * f) * 100.0F);
				if (l > 25) {
					this.incrementStat(Stats.CM_FLOWN, l);
				}
			}
		}
	}

	private void method_3212(double d, double e, double f) {
		if (this.vehicle != null) {
			int i = Math.round(MathHelper.sqrt(d * d + e * e + f * f) * 100.0F);
			if (i > 0) {
				if (this.vehicle instanceof AbstractMinecartEntity) {
					this.incrementStat(Stats.CM_MINECART, i);
					if (this.field_4011 == null) {
						this.field_4011 = new BlockPos(this);
					} else if (this.field_4011.squaredDistanceTo((double)MathHelper.floor(this.x), (double)MathHelper.floor(this.y), (double)MathHelper.floor(this.z))
						>= 1000000.0) {
						this.incrementStat(AchievementsAndCriterions.ON_A_RAIL);
					}
				} else if (this.vehicle instanceof BoatEntity) {
					this.incrementStat(Stats.CM_SAILED, i);
				} else if (this.vehicle instanceof PigEntity) {
					this.incrementStat(Stats.CM_PIG, i);
				} else if (this.vehicle instanceof HorseBaseEntity) {
					this.incrementStat(Stats.CM_HORSE, i);
				}
			}
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		if (!this.abilities.allowFlying) {
			if (fallDistance >= 2.0F) {
				this.incrementStat(Stats.CM_FALLEN, (int)Math.round((double)fallDistance * 100.0));
			}

			super.handleFallDamage(fallDistance, damageMultiplier);
		}
	}

	@Override
	protected void onSwimmingStart() {
		if (!this.isSpectator()) {
			super.onSwimmingStart();
		}
	}

	@Override
	protected String getFallSound(int distance) {
		return distance > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
	}

	@Override
	public void onKilledOther(LivingEntity other) {
		if (other instanceof Monster) {
			this.incrementStat(AchievementsAndCriterions.KILL_ENEMY);
		}

		EntityType.SpawnEggData spawnEggData = (EntityType.SpawnEggData)EntityType.SPAWN_EGGS.get(EntityType.getIdByEntity(other));
		if (spawnEggData != null) {
			this.incrementStat(spawnEggData.killEntityStat);
		}
	}

	@Override
	public void setInLava() {
		if (!this.abilities.flying) {
			super.setInLava();
		}
	}

	@Override
	public ItemStack getArmorSlot(int i) {
		return this.inventory.getArmor(i);
	}

	public void addExperience(int experience) {
		this.addScore(experience);
		int i = Integer.MAX_VALUE - this.totalExperience;
		if (experience > i) {
			experience = i;
		}

		this.experienceProgress = this.experienceProgress + (float)experience / (float)this.getNextLevelExperience();

		for (this.totalExperience += experience;
			this.experienceProgress >= 1.0F;
			this.experienceProgress = this.experienceProgress / (float)this.getNextLevelExperience()
		) {
			this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getNextLevelExperience();
			this.incrementXp(1);
		}
	}

	public int getEnchantmentTableSeed() {
		return this.enchantmentTableSeed;
	}

	public void decrementXp(int xp) {
		this.experienceLevel -= xp;
		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experienceProgress = 0.0F;
			this.totalExperience = 0;
		}

		this.enchantmentTableSeed = this.random.nextInt();
	}

	public void incrementXp(int xp) {
		this.experienceLevel += xp;
		if (this.experienceLevel < 0) {
			this.experienceLevel = 0;
			this.experienceProgress = 0.0F;
			this.totalExperience = 0;
		}

		if (xp > 0 && this.experienceLevel % 5 == 0 && (float)this.lastPlayedLevelUpSoundTime < (float)this.ticksAlive - 100.0F) {
			float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
			this.world.playSound((Entity)this, "random.levelup", f * 0.75F, 1.0F);
			this.lastPlayedLevelUpSoundTime = this.ticksAlive;
		}
	}

	public int getNextLevelExperience() {
		if (this.experienceLevel >= 30) {
			return 112 + (this.experienceLevel - 30) * 9;
		} else {
			return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
		}
	}

	public void addExhaustion(float exhaustion) {
		if (!this.abilities.invulnerable) {
			if (!this.world.isClient) {
				this.hungerManager.addExhaustion(exhaustion);
			}
		}
	}

	public HungerManager getHungerManager() {
		return this.hungerManager;
	}

	public boolean canConsume(boolean ignoreHunger) {
		return (ignoreHunger || this.hungerManager.isNotFull()) && !this.abilities.invulnerable;
	}

	public boolean canFoodHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
	}

	public void setUseItem(ItemStack item, int i) {
		if (item != this.useItem) {
			this.useItem = item;
			this.itemUseTicks = i;
			if (!this.world.isClient) {
				this.setSwimming(true);
			}
		}
	}

	public boolean canModifyWorld() {
		return this.abilities.allowModifyWorld;
	}

	public boolean canModify(BlockPos pos, Direction direction, ItemStack stack) {
		if (this.abilities.allowModifyWorld) {
			return true;
		} else if (stack == null) {
			return false;
		} else {
			BlockPos blockPos = pos.offset(direction.getOpposite());
			Block block = this.world.getBlockState(blockPos).getBlock();
			return stack.canPlaceOn(block) || stack.hasSubTypes();
		}
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		if (this.world.getGameRules().getBoolean("keepInventory")) {
			return 0;
		} else {
			int i = this.experienceLevel * 7;
			return i > 100 ? 100 : i;
		}
	}

	@Override
	protected boolean shouldAlwaysDropXp() {
		return true;
	}

	@Override
	public boolean shouldRenderName() {
		return true;
	}

	public void copyFrom(PlayerEntity player, boolean bl) {
		if (bl) {
			this.inventory.copy(player.inventory);
			this.setHealth(player.getHealth());
			this.hungerManager = player.hungerManager;
			this.experienceLevel = player.experienceLevel;
			this.totalExperience = player.totalExperience;
			this.experienceProgress = player.experienceProgress;
			this.setScore(player.getScore());
			this.lastPortalBlockPos = player.lastPortalBlockPos;
			this.lastPortalVec3d = player.lastPortalVec3d;
			this.teleportDirection = player.teleportDirection;
		} else if (this.world.getGameRules().getBoolean("keepInventory")) {
			this.inventory.copy(player.inventory);
			this.experienceLevel = player.experienceLevel;
			this.totalExperience = player.totalExperience;
			this.experienceProgress = player.experienceProgress;
			this.setScore(player.getScore());
		}

		this.enchantmentTableSeed = player.enchantmentTableSeed;
		this.enderChest = player.enderChest;
		this.getDataTracker().setProperty(10, player.getDataTracker().getByte(10));
	}

	@Override
	protected boolean canClimb() {
		return !this.abilities.flying;
	}

	public void sendAbilitiesUpdate() {
	}

	public void setGameMode(LevelInfo.GameMode gameMode) {
	}

	@Override
	public String getTranslationKey() {
		return this.gameProfile.getName();
	}

	public EnderChestInventory getEnderChestInventory() {
		return this.enderChest;
	}

	@Override
	public ItemStack getMainSlot(int slot) {
		return slot == 0 ? this.inventory.getMainHandStack() : this.inventory.armor[slot - 1];
	}

	@Override
	public ItemStack getStackInHand() {
		return this.inventory.getMainHandStack();
	}

	@Override
	public void setArmorSlot(int armorSlot, ItemStack item) {
		this.inventory.armor[armorSlot] = item;
	}

	@Override
	public boolean isInvisibleTo(PlayerEntity player) {
		if (!this.isInvisible()) {
			return false;
		} else if (player.isSpectator()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			return abstractTeam == null || player == null || player.getScoreboardTeam() != abstractTeam || !abstractTeam.shouldShowFriendlyInvisibles();
		}
	}

	public abstract boolean isSpectator();

	@Override
	public ItemStack[] getArmorStacks() {
		return this.inventory.armor;
	}

	@Override
	public boolean canFly() {
		return !this.abilities.flying;
	}

	public Scoreboard getScoreboard() {
		return this.world.getScoreboard();
	}

	@Override
	public AbstractTeam getScoreboardTeam() {
		return this.getScoreboard().getPlayerTeam(this.getTranslationKey());
	}

	@Override
	public Text getName() {
		Text text = new LiteralText(Team.decorateName(this.getScoreboardTeam(), this.getTranslationKey()));
		text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getTranslationKey() + " "));
		text.getStyle().setHoverEvent(this.getHoverEvent());
		text.getStyle().setInsertion(this.getTranslationKey());
		return text;
	}

	@Override
	public float getEyeHeight() {
		float f = 1.62F;
		if (this.isSleeping()) {
			f = 0.2F;
		}

		if (this.isSneaking()) {
			f -= 0.08F;
		}

		return f;
	}

	@Override
	public void setAbsorption(float absorption) {
		if (absorption < 0.0F) {
			absorption = 0.0F;
		}

		this.getDataTracker().setProperty(17, absorption);
	}

	@Override
	public float getAbsorption() {
		return this.getDataTracker().getFloat(17);
	}

	public static UUID getUuidFromProfile(GameProfile profile) {
		UUID uUID = profile.getId();
		if (uUID == null) {
			uUID = getOfflinePlayerUuid(profile.getName());
		}

		return uUID;
	}

	public static UUID getOfflinePlayerUuid(String nickname) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(Charsets.UTF_8));
	}

	public boolean isScreenLocked(ScreenHandlerLock screenLock) {
		if (screenLock.hasLock()) {
			return true;
		} else {
			ItemStack itemStack = this.getMainHandStack();
			return itemStack != null && itemStack.hasCustomName() ? itemStack.getCustomName().equals(screenLock.getKey()) : false;
		}
	}

	public boolean isPartVisible(PlayerModelPart modelPart) {
		return (this.getDataTracker().getByte(10) & modelPart.getBitFlag()) == modelPart.getBitFlag();
	}

	@Override
	public boolean sendCommandFeedback() {
		return MinecraftServer.getServer().worlds[0].getGameRules().getBoolean("sendCommandFeedback");
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (slot >= 0 && slot < this.inventory.main.length) {
			this.inventory.setInvStack(slot, item);
			return true;
		} else {
			int i = slot - 100;
			if (i >= 0 && i < this.inventory.armor.length) {
				int j = i + 1;
				if (item != null && item.getItem() != null) {
					if (item.getItem() instanceof ArmorItem) {
						if (MobEntity.getEquipableSlot(item) != j) {
							return false;
						}
					} else if (j != 4 || item.getItem() != Items.SKULL && !(item.getItem() instanceof BlockItem)) {
						return false;
					}
				}

				this.inventory.setInvStack(i + this.inventory.main.length, item);
				return true;
			} else {
				int k = slot - 200;
				if (k >= 0 && k < this.enderChest.getInvSize()) {
					this.enderChest.setInvStack(k, item);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public boolean getReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	public void setReducedDebugInfo(boolean reducedDebugInfo) {
		this.reducedDebugInfo = reducedDebugInfo;
	}

	public static enum ChatVisibilityType {
		FULL(0, "options.chat.visibility.full"),
		SYSTEM(1, "options.chat.visibility.system"),
		HIDDEN(2, "options.chat.visibility.hidden");

		private static final PlayerEntity.ChatVisibilityType[] TYPES = new PlayerEntity.ChatVisibilityType[values().length];
		private final int id;
		private final String name;

		private ChatVisibilityType(int j, String string2) {
			this.id = j;
			this.name = string2;
		}

		public int getId() {
			return this.id;
		}

		public static PlayerEntity.ChatVisibilityType getById(int id) {
			return TYPES[id % TYPES.length];
		}

		public String getName() {
			return this.name;
		}

		static {
			for (PlayerEntity.ChatVisibilityType chatVisibilityType : values()) {
				TYPES[chatVisibilityType.id] = chatVisibilityType;
			}
		}
	}

	public static enum SleepStatus {
		OK,
		NOT_POSSIBLE_HERE,
		NOT_POSSIBLE_NOW,
		TOO_FAR_AWAY,
		OTHER,
		NOT_SAFE;
	}
}
