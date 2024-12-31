package net.minecraft;

import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4369 extends class_4370<Item> {
	private static final Logger field_21480 = LogManager.getLogger();

	public class_4369(class_4344 arg) {
		super(arg, Registry.ITEM);
	}

	@Override
	protected void method_20081() {
		this.method_20077(BlockTags.WOOL, ItemTags.WOOL);
		this.method_20077(BlockTags.PLANKS, ItemTags.PLANKS);
		this.method_20077(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
		this.method_20077(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		this.method_20077(BlockTags.BUTTONS, ItemTags.BUTTONS);
		this.method_20077(BlockTags.CARPETS, ItemTags.CARPETS);
		this.method_20077(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		this.method_20077(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		this.method_20077(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		this.method_20077(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
		this.method_20077(BlockTags.DOORS, ItemTags.DOORS);
		this.method_20077(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		this.method_20077(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
		this.method_20077(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
		this.method_20077(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
		this.method_20077(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
		this.method_20077(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
		this.method_20077(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
		this.method_20077(BlockTags.LOGS, ItemTags.LOGS);
		this.method_20077(BlockTags.SAND, ItemTags.SAND);
		this.method_20077(BlockTags.SLABS, ItemTags.SLABS);
		this.method_20077(BlockTags.STAIRS, ItemTags.STAIRS);
		this.method_20077(BlockTags.ANVIL, ItemTags.ANVIL);
		this.method_20077(BlockTags.RAILS, ItemTags.RAILS);
		this.method_20077(BlockTags.LEAVES, ItemTags.LEAVES);
		this.method_20077(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
		this.method_20077(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
		this.method_20079(ItemTags.BANNERS)
			.add(
				Items.WHITE_BANNER,
				Items.ORANGE_BANNER,
				Items.MAGENTA_BANNER,
				Items.LIGHT_BLUE_BANNER,
				Items.YELLOW_BANNER,
				Items.LIME_BANNER,
				Items.PINK_BANNER,
				Items.GRAY_BANNER,
				Items.LIGHT_GRAY_BANNER,
				Items.CYAN_BANNER,
				Items.PURPLE_BANNER,
				Items.BLUE_BANNER,
				Items.BROWN_BANNER,
				Items.GREEN_BANNER,
				Items.RED_BANNER,
				Items.BLACK_BANNER
			);
		this.method_20079(ItemTags.BOATS).add(Items.BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT);
		this.method_20079(ItemTags.FISHES).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
	}

	protected void method_20077(Tag<Block> tag, Tag<Item> tag2) {
		Tag.Builder<Item> builder = this.method_20079(tag2);

		for (Tag.Entry<Block> entry : tag.entries()) {
			Tag.Entry<Item> entry2 = this.method_20076(entry);
			builder.add(entry2);
		}
	}

	private Tag.Entry<Item> method_20076(Tag.Entry<Block> entry) {
		if (entry instanceof Tag.TagEntry) {
			return new Tag.TagEntry<>(((Tag.TagEntry)entry).getId());
		} else if (entry instanceof Tag.CollectionEntry) {
			List<Item> list = Lists.newArrayList();

			for (Block block : ((Tag.CollectionEntry)entry).getValues()) {
				Item item = block.getItem();
				if (item == Items.AIR) {
					field_21480.warn("Itemless block copied to item tag: {}", Registry.BLOCK.getId(block));
				} else {
					list.add(item);
				}
			}

			return new Tag.CollectionEntry<>(list);
		} else {
			throw new UnsupportedOperationException("Unknown tag entry " + entry);
		}
	}

	@Override
	protected Path method_20078(Identifier identifier) {
		return this.field_21481.method_19993().resolve("data/" + identifier.getNamespace() + "/tags/items/" + identifier.getPath() + ".json");
	}

	@Override
	public String method_19995() {
		return "Item Tags";
	}

	@Override
	protected void method_20080(TagContainer<Item> tagContainer) {
		ItemTags.method_21454(tagContainer);
	}
}
