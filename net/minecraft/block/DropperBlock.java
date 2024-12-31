package net.minecraft.block;

import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DropperBlock extends DispenserBlock {
	private final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

	@Override
	protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
		return this.BEHAVIOR;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new DropperBlockEntity();
	}

	@Override
	protected void dispense(World world, BlockPos pos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		if (dispenserBlockEntity != null) {
			int i = dispenserBlockEntity.chooseNonEmptySlot();
			if (i < 0) {
				world.syncGlobalEvent(1001, pos, 0);
			} else {
				ItemStack itemStack = dispenserBlockEntity.getInvStack(i);
				if (itemStack != null) {
					Direction direction = world.getBlockState(pos).get(FACING);
					BlockPos blockPos = pos.offset(direction);
					Inventory inventory = HopperBlockEntity.getInventoryAt(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
					ItemStack itemStack2;
					if (inventory == null) {
						itemStack2 = this.BEHAVIOR.dispense(blockPointerImpl, itemStack);
						if (itemStack2 != null && itemStack2.count <= 0) {
							itemStack2 = null;
						}
					} else {
						itemStack2 = HopperBlockEntity.transfer(inventory, itemStack.copy().split(1), direction.getOpposite());
						if (itemStack2 == null) {
							itemStack2 = itemStack.copy();
							if (--itemStack2.count <= 0) {
								itemStack2 = null;
							}
						} else {
							itemStack2 = itemStack.copy();
						}
					}

					dispenserBlockEntity.setInvStack(i, itemStack2);
				}
			}
		}
	}
}
