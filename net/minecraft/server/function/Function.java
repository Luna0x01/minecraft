package net.minecraft.server.function;

import com.google.common.collect.Lists;
import java.util.ArrayDeque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;

public class Function {
	private final Function.Executable[] executables;

	public Function(Function.Executable[] executables) {
		this.executables = executables;
	}

	public Function.Executable[] getExecutables() {
		return this.executables;
	}

	public static Function fromLines(FunctionTickable functionTickable, List<String> lines) {
		List<Function.Executable> list = Lists.newArrayListWithCapacity(lines.size());

		for (String string : lines) {
			string = string.trim();
			if (!string.startsWith("#") && !string.isEmpty()) {
				String[] strings = string.split(" ", 2);
				String string2 = strings[0];
				if (!functionTickable.getCommandRegistry().getCommandMap().containsKey(string2)) {
					if (string2.startsWith("//")) {
						throw new IllegalArgumentException("Unknown or invalid command '" + string2 + "' (if you intended to make a comment, use '#' not '//')");
					}

					if (string2.startsWith("/") && string2.length() > 1) {
						throw new IllegalArgumentException(
							"Unknown or invalid command '" + string2 + "' (did you mean '" + string2.substring(1) + "'? Do not use a preceding forwards slash.)"
						);
					}

					throw new IllegalArgumentException("Unknown or invalid command '" + string2 + "'");
				}

				list.add(new Function.SimpleExecutable(string));
			}
		}

		return new Function((Function.Executable[])list.toArray(new Function.Executable[list.size()]));
	}

	public interface Executable {
		void execute(FunctionTickable functionTickable, CommandSource source, ArrayDeque<FunctionTickable.class_3350> arrayDeque, int i);
	}

	public static class FunctionExecutable implements Function.Executable {
		private final Function.FunctionIdentifier identifier;

		public FunctionExecutable(Function function) {
			this.identifier = new Function.FunctionIdentifier(function);
		}

		@Override
		public void execute(FunctionTickable functionTickable, CommandSource source, ArrayDeque<FunctionTickable.class_3350> arrayDeque, int i) {
			Function function = this.identifier.method_14540(functionTickable);
			if (function != null) {
				Function.Executable[] executables = function.getExecutables();
				int j = i - arrayDeque.size();
				int k = Math.min(executables.length, j);

				for (int l = k - 1; l >= 0; l--) {
					arrayDeque.addFirst(new FunctionTickable.class_3350(functionTickable, source, executables[l]));
				}
			}
		}

		public String toString() {
			return "/function " + this.identifier;
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

		public String toString() {
			return String.valueOf(this.identifier);
		}
	}

	public static class SimpleExecutable implements Function.Executable {
		private final String field_16002;

		public SimpleExecutable(String string) {
			this.field_16002 = string;
		}

		@Override
		public void execute(FunctionTickable functionTickable, CommandSource source, ArrayDeque<FunctionTickable.class_3350> arrayDeque, int i) {
			functionTickable.getCommandRegistry().execute(source, this.field_16002);
		}

		public String toString() {
			return "/" + this.field_16002;
		}
	}
}
