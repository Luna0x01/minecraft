package net.minecraft.entity.mob;

import java.util.Collection;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class CreeperEntity extends HostileEntity implements SkinOverlayOwner {
	private static final TrackedData<Integer> FUSE_SPEED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int lastFuseTime;
	private int currentFuseTime;
	private int fuseTime = 30;
	private int explosionRadius = 3;
	private int headsDropped;

	public CreeperEntity(EntityType<? extends CreeperEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new CreeperIgniteGoal(this));
		this.goalSelector.add(3, new FleeEntityGoal(this, OcelotEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.add(3, new FleeEntityGoal(this, CatEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(2, new RevengeGoal(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public int getSafeFallDistance() {
		return this.getTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
	}

	@Override
	public boolean handleFallDamage(float f, float g) {
		boolean bl = super.handleFallDamage(f, g);
		this.currentFuseTime = (int)((float)this.currentFuseTime + f * 1.5F);
		if (this.currentFuseTime > this.fuseTime - 5) {
			this.currentFuseTime = this.fuseTime - 5;
		}

		return bl;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(FUSE_SPEED, -1);
		this.dataTracker.startTracking(CHARGED, false);
		this.dataTracker.startTracking(IGNITED, false);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.dataTracker.get(CHARGED)) {
			compoundTag.putBoolean("powered", true);
		}

		compoundTag.putShort("Fuse", (short)this.fuseTime);
		compoundTag.putByte("ExplosionRadius", (byte)this.explosionRadius);
		compoundTag.putBoolean("ignited", this.getIgnited());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.dataTracker.set(CHARGED, compoundTag.getBoolean("powered"));
		if (compoundTag.contains("Fuse", 99)) {
			this.fuseTime = compoundTag.getShort("Fuse");
		}

		if (compoundTag.contains("ExplosionRadius", 99)) {
			this.explosionRadius = compoundTag.getByte("ExplosionRadius");
		}

		if (compoundTag.getBoolean("ignited")) {
			this.setIgnited();
		}
	}

	@Override
	public void tick() {
		if (this.isAlive()) {
			this.lastFuseTime = this.currentFuseTime;
			if (this.getIgnited()) {
				this.setFuseSpeed(1);
			}

			int i = this.getFuseSpeed();
			if (i > 0 && this.currentFuseTime == 0) {
				this.playSound(SoundEvents.field_15057, 1.0F, 0.5F);
			}

			this.currentFuseTime += i;
			if (this.currentFuseTime < 0) {
				this.currentFuseTime = 0;
			}

			if (this.currentFuseTime >= this.fuseTime) {
				this.currentFuseTime = this.fuseTime;
				this.explode();
			}
		}

		super.tick();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15192;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14907;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.getAttacker();
		if (entity != this && entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				this.dropItem(Items.CREEPER_HEAD);
			}
		}
	}

	@Override
	public boolean tryAttack(Entity entity) {
		return true;
	}

	@Override
	public boolean shouldRenderOverlay() {
		return this.dataTracker.get(CHARGED);
	}

	public float getClientFuseTime(float f) {
		return MathHelper.lerp(f, (float)this.lastFuseTime, (float)this.currentFuseTime) / (float)(this.fuseTime - 2);
	}

	public int getFuseSpeed() {
		return this.dataTracker.get(FUSE_SPEED);
	}

	public void setFuseSpeed(int i) {
		this.dataTracker.set(FUSE_SPEED, i);
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		super.onStruckByLightning(lightningEntity);
		this.dataTracker.set(CHARGED, true);
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8884) {
			this.world
				.playSound(
					playerEntity, this.getX(), this.getY(), this.getZ(), SoundEvents.field_15145, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F
				);
			if (!this.world.isClient) {
				this.setIgnited();
				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	private void explode() {
		if (!this.world.isClient) {
			Explosion.DestructionType destructionType = this.world.getGameRules().getBoolean(GameRules.field_19388)
				? Explosion.DestructionType.field_18687
				: Explosion.DestructionType.field_18685;
			float f = this.shouldRenderOverlay() ? 2.0F : 1.0F;
			this.dead = true;
			this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, destructionType);
			this.remove();
			this.spawnEffectsCloud();
		}
	}

	private void spawnEffectsCloud() {
		Collection<StatusEffectInstance> collection = this.getStatusEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
			areaEffectCloudEntity.setRadius(2.5F);
			areaEffectCloudEntity.setRadiusOnUse(-0.5F);
			areaEffectCloudEntity.setWaitTime(10);
			areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
			areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

			for (StatusEffectInstance statusEffectInstance : collection) {
				areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
			}

			this.world.spawnEntity(areaEffectCloudEntity);
		}
	}

	public boolean getIgnited() {
		return this.dataTracker.get(IGNITED);
	}

	public void setIgnited() {
		this.dataTracker.set(IGNITED, true);
	}

	public boolean shouldDropHead() {
		return this.shouldRenderOverlay() && this.headsDropped < 1;
	}

	public void onHeadDropped() {
		this.headsDropped++;
	}
}
