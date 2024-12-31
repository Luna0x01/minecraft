package net.minecraft.block;

import net.minecraft.class_3697;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpkinBlock extends GourdBlock {
	protected PumpkinBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == Items.SHEARS) {
			if (!world.isClient) {
				Direction direction2 = direction.getAxis() == Direction.Axis.Y ? player.getHorizontalDirection().getOpposite() : direction;
				world.playSound(null, pos, Sounds.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				world.setBlockState(pos, Blocks.CARVED_PUMPKIN.getDefaultState().withProperty(class_3697.field_18229, direction2), 11);
				ItemEntity itemEntity = new ItemEntity(
					world,
					(double)pos.getX() + 0.5 + (double)direction2.getOffsetX() * 0.65,
					(double)pos.getY() + 0.1,
					(double)pos.getZ() + 0.5 + (double)direction2.getOffsetZ() * 0.65,
					new ItemStack(Items.PUMPKIN_SEEDS, 4)
				);
				itemEntity.velocityX = 0.05 * (double)direction2.getOffsetX() + world.random.nextDouble() * 0.02;
				itemEntity.velocityY = 0.05;
				itemEntity.velocityZ = 0.05 * (double)direction2.getOffsetZ() + world.random.nextDouble() * 0.02;
				world.method_3686(itemEntity);
				itemStack.damage(1, player);
			}

			return true;
		} else {
			return super.onUse(state, world, pos, player, hand, direction, distanceX, distanceY, distanceZ);
		}
	}

	@Override
	public StemBlock getStem() {
		return (StemBlock)Blocks.PUMPKIN_STEM;
	}

	@Override
	public AttachedStemBlock getAttachedStem() {
		return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
	}
}
