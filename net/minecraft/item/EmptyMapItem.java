package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.item.map.MapState;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class EmptyMapItem extends NetworkSyncedItem {
	protected EmptyMapItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		ItemStack itemStack = new ItemStack(Items.FILLED_MAP, 1, world.getIntState("map"));
		String string = "map_" + itemStack.getData();
		MapState mapState = new MapState(string);
		world.replaceState(string, mapState);
		mapState.scale = 0;
		mapState.method_9308(player.x, player.z, mapState.scale);
		mapState.dimensionId = (byte)world.dimension.getType();
		mapState.markDirty();
		stack.count--;
		if (stack.count <= 0) {
			return itemStack;
		} else {
			if (!player.inventory.insertStack(itemStack.copy())) {
				player.dropItem(itemStack, false);
			}

			player.incrementStat(Stats.USED[Item.getRawId(this)]);
			return stack;
		}
	}
}
