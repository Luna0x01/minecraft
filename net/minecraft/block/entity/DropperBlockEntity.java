package net.minecraft.block.entity;

public class DropperBlockEntity extends DispenserBlockEntity {
	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.customName : "container.dropper";
	}

	@Override
	public String getId() {
		return "minecraft:dropper";
	}
}
