package net.minecraft.item;

import com.google.common.base.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;

public class VariantBlockItem extends BlockItem {
	protected final Block variantBlock;
	protected final Function<ItemStack, String> nameFunction;

	public VariantBlockItem(Block block, Block block2, Function<ItemStack, String> function) {
		super(block);
		this.variantBlock = block2;
		this.nameFunction = function;
		this.setMaxDamage(0);
		this.setUnbreakable(true);
	}

	public VariantBlockItem(Block block, Block block2, String[] strings) {
		this(block, block2, new Function<ItemStack, String>() {
			@Nullable
			public String apply(@Nullable ItemStack itemStack) {
				int i = itemStack.getData();
				if (i < 0 || i >= strings.length) {
					i = 0;
				}

				return strings[i];
			}
		});
	}

	@Override
	public int getMeta(int i) {
		return i;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey() + "." + (String)this.nameFunction.apply(stack);
	}
}
