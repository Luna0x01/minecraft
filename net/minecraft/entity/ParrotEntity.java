package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3159;
import net.minecraft.class_3369;
import net.minecraft.class_3379;
import net.minecraft.class_3384;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.class_3373;
import net.minecraft.entity.ai.goal.class_3374;
import net.minecraft.entity.ai.goal.class_3375;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.class_3383;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ParrotEntity extends class_3159 implements class_3384 {
	private static final TrackedData<Integer> field_15562 = DataTracker.registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Predicate<MobEntity> field_16914 = new Predicate<MobEntity>() {
		public boolean test(@Nullable MobEntity mobEntity) {
			return mobEntity != null && ParrotEntity.field_16915.containsKey(mobEntity.method_15557());
		}
	};
	private static final Item field_15564 = Items.COOKIE;
	private static final Set<Item> field_15565 = Sets.newHashSet(new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEED});
	private static final Map<EntityType<?>, Sound> field_16915 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put(EntityType.BLAZE, Sounds.ENTITY_PARROT_IMITATE_BLAZE);
		hashMap.put(EntityType.CAVE_SPIDER, Sounds.ENTITY_PARROT_IMITATE_SPIDER);
		hashMap.put(EntityType.CREEPER, Sounds.ENTITY_PARROT_IMITATE_CREEPER);
		hashMap.put(EntityType.DROWNED, Sounds.ENTITY_PARROT_IMITATE_DROWNED);
		hashMap.put(EntityType.ELDER_GUARDIAN, Sounds.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
		hashMap.put(EntityType.ENDER_DRAGON, Sounds.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
		hashMap.put(EntityType.ENDERMAN, Sounds.ENTITY_PARROT_IMITATE_ENDERMAN);
		hashMap.put(EntityType.ENDERMITE, Sounds.ENTITY_PARROT_IMITATE_ENDERMITE);
		hashMap.put(EntityType.EVOKER, Sounds.ENTITY_PARROT_IMITATE_EVOKER);
		hashMap.put(EntityType.GHAST, Sounds.ENTITY_PARROT_IMITATE_GHAST);
		hashMap.put(EntityType.HUSK, Sounds.ENTITY_PARROT_IMITATE_HUSK);
		hashMap.put(EntityType.ILLUSIONER, Sounds.ENTITY_PARROT_IMITATE_ILLUSIONER);
		hashMap.put(EntityType.MAGMA_CUBE, Sounds.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
		hashMap.put(EntityType.ZOMBIE_PIGMAN, Sounds.ENTITY_PARROT_IMITATE_ZOMBIE_PIGMAN);
		hashMap.put(EntityType.PHANTOM, Sounds.ENTITY_PARROT_IMITATE_PHANTOM);
		hashMap.put(EntityType.POLAR_BEAR, Sounds.ENTITY_PARROT_IMITATE_POLAR_BEAR);
		hashMap.put(EntityType.SHULKER, Sounds.ENTITY_PARROT_IMITATE_SHULKER);
		hashMap.put(EntityType.SILVERFISH, Sounds.ENTITY_PARROT_IMITATE_SILVERFISH);
		hashMap.put(EntityType.SKELETON, Sounds.ENTITY_PARROT_IMITATE_SKELETON);
		hashMap.put(EntityType.SLIME, Sounds.ENTITY_PARROT_IMITATE_SLIME);
		hashMap.put(EntityType.SPIDER, Sounds.ENTITY_PARROT_IMITATE_SPIDER);
		hashMap.put(EntityType.STRAY, Sounds.ENTITY_PARROT_IMITATE_STRAY);
		hashMap.put(EntityType.VEX, Sounds.ENTITY_PARROT_IMITATE_VEX);
		hashMap.put(EntityType.VINDICATOR, Sounds.ENTITY_PARROT_IMITATE_VINDICATOR);
		hashMap.put(EntityType.WITCH, Sounds.ENTITY_PARROT_IMITATE_WITCH);
		hashMap.put(EntityType.WITHER, Sounds.ENTITY_PARROT_IMITATE_WITHER);
		hashMap.put(EntityType.WITHER_SKELETON, Sounds.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
		hashMap.put(EntityType.WOLF, Sounds.ENTITY_PARROT_IMITATE_WOLF);
		hashMap.put(EntityType.ZOMBIE, Sounds.ENTITY_PARROT_IMITATE_ZOMBIE);
		hashMap.put(EntityType.ZOMBIE_VILLAGER, Sounds.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
	});
	public float field_15557;
	public float field_15558;
	public float field_15559;
	public float field_15560;
	public float field_15561 = 1.0F;
	private boolean field_15567;
	private BlockPos field_15568;

	public ParrotEntity(World world) {
		super(EntityType.PARROT, world);
		this.setBounds(0.5F, 0.9F);
		this.entityMotionHelper = new class_3369(this);
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		this.method_14111(this.random.nextInt(5));
		return super.initialize(difficulty, entityData, nbt);
	}

	@Override
	protected void initGoals() {
		this.sitGoal = new SitGoal(this);
		this.goals.add(0, new EscapeDangerGoal(this, 1.25));
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(2, this.sitGoal);
		this.goals.add(2, new class_3374(this, 1.0, 5.0F, 1.0F));
		this.goals.add(2, new class_3379(this, 1.0));
		this.goals.add(3, new class_3375(this));
		this.goals.add(3, new class_3373(this, 1.0, 3.0F, 7.0F));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.getAttributeContainer().register(EntityAttributes.GENERIC_FLYING_SPEED);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(6.0);
		this.initializeAttribute(EntityAttributes.GENERIC_FLYING_SPEED).setBaseValue(0.4F);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		class_3383 lv = new class_3383(this, world);
		lv.method_15098(false);
		lv.method_15709(true);
		lv.method_15099(true);
		return lv;
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.6F;
	}

	@Override
	public void tickMovement() {
		method_14104(this.world, this);
		if (this.field_15568 == null
			|| this.field_15568.squaredDistanceTo(this.x, this.y, this.z) > 12.0
			|| this.world.getBlockState(this.field_15568).getBlock() != Blocks.JUKEBOX) {
			this.field_15567 = false;
			this.field_15568 = null;
		}

		super.tickMovement();
		this.method_14109();
	}

	@Override
	public void method_15058(BlockPos blockPos, boolean bl) {
		this.field_15568 = blockPos;
		this.field_15567 = bl;
	}

	public boolean method_14106() {
		return this.field_15567;
	}

	private void method_14109() {
		this.field_15560 = this.field_15557;
		this.field_15559 = this.field_15558;
		this.field_15558 = (float)((double)this.field_15558 + (double)(this.onGround ? -1 : 4) * 0.3);
		this.field_15558 = MathHelper.clamp(this.field_15558, 0.0F, 1.0F);
		if (!this.onGround && this.field_15561 < 1.0F) {
			this.field_15561 = 1.0F;
		}

		this.field_15561 = (float)((double)this.field_15561 * 0.9);
		if (!this.onGround && this.velocityY < 0.0) {
			this.velocityY *= 0.6;
		}

		this.field_15557 = this.field_15557 + this.field_15561 * 2.0F;
	}

	private static boolean method_14104(World world, Entity entity) {
		if (!entity.isSilent() && world.random.nextInt(50) == 0) {
			List<MobEntity> list = world.method_16325(MobEntity.class, entity.getBoundingBox().expand(20.0), field_16914);
			if (!list.isEmpty()) {
				MobEntity mobEntity = (MobEntity)list.get(world.random.nextInt(list.size()));
				if (!mobEntity.isSilent()) {
					Sound sound = method_15754(mobEntity.method_15557());
					world.playSound(null, entity.x, entity.y, entity.z, sound, entity.getSoundCategory(), 0.7F, method_14105(world.random));
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!this.isTamed() && field_15565.contains(itemStack.getItem())) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			if (!this.isSilent()) {
				this.world
					.playSound(
						null, this.x, this.y, this.z, Sounds.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
					);
			}

			if (!this.world.isClient) {
				if (this.random.nextInt(10) == 0) {
					this.method_15070(playerEntity);
					this.showEmoteParticle(true);
					this.world.sendEntityStatus(this, (byte)7);
				} else {
					this.showEmoteParticle(false);
					this.world.sendEntityStatus(this, (byte)6);
				}
			}

			return true;
		} else if (itemStack.getItem() == field_15564) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			this.method_2654(new StatusEffectInstance(StatusEffects.POISON, 900));
			if (playerEntity.isCreative() || !this.isInvulnerable()) {
				this.damage(DamageSource.player(playerEntity), Float.MAX_VALUE);
			}

			return true;
		} else {
			if (!this.world.isClient && !this.method_14101() && this.isTamed() && this.isOwner(playerEntity)) {
				this.sitGoal.setEnabledWithOwner(!this.isSitting());
			}

			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Block block = iWorld.getBlockState(blockPos.down()).getBlock();
		return block instanceof LeavesBlock || block == Blocks.GRASS || block instanceof LogBlock || block == Blocks.AIR && super.method_15652(iWorld, bl);
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPos) {
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return false;
	}

	@Nullable
	@Override
	public PassiveEntity breed(PassiveEntity entity) {
		return null;
	}

	public static void method_14102(World world, Entity entity) {
		if (!entity.isSilent() && !method_14104(world, entity) && world.random.nextInt(200) == 0) {
			world.playSound(null, entity.x, entity.y, entity.z, method_14103(world.random), entity.getSoundCategory(), 1.0F, method_14105(world.random));
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		return target.damage(DamageSource.mob(this), 3.0F);
	}

	@Nullable
	@Override
	public Sound ambientSound() {
		return method_14103(this.random);
	}

	private static Sound method_14103(Random random) {
		if (random.nextInt(1000) == 0) {
			List<EntityType<?>> list = Lists.newArrayList(field_16915.keySet());
			return method_15754((EntityType<?>)list.get(random.nextInt(list.size())));
		} else {
			return Sounds.ENTITY_PARROT_AMBIENT;
		}
	}

	public static Sound method_15754(EntityType<?> entityType) {
		return (Sound)field_16915.getOrDefault(entityType, Sounds.ENTITY_PARROT_AMBIENT);
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_PARROT_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_PARROT_DEATH;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(Sounds.ENTITY_PARROT_STEP, 0.15F, 1.0F);
	}

	@Override
	protected float method_15055(float f) {
		this.playSound(Sounds.ENTITY_PARROT_FLY, 0.15F, 1.0F);
		return f + this.field_15558 / 2.0F;
	}

	@Override
	protected boolean method_15051() {
		return true;
	}

	@Override
	protected float getSoundPitch() {
		return method_14105(this.random);
	}

	private static float method_14105(Random random) {
		return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.NEUTRAL;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	protected void pushAway(Entity entity) {
		if (!(entity instanceof PlayerEntity)) {
			super.pushAway(entity);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (this.sitGoal != null) {
				this.sitGoal.setEnabledWithOwner(false);
			}

			return super.damage(source, amount);
		}
	}

	public int method_14107() {
		return MathHelper.clamp(this.dataTracker.get(field_15562), 0, 4);
	}

	public void method_14111(int i) {
		this.dataTracker.set(field_15562, i);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15562, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.method_14107());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_14111(nbt.getInt("Variant"));
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.PARROT_ENTITIE;
	}

	public boolean method_14101() {
		return !this.onGround;
	}
}
