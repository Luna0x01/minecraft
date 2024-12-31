package net.minecraft.block.material;

import net.minecraft.block.piston.PistonBehavior;

public final class Material {
	public static final Material AIR = new Material.class_4031(MaterialColor.AIR).method_17829().method_17836().method_17828().method_17832().method_17835();
	public static final Material CAVE_AIR = new Material.class_4031(MaterialColor.AIR).method_17829().method_17836().method_17828().method_17832().method_17835();
	public static final Material PORTAL = new Material.class_4031(MaterialColor.AIR).method_17829().method_17836().method_17828().method_17834().method_17835();
	public static final Material CARPET = new Material.class_4031(MaterialColor.WEB).method_17829().method_17836().method_17828().method_17831().method_17835();
	public static final Material PLANT = new Material.class_4031(MaterialColor.FOLIAGE).method_17829().method_17836().method_17828().method_17833().method_17835();
	public static final Material field_19498 = new Material.class_4031(MaterialColor.WATER)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17835();
	public static final Material REPLACEABLE_PLANT = new Material.class_4031(MaterialColor.FOLIAGE)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17832()
		.method_17831()
		.method_17835();
	public static final Material field_19499 = new Material.class_4031(MaterialColor.WATER)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17832()
		.method_17835();
	public static final Material WATER = new Material.class_4031(MaterialColor.WATER)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17832()
		.method_17826()
		.method_17835();
	public static final Material field_19500 = new Material.class_4031(MaterialColor.WATER)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17832()
		.method_17826()
		.method_17835();
	public static final Material LAVA = new Material.class_4031(MaterialColor.LAVA)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17832()
		.method_17826()
		.method_17835();
	public static final Material SNOW_LAYER = new Material.class_4031(MaterialColor.WHITE)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17832()
		.method_17830()
		.method_17835();
	public static final Material FIRE = new Material.class_4031(MaterialColor.AIR)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17832()
		.method_17835();
	public static final Material DECORATION = new Material.class_4031(MaterialColor.AIR)
		.method_17829()
		.method_17836()
		.method_17828()
		.method_17833()
		.method_17835();
	public static final Material COBWEB = new Material.class_4031(MaterialColor.WEB).method_17829().method_17836().method_17833().method_17830().method_17835();
	public static final Material REDSTONE_LAMP = new Material.class_4031(MaterialColor.AIR).method_17835();
	public static final Material CLAY = new Material.class_4031(MaterialColor.CLAY).method_17835();
	public static final Material DIRT = new Material.class_4031(MaterialColor.DIRT).method_17835();
	public static final Material GRASS = new Material.class_4031(MaterialColor.GRASS).method_17835();
	public static final Material PACKED_ICE = new Material.class_4031(MaterialColor.ICE).method_17835();
	public static final Material SAND = new Material.class_4031(MaterialColor.SAND).method_17835();
	public static final Material SPONGE = new Material.class_4031(MaterialColor.YELLOW).method_17835();
	public static final Material WOOD = new Material.class_4031(MaterialColor.WOOD).method_17831().method_17835();
	public static final Material WOOL = new Material.class_4031(MaterialColor.WEB).method_17831().method_17835();
	public static final Material TNT = new Material.class_4031(MaterialColor.LAVA).method_17831().method_17836().method_17835();
	public static final Material FOLIAGE = new Material.class_4031(MaterialColor.FOLIAGE).method_17831().method_17836().method_17833().method_17835();
	public static final Material GLASS = new Material.class_4031(MaterialColor.AIR).method_17836().method_17835();
	public static final Material ICE = new Material.class_4031(MaterialColor.ICE).method_17836().method_17835();
	public static final Material CACTUS = new Material.class_4031(MaterialColor.FOLIAGE).method_17836().method_17833().method_17835();
	public static final Material STONE = new Material.class_4031(MaterialColor.STONE).method_17830().method_17835();
	public static final Material IRON = new Material.class_4031(MaterialColor.IRON).method_17830().method_17835();
	public static final Material SNOW = new Material.class_4031(MaterialColor.WHITE).method_17830().method_17835();
	public static final Material ANVIL = new Material.class_4031(MaterialColor.IRON).method_17830().method_17834().method_17835();
	public static final Material BARRIER = new Material.class_4031(MaterialColor.AIR).method_17830().method_17834().method_17835();
	public static final Material PISTON = new Material.class_4031(MaterialColor.STONE).method_17834().method_17835();
	public static final Material SWORD = new Material.class_4031(MaterialColor.FOLIAGE).method_17833().method_17835();
	public static final Material PUMPKIN = new Material.class_4031(MaterialColor.FOLIAGE).method_17833().method_17835();
	public static final Material EGG = new Material.class_4031(MaterialColor.FOLIAGE).method_17833().method_17835();
	public static final Material CAKE = new Material.class_4031(MaterialColor.AIR).method_17833().method_17835();
	private final MaterialColor color;
	private final PistonBehavior pistonBehavior;
	private final boolean field_19494;
	private final boolean burnable;
	private final boolean blocksMovement;
	private final boolean field_19495;
	private final boolean field_19496;
	private final boolean replaceable;
	private final boolean field_19497;

	public Material(
		MaterialColor materialColor, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7, PistonBehavior pistonBehavior
	) {
		this.color = materialColor;
		this.field_19495 = bl;
		this.field_19497 = bl2;
		this.field_19494 = bl3;
		this.field_19496 = bl4;
		this.blocksMovement = bl5;
		this.burnable = bl6;
		this.replaceable = bl7;
		this.pistonBehavior = pistonBehavior;
	}

	public boolean isFluid() {
		return this.field_19495;
	}

	public boolean isSolid() {
		return this.field_19497;
	}

	public boolean blocksMovement() {
		return this.field_19494;
	}

	public boolean isBurnable() {
		return this.burnable;
	}

	public boolean isReplaceable() {
		return this.replaceable;
	}

	public boolean isOpaque() {
		return this.field_19496;
	}

	public boolean doesBlockMovement() {
		return this.blocksMovement;
	}

	public PistonBehavior getPistonBehavior() {
		return this.pistonBehavior;
	}

	public MaterialColor getColor() {
		return this.color;
	}

	public static class class_4031 {
		private PistonBehavior field_19501 = PistonBehavior.NORMAL;
		private boolean field_19502 = true;
		private boolean field_19503;
		private boolean field_19504 = true;
		private boolean field_19505;
		private boolean field_19506;
		private boolean field_19507 = true;
		private final MaterialColor field_19508;
		private boolean field_19509 = true;

		public class_4031(MaterialColor materialColor) {
			this.field_19508 = materialColor;
		}

		public Material.class_4031 method_17826() {
			this.field_19505 = true;
			return this;
		}

		public Material.class_4031 method_17828() {
			this.field_19507 = false;
			return this;
		}

		public Material.class_4031 method_17829() {
			this.field_19502 = false;
			return this;
		}

		private Material.class_4031 method_17836() {
			this.field_19509 = false;
			return this;
		}

		protected Material.class_4031 method_17830() {
			this.field_19504 = false;
			return this;
		}

		protected Material.class_4031 method_17831() {
			this.field_19503 = true;
			return this;
		}

		public Material.class_4031 method_17832() {
			this.field_19506 = true;
			return this;
		}

		protected Material.class_4031 method_17833() {
			this.field_19501 = PistonBehavior.DESTROY;
			return this;
		}

		protected Material.class_4031 method_17834() {
			this.field_19501 = PistonBehavior.BLOCK;
			return this;
		}

		public Material method_17835() {
			return new Material(
				this.field_19508,
				this.field_19505,
				this.field_19507,
				this.field_19502,
				this.field_19509,
				this.field_19504,
				this.field_19503,
				this.field_19506,
				this.field_19501
			);
		}
	}
}
