package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CraftingTableBlock extends Block {
	protected CraftingTableBlock() {
		super(Material.WOOD);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			player.openHandledScreen(new CraftingTableBlock.ClientDummyScreenHandlerProvider(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			return true;
		}
	}

	public static class ClientDummyScreenHandlerProvider implements NamedScreenHandlerFactory {
		private final World world;
		private final BlockPos pos;

		public ClientDummyScreenHandlerProvider(World world, BlockPos blockPos) {
			this.world = world;
			this.pos = blockPos;
		}

		@Override
		public String getTranslationKey() {
			return null;
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public Text getName() {
			return new TranslatableText(Blocks.CRAFTING_TABLE.getTranslationKey() + ".name");
		}

		@Override
		public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
			return new CraftingScreenHandler(inventory, this.world, this.pos);
		}

		@Override
		public String getId() {
			return "minecraft:crafting_table";
		}
	}
}
