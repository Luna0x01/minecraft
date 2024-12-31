package net.minecraft.entity.mob;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombiePigmanEntity extends ZombieEntity {
	private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
	private static final AttributeModifier field_6915 = new AttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.05, 0).setSerialized(false);
	private int anger;
	private int angrySoundDelay;
	private UUID angerTarget;

	public ZombiePigmanEntity(World world) {
		super(world);
		this.isFireImmune = true;
	}

	@Override
	public void setAttacker(LivingEntity entity) {
		super.setAttacker(entity);
		if (entity != null) {
			this.angerTarget = entity.getUuid();
		}
	}

	@Override
	protected void initCustomGoals() {
		this.attackGoals.add(1, new ZombiePigmanEntity.AvoidZombiesGoal(this));
		this.attackGoals.add(2, new ZombiePigmanEntity.FollowPlayerIfAngryGoal(this));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).setBaseValue(0.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23F);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	protected void mobTick() {
		EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (this.isAngry()) {
			if (!this.isBaby() && !entityAttributeInstance.hasModifier(field_6915)) {
				entityAttributeInstance.addModifier(field_6915);
			}

			this.anger--;
		} else if (entityAttributeInstance.hasModifier(field_6915)) {
			entityAttributeInstance.method_6193(field_6915);
		}

		if (this.angrySoundDelay > 0 && --this.angrySoundDelay == 0) {
			this.playSound("mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
		}

		if (this.anger > 0 && this.angerTarget != null && this.getAttacker() == null) {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.angerTarget);
			this.setAttacker(playerEntity);
			this.attackingPlayer = playerEntity;
			this.playerHitTimer = this.getLastHurtTimestamp();
		}

		super.mobTick();
	}

	@Override
	public boolean canSpawn() {
		return this.world.getGlobalDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean hasNoSpawnCollisions() {
		return this.world.hasEntityIn(this.getBoundingBox(), this)
			&& this.world.doesBoxCollide(this, this.getBoundingBox()).isEmpty()
			&& !this.world.containsFluid(this.getBoundingBox());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putShort("Anger", (short)this.anger);
		if (this.angerTarget != null) {
			nbt.putString("HurtBy", this.angerTarget.toString());
		} else {
			nbt.putString("HurtBy", "");
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.anger = nbt.getShort("Anger");
		String string = nbt.getString("HurtBy");
		if (string.length() > 0) {
			this.angerTarget = UUID.fromString(string);
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.angerTarget);
			this.setAttacker(playerEntity);
			if (playerEntity != null) {
				this.attackingPlayer = playerEntity;
				this.playerHitTimer = this.getLastHurtTimestamp();
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			Entity entity = source.getAttacker();
			if (entity instanceof PlayerEntity) {
				this.method_3088(entity);
			}

			return super.damage(source, amount);
		}
	}

	private void method_3088(Entity entity) {
		this.anger = 400 + this.random.nextInt(400);
		this.angrySoundDelay = this.random.nextInt(40);
		if (entity instanceof LivingEntity) {
			this.setAttacker((LivingEntity)entity);
		}
	}

	public boolean isAngry() {
		return this.anger > 0;
	}

	@Override
	protected String getAmbientSound() {
		return "mob.zombiepig.zpig";
	}

	@Override
	protected String getHurtSound() {
		return "mob.zombiepig.zpighurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.zombiepig.zpigdeath";
	}

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(2 + lootingMultiplier);

		for (int j = 0; j < i; j++) {
			this.dropItem(Items.ROTTEN_FLESH, 1);
		}

		i = this.random.nextInt(2 + lootingMultiplier);

		for (int k = 0; k < i; k++) {
			this.dropItem(Items.GOLD_NUGGET, 1);
		}
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	protected void method_4473() {
		this.dropItem(Items.GOLD_INGOT, 1);
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.setArmorSlot(0, new ItemStack(Items.GOLDEN_SWORD));
	}

	@Override
	public EntityData initialize(LocalDifficulty difficulty, EntityData data) {
		super.initialize(difficulty, data);
		this.setVillager(false);
		return data;
	}

	static class AvoidZombiesGoal extends RevengeGoal {
		public AvoidZombiesGoal(ZombiePigmanEntity zombiePigmanEntity) {
			super(zombiePigmanEntity, true);
		}

		@Override
		protected void setMobEntityTarget(PathAwareEntity mob, LivingEntity target) {
			super.setMobEntityTarget(mob, target);
			if (mob instanceof ZombiePigmanEntity) {
				((ZombiePigmanEntity)mob).method_3088(target);
			}
		}
	}

	static class FollowPlayerIfAngryGoal extends FollowTargetGoal<PlayerEntity> {
		public FollowPlayerIfAngryGoal(ZombiePigmanEntity zombiePigmanEntity) {
			super(zombiePigmanEntity, PlayerEntity.class, true);
		}

		@Override
		public boolean canStart() {
			return ((ZombiePigmanEntity)this.mob).isAngry() && super.canStart();
		}
	}
}
