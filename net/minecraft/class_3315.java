package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public class class_3315 implements class_3318 {
	private static final Set<Block> field_16224 = Sets.newHashSet(new Block[]{Blocks.LOG, Blocks.LOG2});
	private static final Text field_16225 = new TranslatableText("tutorial.punch_tree.title");
	private static final Text field_16226 = new TranslatableText("tutorial.punch_tree.description", class_3316.method_14725("attack"));
	private final class_3316 field_16227;
	private class_3266 field_16228;
	private int field_16229;
	private int field_16230;

	public class_3315(class_3316 arg) {
		this.field_16227 = arg;
	}

	@Override
	public void method_14731() {
		this.field_16229++;
		if (this.field_16227.method_14730() != GameMode.SURVIVAL) {
			this.field_16227.method_14724(class_3319.NONE);
		} else {
			if (this.field_16229 == 1) {
				ClientPlayerEntity clientPlayerEntity = this.field_16227.method_14729().player;
				if (clientPlayerEntity != null) {
					for (Block block : field_16224) {
						if (clientPlayerEntity.inventory.contains(new ItemStack(block))) {
							this.field_16227.method_14724(class_3319.CRAFT_PLANKS);
							return;
						}
					}

					if (class_3312.method_14717(clientPlayerEntity)) {
						this.field_16227.method_14724(class_3319.CRAFT_PLANKS);
						return;
					}
				}
			}

			if ((this.field_16229 >= 600 || this.field_16230 > 3) && this.field_16228 == null) {
				this.field_16228 = new class_3266(class_3266.class_3267.TREE, field_16225, field_16226, true);
				this.field_16227.method_14729().method_14462().method_14491(this.field_16228);
			}
		}
	}

	@Override
	public void method_14737() {
		if (this.field_16228 != null) {
			this.field_16228.method_14498();
			this.field_16228 = null;
		}
	}

	@Override
	public void method_14735(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
		boolean bl = field_16224.contains(blockState.getBlock());
		if (bl && f > 0.0F) {
			if (this.field_16228 != null) {
				this.field_16228.method_14499(f);
			}

			if (f >= 1.0F) {
				this.field_16227.method_14724(class_3319.OPEN_INVENTORY);
			}
		} else if (this.field_16228 != null) {
			this.field_16228.method_14499(0.0F);
		} else if (bl) {
			this.field_16230++;
		}
	}

	@Override
	public void method_14732(ItemStack itemStack) {
		for (Block block : field_16224) {
			if (itemStack.getItem() == Item.fromBlock(block)) {
				this.field_16227.method_14724(class_3319.CRAFT_PLANKS);
				return;
			}
		}
	}
}
