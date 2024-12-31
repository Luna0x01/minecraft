package net.minecraft.client.render.debug;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ai.pathing.PathMinHeap;

public class class_2891 {
	private final MinecraftClient client;
	private Map<Integer, PathMinHeap> field_13623 = new HashMap();
	private Map<Integer, Float> field_13624 = new HashMap();
	private Map<Integer, Long> field_13625 = new HashMap();
	private static float field_13626 = 40.0F;

	public class_2891(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_12434(int i, PathMinHeap pathMinHeap, float f) {
		this.field_13623.put(i, pathMinHeap);
		this.field_13625.put(i, System.currentTimeMillis());
		this.field_13624.put(i, f);
	}
}
