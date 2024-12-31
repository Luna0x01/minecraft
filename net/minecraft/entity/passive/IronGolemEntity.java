package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.class_4337;
import net.minecraft.class_4342;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToEntityTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.IronGolemLookGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackIronGolemTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class IronGolemEntity extends GolemEntity {
	protected static final TrackedData<Byte> field_14623 = DataTracker.registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
	private int field_3724;
	@Nullable
	private Village village;
	private int attackTicksLeft;
	private int lookingAtVillagerTicksLeft;

	public IronGolemEntity(World world) {
		super(EntityType.IRON_GOLEM, world);
		this.setBounds(1.4F, 2.7F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new MeleeAttackGoal(this, 1.0, true));
		this.goals.add(2, new GoToEntityTargetGoal(this, 0.9, 32.0F));
		this.goals.add(3, new MoveThroughVillageGoal(this, 0.6, true));
		this.goals.add(4, new GoToWalkTargetGoal(this, 1.0));
		this.goals.add(5, new IronGolemLookGoal(this));
		this.goals.add(6, new class_3133(this, 0.6));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.attackGoals.add(1, new TrackIronGolemTargetGoal(this));
		this.attackGoals.add(2, new RevengeGoal(this, false));
		this.attackGoals
			.add(
				3,
				new FollowTargetGoal(
					this, MobEntity.class, 10, false, true, mobEntity -> mobEntity != null && Monster.field_17045.test(mobEntity) && !(mobEntity instanceof CreeperEntity)
				)
			);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14623, (byte)0);
	}

	@Override
	protected void mobTick() {
		if (--this.field_3724 <= 0) {
			this.field_3724 = 70 + this.random.nextInt(50);
			this.village = this.world.getVillageState().method_11062(new BlockPos(this), 32);
			if (this.village == null) {
				this.method_6173();
			} else {
				BlockPos blockPos = this.village.getMinPos();
				this.setPositionTarget(blockPos, (int)((float)this.village.getRadius() * 0.6F));
			}
		}

		super.mobTick();
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(100.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
		this.initializeAttribute(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
	}

	@Override
	protected int getNextAirUnderwater(int air) {
		return air;
	}

	@Override
	protected void pushAway(Entity entity) {
		if (entity instanceof Monster && !(entity instanceof CreeperEntity) && this.getRandom().nextInt(20) == 0) {
			this.setTarget((LivingEntity)entity);
		}

		super.pushAway(entity);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.attackTicksLeft > 0) {
			this.attackTicksLeft--;
		}

		if (this.lookingAtVillagerTicksLeft > 0) {
			this.lookingAtVillagerTicksLeft--;
		}

		if (this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 2.5000003E-7F && this.random.nextInt(5) == 0) {
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y - 0.2F);
			int k = MathHelper.floor(this.z);
			BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
			if (!blockState.isAir()) {
				this.world
					.method_16343(
						new class_4337(class_4342.BLOCK, blockState),
						this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
						this.getBoundingBox().minY + 0.1,
						this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
						4.0 * ((double)this.random.nextFloat() - 0.5),
						0.5,
						((double)this.random.nextFloat() - 0.5) * 4.0
					);
			}
		}
	}

	@Override
	public boolean canAttackEntity(Class<? extends LivingEntity> clazz) {
		if (this.isPlayerCreated() && PlayerEntity.class.isAssignableFrom(clazz)) {
			return false;
		} else {
			return clazz == CreeperEntity.class ? false : super.canAttackEntity(clazz);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("PlayerCreated", this.isPlayerCreated());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setPlayerCreated(nbt.getBoolean("PlayerCreated"));
	}

	@Override
	public boolean tryAttack(Entity target) {
		this.attackTicksLeft = 10;
		this.world.sendEntityStatus(this, (byte)4);
		boolean bl = target.damage(DamageSource.mob(this), (float)(7 + this.random.nextInt(15)));
		if (bl) {
			target.velocityY += 0.4F;
			this.dealDamage(this, target);
		}

		this.playSound(Sounds.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		return bl;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 4) {
			this.attackTicksLeft = 10;
			this.playSound(Sounds.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		} else if (status == 11) {
			this.lookingAtVillagerTicksLeft = 400;
		} else if (status == 34) {
			this.lookingAtVillagerTicksLeft = 0;
		} else {
			super.handleStatus(status);
		}
	}

	public Village method_2870() {
		return this.village;
	}

	public int getAttackTicksLeft() {
		return this.attackTicksLeft;
	}

	public void setLookingAtVillager(boolean lookingAtVillager) {
		if (lookingAtVillager) {
			this.lookingAtVillagerTicksLeft = 400;
			this.world.sendEntityStatus(this, (byte)11);
		} else {
			this.lookingAtVillagerTicksLeft = 0;
			this.world.sendEntityStatus(this, (byte)34);
		}
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_IRON_GOLEM_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_IRON_GOLEM_DEATH;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(Sounds.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.IRON_GOLEM_ENTITIE;
	}

	public int getLookingAtVillagerTicks() {
		return this.lookingAtVillagerTicksLeft;
	}

	public boolean isPlayerCreated() {
		return (this.dataTracker.get(field_14623) & 1) != 0;
	}

	public void setPlayerCreated(boolean playerCreated) {
		byte b = this.dataTracker.get(field_14623);
		if (playerCreated) {
			this.dataTracker.set(field_14623, (byte)(b | 1));
		} else {
			this.dataTracker.set(field_14623, (byte)(b & -2));
		}
	}

	@Override
	public void onKilled(DamageSource source) {
		if (!this.isPlayerCreated() && this.attackingPlayer != null && this.village != null) {
			this.village.method_4505(this.attackingPlayer.getGameProfile().getName(), -5);
		}

		super.onKilled(source);
	}

	@Override
	public boolean method_15653(RenderBlockView renderBlockView) {
		BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
		BlockState blockState = renderBlockView.getBlockState(blockPos);
		BlockState blockState2 = renderBlockView.getBlockState(blockPos.down());
		BlockState blockState3 = renderBlockView.getBlockState(blockPos.up());
		return blockState2.method_16913()
			&& MobSpawnerHelper.method_16407(blockState3, blockState3.getFluidState())
			&& MobSpawnerHelper.method_16407(blockState, Fluids.EMPTY.getDefaultState())
			&& renderBlockView.method_16387(this, this.getBoundingBox())
			&& renderBlockView.method_16382(this, this.getBoundingBox());
	}
}
