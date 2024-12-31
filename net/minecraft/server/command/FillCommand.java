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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class FillCommand {
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.fill.toobig", object, object2)
	);
	private static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.field_10124.getDefaultState(), Collections.emptySet(), null);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.fill.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fill").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
				.then(
					CommandManager.argument("from", BlockPosArgumentType.blockPos())
						.then(
							CommandManager.argument("to", BlockPosArgumentType.blockPos())
								.then(
									((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument(
																"block", BlockStateArgumentType.blockState()
															)
															.executes(
																commandContext -> execute(
																		(ServerCommandSource)commandContext.getSource(),
																		new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")),
																		BlockStateArgumentType.getBlockState(commandContext, "block"),
																		FillCommand.Mode.field_13655,
																		null
																	)
															))
														.then(
															((LiteralArgumentBuilder)CommandManager.literal("replace")
																	.executes(
																		commandContext -> execute(
																				(ServerCommandSource)commandContext.getSource(),
																				new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")),
																				BlockStateArgumentType.getBlockState(commandContext, "block"),
																				FillCommand.Mode.field_13655,
																				null
																			)
																	))
																.then(
																	CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate())
																		.executes(
																			commandContext -> execute(
																					(ServerCommandSource)commandContext.getSource(),
																					new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")),
																					BlockStateArgumentType.getBlockState(commandContext, "block"),
																					FillCommand.Mode.field_13655,
																					BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter")
																				)
																		)
																)
														))
													.then(
														CommandManager.literal("keep")
															.executes(
																commandContext -> execute(
																		(ServerCommandSource)commandContext.getSource(),
																		new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")),
																		BlockStateArgumentType.getBlockState(commandContext, "block"),
																		FillCommand.Mode.field_13655,
																		cachedBlockPosition -> cachedBlockPosition.getWorld().isAir(cachedBlockPosition.getBlockPos())
																	)
															)
													))
												.then(
													CommandManager.literal("outline")
														.executes(
															commandContext -> execute(
																	(ServerCommandSource)commandContext.getSource(),
																	new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")),
																	BlockStateArgumentType.getBlockState(commandContext, "block"),
																	FillCommand.Mode.field_13652,
																	null
																)
														)
												))
											.then(
												CommandManager.literal("hollow")
													.executes(
														commandContext -> execute(
																(ServerCommandSource)commandContext.getSource(),
																new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")),
																BlockStateArgumentType.getBlockState(commandContext, "block"),
																FillCommand.Mode.field_13656,
																null
															)
													)
											))
										.then(
											CommandManager.literal("destroy")
												.executes(
													commandContext -> execute(
															(ServerCommandSource)commandContext.getSource(),
															new BlockBox(BlockPosArgumentType.getLoadedBlockPos(commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "to")),
															BlockStateArgumentType.getBlockState(commandContext, "block"),
															FillCommand.Mode.field_13651,
															null
														)
												)
										)
								)
						)
				)
		);
	}

	private static int execute(
		ServerCommandSource serverCommandSource,
		BlockBox blockBox,
		BlockStateArgument blockStateArgument,
		FillCommand.Mode mode,
		@Nullable Predicate<CachedBlockPosition> predicate
	) throws CommandSyntaxException {
		int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
		if (i > 32768) {
			throw TOOBIG_EXCEPTION.create(32768, i);
		} else {
			List<BlockPos> list = Lists.newArrayList();
			ServerWorld serverWorld = serverCommandSource.getWorld();
			int j = 0;

			for (BlockPos blockPos : BlockPos.iterate(blockBox.minX, blockBox.minY, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ)) {
				if (predicate == null || predicate.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
					BlockStateArgument blockStateArgument2 = mode.filter.filter(blockBox, blockPos, blockStateArgument, serverWorld);
					if (blockStateArgument2 != null) {
						BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
						Clearable.clear(blockEntity);
						if (blockStateArgument2.setBlockState(serverWorld, blockPos, 2)) {
							list.add(blockPos.toImmutable());
							j++;
						}
					}
				}
			}

			for (BlockPos blockPos2 : list) {
				Block block = serverWorld.getBlockState(blockPos2).getBlock();
				serverWorld.updateNeighbors(blockPos2, block);
			}

			if (j == 0) {
				throw FAILED_EXCEPTION.create();
			} else {
				serverCommandSource.sendFeedback(new TranslatableText("commands.fill.success", j), true);
				return j;
			}
		}
	}

	static enum Mode {
		field_13655((blockBox, blockPos, blockStateArgument, serverWorld) -> blockStateArgument),
		field_13652(
			(blockBox, blockPos, blockStateArgument, serverWorld) -> blockPos.getX() != blockBox.minX
						&& blockPos.getX() != blockBox.maxX
						&& blockPos.getY() != blockBox.minY
						&& blockPos.getY() != blockBox.maxY
						&& blockPos.getZ() != blockBox.minZ
						&& blockPos.getZ() != blockBox.maxZ
					? null
					: blockStateArgument
		),
		field_13656(
			(blockBox, blockPos, blockStateArgument, serverWorld) -> blockPos.getX() != blockBox.minX
						&& blockPos.getX() != blockBox.maxX
						&& blockPos.getY() != blockBox.minY
						&& blockPos.getY() != blockBox.maxY
						&& blockPos.getZ() != blockBox.minZ
						&& blockPos.getZ() != blockBox.maxZ
					? FillCommand.AIR_BLOCK_ARGUMENT
					: blockStateArgument
		),
		field_13651((blockBox, blockPos, blockStateArgument, serverWorld) -> {
			serverWorld.breakBlock(blockPos, true);
			return blockStateArgument;
		});

		public final SetBlockCommand.Filter filter;

		private Mode(SetBlockCommand.Filter filter) {
			this.filter = filter;
		}
	}
}
