package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class OcelotEntity extends TameableEntity {
	private static final TrackedData<Integer> field_14612 = DataTracker.registerData(OcelotEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private FleeEntityGoal<PlayerEntity> field_11976;
	private TemptGoal field_3705;

	public OcelotEntity(World world) {
		super(world);
		this.setBounds(0.6F, 0.7F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, this.sitGoal = new SitGoal(this));
		this.goals.add(3, this.field_3705 = new TemptGoal(this, 0.6, Items.RAW_FISH, true));
		this.goals.add(5, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
		this.goals.add(6, new CatSitOnBlockGoal(this, 0.8));
		this.goals.add(7, new PounceAtTargetGoal(this, 0.3F));
		this.goals.add(8, new AttackGoal(this));
		this.goals.add(9, new BreedGoal(this, 0.8));
		this.goals.add(10, new WanderAroundGoal(this, 0.8));
		this.goals.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		this.attackGoals.add(1, new FollowTargetIfTamedGoal(this, ChickenEntity.class, false, null));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14612, 0);
	}

	@Override
	public void mobTick() {
		if (this.getMotionHelper().isMoving()) {
			double d = this.getMotionHelper().getSpeed();
			if (d == 0.6) {
				this.setSneaking(true);
				this.setSprinting(false);
			} else if (d == 1.33) {
				this.setSneaking(false);
				this.setSprinting(true);
			} else {
				this.setSneaking(false);
				this.setSprinting(false);
			}
		} else {
			this.setSneaking(false);
			this.setSprinting(false);
		}
	}

	@Override
	protected boolean canImmediatelyDespawn() {
		return !this.isTamed() && this.ticksAlive > 2400;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("CatType", this.getCatVariant());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setCatVariant(nbt.getInt("CatType"));
	}

	@Nullable
	@Override
	protected Sound ambientSound() {
		if (this.isTamed()) {
			if (this.isInLove()) {
				return Sounds.ENTITY_CAT_PURR;
			} else {
				return this.random.nextInt(4) == 0 ? Sounds.ENTITY_CAT_PURREOW : Sounds.ENTITY_CAT_AMBIENT;
			}
		} else {
			return null;
		}
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_CAT_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_CAT_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public boolean tryAttack(Entity target) {
		return target.damage(DamageSource.mob(this), 3.0F);
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

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.OCELOT_ENTITIE;
	}

	@Override
	public boolean method_13079(PlayerEntity playerEntity, Hand hand, @Nullable ItemStack itemStack) {
		if (this.isTamed()) {
			if (this.isOwner(playerEntity) && !this.world.isClient && !this.isBreedingItem(itemStack)) {
				this.sitGoal.setEnabledWithOwner(!this.isSitting());
			}
		} else if ((this.field_3705 == null || this.field_3705.isActive())
			&& itemStack != null
			&& itemStack.getItem() == Items.RAW_FISH
			&& playerEntity.squaredDistanceTo(this) < 9.0) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.count--;
			}

			if (!this.world.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.setTamed(true);
					this.setCatVariant(1 + this.world.random.nextInt(3));
					this.method_13092(playerEntity.getUuid());
					this.showEmoteParticle(true);
					this.sitGoal.setEnabledWithOwner(true);
					this.world.sendEntityStatus(this, (byte)7);
				} else {
					this.showEmoteParticle(false);
					this.world.sendEntityStatus(this, (byte)6);
				}
			}

			return true;
		}

		return super.method_13079(playerEntity, hand, itemStack);
	}

	public OcelotEntity breed(PassiveEntity passiveEntity) {
		OcelotEntity ocelotEntity = new OcelotEntity(this.world);
		if (this.isTamed()) {
			ocelotEntity.method_13092(this.method_2719());
			ocelotEntity.setTamed(true);
			ocelotEntity.setCatVariant(this.getCatVariant());
		}

		return ocelotEntity;
	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack) {
		return stack != null && stack.getItem() == Items.RAW_FISH;
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(other instanceof OcelotEntity)) {
			return false;
		} else {
			OcelotEntity ocelotEntity = (OcelotEntity)other;
			return !ocelotEntity.isTamed() ? false : this.isInLove() && ocelotEntity.isInLove();
		}
	}

	public int getCatVariant() {
		return this.dataTracker.get(field_14612);
	}

	public void setCatVariant(int variant) {
		this.dataTracker.set(field_14612, variant);
	}

	@Override
	public boolean canSpawn() {
		return this.world.random.nextInt(3) != 0;
	}

	@Override
	public boolean hasNoSpawnCollisions() {
		if (this.world.hasEntityIn(this.getBoundingBox(), this)
			&& this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()
			&& !this.world.containsFluid(this.getBoundingBox())) {
			BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
			if (blockPos.getY() < this.world.getSeaLevel()) {
				return false;
			}

			BlockState blockState = this.world.getBlockState(blockPos.down());
			Block block = blockState.getBlock();
			if (block == Blocks.GRASS || blockState.getMaterial() == Material.FOLIAGE) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getTranslationKey() {
		if (this.hasCustomName()) {
			return this.getCustomName();
		} else {
			return this.isTamed() ? CommonI18n.translate("entity.Cat.name") : super.getTranslationKey();
		}
	}

	@Override
	public void setTamed(boolean tamed) {
		super.setTamed(tamed);
	}

	@Override
	protected void onTamedChanged() {
		if (this.field_11976 == null) {
			this.field_11976 = new FleeEntityGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.goals.method_4497(this.field_11976);
		if (!this.isTamed()) {
			this.goals.add(4, this.field_11976);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		data = super.initialize(difficulty, data);
		if (this.world.random.nextInt(7) == 0) {
			for (int i = 0; i < 2; i++) {
				OcelotEntity ocelotEntity = new OcelotEntity(this.world);
				ocelotEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
				ocelotEntity.setAge(-24000);
				this.world.spawnEntity(ocelotEntity);
			}
		}

		return data;
	}
}
