package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class StoneSlabItem extends BlockItem {
	private final SlabBlock singleSlab;
	private final SlabBlock doubleSlab;

	public StoneSlabItem(Block block, SlabBlock slabBlock, SlabBlock slabBlock2) {
		super(block);
		this.singleSlab = slabBlock;
		this.doubleSlab = slabBlock2;
		this.setMaxDamage(0);
		this.setUnbreakable(true);
	}

	@Override
	public int getMeta(int i) {
		return i;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return this.singleSlab.getVariantTranslationKey(stack.getData());
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (itemStack.count == 0) {
			return false;
		} else if (!player.canModify(pos.offset(direction), direction, itemStack)) {
			return false;
		} else {
			Object object = this.singleSlab.getSlabType(itemStack);
			BlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() == this.singleSlab) {
				Property property = this.singleSlab.getSlabProperty();
				Comparable comparable = blockState.get(property);
				SlabBlock.SlabType slabType = blockState.get(SlabBlock.HALF);
				if ((direction == Direction.UP && slabType == SlabBlock.SlabType.BOTTOM || direction == Direction.DOWN && slabType == SlabBlock.SlabType.TOP)
					&& comparable == object) {
					BlockState blockState2 = this.doubleSlab.getDefaultState().with(property, comparable);
					if (world.hasEntityIn(this.doubleSlab.getCollisionBox(world, pos, blockState2)) && world.setBlockState(pos, blockState2, 3)) {
						world.playSound(
							(double)((float)pos.getX() + 0.5F),
							(double)((float)pos.getY() + 0.5F),
							(double)((float)pos.getZ() + 0.5F),
							this.doubleSlab.sound.getSound(),
							(this.doubleSlab.sound.getVolume() + 1.0F) / 2.0F,
							this.doubleSlab.sound.getPitch() * 0.8F
						);
						itemStack.count--;
					}

					return true;
				}
			}

			return this.doubleSlab(itemStack, world, pos.offset(direction), object)
				? true
				: super.use(itemStack, player, world, pos, direction, facingX, facingY, facingZ);
		}
	}

	@Override
	public boolean canPlaceItemBlock(World world, BlockPos pos, Direction dir, PlayerEntity player, ItemStack stack) {
		Property property = this.singleSlab.getSlabProperty();
		Object object = this.singleSlab.getSlabType(stack);
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == this.singleSlab) {
			boolean bl = blockState.get(SlabBlock.HALF) == SlabBlock.SlabType.TOP;
			if ((dir == Direction.UP && !bl || dir == Direction.DOWN && bl) && object == blockState.get(property)) {
				return true;
			}
		}

		BlockPos var11 = pos.offset(dir);
		BlockState blockState2 = world.getBlockState(var11);
		return blockState2.getBlock() == this.singleSlab && object == blockState2.get(property) ? true : super.canPlaceItemBlock(world, pos, dir, player, stack);
	}

	private boolean doubleSlab(ItemStack stack, World world, BlockPos pos, Object slabProperty) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == this.singleSlab) {
			Comparable comparable = blockState.get((Property<Comparable>)this.singleSlab.getSlabProperty());
			if (comparable == slabProperty) {
				BlockState blockState2 = this.doubleSlab.getDefaultState().with(this.singleSlab.getSlabProperty(), comparable);
				if (world.hasEntityIn(this.doubleSlab.getCollisionBox(world, pos, blockState2)) && world.setBlockState(pos, blockState2, 3)) {
					world.playSound(
						(double)((float)pos.getX() + 0.5F),
						(double)((float)pos.getY() + 0.5F),
						(double)((float)pos.getZ() + 0.5F),
						this.doubleSlab.sound.getSound(),
						(this.doubleSlab.sound.getVolume() + 1.0F) / 2.0F,
						this.doubleSlab.sound.getPitch() * 0.8F
					);
					stack.count--;
				}

				return true;
			}
		}

		return false;
	}
}
