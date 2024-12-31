package net.minecraft.resource;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.class_4457;
import net.minecraft.util.Identifier;

public interface Resource extends Closeable {
	Identifier getId();

	InputStream getInputStream();

	boolean hasMetadata();

	@Nullable
	<T> T method_21371(class_4457<T> arg);

	String getResourcePackName();
}
