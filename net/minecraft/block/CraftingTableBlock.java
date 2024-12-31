package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CraftingTableBlock extends Block {
	protected CraftingTableBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			player.openHandledScreen(new CraftingTableBlock.ClientDummyScreenHandlerProvider(world, pos));
			player.method_15928(Stats.INTERACT_WITH_CRAFTING_TABLE);
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
		public Text method_15540() {
			return new TranslatableText(Blocks.CRAFTING_TABLE.getTranslationKey() + ".name");
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Nullable
		@Override
		public Text method_15541() {
			return null;
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
