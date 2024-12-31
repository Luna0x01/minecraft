package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;

public final class Material {
	public static final Material AIR = new Material.Builder(MaterialColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
	public static final Material STRUCTURE_VOID = new Material.Builder(MaterialColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
	public static final Material PORTAL = new Material.Builder(MaterialColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().blocksPistons().build();
	public static final Material CARPET = new Material.Builder(MaterialColor.WEB).allowsMovement().lightPassesThrough().notSolid().burnable().build();
	public static final Material PLANT = new Material.Builder(MaterialColor.FOLIAGE).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
	public static final Material UNDERWATER_PLANT = new Material.Builder(MaterialColor.WATER)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.build();
	public static final Material REPLACEABLE_PLANT = new Material.Builder(MaterialColor.FOLIAGE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.burnable()
		.build();
	public static final Material NETHER_SHOOTS = new Material.Builder(MaterialColor.FOLIAGE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material REPLACEABLE_UNDERWATER_PLANT = new Material.Builder(MaterialColor.WATER)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material WATER = new Material.Builder(MaterialColor.WATER)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.liquid()
		.build();
	public static final Material BUBBLE_COLUMN = new Material.Builder(MaterialColor.WATER)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.liquid()
		.build();
	public static final Material LAVA = new Material.Builder(MaterialColor.LAVA)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.liquid()
		.build();
	public static final Material SNOW_LAYER = new Material.Builder(MaterialColor.WHITE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material FIRE = new Material.Builder(MaterialColor.CLEAR)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material SUPPORTED = new Material.Builder(MaterialColor.CLEAR)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.build();
	public static final Material COBWEB = new Material.Builder(MaterialColor.WEB).allowsMovement().lightPassesThrough().destroyedByPiston().build();
	public static final Material REDSTONE_LAMP = new Material.Builder(MaterialColor.CLEAR).build();
	public static final Material ORGANIC_PRODUCT = new Material.Builder(MaterialColor.CLAY).build();
	public static final Material SOIL = new Material.Builder(MaterialColor.DIRT).build();
	public static final Material SOLID_ORGANIC = new Material.Builder(MaterialColor.GRASS).build();
	public static final Material DENSE_ICE = new Material.Builder(MaterialColor.ICE).build();
	public static final Material AGGREGATE = new Material.Builder(MaterialColor.SAND).build();
	public static final Material SPONGE = new Material.Builder(MaterialColor.YELLOW).build();
	public static final Material SHULKER_BOX = new Material.Builder(MaterialColor.PURPLE).build();
	public static final Material WOOD = new Material.Builder(MaterialColor.WOOD).burnable().build();
	public static final Material NETHER_WOOD = new Material.Builder(MaterialColor.WOOD).build();
	public static final Material BAMBOO_SAPLING = new Material.Builder(MaterialColor.WOOD).burnable().destroyedByPiston().allowsMovement().build();
	public static final Material BAMBOO = new Material.Builder(MaterialColor.WOOD).burnable().destroyedByPiston().build();
	public static final Material WOOL = new Material.Builder(MaterialColor.WEB).burnable().build();
	public static final Material TNT = new Material.Builder(MaterialColor.LAVA).burnable().lightPassesThrough().build();
	public static final Material LEAVES = new Material.Builder(MaterialColor.FOLIAGE).burnable().lightPassesThrough().destroyedByPiston().build();
	public static final Material GLASS = new Material.Builder(MaterialColor.CLEAR).lightPassesThrough().build();
	public static final Material ICE = new Material.Builder(MaterialColor.ICE).lightPassesThrough().build();
	public static final Material CACTUS = new Material.Builder(MaterialColor.FOLIAGE).lightPassesThrough().destroyedByPiston().build();
	public static final Material STONE = new Material.Builder(MaterialColor.STONE).build();
	public static final Material METAL = new Material.Builder(MaterialColor.IRON).build();
	public static final Material SNOW_BLOCK = new Material.Builder(MaterialColor.WHITE).build();
	public static final Material REPAIR_STATION = new Material.Builder(MaterialColor.IRON).blocksPistons().build();
	public static final Material BARRIER = new Material.Builder(MaterialColor.CLEAR).blocksPistons().build();
	public static final Material PISTON = new Material.Builder(MaterialColor.STONE).blocksPistons().build();
	public static final Material UNUSED_PLANT = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material GOURD = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material EGG = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material CAKE = new Material.Builder(MaterialColor.CLEAR).destroyedByPiston().build();
	private final MaterialColor color;
	private final PistonBehavior pistonBehavior;
	private final boolean blocksMovement;
	private final boolean burnable;
	private final boolean liquid;
	private final boolean blocksLight;
	private final boolean replaceable;
	private final boolean solid;

	public Material(
		MaterialColor color,
		boolean liquid,
		boolean solid,
		boolean blocksMovement,
		boolean blocksLight,
		boolean breakByHand,
		boolean burnable,
		PistonBehavior pistonBehavior
	) {
		this.color = color;
		this.liquid = liquid;
		this.solid = solid;
		this.blocksMovement = blocksMovement;
		this.blocksLight = blocksLight;
		this.burnable = breakByHand;
		this.replaceable = burnable;
		this.pistonBehavior = pistonBehavior;
	}

	public boolean isLiquid() {
		return this.liquid;
	}

	public boolean isSolid() {
		return this.solid;
	}

	public boolean blocksMovement() {
		return this.blocksMovement;
	}

	public boolean isBurnable() {
		return this.burnable;
	}

	public boolean isReplaceable() {
		return this.replaceable;
	}

	public boolean blocksLight() {
		return this.blocksLight;
	}

	public PistonBehavior getPistonBehavior() {
		return this.pistonBehavior;
	}

	public MaterialColor getColor() {
		return this.color;
	}

	public static class Builder {
		private PistonBehavior pistonBehavior = PistonBehavior.NORMAL;
		private boolean blocksMovement = true;
		private boolean burnable;
		private boolean liquid;
		private boolean replaceable;
		private boolean solid = true;
		private final MaterialColor color;
		private boolean blocksLight = true;

		public Builder(MaterialColor color) {
			this.color = color;
		}

		public Material.Builder liquid() {
			this.liquid = true;
			return this;
		}

		public Material.Builder notSolid() {
			this.solid = false;
			return this;
		}

		public Material.Builder allowsMovement() {
			this.blocksMovement = false;
			return this;
		}

		private Material.Builder lightPassesThrough() {
			this.blocksLight = false;
			return this;
		}

		protected Material.Builder burnable() {
			this.burnable = true;
			return this;
		}

		public Material.Builder replaceable() {
			this.replaceable = true;
			return this;
		}

		protected Material.Builder destroyedByPiston() {
			this.pistonBehavior = PistonBehavior.DESTROY;
			return this;
		}

		protected Material.Builder blocksPistons() {
			this.pistonBehavior = PistonBehavior.BLOCK;
			return this;
		}

		public Material build() {
			return new Material(this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.burnable, this.replaceable, this.pistonBehavior);
		}
	}
}
