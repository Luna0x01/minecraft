package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.datafixer.DataFix;
import net.minecraft.nbt.NbtCompound;

public class EntityIdFix implements DataFix {
	private static final Map<String, String> RENAMED_ENTITIES = Maps.newHashMap();

	@Override
	public int getVersion() {
		return 704;
	}

	@Override
	public NbtCompound fixData(NbtCompound tag) {
		String string = (String)RENAMED_ENTITIES.get(tag.getString("id"));
		if (string != null) {
			tag.putString("id", string);
		}

		return tag;
	}

	static {
		RENAMED_ENTITIES.put("AreaEffectCloud", "minecraft:area_effect_cloud");
		RENAMED_ENTITIES.put("ArmorStand", "minecraft:armor_stand");
		RENAMED_ENTITIES.put("Arrow", "minecraft:arrow");
		RENAMED_ENTITIES.put("Bat", "minecraft:bat");
		RENAMED_ENTITIES.put("Blaze", "minecraft:blaze");
		RENAMED_ENTITIES.put("Boat", "minecraft:boat");
		RENAMED_ENTITIES.put("CaveSpider", "minecraft:cave_spider");
		RENAMED_ENTITIES.put("Chicken", "minecraft:chicken");
		RENAMED_ENTITIES.put("Cow", "minecraft:cow");
		RENAMED_ENTITIES.put("Creeper", "minecraft:creeper");
		RENAMED_ENTITIES.put("Donkey", "minecraft:donkey");
		RENAMED_ENTITIES.put("DragonFireball", "minecraft:dragon_fireball");
		RENAMED_ENTITIES.put("ElderGuardian", "minecraft:elder_guardian");
		RENAMED_ENTITIES.put("EnderCrystal", "minecraft:ender_crystal");
		RENAMED_ENTITIES.put("EnderDragon", "minecraft:ender_dragon");
		RENAMED_ENTITIES.put("Enderman", "minecraft:enderman");
		RENAMED_ENTITIES.put("Endermite", "minecraft:endermite");
		RENAMED_ENTITIES.put("EyeOfEnderSignal", "minecraft:eye_of_ender_signal");
		RENAMED_ENTITIES.put("FallingSand", "minecraft:falling_block");
		RENAMED_ENTITIES.put("Fireball", "minecraft:fireball");
		RENAMED_ENTITIES.put("FireworksRocketEntity", "minecraft:fireworks_rocket");
		RENAMED_ENTITIES.put("Ghast", "minecraft:ghast");
		RENAMED_ENTITIES.put("Giant", "minecraft:giant");
		RENAMED_ENTITIES.put("Guardian", "minecraft:guardian");
		RENAMED_ENTITIES.put("Horse", "minecraft:horse");
		RENAMED_ENTITIES.put("Husk", "minecraft:husk");
		RENAMED_ENTITIES.put("Item", "minecraft:item");
		RENAMED_ENTITIES.put("ItemFrame", "minecraft:item_frame");
		RENAMED_ENTITIES.put("LavaSlime", "minecraft:magma_cube");
		RENAMED_ENTITIES.put("LeashKnot", "minecraft:leash_knot");
		RENAMED_ENTITIES.put("MinecartChest", "minecraft:chest_minecart");
		RENAMED_ENTITIES.put("MinecartCommandBlock", "minecraft:commandblock_minecart");
		RENAMED_ENTITIES.put("MinecartFurnace", "minecraft:furnace_minecart");
		RENAMED_ENTITIES.put("MinecartHopper", "minecraft:hopper_minecart");
		RENAMED_ENTITIES.put("MinecartRideable", "minecraft:minecart");
		RENAMED_ENTITIES.put("MinecartSpawner", "minecraft:spawner_minecart");
		RENAMED_ENTITIES.put("MinecartTNT", "minecraft:tnt_minecart");
		RENAMED_ENTITIES.put("Mule", "minecraft:mule");
		RENAMED_ENTITIES.put("MushroomCow", "minecraft:mooshroom");
		RENAMED_ENTITIES.put("Ozelot", "minecraft:ocelot");
		RENAMED_ENTITIES.put("Painting", "minecraft:painting");
		RENAMED_ENTITIES.put("Pig", "minecraft:pig");
		RENAMED_ENTITIES.put("PigZombie", "minecraft:zombie_pigman");
		RENAMED_ENTITIES.put("PolarBear", "minecraft:polar_bear");
		RENAMED_ENTITIES.put("PrimedTnt", "minecraft:tnt");
		RENAMED_ENTITIES.put("Rabbit", "minecraft:rabbit");
		RENAMED_ENTITIES.put("Sheep", "minecraft:sheep");
		RENAMED_ENTITIES.put("Shulker", "minecraft:shulker");
		RENAMED_ENTITIES.put("ShulkerBullet", "minecraft:shulker_bullet");
		RENAMED_ENTITIES.put("Silverfish", "minecraft:silverfish");
		RENAMED_ENTITIES.put("Skeleton", "minecraft:skeleton");
		RENAMED_ENTITIES.put("SkeletonHorse", "minecraft:skeleton_horse");
		RENAMED_ENTITIES.put("Slime", "minecraft:slime");
		RENAMED_ENTITIES.put("SmallFireball", "minecraft:small_fireball");
		RENAMED_ENTITIES.put("SnowMan", "minecraft:snowman");
		RENAMED_ENTITIES.put("Snowball", "minecraft:snowball");
		RENAMED_ENTITIES.put("SpectralArrow", "minecraft:spectral_arrow");
		RENAMED_ENTITIES.put("Spider", "minecraft:spider");
		RENAMED_ENTITIES.put("Squid", "minecraft:squid");
		RENAMED_ENTITIES.put("Stray", "minecraft:stray");
		RENAMED_ENTITIES.put("ThrownEgg", "minecraft:egg");
		RENAMED_ENTITIES.put("ThrownEnderpearl", "minecraft:ender_pearl");
		RENAMED_ENTITIES.put("ThrownExpBottle", "minecraft:xp_bottle");
		RENAMED_ENTITIES.put("ThrownPotion", "minecraft:potion");
		RENAMED_ENTITIES.put("Villager", "minecraft:villager");
		RENAMED_ENTITIES.put("VillagerGolem", "minecraft:villager_golem");
		RENAMED_ENTITIES.put("Witch", "minecraft:witch");
		RENAMED_ENTITIES.put("WitherBoss", "minecraft:wither");
		RENAMED_ENTITIES.put("WitherSkeleton", "minecraft:wither_skeleton");
		RENAMED_ENTITIES.put("WitherSkull", "minecraft:wither_skull");
		RENAMED_ENTITIES.put("Wolf", "minecraft:wolf");
		RENAMED_ENTITIES.put("XPOrb", "minecraft:xp_orb");
		RENAMED_ENTITIES.put("Zombie", "minecraft:zombie");
		RENAMED_ENTITIES.put("ZombieHorse", "minecraft:zombie_horse");
		RENAMED_ENTITIES.put("ZombieVillager", "minecraft:zombie_villager");
	}
}
