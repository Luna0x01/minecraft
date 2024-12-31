package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class ModelElement {
	public final Vector3f from;
	public final Vector3f to;
	public final Map<Direction, ModelElementFace> faces;
	public final ModelRotation rotation;
	public final boolean shade;

	public ModelElement(Vector3f vector3f, Vector3f vector3f2, Map<Direction, ModelElementFace> map, ModelRotation modelRotation, boolean bl) {
		this.from = vector3f;
		this.to = vector3f2;
		this.faces = map;
		this.rotation = modelRotation;
		this.shade = bl;
		this.initTextures();
	}

	private void initTextures() {
		for (Entry<Direction, ModelElementFace> entry : this.faces.entrySet()) {
			float[] fs = this.getRotatedMatrix((Direction)entry.getKey());
			((ModelElementFace)entry.getValue()).textureReference.setUvs(fs);
		}
	}

	private float[] getRotatedMatrix(Direction direction) {
		float[] fs;
		switch (direction) {
			case DOWN:
			case UP:
				fs = new float[]{this.from.x, this.from.z, this.to.x, this.to.z};
				break;
			case NORTH:
			case SOUTH:
				fs = new float[]{this.from.x, 16.0F - this.to.y, this.to.x, 16.0F - this.from.y};
				break;
			case WEST:
			case EAST:
				fs = new float[]{this.from.z, 16.0F - this.to.y, this.to.z, 16.0F - this.from.y};
				break;
			default:
				throw new NullPointerException();
		}

		return fs;
	}

	static class Deserializer implements JsonDeserializer<ModelElement> {
		public ModelElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Vector3f vector3f = this.getFromVector(jsonObject);
			Vector3f vector3f2 = this.getToVector(jsonObject);
			ModelRotation modelRotation = this.deserializeRotation(jsonObject);
			Map<Direction, ModelElementFace> map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
			if (jsonObject.has("shade") && !JsonHelper.hasBoolean(jsonObject, "shade")) {
				throw new JsonParseException("Expected shade to be a Boolean");
			} else {
				boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
				return new ModelElement(vector3f, vector3f2, map, modelRotation, bl);
			}
		}

		private ModelRotation deserializeRotation(JsonObject object) {
			ModelRotation modelRotation = null;
			if (object.has("rotation")) {
				JsonObject jsonObject = JsonHelper.getObject(object, "rotation");
				Vector3f vector3f = this.deserializeVector3f(jsonObject, "origin");
				vector3f.scale(0.0625F);
				Direction.Axis axis = this.deserializeAxis(jsonObject);
				float f = this.deserializeRotationAngle(jsonObject);
				boolean bl = JsonHelper.getBoolean(jsonObject, "rescale", false);
				modelRotation = new ModelRotation(vector3f, axis, f, bl);
			}

			return modelRotation;
		}

		private float deserializeRotationAngle(JsonObject object) {
			float f = JsonHelper.getFloat(object, "angle");
			if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
				throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
			} else {
				return f;
			}
		}

		private Direction.Axis deserializeAxis(JsonObject object) {
			String string = JsonHelper.getString(object, "axis");
			Direction.Axis axis = Direction.Axis.fromName(string.toLowerCase());
			if (axis == null) {
				throw new JsonParseException("Invalid rotation axis: " + string);
			} else {
				return axis;
			}
		}

		private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext context, JsonObject object) {
			Map<Direction, ModelElementFace> map = this.deserializeFaces(context, object);
			if (map.isEmpty()) {
				throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
			} else {
				return map;
			}
		}

		private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext context, JsonObject object) {
			Map<Direction, ModelElementFace> map = Maps.newEnumMap(Direction.class);
			JsonObject jsonObject = JsonHelper.getObject(object, "faces");

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				Direction direction = this.getDirection((String)entry.getKey());
				map.put(direction, (ModelElementFace)context.deserialize((JsonElement)entry.getValue(), ModelElementFace.class));
			}

			return map;
		}

		private Direction getDirection(String name) {
			Direction direction = Direction.byName(name);
			if (direction == null) {
				throw new JsonParseException("Unknown facing: " + name);
			} else {
				return direction;
			}
		}

		private Vector3f getToVector(JsonObject json) {
			Vector3f vector3f = this.deserializeVector3f(json, "to");
			if (!(vector3f.x < -16.0F) && !(vector3f.y < -16.0F) && !(vector3f.z < -16.0F) && !(vector3f.x > 32.0F) && !(vector3f.y > 32.0F) && !(vector3f.z > 32.0F)) {
				return vector3f;
			} else {
				throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f getFromVector(JsonObject json) {
			Vector3f vector3f = this.deserializeVector3f(json, "from");
			if (!(vector3f.x < -16.0F) && !(vector3f.y < -16.0F) && !(vector3f.z < -16.0F) && !(vector3f.x > 32.0F) && !(vector3f.y > 32.0F) && !(vector3f.z > 32.0F)) {
				return vector3f;
			} else {
				throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f deserializeVector3f(JsonObject json, String key) {
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
