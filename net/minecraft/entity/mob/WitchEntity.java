package net.minecraft.entity.mob;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WitchEntity extends HostileEntity implements RangedAttackMob {
	private static final UUID DRINKING_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	private static final AttributeModifier field_6924 = new AttributeModifier(DRINKING_SPEED_PENALTY_MODIFIER_ID, "Drinking speed penalty", -0.25, 0)
		.setSerialized(false);
	private static final Item[] field_9127 = new Item[]{
		Items.GLOWSTONE_DUST, Items.SUGAR, Items.REDSTONE, Items.SPIDER_EYE, Items.GLASS_BOTTLE, Items.GUNPOWDER, Items.STICK, Items.STICK
	};
	private int drinkTimeLeft;

	public WitchEntity(World world) {
		super(world);
		this.setBounds(0.6F, 1.95F);
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
		this.getDataTracker().track(21, (byte)0);
	}

	@Override
	protected String getAmbientSound() {
		return null;
	}

	@Override
	protected String getHurtSound() {
		return null;
	}

	@Override
	protected String getDeathSound() {
		return null;
	}

	public void method_4556(boolean bl) {
		this.getDataTracker().setProperty(21, Byte.valueOf((byte)(bl ? 1 : 0)));
	}

	public boolean method_4557() {
		return this.getDataTracker().getByte(21) == 1;
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
			if (this.method_4557()) {
				if (this.drinkTimeLeft-- <= 0) {
					this.method_4556(false);
					ItemStack itemStack = this.getStackInHand();
					this.setArmorSlot(0, null);
					if (itemStack != null && itemStack.getItem() == Items.POTION) {
						List<StatusEffectInstance> list = Items.POTION.getCustomPotionEffects(itemStack);
						if (list != null) {
							for (StatusEffectInstance statusEffectInstance : list) {
								this.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
							}
						}
					}

					this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).method_6193(field_6924);
				}
			} else {
				int i = -1;
				if (this.random.nextFloat() < 0.15F && this.isSubmergedIn(Material.WATER) && !this.hasStatusEffect(StatusEffect.WATER_BREATHING)) {
					i = 8237;
				} else if (this.random.nextFloat() < 0.15F && this.isOnFire() && !this.hasStatusEffect(StatusEffect.FIRE_RESISTANCE)) {
					i = 16307;
				} else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
					i = 16341;
				} else if (this.random.nextFloat() < 0.25F
					&& this.getTarget() != null
					&& !this.hasStatusEffect(StatusEffect.SPEED)
					&& this.getTarget().squaredDistanceTo(this) > 121.0) {
					i = 16274;
				} else if (this.random.nextFloat() < 0.25F
					&& this.getTarget() != null
					&& !this.hasStatusEffect(StatusEffect.SPEED)
					&& this.getTarget().squaredDistanceTo(this) > 121.0) {
					i = 16274;
				}

				if (i > -1) {
					this.setArmorSlot(0, new ItemStack(Items.POTION, 1, i));
					this.drinkTimeLeft = this.getStackInHand().getMaxUseTime();
					this.method_4556(true);
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

	@Override
	protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
		int i = this.random.nextInt(3) + 1;

		for (int j = 0; j < i; j++) {
			int k = this.random.nextInt(3);
			Item item = field_9127[this.random.nextInt(field_9127.length)];
			if (lootingMultiplier > 0) {
				k += this.random.nextInt(lootingMultiplier + 1);
			}

			for (int l = 0; l < k; l++) {
				this.dropItem(item, 1);
			}
		}
	}

	@Override
	public void rangedAttack(LivingEntity target, float pullProgress) {
		if (!this.method_4557()) {
			PotionEntity potionEntity = new PotionEntity(this.world, this, 32732);
			double d = target.y + (double)target.getEyeHeight() - 1.1F;
			potionEntity.pitch -= -20.0F;
			double e = target.x + target.velocityX - this.x;
			double f = d - this.y;
			double g = target.z + target.velocityZ - this.z;
			float h = MathHelper.sqrt(e * e + g * g);
			if (h >= 8.0F && !target.hasStatusEffect(StatusEffect.SLOWNESS)) {
				potionEntity.setPotionValue(32698);
			} else if (target.getHealth() >= 8.0F && !target.hasStatusEffect(StatusEffect.POISON)) {
				potionEntity.setPotionValue(32660);
			} else if (h <= 3.0F && !target.hasStatusEffect(StatusEffect.WEAKNESS) && this.random.nextFloat() < 0.25F) {
				potionEntity.setPotionValue(32696);
			}

			potionEntity.setVelocity(e, f + (double)(h * 0.2F), g, 0.75F, 8.0F);
			this.world.spawnEntity(potionEntity);
		}
	}

	@Override
	public float getEyeHeight() {
		return 1.62F;
	}
}
