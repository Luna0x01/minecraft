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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DropperBlock extends DispenserBlock {
	private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

	public DropperBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
		return BEHAVIOR;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new DropperBlockEntity();
	}

	@Override
	protected void dispense(World world, BlockPos pos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		int i = dispenserBlockEntity.chooseNonEmptySlot();
		if (i < 0) {
			world.syncGlobalEvent(1001, pos, 0);
		} else {
			ItemStack itemStack = dispenserBlockEntity.getInvStack(i);
			if (!itemStack.isEmpty()) {
				Direction direction = world.getBlockState(pos).getProperty(FACING);
				Inventory inventory = HopperBlockEntity.method_16823(world, pos.offset(direction));
				ItemStack itemStack2;
				if (inventory == null) {
					itemStack2 = BEHAVIOR.dispense(blockPointerImpl, itemStack);
				} else {
					itemStack2 = HopperBlockEntity.method_13727(dispenserBlockEntity, inventory, itemStack.copy().split(1), direction.getOpposite());
					if (itemStack2.isEmpty()) {
						itemStack2 = itemStack.copy();
						itemStack2.decrement(1);
					} else {
						itemStack2 = itemStack.copy();
					}
				}

				dispenserBlockEntity.setInvStack(i, itemStack2);
			}
		}
	}
}
