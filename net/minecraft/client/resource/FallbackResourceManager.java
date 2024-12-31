package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.class_4454;
import net.minecraft.class_4455;
import net.minecraft.class_4469;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements ResourceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final List<class_4454> resourcePacks = Lists.newArrayList();
	private final class_4455 field_6610;

	public FallbackResourceManager(class_4455 arg) {
		this.field_6610 = arg;
	}

	public void method_5882(class_4454 arg) {
		this.resourcePacks.add(arg);
	}

	@Override
	public Set<String> getAllNamespaces() {
		return Collections.emptySet();
	}

	@Override
	public Resource getResource(Identifier id) throws IOException {
		this.method_12498(id);
		class_4454 lv = null;
		Identifier identifier = method_5883(id);

		for (int i = this.resourcePacks.size() - 1; i >= 0; i--) {
			class_4454 lv2 = (class_4454)this.resourcePacks.get(i);
			if (lv == null && lv2.method_5900(this.field_6610, identifier)) {
				lv = lv2;
			}

			if (lv2.method_5900(this.field_6610, id)) {
				InputStream inputStream = null;
				if (lv != null) {
					inputStream = this.method_10362(identifier, lv);
				}

				return new class_4469(lv2.method_5899(), id, this.method_10362(id, lv2), inputStream);
			}
		}

		throw new FileNotFoundException(id.toString());
	}

	protected InputStream method_10362(Identifier identifier, class_4454 arg) throws IOException {
		InputStream inputStream = arg.method_5897(this.field_6610, identifier);
		return (InputStream)(LOGGER.isDebugEnabled() ? new FallbackResourceManager.LeakedResourceStream(inputStream, identifier, arg.method_5899()) : inputStream);
	}

	private void method_12498(Identifier identifier) throws IOException {
		if (identifier.getPath().contains("..")) {
			throw new IOException("Invalid relative path to resource: " + identifier);
		}
	}

	@Override
	public List<Resource> getAllResources(Identifier id) throws IOException {
		this.method_12498(id);
		List<Resource> list = Lists.newArrayList();
		Identifier identifier = method_5883(id);

		for (class_4454 lv : this.resourcePacks) {
			if (lv.method_5900(this.field_6610, id)) {
				InputStream inputStream = lv.method_5900(this.field_6610, identifier) ? this.method_10362(identifier, lv) : null;
				list.add(new class_4469(lv.method_5899(), id, this.method_10362(id, lv), inputStream));
			}
		}

		if (list.isEmpty()) {
			throw new FileNotFoundException(id.toString());
		} else {
			return list;
		}
	}

	@Override
	public Collection<Identifier> method_21372(String string, Predicate<String> predicate) {
		List<Identifier> list = Lists.newArrayList();

		for (class_4454 lv : this.resourcePacks) {
			list.addAll(lv.method_21328(this.field_6610, string, Integer.MAX_VALUE, predicate));
		}

		Collections.sort(list);
		return list;
	}

	static Identifier method_5883(Identifier identifier) {
		return new Identifier(identifier.getNamespace(), identifier.getPath() + ".mcmeta");
	}

	static class LeakedResourceStream extends InputStream {
		private final InputStream stream;
		private final String field_11260;
		private boolean field_11261;

		public LeakedResourceStream(InputStream inputStream, Identifier identifier, String string) {
			this.stream = inputStream;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
			this.field_11260 = "Leaked resource: '" + identifier + "' loaded from pack: '" + string + "'\n" + byteArrayOutputStream;
		}

		public void close() throws IOException {
			this.stream.close();
			this.field_11261 = true;
		}

		protected void finalize() throws Throwable {
			if (!this.field_11261) {
				FallbackResourceManager.LOGGER.warn(this.field_11260);
			}

			super.finalize();
		}

		public int read() throws IOException {
			return this.stream.read();
		}
	}
}
