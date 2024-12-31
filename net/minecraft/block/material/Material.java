package net.minecraft.block.material;

import net.minecraft.block.piston.PistonBehavior;

public class Material {
	public static final Material AIR = new AirMaterial(MaterialColor.AIR);
	public static final Material GRASS = new Material(MaterialColor.GRASS);
	public static final Material DIRT = new Material(MaterialColor.DIRT);
	public static final Material WOOD = new Material(MaterialColor.WOOD).setFlammable();
	public static final Material STONE = new Material(MaterialColor.STONE).requiresTool();
	public static final Material IRON = new Material(MaterialColor.IRON).requiresTool();
	public static final Material ANVIL = new Material(MaterialColor.IRON).requiresTool().setImmovable();
	public static final Material WATER = new FluidMaterial(MaterialColor.WATER).setNoPushing();
	public static final Material LAVA = new FluidMaterial(MaterialColor.LAVA).setNoPushing();
	public static final Material FOLIAGE = new Material(MaterialColor.FOLIAGE).setFlammable().requiresSilkTouch().setNoPushing();
	public static final Material PLANT = new PlantMaterial(MaterialColor.FOLIAGE).setNoPushing();
	public static final Material REPLACEABLE_PLANT = new PlantMaterial(MaterialColor.FOLIAGE).setFlammable().setNoPushing().setReplaceable();
	public static final Material SPONGE = new Material(MaterialColor.YELLOW);
	public static final Material WOOL = new Material(MaterialColor.WEB).setFlammable();
	public static final Material FIRE = new AirMaterial(MaterialColor.AIR).setNoPushing();
	public static final Material SAND = new Material(MaterialColor.SAND);
	public static final Material DECORATION = new PlantMaterial(MaterialColor.AIR).setNoPushing();
	public static final Material CARPET = new PlantMaterial(MaterialColor.WEB).setFlammable();
	public static final Material GLASS = new Material(MaterialColor.AIR).requiresSilkTouch().setCanBeBrokenInAdventureMode();
	public static final Material REDSTONE_LAMP = new Material(MaterialColor.AIR).setCanBeBrokenInAdventureMode();
	public static final Material TNT = new Material(MaterialColor.LAVA).setFlammable().requiresSilkTouch();
	public static final Material SWORD = new Material(MaterialColor.FOLIAGE).setNoPushing();
	public static final Material ICE = new Material(MaterialColor.ICE).requiresSilkTouch().setCanBeBrokenInAdventureMode();
	public static final Material PACKED_ICE = new Material(MaterialColor.ICE).setCanBeBrokenInAdventureMode();
	public static final Material SNOW_LAYER = new PlantMaterial(MaterialColor.WHITE).setReplaceable().requiresSilkTouch().requiresTool().setNoPushing();
	public static final Material SNOW = new Material(MaterialColor.WHITE).requiresTool();
	public static final Material CACTUS = new Material(MaterialColor.FOLIAGE).requiresSilkTouch().setNoPushing();
	public static final Material CLAY = new Material(MaterialColor.CLAY);
	public static final Material PUMPKIN = new Material(MaterialColor.FOLIAGE).setNoPushing();
	public static final Material EGG = new Material(MaterialColor.FOLIAGE).setNoPushing();
	public static final Material PORTAL = new PortalMaterial(MaterialColor.AIR).setImmovable();
	public static final Material CAKE = new Material(MaterialColor.AIR).setNoPushing();
	public static final Material COBWEB = (new Material(MaterialColor.WEB) {
		@Override
		public boolean blocksMovement() {
			return false;
		}
	}).requiresTool().setNoPushing();
	public static final Material PISTON = new Material(MaterialColor.STONE).setImmovable();
	public static final Material BARRIER = new Material(MaterialColor.AIR).requiresTool().setImmovable();
	private boolean burnable;
	private boolean replaceable;
	private boolean requiresSilkTouch;
	private final MaterialColor color;
	private boolean blocksMovement = true;
	private PistonBehavior pistonBehavior = PistonBehavior.NORMAL;
	private boolean canBeBrokenInAdventureMode;

	public Material(MaterialColor materialColor) {
		this.color = materialColor;
	}

	public boolean isFluid() {
		return false;
	}

	public boolean isSolid() {
		return true;
	}

	public boolean isTranslucent() {
		return true;
	}

	public boolean blocksMovement() {
		return true;
	}

	private Material requiresSilkTouch() {
		this.requiresSilkTouch = true;
		return this;
	}

	protected Material requiresTool() {
		this.blocksMovement = false;
		return this;
	}

	protected Material setFlammable() {
		this.burnable = true;
		return this;
	}

	public boolean isBurnable() {
		return this.burnable;
	}

	public Material setReplaceable() {
		this.replaceable = true;
		return this;
	}

	public boolean isReplaceable() {
		return this.replaceable;
	}

	public boolean isOpaque() {
		return this.requiresSilkTouch ? false : this.blocksMovement();
	}

	public boolean doesBlockMovement() {
		return this.blocksMovement;
	}

	public PistonBehavior getPistonBehavior() {
		return this.pistonBehavior;
	}

	protected Material setNoPushing() {
		this.pistonBehavior = PistonBehavior.DESTROY;
		return this;
	}

	protected Material setImmovable() {
		this.pistonBehavior = PistonBehavior.BLOCK;
		return this;
	}

	protected Material setCanBeBrokenInAdventureMode() {
		this.canBeBrokenInAdventureMode = true;
		return this;
	}

	public MaterialColor getColor() {
		return this.color;
	}
}
