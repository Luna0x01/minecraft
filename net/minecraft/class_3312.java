package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.GameMode;

public class class_3312 implements class_3318 {
	private static final Set<Block> field_16199 = Sets.newHashSet(
		new Block[]{
			Blocks.OAK_LOG,
			Blocks.SPRUCE_LOG,
			Blocks.BIRCH_LOG,
			Blocks.JUNGLE_LOG,
			Blocks.ACACIA_LOG,
			Blocks.DARK_OAK_LOG,
			Blocks.OAK_WOOD,
			Blocks.SPRUCE_WOOD,
			Blocks.BIRCH_WOOD,
			Blocks.JUNGLE_WOOD,
			Blocks.ACACIA_WOOD,
			Blocks.DARK_OAK_WOOD,
			Blocks.OAK_LEAVES,
			Blocks.SPRUCE_LEAVES,
			Blocks.BIRCH_LEAVES,
			Blocks.JUNGLE_LEAVES,
			Blocks.ACACIA_LEAVES,
			Blocks.DARK_OAK_LEAVES
		}
	);
	private static final Text field_16200 = new TranslatableText("tutorial.find_tree.title");
	private static final Text field_16201 = new TranslatableText("tutorial.find_tree.description");
	private final class_3316 field_16202;
	private class_3266 field_16203;
	private int field_16204;

	public class_3312(class_3316 arg) {
		this.field_16202 = arg;
	}

	@Override
	public void method_14731() {
		this.field_16204++;
		if (this.field_16202.method_14730() != GameMode.SURVIVAL) {
			this.field_16202.method_14724(class_3319.NONE);
		} else {
			if (this.field_16204 == 1) {
				ClientPlayerEntity clientPlayerEntity = this.field_16202.method_14729().player;
				if (clientPlayerEntity != null) {
					for (Block block : field_16199) {
						if (clientPlayerEntity.inventory.contains(new ItemStack(block))) {
							this.field_16202.method_14724(class_3319.CRAFT_PLANKS);
							return;
						}
					}

					if (method_14717(clientPlayerEntity)) {
						this.field_16202.method_14724(class_3319.CRAFT_PLANKS);
						return;
					}
				}
			}

			if (this.field_16204 >= 6000 && this.field_16203 == null) {
				this.field_16203 = new class_3266(class_3266.class_3267.TREE, field_16200, field_16201, false);
				this.field_16202.method_14729().method_14462().method_14491(this.field_16203);
			}
		}
	}

	@Override
	public void method_14737() {
		if (this.field_16203 != null) {
			this.field_16203.method_14498();
			this.field_16203 = null;
		}
	}

	@Override
	public void method_14734(ClientWorld clientWorld, BlockHitResult blockHitResult) {
		if (blockHitResult.type == BlockHitResult.Type.BLOCK && blockHitResult.getBlockPos() != null) {
			BlockState blockState = clientWorld.getBlockState(blockHitResult.getBlockPos());
			if (field_16199.contains(blockState.getBlock())) {
				this.field_16202.method_14724(class_3319.PUNCH_TREE);
			}
		}
	}

	@Override
	public void method_14732(ItemStack itemStack) {
		for (Block block : field_16199) {
			if (itemStack.getItem() == block.getItem()) {
				this.field_16202.method_14724(class_3319.CRAFT_PLANKS);
				return;
			}
		}
	}

	public static boolean method_14717(ClientPlayerEntity clientPlayerEntity) {
		for (Block block : field_16199) {
			if (clientPlayerEntity.getStatHandler().method_21434(Stats.MINED.method_21429(block)) > 0) {
				return true;
			}
		}

		return false;
	}
}
