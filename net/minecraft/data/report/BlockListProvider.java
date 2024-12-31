package net.minecraft.data.report;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class BlockListProvider implements DataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator root;

	public BlockListProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache dataCache) throws IOException {
		JsonObject jsonObject = new JsonObject();

		for (Block block : Registry.field_11146) {
			Identifier identifier = Registry.field_11146.getId(block);
			JsonObject jsonObject2 = new JsonObject();
			StateManager<Block, BlockState> stateManager = block.getStateManager();
			if (!stateManager.getProperties().isEmpty()) {
				JsonObject jsonObject3 = new JsonObject();

				for (Property<?> property : stateManager.getProperties()) {
					JsonArray jsonArray = new JsonArray();

					for (Comparable<?> comparable : property.getValues()) {
						jsonArray.add(Util.getValueAsString(property, comparable));
					}

					jsonObject3.add(property.getName(), jsonArray);
				}

				jsonObject2.add("properties", jsonObject3);
			}

			JsonArray jsonArray2 = new JsonArray();
			UnmodifiableIterator var17 = stateManager.getStates().iterator();

			while (var17.hasNext()) {
				BlockState blockState = (BlockState)var17.next();
				JsonObject jsonObject4 = new JsonObject();
				JsonObject jsonObject5 = new JsonObject();

				for (Property<?> property2 : stateManager.getProperties()) {
					jsonObject5.addProperty(property2.getName(), Util.getValueAsString(property2, blockState.get(property2)));
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

		Path path = this.root.getOutput().resolve("reports/blocks.json");
		DataProvider.writeToPath(GSON, dataCache, jsonObject, path);
	}

	@Override
	public String getName() {
		return "Block List";
	}
}
