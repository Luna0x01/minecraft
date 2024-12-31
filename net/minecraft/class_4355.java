package net.minecraft;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_4355 implements class_4345 {
	private final class_4344 field_21425;

	public class_4355(class_4344 arg) {
		this.field_21425 = arg;
	}

	@Override
	public void method_19996(class_4346 arg) throws IOException {
		JsonObject jsonObject = new JsonObject();

		for (Item item : Registry.ITEM) {
			Identifier identifier = Registry.ITEM.getId(item);
			JsonObject jsonObject2 = new JsonObject();
			jsonObject2.addProperty("protocol_id", Item.getRawId(item));
			jsonObject.add(identifier.toString(), jsonObject2);
		}

		Path path = this.field_21425.method_19993().resolve("reports/items.json");
		Files.createDirectories(path.getParent());
		BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
		Throwable var18 = null;

		try {
			String string = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
			bufferedWriter.write(string);
		} catch (Throwable var14) {
			var18 = var14;
			throw var14;
		} finally {
			if (bufferedWriter != null) {
				if (var18 != null) {
					try {
						bufferedWriter.close();
					} catch (Throwable var13) {
						var18.addSuppressed(var13);
					}
				} else {
					bufferedWriter.close();
				}
			}
		}
	}

	@Override
	public String method_19995() {
		return "Item List";
	}
}
