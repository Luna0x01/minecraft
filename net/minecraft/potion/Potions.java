package net.minecraft.potion;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.Bootstrap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Potions {
	private static final Set<Potion> POTIONS;
	public static final Potion EMPTY;
	public static final Potion WATER;
	public static final Potion MUNDANE;
	public static final Potion THICK;
	public static final Potion AWKWARD;
	public static final Potion NIGHT_VISION;
	public static final Potion LONG_NIGHT_VISION;
	public static final Potion INVISIBILITY;
	public static final Potion LONG_INVISIBILITY;
	public static final Potion LEAPING;
	public static final Potion LONG_LEAPING;
	public static final Potion STRONG_LEAPING;
	public static final Potion FIRE_RESISTANCE;
	public static final Potion LONG_FIRE_RESISTANCE;
	public static final Potion SWIFTNESS;
	public static final Potion LONG_SWIFTNESS;
	public static final Potion STRONG_SWIFTNESS;
	public static final Potion SLOWNESS;
	public static final Potion LONG_SLOWNESS;
	public static final Potion STRONG_SLOWNESS;
	public static final Potion TURTLE_MASTER;
	public static final Potion LONG_TURTLE_MASTER;
	public static final Potion STRONG_TURTLE_MASTER;
	public static final Potion WATER_BREATHING;
	public static final Potion LONG_WATER_BREATHING;
	public static final Potion HEALING;
	public static final Potion STRONG_HEALING;
	public static final Potion HARMING;
	public static final Potion STRONG_HARMING;
	public static final Potion POISON;
	public static final Potion LONG_POISON;
	public static final Potion STRONG_POISON;
	public static final Potion REGENERATION;
	public static final Potion LONG_REGENERATION;
	public static final Potion STRONG_REGENERATION;
	public static final Potion STRENGTH;
	public static final Potion LONG_STRENGTH;
	public static final Potion STRONG_STRENGTH;
	public static final Potion WEAKNESS;
	public static final Potion LONG_WEAKNESS;
	public static final Potion SLOW_FALLING;
	public static final Potion LONG_SLOW_FALLING;

	private static Potion get(String id) {
		Potion potion = Registry.POTION.get(new Identifier(id));
		if (!POTIONS.add(potion)) {
			throw new IllegalStateException("Invalid Potion requested: " + id);
		} else {
			return potion;
		}
	}

	static {
		if (!Bootstrap.isInitialized()) {
			throw new RuntimeException("Accessed Potions before Bootstrap!");
		} else {
			POTIONS = Sets.newHashSet(new Potion[]{(Potion)null});
			EMPTY = get("empty");
			WATER = get("water");
			MUNDANE = get("mundane");
			THICK = get("thick");
			AWKWARD = get("awkward");
			NIGHT_VISION = get("night_vision");
			LONG_NIGHT_VISION = get("long_night_vision");
			INVISIBILITY = get("invisibility");
			LONG_INVISIBILITY = get("long_invisibility");
			LEAPING = get("leaping");
			LONG_LEAPING = get("long_leaping");
			STRONG_LEAPING = get("strong_leaping");
			FIRE_RESISTANCE = get("fire_resistance");
			LONG_FIRE_RESISTANCE = get("long_fire_resistance");
			SWIFTNESS = get("swiftness");
			LONG_SWIFTNESS = get("long_swiftness");
			STRONG_SWIFTNESS = get("strong_swiftness");
			SLOWNESS = get("slowness");
			LONG_SLOWNESS = get("long_slowness");
			STRONG_SLOWNESS = get("strong_slowness");
			TURTLE_MASTER = get("turtle_master");
			LONG_TURTLE_MASTER = get("long_turtle_master");
			STRONG_TURTLE_MASTER = get("strong_turtle_master");
			WATER_BREATHING = get("water_breathing");
			LONG_WATER_BREATHING = get("long_water_breathing");
			HEALING = get("healing");
			STRONG_HEALING = get("strong_healing");
			HARMING = get("harming");
			STRONG_HARMING = get("strong_harming");
			POISON = get("poison");
			LONG_POISON = get("long_poison");
			STRONG_POISON = get("strong_poison");
			REGENERATION = get("regeneration");
			LONG_REGENERATION = get("long_regeneration");
			STRONG_REGENERATION = get("strong_regeneration");
			STRENGTH = get("strength");
			LONG_STRENGTH = get("long_strength");
			STRONG_STRENGTH = get("strong_strength");
			WEAKNESS = get("weakness");
			LONG_WEAKNESS = get("long_weakness");
			SLOW_FALLING = get("slow_falling");
			LONG_SLOW_FALLING = get("long_slow_falling");
			POTIONS.clear();
		}
	}
}
