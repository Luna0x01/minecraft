package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CowEntity extends AnimalEntity {
	public CowEntity(World world) {
		super(world);
		this.setBounds(0.9F, 1.3F);
		((MobNavigation)this.getNavigation()).method_11027(true);
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new EscapeDangerGoal(this, 2.0));
		this.goals.add(2, new BreedGoal(this, 1.0));
		this.goals.add(3, new TemptGoal(this, 1.25, Items.WHEAT, false));
		this.goals.add(4, new FollowParentGoal(this, 1.25));
		this.goals.add(5, new WanderAroundGoal(this, 1.0));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(7, new LookAroundGoal(this));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	protected String getAmbientSound() {
		return "mob.cow.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.cow.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.cow.hurt";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound("mob.cow.step", 0.15F, 1.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.LEATHER;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(3) + this.random.nextInt(1 + lootingMultiplier);

		for (int j = 0; j < i; j++) {
			this.dropItem(Items.LEATHER, 1);
		}

		i = this.random.nextInt(3) + 1 + this.random.nextInt(1 + lootingMultiplier);

		for (int k = 0; k < i; k++) {
			if (this.isOnFire()) {
				this.dropItem(Items.COOKED_BEEF, 1);
			} else {
				this.dropItem(Items.BEEF, 1);
			}
		}
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.getMainHandStack();
		if (itemStack != null && itemStack.getItem() == Items.BUCKET && !playerEntity.abilities.creativeMode && !this.isBaby()) {
			if (itemStack.count-- == 1) {
				playerEntity.inventory.setInvStack(playerEntity.inventory.selectedSlot, new ItemStack(Items.MILK_BUCKET));
			} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.MILK_BUCKET))) {
				playerEntity.dropItem(new ItemStack(Items.MILK_BUCKET, 1, 0), false);
			}

			return true;
		} else {
			return super.method_2537(playerEntity);
		}
	}

	public CowEntity breed(PassiveEntity passiveEntity) {
		return new CowEntity(this.world);
	}

	@Override
	public float getEyeHeight() {
		return this.height;
	}
}
