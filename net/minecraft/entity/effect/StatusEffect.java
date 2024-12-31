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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class StatusEffect {
	private final Map<EntityAttribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
	private final boolean negative;
	private final int color;
	@Nullable
	private String translationKey;
	private int iconLevel = -1;
	private double field_3161;
	private boolean field_3162;

	@Nullable
	public static StatusEffect byIndex(int index) {
		return Registry.MOB_EFFECT.getByRawId(index);
	}

	public static int getIndex(StatusEffect statusEffect) {
		return Registry.MOB_EFFECT.getRawId(statusEffect);
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
		this.iconLevel = i + j * 12;
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
			} else {
				this.method_6087(livingEntity, i);
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

	protected String computeTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("effect", Registry.MOB_EFFECT.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.computeTranslationKey();
	}

	public Text method_15550() {
		return new TranslatableText(this.getTranslationKey());
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

	protected StatusEffect method_2434(double d) {
		this.field_3161 = d;
		return this;
	}

	public int getColor() {
		return this.color;
	}

	public StatusEffect addAttribute(EntityAttribute entityAttribute, String string, double d, int i) {
		AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString(string), this::getTranslationKey, d, i);
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
		register(
			1,
			"speed",
			new StatusEffect(false, 8171462)
				.method_2440(0, 0)
				.addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, 2)
				.method_12944()
		);
		register(
			2,
			"slowness",
			new StatusEffect(true, 5926017).method_2440(1, 0).addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, 2)
		);
		register(
			3,
			"haste",
			new StatusEffect(false, 14270531)
				.method_2440(2, 0)
				.method_2434(1.5)
				.method_12944()
				.addAttribute(EntityAttributes.GENERIC_ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1F, 2)
		);
		register(
			4,
			"mining_fatigue",
			new StatusEffect(true, 4866583).method_2440(3, 0).addAttribute(EntityAttributes.GENERIC_ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.1F, 2)
		);
		register(
			5,
			"strength",
			new CombatStatusEffect(false, 9643043, 3.0)
				.method_2440(4, 0)
				.addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0, 0)
				.method_12944()
		);
		register(6, "instant_health", new InstantStatusEffect(false, 16262179).method_12944());
		register(7, "instant_damage", new InstantStatusEffect(true, 4393481).method_12944());
		register(8, "jump_boost", new StatusEffect(false, 2293580).method_2440(2, 1).method_12944());
		register(9, "nausea", new StatusEffect(true, 5578058).method_2440(3, 1).method_2434(0.25));
		register(10, "regeneration", new StatusEffect(false, 13458603).method_2440(7, 0).method_2434(0.25).method_12944());
		register(11, "resistance", new StatusEffect(false, 10044730).method_2440(6, 1).method_12944());
		register(12, "fire_resistance", new StatusEffect(false, 14981690).method_2440(7, 1).method_12944());
		register(13, "water_breathing", new StatusEffect(false, 3035801).method_2440(0, 2).method_12944());
		register(14, "invisibility", new StatusEffect(false, 8356754).method_2440(0, 1).method_12944());
		register(15, "blindness", new StatusEffect(true, 2039587).method_2440(5, 1).method_2434(0.25));
		register(16, "night_vision", new StatusEffect(false, 2039713).method_2440(4, 1).method_12944());
		register(17, "hunger", new StatusEffect(true, 5797459).method_2440(1, 1));
		register(
			18,
			"weakness",
			new CombatStatusEffect(true, 4738376, -4.0)
				.method_2440(5, 0)
				.addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0, 0)
		);
		register(19, "poison", new StatusEffect(true, 5149489).method_2440(6, 0).method_2434(0.25));
		register(20, "wither", new StatusEffect(true, 3484199).method_2440(1, 2).method_2434(0.25));
		register(
			21,
			"health_boost",
			new HealthBoostStatusEffect(false, 16284963)
				.method_2440(7, 2)
				.addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, 0)
				.method_12944()
		);
		register(22, "absorption", new AbsorptionStatusEffect(false, 2445989).method_2440(2, 2).method_12944());
		register(23, "saturation", new InstantStatusEffect(false, 16262179).method_12944());
		register(24, "glowing", new StatusEffect(false, 9740385).method_2440(4, 2));
		register(25, "levitation", new StatusEffect(true, 13565951).method_2440(3, 2));
		register(
			26,
			"luck",
			new StatusEffect(false, 3381504)
				.method_2440(5, 2)
				.method_12944()
				.addAttribute(EntityAttributes.GENERIC_LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0, 0)
		);
		register(
			27,
			"unluck",
			new StatusEffect(true, 12624973).method_2440(6, 2).addAttribute(EntityAttributes.GENERIC_LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0, 0)
		);
		register(28, "slow_falling", new StatusEffect(false, 16773073).method_2440(8, 0).method_12944());
		register(29, "conduit_power", new StatusEffect(false, 1950417).method_2440(9, 0).method_12944());
		register(30, "dolphins_grace", new StatusEffect(false, 8954814).method_2440(10, 0).method_12944());
	}

	private static void register(int i, String string, StatusEffect statusEffect) {
		Registry.MOB_EFFECT.set(i, new Identifier(string), statusEffect);
	}
}
