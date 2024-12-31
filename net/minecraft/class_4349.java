package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.achievement.class_3363;
import net.minecraft.achievement.class_3380;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.AdvancementType;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4349 implements Consumer<Consumer<SimpleAdvancement>> {
	private static final EntityType<?>[] field_21419 = new EntityType[]{
		EntityType.HORSE,
		EntityType.SHEEP,
		EntityType.COW,
		EntityType.MOOSHROOM,
		EntityType.PIG,
		EntityType.CHICKEN,
		EntityType.WOLF,
		EntityType.OCELOT,
		EntityType.RABBIT,
		EntityType.LLAMA,
		EntityType.TURTLE
	};
	private static final Item[] field_21420 = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
	private static final Item[] field_21421 = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
	private static final Item[] field_21422 = new Item[]{
		Items.APPLE,
		Items.MUSHROOM_STEW,
		Items.BREAD,
		Items.RAW_PORKCHOP,
		Items.COOKED_PORKCHOP,
		Items.GOLDEN_APPLE,
		Items.ENCHANTED_GOLDEN_APPLE,
		Items.COD,
		Items.SALMON,
		Items.TROPICAL_FISH,
		Items.PUFFERFISH,
		Items.COOKED_COD,
		Items.COOKED_SALMON,
		Items.COOKIE,
		Items.MELON,
		Items.BEEF,
		Items.COOKED_BEEF,
		Items.CHICKEN,
		Items.COOKED_CHICKEN,
		Items.ROTTEN_FLESH,
		Items.SPIDER_EYE,
		Items.CARROT,
		Items.POTATO,
		Items.BAKED_POTATO,
		Items.POISONOUS_POTATO,
		Items.GOLDEN_CARROT,
		Items.PUMPKIN_PIE,
		Items.RAW_RABBIT,
		Items.COOKED_RABBIT,
		Items.RABBIT_STEW,
		Items.MUTTON,
		Items.COOKED_MUTTON,
		Items.CHORUS_FRUIT,
		Items.BEETROOT,
		Items.BEETROOT_SOUP,
		Items.DRIED_KELP
	};

	public void accept(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20249(
				Blocks.HAY_BALE,
				new TranslatableText("advancements.husbandry.root.title"),
				new TranslatableText("advancements.husbandry.root.description"),
				new Identifier("minecraft:textures/gui/advancements/backgrounds/husbandry.png"),
				AdvancementType.TASK,
				false,
				false,
				false
			)
			.method_20251("consumed_item", class_3380.class_3382.method_15544())
			.method_20252(consumer, "husbandry/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Items.WHEAT,
				new TranslatableText("advancements.husbandry.plant_seed.title"),
				new TranslatableText("advancements.husbandry.plant_seed.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20258(class_4470.field_21947)
			.method_20251("wheat", class_3222.class_3704.method_16662(Blocks.WHEAT))
			.method_20251("pumpkin_stem", class_3222.class_3704.method_16662(Blocks.PUMPKIN_STEM))
			.method_20251("melon_stem", class_3222.class_3704.method_16662(Blocks.MELON_STEM))
			.method_20251("beetroots", class_3222.class_3704.method_16662(Blocks.BEETROOTS))
			.method_20251("nether_wart", class_3222.class_3704.method_16662(Blocks.NETHER_WART))
			.method_20252(consumer, "husbandry/plant_seed");
		SimpleAdvancement simpleAdvancement3 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Items.WHEAT,
				new TranslatableText("advancements.husbandry.breed_an_animal.title"),
				new TranslatableText("advancements.husbandry.breed_an_animal.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20258(class_4470.field_21947)
			.method_20251("bred", class_3363.class_4515.method_21706())
			.method_20252(consumer, "husbandry/breed_an_animal");
		SimpleAdvancement simpleAdvancement4 = this.method_20014(SimpleAdvancement.TaskAdvancement.method_20248())
			.method_20253(simpleAdvancement2)
			.method_20249(
				Items.APPLE,
				new TranslatableText("advancements.husbandry.balanced_diet.title"),
				new TranslatableText("advancements.husbandry.balanced_diet.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(100))
			.method_20252(consumer, "husbandry/balanced_diet");
		SimpleAdvancement simpleAdvancement5 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement2)
			.method_20249(
				Items.DIAMOND_HOE,
				new TranslatableText("advancements.husbandry.break_diamond_hoe.title"),
				new TranslatableText("advancements.husbandry.break_diamond_hoe.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(100))
			.method_20251(
				"broke_hoe",
				class_3197.class_3557.method_16124(
					class_3200.class_3568.method_16172().method_16173(Items.DIAMOND_HOE).method_16176(), class_3638.class_3642.method_16523(-1)
				)
			)
			.method_20252(consumer, "husbandry/break_diamond_hoe");
		SimpleAdvancement simpleAdvancement6 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Items.LEAD,
				new TranslatableText("advancements.husbandry.tame_an_animal.title"),
				new TranslatableText("advancements.husbandry.tame_an_animal.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("tamed_animal", class_3235.class_3727.method_16748())
			.method_20252(consumer, "husbandry/tame_an_animal");
		SimpleAdvancement simpleAdvancement7 = this.method_20015(SimpleAdvancement.TaskAdvancement.method_20248())
			.method_20253(simpleAdvancement3)
			.method_20249(
				Items.GOLDEN_CARROT,
				new TranslatableText("advancements.husbandry.breed_all_animals.title"),
				new TranslatableText("advancements.husbandry.breed_all_animals.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(100))
			.method_20252(consumer, "husbandry/bred_all_animals");
		SimpleAdvancement simpleAdvancement8 = this.method_20017(SimpleAdvancement.TaskAdvancement.method_20248())
			.method_20253(simpleAdvancement)
			.method_20258(class_4470.field_21947)
			.method_20249(
				Items.FISHING_ROD,
				new TranslatableText("advancements.husbandry.fishy_business.title"),
				new TranslatableText("advancements.husbandry.fishy_business.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20252(consumer, "husbandry/fishy_business");
		SimpleAdvancement simpleAdvancement9 = this.method_20016(SimpleAdvancement.TaskAdvancement.method_20248())
			.method_20253(simpleAdvancement8)
			.method_20258(class_4470.field_21947)
			.method_20249(
				Items.PUFFERFISH_BUCKET,
				new TranslatableText("advancements.husbandry.tactical_fishing.title"),
				new TranslatableText("advancements.husbandry.tactical_fishing.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20252(consumer, "husbandry/tactical_fishing");
	}

	private SimpleAdvancement.TaskAdvancement method_20014(SimpleAdvancement.TaskAdvancement taskAdvancement) {
		for (Item item : field_21422) {
			taskAdvancement.method_20251(Registry.ITEM.getId(item).getPath(), class_3380.class_3382.method_15543(item));
		}

		return taskAdvancement;
	}

	private SimpleAdvancement.TaskAdvancement method_20015(SimpleAdvancement.TaskAdvancement taskAdvancement) {
		for (EntityType<?> entityType : field_21419) {
			taskAdvancement.method_20251(
				EntityType.getId(entityType).toString(), class_3363.class_4515.method_21704(class_3528.class_3529.method_15909().method_15910(entityType))
			);
		}

		return taskAdvancement;
	}

	private SimpleAdvancement.TaskAdvancement method_20016(SimpleAdvancement.TaskAdvancement taskAdvancement) {
		for (Item item : field_21421) {
			taskAdvancement.method_20251(
				Registry.ITEM.getId(item).getPath(), class_3533.class_3535.method_15975(class_3200.class_3568.method_16172().method_16173(item).method_16176())
			);
		}

		return taskAdvancement;
	}

	private SimpleAdvancement.TaskAdvancement method_20017(SimpleAdvancement.TaskAdvancement taskAdvancement) {
		for (Item item : field_21420) {
			taskAdvancement.method_20251(
				Registry.ITEM.getId(item).getPath(),
				class_3539.class_3541.method_15995(class_3200.field_15710, class_3528.field_17075, class_3200.class_3568.method_16172().method_16173(item).method_16176())
			);
		}

		return taskAdvancement;
	}
}
