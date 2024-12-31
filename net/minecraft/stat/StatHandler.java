package net.minecraft.stat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.class_4472;
import net.minecraft.entity.player.PlayerEntity;

public class StatHandler {
	protected final Object2IntMap<class_4472<?>> field_22141 = Object2IntMaps.synchronize(new Object2IntOpenHashMap());

	public StatHandler() {
		this.field_22141.defaultReturnValue(0);
	}

	public void method_8302(PlayerEntity playerEntity, class_4472<?> arg, int i) {
		this.method_8300(playerEntity, arg, this.method_21434(arg) + i);
	}

	public void method_8300(PlayerEntity playerEntity, class_4472<?> arg, int i) {
		this.field_22141.put(arg, i);
	}

	public <T> int method_21435(StatType<T> statType, T object) {
		return statType.method_21425(object) ? this.method_21434(statType.method_21429(object)) : 0;
	}

	public int method_21434(class_4472<?> arg) {
		return this.field_22141.getInt(arg);
	}
}
