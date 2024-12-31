package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BrewingStandBlock extends BlockWithEntity {
	public static final BooleanProperty[] HAS_BOTTLES = new BooleanProperty[]{
		BooleanProperty.of("has_bottle_0"), BooleanProperty.of("has_bottle_1"), BooleanProperty.of("has_bottle_2")
	};
	protected static final Box field_12592 = new Box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
	protected static final Box field_12593 = new Box(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625);

	public BrewingStandBlock() {
		super(Material.IRON);
		this.setDefaultState(this.stateManager.getDefaultState().with(HAS_BOTTLES[0], false).with(HAS_BOTTLES[1], false).with(HAS_BOTTLES[2], false));
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate("item.brewingStand.name");
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new BrewingStandBlockEntity();
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
		appendCollisionBoxes(pos, entityBox, boxes, field_12593);
		appendCollisionBoxes(pos, entityBox, boxes, field_12592);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12592;
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
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				playerEntity.openInventory((BrewingStandBlockEntity)blockEntity);
				playerEntity.incrementStat(Stats.INTERACTIONS_WITH_BREWING_STAND);
			}

			return true;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				((BrewingStandBlockEntity)blockEntity).setCustomName(itemStack.getCustomName());
			}
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)((float)pos.getX() + 0.4F + random.nextFloat() * 0.2F);
		double e = (double)((float)pos.getY() + 0.7F + random.nextFloat() * 0.3F);
		double f = (double)((float)pos.getZ() + 0.4F + random.nextFloat() * 0.2F);
		world.addParticle(ParticleType.SMOKE, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BrewingStandBlockEntity) {
			ItemScatterer.spawn(world, pos, (BrewingStandBlockEntity)blockEntity);
		}

		super.onBreaking(world, pos, state);
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.BREWING_STAND;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.BREWING_STAND);
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState();

		for (int i = 0; i < 3; i++) {
			blockState = blockState.with(HAS_BOTTLES[i], (data & 1 << i) > 0);
		}

		return blockState;
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;

		for (int j = 0; j < 3; j++) {
			if ((Boolean)state.get(HAS_BOTTLES[j])) {
				i |= 1 << j;
			}
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, HAS_BOTTLES[0], HAS_BOTTLES[1], HAS_BOTTLES[2]);
	}
}
