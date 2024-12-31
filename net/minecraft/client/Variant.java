package net.minecraft.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class Variant {
	private final Identifier modelLocation;
	private final ModelRotation rotation;
	private final boolean uvLock;
	private final int weight;

	public Variant(Identifier identifier, ModelRotation modelRotation, boolean bl, int i) {
		this.modelLocation = identifier;
		this.rotation = modelRotation;
		this.uvLock = bl;
		this.weight = i;
	}

	public Identifier getIdentifier() {
		return this.modelLocation;
	}

	public ModelRotation getRotation() {
		return this.rotation;
	}

	public boolean getUvLock() {
		return this.uvLock;
	}

	public int getWeight() {
		return this.weight;
	}

	public String toString() {
		return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof Variant)) {
			return false;
		} else {
			Variant variant = (Variant)other;
			return this.modelLocation.equals(variant.modelLocation)
				&& this.rotation == variant.rotation
				&& this.uvLock == variant.uvLock
				&& this.weight == variant.weight;
		}
	}

	public int hashCode() {
		int i = this.modelLocation.hashCode();
		i = 31 * i + this.rotation.hashCode();
		i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
		return 31 * i + this.weight;
	}

	public static class VariantDeserializer implements JsonDeserializer<Variant> {
		public Variant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Identifier identifier = this.method_19260(jsonObject);
			ModelRotation modelRotation = this.readModelRotation(jsonObject);
			boolean bl = this.readUvLock(jsonObject);
			int i = this.readWeight(jsonObject);
			return new Variant(identifier, modelRotation, bl, i);
		}

		private boolean readUvLock(JsonObject object) {
			return JsonHelper.getBoolean(object, "uvlock", false);
		}

		protected ModelRotation readModelRotation(JsonObject object) {
			int i = JsonHelper.getInt(object, "x", 0);
			int j = JsonHelper.getInt(object, "y", 0);
			ModelRotation modelRotation = ModelRotation.get(i, j);
			if (modelRotation == null) {
				throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
			} else {
				return modelRotation;
			}
		}

		protected Identifier method_19260(JsonObject jsonObject) {
			return new Identifier(JsonHelper.getString(jsonObject, "model"));
		}

		protected int readWeight(JsonObject object) {
			int i = JsonHelper.getInt(object, "weight", 1);
			if (i < 1) {
				throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
			} else {
				return i;
			}
		}
	}
}
