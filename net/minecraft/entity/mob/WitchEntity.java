package net.minecraft.entity.mob;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WitchEntity extends HostileEntity implements RangedAttackMob {
	private static final UUID DRINKING_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	private static final AttributeModifier field_6924 = new AttributeModifier(DRINKING_SPEED_PENALTY_MODIFIER_ID, "Drinking speed penalty", -0.25, 0)
		.setSerialized(false);
	private static final TrackedData<Boolean> field_14784 = DataTracker.registerData(WitchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int drinkTimeLeft;

	public WitchEntity(World world) {
		super(world);
		this.setBounds(0.6F, 1.95F);
	}

	@Override
	protected void initGoals() {
		this.goals.add(1, new SwimGoal(this));
		this.goals.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0F));
		this.goals.add(2, new WanderAroundGoal(this, 1.0));
		this.goals.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goals.add(3, new LookAroundGoal(this));
		this.attackGoals.add(1, new RevengeGoal(this, false));
		this.attackGoals.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(field_14784, false);
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_WITCH_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_WITCH_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_WITCH_DEATH;
	}

	public void method_4556(boolean bl) {
		this.getDataTracker().set(field_14784, bl);
	}

	public boolean method_13243() {
		return this.getDataTracker().get(field_14784);
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(26.0);
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public void tickMovement() {
		if (!this.world.isClient) {
			if (this.method_13243()) {
				if (this.drinkTimeLeft-- <= 0) {
					this.method_4556(false);
					ItemStack itemStack = this.getMainHandStack();
					this.equipStack(EquipmentSlot.MAINHAND, null);
					if (itemStack != null && itemStack.getItem() == Items.POTION) {
						List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
						if (list != null) {
							for (StatusEffectInstance statusEffectInstance : list) {
								this.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
							}
						}
					}

					this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).method_6193(field_6924);
				}
			} else {
				Potion potion = null;
				if (this.random.nextFloat() < 0.15F && this.isSubmergedIn(Material.WATER) && !this.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
					potion = Potions.WATER_BREATHING;
				} else if (this.random.nextFloat() < 0.15F && this.isOnFire() && !this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
					potion = Potions.FIRE_RESISTANCE;
				} else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
					potion = Potions.HEALING;
				} else if (this.random.nextFloat() < 0.5F
					&& this.getTarget() != null
					&& !this.hasStatusEffect(StatusEffects.SPEED)
					&& this.getTarget().squaredDistanceTo(this) > 121.0) {
					potion = Potions.SWIFTNESS;
				}

				if (potion != null) {
					this.equipStack(EquipmentSlot.MAINHAND, PotionUtil.setPotion(new ItemStack(Items.POTION), potion));
					this.drinkTimeLeft = this.getMainHandStack().getMaxUseTime();
					this.method_4556(true);
					this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
					EntityAttributeInstance entityAttributeInstance = this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED);
					entityAttributeInstance.method_6193(field_6924);
					entityAttributeInstance.addModifier(field_6924);
				}
			}

			if (this.random.nextFloat() < 7.5E-4F) {
				this.world.sendEntityStatus(this, (byte)15);
			}
		}

		super.tickMovement();
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 15) {
			for (int i = 0; i < this.random.nextInt(35) + 10; i++) {
				this.world
					.addParticle(
						ParticleType.WITCH_SPELL,
						this.x + this.random.nextGaussian() * 0.13F,
						this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.13F,
						this.z + this.random.nextGaussian() * 0.13F,
						0.0,
						0.0,
						0.0
					);
			}
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	protected float applyEnchantmentsToDamage(DamageSource source, float amount) {
		amount = super.applyEnchantmentsToDamage(source, amount);
		if (source.getAttacker() == this) {
			amount = 0.0F;
		}

		if (source.getMagic()) {
			amount = (float)((double)amount * 0.15);
		}

		return amount;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.WITCH_ENTITIE;
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		if (!this.method_13243()) {
			double d = target.y + (double)target.getEyeHeight() - 1.1F;
			double e = target.x + target.velocityX - this.x;
			double f = d - this.y;
			double g = target.z + target.velocityZ - this.z;
			float h = MathHelper.sqrt(e * e + g * g);
			Potion potion = Potions.HARMING;
			if (h >= 8.0F && !target.hasStatusEffect(StatusEffects.SLOWNESS)) {
				potion = Potions.SLOWNESS;
			} else if (target.getHealth() >= 8.0F && !target.hasStatusEffect(StatusEffects.POISON)) {
				potion = Potions.POISON;
			} else if (h <= 3.0F && !target.hasStatusEffect(StatusEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
				potion = Potions.WEAKNESS;
			}

			PotionEntity potionEntity = new PotionEntity(this.world, this, PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
			potionEntity.pitch -= -20.0F;
			potionEntity.setVelocity(e, f + (double)(h * 0.2F), g, 0.75F, 8.0F);
			this.world.playSound(null, this.x, this.y, this.z, Sounds.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
			this.world.spawnEntity(potionEntity);
		}
	}

	@Override
	public float getEyeHeight() {
		return 1.62F;
	}
}
