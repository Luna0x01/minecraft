package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class class_2686 {
	private final Map<Item, class_2686.class_2687> field_12306 = Maps.newHashMap();
	private int field_12307;

	public boolean method_11382(Item item) {
		return this.getCooldownProgress(item, 0.0F) > 0.0F;
	}

	public float getCooldownProgress(Item item, float f) {
		class_2686.class_2687 lv = (class_2686.class_2687)this.field_12306.get(item);
		if (lv != null) {
			float g = (float)(lv.field_17200 - lv.field_17199);
			float h = (float)lv.field_17200 - ((float)this.field_12307 + f);
			return MathHelper.clamp(h / g, 0.0F, 1.0F);
		} else {
			return 0.0F;
		}
	}

	public void method_11381() {
		this.field_12307++;
		if (!this.field_12306.isEmpty()) {
			Iterator<Entry<Item, class_2686.class_2687>> iterator = this.field_12306.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Item, class_2686.class_2687> entry = (Entry<Item, class_2686.class_2687>)iterator.next();
				if (((class_2686.class_2687)entry.getValue()).field_17200 <= this.field_12307) {
					iterator.remove();
					this.method_11387((Item)entry.getKey());
				}
			}
		}
	}

	public void method_11384(Item item, int i) {
		this.field_12306.put(item, new class_2686.class_2687(this.field_12307, this.field_12307 + i));
		this.method_11386(item, i);
	}

	public void method_11385(Item item) {
		this.field_12306.remove(item);
		this.method_11387(item);
	}

	protected void method_11386(Item item, int i) {
	}

	protected void method_11387(Item item) {
	}

	class class_2687 {
		private final int field_17199;
		private final int field_17200;

		private class_2687(int i, int j) {
			this.field_17199 = i;
			this.field_17200 = j;
		}
	}
}
