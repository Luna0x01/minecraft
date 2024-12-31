package net.minecraft;

import net.minecraft.client.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4342 {
	public static final class_4343 field_21375;
	public static final class_4343 field_21376;
	public static final class_4343 field_21377;
	public static final ParticleType<class_4337> BLOCK;
	public static final class_4343 field_21379;
	public static final class_4343 field_21380;
	public static final class_4343 field_21381;
	public static final class_4343 field_21382;
	public static final class_4343 field_21383;
	public static final class_4343 field_21384;
	public static final class_4343 field_21385;
	public static final class_4343 field_21386;
	public static final ParticleType<class_4338> DUST;
	public static final class_4343 field_21388;
	public static final class_4343 field_21389;
	public static final class_4343 field_21390;
	public static final class_4343 field_21391;
	public static final class_4343 field_21392;
	public static final class_4343 field_21393;
	public static final class_4343 field_21394;
	public static final class_4343 field_21395;
	public static final ParticleType<class_4337> FALLING_DUST;
	public static final class_4343 field_21397;
	public static final class_4343 field_21398;
	public static final class_4343 field_21399;
	public static final class_4343 field_21400;
	public static final class_4343 field_21351;
	public static final class_4343 field_21352;
	public static final ParticleType<class_4339> ITEM;
	public static final class_4343 field_21354;
	public static final class_4343 field_21355;
	public static final class_4343 field_21356;
	public static final class_4343 field_21357;
	public static final class_4343 field_21358;
	public static final class_4343 field_21359;
	public static final class_4343 field_21360;
	public static final class_4343 field_21361;
	public static final class_4343 field_21362;
	public static final class_4343 field_21363;
	public static final class_4343 field_21364;
	public static final class_4343 field_21365;
	public static final class_4343 field_21366;
	public static final class_4343 field_21367;
	public static final class_4343 field_21368;
	public static final class_4343 field_21369;
	public static final class_4343 field_21370;
	public static final class_4343 field_21371;
	public static final class_4343 field_21372;
	public static final class_4343 field_21373;
	public static final class_4343 field_21374;

	private static <T extends ParticleType<?>> T method_19988(String string) {
		T particleType = (T)Registry.PARTICLE_TYPE.getByIdentifier(new Identifier(string));
		if (particleType == null) {
			throw new IllegalStateException("Invalid or unknown particle type: " + string);
		} else {
			return particleType;
		}
	}

	static {
		if (!Bootstrap.isInitialized()) {
			throw new RuntimeException("Accessed particles before Bootstrap!");
		} else {
			field_21375 = method_19988("ambient_entity_effect");
			field_21376 = method_19988("angry_villager");
			field_21377 = method_19988("barrier");
			BLOCK = method_19988("block");
			field_21379 = method_19988("bubble");
			field_21380 = method_19988("bubble_column_up");
			field_21381 = method_19988("cloud");
			field_21382 = method_19988("crit");
			field_21383 = method_19988("damage_indicator");
			field_21384 = method_19988("dragon_breath");
			field_21385 = method_19988("dripping_lava");
			field_21386 = method_19988("dripping_water");
			DUST = method_19988("dust");
			field_21388 = method_19988("effect");
			field_21389 = method_19988("elder_guardian");
			field_21390 = method_19988("enchanted_hit");
			field_21391 = method_19988("enchant");
			field_21392 = method_19988("end_rod");
			field_21393 = method_19988("entity_effect");
			field_21394 = method_19988("explosion_emitter");
			field_21395 = method_19988("explosion");
			FALLING_DUST = method_19988("falling_dust");
			field_21397 = method_19988("firework");
			field_21398 = method_19988("fishing");
			field_21399 = method_19988("flame");
			field_21400 = method_19988("happy_villager");
			field_21351 = method_19988("heart");
			field_21352 = method_19988("instant_effect");
			ITEM = method_19988("item");
			field_21354 = method_19988("item_slime");
			field_21355 = method_19988("item_snowball");
			field_21356 = method_19988("large_smoke");
			field_21357 = method_19988("lava");
			field_21358 = method_19988("mycelium");
			field_21359 = method_19988("note");
			field_21360 = method_19988("poof");
			field_21361 = method_19988("portal");
			field_21362 = method_19988("rain");
			field_21363 = method_19988("smoke");
			field_21364 = method_19988("spit");
			field_21365 = method_19988("sweep_attack");
			field_21366 = method_19988("totem_of_undying");
			field_21367 = method_19988("underwater");
			field_21368 = method_19988("splash");
			field_21369 = method_19988("witch");
			field_21370 = method_19988("bubble_pop");
			field_21371 = method_19988("current_down");
			field_21372 = method_19988("squid_ink");
			field_21373 = method_19988("nautilus");
			field_21374 = method_19988("dolphin");
		}
	}
}
