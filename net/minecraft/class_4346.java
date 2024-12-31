package net.minecraft;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4346 {
	private static final Logger field_21407 = LogManager.getLogger();
	private final Path field_21408;
	private final Path field_21409;
	private int field_21410;
	private final Map<Path, String> field_21411 = Maps.newHashMap();
	private final Map<Path, String> field_21412 = Maps.newHashMap();

	public class_4346(Path path, String string) throws IOException {
		this.field_21408 = path;
		Path path2 = path.resolve(".cache");
		Files.createDirectories(path2);
		this.field_21409 = path2.resolve(string);
		this.method_20004().forEach(pathx -> {
			String var10000 = (String)this.field_21411.put(pathx, "");
		});
		if (Files.isReadable(this.field_21409)) {
			IOUtils.readLines(Files.newInputStream(this.field_21409), Charsets.UTF_8).forEach(stringx -> {
				int i = stringx.indexOf(32);
				this.field_21411.put(path.resolve(stringx.substring(i + 1)), stringx.substring(0, i));
			});
		}
	}

	public void method_19997() throws IOException {
		this.method_20001();

		Writer writer;
		try {
			writer = Files.newBufferedWriter(this.field_21409);
		} catch (IOException var3) {
			field_21407.warn("Unable write cachefile {}: {}", this.field_21409, var3.toString());
			return;
		}

		IOUtils.writeLines(
			(Collection)this.field_21412
				.entrySet()
				.stream()
				.map(entry -> (String)entry.getValue() + ' ' + this.field_21408.relativize((Path)entry.getKey()))
				.collect(Collectors.toList()),
			System.lineSeparator(),
			writer
		);
		writer.close();
		field_21407.debug("Caching: cache hits: {}, created: {} removed: {}", this.field_21410, this.field_21412.size() - this.field_21410, this.field_21411.size());
	}

	@Nullable
	public String method_19998(Path path) {
		return (String)this.field_21411.get(path);
	}

	public void method_19999(Path path, String string) {
		this.field_21412.put(path, string);
		if (Objects.equals(this.field_21411.remove(path), string)) {
			this.field_21410++;
		}
	}

	public boolean method_20002(Path path) {
		return this.field_21411.containsKey(path);
	}

	private void method_20001() throws IOException {
		this.method_20004().forEach(path -> {
			if (this.method_20002(path)) {
				try {
					Files.delete(path);
				} catch (IOException var3) {
					field_21407.debug("Unable to delete: {} ({})", path, var3.toString());
				}
			}
		});
	}

	private Stream<Path> method_20004() throws IOException {
		return Files.walk(this.field_21408).filter(path -> !Objects.equals(this.field_21409, path) && !Files.isDirectory(path, new LinkOption[0]));
	}
}
