package net.minecraft.entity.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.SimpleRegistry;

public class StatusEffect {
	public static final SimpleRegistry<Identifier, StatusEffect> REGISTRY = new SimpleRegistry<>();
	private final Map<EntityAttribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
	private final boolean negative;
	private final int color;
	private String translationKey = "";
	private int iconLevel = -1;
	private double field_3161;
	private boolean field_3162;

	@Nullable
	public static StatusEffect byIndex(int index) {
		return REGISTRY.getByRawId(index);
	}

	public static int getIndex(StatusEffect statusEffect) {
		return REGISTRY.getRawId(statusEffect);
	}

	@Nullable
	public static StatusEffect get(String id) {
		return REGISTRY.get(new Identifier(id));
	}

	protected StatusEffect(boolean bl, int i) {
		this.negative = bl;
		if (bl) {
			this.field_3161 = 0.5;
		} else {
			this.field_3161 = 1.0;
		}

		this.color = i;
	}

	protected StatusEffect method_2440(int i, int j) {
		this.iconLevel = i + j * 8;
		return this;
	}

	public void method_6087(LivingEntity livingEntity, int i) {
		if (this == StatusEffects.REGENERATION) {
			if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
				livingEntity.heal(1.0F);
			}
		} else if (this == StatusEffects.POISON) {
			if (livingEntity.getHealth() > 1.0F) {
				livingEntity.damage(DamageSource.MAGIC, 1.0F);
			}
		} else if (this == StatusEffects.WITHER) {
			livingEntity.damage(DamageSource.WITHER, 1.0F);
		} else if (this == StatusEffects.HUNGER && livingEntity instanceof PlayerEntity) {
			((PlayerEntity)livingEntity).addExhaustion(0.005F * (float)(i + 1));
		} else if (this == StatusEffects.SATURATION && livingEntity instanceof PlayerEntity) {
			if (!livingEntity.world.isClient) {
				((PlayerEntity)livingEntity).getHungerManager().add(i + 1, 1.0F);
			}
		} else if ((this != StatusEffects.INSTANT_HEALTH || livingEntity.isAffectedBySmite())
			&& (this != StatusEffects.INSTANT_DAMAGE || !livingEntity.isAffectedBySmite())) {
			if (this == StatusEffects.INSTANT_DAMAGE && !livingEntity.isAffectedBySmite() || this == StatusEffects.INSTANT_HEALTH && livingEntity.isAffectedBySmite()) {
				livingEntity.damage(DamageSource.MAGIC, (float)(6 << i));
			}
		} else {
			livingEntity.heal((float)Math.max(4 << i, 0));
		}
	}

	public void method_6088(@Nullable Entity entity, @Nullable Entity entity2, LivingEntity livingEntity, int i, double d) {
		if ((this != StatusEffects.INSTANT_HEALTH || livingEntity.isAffectedBySmite()) && (this != StatusEffects.INSTANT_DAMAGE || !livingEntity.isAffectedBySmite())
			)
		 {
			if (this == StatusEffects.INSTANT_DAMAGE && !livingEntity.isAffectedBySmite() || this == StatusEffects.INSTANT_HEALTH && livingEntity.isAffectedBySmite()) {
				int k = (int)(d * (double)(6 << i) + 0.5);
				if (entity == null) {
					livingEntity.damage(DamageSource.MAGIC, (float)k);
				} else {
					livingEntity.damage(DamageSource.magic(entity, entity2), (float)k);
				}
			}
		} else {
			int j = (int)(d * (double)(4 << i) + 0.5);
			livingEntity.heal((float)j);
		}
	}

	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		if (this == StatusEffects.REGENERATION) {
			int i = 50 >> amplifier;
			return i > 0 ? duration % i == 0 : true;
		} else if (this == StatusEffects.POISON) {
			int j = 25 >> amplifier;
			return j > 0 ? duration % j == 0 : true;
		} else if (this == StatusEffects.WITHER) {
			int k = 40 >> amplifier;
			return k > 0 ? duration % k == 0 : true;
		} else {
			return this == StatusEffects.HUNGER;
		}
	}

	public boolean isInstant() {
		return false;
	}

	public StatusEffect setTranslationKey(String key) {
		this.translationKey = key;
		return this;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public boolean hasIcon() {
		return this.iconLevel >= 0;
	}

	public int getIconLevel() {
		return this.iconLevel;
	}

	public boolean isNegative() {
		return this.negative;
	}

	public static String method_2436(StatusEffectInstance statusEffectInstance, float f) {
		if (statusEffectInstance.isPermanent()) {
			return "**:**";
		} else {
			int i = MathHelper.floor((float)statusEffectInstance.getDuration() * f);
			return ChatUtil.ticksToString(i);
		}
	}

	protected StatusEffect method_2434(double d) {
		this.field_3161 = d;
		return this;
	}

	public int getColor() {
		return this.color;
	}

	public StatusEffect addAttribute(EntityAttribute entityAttribute, String string, double d, int i) {
		AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString(string), this.getTranslationKey(), d, i);
		this.attributeModifiers.put(entityAttribute, attributeModifier);
		return this;
	}

	public Map<EntityAttribute, AttributeModifier> getAttributeModifiers() {
		return this.attributeModifiers;
	}

	public void onRemoved(LivingEntity entity, AbstractEntityAttributeContainer attributes, int amplifier) {
		for (Entry<EntityAttribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = attributes.get((EntityAttribute)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.method_6193((AttributeModifier)entry.getValue());
			}
		}
	}

	public void method_6091(LivingEntity entity, AbstractEntityAttributeContainer attributes, int i) {
		for (Entry<EntityAttribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = attributes.get((EntityAttribute)entry.getKey());
			if (entityAttributeInstance != null) {
				AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
				entityAttributeInstance.method_6193(attributeModifier);
				entityAttributeInstance.addModifier(
					new AttributeModifier(
						attributeModifier.getId(), this.getTranslationKey() + " " + i, this.adjustModifierAmount(i, attributeModifier), attributeModifier.getOperation()
					)
				);
			}
		}
	}

	public double adjustModifierAmount(int amplifier, AttributeModifier modifier) {
		return modifier.getAmount() * (double)(amplifier + 1);
	}

	public boolean method_2448() {
		return this.field_3162;
	}

	public StatusEffect method_12944() {
		this.field_3162 = true;
		return this;
	}

	public static void register() {
		REGISTRY.add(
			1,
			new Identifier("speed"),
			new StatusEffect(false, 8171462)
				.setTranslationKey("effect.moveSpeed")
				.method_2440(0, 0)
				.addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, 2)
				.method_12944()
		);
		REGISTRY.add(
			2,
			new Identifier("slowness"),
			new StatusEffect(true, 5926017)
				.setTranslationKey("effect.moveSlowdown")
				.method_2440(1, 0)
				.addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, 2)
		);
		REGISTRY.add(
			3,
			new Identifier("haste"),
			new StatusEffect(false, 14270531)
				.setTranslationKey("effect.digSpeed")
				.method_2440(2, 0)
				.method_2434(1.5)
				.method_12944()
				.addAttribute(EntityAttributes.GENERIC_ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1F, 2)
		);
		REGISTRY.add(
			4,
			new Identifier("mining_fatigue"),
			new StatusEffect(true, 4866583)
				.setTranslationKey("effect.digSlowDown")
				.method_2440(3, 0)
				.addAttribute(EntityAttributes.GENERIC_ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.1F, 2)
		);
		REGISTRY.add(
			5,
			new Identifier("strength"),
			new CombatStatusEffect(false, 9643043, 3.0)
				.setTranslationKey("effect.damageBoost")
				.method_2440(4, 0)
				.addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0, 0)
				.method_12944()
		);
		REGISTRY.add(6, new Identifier("instant_health"), new InstantStatusEffect(false, 16262179).setTranslationKey("effect.heal").method_12944());
		REGISTRY.add(7, new Identifier("instant_damage"), new InstantStatusEffect(true, 4393481).setTranslationKey("effect.harm").method_12944());
		REGISTRY.add(8, new Identifier("jump_boost"), new StatusEffect(false, 2293580).setTranslationKey("effect.jump").method_2440(2, 1).method_12944());
		REGISTRY.add(9, new Identifier("nausea"), new StatusEffect(true, 5578058).setTranslationKey("effect.confusion").method_2440(3, 1).method_2434(0.25));
		REGISTRY.add(
			10,
			new Identifier("regeneration"),
			new StatusEffect(false, 13458603).setTranslationKey("effect.regeneration").method_2440(7, 0).method_2434(0.25).method_12944()
		);
		REGISTRY.add(11, new Identifier("resistance"), new StatusEffect(false, 10044730).setTranslationKey("effect.resistance").method_2440(6, 1).method_12944());
		REGISTRY.add(
			12, new Identifier("fire_resistance"), new StatusEffect(false, 14981690).setTranslationKey("effect.fireResistance").method_2440(7, 1).method_12944()
		);
		REGISTRY.add(
			13, new Identifier("water_breathing"), new StatusEffect(false, 3035801).setTranslationKey("effect.waterBreathing").method_2440(0, 2).method_12944()
		);
		REGISTRY.add(14, new Identifier("invisibility"), new StatusEffect(false, 8356754).setTranslationKey("effect.invisibility").method_2440(0, 1).method_12944());
		REGISTRY.add(15, new Identifier("blindness"), new StatusEffect(true, 2039587).setTranslationKey("effect.blindness").method_2440(5, 1).method_2434(0.25));
		REGISTRY.add(16, new Identifier("night_vision"), new StatusEffect(false, 2039713).setTranslationKey("effect.nightVision").method_2440(4, 1).method_12944());
		REGISTRY.add(17, new Identifier("hunger"), new StatusEffect(true, 5797459).setTranslationKey("effect.hunger").method_2440(1, 1));
		REGISTRY.add(
			18,
			new Identifier("weakness"),
			new CombatStatusEffect(true, 4738376, -4.0)
				.setTranslationKey("effect.weakness")
				.method_2440(5, 0)
				.addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0, 0)
		);
		REGISTRY.add(19, new Identifier("poison"), new StatusEffect(true, 5149489).setTranslationKey("effect.poison").method_2440(6, 0).method_2434(0.25));
		REGISTRY.add(20, new Identifier("wither"), new StatusEffect(true, 3484199).setTranslationKey("effect.wither").method_2440(1, 2).method_2434(0.25));
		REGISTRY.add(
			21,
			new Identifier("health_boost"),
			new HealthBoostStatusEffect(false, 16284963)
				.setTranslationKey("effect.healthBoost")
				.method_2440(7, 2)
				.addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, 0)
				.method_12944()
		);
		REGISTRY.add(
			22, new Identifier("absorption"), new AbsorptionStatusEffect(false, 2445989).setTranslationKey("effect.absorption").method_2440(2, 2).method_12944()
		);
		REGISTRY.add(23, new Identifier("saturation"), new InstantStatusEffect(false, 16262179).setTranslationKey("effect.saturation").method_12944());
		REGISTRY.add(24, new Identifier("glowing"), new StatusEffect(false, 9740385).setTranslationKey("effect.glowing").method_2440(4, 2));
		REGISTRY.add(25, new Identifier("levitation"), new StatusEffect(true, 13565951).setTranslationKey("effect.levitation").method_2440(3, 2));
		REGISTRY.add(
			26,
			new Identifier("luck"),
			new StatusEffect(false, 3381504)
				.setTranslationKey("effect.luck")
				.method_2440(5, 2)
				.method_12944()
				.addAttribute(EntityAttributes.GENERIC_LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0, 0)
		);
		REGISTRY.add(
			27,
			new Identifier("unluck"),
			new StatusEffect(true, 12624973)
				.setTranslationKey("effect.unluck")
				.method_2440(6, 2)
				.addAttribute(EntityAttributes.GENERIC_LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0, 0)
		);
	}
}
