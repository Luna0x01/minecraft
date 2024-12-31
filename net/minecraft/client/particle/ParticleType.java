package net.minecraft.client.particle;

import net.minecraft.class_4337;
import net.minecraft.class_4338;
import net.minecraft.class_4339;
import net.minecraft.class_4343;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleType<T extends ParticleEffect> {
	private final Identifier field_21348;
	private final boolean field_21349;
	private final ParticleEffect.class_4341<T> field_21350;

	protected ParticleType(Identifier identifier, boolean bl, ParticleEffect.class_4341<T> arg) {
		this.field_21348 = identifier;
		this.field_21349 = bl;
		this.field_21350 = arg;
	}

	public static void method_19985() {
		method_19983("ambient_entity_effect", false);
		method_19983("angry_villager", false);
		method_19983("barrier", false);
		method_19984("block", false, class_4337.field_21336);
		method_19983("bubble", false);
		method_19983("cloud", false);
		method_19983("crit", false);
		method_19983("damage_indicator", true);
		method_19983("dragon_breath", false);
		method_19983("dripping_lava", false);
		method_19983("dripping_water", false);
		method_19984("dust", false, class_4338.field_21340);
		method_19983("effect", false);
		method_19983("elder_guardian", true);
		method_19983("enchanted_hit", false);
		method_19983("enchant", false);
		method_19983("end_rod", false);
		method_19983("entity_effect", false);
		method_19983("explosion_emitter", true);
		method_19983("explosion", true);
		method_19984("falling_dust", false, class_4337.field_21336);
		method_19983("firework", false);
		method_19983("fishing", false);
		method_19983("flame", false);
		method_19983("happy_villager", false);
		method_19983("heart", false);
		method_19983("instant_effect", false);
		method_19984("item", false, class_4339.field_21345);
		method_19983("item_slime", false);
		method_19983("item_snowball", false);
		method_19983("large_smoke", false);
		method_19983("lava", false);
		method_19983("mycelium", false);
		method_19983("note", false);
		method_19983("poof", true);
		method_19983("portal", false);
		method_19983("rain", false);
		method_19983("smoke", false);
		method_19983("spit", true);
		method_19983("squid_ink", true);
		method_19983("sweep_attack", true);
		method_19983("totem_of_undying", false);
		method_19983("underwater", false);
		method_19983("splash", false);
		method_19983("witch", false);
		method_19983("bubble_pop", false);
		method_19983("current_down", false);
		method_19983("bubble_column_up", false);
		method_19983("nautilus", false);
		method_19983("dolphin", false);
	}

	public Identifier method_19986() {
		return this.field_21348;
	}

	public boolean getAlwaysShow() {
		return this.field_21349;
	}

	public ParticleEffect.class_4341<T> method_19987() {
		return this.field_21350;
	}

	private static void method_19983(String string, boolean bl) {
		Registry.PARTICLE_TYPE.add(new Identifier(string), new class_4343(new Identifier(string), bl));
	}

	private static <T extends ParticleEffect> void method_19984(String string, boolean bl, ParticleEffect.class_4341<T> arg) {
		Registry.PARTICLE_TYPE.add(new Identifier(string), new ParticleType<>(new Identifier(string), bl, arg));
	}
}
