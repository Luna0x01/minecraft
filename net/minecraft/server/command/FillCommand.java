package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4213;
import net.minecraft.class_4220;
import net.minecraft.class_4229;
import net.minecraft.class_4252;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class FillCommand {
	private static final Dynamic2CommandExceptionType field_21745 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.fill.toobig", object, object2)
	);
	private static final class_4213 field_21746 = new class_4213(Blocks.AIR.getDefaultState(), Collections.emptySet(), null);
	private static final SimpleCommandExceptionType field_21747 = new SimpleCommandExceptionType(new TranslatableText("commands.fill.failed"));

	public static void method_20778(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("fill").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("from", class_4252.method_19358())
						.then(
							CommandManager.method_17530("to", class_4252.method_19358())
								.then(
									((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
																"block", class_4229.method_19207()
															)
															.executes(
																commandContext -> method_20777(
																		(class_3915)commandContext.getSource(),
																		new BlockBox(class_4252.method_19360(commandContext, "from"), class_4252.method_19360(commandContext, "to")),
																		class_4229.method_19209(commandContext, "block"),
																		FillCommand.class_4423.REPLACE,
																		null
																	)
															))
														.then(
															((LiteralArgumentBuilder)CommandManager.method_17529("replace")
																	.executes(
																		commandContext -> method_20777(
																				(class_3915)commandContext.getSource(),
																				new BlockBox(class_4252.method_19360(commandContext, "from"), class_4252.method_19360(commandContext, "to")),
																				class_4229.method_19209(commandContext, "block"),
																				FillCommand.class_4423.REPLACE,
																				null
																			)
																	))
																.then(
																	CommandManager.method_17530("filter", class_4220.method_19107())
																		.executes(
																			commandContext -> method_20777(
																					(class_3915)commandContext.getSource(),
																					new BlockBox(class_4252.method_19360(commandContext, "from"), class_4252.method_19360(commandContext, "to")),
																					class_4229.method_19209(commandContext, "block"),
																					FillCommand.class_4423.REPLACE,
																					class_4220.method_19109(commandContext, "filter")
																				)
																		)
																)
														))
													.then(
														CommandManager.method_17529("keep")
															.executes(
																commandContext -> method_20777(
																		(class_3915)commandContext.getSource(),
																		new BlockBox(class_4252.method_19360(commandContext, "from"), class_4252.method_19360(commandContext, "to")),
																		class_4229.method_19209(commandContext, "block"),
																		FillCommand.class_4423.REPLACE,
																		cachedBlockPosition -> cachedBlockPosition.method_16937().method_8579(cachedBlockPosition.getPos())
																	)
															)
													))
												.then(
													CommandManager.method_17529("outline")
														.executes(
															commandContext -> method_20777(
																	(class_3915)commandContext.getSource(),
																	new BlockBox(class_4252.method_19360(commandContext, "from"), class_4252.method_19360(commandContext, "to")),
																	class_4229.method_19209(commandContext, "block"),
																	FillCommand.class_4423.OUTLINE,
																	null
																)
														)
												))
											.then(
												CommandManager.method_17529("hollow")
													.executes(
														commandContext -> method_20777(
																(class_3915)commandContext.getSource(),
																new BlockBox(class_4252.method_19360(commandContext, "from"), class_4252.method_19360(commandContext, "to")),
																class_4229.method_19209(commandContext, "block"),
																FillCommand.class_4423.HOLLOW,
																null
															)
													)
											))
										.then(
											CommandManager.method_17529("destroy")
												.executes(
													commandContext -> method_20777(
															(class_3915)commandContext.getSource(),
															new BlockBox(class_4252.method_19360(commandContext, "from"), class_4252.method_19360(commandContext, "to")),
															class_4229.method_19209(commandContext, "block"),
															FillCommand.class_4423.DESTROY,
															null
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_20777(
		class_3915 arg, BlockBox blockBox, class_4213 arg2, FillCommand.class_4423 arg3, @Nullable Predicate<CachedBlockPosition> predicate
	) throws CommandSyntaxException {
		int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
		if (i > 32768) {
			throw field_21745.create(32768, i);
		} else {
			List<BlockPos> list = Lists.newArrayList();
			ServerWorld serverWorld = arg.method_17468();
			int j = 0;

			for (BlockPos blockPos : BlockPos.Mutable.iterate(blockBox.minX, blockBox.minY, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ)) {
				if (predicate == null || predicate.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
					class_4213 lv = arg3.field_21752.filter(blockBox, blockPos, arg2, serverWorld);
					if (lv != null) {
						BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
						if (blockEntity != null && blockEntity instanceof Inventory) {
							((Inventory)blockEntity).clear();
						}

						if (lv.method_19039(serverWorld, blockPos, 2)) {
							list.add(blockPos.toImmutable());
							j++;
						}
					}
				}
			}

			for (BlockPos blockPos2 : list) {
				Block block = serverWorld.getBlockState(blockPos2).getBlock();
				serverWorld.method_16342(blockPos2, block);
			}

			if (j == 0) {
				throw field_21747.create();
			} else {
				arg.method_17459(new TranslatableText("commands.fill.success", j), true);
				return j;
			}
		}
	}

	static enum class_4423 {
		REPLACE((blockBox, blockPos, arg, serverWorld) -> arg),
		OUTLINE(
			(blockBox, blockPos, arg, serverWorld) -> blockPos.getX() != blockBox.minX
						&& blockPos.getX() != blockBox.maxX
						&& blockPos.getY() != blockBox.minY
						&& blockPos.getY() != blockBox.maxY
						&& blockPos.getZ() != blockBox.minZ
						&& blockPos.getZ() != blockBox.maxZ
					? null
					: arg
		),
		HOLLOW(
			(blockBox, blockPos, arg, serverWorld) -> blockPos.getX() != blockBox.minX
						&& blockPos.getX() != blockBox.maxX
						&& blockPos.getY() != blockBox.minY
						&& blockPos.getY() != blockBox.maxY
						&& blockPos.getZ() != blockBox.minZ
						&& blockPos.getZ() != blockBox.maxZ
					? FillCommand.field_21746
					: arg
		),
		DESTROY((blockBox, blockPos, arg, serverWorld) -> {
			serverWorld.method_8535(blockPos, true);
			return arg;
		});

		public final SetBlockCommand.class_4427 field_21752;

		private class_4423(SetBlockCommand.class_4427 arg) {
			this.field_21752 = arg;
		}
	}
}
