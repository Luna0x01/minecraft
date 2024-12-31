package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.class_3462;
import net.minecraft.class_4342;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EndermiteEntity extends HostileEntity {
	private int lifeTime;
	private boolean playerSpawned;

	public EndermiteEntity(World world) {
		super(EntityType.ENDERMITE, world);
		this.experiencePoints = 3;
		this.setBounds(0.4F, 0.3F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new MeleeAttackGoal(this, 1.0, false));
		this.goals.add(3, new class_3133(this, 1.0));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, true));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	public float getEyeHeight() {
		return 0.1F;
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(8.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(2.0);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_ENDERMITE_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_ENDERMITE_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_ENDERMITE_DEATH;
	}

	@Override
	protected void method_10936(BlockPos blockPos, BlockState blockState) {
		this.playSound(Sounds.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ENDERMITE_ENTITIE;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.lifeTime = nbt.getInt("Lifetime");
		this.playerSpawned = nbt.getBoolean("PlayerSpawned");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Lifetime", this.lifeTime);
		nbt.putBoolean("PlayerSpawned", this.playerSpawned);
	}

	@Override
	public void tick() {
		this.bodyYaw = this.yaw;
		super.tick();
	}

	@Override
	public void setYaw(float yaw) {
		this.yaw = yaw;
		super.setYaw(yaw);
	}

	@Override
	public double getHeightOffset() {
		return 0.1;
	}

	public boolean isPlayerSpawned() {
		return this.playerSpawned;
	}

	public void setPlayerSpawned(boolean playerSpawned) {
		this.playerSpawned = playerSpawned;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient) {
			for (int i = 0; i < 2; i++) {
				this.world
					.method_16343(
						class_4342.field_21361,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
						this.y + this.random.nextDouble() * (double)this.height,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
						(this.random.nextDouble() - 0.5) * 2.0,
						-this.random.nextDouble(),
						(this.random.nextDouble() - 0.5) * 2.0
					);
			}
		} else {
			if (!this.isPersistent()) {
				this.lifeTime++;
			}

			if (this.lifeTime >= 2400) {
				this.remove();
			}
		}
	}

	@Override
	protected boolean method_3087() {
		return true;
	}

	@Override
	public boolean method_15652(IWorld iWorld, boolean bl) {
		if (super.method_15652(iWorld, bl)) {
			PlayerEntity playerEntity = iWorld.method_16364(this, 5.0);
			return playerEntity == null;
		} else {
			return false;
		}
	}

	@Override
	public class_3462 method_2647() {
		return class_3462.field_16820;
	}
}
