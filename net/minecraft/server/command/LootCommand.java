package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LootCommand {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
		LootManager lootManager = ((ServerCommandSource)context.getSource()).getServer().getLootManager();
		return CommandSource.suggestIdentifiers(lootManager.getTableIds(), builder);
	};
	private static final DynamicCommandExceptionType NO_HELD_ITEMS_EXCEPTION = new DynamicCommandExceptionType(
		entityName -> new TranslatableText("commands.drop.no_held_items", entityName)
	);
	private static final DynamicCommandExceptionType NO_LOOT_TABLE_EXCEPTION = new DynamicCommandExceptionType(
		entityName -> new TranslatableText("commands.drop.no_loot_table", entityName)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			addTargetArguments(
				(LiteralArgumentBuilder)CommandManager.literal("loot").requires(source -> source.hasPermissionLevel(2)),
				(builder, constructor) -> builder.then(
							CommandManager.literal("fish")
								.then(
									CommandManager.argument("loot_table", IdentifierArgumentType.identifier())
										.suggests(SUGGESTION_PROVIDER)
										.then(
											((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos())
															.executes(
																context -> executeFish(
																		context,
																		IdentifierArgumentType.getIdentifier(context, "loot_table"),
																		BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																		ItemStack.EMPTY,
																		constructor
																	)
															))
														.then(
															CommandManager.argument("tool", ItemStackArgumentType.itemStack())
																.executes(
																	context -> executeFish(
																			context,
																			IdentifierArgumentType.getIdentifier(context, "loot_table"),
																			BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																			ItemStackArgumentType.getItemStackArgument(context, "tool").createStack(1, false),
																			constructor
																		)
																)
														))
													.then(
														CommandManager.literal("mainhand")
															.executes(
																context -> executeFish(
																		context,
																		IdentifierArgumentType.getIdentifier(context, "loot_table"),
																		BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																		getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.MAINHAND),
																		constructor
																	)
															)
													))
												.then(
													CommandManager.literal("offhand")
														.executes(
															context -> executeFish(
																	context,
																	IdentifierArgumentType.getIdentifier(context, "loot_table"),
																	BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																	getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.OFFHAND),
																	constructor
																)
														)
												)
										)
								)
						)
						.then(
							CommandManager.literal("loot")
								.then(
									CommandManager.argument("loot_table", IdentifierArgumentType.identifier())
										.suggests(SUGGESTION_PROVIDER)
										.executes(context -> executeLoot(context, IdentifierArgumentType.getIdentifier(context, "loot_table"), constructor))
								)
						)
						.then(
							CommandManager.literal("kill")
								.then(
									CommandManager.argument("target", EntityArgumentType.entity())
										.executes(context -> executeKill(context, EntityArgumentType.getEntity(context, "target"), constructor))
								)
						)
						.then(
							CommandManager.literal("mine")
								.then(
									((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos())
													.executes(context -> executeMine(context, BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemStack.EMPTY, constructor)))
												.then(
													CommandManager.argument("tool", ItemStackArgumentType.itemStack())
														.executes(
															context -> executeMine(
																	context,
																	BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																	ItemStackArgumentType.getItemStackArgument(context, "tool").createStack(1, false),
																	constructor
																)
														)
												))
											.then(
												CommandManager.literal("mainhand")
													.executes(
														context -> executeMine(
																context,
																BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
																getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.MAINHAND),
																constructor
															)
													)
											))
										.then(
											CommandManager.literal("offhand")
												.executes(
													context -> executeMine(
															context,
															BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
															getHeldItem((ServerCommandSource)context.getSource(), EquipmentSlot.OFFHAND),
															constructor
														)
												)
										)
								)
						)
			)
		);
	}

	private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addTargetArguments(T rootArgument, LootCommand.SourceConstructor sourceConstructor) {
		return (T)rootArgument.then(
				((LiteralArgumentBuilder)CommandManager.literal("replace")
						.then(
							CommandManager.literal("entity")
								.then(
									CommandManager.argument("entities", EntityArgumentType.entities())
										.then(
											sourceConstructor.construct(
													CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()),
													(context, stacks, messageSender) -> executeReplace(
															EntityArgumentType.getEntities(context, "entities"), ItemSlotArgumentType.getItemSlot(context, "slot"), stacks.size(), stacks, messageSender
														)
												)
												.then(
													sourceConstructor.construct(
														CommandManager.argument("count", IntegerArgumentType.integer(0)),
														(context, stacks, messageSender) -> executeReplace(
																EntityArgumentType.getEntities(context, "entities"),
																ItemSlotArgumentType.getItemSlot(context, "slot"),
																IntegerArgumentType.getInteger(context, "count"),
																stacks,
																messageSender
															)
													)
												)
										)
								)
						))
					.then(
						CommandManager.literal("block")
							.then(
								CommandManager.argument("targetPos", BlockPosArgumentType.blockPos())
									.then(
										sourceConstructor.construct(
												CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()),
												(context, stacks, messageSender) -> executeBlock(
														(ServerCommandSource)context.getSource(),
														BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"),
														ItemSlotArgumentType.getItemSlot(context, "slot"),
														stacks.size(),
														stacks,
														messageSender
													)
											)
											.then(
												sourceConstructor.construct(
													CommandManager.argument("count", IntegerArgumentType.integer(0)),
													(context, stacks, messageSender) -> executeBlock(
															(ServerCommandSource)context.getSource(),
															BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"),
															IntegerArgumentType.getInteger(context, "slot"),
															IntegerArgumentType.getInteger(context, "count"),
															stacks,
															messageSender
														)
												)
											)
									)
							)
					)
			)
			.then(
				CommandManager.literal("insert")
					.then(
						sourceConstructor.construct(
							CommandManager.argument("targetPos", BlockPosArgumentType.blockPos()),
							(context, stacks, messageSender) -> executeInsert(
									(ServerCommandSource)context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "targetPos"), stacks, messageSender
								)
						)
					)
			)
			.then(
				CommandManager.literal("give")
					.then(
						sourceConstructor.construct(
							CommandManager.argument("players", EntityArgumentType.players()),
							(context, stacks, messageSender) -> executeGive(EntityArgumentType.getPlayers(context, "players"), stacks, messageSender)
						)
					)
			)
			.then(
				CommandManager.literal("spawn")
					.then(
						sourceConstructor.construct(
							CommandManager.argument("targetPos", Vec3ArgumentType.vec3()),
							(context, stacks, messageSender) -> executeSpawn(
									(ServerCommandSource)context.getSource(), Vec3ArgumentType.getVec3(context, "targetPos"), stacks, messageSender
								)
						)
					)
			);
	}

	private static Inventory getBlockInventory(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
		BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
		if (!(blockEntity instanceof Inventory)) {
			throw ItemCommand.NOT_A_CONTAINER_TARGET_EXCEPTION.create(pos.getX(), pos.getY(), pos.getZ());
		} else {
			return (Inventory)blockEntity;
		}
	}

	private static int executeInsert(ServerCommandSource source, BlockPos targetPos, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender) throws CommandSyntaxException {
		Inventory inventory = getBlockInventory(source, targetPos);
		List<ItemStack> list = Lists.newArrayListWithCapacity(stacks.size());

		for (ItemStack itemStack : stacks) {
			if (insert(inventory, itemStack.copy())) {
				inventory.markDirty();
				list.add(itemStack);
			}
		}

		messageSender.accept(list);
		return list.size();
	}

	private static boolean insert(Inventory inventory, ItemStack stack) {
		boolean bl = false;

		for (int i = 0; i < inventory.size() && !stack.isEmpty(); i++) {
			ItemStack itemStack = inventory.getStack(i);
			if (inventory.isValid(i, stack)) {
				if (itemStack.isEmpty()) {
					inventory.setStack(i, stack);
					bl = true;
					break;
				}

				if (itemsMatch(itemStack, stack)) {
					int j = stack.getMaxCount() - itemStack.getCount();
					int k = Math.min(stack.getCount(), j);
					stack.decrement(k);
					itemStack.increment(k);
					bl = true;
				}
			}
		}

		return bl;
	}

	private static int executeBlock(
		ServerCommandSource source, BlockPos targetPos, int slot, int stackCount, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender
	) throws CommandSyntaxException {
		Inventory inventory = getBlockInventory(source, targetPos);
		int i = inventory.size();
		if (slot >= 0 && slot < i) {
			List<ItemStack> list = Lists.newArrayListWithCapacity(stacks.size());

			for (int j = 0; j < stackCount; j++) {
				int k = slot + j;
				ItemStack itemStack = j < stacks.size() ? (ItemStack)stacks.get(j) : ItemStack.EMPTY;
				if (inventory.isValid(k, itemStack)) {
					inventory.setStack(k, itemStack);
					list.add(itemStack);
				}
			}

			messageSender.accept(list);
			return list.size();
		} else {
			throw ItemCommand.NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
		}
	}

	private static boolean itemsMatch(ItemStack first, ItemStack second) {
		return first.isOf(second.getItem())
			&& first.getDamage() == second.getDamage()
			&& first.getCount() <= first.getMaxCount()
			&& Objects.equals(first.getTag(), second.getTag());
	}

	private static int executeGive(Collection<ServerPlayerEntity> players, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender) throws CommandSyntaxException {
		List<ItemStack> list = Lists.newArrayListWithCapacity(stacks.size());

		for (ItemStack itemStack : stacks) {
			for (ServerPlayerEntity serverPlayerEntity : players) {
				if (serverPlayerEntity.getInventory().insertStack(itemStack.copy())) {
					list.add(itemStack);
				}
			}
		}

		messageSender.accept(list);
		return list.size();
	}

	private static void replace(Entity entity, List<ItemStack> stacks, int slot, int stackCount, List<ItemStack> addedStacks) {
		for (int i = 0; i < stackCount; i++) {
			ItemStack itemStack = i < stacks.size() ? (ItemStack)stacks.get(i) : ItemStack.EMPTY;
			StackReference stackReference = entity.getStackReference(slot + i);
			if (stackReference != StackReference.EMPTY && stackReference.set(itemStack.copy())) {
				addedStacks.add(itemStack);
			}
		}
	}

	private static int executeReplace(
		Collection<? extends Entity> targets, int slot, int stackCount, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender
	) throws CommandSyntaxException {
		List<ItemStack> list = Lists.newArrayListWithCapacity(stacks.size());

		for (Entity entity : targets) {
			if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
				replace(entity, stacks, slot, stackCount, list);
				serverPlayerEntity.currentScreenHandler.sendContentUpdates();
			} else {
				replace(entity, stacks, slot, stackCount, list);
			}
		}

		messageSender.accept(list);
		return list.size();
	}

	private static int executeSpawn(ServerCommandSource source, Vec3d pos, List<ItemStack> stacks, LootCommand.FeedbackMessage messageSender) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		stacks.forEach(stack -> {
			ItemEntity itemEntity = new ItemEntity(serverWorld, pos.x, pos.y, pos.z, stack.copy());
			itemEntity.setToDefaultPickupDelay();
			serverWorld.spawnEntity(itemEntity);
		});
		messageSender.accept(stacks);
		return stacks.size();
	}

	private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks) {
		if (stacks.size() == 1) {
			ItemStack itemStack = (ItemStack)stacks.get(0);
			source.sendFeedback(new TranslatableText("commands.drop.success.single", itemStack.getCount(), itemStack.toHoverableText()), false);
		} else {
			source.sendFeedback(new TranslatableText("commands.drop.success.multiple", stacks.size()), false);
		}
	}

	private static void sendDroppedFeedback(ServerCommandSource source, List<ItemStack> stacks, Identifier lootTable) {
		if (stacks.size() == 1) {
			ItemStack itemStack = (ItemStack)stacks.get(0);
			source.sendFeedback(new TranslatableText("commands.drop.success.single_with_table", itemStack.getCount(), itemStack.toHoverableText(), lootTable), false);
		} else {
			source.sendFeedback(new TranslatableText("commands.drop.success.multiple_with_table", stacks.size(), lootTable), false);
		}
	}

	private static ItemStack getHeldItem(ServerCommandSource source, EquipmentSlot slot) throws CommandSyntaxException {
		Entity entity = source.getEntityOrThrow();
		if (entity instanceof LivingEntity) {
			return ((LivingEntity)entity).getEquippedStack(slot);
		} else {
			throw NO_HELD_ITEMS_EXCEPTION.create(entity.getDisplayName());
		}
	}

	private static int executeMine(CommandContext<ServerCommandSource> context, BlockPos pos, ItemStack stack, LootCommand.Target constructor) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
		ServerWorld serverWorld = serverCommandSource.getWorld();
		BlockState blockState = serverWorld.getBlockState(pos);
		BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
			.parameter(LootContextParameters.BLOCK_STATE, blockState)
			.optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity)
			.optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity())
			.parameter(LootContextParameters.TOOL, stack);
		List<ItemStack> list = blockState.getDroppedStacks(builder);
		return constructor.accept(context, list, stacks -> sendDroppedFeedback(serverCommandSource, stacks, blockState.getBlock().getLootTableId()));
	}

	private static int executeKill(CommandContext<ServerCommandSource> context, Entity entity, LootCommand.Target constructor) throws CommandSyntaxException {
		if (!(entity instanceof LivingEntity)) {
			throw NO_LOOT_TABLE_EXCEPTION.create(entity.getDisplayName());
		} else {
			Identifier identifier = ((LivingEntity)entity).getLootTable();
			ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
			LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld());
			Entity entity2 = serverCommandSource.getEntity();
			if (entity2 instanceof PlayerEntity) {
				builder.parameter(LootContextParameters.LAST_DAMAGE_PLAYER, (PlayerEntity)entity2);
			}

			builder.parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.MAGIC);
			builder.optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, entity2);
			builder.optionalParameter(LootContextParameters.KILLER_ENTITY, entity2);
			builder.parameter(LootContextParameters.THIS_ENTITY, entity);
			builder.parameter(LootContextParameters.ORIGIN, serverCommandSource.getPosition());
			LootTable lootTable = serverCommandSource.getServer().getLootManager().getTable(identifier);
			List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));
			return constructor.accept(context, list, stacks -> sendDroppedFeedback(serverCommandSource, stacks, identifier));
		}
	}

	private static int executeLoot(CommandContext<ServerCommandSource> context, Identifier lootTable, LootCommand.Target constructor) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
		LootContext.Builder builder = new LootContext.Builder(serverCommandSource.getWorld())
			.optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity())
			.parameter(LootContextParameters.ORIGIN, serverCommandSource.getPosition());
		return getFeedbackMessageSingle(context, lootTable, builder.build(LootContextTypes.CHEST), constructor);
	}

	private static int executeFish(
		CommandContext<ServerCommandSource> context, Identifier lootTable, BlockPos pos, ItemStack stack, LootCommand.Target constructor
	) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
		LootContext lootContext = new LootContext.Builder(serverCommandSource.getWorld())
			.parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
			.parameter(LootContextParameters.TOOL, stack)
			.optionalParameter(LootContextParameters.THIS_ENTITY, serverCommandSource.getEntity())
			.build(LootContextTypes.FISHING);
		return getFeedbackMessageSingle(context, lootTable, lootContext, constructor);
	}

	private static int getFeedbackMessageSingle(
		CommandContext<ServerCommandSource> context, Identifier lootTable, LootContext lootContext, LootCommand.Target constructor
	) throws CommandSyntaxException {
		ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
		LootTable lootTable2 = serverCommandSource.getServer().getLootManager().getTable(lootTable);
		List<ItemStack> list = lootTable2.generateLoot(lootContext);
		return constructor.accept(context, list, stacks -> sendDroppedFeedback(serverCommandSource, stacks));
	}

	@FunctionalInterface
	interface FeedbackMessage {
		void accept(List<ItemStack> items) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface SourceConstructor {
		ArgumentBuilder<ServerCommandSource, ?> construct(ArgumentBuilder<ServerCommandSource, ?> builder, LootCommand.Target target);
	}

	@FunctionalInterface
	interface Target {
		int accept(CommandContext<ServerCommandSource> context, List<ItemStack> items, LootCommand.FeedbackMessage messageSender) throws CommandSyntaxException;
	}
}
