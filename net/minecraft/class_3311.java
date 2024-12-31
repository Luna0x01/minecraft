package net.minecraft;

import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class class_3311 implements class_3318 {
	private static final Text field_16194 = new TranslatableText("tutorial.craft_planks.title");
	private static final Text field_16195 = new TranslatableText("tutorial.craft_planks.description");
	private final class_3316 field_16196;
	private class_3266 field_16197;
	private int field_16198;

	public class_3311(class_3316 arg) {
		this.field_16196 = arg;
	}

	@Override
	public void method_14731() {
		this.field_16198++;
		if (this.field_16196.method_14730() != GameMode.SURVIVAL) {
			this.field_16196.method_14724(class_3319.NONE);
		} else {
			if (this.field_16198 == 1) {
				ClientPlayerEntity clientPlayerEntity = this.field_16196.method_14729().player;
				if (clientPlayerEntity != null) {
					if (clientPlayerEntity.inventory.method_15923(ItemTags.PLANKS)) {
						this.field_16196.method_14724(class_3319.NONE);
						return;
					}

					if (method_19635(clientPlayerEntity, ItemTags.PLANKS)) {
						this.field_16196.method_14724(class_3319.NONE);
						return;
					}
				}
			}

			if (this.field_16198 >= 1200 && this.field_16197 == null) {
				this.field_16197 = new class_3266(class_3266.class_3267.WOODEN_PLANKS, field_16194, field_16195, false);
				this.field_16196.method_14729().method_14462().method_14491(this.field_16197);
			}
		}
	}

	@Override
	public void method_14737() {
		if (this.field_16197 != null) {
			this.field_16197.method_14498();
			this.field_16197 = null;
		}
	}

	@Override
	public void method_14732(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (ItemTags.PLANKS.contains(item)) {
			this.field_16196.method_14724(class_3319.NONE);
		}
	}

	public static boolean method_19635(ClientPlayerEntity clientPlayerEntity, Tag<Item> tag) {
		for (Item item : tag.values()) {
			if (clientPlayerEntity.getStatHandler().method_21434(Stats.CRAFTED.method_21429(item)) > 0) {
				return true;
			}
		}

		return false;
	}
}
