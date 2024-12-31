package net.minecraft;

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
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class class_4410 {
	private static final SimpleCommandExceptionType field_21707 = new SimpleCommandExceptionType(new TranslatableText("commands.clone.overlap"));
	private static final Dynamic2CommandExceptionType field_21708 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.clone.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType field_21709 = new SimpleCommandExceptionType(new TranslatableText("commands.clone.failed"));
	public static final Predicate<CachedBlockPosition> field_21706 = cachedBlockPosition -> !cachedBlockPosition.getBlockState().isAir();

	public static void method_20587(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("clone").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("begin", class_4252.method_19358())
						.then(
							CommandManager.method_17530("end", class_4252.method_19358())
								.then(
									((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("destination", class_4252.method_19358())
													.executes(
														commandContext -> method_20586(
																(class_3915)commandContext.getSource(),
																class_4252.method_19360(commandContext, "begin"),
																class_4252.method_19360(commandContext, "end"),
																class_4252.method_19360(commandContext, "destination"),
																cachedBlockPosition -> true,
																class_4410.class_4411.NORMAL
															)
													))
												.then(
													((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("replace")
																	.executes(
																		commandContext -> method_20586(
																				(class_3915)commandContext.getSource(),
																				class_4252.method_19360(commandContext, "begin"),
																				class_4252.method_19360(commandContext, "end"),
																				class_4252.method_19360(commandContext, "destination"),
																				cachedBlockPosition -> true,
																				class_4410.class_4411.NORMAL
																			)
																	))
																.then(
																	CommandManager.method_17529("force")
																		.executes(
																			commandContext -> method_20586(
																					(class_3915)commandContext.getSource(),
																					class_4252.method_19360(commandContext, "begin"),
																					class_4252.method_19360(commandContext, "end"),
																					class_4252.method_19360(commandContext, "destination"),
																					cachedBlockPosition -> true,
																					class_4410.class_4411.FORCE
																				)
																		)
																))
															.then(
																CommandManager.method_17529("move")
																	.executes(
																		commandContext -> method_20586(
																				(class_3915)commandContext.getSource(),
																				class_4252.method_19360(commandContext, "begin"),
																				class_4252.method_19360(commandContext, "end"),
																				class_4252.method_19360(commandContext, "destination"),
																				cachedBlockPosition -> true,
																				class_4410.class_4411.MOVE
																			)
																	)
															))
														.then(
															CommandManager.method_17529("normal")
																.executes(
																	commandContext -> method_20586(
																			(class_3915)commandContext.getSource(),
																			class_4252.method_19360(commandContext, "begin"),
																			class_4252.method_19360(commandContext, "end"),
																			class_4252.method_19360(commandContext, "destination"),
																			cachedBlockPosition -> true,
																			class_4410.class_4411.NORMAL
																		)
																)
														)
												))
											.then(
												((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("masked")
																.executes(
																	commandContext -> method_20586(
																			(class_3915)commandContext.getSource(),
																			class_4252.method_19360(commandContext, "begin"),
																			class_4252.method_19360(commandContext, "end"),
																			class_4252.method_19360(commandContext, "destination"),
																			field_21706,
																			class_4410.class_4411.NORMAL
																		)
																))
															.then(
																CommandManager.method_17529("force")
																	.executes(
																		commandContext -> method_20586(
																				(class_3915)commandContext.getSource(),
																				class_4252.method_19360(commandContext, "begin"),
																				class_4252.method_19360(commandContext, "end"),
																				class_4252.method_19360(commandContext, "destination"),
																				field_21706,
																				class_4410.class_4411.FORCE
																			)
																	)
															))
														.then(
															CommandManager.method_17529("move")
																.executes(
																	commandContext -> method_20586(
																			(class_3915)commandContext.getSource(),
																			class_4252.method_19360(commandContext, "begin"),
																			class_4252.method_19360(commandContext, "end"),
																			class_4252.method_19360(commandContext, "destination"),
																			field_21706,
																			class_4410.class_4411.MOVE
																		)
																)
														))
													.then(
														CommandManager.method_17529("normal")
															.executes(
																commandContext -> method_20586(
																		(class_3915)commandContext.getSource(),
																		class_4252.method_19360(commandContext, "begin"),
																		class_4252.method_19360(commandContext, "end"),
																		class_4252.method_19360(commandContext, "destination"),
																		field_21706,
																		class_4410.class_4411.NORMAL
																	)
															)
													)
											))
										.then(
											CommandManager.method_17529("filtered")
												.then(
													((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("filter", class_4220.method_19107())
																	.executes(
																		commandContext -> method_20586(
																				(class_3915)commandContext.getSource(),
																				class_4252.method_19360(commandContext, "begin"),
																				class_4252.method_19360(commandContext, "end"),
																				class_4252.method_19360(commandContext, "destination"),
																				class_4220.method_19109(commandContext, "filter"),
																				class_4410.class_4411.NORMAL
																			)
																	))
																.then(
																	CommandManager.method_17529("force")
																		.executes(
																			commandContext -> method_20586(
																					(class_3915)commandContext.getSource(),
																					class_4252.method_19360(commandContext, "begin"),
																					class_4252.method_19360(commandContext, "end"),
																					class_4252.method_19360(commandContext, "destination"),
																					class_4220.method_19109(commandContext, "filter"),
																					class_4410.class_4411.FORCE
																				)
																		)
																))
															.then(
																CommandManager.method_17529("move")
																	.executes(
																		commandContext -> method_20586(
																				(class_3915)commandContext.getSource(),
																				class_4252.method_19360(commandContext, "begin"),
																				class_4252.method_19360(commandContext, "end"),
																				class_4252.method_19360(commandContext, "destination"),
																				class_4220.method_19109(commandContext, "filter"),
																				class_4410.class_4411.MOVE
																			)
																	)
															))
														.then(
															CommandManager.method_17529("normal")
																.executes(
																	commandContext -> method_20586(
																			(class_3915)commandContext.getSource(),
																			class_4252.method_19360(commandContext, "begin"),
																			class_4252.method_19360(commandContext, "end"),
																			class_4252.method_19360(commandContext, "destination"),
																			class_4220.method_19109(commandContext, "filter"),
																			class_4410.class_4411.NORMAL
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

	private static int method_20586(
		class_3915 arg, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Predicate<CachedBlockPosition> predicate, class_4410.class_4411 arg2
	) throws CommandSyntaxException {
		BlockBox blockBox = new BlockBox(blockPos, blockPos2);
		BlockBox blockBox2 = new BlockBox(blockPos3, blockPos3.add(blockBox.getDimensions()));
		if (!arg2.method_20607() && blockBox2.intersects(blockBox)) {
			throw field_21707.create();
		} else {
			int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
			if (i > 32768) {
				throw field_21708.create(32768, i);
			} else {
				ServerWorld serverWorld = arg.method_17468();
				if (serverWorld.method_16374(blockBox) && serverWorld.method_16374(blockBox2)) {
					List<class_4410.class_2635> list = Lists.newArrayList();
					List<class_4410.class_2635> list2 = Lists.newArrayList();
					List<class_4410.class_2635> list3 = Lists.newArrayList();
					Deque<BlockPos> deque = Lists.newLinkedList();
					BlockPos blockPos4 = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);

					for (int j = blockBox.minZ; j <= blockBox.maxZ; j++) {
						for (int k = blockBox.minY; k <= blockBox.maxY; k++) {
							for (int l = blockBox.minX; l <= blockBox.maxX; l++) {
								BlockPos blockPos5 = new BlockPos(l, k, j);
								BlockPos blockPos6 = blockPos5.add(blockPos4);
								CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(serverWorld, blockPos5, false);
								BlockState blockState = cachedBlockPosition.getBlockState();
								if (predicate.test(cachedBlockPosition)) {
									BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos5);
									if (blockEntity != null) {
										NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());
										list2.add(new class_4410.class_2635(blockPos6, blockState, nbtCompound));
										deque.addLast(blockPos5);
									} else if (!blockState.isFullOpaque(serverWorld, blockPos5) && !blockState.method_16897()) {
										list3.add(new class_4410.class_2635(blockPos6, blockState, null));
										deque.addFirst(blockPos5);
									} else {
										list.add(new class_4410.class_2635(blockPos6, blockState, null));
										deque.addLast(blockPos5);
									}
								}
							}
						}
					}

					if (arg2 == class_4410.class_4411.MOVE) {
						for (BlockPos blockPos7 : deque) {
							BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos7);
							if (blockEntity2 instanceof Inventory) {
								((Inventory)blockEntity2).clear();
							}

							serverWorld.setBlockState(blockPos7, Blocks.BARRIER.getDefaultState(), 2);
						}

						for (BlockPos blockPos8 : deque) {
							serverWorld.setBlockState(blockPos8, Blocks.AIR.getDefaultState(), 3);
						}
					}

					List<class_4410.class_2635> list4 = Lists.newArrayList();
					list4.addAll(list);
					list4.addAll(list2);
					list4.addAll(list3);
					List<class_4410.class_2635> list5 = Lists.reverse(list4);

					for (class_4410.class_2635 lv : list5) {
						BlockEntity blockEntity3 = serverWorld.getBlockEntity(lv.field_12011);
						if (blockEntity3 instanceof Inventory) {
							((Inventory)blockEntity3).clear();
						}

						serverWorld.setBlockState(lv.field_12011, Blocks.BARRIER.getDefaultState(), 2);
					}

					int m = 0;

					for (class_4410.class_2635 lv2 : list4) {
						if (serverWorld.setBlockState(lv2.field_12011, lv2.field_12012, 2)) {
							m++;
						}
					}

					for (class_4410.class_2635 lv3 : list2) {
						BlockEntity blockEntity4 = serverWorld.getBlockEntity(lv3.field_12011);
						if (lv3.field_12013 != null && blockEntity4 != null) {
							lv3.field_12013.putInt("x", lv3.field_12011.getX());
							lv3.field_12013.putInt("y", lv3.field_12011.getY());
							lv3.field_12013.putInt("z", lv3.field_12011.getZ());
							blockEntity4.fromNbt(lv3.field_12013);
							blockEntity4.markDirty();
						}

						serverWorld.setBlockState(lv3.field_12011, lv3.field_12012, 2);
					}

					for (class_4410.class_2635 lv4 : list5) {
						serverWorld.method_16342(lv4.field_12011, lv4.field_12012.getBlock());
					}

					serverWorld.getBlockTickScheduler().method_16412(blockBox, blockPos4);
					if (m == 0) {
						throw field_21709.create();
					} else {
						arg.method_17459(new TranslatableText("commands.clone.success", m), true);
						return m;
					}
				} else {
					throw class_4252.field_20911.create();
				}
			}
		}
	}

	static class class_2635 {
		public final BlockPos field_12011;
		public final BlockState field_12012;
		@Nullable
		public final NbtCompound field_12013;

		public class_2635(BlockPos blockPos, BlockState blockState, @Nullable NbtCompound nbtCompound) {
			this.field_12011 = blockPos;
			this.field_12012 = blockState;
			this.field_12013 = nbtCompound;
		}
	}

	static enum class_4411 {
		FORCE(true),
		MOVE(true),
		NORMAL(false);

		private final boolean field_21713;

		private class_4411(boolean bl) {
			this.field_21713 = bl;
		}

		public boolean method_20607() {
			return this.field_21713;
		}
	}
}
