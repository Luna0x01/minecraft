package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SnowGolemEntity extends GolemEntity implements RangedAttackMob {
	public SnowGolemEntity(World world) {
		super(world);
		this.setBounds(0.7F, 1.9F);
		((MobNavigation)this.getNavigation()).method_11027(true);
		this.goals.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0F));
		this.goals.add(2, new WanderAroundGoal(this, 1.0));
		this.goals.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(4, new LookAroundGoal(this));
		this.attackGoals.add(1, new FollowTargetGoal(this, MobEntity.class, 10, true, false, Monster.MONSTER_PREDICATE));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(4.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.world.isClient) {
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y);
			int k = MathHelper.floor(this.z);
			if (this.tickFire()) {
				this.damage(DamageSource.DROWN, 1.0F);
			}

			if (this.world.getBiome(new BlockPos(i, 0, k)).getTemperature(new BlockPos(i, j, k)) > 1.0F) {
				this.damage(DamageSource.ON_FIRE, 1.0F);
			}

			for (int l = 0; l < 4; l++) {
				i = MathHelper.floor(this.x + (double)((float)(l % 2 * 2 - 1) * 0.25F));
				j = MathHelper.floor(this.y);
				k = MathHelper.floor(this.z + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
				BlockPos blockPos = new BlockPos(i, j, k);
				if (this.world.getBlockState(blockPos).getBlock().getMaterial() == Material.AIR
					&& this.world.getBiome(new BlockPos(i, 0, k)).getTemperature(blockPos) < 0.8F
					&& Blocks.SNOW_LAYER.canBePlacedAtPos(this.world, blockPos)) {
					this.world.setBlockState(blockPos, Blocks.SNOW_LAYER.getDefaultState());
				}
			}
		}
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.SNOWBALL;
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(16);

		for (int j = 0; j < i; j++) {
			this.dropItem(Items.SNOWBALL, 1);
		}
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		SnowballEntity snowballEntity = new SnowballEntity(this.world, this);
		double d = target.y + (double)target.getEyeHeight() - 1.1F;
		double e = target.x - this.x;
		double f = d - snowballEntity.y;
		double g = target.z - this.z;
		float h = MathHelper.sqrt(e * e + g * g) * 0.2F;
		snowballEntity.setVelocity(e, f + (double)h, g, 1.6F, 12.0F);
		this.playSound("random.bow", 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(snowballEntity);
	}

	@Override
	public float getEyeHeight() {
		return 1.7F;
	}
}
