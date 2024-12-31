package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndGatewayBlock extends BlockWithEntity {
	protected EndGatewayBlock(Material material) {
		super(material);
		this.setLightLevel(1.0F);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new EndGatewayBlockEntity();
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		BlockState blockState = view.getBlockState(pos.offset(direction));
		Block block = blockState.getBlock();
		return !blockState.isFullBoundsCubeForCulling() && block != Blocks.END_GATEWAY;
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof EndGatewayBlockEntity) {
			int i = ((EndGatewayBlockEntity)blockEntity).method_11697();

			for (int j = 0; j < i; j++) {
				double d = (double)((float)pos.getX() + random.nextFloat());
				double e = (double)((float)pos.getY() + random.nextFloat());
				double f = (double)((float)pos.getZ() + random.nextFloat());
				double g = ((double)random.nextFloat() - 0.5) * 0.5;
				double h = ((double)random.nextFloat() - 0.5) * 0.5;
				double k = ((double)random.nextFloat() - 0.5) * 0.5;
				int l = random.nextInt(2) * 2 - 1;
				if (random.nextBoolean()) {
					f = (double)pos.getZ() + 0.5 + 0.25 * (double)l;
					k = (double)(random.nextFloat() * 2.0F * (float)l);
				} else {
					d = (double)pos.getX() + 0.5 + 0.25 * (double)l;
					g = (double)(random.nextFloat() * 2.0F * (float)l);
				}

				world.addParticle(ParticleType.NETHER_PORTAL, d, e, f, g, h, k);
			}
		}
	}

	@Nullable
	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return null;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.BLACK;
	}
}
