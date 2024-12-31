package net.minecraft.item;

import java.util.List;
import net.minecraft.nbt.CompoundTag;

public interface DyeableItem {
	default boolean hasColor(ItemStack stack) {
		CompoundTag compoundTag = stack.getSubTag("display");
		return compoundTag != null && compoundTag.contains("color", 99);
	}

	default int getColor(ItemStack stack) {
		CompoundTag compoundTag = stack.getSubTag("display");
		return compoundTag != null && compoundTag.contains("color", 99) ? compoundTag.getInt("color") : 10511680;
	}

	default void removeColor(ItemStack stack) {
		CompoundTag compoundTag = stack.getSubTag("display");
		if (compoundTag != null && compoundTag.contains("color")) {
			compoundTag.remove("color");
		}
	}

	default void setColor(ItemStack stack, int color) {
		stack.getOrCreateSubTag("display").putInt("color", color);
	}

	static ItemStack blendAndSetColor(ItemStack stack, List<DyeItem> colors) {
		ItemStack itemStack = ItemStack.EMPTY;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		DyeableItem dyeableItem = null;
		Item item = stack.getItem();
		if (item instanceof DyeableItem) {
			dyeableItem = (DyeableItem)item;
			itemStack = stack.copy();
			itemStack.setCount(1);
			if (dyeableItem.hasColor(stack)) {
				int k = dyeableItem.getColor(itemStack);
				float f = (float)(k >> 16 & 0xFF) / 255.0F;
				float g = (float)(k >> 8 & 0xFF) / 255.0F;
				float h = (float)(k & 0xFF) / 255.0F;
				i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0F);
				is[0] = (int)((float)is[0] + f * 255.0F);
				is[1] = (int)((float)is[1] + g * 255.0F);
				is[2] = (int)((float)is[2] + h * 255.0F);
				j++;
			}

			for (DyeItem dyeItem : colors) {
				float[] fs = dyeItem.getColor().getColorComponents();
				int l = (int)(fs[0] * 255.0F);
				int m = (int)(fs[1] * 255.0F);
				int n = (int)(fs[2] * 255.0F);
				i += Math.max(l, Math.max(m, n));
				is[0] += l;
				is[1] += m;
				is[2] += n;
				j++;
			}
		}

		if (dyeableItem == null) {
			return ItemStack.EMPTY;
		} else {
			int o = is[0] / j;
			int p = is[1] / j;
			int q = is[2] / j;
			float r = (float)i / (float)j;
			float s = (float)Math.max(o, Math.max(p, q));
			o = (int)((float)o * r / s);
			p = (int)((float)p * r / s);
			q = (int)((float)q * r / s);
			int var26 = (o << 8) + p;
			var26 = (var26 << 8) + q;
			dyeableItem.setColor(itemStack, var26);
			return itemStack;
		}
	}
}
