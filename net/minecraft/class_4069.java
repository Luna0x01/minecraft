package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.entity.EntityType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4069 implements ArgumentType<Identifier> {
	private static final Collection<String> field_19758 = Arrays.asList("minecraft:pig", "cow");
	public static final DynamicCommandExceptionType field_19757 = new DynamicCommandExceptionType(object -> new TranslatableText("entity.notFound", object));

	public static class_4069 method_17944() {
		return new class_4069();
	}

	public static Identifier method_17946(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return method_17948((Identifier)commandContext.getArgument(string, Identifier.class));
	}

	private static final Identifier method_17948(Identifier identifier) throws CommandSyntaxException {
		EntityType<?> entityType = Registry.ENTITY_TYPE.getByIdentifier(identifier);
		if (entityType != null && entityType.method_15626()) {
			return identifier;
		} else {
			throw field_19757.create(identifier);
		}
	}

	public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
		return method_17948(Identifier.method_20442(stringReader));
	}

	public Collection<String> getExamples() {
		return field_19758;
	}
}
