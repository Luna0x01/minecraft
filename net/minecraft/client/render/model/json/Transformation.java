package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class Transformation {
	public static final Transformation DEFAULT = new Transformation(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
	public final Vector3f rotation;
	public final Vector3f translation;
	public final Vector3f scale;

	public Transformation(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3) {
		this.rotation = new Vector3f(vector3f);
		this.translation = new Vector3f(vector3f2);
		this.scale = new Vector3f(vector3f3);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (this.getClass() != obj.getClass()) {
			return false;
		} else {
			Transformation transformation = (Transformation)obj;
			if (!this.rotation.equals(transformation.rotation)) {
				return false;
			} else {
				return !this.scale.equals(transformation.scale) ? false : this.translation.equals(transformation.translation);
			}
		}
	}

	public int hashCode() {
		int i = this.rotation.hashCode();
		i = 31 * i + this.translation.hashCode();
		return 31 * i + this.scale.hashCode();
	}

	static class Deserializer implements JsonDeserializer<Transformation> {
		private static final Vector3f rotation = new Vector3f(0.0F, 0.0F, 0.0F);
		private static final Vector3f translation = new Vector3f(0.0F, 0.0F, 0.0F);
		private static final Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);

		public Transformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Vector3f vector3f = this.deserializeVector3f(jsonObject, "rotation", rotation);
			Vector3f vector3f2 = this.deserializeVector3f(jsonObject, "translation", translation);
			vector3f2.scale(0.0625F);
			vector3f2.x = MathHelper.clamp(vector3f2.x, -1.5F, 1.5F);
			vector3f2.y = MathHelper.clamp(vector3f2.y, -1.5F, 1.5F);
			vector3f2.z = MathHelper.clamp(vector3f2.z, -1.5F, 1.5F);
			Vector3f vector3f3 = this.deserializeVector3f(jsonObject, "scale", scale);
			vector3f3.x = MathHelper.clamp(vector3f3.x, -4.0F, 4.0F);
			vector3f3.y = MathHelper.clamp(vector3f3.y, -4.0F, 4.0F);
			vector3f3.z = MathHelper.clamp(vector3f3.z, -4.0F, 4.0F);
			return new Transformation(vector3f, vector3f2, vector3f3);
		}

		private Vector3f deserializeVector3f(JsonObject json, String key, Vector3f fallback) {
			if (!json.has(key)) {
				return fallback;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(json, key);
				if (jsonArray.size() != 3) {
					throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray.size());
				} else {
					float[] fs = new float[3];

					for (int i = 0; i < fs.length; i++) {
						fs[i] = JsonHelper.asFloat(jsonArray.get(i), key + "[" + i + "]");
					}

					return new Vector3f(fs[0], fs[1], fs[2]);
				}
			}
		}
	}
}
