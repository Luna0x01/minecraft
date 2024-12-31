package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RecipeCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "recipe";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.recipe.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.recipe.usage");
		} else {
			boolean bl = "give".equalsIgnoreCase(args[0]);
			boolean bl2 = "take".equalsIgnoreCase(args[0]);
			if (!bl && !bl2) {
				throw new IncorrectUsageException("commands.recipe.usage");
			} else {
				for (ServerPlayerEntity serverPlayerEntity : method_14455(minecraftServer, commandSource, args[1])) {
					if ("*".equals(args[2])) {
						if (bl) {
							serverPlayerEntity.method_14154(this.method_14746());
							run(commandSource, this, "commands.recipe.give.success.all", new Object[]{serverPlayerEntity.getTranslationKey()});
						} else {
							serverPlayerEntity.method_14156(this.method_14746());
							run(commandSource, this, "commands.recipe.take.success.all", new Object[]{serverPlayerEntity.getTranslationKey()});
						}
					} else {
						RecipeType recipeType = RecipeDispatcher.get(new Identifier(args[2]));
						if (recipeType == null) {
							throw new CommandException("commands.recipe.unknownrecipe", args[2]);
						}

						if (recipeType.method_14251()) {
							throw new CommandException("commands.recipe.unsupported", args[2]);
						}

						List<RecipeType> list2 = Lists.newArrayList(new RecipeType[]{recipeType});
						if (bl == serverPlayerEntity.method_14965().method_14987(recipeType)) {
							String string = bl ? "commands.recipe.alreadyHave" : "commands.recipe.dontHave";
							throw new CommandException(string, serverPlayerEntity.getTranslationKey(), recipeType.getOutput().getCustomName());
						}

						if (bl) {
							serverPlayerEntity.method_14154(list2);
							run(
								commandSource, this, "commands.recipe.give.success.one", new Object[]{serverPlayerEntity.getTranslationKey(), recipeType.getOutput().getCustomName()}
							);
						} else {
							serverPlayerEntity.method_14156(list2);
							run(
								commandSource, this, "commands.recipe.take.success.one", new Object[]{recipeType.getOutput().getCustomName(), serverPlayerEntity.getTranslationKey()}
							);
						}
					}
				}
			}
		}
	}

	private List<RecipeType> method_14746() {
		return Lists.newArrayList(RecipeDispatcher.REGISTRY);
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"give", "take"});
		} else if (strings.length == 2) {
			return method_2894(strings, server.getPlayerNames());
		} else {
			return strings.length == 3 ? method_10708(strings, RecipeDispatcher.REGISTRY.getKeySet()) : Collections.emptyList();
		}
	}
}
