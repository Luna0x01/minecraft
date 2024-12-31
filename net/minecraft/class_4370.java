package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_4370<T> implements class_4345 {
	private static final Logger field_21484 = LogManager.getLogger();
	private static final Gson field_21485 = new GsonBuilder().setPrettyPrinting().create();
	protected final class_4344 field_21481;
	protected final Registry<T> field_21482;
	protected final Map<Tag<T>, Tag.Builder<T>> field_21483 = Maps.newLinkedHashMap();

	protected class_4370(class_4344 arg, Registry<T> registry) {
		this.field_21481 = arg;
		this.field_21482 = registry;
	}

	protected abstract void method_20081();

	@Override
	public void method_19996(class_4346 arg) throws IOException {
		this.field_21483.clear();
		this.method_20081();
		TagContainer<T> tagContainer = new TagContainer<>(identifierx -> false, identifierx -> null, "", false, "generated");

		for (Entry<Tag<T>, Tag.Builder<T>> entry : this.field_21483.entrySet()) {
			Identifier identifier = ((Tag)entry.getKey()).getId();
			if (!((Tag.Builder)entry.getValue()).applyTagGetter(tagContainer::method_21486)) {
				throw new UnsupportedOperationException("Unsupported referencing of tags!");
			}

			Tag<T> tag = ((Tag.Builder)entry.getValue()).build(identifier);
			JsonObject jsonObject = tag.toJson(this.field_21482::getId);
			Path path = this.method_20078(identifier);
			tagContainer.method_21488(tag);
			this.method_20080(tagContainer);

			try {
				String string = field_21485.toJson(jsonObject);
				String string2 = field_21406.hashUnencodedChars(string).toString();
				if (!Objects.equals(arg.method_19998(path), string2) || !Files.exists(path, new LinkOption[0])) {
					Files.createDirectories(path.getParent());
					BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
					Throwable var12 = null;

					try {
						bufferedWriter.write(string);
					} catch (Throwable var22) {
						var12 = var22;
						throw var22;
					} finally {
						if (bufferedWriter != null) {
							if (var12 != null) {
								try {
									bufferedWriter.close();
								} catch (Throwable var21) {
									var12.addSuppressed(var21);
								}
							} else {
								bufferedWriter.close();
							}
						}
					}
				}

				arg.method_19999(path, string2);
			} catch (IOException var24) {
				field_21484.error("Couldn't save tags to {}", path, var24);
			}
		}
	}

	protected abstract void method_20080(TagContainer<T> tagContainer);

	protected abstract Path method_20078(Identifier identifier);

	protected Tag.Builder<T> method_20079(Tag<T> tag) {
		return (Tag.Builder<T>)this.field_21483.computeIfAbsent(tag, tagx -> Tag.Builder.create());
	}
}
