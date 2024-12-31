package net.minecraft.util;

public class JsonIntSerializable {
	private int value;
	private JsonElementProvider jsonElementProvider;

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public <T extends JsonElementProvider> T getJsonElementProvider() {
		return (T)this.jsonElementProvider;
	}

	public void setJsonElementProvider(JsonElementProvider jsonElementProvider) {
		this.jsonElementProvider = jsonElementProvider;
	}
}
