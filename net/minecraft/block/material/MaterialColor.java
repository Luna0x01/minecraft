package net.minecraft.block.material;

public class MaterialColor {
	public static final MaterialColor[] COLORS = new MaterialColor[64];
	public static final MaterialColor AIR = new MaterialColor(0, 0);
	public static final MaterialColor GRASS = new MaterialColor(1, 8368696);
	public static final MaterialColor SAND = new MaterialColor(2, 16247203);
	public static final MaterialColor WEB = new MaterialColor(3, 13092807);
	public static final MaterialColor LAVA = new MaterialColor(4, 16711680);
	public static final MaterialColor ICE = new MaterialColor(5, 10526975);
	public static final MaterialColor IRON = new MaterialColor(6, 10987431);
	public static final MaterialColor FOLIAGE = new MaterialColor(7, 31744);
	public static final MaterialColor WHITE = new MaterialColor(8, 16777215);
	public static final MaterialColor CLAY = new MaterialColor(9, 10791096);
	public static final MaterialColor DIRT = new MaterialColor(10, 9923917);
	public static final MaterialColor STONE = new MaterialColor(11, 7368816);
	public static final MaterialColor WATER = new MaterialColor(12, 4210943);
	public static final MaterialColor WOOD = new MaterialColor(13, 9402184);
	public static final MaterialColor QUARTZ = new MaterialColor(14, 16776437);
	public static final MaterialColor ORANGE = new MaterialColor(15, 14188339);
	public static final MaterialColor MAGENTA = new MaterialColor(16, 11685080);
	public static final MaterialColor LIGHT_BLUE = new MaterialColor(17, 6724056);
	public static final MaterialColor YELLOW = new MaterialColor(18, 15066419);
	public static final MaterialColor LIME = new MaterialColor(19, 8375321);
	public static final MaterialColor PINK = new MaterialColor(20, 15892389);
	public static final MaterialColor GRAY = new MaterialColor(21, 5000268);
	public static final MaterialColor LIGHT_GRAY = new MaterialColor(22, 10066329);
	public static final MaterialColor CYAN = new MaterialColor(23, 5013401);
	public static final MaterialColor PURPLE = new MaterialColor(24, 8339378);
	public static final MaterialColor BLUE = new MaterialColor(25, 3361970);
	public static final MaterialColor BROWN = new MaterialColor(26, 6704179);
	public static final MaterialColor GREEN = new MaterialColor(27, 6717235);
	public static final MaterialColor RED = new MaterialColor(28, 10040115);
	public static final MaterialColor BLACK = new MaterialColor(29, 1644825);
	public static final MaterialColor GOLD = new MaterialColor(30, 16445005);
	public static final MaterialColor DIAMOND = new MaterialColor(31, 6085589);
	public static final MaterialColor LAPIS = new MaterialColor(32, 4882687);
	public static final MaterialColor EMERALD = new MaterialColor(33, 55610);
	public static final MaterialColor SPRUCE = new MaterialColor(34, 8476209);
	public static final MaterialColor NETHER = new MaterialColor(35, 7340544);
	public final int color;
	public final int id;

	private MaterialColor(int i, int j) {
		if (i >= 0 && i <= 63) {
			this.id = i;
			this.color = j;
			COLORS[i] = this;
		} else {
			throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
		}
	}

	public int getRenderColor(int shade) {
		int i = 220;
		if (shade == 3) {
			i = 135;
		}

		if (shade == 2) {
			i = 255;
		}

		if (shade == 1) {
			i = 220;
		}

		if (shade == 0) {
			i = 180;
		}

		int j = (this.color >> 16 & 0xFF) * i / 255;
		int k = (this.color >> 8 & 0xFF) * i / 255;
		int l = (this.color & 0xFF) * i / 255;
		return 0xFF000000 | j << 16 | k << 8 | l;
	}
}
