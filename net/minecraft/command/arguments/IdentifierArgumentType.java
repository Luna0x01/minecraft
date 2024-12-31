package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.Advancement;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class IdentifierArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("advancement.advancementNotFound", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("recipe.notFound", object)
	);
	private static final DynamicCommandExceptionType field_21506 = new DynamicCommandExceptionType(object -> new TranslatableText("predicate.unknown", object));

	public static IdentifierArgumentType identifier() {
		return new IdentifierArgumentType();
	}

	public static Advancement getAdvancementArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		Identifier identifier = (Identifier)commandContext.getArgument(string, Identifier.class);
		Advancement advancement = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().get(identifier);
		if (advancement == null) {
			throw UNKNOWN_ADVANCEMENT_EXCEPTION.create(identifier);
		} else {
			return advancement;
		}
	}

	public static Recipe<?> getRecipeArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		RecipeManager recipeManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager();
		Identifier identifier = (Identifier)commandContext.getArgument(string, Identifier.class);
		return (Recipe<?>)recipeManager.get(identifier).orElseThrow(() -> UNKNOWN_RECIPE_EXCEPTION.create(identifier));
	}

	public static LootCondition method_23727(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		Identifier identifier = (Identifier)commandContext.getArgument(string, Identifier.class);
		LootConditionManager lootConditionManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPredicateManager();
		LootCondition lootCondition = lootConditionManager.get(identifier);
		if (lootCondition == null) {
			throw field_21506.create(identifier);
		} else {
			return lootCondition;
		}
	}

	public static Identifier getIdentifier(CommandContext<ServerCommandSource> commandContext, String string) {
		return (Identifier)commandContext.getArgument(string, Identifier.class);
	}

	public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
		return Identifier.fromCommandInput(stringReader);
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
