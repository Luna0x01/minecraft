package net.minecraft.entity.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
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

public class StatusEffect {
	public static final StatusEffect[] STATUS_EFFECTS = new StatusEffect[32];
	private static final Map<Identifier, StatusEffect> STATUS_EFFECTS_BY_ID = Maps.newHashMap();
	public static final StatusEffect UNKNOWN = null;
	public static final StatusEffect SPEED = new StatusEffect(1, new Identifier("speed"), false, 8171462)
		.setTranslationKey("potion.moveSpeed")
		.method_2440(0, 0)
		.addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, 2);
	public static final StatusEffect SLOWNESS = new StatusEffect(2, new Identifier("slowness"), true, 5926017)
		.setTranslationKey("potion.moveSlowdown")
		.method_2440(1, 0)
		.addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, 2);
	public static final StatusEffect HASTE = new StatusEffect(3, new Identifier("haste"), false, 14270531)
		.setTranslationKey("potion.digSpeed")
		.method_2440(2, 0)
		.method_2434(1.5);
	public static final StatusEffect MINING_FATIGUE = new StatusEffect(4, new Identifier("mining_fatigue"), true, 4866583)
		.setTranslationKey("potion.digSlowDown")
		.method_2440(3, 0);
	public static final StatusEffect STRENGTH = new CombatStatusEffect(5, new Identifier("strength"), false, 9643043)
		.setTranslationKey("potion.damageBoost")
		.method_2440(4, 0)
		.addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5, 2);
	public static final StatusEffect INSTANT_HEALTH = new InstantStatusEffect(6, new Identifier("instant_health"), false, 16262179)
		.setTranslationKey("potion.heal");
	public static final StatusEffect INSTANT_DAMAGE = new InstantStatusEffect(7, new Identifier("instant_damage"), true, 4393481).setTranslationKey("potion.harm");
	public static final StatusEffect JUMP_BOOST = new StatusEffect(8, new Identifier("jump_boost"), false, 2293580)
		.setTranslationKey("potion.jump")
		.method_2440(2, 1);
	public static final StatusEffect NAUSEA = new StatusEffect(9, new Identifier("nausea"), true, 5578058)
		.setTranslationKey("potion.confusion")
		.method_2440(3, 1)
		.method_2434(0.25);
	public static final StatusEffect REGENERATION = new StatusEffect(10, new Identifier("regeneration"), false, 13458603)
		.setTranslationKey("potion.regeneration")
		.method_2440(7, 0)
		.method_2434(0.25);
	public static final StatusEffect RESISTANCE = new StatusEffect(11, new Identifier("resistance"), false, 10044730)
		.setTranslationKey("potion.resistance")
		.method_2440(6, 1);
	public static final StatusEffect FIRE_RESISTANCE = new StatusEffect(12, new Identifier("fire_resistance"), false, 14981690)
		.setTranslationKey("potion.fireResistance")
		.method_2440(7, 1);
	public static final StatusEffect WATER_BREATHING = new StatusEffect(13, new Identifier("water_breathing"), false, 3035801)
		.setTranslationKey("potion.waterBreathing")
		.method_2440(0, 2);
	public static final StatusEffect INVISIBILITY = new StatusEffect(14, new Identifier("invisibility"), false, 8356754)
		.setTranslationKey("potion.invisibility")
		.method_2440(0, 1);
	public static final StatusEffect BLINDNESS = new StatusEffect(15, new Identifier("blindness"), true, 2039587)
		.setTranslationKey("potion.blindness")
		.method_2440(5, 1)
		.method_2434(0.25);
	public static final StatusEffect NIGHTVISION = new StatusEffect(16, new Identifier("night_vision"), false, 2039713)
		.setTranslationKey("potion.nightVision")
		.method_2440(4, 1);
	public static final StatusEffect HUNGER = new StatusEffect(17, new Identifier("hunger"), true, 5797459).setTranslationKey("potion.hunger").method_2440(1, 1);
	public static final StatusEffect WEAKNESS = new CombatStatusEffect(18, new Identifier("weakness"), true, 4738376)
		.setTranslationKey("potion.weakness")
		.method_2440(5, 0)
		.addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0, 0);
	public static final StatusEffect POISON = new StatusEffect(19, new Identifier("poison"), true, 5149489)
		.setTranslationKey("potion.poison")
		.method_2440(6, 0)
		.method_2434(0.25);
	public static final StatusEffect WITHER = new StatusEffect(20, new Identifier("wither"), true, 3484199)
		.setTranslationKey("potion.wither")
		.method_2440(1, 2)
		.method_2434(0.25);
	public static final StatusEffect HEALTH_BOOST = new HealthBoostStatusEffect(21, new Identifier("health_boost"), false, 16284963)
		.setTranslationKey("potion.healthBoost")
		.method_2440(2, 2)
		.addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, 0);
	public static final StatusEffect ABSORPTION = new AbsorptionStatusEffect(22, new Identifier("absorption"), false, 2445989)
		.setTranslationKey("potion.absorption")
		.method_2440(2, 2);
	public static final StatusEffect SATURATION = new InstantStatusEffect(23, new Identifier("saturation"), false, 16262179)
		.setTranslationKey("potion.saturation");
	public static final StatusEffect field_3189 = null;
	public static final StatusEffect field_3150 = null;
	public static final StatusEffect field_3151 = null;
	public static final StatusEffect field_3152 = null;
	public static final StatusEffect field_3153 = null;
	public static final StatusEffect field_3154 = null;
	public static final StatusEffect field_3155 = null;
	public static final StatusEffect field_3156 = null;
	public final int id;
	private final Map<EntityAttribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
	private final boolean negative;
	private final int color;
	private String translationKey = "";
	private int iconLevel = -1;
	private double field_3161;
	private boolean field_3162;

	protected StatusEffect(int i, Identifier identifier, boolean bl, int j) {
		this.id = i;
		STATUS_EFFECTS[i] = this;
		STATUS_EFFECTS_BY_ID.put(identifier, this);
		this.negative = bl;
		if (bl) {
			this.field_3161 = 0.5;
		} else {
			this.field_3161 = 1.0;
		}

		this.color = j;
	}

	public static StatusEffect get(String id) {
		return (StatusEffect)STATUS_EFFECTS_BY_ID.get(new Identifier(id));
	}

	public static Set<Identifier> method_10923() {
		return STATUS_EFFECTS_BY_ID.keySet();
	}

	protected StatusEffect method_2440(int i, int j) {
		this.iconLevel = i + j * 8;
		return this;
	}

	public int getId() {
		return this.id;
	}

	public void method_6087(LivingEntity livingEntity, int i) {
		if (this.id == REGENERATION.id) {
			if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
				livingEntity.heal(1.0F);
			}
		} else if (this.id == POISON.id) {
			if (livingEntity.getHealth() > 1.0F) {
				livingEntity.damage(DamageSource.MAGIC, 1.0F);
			}
		} else if (this.id == WITHER.id) {
			livingEntity.damage(DamageSource.WITHER, 1.0F);
		} else if (this.id == HUNGER.id && livingEntity instanceof PlayerEntity) {
			((PlayerEntity)livingEntity).addExhaustion(0.025F * (float)(i + 1));
		} else if (this.id == SATURATION.id && livingEntity instanceof PlayerEntity) {
			if (!livingEntity.world.isClient) {
				((PlayerEntity)livingEntity).getHungerManager().add(i + 1, 1.0F);
			}
		} else if ((this.id != INSTANT_HEALTH.id || livingEntity.isAffectedBySmite()) && (this.id != INSTANT_DAMAGE.id || !livingEntity.isAffectedBySmite())) {
			if (this.id == INSTANT_DAMAGE.id && !livingEntity.isAffectedBySmite() || this.id == INSTANT_HEALTH.id && livingEntity.isAffectedBySmite()) {
				livingEntity.damage(DamageSource.MAGIC, (float)(6 << i));
			}
		} else {
			livingEntity.heal((float)Math.max(4 << i, 0));
		}
	}

	public void method_6088(Entity entity, Entity entity2, LivingEntity livingEntity, int i, double d) {
		if ((this.id != INSTANT_HEALTH.id || livingEntity.isAffectedBySmite()) && (this.id != INSTANT_DAMAGE.id || !livingEntity.isAffectedBySmite())) {
			if (this.id == INSTANT_DAMAGE.id && !livingEntity.isAffectedBySmite() || this.id == INSTANT_HEALTH.id && livingEntity.isAffectedBySmite()) {
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

	public boolean isInstant() {
		return false;
	}

	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		if (this.id == REGENERATION.id) {
			int i = 50 >> amplifier;
			return i > 0 ? duration % i == 0 : true;
		} else if (this.id == POISON.id) {
			int j = 25 >> amplifier;
			return j > 0 ? duration % j == 0 : true;
		} else if (this.id == WITHER.id) {
			int k = 40 >> amplifier;
			return k > 0 ? duration % k == 0 : true;
		} else {
			return this.id == HUNGER.id;
		}
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

	public static String getFormattedDuration(StatusEffectInstance effect) {
		if (effect.isPermanent()) {
			return "**:**";
		} else {
			int i = effect.getDuration();
			return ChatUtil.ticksToString(i);
		}
	}

	protected StatusEffect method_2434(double d) {
		this.field_3161 = d;
		return this;
	}

	public double method_2446() {
		return this.field_3161;
	}

	public boolean method_2448() {
		return this.field_3162;
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
}
