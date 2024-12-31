package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class CloneCommand {
	private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.overlap"));
	private static final Dynamic2CommandExceptionType TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.clone.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.failed"));
	public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = cachedBlockPosition -> !cachedBlockPosition.getBlockState().isAir();

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clone").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
				.then(
					CommandManager.argument("begin", BlockPosArgumentType.blockPos())
						.then(
							CommandManager.argument("end", BlockPosArgumentType.blockPos())
								.then(
									((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos())
													.executes(
														commandContext -> execute(
																(ServerCommandSource)commandContext.getSource(),
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																cachedBlockPosition -> true,
																CloneCommand.Mode.field_13499
															)
													))
												.then(
													((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replace")
																	.executes(
																		commandContext -> execute(
																				(ServerCommandSource)commandContext.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																				cachedBlockPosition -> true,
																				CloneCommand.Mode.field_13499
																			)
																	))
																.then(
																	CommandManager.literal("force")
																		.executes(
																			commandContext -> execute(
																					(ServerCommandSource)commandContext.getSource(),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																					cachedBlockPosition -> true,
																					CloneCommand.Mode.field_13497
																				)
																		)
																))
															.then(
																CommandManager.literal("move")
																	.executes(
																		commandContext -> execute(
																				(ServerCommandSource)commandContext.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																				cachedBlockPosition -> true,
																				CloneCommand.Mode.field_13500
																			)
																	)
															))
														.then(
															CommandManager.literal("normal")
																.executes(
																	commandContext -> execute(
																			(ServerCommandSource)commandContext.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																			cachedBlockPosition -> true,
																			CloneCommand.Mode.field_13499
																		)
																)
														)
												))
											.then(
												((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("masked")
																.executes(
																	commandContext -> execute(
																			(ServerCommandSource)commandContext.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																			IS_AIR_PREDICATE,
																			CloneCommand.Mode.field_13499
																		)
																))
															.then(
																CommandManager.literal("force")
																	.executes(
																		commandContext -> execute(
																				(ServerCommandSource)commandContext.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																				IS_AIR_PREDICATE,
																				CloneCommand.Mode.field_13497
																			)
																	)
															))
														.then(
															CommandManager.literal("move")
																.executes(
																	commandContext -> execute(
																			(ServerCommandSource)commandContext.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																			IS_AIR_PREDICATE,
																			CloneCommand.Mode.field_13500
																		)
																)
														))
													.then(
														CommandManager.literal("normal")
															.executes(
																commandContext -> execute(
																		(ServerCommandSource)commandContext.getSource(),
																		BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																		BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																		BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																		IS_AIR_PREDICATE,
																		CloneCommand.Mode.field_13499
																	)
															)
													)
											))
										.then(
											CommandManager.literal("filtered")
												.then(
													((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument(
																		"filter", BlockPredicateArgumentType.blockPredicate()
																	)
																	.executes(
																		commandContext -> execute(
																				(ServerCommandSource)commandContext.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																				BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																				CloneCommand.Mode.field_13499
																			)
																	))
																.then(
																	CommandManager.literal("force")
																		.executes(
																			commandContext -> execute(
																					(ServerCommandSource)commandContext.getSource(),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																					BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																					CloneCommand.Mode.field_13497
																				)
																		)
																))
															.then(
																CommandManager.literal("move")
																	.executes(
																		commandContext -> execute(
																				(ServerCommandSource)commandContext.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																				BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																				CloneCommand.Mode.field_13500
																			)
																	)
															))
														.then(
															CommandManager.literal("normal")
																.executes(
																	commandContext -> execute(
																			(ServerCommandSource)commandContext.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "destination"),
																			BlockPredicateArgumentType.getBlockPredicate(commandContext, "filter"),
																			CloneCommand.Mode.field_13499
																		)
																)
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
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockPos blockPos3,
		Predicate<CachedBlockPosition> predicate,
		CloneCommand.Mode mode
	) throws CommandSyntaxException {
		BlockBox blockBox = new BlockBox(blockPos, blockPos2);
		BlockPos blockPos4 = blockPos3.add(blockBox.getDimensions());
		BlockBox blockBox2 = new BlockBox(blockPos3, blockPos4);
		if (!mode.allowsOverlap() && blockBox2.intersects(blockBox)) {
			throw OVERLAP_EXCEPTION.create();
		} else {
			int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
			if (i > 32768) {
				throw TOOBIG_EXCEPTION.create(32768, i);
			} else {
				ServerWorld serverWorld = serverCommandSource.getWorld();
				if (serverWorld.isRegionLoaded(blockPos, blockPos2) && serverWorld.isRegionLoaded(blockPos3, blockPos4)) {
					List<CloneCommand.BlockInfo> list = Lists.newArrayList();
					List<CloneCommand.BlockInfo> list2 = Lists.newArrayList();
					List<CloneCommand.BlockInfo> list3 = Lists.newArrayList();
					Deque<BlockPos> deque = Lists.newLinkedList();
					BlockPos blockPos5 = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);

					for (int j = blockBox.minZ; j <= blockBox.maxZ; j++) {
						for (int k = blockBox.minY; k <= blockBox.maxY; k++) {
							for (int l = blockBox.minX; l <= blockBox.maxX; l++) {
								BlockPos blockPos6 = new BlockPos(l, k, j);
								BlockPos blockPos7 = blockPos6.add(blockPos5);
								CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos6, false);
								BlockState blockState = cachedBlockPosition.getBlockState();
								if (predicate.test(cachedBlockPosition)) {
									BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos6);
									if (blockEntity != null) {
										CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
										list2.add(new CloneCommand.BlockInfo(blockPos7, blockState, compoundTag));
										deque.addLast(blockPos6);
									} else if (!blockState.isFullOpaque(serverWorld, blockPos6) && !blockState.isFullCube(serverWorld, blockPos6)) {
										list3.add(new CloneCommand.BlockInfo(blockPos7, blockState, null));
										deque.addFirst(blockPos6);
									} else {
										list.add(new CloneCommand.BlockInfo(blockPos7, blockState, null));
										deque.addLast(blockPos6);
									}
								}
							}
						}
					}

					if (mode == CloneCommand.Mode.field_13500) {
						for (BlockPos blockPos8 : deque) {
							BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos8);
							Clearable.clear(blockEntity2);
							serverWorld.setBlockState(blockPos8, Blocks.field_10499.getDefaultState(), 2);
						}

						for (BlockPos blockPos9 : deque) {
							serverWorld.setBlockState(blockPos9, Blocks.field_10124.getDefaultState(), 3);
						}
					}

					List<CloneCommand.BlockInfo> list4 = Lists.newArrayList();
					list4.addAll(list);
					list4.addAll(list2);
					list4.addAll(list3);
					List<CloneCommand.BlockInfo> list5 = Lists.reverse(list4);

					for (CloneCommand.BlockInfo blockInfo : list5) {
						BlockEntity blockEntity3 = serverWorld.getBlockEntity(blockInfo.pos);
						Clearable.clear(blockEntity3);
						serverWorld.setBlockState(blockInfo.pos, Blocks.field_10499.getDefaultState(), 2);
					}

					int m = 0;

					for (CloneCommand.BlockInfo blockInfo2 : list4) {
						if (serverWorld.setBlockState(blockInfo2.pos, blockInfo2.state, 2)) {
							m++;
						}
					}

					for (CloneCommand.BlockInfo blockInfo3 : list2) {
						BlockEntity blockEntity4 = serverWorld.getBlockEntity(blockInfo3.pos);
						if (blockInfo3.blockEntityTag != null && blockEntity4 != null) {
							blockInfo3.blockEntityTag.putInt("x", blockInfo3.pos.getX());
							blockInfo3.blockEntityTag.putInt("y", blockInfo3.pos.getY());
							blockInfo3.blockEntityTag.putInt("z", blockInfo3.pos.getZ());
							blockEntity4.fromTag(blockInfo3.blockEntityTag);
							blockEntity4.markDirty();
						}

						serverWorld.setBlockState(blockInfo3.pos, blockInfo3.state, 2);
					}

					for (CloneCommand.BlockInfo blockInfo4 : list5) {
						serverWorld.updateNeighbors(blockInfo4.pos, blockInfo4.state.getBlock());
					}

					serverWorld.getBlockTickScheduler().copyScheduledTicks(blockBox, blockPos5);
					if (m == 0) {
						throw FAILED_EXCEPTION.create();
					} else {
						serverCommandSource.sendFeedback(new TranslatableText("commands.clone.success", m), true);
						return m;
					}
				} else {
					throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
				}
			}
		}
	}

	static class BlockInfo {
		public final BlockPos pos;
		public final BlockState state;
		@Nullable
		public final CompoundTag blockEntityTag;

		public BlockInfo(BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
			this.pos = blockPos;
			this.state = blockState;
			this.blockEntityTag = compoundTag;
		}
	}

	static enum Mode {
		field_13497(true),
		field_13500(true),
		field_13499(false);

		private final boolean allowsOverlap;

		private Mode(boolean bl) {
			this.allowsOverlap = bl;
		}

		public boolean allowsOverlap() {
			return this.allowsOverlap;
		}
	}
}
