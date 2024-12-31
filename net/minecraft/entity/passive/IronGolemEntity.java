package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
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
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class IronGolemEntity extends GolemEntity {
	private int field_3724;
	Village village;
	private int attackTicksLeft;
	private int lookingAtVillagerTicksLeft;

	public IronGolemEntity(World world) {
		super(world);
		this.setBounds(1.4F, 2.9F);
		((MobNavigation)this.getNavigation()).method_11027(true);
		this.goals.add(1, new MeleeAttackGoal(this, 1.0, true));
		this.goals.add(2, new GoToEntityTargetGoal(this, 0.9, 32.0F));
		this.goals.add(3, new MoveThroughVillageGoal(this, 0.6, true));
		this.goals.add(4, new GoToWalkTargetGoal(this, 1.0));
		this.goals.add(5, new IronGolemLookGoal(this));
		this.goals.add(6, new WanderAroundGoal(this, 0.6));
		this.goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goals.add(8, new LookAroundGoal(this));
		this.attackGoals.add(1, new TrackIronGolemTargetGoal(this));
		this.attackGoals.add(2, new RevengeGoal(this, false));
		this.attackGoals.add(3, new IronGolemEntity.IronGolemFollowTargetGoal(this, MobEntity.class, 10, false, true, Monster.VISIBLE_MONSTER_PREDICATE));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, (byte)0);
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
			Block block = blockState.getBlock();
			if (block.getMaterial() != Material.AIR) {
				this.world
					.addParticle(
						ParticleType.BLOCK_CRACK,
						this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
						this.getBoundingBox().minY + 0.1,
						this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
						4.0 * ((double)this.random.nextFloat() - 0.5),
						0.5,
						((double)this.random.nextFloat() - 0.5) * 4.0,
						Block.getByBlockState(blockState)
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

		this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
		return bl;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 4) {
			this.attackTicksLeft = 10;
			this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
		} else if (status == 11) {
			this.lookingAtVillagerTicksLeft = 400;
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
		this.lookingAtVillagerTicksLeft = lookingAtVillager ? 400 : 0;
		this.world.sendEntityStatus(this, (byte)11);
	}

	@Override
	protected String getHurtSound() {
		return "mob.irongolem.hit";
	}

	@Override
	protected String getDeathSound() {
		return "mob.irongolem.death";
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {
		this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(3);

		for (int j = 0; j < i; j++) {
			this.dropItem(Item.fromBlock(Blocks.RED_FLOWER), 1, (float)FlowerBlock.FlowerType.POPPY.getDataIndex());
		}

		int k = 3 + this.random.nextInt(3);

		for (int l = 0; l < k; l++) {
			this.dropItem(Items.IRON_INGOT, 1);
		}
	}

	public int getLookingAtVillagerTicks() {
		return this.lookingAtVillagerTicksLeft;
	}

	public boolean isPlayerCreated() {
		return (this.dataTracker.getByte(16) & 1) != 0;
	}

	public void setPlayerCreated(boolean playerCreated) {
		byte b = this.dataTracker.getByte(16);
		if (playerCreated) {
			this.dataTracker.setProperty(16, (byte)(b | 1));
		} else {
			this.dataTracker.setProperty(16, (byte)(b & -2));
		}
	}

	@Override
	public void onKilled(DamageSource source) {
		if (!this.isPlayerCreated() && this.attackingPlayer != null && this.village != null) {
			this.village.method_4505(this.attackingPlayer.getTranslationKey(), -5);
		}

		super.onKilled(source);
	}

	static class IronGolemFollowTargetGoal<T extends LivingEntity> extends FollowTargetGoal<T> {
		public IronGolemFollowTargetGoal(PathAwareEntity pathAwareEntity, Class<T> class_, int i, boolean bl, boolean bl2, Predicate<? super T> predicate) {
			super(pathAwareEntity, class_, i, bl, bl2, predicate);
			this.targetPredicate = new Predicate<T>() {
				public boolean apply(T livingEntity) {
					if (predicate != null && !predicate.apply(livingEntity)) {
						return false;
					} else if (livingEntity instanceof CreeperEntity) {
						return false;
					} else {
						if (livingEntity instanceof PlayerEntity) {
							double d = IronGolemFollowTargetGoal.this.getFollowRange();
							if (livingEntity.isSneaking()) {
								d *= 0.8F;
							}

							if (livingEntity.isInvisible()) {
								float f = ((PlayerEntity)livingEntity).method_4575();
								if (f < 0.1F) {
									f = 0.1F;
								}

								d *= (double)(0.7F * f);
							}

							if ((double)livingEntity.distanceTo(pathAwareEntity) > d) {
								return false;
							}
						}

						return IronGolemFollowTargetGoal.this.canTrack(livingEntity, false);
					}
				}
			};
		}
	}
}
