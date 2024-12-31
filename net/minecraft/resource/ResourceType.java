package net.minecraft.resource;

public enum ResourceType {
	field_14188("assets"),
	field_14190("data");

	private final String directory;

	private ResourceType(String string2) {
		this.directory = string2;
	}

	public String getDirectory() {
		return this.directory;
	}
}
