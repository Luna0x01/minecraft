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
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class CloneCommand {
	private static final int MAX_BLOCKS = 32768;
	private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.overlap"));
	private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(maxCount, count) -> new TranslatableText("commands.clone.toobig", maxCount, count)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.clone.failed"));
	public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = pos -> !pos.getBlockState().isAir();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clone").requires(source -> source.hasPermissionLevel(2)))
				.then(
					CommandManager.argument("begin", BlockPosArgumentType.blockPos())
						.then(
							CommandManager.argument("end", BlockPosArgumentType.blockPos())
								.then(
									((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos())
													.executes(
														context -> execute(
																(ServerCommandSource)context.getSource(),
																BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																pos -> true,
																CloneCommand.Mode.NORMAL
															)
													))
												.then(
													((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replace")
																	.executes(
																		context -> execute(
																				(ServerCommandSource)context.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																				pos -> true,
																				CloneCommand.Mode.NORMAL
																			)
																	))
																.then(
																	CommandManager.literal("force")
																		.executes(
																			context -> execute(
																					(ServerCommandSource)context.getSource(),
																					BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																					BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																					BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																					pos -> true,
																					CloneCommand.Mode.FORCE
																				)
																		)
																))
															.then(
																CommandManager.literal("move")
																	.executes(
																		context -> execute(
																				(ServerCommandSource)context.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																				pos -> true,
																				CloneCommand.Mode.MOVE
																			)
																	)
															))
														.then(
															CommandManager.literal("normal")
																.executes(
																	context -> execute(
																			(ServerCommandSource)context.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																			pos -> true,
																			CloneCommand.Mode.NORMAL
																		)
																)
														)
												))
											.then(
												((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("masked")
																.executes(
																	context -> execute(
																			(ServerCommandSource)context.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																			IS_AIR_PREDICATE,
																			CloneCommand.Mode.NORMAL
																		)
																))
															.then(
																CommandManager.literal("force")
																	.executes(
																		context -> execute(
																				(ServerCommandSource)context.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																				IS_AIR_PREDICATE,
																				CloneCommand.Mode.FORCE
																			)
																	)
															))
														.then(
															CommandManager.literal("move")
																.executes(
																	context -> execute(
																			(ServerCommandSource)context.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																			IS_AIR_PREDICATE,
																			CloneCommand.Mode.MOVE
																		)
																)
														))
													.then(
														CommandManager.literal("normal")
															.executes(
																context -> execute(
																		(ServerCommandSource)context.getSource(),
																		BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																		BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																		BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																		IS_AIR_PREDICATE,
																		CloneCommand.Mode.NORMAL
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
																		context -> execute(
																				(ServerCommandSource)context.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																				BlockPredicateArgumentType.getBlockPredicate(context, "filter"),
																				CloneCommand.Mode.NORMAL
																			)
																	))
																.then(
																	CommandManager.literal("force")
																		.executes(
																			context -> execute(
																					(ServerCommandSource)context.getSource(),
																					BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																					BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																					BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																					BlockPredicateArgumentType.getBlockPredicate(context, "filter"),
																					CloneCommand.Mode.FORCE
																				)
																		)
																))
															.then(
																CommandManager.literal("move")
																	.executes(
																		context -> execute(
																				(ServerCommandSource)context.getSource(),
																				BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																				BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																				BlockPredicateArgumentType.getBlockPredicate(context, "filter"),
																				CloneCommand.Mode.MOVE
																			)
																	)
															))
														.then(
															CommandManager.literal("normal")
																.executes(
																	context -> execute(
																			(ServerCommandSource)context.getSource(),
																			BlockPosArgumentType.getLoadedBlockPos(context, "begin"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "end"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "destination"),
																			BlockPredicateArgumentType.getBlockPredicate(context, "filter"),
																			CloneCommand.Mode.NORMAL
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
		ServerCommandSource source, BlockPos begin, BlockPos end, BlockPos destination, Predicate<CachedBlockPosition> filter, CloneCommand.Mode mode
	) throws CommandSyntaxException {
		BlockBox blockBox = BlockBox.create(begin, end);
		BlockPos blockPos = destination.add(blockBox.getDimensions());
		BlockBox blockBox2 = BlockBox.create(destination, blockPos);
		if (!mode.allowsOverlap() && blockBox2.intersects(blockBox)) {
			throw OVERLAP_EXCEPTION.create();
		} else {
			int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
			if (i > 32768) {
				throw TOO_BIG_EXCEPTION.create(32768, i);
			} else {
				ServerWorld serverWorld = source.getWorld();
				if (serverWorld.isRegionLoaded(begin, end) && serverWorld.isRegionLoaded(destination, blockPos)) {
					List<CloneCommand.BlockInfo> list = Lists.newArrayList();
					List<CloneCommand.BlockInfo> list2 = Lists.newArrayList();
					List<CloneCommand.BlockInfo> list3 = Lists.newArrayList();
					Deque<BlockPos> deque = Lists.newLinkedList();
					BlockPos blockPos2 = new BlockPos(
						blockBox2.getMinX() - blockBox.getMinX(), blockBox2.getMinY() - blockBox.getMinY(), blockBox2.getMinZ() - blockBox.getMinZ()
					);

					for (int j = blockBox.getMinZ(); j <= blockBox.getMaxZ(); j++) {
						for (int k = blockBox.getMinY(); k <= blockBox.getMaxY(); k++) {
							for (int l = blockBox.getMinX(); l <= blockBox.getMaxX(); l++) {
								BlockPos blockPos3 = new BlockPos(l, k, j);
								BlockPos blockPos4 = blockPos3.add(blockPos2);
								CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos3, false);
								BlockState blockState = cachedBlockPosition.getBlockState();
								if (filter.test(cachedBlockPosition)) {
									BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos3);
									if (blockEntity != null) {
										NbtCompound nbtCompound = blockEntity.writeNbt(new NbtCompound());
										list2.add(new CloneCommand.BlockInfo(blockPos4, blockState, nbtCompound));
										deque.addLast(blockPos3);
									} else if (!blockState.isOpaqueFullCube(serverWorld, blockPos3) && !blockState.isFullCube(serverWorld, blockPos3)) {
										list3.add(new CloneCommand.BlockInfo(blockPos4, blockState, null));
										deque.addFirst(blockPos3);
									} else {
										list.add(new CloneCommand.BlockInfo(blockPos4, blockState, null));
										deque.addLast(blockPos3);
									}
								}
							}
						}
					}

					if (mode == CloneCommand.Mode.MOVE) {
						for (BlockPos blockPos5 : deque) {
							BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos5);
							Clearable.clear(blockEntity2);
							serverWorld.setBlockState(blockPos5, Blocks.BARRIER.getDefaultState(), 2);
						}

						for (BlockPos blockPos6 : deque) {
							serverWorld.setBlockState(blockPos6, Blocks.AIR.getDefaultState(), 3);
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
						serverWorld.setBlockState(blockInfo.pos, Blocks.BARRIER.getDefaultState(), 2);
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
							blockEntity4.readNbt(blockInfo3.blockEntityTag);
							blockEntity4.markDirty();
						}

						serverWorld.setBlockState(blockInfo3.pos, blockInfo3.state, 2);
					}

					for (CloneCommand.BlockInfo blockInfo4 : list5) {
						serverWorld.updateNeighbors(blockInfo4.pos, blockInfo4.state.getBlock());
					}

					serverWorld.getBlockTickScheduler().copyScheduledTicks(blockBox, blockPos2);
					if (m == 0) {
						throw FAILED_EXCEPTION.create();
					} else {
						source.sendFeedback(new TranslatableText("commands.clone.success", m), true);
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
		public final NbtCompound blockEntityTag;

		public BlockInfo(BlockPos pos, BlockState state, @Nullable NbtCompound blockEntityTag) {
			this.pos = pos;
			this.state = state;
			this.blockEntityTag = blockEntityTag;
		}
	}

	static enum Mode {
		FORCE(true),
		MOVE(true),
		NORMAL(false);

		private final boolean allowsOverlap;

		private Mode(boolean allowsOverlap) {
			this.allowsOverlap = allowsOverlap;
		}

		public boolean allowsOverlap() {
			return this.allowsOverlap;
		}
	}
}
