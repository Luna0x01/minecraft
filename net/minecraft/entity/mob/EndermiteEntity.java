package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixerUpper;
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
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EndermiteEntity extends HostileEntity {
	private int lifeTime;
	private boolean playerSpawned;

	public EndermiteEntity(World world) {
		super(world);
		this.experiencePoints = 3;
		this.setBounds(0.4F, 0.3F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new MeleeAttackGoal(this, 1.0, false));
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
	protected Sound ambientSound() {
		return Sounds.ENTITY_ENDERMITE_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_ENDERMITE_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_ENDERMITE_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound(Sounds.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ENDERMITE_ENTITIE;
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.method_13496(dataFixer, "Endermite");
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
	public double getHeightOffset() {
		return 0.3;
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
