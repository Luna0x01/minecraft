package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.item.map.MapState;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EmptyMapItem extends NetworkSyncedItem {
	protected EmptyMapItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack2 = new ItemStack(Items.FILLED_MAP, 1, world.getIntState("map"));
		String string = "map_" + itemStack2.getData();
		MapState mapState = new MapState(string);
		world.replaceState(string, mapState);
		mapState.scale = 0;
		mapState.method_9308(playerEntity.x, playerEntity.z, mapState.scale);
		mapState.dimensionId = (byte)world.dimension.getDimensionType().getId();
		mapState.trackingPosition = true;
		mapState.markDirty();
		itemStack.count--;
		if (itemStack.count <= 0) {
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack2);
		} else {
			if (!playerEntity.inventory.insertStack(itemStack2.copy())) {
				playerEntity.dropItem(itemStack2, false);
			}

			playerEntity.incrementStat(Stats.used(this));
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		}
	}
}
