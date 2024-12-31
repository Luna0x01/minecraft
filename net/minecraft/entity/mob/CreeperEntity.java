package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class CreeperEntity extends HostileEntity {
	private int lastFuseTime;
	private int currentFuseTime;
	private int fuseTime = 30;
	private int explosionRadius = 3;
	private int headsDropped = 0;

	public CreeperEntity(World world) {
		super(world);
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new CreeperIgniteGoal(this));
		this.goals.add(3, new FleeEntityGoal(this, OcelotEntity.class, 6.0F, 1.0, 1.2));
		this.goals.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goals.add(5, new WanderAroundGoal(this, 0.8));
		this.goals.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(6, new LookAroundGoal(this));
		this.attackGoals.add(1, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.attackGoals.add(2, new RevengeGoal(this, false));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public int getSafeFallDistance() {
		return this.getTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
		super.handleFallDamage(fallDistance, damageMultiplier);
		this.currentFuseTime = (int)((float)this.currentFuseTime + fallDistance * 1.5F);
		if (this.currentFuseTime > this.fuseTime - 5) {
			this.currentFuseTime = this.fuseTime - 5;
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.track(16, (byte)-1);
		this.dataTracker.track(17, (byte)0);
		this.dataTracker.track(18, (byte)0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.dataTracker.getByte(17) == 1) {
			nbt.putBoolean("powered", true);
		}

		nbt.putShort("Fuse", (short)this.fuseTime);
		nbt.putByte("ExplosionRadius", (byte)this.explosionRadius);
		nbt.putBoolean("ignited", this.isIgnited());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.dataTracker.setProperty(17, (byte)(nbt.getBoolean("powered") ? 1 : 0));
		if (nbt.contains("Fuse", 99)) {
			this.fuseTime = nbt.getShort("Fuse");
		}

		if (nbt.contains("ExplosionRadius", 99)) {
			this.explosionRadius = nbt.getByte("ExplosionRadius");
		}

		if (nbt.getBoolean("ignited")) {
			this.ignite();
		}
	}

	@Override
	public void tick() {
		if (this.isAlive()) {
			this.lastFuseTime = this.currentFuseTime;
			if (this.isIgnited()) {
				this.setFuseSpeed(1);
			}

			int i = this.getFuseSpeed();
			if (i > 0 && this.currentFuseTime == 0) {
				this.playSound("creeper.primed", 1.0F, 0.5F);
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
	protected String getHurtSound() {
		return "mob.creeper.say";
	}

	@Override
	protected String getDeathSound() {
		return "mob.creeper.death";
	}

	@Override
	public void onKilled(DamageSource source) {
		super.onKilled(source);
		if (source.getAttacker() instanceof SkeletonEntity) {
			int i = Item.getRawId(Items.RECORD_13);
			int j = Item.getRawId(Items.RECORD_WAIT);
			int k = i + this.random.nextInt(j - i + 1);
			this.dropItem(Item.byRawId(k), 1);
		} else if (source.getAttacker() instanceof CreeperEntity
			&& source.getAttacker() != this
			&& ((CreeperEntity)source.getAttacker()).method_3074()
			&& ((CreeperEntity)source.getAttacker()).shouldDropHead()) {
			((CreeperEntity)source.getAttacker()).onHeadDropped();
			this.dropItem(new ItemStack(Items.SKULL, 1, 4), 0.0F);
		}
	}

	@Override
	public boolean tryAttack(Entity target) {
		return true;
	}

	public boolean method_3074() {
		return this.dataTracker.getByte(17) == 1;
	}

	public float getClientFuseTime(float timeDelta) {
		return ((float)this.lastFuseTime + (float)(this.currentFuseTime - this.lastFuseTime) * timeDelta) / (float)(this.fuseTime - 2);
	}

	@Override
	protected Item getDefaultDrop() {
		return Items.GUNPOWDER;
	}

	public int getFuseSpeed() {
		return this.dataTracker.getByte(16);
	}

	public void setFuseSpeed(int value) {
		this.dataTracker.setProperty(16, (byte)value);
	}

	@Override
	public void onLightningStrike(LightningBoltEntity lightning) {
		super.onLightningStrike(lightning);
		this.dataTracker.setProperty(17, (byte)1);
	}

	@Override
	protected boolean method_2537(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.getMainHandStack();
		if (itemStack != null && itemStack.getItem() == Items.FLINT_AND_STEEL) {
			this.world.playSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, "fire.ignite", 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
			playerEntity.swingHand();
			if (!this.world.isClient) {
				this.ignite();
				itemStack.damage(1, playerEntity);
				return true;
			}
		}

		return super.method_2537(playerEntity);
	}

	private void explode() {
		if (!this.world.isClient) {
			boolean bl = this.world.getGameRules().getBoolean("mobGriefing");
			float f = this.method_3074() ? 2.0F : 1.0F;
			this.world.createExplosion(this, this.x, this.y, this.z, (float)this.explosionRadius * f, bl);
			this.remove();
		}
	}

	public boolean isIgnited() {
		return this.dataTracker.getByte(18) != 0;
	}

	public void ignite() {
		this.dataTracker.setProperty(18, (byte)1);
	}

	public boolean shouldDropHead() {
		return this.headsDropped < 1 && this.world.getGameRules().getBoolean("doMobLoot");
	}

	public void onHeadDropped() {
		this.headsDropped++;
	}
}
