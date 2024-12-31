package net.minecraft.entity.effect;

import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StatusEffects {
	public static final StatusEffect SPEED;
	public static final StatusEffect SLOWNESS;
	public static final StatusEffect HASTE;
	public static final StatusEffect MINING_FATIGUE;
	public static final StatusEffect STRENGTH;
	public static final StatusEffect INSTANT_HEALTH;
	public static final StatusEffect INSTANT_DAMAGE;
	public static final StatusEffect JUMP_BOOST;
	public static final StatusEffect NAUSEA;
	public static final StatusEffect REGENERATION;
	public static final StatusEffect RESISTANCE;
	public static final StatusEffect FIRE_RESISTANCE;
	public static final StatusEffect WATER_BREATHING;
	public static final StatusEffect INVISIBILITY;
	public static final StatusEffect BLINDNESS;
	public static final StatusEffect NIGHT_VISION;
	public static final StatusEffect HUNGER;
	public static final StatusEffect WEAKNESS;
	public static final StatusEffect POISON;
	public static final StatusEffect WITHER;
	public static final StatusEffect HEALTH_BOOST;
	public static final StatusEffect ABSORPTION;
	public static final StatusEffect SATURATION;
	public static final StatusEffect GLOWING;
	public static final StatusEffect LEVITATION;
	public static final StatusEffect LUCK;
	public static final StatusEffect UNLUCK;
	public static final StatusEffect SLOW_FALLING;
	public static final StatusEffect CONDUIT_POWER;
	public static final StatusEffect DOLPHINS_GRACE;

	@Nullable
	private static StatusEffect get(String id) {
		StatusEffect statusEffect = Registry.MOB_EFFECT.getByIdentifier(new Identifier(id));
		if (statusEffect == null) {
			throw new IllegalStateException("Invalid MobEffect requested: " + id);
		} else {
			return statusEffect;
		}
	}

	static {
		if (!Bootstrap.isInitialized()) {
			throw new RuntimeException("Accessed MobEffects before Bootstrap!");
		} else {
			SPEED = get("speed");
			SLOWNESS = get("slowness");
			HASTE = get("haste");
			MINING_FATIGUE = get("mining_fatigue");
			STRENGTH = get("strength");
			INSTANT_HEALTH = get("instant_health");
			INSTANT_DAMAGE = get("instant_damage");
			JUMP_BOOST = get("jump_boost");
			NAUSEA = get("nausea");
			REGENERATION = get("regeneration");
			RESISTANCE = get("resistance");
			FIRE_RESISTANCE = get("fire_resistance");
			WATER_BREATHING = get("water_breathing");
			INVISIBILITY = get("invisibility");
			BLINDNESS = get("blindness");
			NIGHT_VISION = get("night_vision");
			HUNGER = get("hunger");
			WEAKNESS = get("weakness");
			POISON = get("poison");
			WITHER = get("wither");
			HEALTH_BOOST = get("health_boost");
			ABSORPTION = get("absorption");
			SATURATION = get("saturation");
			GLOWING = get("glowing");
			LEVITATION = get("levitation");
			LUCK = get("luck");
			UNLUCK = get("unluck");
			SLOW_FALLING = get("slow_falling");
			CONDUIT_POWER = get("conduit_power");
			DOLPHINS_GRACE = get("dolphins_grace");
		}
	}
}
