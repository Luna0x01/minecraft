package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;

public class ModelElementFace {
	public static final int field_32789 = -1;
	public final Direction cullFace;
	public final int tintIndex;
	public final String textureId;
	public final ModelElementTexture textureData;

	public ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, ModelElementTexture textureData) {
		this.cullFace = cullFace;
		this.tintIndex = tintIndex;
		this.textureId = textureId;
		this.textureData = textureData;
	}

	protected static class Deserializer implements JsonDeserializer<ModelElementFace> {
		private static final int DEFAULT_TINT_INDEX = -1;

		public ModelElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Direction direction = this.deserializeCullFace(jsonObject);
			int i = this.deserializeTintIndex(jsonObject);
			String string = this.deserializeTexture(jsonObject);
			ModelElementTexture modelElementTexture = (ModelElementTexture)jsonDeserializationContext.deserialize(jsonObject, ModelElementTexture.class);
			return new ModelElementFace(direction, i, string, modelElementTexture);
		}

		protected int deserializeTintIndex(JsonObject object) {
			return JsonHelper.getInt(object, "tintindex", -1);
		}

		private String deserializeTexture(JsonObject object) {
			return JsonHelper.getString(object, "texture");
		}

		@Nullable
		private Direction deserializeCullFace(JsonObject object) {
			String string = JsonHelper.getString(object, "cullface", "");
			return Direction.byName(string);
		}
	}
}
