package net.minecraft.world.level;

public class LevelGeneratorType {
	public static final LevelGeneratorType[] TYPES = new LevelGeneratorType[16];
	public static final LevelGeneratorType DEFAULT = new LevelGeneratorType(0, "default", 1).setVersioned();
	public static final LevelGeneratorType FLAT = new LevelGeneratorType(1, "flat");
	public static final LevelGeneratorType LARGE_BIOMES = new LevelGeneratorType(2, "largeBiomes");
	public static final LevelGeneratorType AMPLIFIED = new LevelGeneratorType(3, "amplified").setHasInfo();
	public static final LevelGeneratorType CUSTOMIZED = new LevelGeneratorType(4, "customized");
	public static final LevelGeneratorType DEBUG = new LevelGeneratorType(5, "debug_all_block_states");
	public static final LevelGeneratorType DEFAULT_1_1 = new LevelGeneratorType(8, "default_1_1", 0).setVisible(false);
	private final int id;
	private final String name;
	private final int version;
	private boolean visible;
	private boolean versioned;
	private boolean info;

	private LevelGeneratorType(int i, String string) {
		this(i, string, 0);
	}

	private LevelGeneratorType(int i, String string, int j) {
		this.name = string;
		this.version = j;
		this.visible = true;
		this.id = i;
		TYPES[i] = this;
	}

	public String getName() {
		return this.name;
	}

	public String getTranslationKey() {
		return "generator." + this.name;
	}

	public String getInfoTranslationKey() {
		return this.getTranslationKey() + ".info";
	}

	public int getVersion() {
		return this.version;
	}

	public LevelGeneratorType getTypeForVersion(int version) {
		return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
	}

	private LevelGeneratorType setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public boolean isVisible() {
		return this.visible;
	}

	private LevelGeneratorType setVersioned() {
		this.versioned = true;
		return this;
	}

	public boolean isVersioned() {
		return this.versioned;
	}

	public static LevelGeneratorType getTypeFromName(String name) {
		for (int i = 0; i < TYPES.length; i++) {
			if (TYPES[i] != null && TYPES[i].name.equalsIgnoreCase(name)) {
				return TYPES[i];
			}
		}

		return null;
	}

	public int getId() {
		return this.id;
	}

	public boolean hasInfo() {
		return this.info;
	}

	private LevelGeneratorType setHasInfo() {
		this.info = true;
		return this;
	}
}
