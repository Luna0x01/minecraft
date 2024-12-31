package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.function.Function;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class class_4437 {
	private static final SimpleCommandExceptionType field_21828 = new SimpleCommandExceptionType(new TranslatableText("commands.data.merge.failed"));
	private static final DynamicCommandExceptionType field_21829 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.data.get.invalid", object)
	);
	private static final DynamicCommandExceptionType field_21830 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.data.get.unknown", object)
	);
	public static final List<class_4437.class_4438> field_21827 = Lists.newArrayList(new class_4437.class_4438[]{class_4439.field_21831, class_4435.field_21823});

	public static void method_21217(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralArgumentBuilder<class_3915> literalArgumentBuilder = (LiteralArgumentBuilder<class_3915>)CommandManager.method_17529("data")
			.requires(arg -> arg.method_17575(2));

		for (class_4437.class_4438 lv : field_21827) {
			((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(
						lv.method_21229(
							CommandManager.method_17529("merge"),
							argumentBuilder -> argumentBuilder.then(
									CommandManager.method_17530("nbt", class_4119.method_18393())
										.executes(
											commandContext -> method_21216(
													(class_3915)commandContext.getSource(), lv.method_21230(commandContext), class_4119.method_18395(commandContext, "nbt")
												)
										)
								)
						)
					))
					.then(
						lv.method_21229(
							CommandManager.method_17529("get"),
							argumentBuilder -> argumentBuilder.executes(commandContext -> method_21213((class_3915)commandContext.getSource(), lv.method_21230(commandContext)))
									.then(
										((RequiredArgumentBuilder)CommandManager.method_17530("path", class_4124.method_18432())
												.executes(
													commandContext -> method_21221(
															(class_3915)commandContext.getSource(), lv.method_21230(commandContext), class_4124.method_18435(commandContext, "path")
														)
												))
											.then(
												CommandManager.method_17530("scale", DoubleArgumentType.doubleArg())
													.executes(
														commandContext -> method_21215(
																(class_3915)commandContext.getSource(),
																lv.method_21230(commandContext),
																class_4124.method_18435(commandContext, "path"),
																DoubleArgumentType.getDouble(commandContext, "scale")
															)
													)
											)
									)
						)
					))
				.then(
					lv.method_21229(
						CommandManager.method_17529("remove"),
						argumentBuilder -> argumentBuilder.then(
								CommandManager.method_17530("path", class_4124.method_18432())
									.executes(
										commandContext -> method_21214(
												(class_3915)commandContext.getSource(), lv.method_21230(commandContext), class_4124.method_18435(commandContext, "path")
											)
									)
							)
					)
				);
		}

		commandDispatcher.register(literalArgumentBuilder);
	}

	private static int method_21214(class_3915 arg, class_4436 arg2, class_4124.class_4127 arg3) throws CommandSyntaxException {
		NbtCompound nbtCompound = arg2.method_21207();
		NbtCompound nbtCompound2 = nbtCompound.copy();
		arg3.method_18444(nbtCompound);
		if (nbtCompound2.equals(nbtCompound)) {
			throw field_21828.create();
		} else {
			arg2.method_21209(nbtCompound);
			arg.method_17459(arg2.method_21211(), true);
			return 1;
		}
	}

	private static int method_21221(class_3915 arg, class_4436 arg2, class_4124.class_4127 arg3) throws CommandSyntaxException {
		NbtElement nbtElement = arg3.method_18442(arg2.method_21207());
		int i;
		if (nbtElement instanceof AbstractNbtNumber) {
			i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue());
		} else if (nbtElement instanceof AbstractNbtList) {
			i = ((AbstractNbtList)nbtElement).size();
		} else if (nbtElement instanceof NbtCompound) {
			i = ((NbtCompound)nbtElement).getSize();
		} else {
			if (!(nbtElement instanceof NbtString)) {
				throw field_21830.create(arg3.toString());
			}

			i = ((NbtString)nbtElement).asString().length();
		}

		arg.method_17459(arg2.method_21210(nbtElement), false);
		return i;
	}

	private static int method_21215(class_3915 arg, class_4436 arg2, class_4124.class_4127 arg3, double d) throws CommandSyntaxException {
		NbtElement nbtElement = arg3.method_18442(arg2.method_21207());
		if (!(nbtElement instanceof AbstractNbtNumber)) {
			throw field_21829.create(arg3.toString());
		} else {
			int i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue() * d);
			arg.method_17459(arg2.method_21208(arg3, d, i), false);
			return i;
		}
	}

	private static int method_21213(class_3915 arg, class_4436 arg2) throws CommandSyntaxException {
		arg.method_17459(arg2.method_21210(arg2.method_21207()), false);
		return 1;
	}

	private static int method_21216(class_3915 arg, class_4436 arg2, NbtCompound nbtCompound) throws CommandSyntaxException {
		NbtCompound nbtCompound2 = arg2.method_21207();
		NbtCompound nbtCompound3 = nbtCompound2.copy().putAll(nbtCompound);
		if (nbtCompound2.equals(nbtCompound3)) {
			throw field_21828.create();
		} else {
			arg2.method_21209(nbtCompound3);
			arg.method_17459(arg2.method_21211(), true);
			return 1;
		}
	}

	public interface class_4438 {
		class_4436 method_21230(CommandContext<class_3915> commandContext) throws CommandSyntaxException;

		ArgumentBuilder<class_3915, ?> method_21229(
			ArgumentBuilder<class_3915, ?> argumentBuilder, Function<ArgumentBuilder<class_3915, ?>, ArgumentBuilder<class_3915, ?>> function
		);
	}
}
