package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
	private static final SimpleCommandExceptionType CENTER_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.center.failed")
	);
	private static final SimpleCommandExceptionType SET_FAILED_NO_CHANGE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.set.failed.nochange")
	);
	private static final SimpleCommandExceptionType SET_FAILED_SMALL_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.set.failed.small")
	);
	private static final SimpleCommandExceptionType SET_FAILED_BIG_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.set.failed.big", 5.999997E7F)
	);
	private static final SimpleCommandExceptionType WARNING_TIME_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.warning.time.failed")
	);
	private static final SimpleCommandExceptionType WARNING_DISTANCE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.warning.distance.failed")
	);
	private static final SimpleCommandExceptionType DAMAGE_BUFFER_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.damage.buffer.failed")
	);
	private static final SimpleCommandExceptionType DAMAGE_AMOUNT_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.worldborder.damage.amount.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal(
											"worldborder"
										)
										.requires(source -> source.hasPermissionLevel(2)))
									.then(
										CommandManager.literal("add")
											.then(
												((RequiredArgumentBuilder)CommandManager.argument("distance", DoubleArgumentType.doubleArg(-5.999997E7F, 5.999997E7F))
														.executes(
															context -> executeSet(
																	(ServerCommandSource)context.getSource(),
																	((ServerCommandSource)context.getSource()).getWorld().getWorldBorder().getSize() + DoubleArgumentType.getDouble(context, "distance"),
																	0L
																)
														))
													.then(
														CommandManager.argument("time", IntegerArgumentType.integer(0))
															.executes(
																context -> executeSet(
																		(ServerCommandSource)context.getSource(),
																		((ServerCommandSource)context.getSource()).getWorld().getWorldBorder().getSize() + DoubleArgumentType.getDouble(context, "distance"),
																		((ServerCommandSource)context.getSource()).getWorld().getWorldBorder().getSizeLerpTime()
																			+ (long)IntegerArgumentType.getInteger(context, "time") * 1000L
																	)
															)
													)
											)
									))
								.then(
									CommandManager.literal("set")
										.then(
											((RequiredArgumentBuilder)CommandManager.argument("distance", DoubleArgumentType.doubleArg(-5.999997E7F, 5.999997E7F))
													.executes(context -> executeSet((ServerCommandSource)context.getSource(), DoubleArgumentType.getDouble(context, "distance"), 0L)))
												.then(
													CommandManager.argument("time", IntegerArgumentType.integer(0))
														.executes(
															context -> executeSet(
																	(ServerCommandSource)context.getSource(),
																	DoubleArgumentType.getDouble(context, "distance"),
																	(long)IntegerArgumentType.getInteger(context, "time") * 1000L
																)
														)
												)
										)
								))
							.then(
								CommandManager.literal("center")
									.then(
										CommandManager.argument("pos", Vec2ArgumentType.vec2())
											.executes(context -> executeCenter((ServerCommandSource)context.getSource(), Vec2ArgumentType.getVec2(context, "pos")))
									)
							))
						.then(
							((LiteralArgumentBuilder)CommandManager.literal("damage")
									.then(
										CommandManager.literal("amount")
											.then(
												CommandManager.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F))
													.executes(context -> executeDamage((ServerCommandSource)context.getSource(), FloatArgumentType.getFloat(context, "damagePerBlock")))
											)
									))
								.then(
									CommandManager.literal("buffer")
										.then(
											CommandManager.argument("distance", FloatArgumentType.floatArg(0.0F))
												.executes(context -> executeBuffer((ServerCommandSource)context.getSource(), FloatArgumentType.getFloat(context, "distance")))
										)
								)
						))
					.then(CommandManager.literal("get").executes(context -> executeGet((ServerCommandSource)context.getSource()))))
				.then(
					((LiteralArgumentBuilder)CommandManager.literal("warning")
							.then(
								CommandManager.literal("distance")
									.then(
										CommandManager.argument("distance", IntegerArgumentType.integer(0))
											.executes(context -> executeWarningDistance((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "distance")))
									)
							))
						.then(
							CommandManager.literal("time")
								.then(
									CommandManager.argument("time", IntegerArgumentType.integer(0))
										.executes(context -> executeWarningTime((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "time")))
								)
						)
				)
		);
	}

	private static int executeBuffer(ServerCommandSource source, float distance) throws CommandSyntaxException {
		WorldBorder worldBorder = source.getWorld().getWorldBorder();
		if (worldBorder.getSafeZone() == (double)distance) {
			throw DAMAGE_BUFFER_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setSafeZone((double)distance);
			source.sendFeedback(new TranslatableText("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", distance)), true);
			return (int)distance;
		}
	}

	private static int executeDamage(ServerCommandSource source, float damagePerBlock) throws CommandSyntaxException {
		WorldBorder worldBorder = source.getWorld().getWorldBorder();
		if (worldBorder.getDamagePerBlock() == (double)damagePerBlock) {
			throw DAMAGE_AMOUNT_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setDamagePerBlock((double)damagePerBlock);
			source.sendFeedback(new TranslatableText("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", damagePerBlock)), true);
			return (int)damagePerBlock;
		}
	}

	private static int executeWarningTime(ServerCommandSource source, int time) throws CommandSyntaxException {
		WorldBorder worldBorder = source.getWorld().getWorldBorder();
		if (worldBorder.getWarningTime() == time) {
			throw WARNING_TIME_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setWarningTime(time);
			source.sendFeedback(new TranslatableText("commands.worldborder.warning.time.success", time), true);
			return time;
		}
	}

	private static int executeWarningDistance(ServerCommandSource source, int distance) throws CommandSyntaxException {
		WorldBorder worldBorder = source.getWorld().getWorldBorder();
		if (worldBorder.getWarningBlocks() == distance) {
			throw WARNING_DISTANCE_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setWarningBlocks(distance);
			source.sendFeedback(new TranslatableText("commands.worldborder.warning.distance.success", distance), true);
			return distance;
		}
	}

	private static int executeGet(ServerCommandSource source) {
		double d = source.getWorld().getWorldBorder().getSize();
		source.sendFeedback(new TranslatableText("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
		return MathHelper.floor(d + 0.5);
	}

	private static int executeCenter(ServerCommandSource source, Vec2f pos) throws CommandSyntaxException {
		WorldBorder worldBorder = source.getWorld().getWorldBorder();
		if (worldBorder.getCenterX() == (double)pos.x && worldBorder.getCenterZ() == (double)pos.y) {
			throw CENTER_FAILED_EXCEPTION.create();
		} else {
			worldBorder.setCenter((double)pos.x, (double)pos.y);
			source.sendFeedback(
				new TranslatableText("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", pos.x), String.format("%.2f", pos.y)), true
			);
			return 0;
		}
	}

	private static int executeSet(ServerCommandSource source, double distance, long time) throws CommandSyntaxException {
		WorldBorder worldBorder = source.getWorld().getWorldBorder();
		double d = worldBorder.getSize();
		if (d == distance) {
			throw SET_FAILED_NO_CHANGE_EXCEPTION.create();
		} else if (distance < 1.0) {
			throw SET_FAILED_SMALL_EXCEPTION.create();
		} else if (distance > 5.999997E7F) {
			throw SET_FAILED_BIG_EXCEPTION.create();
		} else {
			if (time > 0L) {
				worldBorder.interpolateSize(d, distance, time);
				if (distance > d) {
					source.sendFeedback(new TranslatableText("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", distance), Long.toString(time / 1000L)), true);
				} else {
					source.sendFeedback(
						new TranslatableText("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", distance), Long.toString(time / 1000L)), true
					);
				}
			} else {
				worldBorder.setSize(distance);
				source.sendFeedback(new TranslatableText("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", distance)), true);
			}

			return (int)(distance - d);
		}
	}
}
