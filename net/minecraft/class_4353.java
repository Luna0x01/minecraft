package net.minecraft;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class class_4353 implements class_4345 {
	private final class_4344 field_21423;

	public class_4353(class_4344 arg) {
		this.field_21423 = arg;
	}

	@Override
	public void method_19996(class_4346 arg) throws IOException {
		JsonObject jsonObject = new JsonObject();

		for (Block block : Registry.BLOCK) {
			Identifier identifier = Registry.BLOCK.getId(block);
			JsonObject jsonObject2 = new JsonObject();
			StateManager<Block, BlockState> stateManager = block.getStateManager();
			if (!stateManager.getProperties().isEmpty()) {
				JsonObject jsonObject3 = new JsonObject();

				for (Property<?> property : stateManager.getProperties()) {
					JsonArray jsonArray = new JsonArray();

					for (Comparable<?> comparable : property.getValues()) {
						jsonArray.add(Util.method_20219(property, comparable));
					}

					jsonObject3.add(property.getName(), jsonArray);
				}

				jsonObject2.add("properties", jsonObject3);
			}

			JsonArray jsonArray2 = new JsonArray();
			UnmodifiableIterator var29 = stateManager.getBlockStates().iterator();

			while (var29.hasNext()) {
				BlockState blockState = (BlockState)var29.next();
				JsonObject jsonObject4 = new JsonObject();
				JsonObject jsonObject5 = new JsonObject();

				for (Property<?> property2 : stateManager.getProperties()) {
					jsonObject5.addProperty(property2.getName(), Util.method_20219(property2, blockState.getProperty(property2)));
				}

				if (jsonObject5.size() > 0) {
					jsonObject4.add("properties", jsonObject5);
				}

				jsonObject4.addProperty("id", Block.getRawIdFromState(blockState));
				if (blockState == block.getDefaultState()) {
					jsonObject4.addProperty("default", true);
				}

				jsonArray2.add(jsonObject4);
			}

			jsonObject2.add("states", jsonArray2);
			jsonObject.add(identifier.toString(), jsonObject2);
		}

		Path path = this.field_21423.method_19993().resolve("reports/blocks.json");
		Files.createDirectories(path.getParent());
		BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
		Throwable var26 = null;

		try {
			String string = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
			bufferedWriter.write(string);
		} catch (Throwable var22) {
			var26 = var22;
			throw var22;
		} finally {
			if (bufferedWriter != null) {
				if (var26 != null) {
					try {
						bufferedWriter.close();
					} catch (Throwable var21) {
						var26.addSuppressed(var21);
					}
				} else {
					bufferedWriter.close();
				}
			}
		}
	}

	@Override
	public String method_19995() {
		return "Block List";
	}
}
