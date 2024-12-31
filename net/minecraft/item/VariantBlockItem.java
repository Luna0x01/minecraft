package net.minecraft.item;

import net.minecraft.block.Block;

public class VariantBlockItem extends BlockItem {
	protected final Block variantBlock;
	protected final VariantBlockItem.class_3057 field_15114;

	public VariantBlockItem(Block block, Block block2, VariantBlockItem.class_3057 arg) {
		super(block);
		this.variantBlock = block2;
		this.field_15114 = arg;
		this.setMaxDamage(0);
		this.setUnbreakable(true);
	}

	public VariantBlockItem(Block block, Block block2, String[] strings) {
		this(block, block2, new VariantBlockItem.class_3057() {
			@Override
			public String method_8437(ItemStack itemStack) {
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
		return super.getTranslationKey() + "." + this.field_15114.method_8437(stack);
	}

	public interface class_3057 {
		String method_8437(ItemStack itemStack);
	}
}
