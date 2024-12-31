package net.minecraft;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4469 implements Resource {
	private static final Logger field_21929 = LogManager.getLogger();
	public static final Executor field_21928 = Executors.newSingleThreadExecutor(
		new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Resource IO {0}").setUncaughtExceptionHandler(new class_4325(field_21929)).build()
	);
	private final String field_21930;
	private final Identifier field_21931;
	private final InputStream field_21932;
	private final InputStream field_21933;
	private boolean field_21934;
	private JsonObject field_21935;

	public class_4469(String string, Identifier identifier, InputStream inputStream, @Nullable InputStream inputStream2) {
		this.field_21930 = string;
		this.field_21931 = identifier;
		this.field_21932 = inputStream;
		this.field_21933 = inputStream2;
	}

	@Override
	public Identifier getId() {
		return this.field_21931;
	}

	@Override
	public InputStream getInputStream() {
		return this.field_21932;
	}

	@Override
	public boolean hasMetadata() {
		return this.field_21933 != null;
	}

	@Nullable
	@Override
	public <T> T method_21371(class_4457<T> arg) {
		if (!this.hasMetadata()) {
			return null;
		} else {
			if (this.field_21935 == null && !this.field_21934) {
				this.field_21934 = true;
				BufferedReader bufferedReader = null;

				try {
					bufferedReader = new BufferedReader(new InputStreamReader(this.field_21933, StandardCharsets.UTF_8));
					this.field_21935 = JsonHelper.method_21500(bufferedReader);
				} finally {
					IOUtils.closeQuietly(bufferedReader);
				}
			}

			if (this.field_21935 == null) {
				return null;
			} else {
				String string = arg.method_5956();
				return this.field_21935.has(string) ? arg.method_21335(JsonHelper.getObject(this.field_21935, string)) : null;
			}
		}
	}

	@Override
	public String getResourcePackName() {
		return this.field_21930;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_4469)) {
			return false;
		} else {
			class_4469 lv = (class_4469)object;
			if (this.field_21931 != null ? this.field_21931.equals(lv.field_21931) : lv.field_21931 == null) {
				return this.field_21930 != null ? this.field_21930.equals(lv.field_21930) : lv.field_21930 == null;
			} else {
				return false;
			}
		}
	}

	public int hashCode() {
		int i = this.field_21930 != null ? this.field_21930.hashCode() : 0;
		return 31 * i + (this.field_21931 != null ? this.field_21931.hashCode() : 0);
	}

	public void close() throws IOException {
		this.field_21932.close();
		if (this.field_21933 != null) {
			this.field_21933.close();
		}
	}
}
