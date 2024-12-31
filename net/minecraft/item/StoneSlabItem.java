package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isEmpty() && player.canModify(pos.offset(direction), direction, itemStack)) {
			Comparable<?> comparable = this.singleSlab.method_11615(itemStack);
			BlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() == this.singleSlab) {
				Property<?> property = this.singleSlab.getSlabProperty();
				Comparable<?> comparable2 = blockState.get((Property<Comparable<?>>)property);
				SlabBlock.SlabType slabType = blockState.get(SlabBlock.HALF);
				if ((direction == Direction.UP && slabType == SlabBlock.SlabType.BOTTOM || direction == Direction.DOWN && slabType == SlabBlock.SlabType.TOP)
					&& comparable2 == comparable) {
					BlockState blockState2 = this.method_11403(property, comparable2);
					Box box = blockState2.method_11726(world, pos);
					if (box != Block.EMPTY_BOX && world.hasEntityIn(box.offset(pos)) && world.setBlockState(pos, blockState2, 11)) {
						BlockSoundGroup blockSoundGroup = this.doubleSlab.getSoundGroup();
						world.method_11486(
							player, pos, blockSoundGroup.method_4194(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F
						);
						itemStack.decrement(1);
					}

					return ActionResult.SUCCESS;
				}
			}

			return this.method_11404(player, itemStack, world, pos.offset(direction), comparable)
				? ActionResult.SUCCESS
				: super.use(player, world, pos, hand, direction, x, y, z);
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public boolean canPlaceItemBlock(World world, BlockPos pos, Direction dir, PlayerEntity player, ItemStack stack) {
		Property<?> property = this.singleSlab.getSlabProperty();
		Comparable<?> comparable = this.singleSlab.method_11615(stack);
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == this.singleSlab) {
			boolean bl = blockState.get(SlabBlock.HALF) == SlabBlock.SlabType.TOP;
			if ((dir == Direction.UP && !bl || dir == Direction.DOWN && bl) && comparable == blockState.get(property)) {
				return true;
			}
		}

		BlockPos var11 = pos.offset(dir);
		BlockState blockState2 = world.getBlockState(var11);
		return blockState2.getBlock() == this.singleSlab && comparable == blockState2.get(property) ? true : super.canPlaceItemBlock(world, pos, dir, player, stack);
	}

	private boolean method_11404(PlayerEntity playerEntity, ItemStack itemStack, World world, BlockPos blockPos, Object object) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this.singleSlab) {
			Comparable<?> comparable = blockState.get((Property<Comparable<?>>)this.singleSlab.getSlabProperty());
			if (comparable == object) {
				BlockState blockState2 = this.method_11403(this.singleSlab.getSlabProperty(), comparable);
				Box box = blockState2.method_11726(world, blockPos);
				if (box != Block.EMPTY_BOX && world.hasEntityIn(box.offset(blockPos)) && world.setBlockState(blockPos, blockState2, 11)) {
					BlockSoundGroup blockSoundGroup = this.doubleSlab.getSoundGroup();
					world.method_11486(
						playerEntity,
						blockPos,
						blockSoundGroup.method_4194(),
						SoundCategory.BLOCKS,
						(blockSoundGroup.getVolume() + 1.0F) / 2.0F,
						blockSoundGroup.getPitch() * 0.8F
					);
					itemStack.decrement(1);
				}

				return true;
			}
		}

		return false;
	}

	protected <T extends Comparable<T>> BlockState method_11403(Property<T> property, Comparable<?> comparable) {
		return this.doubleSlab.getDefaultState().with(property, comparable);
	}
}
