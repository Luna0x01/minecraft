package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

public class class_4275 implements ArgumentType<EnumSet<Direction.Axis>> {
	private static final Collection<String> field_20977 = Arrays.asList("xyz", "x");
	private static final SimpleCommandExceptionType field_20978 = new SimpleCommandExceptionType(new TranslatableText("arguments.swizzle.invalid"));

	public static class_4275 method_19445() {
		return new class_4275();
	}

	public static EnumSet<Direction.Axis> method_19447(CommandContext<class_3915> commandContext, String string) {
		return (EnumSet<Direction.Axis>)commandContext.getArgument(string, EnumSet.class);
	}

	public EnumSet<Direction.Axis> parse(StringReader stringReader) throws CommandSyntaxException {
		EnumSet<Direction.Axis> enumSet = EnumSet.noneOf(Direction.Axis.class);

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			char c = stringReader.read();
			Direction.Axis axis;
			switch (c) {
				case 'x':
					axis = Direction.Axis.X;
					break;
				case 'y':
					axis = Direction.Axis.Y;
					break;
				case 'z':
					axis = Direction.Axis.Z;
					break;
				default:
					throw field_20978.create();
			}

			if (enumSet.contains(axis)) {
				throw field_20978.create();
			}

			enumSet.add(axis);
		}

		return enumSet;
	}

	public Collection<String> getExamples() {
		return field_20977;
	}
}
