package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_3133;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.class_2974;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class ZombiePigmanEntity extends ZombieEntity {
	private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
	private static final AttributeModifier field_6915 = new AttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.05, 0).setSerialized(false);
	private int anger;
	private int angrySoundDelay;
	private UUID angerTarget;

	public ZombiePigmanEntity(World world) {
		super(EntityType.ZOMBIE_PIGMAN, world);
		this.isFireImmune = true;
	}

	@Override
	public void setAttacker(@Nullable LivingEntity entity) {
		super.setAttacker(entity);
		if (entity != null) {
			this.angerTarget = entity.getUuid();
		}
	}

	@Override
	protected void initCustomGoals() {
		this.goals.add(2, new class_2974(this, 1.0, false));
		this.goals.add(7, new class_3133(this, 1.0));
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
	protected boolean method_15900() {
		return false;
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
			this.playSound(Sounds.ENTITY_ZOMBIE_PIGMAN_ANGRY, this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
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
	public boolean method_15652(IWorld iWorld, boolean bl) {
		return iWorld.method_16346() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean method_15653(RenderBlockView renderBlockView) {
		return renderBlockView.method_16382(this, this.getBoundingBox())
			&& renderBlockView.method_16387(this, this.getBoundingBox())
			&& !renderBlockView.method_16388(this.getBoundingBox());
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
		if (!string.isEmpty()) {
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
			if (entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative()) {
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
	protected Sound ambientSound() {
		return Sounds.ENTITY_ZOMBIE_PIGMAN_AMBIENT;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_ZOMBIE_PIGMAN_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_ZOMBIE_PIGMAN_DEATH;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ZOMBIE_PIGMAN_ENTITIE;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean method_14129(PlayerEntity playerEntity) {
		return this.isAngry();
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
