package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.AdvancementType;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.DistanceJson;
import net.minecraft.util.json.LocationJson;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class class_4348 implements Consumer<Consumer<SimpleAdvancement>> {
	private static final Biome[] field_21417 = new Biome[]{
		Biomes.BIRCH_FOREST_HILLS,
		Biomes.RIVER,
		Biomes.SWAMP,
		Biomes.DESERT,
		Biomes.FOREST_HILLS,
		Biomes.GIANT_TREE_TAIGA_HILLS,
		Biomes.TAIGA_COLD,
		Biomes.MESA,
		Biomes.FOREST,
		Biomes.STONE_BEACH,
		Biomes.ICE_FLATS,
		Biomes.TAIGA_HILLS,
		Biomes.ICE_MOUNTAINS,
		Biomes.WOODED_BADLANDS_PLATEAU,
		Biomes.SAVANNA,
		Biomes.PLAINS,
		Biomes.FROZEN_RIVER,
		Biomes.GIANT_TREE_TAIGA,
		Biomes.COLD_BEACH,
		Biomes.JUNGLE_HILLS,
		Biomes.JUNGLE_EDGE,
		Biomes.MUSHROOM_ISLAND_SHORE,
		Biomes.EXTREME_HILLS,
		Biomes.DESERT_HILLS,
		Biomes.JUNGLE,
		Biomes.BEACH,
		Biomes.SAVANNA_PLATEAU,
		Biomes.TAIGA_COLD_HILLS,
		Biomes.BADLANDS_PLATEAU,
		Biomes.ROOFED_FOREST,
		Biomes.TAIGA,
		Biomes.BIRCH_FOREST,
		Biomes.MUSHROOM_ISLAND,
		Biomes.EXTREME_HILLS_WITH_TREES,
		Biomes.WARM_OCEAN,
		Biomes.LUKEWARM_OCEAN,
		Biomes.COLD_OCEAN,
		Biomes.DEEP_LUKEWARM_OCEAN,
		Biomes.DEEP_COLD_OCEAN,
		Biomes.DEEP_FROZEN_OCEAN
	};
	private static final EntityType<?>[] field_21418 = new EntityType[]{
		EntityType.CAVE_SPIDER,
		EntityType.SPIDER,
		EntityType.ZOMBIE_PIGMAN,
		EntityType.ENDERMAN,
		EntityType.POLAR_BEAR,
		EntityType.BLAZE,
		EntityType.CREEPER,
		EntityType.EVOKER,
		EntityType.GHAST,
		EntityType.GUARDIAN,
		EntityType.HUSK,
		EntityType.MAGMA_CUBE,
		EntityType.SHULKER,
		EntityType.SILVERFISH,
		EntityType.SKELETON,
		EntityType.SLIME,
		EntityType.STRAY,
		EntityType.VINDICATOR,
		EntityType.WITCH,
		EntityType.WITHER_SKELETON,
		EntityType.ZOMBIE,
		EntityType.ZOMBIE_VILLAGER,
		EntityType.PHANTOM,
		EntityType.DROWNED
	};

	public void accept(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20249(
				Items.MAP,
				new TranslatableText("advancements.adventure.root.title"),
				new TranslatableText("advancements.adventure.root.description"),
				new Identifier("minecraft:textures/gui/advancements/backgrounds/adventure.png"),
				AdvancementType.TASK,
				false,
				false,
				false
			)
			.method_20258(class_4470.field_21947)
			.method_20251("killed_something", class_3201.class_3586.method_16254())
			.method_20251("killed_by_something", class_3201.class_3586.method_16255())
			.method_20252(consumer, "adventure/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Blocks.RED_BED,
				new TranslatableText("advancements.adventure.sleep_in_bed.title"),
				new TranslatableText("advancements.adventure.sleep_in_bed.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("slept_in_bed", class_3210.class_3613.method_16491())
			.method_20252(consumer, "adventure/sleep_in_bed");
		SimpleAdvancement simpleAdvancement3 = this.method_20012(SimpleAdvancement.TaskAdvancement.method_20248())
			.method_20253(simpleAdvancement2)
			.method_20249(
				Items.DIAMOND_BOOTS,
				new TranslatableText("advancements.adventure.adventuring_time.title"),
				new TranslatableText("advancements.adventure.adventuring_time.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(500))
			.method_20252(consumer, "adventure/adventuring_time");
		SimpleAdvancement simpleAdvancement4 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Items.EMERALD,
				new TranslatableText("advancements.adventure.trade.title"),
				new TranslatableText("advancements.adventure.trade.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("traded", class_3241.class_3744.method_16832())
			.method_20252(consumer, "adventure/trade");
		SimpleAdvancement simpleAdvancement5 = this.method_20011(SimpleAdvancement.TaskAdvancement.method_20248())
			.method_20253(simpleAdvancement)
			.method_20249(
				Items.IRON_SWORD,
				new TranslatableText("advancements.adventure.kill_a_mob.title"),
				new TranslatableText("advancements.adventure.kill_a_mob.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20258(class_4470.field_21947)
			.method_20252(consumer, "adventure/kill_a_mob");
		SimpleAdvancement simpleAdvancement6 = this.method_20011(SimpleAdvancement.TaskAdvancement.method_20248())
			.method_20253(simpleAdvancement5)
			.method_20249(
				Items.DIAMOND_SWORD,
				new TranslatableText("advancements.adventure.kill_all_mobs.title"),
				new TranslatableText("advancements.adventure.kill_all_mobs.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(100))
			.method_20252(consumer, "adventure/kill_all_mobs");
		SimpleAdvancement simpleAdvancement7 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement5)
			.method_20249(
				Items.BOW,
				new TranslatableText("advancements.adventure.shoot_arrow.title"),
				new TranslatableText("advancements.adventure.shoot_arrow.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251(
				"shot_arrow",
				class_3226.class_3707.method_16683(
					class_3160.class_3466.method_15669()
						.method_15670(class_3161.class_3472.method_15690().method_15692(true).method_15691(class_3528.class_3529.method_15909().method_15910(EntityType.ARROW)))
				)
			)
			.method_20252(consumer, "adventure/shoot_arrow");
		SimpleAdvancement simpleAdvancement8 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement5)
			.method_20249(
				Items.TRIDENT,
				new TranslatableText("advancements.adventure.throw_trident.title"),
				new TranslatableText("advancements.adventure.throw_trident.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251(
				"shot_trident",
				class_3226.class_3707.method_16683(
					class_3160.class_3466.method_15669()
						.method_15670(class_3161.class_3472.method_15690().method_15692(true).method_15691(class_3528.class_3529.method_15909().method_15910(EntityType.TRIDENT)))
				)
			)
			.method_20252(consumer, "adventure/throw_trident");
		SimpleAdvancement simpleAdvancement9 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement8)
			.method_20249(
				Items.TRIDENT,
				new TranslatableText("advancements.adventure.very_very_frightening.title"),
				new TranslatableText("advancements.adventure.very_very_frightening.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("struck_villager", class_3430.class_3432.method_15328(class_3528.class_3529.method_15909().method_15910(EntityType.VILLAGER).method_15916()))
			.method_20252(consumer, "adventure/very_very_frightening");
		SimpleAdvancement simpleAdvancement10 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement4)
			.method_20249(
				Blocks.CARVED_PUMPKIN,
				new TranslatableText("advancements.adventure.summon_iron_golem.title"),
				new TranslatableText("advancements.adventure.summon_iron_golem.description"),
				null,
				AdvancementType.GOAL,
				true,
				true,
				false
			)
			.method_20251("summoned_golem", class_3232.class_3718.method_16732(class_3528.class_3529.method_15909().method_15910(EntityType.IRON_GOLEM)))
			.method_20252(consumer, "adventure/summon_iron_golem");
		SimpleAdvancement simpleAdvancement11 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement7)
			.method_20249(
				Items.ARROW,
				new TranslatableText("advancements.adventure.sniper_duel.title"),
				new TranslatableText("advancements.adventure.sniper_duel.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(50))
			.method_20251(
				"killed_skeleton",
				class_3201.class_3586.method_16252(
					class_3528.class_3529.method_15909().method_15910(EntityType.SKELETON).method_15911(DistanceJson.method_15703(class_3638.class_3641.method_16520(50.0F))),
					class_3161.class_3472.method_15690().method_15692(true)
				)
			)
			.method_20252(consumer, "adventure/sniper_duel");
		SimpleAdvancement simpleAdvancement12 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement5)
			.method_20249(
				Items.TOTEM_OF_UNDYING,
				new TranslatableText("advancements.adventure.totem_of_undying.title"),
				new TranslatableText("advancements.adventure.totem_of_undying.description"),
				null,
				AdvancementType.GOAL,
				true,
				true,
				false
			)
			.method_20251("used_totem", class_3247.class_3771.method_16964(Items.TOTEM_OF_UNDYING))
			.method_20252(consumer, "adventure/totem_of_undying");
	}

	private SimpleAdvancement.TaskAdvancement method_20011(SimpleAdvancement.TaskAdvancement taskAdvancement) {
		for (EntityType<?> entityType : field_21418) {
			taskAdvancement.method_20251(
				Registry.ENTITY_TYPE.getId(entityType).toString(), class_3201.class_3586.method_16251(class_3528.class_3529.method_15909().method_15910(entityType))
			);
		}

		return taskAdvancement;
	}

	private SimpleAdvancement.TaskAdvancement method_20012(SimpleAdvancement.TaskAdvancement taskAdvancement) {
		for (Biome biome : field_21417) {
			taskAdvancement.method_20251(Registry.BIOME.getId(biome).toString(), class_3210.class_3613.method_16489(LocationJson.method_16353(biome)));
		}

		return taskAdvancement;
	}
}
