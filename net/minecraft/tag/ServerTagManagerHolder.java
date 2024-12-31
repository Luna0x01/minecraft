package net.minecraft.tag;

public class ServerTagManagerHolder {
	private static volatile TagManager tagManager = RequiredTagListRegistry.createBuiltinTagManager();

	public static TagManager getTagManager() {
		return tagManager;
	}

	public static void setTagManager(TagManager tagManager) {
		ServerTagManagerHolder.tagManager = tagManager;
	}
}
