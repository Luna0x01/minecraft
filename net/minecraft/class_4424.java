package net.minecraft;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;

public class class_4424 {
	private static final Dynamic2CommandExceptionType field_21754 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.forceload.toobig", object, object2)
	);
	private static final Dynamic2CommandExceptionType field_21755 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.forceload.query.failure", object, object2)
	);
	private static final SimpleCommandExceptionType field_21756 = new SimpleCommandExceptionType(new TranslatableText("commands.forceload.added.failure"));
	private static final SimpleCommandExceptionType field_21757 = new SimpleCommandExceptionType(new TranslatableText("commands.forceload.removed.failure"));

	public static void method_20794(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("forceload")
							.requires(arg -> arg.method_17575(4)))
						.then(
							CommandManager.method_17529("add")
								.then(
									((RequiredArgumentBuilder)CommandManager.method_17530("from", class_4257.method_19369())
											.executes(
												commandContext -> method_20793(
														(class_3915)commandContext.getSource(), class_4257.method_19371(commandContext, "from"), class_4257.method_19371(commandContext, "from"), true
													)
											))
										.then(
											CommandManager.method_17530("to", class_4257.method_19369())
												.executes(
													commandContext -> method_20793(
															(class_3915)commandContext.getSource(), class_4257.method_19371(commandContext, "from"), class_4257.method_19371(commandContext, "to"), true
														)
												)
										)
								)
						))
					.then(
						((LiteralArgumentBuilder)CommandManager.method_17529("remove")
								.then(
									((RequiredArgumentBuilder)CommandManager.method_17530("from", class_4257.method_19369())
											.executes(
												commandContext -> method_20793(
														(class_3915)commandContext.getSource(), class_4257.method_19371(commandContext, "from"), class_4257.method_19371(commandContext, "from"), false
													)
											))
										.then(
											CommandManager.method_17530("to", class_4257.method_19369())
												.executes(
													commandContext -> method_20793(
															(class_3915)commandContext.getSource(), class_4257.method_19371(commandContext, "from"), class_4257.method_19371(commandContext, "to"), false
														)
												)
										)
								))
							.then(CommandManager.method_17529("all").executes(commandContext -> method_20798((class_3915)commandContext.getSource())))
					))
				.then(
					((LiteralArgumentBuilder)CommandManager.method_17529("query").executes(commandContext -> method_20791((class_3915)commandContext.getSource())))
						.then(
							CommandManager.method_17530("pos", class_4257.method_19369())
								.executes(commandContext -> method_20792((class_3915)commandContext.getSource(), class_4257.method_19371(commandContext, "pos")))
						)
				)
		);
	}

	private static int method_20792(class_3915 arg, class_4257.class_4258 arg2) throws CommandSyntaxException {
		ChunkPos chunkPos = new ChunkPos(arg2.field_20925 >> 4, arg2.field_20926 >> 4);
		DimensionType dimensionType = arg.method_17468().method_16393().method_11789();
		boolean bl = arg.method_17473().method_20312(dimensionType).method_16335(chunkPos.x, chunkPos.z);
		if (bl) {
			arg.method_17459(new TranslatableText("commands.forceload.query.success", chunkPos, dimensionType), false);
			return 1;
		} else {
			throw field_21755.create(chunkPos, dimensionType);
		}
	}

	private static int method_20791(class_3915 arg) {
		DimensionType dimensionType = arg.method_17468().method_16393().method_11789();
		LongSet longSet = arg.method_17473().method_20312(dimensionType).method_16329();
		int i = longSet.size();
		if (i > 0) {
			String string = Joiner.on(", ").join(longSet.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
			if (i == 1) {
				arg.method_17459(new TranslatableText("commands.forceload.list.single", dimensionType, string), false);
			} else {
				arg.method_17459(new TranslatableText("commands.forceload.list.multiple", i, dimensionType, string), false);
			}
		} else {
			arg.method_17458(new TranslatableText("commands.forceload.added.none", dimensionType));
		}

		return i;
	}

	private static int method_20798(class_3915 arg) {
		DimensionType dimensionType = arg.method_17468().method_16393().method_11789();
		ServerWorld serverWorld = arg.method_17473().method_20312(dimensionType);
		LongSet longSet = serverWorld.method_16329();
		longSet.forEach(l -> serverWorld.method_16332(ChunkPos.method_16282(l), ChunkPos.method_16283(l), false));
		arg.method_17459(new TranslatableText("commands.forceload.removed.all", dimensionType), true);
		return 0;
	}

	private static int method_20793(class_3915 arg, class_4257.class_4258 arg2, class_4257.class_4258 arg3, boolean bl) throws CommandSyntaxException {
		int i = Math.min(arg2.field_20925, arg3.field_20925);
		int j = Math.min(arg2.field_20926, arg3.field_20926);
		int k = Math.max(arg2.field_20925, arg3.field_20925);
		int l = Math.max(arg2.field_20926, arg3.field_20926);
		if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
			int m = i >> 4;
			int n = j >> 4;
			int o = k >> 4;
			int p = l >> 4;
			long q = ((long)(o - m) + 1L) * ((long)(p - n) + 1L);
			if (q > 256L) {
				throw field_21754.create(256, q);
			} else {
				DimensionType dimensionType = arg.method_17468().method_16393().method_11789();
				ServerWorld serverWorld = arg.method_17473().method_20312(dimensionType);
				ChunkPos chunkPos = null;
				int r = 0;

				for (int s = m; s <= o; s++) {
					for (int t = n; t <= p; t++) {
						boolean bl2 = serverWorld.method_16332(s, t, bl);
						if (bl2) {
							r++;
							if (chunkPos == null) {
								chunkPos = new ChunkPos(s, t);
							}
						}
					}
				}

				if (r == 0) {
					throw (bl ? field_21756 : field_21757).create();
				} else {
					if (r == 1) {
						arg.method_17459(new TranslatableText("commands.forceload." + (bl ? "added" : "removed") + ".single", chunkPos, dimensionType), true);
					} else {
						ChunkPos chunkPos2 = new ChunkPos(m, n);
						ChunkPos chunkPos3 = new ChunkPos(o, p);
						arg.method_17459(new TranslatableText("commands.forceload." + (bl ? "added" : "removed") + ".multiple", r, dimensionType, chunkPos2, chunkPos3), true);
					}

					return r;
				}
			}
		} else {
			throw class_4252.field_20912.create();
		}
	}
}
