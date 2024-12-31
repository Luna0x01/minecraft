package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3132;
import net.minecraft.class_3133;
import net.minecraft.class_3135;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class LlamaEntity extends class_3135 implements RangedAttackMob {
	private static final TrackedData<Integer> STRENGTH = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_15512 = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean field_15514;
	@Nullable
	private LlamaEntity field_15515;
	@Nullable
	private LlamaEntity field_15516;

	public LlamaEntity(World world) {
		super(world);
		this.setBounds(0.9F, 1.87F);
	}

	private void setStrength(int strength) {
		this.dataTracker.set(STRENGTH, Math.max(1, Math.min(5, strength)));
	}

	private void method_14032() {
		int i = this.random.nextFloat() < 0.04F ? 5 : 3;
		this.setStrength(1 + this.random.nextInt(i));
	}

	public int getStrength() {
		return this.dataTracker.get(STRENGTH);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.getVariant());
		nbt.putInt("Strength", this.getStrength());
		if (!this.animalInventory.getInvStack(1).isEmpty()) {
			nbt.put("DecorItem", this.animalInventory.getInvStack(1).toNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setStrength(nbt.getInt("Strength"));
		super.readCustomDataFromNbt(nbt);
		this.setVariant(nbt.getInt("Variant"));
		if (nbt.contains("DecorItem", 10)) {
			this.animalInventory.setInvStack(1, new ItemStack(nbt.getCompound("DecorItem")));
		}

		this.method_6244();
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goals.add(2, new class_3132(this, 2.1F));
		this.goals.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0F));
		this.goals.add(3, new EscapeDangerGoal(this, 1.2));
		this.goals.add(4, new BreedGoal(this, 1.0));
		this.goals.add(5, new FollowParentGoal(this, 1.0));
		this.goals.add(6, new class_3133(this, 0.7));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.attackGoals.add(1, new LlamaEntity.class_3141(this));
		this.attackGoals.add(2, new LlamaEntity.class_3139(this));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(40.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(STRENGTH, 0);
		this.dataTracker.startTracking(field_15512, -1);
		this.dataTracker.startTracking(VARIANT, 0);
	}

	public int getVariant() {
		return MathHelper.clamp(this.dataTracker.get(VARIANT), 0, 3);
	}

	public void setVariant(int variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	@Override
	protected int method_13987() {
		return this.method_13963() ? 2 + 3 * this.method_13965() : super.method_13987();
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		if (this.hasPassenger(passenger)) {
			float f = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
			float g = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
			float h = 0.3F;
			passenger.updatePosition(this.x + (double)(0.3F * g), this.y + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.z - (double)(0.3F * f));
		}
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)this.height * 0.67;
	}

	@Override
	public boolean canBeControlledByRider() {
		return false;
	}

	@Override
	protected boolean method_13970(PlayerEntity playerEntity, ItemStack itemStack) {
		int i = 0;
		int j = 0;
		float f = 0.0F;
		boolean bl = false;
		Item item = itemStack.getItem();
		if (item == Items.WHEAT) {
			i = 10;
			j = 3;
			f = 2.0F;
		} else if (item == Item.fromBlock(Blocks.HAY_BALE)) {
			i = 90;
			j = 6;
			f = 10.0F;
			if (this.method_13990() && this.age() == 0) {
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

		if (bl && !this.isSilent()) {
			this.world
				.playSound(
					null, this.x, this.y, this.z, Sounds.ENTITY_LLAMA_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
				);
		}

		return bl;
	}

	@Override
	protected boolean method_2610() {
		return this.getHealth() <= 0.0F || this.method_13994();
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		this.method_14032();
		int i;
		if (data instanceof LlamaEntity.class_3140) {
			i = ((LlamaEntity.class_3140)data).field_15517;
		} else {
			i = this.random.nextInt(4);
			data = new LlamaEntity.class_3140(i);
		}

		this.setVariant(i);
		return data;
	}

	public boolean method_14026() {
		return this.method_14027() != null;
	}

	@Override
	protected Sound method_13132() {
		return Sounds.ENTITY_LLAMA_ANGRY;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_LLAMA_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_LLAMA_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_LLAMA_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound(Sounds.ENTITY_LLAMA_STEP, 0.15F, 1.0F);
	}

	@Override
	protected void method_13964() {
		this.playSound(Sounds.ENTITY_LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public void method_13979() {
		Sound sound = this.method_13132();
		if (sound != null) {
			this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.LLAMA_ENTITIE;
	}

	@Override
	public int method_13965() {
		return this.getStrength();
	}

	@Override
	public boolean method_13984() {
		return true;
	}

	@Override
	public boolean method_14001(ItemStack itemStack) {
		return itemStack.getItem() == Item.fromBlock(Blocks.CARPET);
	}

	@Override
	public boolean method_13974() {
		return false;
	}

	@Override
	public void method_13928(Inventory inventory) {
		DyeColor dyeColor = this.method_14027();
		super.method_13928(inventory);
		DyeColor dyeColor2 = this.method_14027();
		if (this.ticksAlive > 20 && dyeColor2 != null && dyeColor2 != dyeColor) {
			this.playSound(Sounds.ENTITY_LLAMA_SWAG, 0.5F, 1.0F);
		}
	}

	@Override
	protected void method_6244() {
		if (!this.world.isClient) {
			super.method_6244();
			this.method_14034(this.animalInventory.getInvStack(1));
		}
	}

	private void method_14019(@Nullable DyeColor dyeColor) {
		this.dataTracker.set(field_15512, dyeColor == null ? -1 : dyeColor.getId());
	}

	private void method_14034(ItemStack itemStack) {
		if (this.method_14001(itemStack)) {
			this.method_14019(DyeColor.byId(itemStack.getData()));
		} else {
			this.method_14019(null);
		}
	}

	@Nullable
	public DyeColor method_14027() {
		int i = this.dataTracker.get(field_15512);
		return i == -1 ? null : DyeColor.byId(i);
	}

	@Override
	public int method_13976() {
		return 30;
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return other != this && other instanceof LlamaEntity && this.method_13980() && ((LlamaEntity)other).method_13980();
	}

	public LlamaEntity breed(PassiveEntity passiveEntity) {
		LlamaEntity llamaEntity = new LlamaEntity(this.world);
		this.method_13968(passiveEntity, llamaEntity);
		LlamaEntity llamaEntity2 = (LlamaEntity)passiveEntity;
		int i = this.random.nextInt(Math.max(this.getStrength(), llamaEntity2.getStrength())) + 1;
		if (this.random.nextFloat() < 0.03F) {
			i++;
		}

		llamaEntity.setStrength(i);
		llamaEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : llamaEntity2.getVariant());
		return llamaEntity;
	}

	private void attack(LivingEntity entity) {
		LlamaSpitEntity llamaSpitEntity = new LlamaSpitEntity(this.world, this);
		double d = entity.x - this.x;
		double e = entity.getBoundingBox().minY + (double)(entity.height / 3.0F) - llamaSpitEntity.y;
		double f = entity.z - this.z;
		float g = MathHelper.sqrt(d * d + f * f) * 0.2F;
		llamaSpitEntity.setVelocity(d, e + (double)g, f, 1.5F, 10.0F);
		this.world
			.playSound(
				null, this.x, this.y, this.z, Sounds.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
			);
		this.world.spawnEntity(llamaSpitEntity);
		this.field_15514 = true;
	}

	private void method_14037(boolean bl) {
		this.field_15514 = bl;
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		int i = MathHelper.ceil((fallDistance * 0.5F - 3.0F) * damageMultiplier);
		if (i > 0) {
			if (fallDistance >= 6.0F) {
				this.damage(DamageSource.FALL, (float)i);
				if (this.hasPassengers()) {
					for (Entity entity : this.getPassengersDeep()) {
						entity.damage(DamageSource.FALL, (float)i);
					}
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

	public void method_14028() {
		if (this.field_15515 != null) {
			this.field_15515.field_15516 = null;
		}

		this.field_15515 = null;
	}

	public void method_14020(LlamaEntity llamaEntity) {
		this.field_15515 = llamaEntity;
		this.field_15515.field_15516 = this;
	}

	public boolean method_14029() {
		return this.field_15516 != null;
	}

	public boolean method_14030() {
		return this.field_15515 != null;
	}

	@Nullable
	public LlamaEntity method_14031() {
		return this.field_15515;
	}

	@Override
	protected double method_13951() {
		return 2.0;
	}

	@Override
	protected void method_13977() {
		if (!this.method_14030() && this.isBaby()) {
			super.method_13977();
		}
	}

	@Override
	public boolean method_13978() {
		return false;
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		this.attack(target);
	}

	@Override
	public void method_14057(boolean bl) {
	}

	static class class_3139 extends FollowTargetGoal<WolfEntity> {
		public class_3139(LlamaEntity llamaEntity) {
			super(llamaEntity, WolfEntity.class, 16, false, true, null);
		}

		@Override
		public boolean canStart() {
			if (super.canStart() && this.target != null && !this.target.isTamed()) {
				return true;
			} else {
				this.mob.setTarget(null);
				return false;
			}
		}

		@Override
		protected double getFollowRange() {
			return super.getFollowRange() * 0.25;
		}
	}

	static class class_3140 implements EntityData {
		public int field_15517;

		private class_3140(int i) {
			this.field_15517 = i;
		}
	}

	static class class_3141 extends RevengeGoal {
		public class_3141(LlamaEntity llamaEntity) {
			super(llamaEntity, false);
		}

		@Override
		public boolean shouldContinue() {
			if (this.mob instanceof LlamaEntity) {
				LlamaEntity llamaEntity = (LlamaEntity)this.mob;
				if (llamaEntity.field_15514) {
					llamaEntity.method_14037(false);
					return false;
				}
			}

			return super.shouldContinue();
		}
	}
}
