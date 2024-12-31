package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.class_3915;
import net.minecraft.class_4284;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
	private static final SimpleCommandExceptionType field_21815 = new SimpleCommandExceptionType(new TranslatableText("commands.worldborder.center.failed"));
	private static final SimpleCommandExceptionType field_21816 = new SimpleCommandExceptionType(new TranslatableText("commands.worldborder.set.failed.nochange"));
	private static final SimpleCommandExceptionType field_21817 = new SimpleCommandExceptionType(new TranslatableText("commands.worldborder.set.failed.small."));
	private static final SimpleCommandExceptionType field_21818 = new SimpleCommandExceptionType(new TranslatableText("commands.worldborder.set.failed.big."));
	private static final SimpleCommandExceptionType field_21819 = new SimpleCommandExceptionType(new TranslatableText("commands.worldborder.warning.time.failed"));
	private static final SimpleCommandExceptionType field_21820 = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.warning.distance.failed")
	);
	private static final SimpleCommandExceptionType field_21821 = new SimpleCommandExceptionType(new TranslatableText("commands.worldborder.damage.buffer.failed"));
	private static final SimpleCommandExceptionType field_21822 = new SimpleCommandExceptionType(new TranslatableText("commands.worldborder.damage.amount.failed"));

	public static void method_21192(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529(
											"worldborder"
										)
										.requires(arg -> arg.method_17575(2)))
									.then(
										CommandManager.method_17529("add")
											.then(
												((RequiredArgumentBuilder)CommandManager.method_17530("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F))
														.executes(
															commandContext -> method_21188(
																	(class_3915)commandContext.getSource(),
																	((class_3915)commandContext.getSource()).method_17468().method_8524().getOldSize()
																		+ (double)FloatArgumentType.getFloat(commandContext, "distance"),
																	0L
																)
														))
													.then(
														CommandManager.method_17530("time", IntegerArgumentType.integer(0))
															.executes(
																commandContext -> method_21188(
																		(class_3915)commandContext.getSource(),
																		((class_3915)commandContext.getSource()).method_17468().method_8524().getOldSize()
																			+ (double)FloatArgumentType.getFloat(commandContext, "distance"),
																		((class_3915)commandContext.getSource()).method_17468().method_8524().getInterpolationDuration()
																			+ (long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L
																	)
															)
													)
											)
									))
								.then(
									CommandManager.method_17529("set")
										.then(
											((RequiredArgumentBuilder)CommandManager.method_17530("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F))
													.executes(
														commandContext -> method_21188((class_3915)commandContext.getSource(), (double)FloatArgumentType.getFloat(commandContext, "distance"), 0L)
													))
												.then(
													CommandManager.method_17530("time", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> method_21188(
																	(class_3915)commandContext.getSource(),
																	(double)FloatArgumentType.getFloat(commandContext, "distance"),
																	(long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L
																)
														)
												)
										)
								))
							.then(
								CommandManager.method_17529("center")
									.then(
										CommandManager.method_17530("pos", class_4284.method_19539())
											.executes(commandContext -> method_21191((class_3915)commandContext.getSource(), class_4284.method_19541(commandContext, "pos")))
									)
							))
						.then(
							((LiteralArgumentBuilder)CommandManager.method_17529("damage")
									.then(
										CommandManager.method_17529("amount")
											.then(
												CommandManager.method_17530("damagePerBlock", FloatArgumentType.floatArg(0.0F))
													.executes(commandContext -> method_21195((class_3915)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "damagePerBlock")))
											)
									))
								.then(
									CommandManager.method_17529("buffer")
										.then(
											CommandManager.method_17530("distance", FloatArgumentType.floatArg(0.0F))
												.executes(commandContext -> method_21189((class_3915)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance")))
										)
								)
						))
					.then(CommandManager.method_17529("get").executes(commandContext -> method_21187((class_3915)commandContext.getSource()))))
				.then(
					((LiteralArgumentBuilder)CommandManager.method_17529("warning")
							.then(
								CommandManager.method_17529("distance")
									.then(
										CommandManager.method_17530("distance", IntegerArgumentType.integer(0))
											.executes(commandContext -> method_21196((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "distance")))
									)
							))
						.then(
							CommandManager.method_17529("time")
								.then(
									CommandManager.method_17530("time", IntegerArgumentType.integer(0))
										.executes(commandContext -> method_21190((class_3915)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
								)
						)
				)
		);
	}

	private static int method_21189(class_3915 arg, float f) throws CommandSyntaxException {
		WorldBorder worldBorder = arg.method_17468().method_8524();
		if (worldBorder.getSafeZone() == (double)f) {
			throw field_21821.create();
		} else {
			worldBorder.setSafeZone((double)f);
			arg.method_17459(new TranslatableText("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", f)), true);
			return (int)f;
		}
	}

	private static int method_21195(class_3915 arg, float f) throws CommandSyntaxException {
		WorldBorder worldBorder = arg.method_17468().method_8524();
		if (worldBorder.getBorderDamagePerBlock() == (double)f) {
			throw field_21822.create();
		} else {
			worldBorder.setDamagePerBlock((double)f);
			arg.method_17459(new TranslatableText("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", f)), true);
			return (int)f;
		}
	}

	private static int method_21190(class_3915 arg, int i) throws CommandSyntaxException {
		WorldBorder worldBorder = arg.method_17468().method_8524();
		if (worldBorder.getWarningTime() == i) {
			throw field_21819.create();
		} else {
			worldBorder.setWarningTime(i);
			arg.method_17459(new TranslatableText("commands.worldborder.warning.time.success", i), true);
			return i;
		}
	}

	private static int method_21196(class_3915 arg, int i) throws CommandSyntaxException {
		WorldBorder worldBorder = arg.method_17468().method_8524();
		if (worldBorder.getWarningBlocks() == i) {
			throw field_21820.create();
		} else {
			worldBorder.setWarningBlocks(i);
			arg.method_17459(new TranslatableText("commands.worldborder.warning.distance.success", i), true);
			return i;
		}
	}

	private static int method_21187(class_3915 arg) {
		double d = arg.method_17468().method_8524().getOldSize();
		arg.method_17459(new TranslatableText("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
		return MathHelper.floor(d + 0.5);
	}

	private static int method_21191(class_3915 arg, Vec2f vec2f) throws CommandSyntaxException {
		WorldBorder worldBorder = arg.method_17468().method_8524();
		if (worldBorder.getCenterX() == (double)vec2f.x && worldBorder.getCenterZ() == (double)vec2f.y) {
			throw field_21815.create();
		} else {
			worldBorder.setCenter((double)vec2f.x, (double)vec2f.y);
			arg.method_17459(
				new TranslatableText("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", vec2f.x), String.format("%.2f", vec2f.y)), true
			);
			return 0;
		}
	}

	private static int method_21188(class_3915 arg, double d, long l) throws CommandSyntaxException {
		WorldBorder worldBorder = arg.method_17468().method_8524();
		double e = worldBorder.getOldSize();
		if (e == d) {
			throw field_21816.create();
		} else if (d < 1.0) {
			throw field_21817.create();
		} else if (d > 6.0E7) {
			throw field_21818.create();
		} else {
			if (l > 0L) {
				worldBorder.interpolateSize(e, d, l);
				if (d > e) {
					arg.method_17459(new TranslatableText("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
				} else {
					arg.method_17459(new TranslatableText("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
				}
			} else {
				worldBorder.setSize(d);
				arg.method_17459(new TranslatableText("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", d)), true);
			}

			return (int)(d - e);
		}
	}
}
