package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.util.Identifier;

public class Function {
	private final Function.Executable[] executables;
	private final Identifier field_19208;

	public Function(Identifier identifier, Function.Executable[] executables) {
		this.field_19208 = identifier;
		this.executables = executables;
	}

	public Identifier method_17355() {
		return this.field_19208;
	}

	public Function.Executable[] getExecutables() {
		return this.executables;
	}

	public static Function method_17357(Identifier identifier, FunctionTickable functionTickable, List<String> list) {
		List<Function.Executable> list2 = Lists.newArrayListWithCapacity(list.size());

		for (int i = 0; i < list.size(); i++) {
			int j = i + 1;
			String string = ((String)list.get(i)).trim();
			StringReader stringReader = new StringReader(string);
			if (stringReader.canRead() && stringReader.peek() != '#') {
				if (stringReader.peek() == '/') {
					stringReader.skip();
					if (stringReader.peek() == '/') {
						throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
					}

					String string2 = stringReader.readUnquotedString();
					throw new IllegalArgumentException(
						"Unknown or invalid command '" + string + "' on line " + j + " (did you mean '" + string2 + "'? Do not use a preceding forwards slash.)"
					);
				}

				try {
					ParseResults<class_3915> parseResults = functionTickable.method_20453().method_2971().method_17518().parse(stringReader, functionTickable.method_20462());
					if (parseResults.getReader().canRead()) {
						if (parseResults.getExceptions().size() == 1) {
							throw (CommandSyntaxException)parseResults.getExceptions().values().iterator().next();
						}

						if (parseResults.getContext().getRange().isEmpty()) {
							throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
						}

						throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults.getReader());
					}

					list2.add(new Function.SimpleExecutable(parseResults));
				} catch (CommandSyntaxException var9) {
					throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + var9.getMessage());
				}
			}
		}

		return new Function(identifier, (Function.Executable[])list2.toArray(new Function.Executable[0]));
	}

	public interface Executable {
		void method_14541(FunctionTickable functionTickable, class_3915 arg, ArrayDeque<FunctionTickable.class_3350> arrayDeque, int i) throws CommandSyntaxException;
	}

	public static class FunctionExecutable implements Function.Executable {
		private final Function.FunctionIdentifier identifier;

		public FunctionExecutable(Function function) {
			this.identifier = new Function.FunctionIdentifier(function);
		}

		@Override
		public void method_14541(FunctionTickable functionTickable, class_3915 arg, ArrayDeque<FunctionTickable.class_3350> arrayDeque, int i) {
			Function function = this.identifier.method_14540(functionTickable);
			if (function != null) {
				Function.Executable[] executables = function.getExecutables();
				int j = i - arrayDeque.size();
				int k = Math.min(executables.length, j);

				for (int l = k - 1; l >= 0; l--) {
					arrayDeque.addFirst(new FunctionTickable.class_3350(functionTickable, arg, executables[l]));
				}
			}
		}

		public String toString() {
			return "function " + this.identifier.method_17358();
		}
	}

	public static class FunctionIdentifier {
		public static final Function.FunctionIdentifier EMPTY = new Function.FunctionIdentifier((Identifier)null);
		@Nullable
		private final Identifier identifier;
		private boolean field_16000;
		private Function function;

		public FunctionIdentifier(@Nullable Identifier identifier) {
			this.identifier = identifier;
		}

		public FunctionIdentifier(Function function) {
			this.identifier = null;
			this.function = function;
		}

		@Nullable
		public Function method_14540(FunctionTickable functionTickable) {
			if (!this.field_16000) {
				if (this.identifier != null) {
					this.function = functionTickable.getFunction(this.identifier);
				}

				this.field_16000 = true;
			}

			return this.function;
		}

		@Nullable
		public Identifier method_17358() {
			return this.function != null ? this.function.field_19208 : this.identifier;
		}
	}

	public static class SimpleExecutable implements Function.Executable {
		private final ParseResults<class_3915> field_19209;

		public SimpleExecutable(ParseResults<class_3915> parseResults) {
			this.field_19209 = parseResults;
		}

		@Override
		public void method_14541(FunctionTickable functionTickable, class_3915 arg, ArrayDeque<FunctionTickable.class_3350> arrayDeque, int i) throws CommandSyntaxException {
			functionTickable.method_20461()
				.execute(
					new ParseResults(
						this.field_19209.getContext().withSource(arg), this.field_19209.getStartIndex(), this.field_19209.getReader(), this.field_19209.getExceptions()
					)
				);
		}

		public String toString() {
			return this.field_19209.getReader().getString();
		}
	}
}
