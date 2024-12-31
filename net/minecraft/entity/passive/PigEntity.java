package net.minecraft.entity.passive;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PlayerControlGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity {
	private final PlayerControlGoal playerControlGoal;

	public PigEntity(World world) {
		super(world);
		this.setBounds(0.9F, 0.9F);
		((MobNavigation)this.getNavigation()).method_11027(true);
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 1.25));
		this.goals.add(2, this.playerControlGoal = new PlayerControlGoal(this, 0.3F));
		this.goals.add(3, new BreedGoal(this, 1.0));
		this.goals.add(4, new TemptGoal(this, 1.2, Items.CARROT_ON_A_STICK, false));
		this.goals.add(4, new TemptGoal(this, 1.2, Items.CARROT, false));
		this.goals.add(5, new FollowParentGoal(this, 1.1));
		this.goals.add(6, new WanderAroundGoal(this, 1.0));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public boolean canBeControlledByRider() {
		ItemStack itemStack = ((PlayerEntity)this.rider).getStackInHand();
		return itemStack != null && itemStack.getItem() == Items.CARROT_ON_A_STICK;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, (byte)0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Saddle", this.isSaddled());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setSaddled(nbt.getBoolean("Saddle"));
	}

	@Override
	protected String getAmbientSound() {
		return "mob.pig.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.pig.say";
	}

	@Override
	protected String getDeathSound() {
		return "mob.pig.death";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound("mob.pig.step", 0.15F, 1.0F);
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		if (super.method_2537(playerEntity)) {
			return true;
		} else if (!this.isSaddled() || this.world.isClient || this.rider != null && this.rider != playerEntity) {
			return false;
		} else {
			playerEntity.startRiding(this);
			return true;
		}
	}

	@Override
	protected Item getDefaultDrop() {
		return this.isOnFire() ? Items.COOKED_PORKCHOP : Items.RAW_PORKCHOP;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(3) + 1 + this.random.nextInt(1 + lootingMultiplier);

		for (int j = 0; j < i; j++) {
			if (this.isOnFire()) {
				this.dropItem(Items.COOKED_PORKCHOP, 1);
			} else {
				this.dropItem(Items.RAW_PORKCHOP, 1);
			}
		}

		if (this.isSaddled()) {
			this.dropItem(Items.SADDLE, 1);
		}
	}

	public boolean isSaddled() {
		return (this.dataTracker.getByte(16) & 1) != 0;
	}

	public void setSaddled(boolean bl) {
		if (bl) {
			this.dataTracker.setProperty(16, (byte)1);
		} else {
			this.dataTracker.setProperty(16, (byte)0);
		}
	}

	@Override
	public void onLightningStrike(LightningBoltEntity lightning) {
		if (!this.world.isClient && !this.removed) {
			ZombiePigmanEntity zombiePigmanEntity = new ZombiePigmanEntity(this.world);
			zombiePigmanEntity.setArmorSlot(0, new ItemStack(Items.GOLDEN_SWORD));
			zombiePigmanEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			zombiePigmanEntity.setAiDisabled(this.hasNoAi());
			if (this.hasCustomName()) {
				zombiePigmanEntity.setCustomName(this.getCustomName());
				zombiePigmanEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			this.world.spawnEntity(zombiePigmanEntity);
			this.remove();
		}
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		super.handleFallDamage(fallDistance, damageMultiplier);
		if (fallDistance > 5.0F && this.rider instanceof PlayerEntity) {
			((PlayerEntity)this.rider).incrementStat(AchievementsAndCriterions.FLY_PIG);
		}
	}

	public PigEntity breed(PassiveEntity passiveEntity) {
		return new PigEntity(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack != null && stack.getItem() == Items.CARROT;
	}

	public PlayerControlGoal getPlayerControlGoal() {
		return this.playerControlGoal;
	}
}
