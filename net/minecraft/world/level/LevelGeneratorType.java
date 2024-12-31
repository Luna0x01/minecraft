package net.minecraft.world.level;

public class LevelGeneratorType {
	public static final LevelGeneratorType[] TYPES = new LevelGeneratorType[16];
	public static final LevelGeneratorType DEFAULT = new LevelGeneratorType(0, "default", 1).setVersioned();
	public static final LevelGeneratorType FLAT = new LevelGeneratorType(1, "flat").method_16400(true);
	public static final LevelGeneratorType LARGE_BIOMES = new LevelGeneratorType(2, "largeBiomes");
	public static final LevelGeneratorType AMPLIFIED = new LevelGeneratorType(3, "amplified").setHasInfo();
	public static final LevelGeneratorType CUSTOMIZED = new LevelGeneratorType(4, "customized", "normal", 0).method_16400(true).setVisible(false);
	public static final LevelGeneratorType field_17505 = new LevelGeneratorType(5, "buffet").method_16400(true);
	public static final LevelGeneratorType DEBUG = new LevelGeneratorType(6, "debug_all_block_states");
	public static final LevelGeneratorType DEFAULT_1_1 = new LevelGeneratorType(8, "default_1_1", 0).setVisible(false);
	private final int id;
	private final String name;
	private final String field_17506;
	private final int version;
	private boolean visible;
	private boolean versioned;
	private boolean info;
	private boolean field_17507;

	private LevelGeneratorType(int i, String string) {
		this(i, string, string, 0);
	}

	private LevelGeneratorType(int i, String string, int j) {
		this(i, string, string, j);
	}

	private LevelGeneratorType(int i, String string, String string2, int j) {
		this.name = string;
		this.field_17506 = string2;
		this.version = j;
		this.visible = true;
		this.id = i;
		TYPES[i] = this;
	}

	public String getName() {
		return this.name;
	}

	public String method_16401() {
		return this.field_17506;
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

	public boolean method_16402() {
		return this.field_17507;
	}

	public LevelGeneratorType method_16400(boolean bl) {
		this.field_17507 = bl;
		return this;
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
		for (LevelGeneratorType levelGeneratorType : TYPES) {
			if (levelGeneratorType != null && levelGeneratorType.name.equalsIgnoreCase(name)) {
				return levelGeneratorType;
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
