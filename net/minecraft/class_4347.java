package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4347 implements class_4345 {
	private static final Logger field_21413 = LogManager.getLogger();
	private static final Gson field_21414 = new GsonBuilder().setPrettyPrinting().create();
	private final class_4344 field_21415;
	private final List<Consumer<Consumer<SimpleAdvancement>>> field_21416 = ImmutableList.of(
		new class_4352(), new class_4349(), new class_4348(), new class_4350(), new class_4351()
	);

	public class_4347(class_4344 arg) {
		this.field_21415 = arg;
	}

	@Override
	public void method_19996(class_4346 arg) throws IOException {
		Path path = this.field_21415.method_19993();
		Set<Identifier> set = Sets.newHashSet();
		Consumer<SimpleAdvancement> consumer = simpleAdvancement -> {
			if (!set.add(simpleAdvancement.getIdentifier())) {
				throw new IllegalStateException("Duplicate advancement " + simpleAdvancement.getIdentifier());
			} else {
				this.method_20008(
					arg,
					simpleAdvancement.asTaskAdvancement().method_20259(),
					path.resolve("data/" + simpleAdvancement.getIdentifier().getNamespace() + "/advancements/" + simpleAdvancement.getIdentifier().getPath() + ".json")
				);
			}
		};

		for (Consumer<Consumer<SimpleAdvancement>> consumer2 : this.field_21416) {
			consumer2.accept(consumer);
		}
	}

	private void method_20008(class_4346 arg, JsonObject jsonObject, Path path) {
		try {
			String string = field_21414.toJson(jsonObject);
			String string2 = field_21406.hashUnencodedChars(string).toString();
			if (!Objects.equals(arg.method_19998(path), string2) || !Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent());
				BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
				Throwable var7 = null;

				try {
					bufferedWriter.write(string);
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (bufferedWriter != null) {
						if (var7 != null) {
							try {
								bufferedWriter.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							bufferedWriter.close();
						}
					}
				}
			}

			arg.method_19999(path, string2);
		} catch (IOException var19) {
			field_21413.error("Couldn't save advancement {}", path, var19);
		}
	}

	@Override
	public String method_19995() {
		return "Advancements";
	}
}
