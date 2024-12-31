package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfestedBlock extends Block {
	private final Block field_18377;
	private static final Map<Block, Block> field_18378 = Maps.newIdentityHashMap();

	public InfestedBlock(Block block, Block.Builder builder) {
		super(builder);
		this.field_18377 = block;
		field_18378.put(block, this);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	public Block method_16685() {
		return this.field_18377;
	}

	public static boolean method_16687(BlockState blockState) {
		return field_18378.containsKey(blockState.getBlock());
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return new ItemStack(this.field_18377);
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
			SilverfishEntity silverfishEntity = new SilverfishEntity(world);
			silverfishEntity.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
			world.method_3686(silverfishEntity);
			silverfishEntity.playSpawnEffects();
		}
	}

	public static BlockState method_16686(Block block) {
		return ((Block)field_18378.get(block)).getDefaultState();
	}
}
