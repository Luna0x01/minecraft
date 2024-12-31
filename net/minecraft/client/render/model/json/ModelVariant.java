package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class ModelVariant implements ModelBakeSettings {
	private final Identifier location;
	private final net.minecraft.client.render.model.ModelRotation rotation;
	private final boolean uvLock;
	private final int weight;

	public ModelVariant(Identifier identifier, net.minecraft.client.render.model.ModelRotation modelRotation, boolean bl, int i) {
		this.location = identifier;
		this.rotation = modelRotation;
		this.uvLock = bl;
		this.weight = i;
	}

	public Identifier getLocation() {
		return this.location;
	}

	@Override
	public net.minecraft.client.render.model.ModelRotation getRotation() {
		return this.rotation;
	}

	@Override
	public boolean isUvLocked() {
		return this.uvLock;
	}

	public int getWeight() {
		return this.weight;
	}

	public String toString() {
		return "Variant{modelLocation=" + this.location + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof ModelVariant)) {
			return false;
		} else {
			ModelVariant modelVariant = (ModelVariant)object;
			return this.location.equals(modelVariant.location)
				&& this.rotation == modelVariant.rotation
				&& this.uvLock == modelVariant.uvLock
				&& this.weight == modelVariant.weight;
		}
	}

	public int hashCode() {
		int i = this.location.hashCode();
		i = 31 * i + this.rotation.hashCode();
		i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
		return 31 * i + this.weight;
	}

	public static class Deserializer implements JsonDeserializer<ModelVariant> {
		public ModelVariant method_3513(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Identifier identifier = this.deserializeModel(jsonObject);
			net.minecraft.client.render.model.ModelRotation modelRotation = this.deserializeRotation(jsonObject);
			boolean bl = this.deserializeUvLock(jsonObject);
			int i = this.deserializeWeight(jsonObject);
			return new ModelVariant(identifier, modelRotation, bl, i);
		}

		private boolean deserializeUvLock(JsonObject jsonObject) {
			return JsonHelper.getBoolean(jsonObject, "uvlock", false);
		}

		protected net.minecraft.client.render.model.ModelRotation deserializeRotation(JsonObject jsonObject) {
			int i = JsonHelper.getInt(jsonObject, "x", 0);
			int j = JsonHelper.getInt(jsonObject, "y", 0);
			net.minecraft.client.render.model.ModelRotation modelRotation = net.minecraft.client.render.model.ModelRotation.get(i, j);
			if (modelRotation == null) {
				throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
			} else {
				return modelRotation;
			}
		}

		protected Identifier deserializeModel(JsonObject jsonObject) {
			return new Identifier(JsonHelper.getString(jsonObject, "model"));
		}

		protected int deserializeWeight(JsonObject jsonObject) {
			int i = JsonHelper.getInt(jsonObject, "weight", 1);
			if (i < 1) {
				throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
			} else {
				return i;
			}
		}
	}
}
