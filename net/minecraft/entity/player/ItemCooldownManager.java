package net.minecraft.entity.player;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class ItemCooldownManager {
	private final Map<Item, ItemCooldownManager.Entry> entries = Maps.newHashMap();
	private int tick;

	public boolean isCoolingDown(Item item) {
		return this.getCooldownProgress(item, 0.0F) > 0.0F;
	}

	public float getCooldownProgress(Item item, float f) {
		ItemCooldownManager.Entry entry = (ItemCooldownManager.Entry)this.entries.get(item);
		if (entry != null) {
			float g = (float)(entry.endTick - entry.startTick);
			float h = (float)entry.endTick - ((float)this.tick + f);
			return MathHelper.clamp(h / g, 0.0F, 1.0F);
		} else {
			return 0.0F;
		}
	}

	public void update() {
		this.tick++;
		if (!this.entries.isEmpty()) {
			Iterator<java.util.Map.Entry<Item, ItemCooldownManager.Entry>> iterator = this.entries.entrySet().iterator();

			while (iterator.hasNext()) {
				java.util.Map.Entry<Item, ItemCooldownManager.Entry> entry = (java.util.Map.Entry<Item, ItemCooldownManager.Entry>)iterator.next();
				if (((ItemCooldownManager.Entry)entry.getValue()).endTick <= this.tick) {
					iterator.remove();
					this.onCooldownUpdate((Item)entry.getKey());
				}
			}
		}
	}

	public void set(Item item, int i) {
		this.entries.put(item, new ItemCooldownManager.Entry(this.tick, this.tick + i));
		this.onCooldownUpdate(item, i);
	}

	public void remove(Item item) {
		this.entries.remove(item);
		this.onCooldownUpdate(item);
	}

	protected void onCooldownUpdate(Item item, int i) {
	}

	protected void onCooldownUpdate(Item item) {
	}

	class Entry {
		private final int startTick;
		private final int endTick;

		private Entry(int i, int j) {
			this.startTick = i;
			this.endTick = j;
		}
	}
}
