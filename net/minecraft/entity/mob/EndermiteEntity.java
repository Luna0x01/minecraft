package net.minecraft.entity.mob;

import net.minecraft.block.Block;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EndermiteEntity extends HostileEntity {
	private int lifeTime = 0;
	private boolean playerSpawned = false;

	public EndermiteEntity(World world) {
		super(world);
		this.experiencePoints = 3;
		this.setBounds(0.4F, 0.3F);
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new MeleeAttackGoal(this, PlayerEntity.class, 1.0, false));
		this.goals.add(3, new WanderAroundGoal(this, 1.0));
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
	protected String getAmbientSound() {
		return "mob.silverfish.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.silverfish.hit";
	}

	@Override
	protected String getDeathSound() {
		return "mob.silverfish.kill";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound("mob.silverfish.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getDefaultDrop() {
		return null;
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
					.addParticle(
						ParticleType.NETHER_PORTAL,
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
	public boolean canSpawn() {
		if (super.canSpawn()) {
			PlayerEntity playerEntity = this.world.getClosestPlayer(this, 5.0);
			return playerEntity == null;
		} else {
			return false;
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}
}
