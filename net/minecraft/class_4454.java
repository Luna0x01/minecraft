package net.minecraft;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public interface class_4454 extends Closeable {
	InputStream method_21330(String string) throws IOException;

	InputStream method_5897(class_4455 arg, Identifier identifier) throws IOException;

	Collection<Identifier> method_21328(class_4455 arg, String string, int i, Predicate<String> predicate);

	boolean method_5900(class_4455 arg, Identifier identifier);

	Set<String> method_21327(class_4455 arg);

	@Nullable
	<T> T method_21329(class_4457<T> arg) throws IOException;

	String method_5899();
}
