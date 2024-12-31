package net.minecraft.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3162;
import net.minecraft.class_3462;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class VindicationIllagerEntity extends class_3162 {
	private boolean field_15072;
	private static final Predicate<Entity> field_17070 = entity -> entity instanceof LivingEntity && ((LivingEntity)entity).method_13948();

	public VindicationIllagerEntity(World world) {
		super(EntityType.VINDICATOR, world);
		this.setBounds(0.6F, 1.95F);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goals.add(8, new WanderAroundGoal(this, 0.6));
		this.goals.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goals.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.attackGoals.add(1, new RevengeGoal(this, true, VindicationIllagerEntity.class));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.attackGoals.add(3, new FollowTargetGoal(this, VillagerEntity.class, true));
		this.attackGoals.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.attackGoals.add(4, new VindicationIllagerEntity.class_3048(this));
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35F);
		this.initializeAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(12.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(24.0);
		this.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Override
	protected Identifier getLootTableId() {
		return LootTables.VINDICATOR_ENTITIE;
	}

	public boolean method_13600() {
		return this.method_14121(1);
	}

	public void method_13598(boolean bl) {
		this.method_14122(1, bl);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.field_15072) {
			nbt.putBoolean("Johnny", true);
		}
	}

	@Override
	public class_3162.class_3163 method_14123() {
		return this.method_13600() ? class_3162.class_3163.ATTACKING : class_3162.class_3163.CROSSED;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Johnny", 99)) {
			this.field_15072 = nbt.getBoolean("Johnny");
		}
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData entityData, @Nullable NbtCompound nbt) {
		EntityData entityData2 = super.initialize(difficulty, entityData, nbt);
		this.initEquipment(difficulty);
		this.updateEnchantments(difficulty);
		return entityData2;
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		this.method_13598(this.getTarget() != null);
	}

	@Override
	public boolean isTeammate(Entity other) {
		if (super.isTeammate(other)) {
			return true;
		} else {
			return other instanceof LivingEntity && ((LivingEntity)other).method_2647() == class_3462.field_16821
				? this.getScoreboardTeam() == null && other.getScoreboardTeam() == null
				: false;
		}
	}

	@Override
	public void method_15578(@Nullable Text text) {
		super.method_15578(text);
		if (!this.field_15072 && text != null && text.getString().equals("Johnny")) {
			this.field_15072 = true;
		}
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_VINDICATOR_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_VINDICATOR_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_VINDICATOR_HURT;
	}

	static class class_3048 extends FollowTargetGoal<LivingEntity> {
		public class_3048(VindicationIllagerEntity vindicationIllagerEntity) {
			super(vindicationIllagerEntity, LivingEntity.class, 0, true, true, VindicationIllagerEntity.field_17070);
		}

		@Override
		public boolean canStart() {
			return ((VindicationIllagerEntity)this.mob).field_15072 && super.canStart();
		}
	}
}
