package net.minecraft.enchantment;

import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Enchantments {
	public static final Enchantment PROTECTION = get("protection");
	public static final Enchantment FIRE_PROTECTION = get("fire_protection");
	public static final Enchantment FEATHER_FALLING = get("feather_falling");
	public static final Enchantment BLAST_PROTECTION = get("blast_protection");
	public static final Enchantment PROJECTILE_PROTECTION = get("projectile_protection");
	public static final Enchantment RESPIRATION = get("respiration");
	public static final Enchantment AQUA_AFFINITY = get("aqua_affinity");
	public static final Enchantment THORNS = get("thorns");
	public static final Enchantment DEPTH_STRIDER = get("depth_strider");
	public static final Enchantment FROST_WALKER = get("frost_walker");
	public static final Enchantment BINDING_CURSE = get("binding_curse");
	public static final Enchantment SHARPNESS = get("sharpness");
	public static final Enchantment SMITE = get("smite");
	public static final Enchantment BANE_OF_ARTHROPODS = get("bane_of_arthropods");
	public static final Enchantment KNOCKBACK = get("knockback");
	public static final Enchantment FIRE_ASPECT = get("fire_aspect");
	public static final Enchantment LOOTING = get("looting");
	public static final Enchantment SWEEPING = get("sweeping");
	public static final Enchantment EFFICIENCY = get("efficiency");
	public static final Enchantment SILK_TOUCH = get("silk_touch");
	public static final Enchantment UNBREAKING = get("unbreaking");
	public static final Enchantment FORTUNE = get("fortune");
	public static final Enchantment POWER = get("power");
	public static final Enchantment PUNCH = get("punch");
	public static final Enchantment FLAME = get("flame");
	public static final Enchantment INFINITY = get("infinity");
	public static final Enchantment LUCK_OF_THE_SEA = get("luck_of_the_sea");
	public static final Enchantment LURE = get("lure");
	public static final Enchantment LOYALTY = get("loyalty");
	public static final Enchantment IMPALING = get("impaling");
	public static final Enchantment RIPTIDE = get("riptide");
	public static final Enchantment CHANNELING = get("channeling");
	public static final Enchantment MENDING = get("mending");
	public static final Enchantment VANISHING_CURSE = get("vanishing_curse");

	@Nullable
	private static Enchantment get(String id) {
		Enchantment enchantment = Registry.ENCHANTMENT.getByIdentifier(new Identifier(id));
		if (enchantment == null) {
			throw new IllegalStateException("Invalid Enchantment requested: " + id);
		} else {
			return enchantment;
		}
	}

	static {
		if (!Bootstrap.isInitialized()) {
			throw new RuntimeException("Accessed Enchantments before Bootstrap!");
		}
	}
}
