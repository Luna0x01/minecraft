package net.minecraft.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.class_4454;
import net.minecraft.class_4455;
import net.minecraft.class_4457;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractFileResourcePack implements class_4454 {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final File base;

	public AbstractFileResourcePack(File file) {
		this.base = file;
	}

	private static String method_21325(class_4455 arg, Identifier identifier) {
		return String.format("%s/%s/%s", arg.method_21331(), identifier.getNamespace(), identifier.getPath());
	}

	protected static String relativize(File base, File target) {
		return base.toURI().relativize(target.toURI()).getPath();
	}

	@Override
	public InputStream method_5897(class_4455 arg, Identifier identifier) throws IOException {
		return this.openFile(method_21325(arg, identifier));
	}

	@Override
	public boolean method_5900(class_4455 arg, Identifier identifier) {
		return this.containsFile(method_21325(arg, identifier));
	}

	protected abstract InputStream openFile(String name) throws IOException;

	@Override
	public InputStream method_21330(String string) throws IOException {
		if (!string.contains("/") && !string.contains("\\")) {
			return this.openFile(string);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	protected abstract boolean containsFile(String name);

	protected void warnNonLowercaseNamespace(String namespace) {
		LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", namespace, this.base);
	}

	@Nullable
	@Override
	public <T> T method_21329(class_4457<T> arg) throws IOException {
		return method_21324(arg, this.openFile("pack.mcmeta"));
	}

	@Nullable
	public static <T> T method_21324(class_4457<T> arg, InputStream inputStream) {
		JsonObject jsonObject;
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			Throwable var4 = null;

			try {
				jsonObject = JsonHelper.method_21500(bufferedReader);
			} catch (Throwable var16) {
				var4 = var16;
				throw var16;
			} finally {
				if (bufferedReader != null) {
					if (var4 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var14) {
							var4.addSuppressed(var14);
						}
					} else {
						bufferedReader.close();
					}
				}
			}
		} catch (JsonParseException | IOException var18) {
			LOGGER.error("Couldn't load {} metadata", arg.method_5956(), var18);
			return null;
		}

		if (!jsonObject.has(arg.method_5956())) {
			return null;
		} else {
			try {
				return arg.method_21335(JsonHelper.getObject(jsonObject, arg.method_5956()));
			} catch (JsonParseException var15) {
				LOGGER.error("Couldn't load {} metadata", arg.method_5956(), var15);
				return null;
			}
		}
	}

	@Override
	public String method_5899() {
		return this.base.getName();
	}
}
