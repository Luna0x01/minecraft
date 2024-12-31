package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class class_4311 implements Predicate<ItemStack> {
	private static final Dynamic2CommandExceptionType field_21155 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("arguments.item.overstacked", object, object2)
	);
	private final Item field_21156;
	@Nullable
	private final NbtCompound field_21157;

	public class_4311(Item item, @Nullable NbtCompound nbtCompound) {
		this.field_21156 = item;
		this.field_21157 = nbtCompound;
	}

	public Item method_19701() {
		return this.field_21156;
	}

	public boolean test(ItemStack itemStack) {
		return itemStack.getItem() == this.field_21156 && NbtHelper.areEqual(this.field_21157, itemStack.getNbt(), true);
	}

	public ItemStack method_19702(int i, boolean bl) throws CommandSyntaxException {
		ItemStack itemStack = new ItemStack(this.field_21156, i);
		if (this.field_21157 != null) {
			itemStack.setNbt(this.field_21157);
		}

		if (bl && i > itemStack.getMaxCount()) {
			throw field_21155.create(Registry.ITEM.getId(this.field_21156), itemStack.getMaxCount());
		} else {
			return itemStack;
		}
	}

	public String method_19705() {
		StringBuilder stringBuilder = new StringBuilder(Registry.ITEM.getRawId(this.field_21156));
		if (this.field_21157 != null) {
			stringBuilder.append(this.field_21157);
		}

		return stringBuilder.toString();
	}
}
