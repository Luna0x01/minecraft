package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class StructureBlock extends BlockWithEntity {
	public static final EnumProperty<StructureBlockEntity.class_2739> field_12799 = EnumProperty.of("mode", StructureBlockEntity.class_2739.class);

	public StructureBlock() {
		super(Material.IRON, MaterialColor.LIGHT_GRAY);
		this.setDefaultState(this.stateManager.getDefaultState());
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new StructureBlockEntity();
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		return false;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
	}

	@Nullable
	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return null;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(field_12799, StructureBlockEntity.class_2739.DATA);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(field_12799, StructureBlockEntity.class_2739.method_11685(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((StructureBlockEntity.class_2739)state.get(field_12799)).method_11684();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, field_12799);
	}
}
