package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class class_4488 implements ResourceReloadListener {
	private final class_4481<Block> field_22242 = new class_4481<>(Registry.BLOCK, "tags/blocks", "block");
	private final class_4481<Item> field_22243 = new class_4481<>(Registry.ITEM, "tags/items", "item");
	private final class_4481<Fluid> field_22244 = new class_4481<>(Registry.FLUID, "tags/fluids", "fluid");

	public class_4481<Block> method_21492() {
		return this.field_22242;
	}

	public class_4481<Item> method_21494() {
		return this.field_22243;
	}

	public class_4481<Fluid> method_21496() {
		return this.field_22244;
	}

	public void method_21497() {
		this.field_22242.method_21489();
		this.field_22243.method_21489();
		this.field_22244.method_21489();
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.method_21497();
		this.field_22242.method_21487(resourceManager);
		this.field_22243.method_21487(resourceManager);
		this.field_22244.method_21487(resourceManager);
		BlockTags.setContainer(this.field_22242);
		ItemTags.method_21454(this.field_22243);
		FluidTags.setContainer(this.field_22244);
	}

	public void method_21493(PacketByteBuf packetByteBuf) {
		this.field_22242.method_21459(packetByteBuf);
		this.field_22243.method_21459(packetByteBuf);
		this.field_22244.method_21459(packetByteBuf);
	}

	public static class_4488 method_21495(PacketByteBuf packetByteBuf) {
		class_4488 lv = new class_4488();
		lv.method_21492().method_21460(packetByteBuf);
		lv.method_21494().method_21460(packetByteBuf);
		lv.method_21496().method_21460(packetByteBuf);
		return lv;
	}
}
