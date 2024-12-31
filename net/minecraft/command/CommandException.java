package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.Text;

public class CommandException extends RuntimeException {
	private final Text field_19221;

	public CommandException(Text text) {
		super(text.computeValue(), null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
		this.field_19221 = text;
	}

	public Text method_17390() {
		return this.field_19221;
	}
}
