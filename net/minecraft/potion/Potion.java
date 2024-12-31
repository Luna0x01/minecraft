package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BiDefaultedRegistry;

public class Potion {
	private static final Identifier field_12335 = new Identifier("water");
	public static final BiDefaultedRegistry<Identifier, Potion> REGISTRY = new BiDefaultedRegistry<>(field_12335);
	private static int field_12336;
	private final String id;
	private final ImmutableList<StatusEffectInstance> effects;

	@Nullable
	public static Potion byIndex(int index) {
		return REGISTRY.getByRawId(index);
	}

	public static int getId(Potion potion) {
		return REGISTRY.getRawId(potion);
	}

	@Nullable
	public static Potion get(String id) {
		return REGISTRY.get(new Identifier(id));
	}

	public Potion(StatusEffectInstance... statusEffectInstances) {
		this(null, statusEffectInstances);
	}

	public Potion(@Nullable String string, StatusEffectInstance... statusEffectInstances) {
		this.id = string;
		this.effects = ImmutableList.copyOf(statusEffectInstances);
	}

	public String method_11414(String id) {
		return this.id == null ? id + REGISTRY.getIdentifier(this).getPath() : id + this.id;
	}

	public List<StatusEffectInstance> getEffects() {
		return this.effects;
	}

	public static void register() {
		register("empty", new Potion());
		register("water", new Potion());
		register("mundane", new Potion());
		register("thick", new Potion());
		register("awkward", new Potion());
		register("night_vision", new Potion(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 3600)));
		register("long_night_vision", new Potion("night_vision", new StatusEffectInstance(StatusEffects.NIGHT_VISION, 9600)));
		register("invisibility", new Potion(new StatusEffectInstance(StatusEffects.INVISIBILITY, 3600)));
		register("long_invisibility", new Potion("invisibility", new StatusEffectInstance(StatusEffects.INVISIBILITY, 9600)));
		register("leaping", new Potion(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600)));
		register("long_leaping", new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, 9600)));
		register("strong_leaping", new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, 1800, 1)));
		register("fire_resistance", new Potion(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3600)));
		register("long_fire_resistance", new Potion("fire_resistance", new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 9600)));
		register("swiftness", new Potion(new StatusEffectInstance(StatusEffects.SPEED, 3600)));
		register("long_swiftness", new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, 9600)));
		register("strong_swiftness", new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, 1800, 1)));
		register("slowness", new Potion(new StatusEffectInstance(StatusEffects.SLOWNESS, 1800)));
		register("long_slowness", new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, 4800)));
		register("water_breathing", new Potion(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 3600)));
		register("long_water_breathing", new Potion("water_breathing", new StatusEffectInstance(StatusEffects.WATER_BREATHING, 9600)));
		register("healing", new Potion(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1)));
		register("strong_healing", new Potion("healing", new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 1)));
		register("harming", new Potion(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1)));
		register("strong_harming", new Potion("harming", new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1)));
		register("poison", new Potion(new StatusEffectInstance(StatusEffects.POISON, 900)));
		register("long_poison", new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, 1800)));
		register("strong_poison", new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, 432, 1)));
		register("regeneration", new Potion(new StatusEffectInstance(StatusEffects.REGENERATION, 900)));
		register("long_regeneration", new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, 1800)));
		register("strong_regeneration", new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, 450, 1)));
		register("strength", new Potion(new StatusEffectInstance(StatusEffects.STRENGTH, 3600)));
		register("long_strength", new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, 9600)));
		register("strong_strength", new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, 1800, 1)));
		register("weakness", new Potion(new StatusEffectInstance(StatusEffects.WEAKNESS, 1800)));
		register("long_weakness", new Potion("weakness", new StatusEffectInstance(StatusEffects.WEAKNESS, 4800)));
		register("luck", new Potion("luck", new StatusEffectInstance(StatusEffects.LUCK, 6000)));
		REGISTRY.validate();
	}

	protected static void register(String id, Potion potion) {
		REGISTRY.add(field_12336++, new Identifier(id), potion);
	}

	public boolean method_11415() {
		if (!this.effects.isEmpty()) {
			for (StatusEffectInstance statusEffectInstance : this.effects) {
				if (statusEffectInstance.getStatusEffect().isInstant()) {
					return true;
				}
			}
		}

		return false;
	}
}
