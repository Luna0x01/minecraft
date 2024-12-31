package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.achievement.class_3370;
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
import net.minecraft.world.dimension.DimensionType;

public class class_4352 implements Consumer<Consumer<SimpleAdvancement>> {
	public void accept(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20249(
				Blocks.END_STONE,
				new TranslatableText("advancements.end.root.title"),
				new TranslatableText("advancements.end.root.description"),
				new Identifier("minecraft:textures/gui/advancements/backgrounds/end.png"),
				AdvancementType.TASK,
				false,
				false,
				false
			)
			.method_20251("entered_end", class_3370.class_3372.method_15243(DimensionType.THE_END))
			.method_20252(consumer, "end/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement)
			.method_20249(
				Blocks.DRAGON_HEAD,
				new TranslatableText("advancements.end.kill_dragon.title"),
				new TranslatableText("advancements.end.kill_dragon.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("killed_dragon", class_3201.class_3586.method_16251(class_3528.class_3529.method_15909().method_15910(EntityType.ENDER_DRAGON)))
			.method_20252(consumer, "end/kill_dragon");
		SimpleAdvancement simpleAdvancement3 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement2)
			.method_20249(
				Items.ENDER_PEARL,
				new TranslatableText("advancements.end.enter_end_gateway.title"),
				new TranslatableText("advancements.end.enter_end_gateway.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("entered_end_gateway", class_3181.class_3506.method_15835(Blocks.END_GATEWAY))
			.method_20252(consumer, "end/enter_end_gateway");
		SimpleAdvancement simpleAdvancement4 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement2)
			.method_20249(
				Items.END_CRYSTAL,
				new TranslatableText("advancements.end.respawn_dragon.title"),
				new TranslatableText("advancements.end.respawn_dragon.description"),
				null,
				AdvancementType.GOAL,
				true,
				true,
				false
			)
			.method_20251("summoned_dragon", class_3232.class_3718.method_16732(class_3528.class_3529.method_15909().method_15910(EntityType.ENDER_DRAGON)))
			.method_20252(consumer, "end/respawn_dragon");
		SimpleAdvancement simpleAdvancement5 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement3)
			.method_20249(
				Blocks.PURPUR_BLOCK,
				new TranslatableText("advancements.end.find_end_city.title"),
				new TranslatableText("advancements.end.find_end_city.description"),
				null,
				AdvancementType.TASK,
				true,
				true,
				false
			)
			.method_20251("in_city", class_3210.class_3613.method_16489(LocationJson.method_16355("EndCity")))
			.method_20252(consumer, "end/find_end_city");
		SimpleAdvancement simpleAdvancement6 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement2)
			.method_20249(
				Items.DRAGON_BREATH,
				new TranslatableText("advancements.end.dragon_breath.title"),
				new TranslatableText("advancements.end.dragon_breath.description"),
				null,
				AdvancementType.GOAL,
				true,
				true,
				false
			)
			.method_20251("dragon_breath", class_3194.class_3554.method_16069(Items.DRAGON_BREATH))
			.method_20252(consumer, "end/dragon_breath");
		SimpleAdvancement simpleAdvancement7 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement5)
			.method_20249(
				Items.SHULKER_SHELL,
				new TranslatableText("advancements.end.levitate.title"),
				new TranslatableText("advancements.end.levitate.description"),
				null,
				AdvancementType.CHALLENGE,
				true,
				true,
				false
			)
			.method_20254(AdvancementRewards.class_4395.method_20388(50))
			.method_20251("levitated", class_3204.class_3591.method_16269(DistanceJson.method_15704(class_3638.class_3641.method_16520(50.0F))))
			.method_20252(consumer, "end/levitate");
		SimpleAdvancement simpleAdvancement8 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement5)
			.method_20249(
				Items.ELYTRA,
				new TranslatableText("advancements.end.elytra.title"),
				new TranslatableText("advancements.end.elytra.description"),
				null,
				AdvancementType.GOAL,
				true,
				true,
				false
			)
			.method_20251("elytra", class_3194.class_3554.method_16069(Items.ELYTRA))
			.method_20252(consumer, "end/elytra");
		SimpleAdvancement simpleAdvancement9 = SimpleAdvancement.TaskAdvancement.method_20248()
			.method_20253(simpleAdvancement2)
			.method_20249(
				Blocks.DRAGON_EGG,
				new TranslatableText("advancements.end.dragon_egg.title"),
				new TranslatableText("advancements.end.dragon_egg.description"),
				null,
				AdvancementType.GOAL,
				true,
				true,
				false
			)
			.method_20251("dragon_egg", class_3194.class_3554.method_16069(Blocks.DRAGON_EGG))
			.method_20252(consumer, "end/dragon_egg");
	}
}
