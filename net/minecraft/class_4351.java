package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.achievement.class_3370;
import net.minecraft.advancement.AdvancementType;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.json.LocationJson;
import net.minecraft.world.dimension.DimensionType;

public class class_4351 implements Consumer<Consumer<SimpleAdvancement>> {
	public void accept(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20249(
				Blocks.GRASS_BLOCK,
				new TranslatableText("advancements.story.root.title"),
				new TranslatableText("advancements.story.root.description"),
				new Identifier("minecraft:textures/gui/advancements/backgrounds/stone.png"),
				AdvancementType.TASK,
				false,
				false,
				false
			)
			.method_20251("crafting_table", class_3194.class_3554.method_16069(Blocks.CRAFTING_TABLE))
			.method_20252(consumer, "story/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Items.WOODEN_PICKAXE,
				new TranslatableText("advancements.story.mine_stone.title"),
				new TranslatableText("advancements.story.mine_stone.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("get_stone", class_3194.class_3554.method_16069(Blocks.COBBLESTONE))
			.method_20252(consumer, "story/mine_stone");
		SimpleAdvancement simpleAdvancement3 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement2)
			.method_20249(
				Items.STONE_PICKAXE,
				new TranslatableText("advancements.story.upgrade_tools.title"),
				new TranslatableText("advancements.story.upgrade_tools.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("stone_pickaxe", class_3194.class_3554.method_16069(Items.STONE_PICKAXE))
			.method_20252(consumer, "story/upgrade_tools");
		SimpleAdvancement simpleAdvancement4 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement3)
			.method_20249(
				Items.IRON_INGOT,
				new TranslatableText("advancements.story.smelt_iron.title"),
				new TranslatableText("advancements.story.smelt_iron.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("iron", class_3194.class_3554.method_16069(Items.IRON_INGOT))
			.method_20252(consumer, "story/smelt_iron");
		SimpleAdvancement simpleAdvancement5 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement4)
			.method_20249(
				Items.IRON_PICKAXE,
				new TranslatableText("advancements.story.iron_tools.title"),
				new TranslatableText("advancements.story.iron_tools.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("iron_pickaxe", class_3194.class_3554.method_16069(Items.IRON_PICKAXE))
			.method_20252(consumer, "story/iron_tools");
		SimpleAdvancement simpleAdvancement6 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement5)
			.method_20249(
				Items.DIAMOND,
				new TranslatableText("advancements.story.mine_diamond.title"),
				new TranslatableText("advancements.story.mine_diamond.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("diamond", class_3194.class_3554.method_16069(Items.DIAMOND))
			.method_20252(consumer, "story/mine_diamond");
		SimpleAdvancement simpleAdvancement7 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement4)
			.method_20249(
				Items.LAVA_BUCKET,
				new TranslatableText("advancements.story.lava_bucket.title"),
				new TranslatableText("advancements.story.lava_bucket.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("lava_bucket", class_3194.class_3554.method_16069(Items.LAVA_BUCKET))
			.method_20252(consumer, "story/lava_bucket");
		SimpleAdvancement simpleAdvancement8 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement4)
			.method_20249(
				Items.IRON_CHESTPLATE,
				new TranslatableText("advancements.story.obtain_armor.title"),
				new TranslatableText("advancements.story.obtain_armor.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20258(class_4470.field_21947)
			.method_20251("iron_helmet", class_3194.class_3554.method_16069(Items.IRON_HELMET))
			.method_20251("iron_chestplate", class_3194.class_3554.method_16069(Items.IRON_CHESTPLATE))
			.method_20251("iron_leggings", class_3194.class_3554.method_16069(Items.IRON_LEGGINGS))
			.method_20251("iron_boots", class_3194.class_3554.method_16069(Items.IRON_BOOTS))
			.method_20252(consumer, "story/obtain_armor");
		SimpleAdvancement simpleAdvancement9 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement6)
			.method_20249(
				Items.ENCHANTED_BOOK,
				new TranslatableText("advancements.story.enchant_item.title"),
				new TranslatableText("advancements.story.enchant_item.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("enchanted_item", class_3177.class_3487.method_15759())
			.method_20252(consumer, "story/enchant_item");
		SimpleAdvancement simpleAdvancement10 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement7)
			.method_20249(
				Blocks.OBSIDIAN,
				new TranslatableText("advancements.story.form_obsidian.title"),
				new TranslatableText("advancements.story.form_obsidian.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("obsidian", class_3194.class_3554.method_16069(Blocks.OBSIDIAN))
			.method_20252(consumer, "story/form_obsidian");
		SimpleAdvancement simpleAdvancement11 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement8)
			.method_20249(
				Items.SHIELD,
				new TranslatableText("advancements.story.deflect_arrow.title"),
				new TranslatableText("advancements.story.deflect_arrow.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251(
				"deflected_projectile",
				class_3184.class_3516.method_15866(
					class_3160.class_3466.method_15669().method_15670(class_3161.class_3472.method_15690().method_15692(true)).method_15671(true)
				)
			)
			.method_20252(consumer, "story/deflect_arrow");
		SimpleAdvancement simpleAdvancement12 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement6)
			.method_20249(
				Items.DIAMOND_CHESTPLATE,
				new TranslatableText("advancements.story.shiny_gear.title"),
				new TranslatableText("advancements.story.shiny_gear.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20258(class_4470.field_21947)
			.method_20251("diamond_helmet", class_3194.class_3554.method_16069(Items.DIAMOND_HELMET))
			.method_20251("diamond_chestplate", class_3194.class_3554.method_16069(Items.DIAMOND_CHESTPLATE))
			.method_20251("diamond_leggings", class_3194.class_3554.method_16069(Items.DIAMOND_LEGGINGS))
			.method_20251("diamond_boots", class_3194.class_3554.method_16069(Items.DIAMOND_BOOTS))
			.method_20252(consumer, "story/shiny_gear");
		SimpleAdvancement simpleAdvancement13 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement10)
			.method_20249(
				Items.FLINT_AND_STEEL,
				new TranslatableText("advancements.story.enter_the_nether.title"),
				new TranslatableText("advancements.story.enter_the_nether.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("entered_nether", class_3370.class_3372.method_15243(DimensionType.THE_NETHER))
			.method_20252(consumer, "story/enter_the_nether");
		SimpleAdvancement simpleAdvancement14 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement13)
			.method_20249(
				Items.GOLDEN_APPLE,
				new TranslatableText("advancements.story.cure_zombie_villager.title"),
				new TranslatableText("advancements.story.cure_zombie_villager.description"),
				null,
				AdvancementType.GOAL,
				true,
				true,
				false
			)
			.method_20251("cured_zombie", CuredZombieVillagerCriterion.CuredZombieVillagerCriterionInstance.method_15639())
			.method_20252(consumer, "story/cure_zombie_villager");
		SimpleAdvancement simpleAdvancement15 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement13)
			.method_20249(
				Items.EYE_OF_ENDER,
				new TranslatableText("advancements.story.follow_ender_eye.title"),
				new TranslatableText("advancements.story.follow_ender_eye.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("in_stronghold", class_3210.class_3613.method_16489(LocationJson.method_16355("Stronghold")))
			.method_20252(consumer, "story/follow_ender_eye");
		SimpleAdvancement simpleAdvancement16 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement15)
			.method_20249(
				Blocks.END_STONE,
				new TranslatableText("advancements.story.enter_the_end.title"),
				new TranslatableText("advancements.story.enter_the_end.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("entered_end", class_3370.class_3372.method_15243(DimensionType.THE_END))
			.method_20252(consumer, "story/enter_the_end");
	}
}
