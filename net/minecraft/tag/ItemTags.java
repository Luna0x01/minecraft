package net.minecraft.tag;

import java.util.Collection;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemTags {
	private static TagContainer<Item> field_22194 = new TagContainer<>(identifier -> false, identifier -> null, "", false, "");
	private static int field_22195;
	public static final Tag<Item> WOOL = method_21452("wool");
	public static final Tag<Item> PLANKS = method_21452("planks");
	public static final Tag<Item> STONE_BRICKS = method_21452("stone_bricks");
	public static final Tag<Item> WOODEN_BUTTONS = method_21452("wooden_buttons");
	public static final Tag<Item> BUTTONS = method_21452("buttons");
	public static final Tag<Item> CARPETS = method_21452("carpets");
	public static final Tag<Item> WOODEN_DOORS = method_21452("wooden_doors");
	public static final Tag<Item> WOODEN_STAIRS = method_21452("wooden_stairs");
	public static final Tag<Item> WOODEN_SLABS = method_21452("wooden_slabs");
	public static final Tag<Item> WOODEN_PRESSURE_PLATES = method_21452("wooden_pressure_plates");
	public static final Tag<Item> WOODEN_TRAPDOORS = method_21452("wooden_trapdoors");
	public static final Tag<Item> DOORS = method_21452("doors");
	public static final Tag<Item> SAPLINGS = method_21452("saplings");
	public static final Tag<Item> LOGS = method_21452("logs");
	public static final Tag<Item> DARK_OAK_LOGS = method_21452("dark_oak_logs");
	public static final Tag<Item> OAK_LOGS = method_21452("oak_logs");
	public static final Tag<Item> BIRCH_LOGS = method_21452("birch_logs");
	public static final Tag<Item> ACACIA_LOGS = method_21452("acacia_logs");
	public static final Tag<Item> JUNGLE_LOGS = method_21452("jungle_logs");
	public static final Tag<Item> SPRUCE_LOGS = method_21452("spruce_logs");
	public static final Tag<Item> BANNERS = method_21452("banners");
	public static final Tag<Item> SAND = method_21452("sand");
	public static final Tag<Item> STAIRS = method_21452("stairs");
	public static final Tag<Item> SLABS = method_21452("slabs");
	public static final Tag<Item> ANVIL = method_21452("anvil");
	public static final Tag<Item> RAILS = method_21452("rails");
	public static final Tag<Item> LEAVES = method_21452("leaves");
	public static final Tag<Item> TRAPDOORS = method_21452("trapdoors");
	public static final Tag<Item> BOATS = method_21452("boats");
	public static final Tag<Item> FISHES = method_21452("fishes");

	public static void method_21454(TagContainer<Item> tagContainer) {
		field_22194 = tagContainer;
		field_22195++;
	}

	public static TagContainer<Item> method_21451() {
		return field_22194;
	}

	private static Tag<Item> method_21452(String string) {
		return new ItemTags.class_4480(new Identifier(string));
	}

	public static class class_4480 extends Tag<Item> {
		private int field_22222 = -1;
		private Tag<Item> field_22223;

		public class_4480(Identifier identifier) {
			super(identifier);
		}

		public boolean contains(Item item) {
			if (this.field_22222 != ItemTags.field_22195) {
				this.field_22223 = ItemTags.field_22194.getOrCreate(this.getId());
				this.field_22222 = ItemTags.field_22195;
			}

			return this.field_22223.contains(item);
		}

		@Override
		public Collection<Item> values() {
			if (this.field_22222 != ItemTags.field_22195) {
				this.field_22223 = ItemTags.field_22194.getOrCreate(this.getId());
				this.field_22222 = ItemTags.field_22195;
			}

			return this.field_22223.values();
		}

		@Override
		public Collection<Tag.Entry<Item>> entries() {
			if (this.field_22222 != ItemTags.field_22195) {
				this.field_22223 = ItemTags.field_22194.getOrCreate(this.getId());
				this.field_22222 = ItemTags.field_22195;
			}

			return this.field_22223.entries();
		}
	}
}
