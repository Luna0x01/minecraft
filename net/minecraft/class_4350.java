package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.achievement.class_3366;
import net.minecraft.achievement.class_3370;
import net.minecraft.achievement.class_3376;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.AdvancementType;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.DistanceJson;
import net.minecraft.util.json.EffectsJson;
import net.minecraft.util.json.LocationJson;
import net.minecraft.world.dimension.DimensionType;

public class class_4350 implements Consumer<Consumer<SimpleAdvancement>> {
	public void accept(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20249(
				Blocks.RED_NETHER_BRICKS,
				new TranslatableText("advancements.nether.root.title"),
				new TranslatableText("advancements.nether.root.description"),
				new Identifier("minecraft:textures/gui/advancements/backgrounds/nether.png"),
				AdvancementType.TASK,
				false,
				false,
				false
			)
			.method_20251("entered_nether", class_3370.class_3372.method_15243(DimensionType.THE_NETHER))
			.method_20252(consumer, "nether/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Items.FIRE_CHARGE,
				new TranslatableText("advancements.nether.return_to_sender.title"),
				new TranslatableText("advancements.nether.return_to_sender.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(50))
			.method_20251(
				"killed_ghast",
				class_3201.class_3586.method_16252(
					class_3528.class_3529.method_15909().method_15910(EntityType.GHAST),
					class_3161.class_3472.method_15690().method_15692(true).method_15691(class_3528.class_3529.method_15909().method_15910(EntityType.FIREBALL))
				)
			)
			.method_20252(consumer, "nether/return_to_sender");
		SimpleAdvancement simpleAdvancement3 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Blocks.NETHER_BRICKS,
				new TranslatableText("advancements.nether.find_fortress.title"),
				new TranslatableText("advancements.nether.find_fortress.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("fortress", class_3210.class_3613.method_16489(LocationJson.method_16355("Fortress")))
			.method_20252(consumer, "nether/find_fortress");
		SimpleAdvancement simpleAdvancement4 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Items.MAP,
				new TranslatableText("advancements.nether.fast_travel.title"),
				new TranslatableText("advancements.nether.fast_travel.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(100))
			.method_20251("travelled", class_3218.class_3696.method_16632(DistanceJson.method_15703(class_3638.class_3641.method_16520(7000.0F))))
			.method_20252(consumer, "nether/fast_travel");
		SimpleAdvancement simpleAdvancement5 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement2)
			.method_20249(
				Items.GHAST_TEAR,
				new TranslatableText("advancements.nether.uneasy_alliance.title"),
				new TranslatableText("advancements.nether.uneasy_alliance.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(100))
			.method_20251(
				"killed_ghast",
				class_3201.class_3586.method_16251(
					class_3528.class_3529.method_15909().method_15910(EntityType.GHAST).method_15913(LocationJson.method_16354(DimensionType.OVERWORLD))
				)
			)
			.method_20252(consumer, "nether/uneasy_alliance");
		SimpleAdvancement simpleAdvancement6 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement3)
			.method_20249(
				Blocks.WITHER_SKELETON_SKULL,
				new TranslatableText("advancements.nether.get_wither_skull.title"),
				new TranslatableText("advancements.nether.get_wither_skull.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("wither_skull", class_3194.class_3554.method_16069(Blocks.WITHER_SKELETON_SKULL))
			.method_20252(consumer, "nether/get_wither_skull");
		SimpleAdvancement simpleAdvancement7 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement6)
			.method_20249(
				Items.NETHER_STAR,
				new TranslatableText("advancements.nether.summon_wither.title"),
				new TranslatableText("advancements.nether.summon_wither.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("summoned", class_3232.class_3718.method_16732(class_3528.class_3529.method_15909().method_15910(EntityType.WITHER)))
			.method_20252(consumer, "nether/summon_wither");
		SimpleAdvancement simpleAdvancement8 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement3)
			.method_20249(
				Items.BLAZE_ROD,
				new TranslatableText("advancements.nether.obtain_blaze_rod.title"),
				new TranslatableText("advancements.nether.obtain_blaze_rod.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("blaze_rod", class_3194.class_3554.method_16069(Items.BLAZE_ROD))
			.method_20252(consumer, "nether/obtain_blaze_rod");
		SimpleAdvancement simpleAdvancement9 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement7)
			.method_20249(
				Blocks.BEACON,
				new TranslatableText("advancements.nether.create_beacon.title"),
				new TranslatableText("advancements.nether.create_beacon.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("beacon", class_3376.class_3378.method_15519(class_3638.class_3642.method_16529(1)))
			.method_20252(consumer, "nether/create_beacon");
		SimpleAdvancement simpleAdvancement10 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement9)
			.method_20249(
				Blocks.BEACON,
				new TranslatableText("advancements.nether.create_full_beacon.title"),
				new TranslatableText("advancements.nether.create_full_beacon.description"),
				null,
				AdvancementType.GOAL,
				true,
				true,
				false
			)
			.method_20251("beacon", class_3376.class_3378.method_15519(class_3638.class_3642.method_16523(4)))
			.method_20252(consumer, "nether/create_full_beacon");
		SimpleAdvancement simpleAdvancement11 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement8)
			.method_20249(
				Items.POTION,
				new TranslatableText("advancements.nether.brew_potion.title"),
				new TranslatableText("advancements.nether.brew_potion.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("potion", class_3366.class_3368.method_15110())
			.method_20252(consumer, "nether/brew_potion");
		SimpleAdvancement simpleAdvancement12 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement11)
			.method_20249(
				Items.MILK_BUCKET,
				new TranslatableText("advancements.nether.all_potions.title"),
				new TranslatableText("advancements.nether.all_potions.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(100))
			.method_20251(
				"all_effects",
				class_3171.class_3476.method_15713(
					EffectsJson.method_16537()
						.method_16538(StatusEffects.SPEED)
						.method_16538(StatusEffects.SLOWNESS)
						.method_16538(StatusEffects.STRENGTH)
						.method_16538(StatusEffects.JUMP_BOOST)
						.method_16538(StatusEffects.REGENERATION)
						.method_16538(StatusEffects.FIRE_RESISTANCE)
						.method_16538(StatusEffects.WATER_BREATHING)
						.method_16538(StatusEffects.INVISIBILITY)
						.method_16538(StatusEffects.NIGHT_VISION)
						.method_16538(StatusEffects.WEAKNESS)
						.method_16538(StatusEffects.POISON)
						.method_16538(StatusEffects.SLOW_FALLING)
						.method_16538(StatusEffects.RESISTANCE)
				)
			)
			.method_20252(consumer, "nether/all_potions");
		SimpleAdvancement simpleAdvancement13 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement12)
			.method_20249(
				Items.BUCKET,
				new TranslatableText("advancements.nether.all_effects.title"),
				new TranslatableText("advancements.nether.all_effects.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				true
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(1000))
			.method_20251(
				"all_effects",
				class_3171.class_3476.method_15713(
					EffectsJson.method_16537()
						.method_16538(StatusEffects.SPEED)
						.method_16538(StatusEffects.SLOWNESS)
						.method_16538(StatusEffects.STRENGTH)
						.method_16538(StatusEffects.JUMP_BOOST)
						.method_16538(StatusEffects.REGENERATION)
						.method_16538(StatusEffects.FIRE_RESISTANCE)
						.method_16538(StatusEffects.WATER_BREATHING)
						.method_16538(StatusEffects.INVISIBILITY)
						.method_16538(StatusEffects.NIGHT_VISION)
						.method_16538(StatusEffects.WEAKNESS)
						.method_16538(StatusEffects.POISON)
						.method_16538(StatusEffects.WITHER)
						.method_16538(StatusEffects.HASTE)
						.method_16538(StatusEffects.MINING_FATIGUE)
						.method_16538(StatusEffects.LEVITATION)
						.method_16538(StatusEffects.GLOWING)
						.method_16538(StatusEffects.ABSORPTION)
						.method_16538(StatusEffects.HUNGER)
						.method_16538(StatusEffects.NAUSEA)
						.method_16538(StatusEffects.RESISTANCE)
						.method_16538(StatusEffects.SLOW_FALLING)
						.method_16538(StatusEffects.CONDUIT_POWER)
						.method_16538(StatusEffects.DOLPHINS_GRACE)
				)
			)
			.method_20252(consumer, "nether/all_effects");
	}
}
