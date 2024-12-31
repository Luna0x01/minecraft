package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4213;
import net.minecraft.class_4229;
import net.minecraft.class_4252;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class SetBlockCommand {
	private static final SimpleCommandExceptionType field_21783 = new SimpleCommandExceptionType(new TranslatableText("commands.setblock.failed"));

	public static void method_20994(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("setblock").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("pos", class_4252.method_19358())
						.then(
							((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("block", class_4229.method_19207())
											.executes(
												commandContext -> method_20993(
														(class_3915)commandContext.getSource(),
														class_4252.method_19360(commandContext, "pos"),
														class_4229.method_19209(commandContext, "block"),
														SetBlockCommand.class_4428.REPLACE,
														null
													)
											))
										.then(
											CommandManager.method_17529("destroy")
												.executes(
													commandContext -> method_20993(
															(class_3915)commandContext.getSource(),
															class_4252.method_19360(commandContext, "pos"),
															class_4229.method_19209(commandContext, "block"),
															SetBlockCommand.class_4428.DESTROY,
															null
														)
												)
										))
									.then(
										CommandManager.method_17529("keep")
											.executes(
												commandContext -> method_20993(
														(class_3915)commandContext.getSource(),
														class_4252.method_19360(commandContext, "pos"),
														class_4229.method_19209(commandContext, "block"),
														SetBlockCommand.class_4428.REPLACE,
														cachedBlockPosition -> cachedBlockPosition.method_16937().method_8579(cachedBlockPosition.getPos())
													)
											)
									))
								.then(
									CommandManager.method_17529("replace")
										.executes(
											commandContext -> method_20993(
													(class_3915)commandContext.getSource(),
													class_4252.method_19360(commandContext, "pos"),
													class_4229.method_19209(commandContext, "block"),
													SetBlockCommand.class_4428.REPLACE,
													null
												)
										)
								)
						)
				)
		);
	}

	private static int method_20993(
		class_3915 arg, BlockPos blockPos, class_4213 arg2, SetBlockCommand.class_4428 arg3, @Nullable Predicate<CachedBlockPosition> predicate
	) throws CommandSyntaxException {
		ServerWorld serverWorld = arg.method_17468();
		if (predicate != null && !predicate.test(new CachedBlockPosition(serverWorld, blockPos, true))) {
			throw field_21783.create();
		} else {
			boolean bl;
			if (arg3 == SetBlockCommand.class_4428.DESTROY) {
				serverWorld.method_8535(blockPos, true);
				bl = !arg2.method_19037().isAir();
			} else {
				BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
				if (blockEntity instanceof Inventory) {
					((Inventory)blockEntity).clear();
				}

				bl = true;
			}

			if (bl && !arg2.method_19039(serverWorld, blockPos, 2)) {
				throw field_21783.create();
			} else {
				serverWorld.method_16342(blockPos, arg2.method_19037().getBlock());
				arg.method_17459(new TranslatableText("commands.setblock.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
				return 1;
			}
		}
	}

	public interface class_4427 {
		@Nullable
		class_4213 filter(BlockBox blockBox, BlockPos blockPos, class_4213 arg, ServerWorld serverWorld);
	}

	public static enum class_4428 {
		REPLACE,
		OUTLINE,
		HOLLOW,
		DESTROY;
	}
}
