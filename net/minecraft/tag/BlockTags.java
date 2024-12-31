package net.minecraft.tag;

import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class BlockTags {
	private static TagContainer<Block> container = new TagContainer<>(identifier -> false, identifier -> null, "", false, "");
	private static int newestVersion;
	public static final Tag<Block> WOOL = of("wool");
	public static final Tag<Block> PLANKS = of("planks");
	public static final Tag<Block> STONE_BRICKS = of("stone_bricks");
	public static final Tag<Block> WOODEN_BUTTONS = of("wooden_buttons");
	public static final Tag<Block> BUTTONS = of("buttons");
	public static final Tag<Block> CARPETS = of("carpets");
	public static final Tag<Block> WOODEN_DOORS = of("wooden_doors");
	public static final Tag<Block> WOODEN_STAIRS = of("wooden_stairs");
	public static final Tag<Block> WOODEN_SLABS = of("wooden_slabs");
	public static final Tag<Block> WOODEN_PRESSURE_PLATES = of("wooden_pressure_plates");
	public static final Tag<Block> WOODEN_TRAPDOORS = of("wooden_trapdoors");
	public static final Tag<Block> DOORS = of("doors");
	public static final Tag<Block> SAPLINGS = of("saplings");
	public static final Tag<Block> LOGS = of("logs");
	public static final Tag<Block> DARK_OAK_LOGS = of("dark_oak_logs");
	public static final Tag<Block> OAK_LOGS = of("oak_logs");
	public static final Tag<Block> BIRCH_LOGS = of("birch_logs");
	public static final Tag<Block> ACACIA_LOGS = of("acacia_logs");
	public static final Tag<Block> JUNGLE_LOGS = of("jungle_logs");
	public static final Tag<Block> SPRUCE_LOGS = of("spruce_logs");
	public static final Tag<Block> BANNERS = of("banners");
	public static final Tag<Block> SAND = of("sand");
	public static final Tag<Block> STAIRS = of("stairs");
	public static final Tag<Block> SLABS = of("slabs");
	public static final Tag<Block> ANVIL = of("anvil");
	public static final Tag<Block> RAILS = of("rails");
	public static final Tag<Block> LEAVES = of("leaves");
	public static final Tag<Block> TRAPDOORS = of("trapdoors");
	public static final Tag<Block> FLOWER_POTS = of("flower_pots");
	public static final Tag<Block> ENDERMAN_HOLDABLE = of("enderman_holdable");
	public static final Tag<Block> ICE = of("ice");
	public static final Tag<Block> VALID_SPAWN = of("valid_spawn");
	public static final Tag<Block> IMPERMEABLE = of("impermeable");
	public static final Tag<Block> UNDERWATER_BONEMEALS = of("underwater_bonemeals");
	public static final Tag<Block> CORAL_BLOCKS = of("coral_blocks");
	public static final Tag<Block> WALL_CORALS = of("wall_corals");
	public static final Tag<Block> CORAL_PLANTS = of("coral_plants");
	public static final Tag<Block> CORALS = of("corals");

	public static void setContainer(TagContainer<Block> container) {
		BlockTags.container = container;
		newestVersion++;
	}

	public static TagContainer<Block> getContainer() {
		return container;
	}

	private static Tag<Block> of(String id) {
		return new BlockTags.BlockTag(new Identifier(id));
	}

	static class BlockTag extends Tag<Block> {
		private int version = -1;
		private Tag<Block> tag;

		public BlockTag(Identifier identifier) {
			super(identifier);
		}

		public boolean contains(Block block) {
			if (this.version != BlockTags.newestVersion) {
				this.tag = BlockTags.container.getOrCreate(this.getId());
				this.version = BlockTags.newestVersion;
			}

			return this.tag.contains(block);
		}

		@Override
		public Collection<Block> values() {
			if (this.version != BlockTags.newestVersion) {
				this.tag = BlockTags.container.getOrCreate(this.getId());
				this.version = BlockTags.newestVersion;
			}

			return this.tag.values();
		}

		@Override
		public Collection<Tag.Entry<Block>> entries() {
			if (this.version != BlockTags.newestVersion) {
				this.tag = BlockTags.container.getOrCreate(this.getId());
				this.version = BlockTags.newestVersion;
			}

			return this.tag.entries();
		}
	}
}
