package net.minecraft.block.entity;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DropperBlockEntity extends DispenserBlockEntity {
	public DropperBlockEntity() {
		super(BlockEntityType.DROPPER);
	}

	@Override
	public Text method_15540() {
		Text text = this.method_15541();
		return (Text)(text != null ? text : new TranslatableText("container.dropper"));
	}

	@Override
	public String getId() {
		return "minecraft:dropper";
	}
}
