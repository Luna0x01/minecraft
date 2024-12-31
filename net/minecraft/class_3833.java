package net.minecraft;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TranslatableText;

public class class_3833 implements BuiltInExceptionProvider {
	private static final Dynamic2CommandExceptionType field_19092 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.double.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_19093 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.double.big", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_19094 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.float.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_19095 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.float.big", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_19096 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.integer.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_19097 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.integer.big", object2, object)
	);
	private static final DynamicCommandExceptionType field_19098 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.literal.incorrect", object)
	);
	private static final SimpleCommandExceptionType field_19099 = new SimpleCommandExceptionType(new TranslatableText("parsing.quote.expected.start"));
	private static final SimpleCommandExceptionType field_19100 = new SimpleCommandExceptionType(new TranslatableText("parsing.quote.expected.end"));
	private static final DynamicCommandExceptionType field_19101 = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.quote.escape", object));
	private static final DynamicCommandExceptionType field_19102 = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.bool.invalid", object));
	private static final DynamicCommandExceptionType field_19103 = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.int.invalid", object));
	private static final SimpleCommandExceptionType field_19104 = new SimpleCommandExceptionType(new TranslatableText("parsing.int.expected"));
	private static final DynamicCommandExceptionType field_19105 = new DynamicCommandExceptionType(
		object -> new TranslatableText("parsing.double.invalid", object)
	);
	private static final SimpleCommandExceptionType field_19106 = new SimpleCommandExceptionType(new TranslatableText("parsing.double.expected"));
	private static final DynamicCommandExceptionType field_19107 = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.float.invalid", object));
	private static final SimpleCommandExceptionType field_19108 = new SimpleCommandExceptionType(new TranslatableText("parsing.float.expected"));
	private static final SimpleCommandExceptionType field_19109 = new SimpleCommandExceptionType(new TranslatableText("parsing.bool.expected"));
	private static final DynamicCommandExceptionType field_19110 = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.expected", object));
	private static final SimpleCommandExceptionType field_19111 = new SimpleCommandExceptionType(new TranslatableText("command.unknown.command"));
	private static final SimpleCommandExceptionType field_19112 = new SimpleCommandExceptionType(new TranslatableText("command.unknown.argument"));
	private static final SimpleCommandExceptionType field_19113 = new SimpleCommandExceptionType(new TranslatableText("command.expected.separator"));
	private static final DynamicCommandExceptionType field_19114 = new DynamicCommandExceptionType(object -> new TranslatableText("command.exception", object));

	public Dynamic2CommandExceptionType doubleTooLow() {
		return field_19092;
	}

	public Dynamic2CommandExceptionType doubleTooHigh() {
		return field_19093;
	}

	public Dynamic2CommandExceptionType floatTooLow() {
		return field_19094;
	}

	public Dynamic2CommandExceptionType floatTooHigh() {
		return field_19095;
	}

	public Dynamic2CommandExceptionType integerTooLow() {
		return field_19096;
	}

	public Dynamic2CommandExceptionType integerTooHigh() {
		return field_19097;
	}

	public DynamicCommandExceptionType literalIncorrect() {
		return field_19098;
	}

	public SimpleCommandExceptionType readerExpectedStartOfQuote() {
		return field_19099;
	}

	public SimpleCommandExceptionType readerExpectedEndOfQuote() {
		return field_19100;
	}

	public DynamicCommandExceptionType readerInvalidEscape() {
		return field_19101;
	}

	public DynamicCommandExceptionType readerInvalidBool() {
		return field_19102;
	}

	public DynamicCommandExceptionType readerInvalidInt() {
		return field_19103;
	}

	public SimpleCommandExceptionType readerExpectedInt() {
		return field_19104;
	}

	public DynamicCommandExceptionType readerInvalidDouble() {
		return field_19105;
	}

	public SimpleCommandExceptionType readerExpectedDouble() {
		return field_19106;
	}

	public DynamicCommandExceptionType readerInvalidFloat() {
		return field_19107;
	}

	public SimpleCommandExceptionType readerExpectedFloat() {
		return field_19108;
	}

	public SimpleCommandExceptionType readerExpectedBool() {
		return field_19109;
	}

	public DynamicCommandExceptionType readerExpectedSymbol() {
		return field_19110;
	}

	public SimpleCommandExceptionType dispatcherUnknownCommand() {
		return field_19111;
	}

	public SimpleCommandExceptionType dispatcherUnknownArgument() {
		return field_19112;
	}

	public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
		return field_19113;
	}

	public DynamicCommandExceptionType dispatcherParseException() {
		return field_19114;
	}
}
