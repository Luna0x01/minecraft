package net.minecraft;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class class_4435 implements class_4436 {
	private static final SimpleCommandExceptionType field_21824 = new SimpleCommandExceptionType(new TranslatableText("commands.data.block.invalid"));
	public static final class_4437.class_4438 field_21823 = new class_4437.class_4438() {
		@Override
		public class_4436 method_21230(CommandContext<class_3915> commandContext) throws CommandSyntaxException {
			BlockPos blockPos = class_4252.method_19360(commandContext, "pos");
			BlockEntity blockEntity = ((class_3915)commandContext.getSource()).method_17468().getBlockEntity(blockPos);
			if (blockEntity == null) {
				throw class_4435.field_21824.create();
			} else {
				return new class_4435(blockEntity, blockPos);
			}
		}

		@Override
		public ArgumentBuilder<class_3915, ?> method_21229(
			ArgumentBuilder<class_3915, ?> argumentBuilder, Function<ArgumentBuilder<class_3915, ?>, ArgumentBuilder<class_3915, ?>> function
		) {
			return argumentBuilder.then(
				CommandManager.method_17529("block").then((ArgumentBuilder)function.apply(CommandManager.method_17530("pos", class_4252.method_19358())))
			);
		}
	};
	private final BlockEntity field_21825;
	private final BlockPos field_21826;

	public class_4435(BlockEntity blockEntity, BlockPos blockPos) {
		this.field_21825 = blockEntity;
		this.field_21826 = blockPos;
	}

	@Override
	public void method_21209(NbtCompound nbtCompound) {
		nbtCompound.putInt("x", this.field_21826.getX());
		nbtCompound.putInt("y", this.field_21826.getY());
		nbtCompound.putInt("z", this.field_21826.getZ());
		this.field_21825.fromNbt(nbtCompound);
		this.field_21825.markDirty();
		BlockState blockState = this.field_21825.getEntityWorld().getBlockState(this.field_21826);
		this.field_21825.getEntityWorld().method_11481(this.field_21826, blockState, blockState, 3);
	}

	@Override
	public NbtCompound method_21207() {
		return this.field_21825.toNbt(new NbtCompound());
	}

	@Override
	public Text method_21211() {
		return new TranslatableText("commands.data.block.modified", this.field_21826.getX(), this.field_21826.getY(), this.field_21826.getZ());
	}

	@Override
	public Text method_21210(NbtElement nbtElement) {
		return new TranslatableText("commands.data.block.query", this.field_21826.getX(), this.field_21826.getY(), this.field_21826.getZ(), nbtElement.asText());
	}

	@Override
	public Text method_21208(class_4124.class_4127 arg, double d, int i) {
		return new TranslatableText(
			"commands.data.block.get", arg, this.field_21826.getX(), this.field_21826.getY(), this.field_21826.getZ(), String.format(Locale.ROOT, "%.2f", d), i
		);
	}
}
