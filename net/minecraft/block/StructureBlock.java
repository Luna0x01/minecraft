package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StructureBlock extends BlockWithEntity {
	public static final EnumProperty<StructureBlockMode> field_18522 = Properties.STRUCTURE_BLOCK_MODE;

	protected StructureBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new StructureBlockEntity();
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof StructureBlockEntity ? ((StructureBlockEntity)blockEntity).method_13342(player) : false;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		if (!world.isClient) {
			if (placer != null) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof StructureBlockEntity) {
					((StructureBlockEntity)blockEntity).method_13341(placer);
				}
			}
		}
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 0;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(field_18522, StructureBlockMode.DATA);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18522);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof StructureBlockEntity) {
				StructureBlockEntity structureBlockEntity = (StructureBlockEntity)blockEntity;
				boolean bl = world.isReceivingRedstonePower(pos);
				boolean bl2 = structureBlockEntity.method_13334();
				if (bl && !bl2) {
					structureBlockEntity.method_13346(true);
					this.method_13319(structureBlockEntity);
				} else if (!bl && bl2) {
					structureBlockEntity.method_13346(false);
				}
			}
		}
	}

	private void method_13319(StructureBlockEntity structureBlockEntity) {
		switch (structureBlockEntity.method_13354()) {
			case SAVE:
				structureBlockEntity.method_13343(false);
				break;
			case LOAD:
				structureBlockEntity.method_13344(false);
				break;
			case CORNER:
				structureBlockEntity.method_13332();
			case DATA:
		}
	}
}
