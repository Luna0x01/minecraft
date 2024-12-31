package net.minecraft;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.json.NbtCompoundJson;

public class class_4439 implements class_4436 {
	private static final SimpleCommandExceptionType field_21832 = new SimpleCommandExceptionType(new TranslatableText("commands.data.entity.invalid"));
	public static final class_4437.class_4438 field_21831 = new class_4437.class_4438() {
		@Override
		public class_4436 method_21230(CommandContext<class_3915> commandContext) throws CommandSyntaxException {
			return new class_4439(class_4062.method_17898(commandContext, "target"));
		}

		@Override
		public ArgumentBuilder<class_3915, ?> method_21229(
			ArgumentBuilder<class_3915, ?> argumentBuilder, Function<ArgumentBuilder<class_3915, ?>, ArgumentBuilder<class_3915, ?>> function
		) {
			return argumentBuilder.then(
				CommandManager.method_17529("entity").then((ArgumentBuilder)function.apply(CommandManager.method_17530("target", class_4062.method_17894())))
			);
		}
	};
	private final Entity field_21833;

	public class_4439(Entity entity) {
		this.field_21833 = entity;
	}

	@Override
	public void method_21209(NbtCompound nbtCompound) throws CommandSyntaxException {
		if (this.field_21833 instanceof PlayerEntity) {
			throw field_21832.create();
		} else {
			UUID uUID = this.field_21833.getUuid();
			this.field_21833.fromNbt(nbtCompound);
			this.field_21833.setUuid(uUID);
		}
	}

	@Override
	public NbtCompound method_21207() {
		return NbtCompoundJson.method_16546(this.field_21833);
	}

	@Override
	public Text method_21211() {
		return new TranslatableText("commands.data.entity.modified", this.field_21833.getName());
	}

	@Override
	public Text method_21210(NbtElement nbtElement) {
		return new TranslatableText("commands.data.entity.query", this.field_21833.getName(), nbtElement.asText());
	}

	@Override
	public Text method_21208(class_4124.class_4127 arg, double d, int i) {
		return new TranslatableText("commands.data.entity.get", arg, this.field_21833.getName(), String.format(Locale.ROOT, "%.2f", d), i);
	}
}
