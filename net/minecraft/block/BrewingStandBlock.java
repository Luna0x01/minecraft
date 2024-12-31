package net.minecraft.block;

import java.util.List;
import java.util.Random;
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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BrewingStandBlock extends BlockWithEntity {
	public static final BooleanProperty[] HAS_BOTTLES = new BooleanProperty[]{
		BooleanProperty.of("has_bottle_0"), BooleanProperty.of("has_bottle_1"), BooleanProperty.of("has_bottle_2")
	};

	public BrewingStandBlock() {
		super(Material.IRON);
		this.setDefaultState(this.stateManager.getDefaultState().with(HAS_BOTTLES[0], false).with(HAS_BOTTLES[1], false).with(HAS_BOTTLES[2], false));
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate("item.brewingStand.name");
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public int getBlockType() {
		return 3;
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new BrewingStandBlockEntity();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		this.setBoundingBox(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBlockItemBounds();
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
	}

	@Override
	public void setBlockItemBounds() {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				player.openInventory((BrewingStandBlockEntity)blockEntity);
				player.incrementStat(Stats.INTERACTIONS_WITH_BREWING_STAND);
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
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		double d = (double)((float)pos.getX() + 0.4F + rand.nextFloat() * 0.2F);
		double e = (double)((float)pos.getY() + 0.7F + rand.nextFloat() * 0.3F);
		double f = (double)((float)pos.getZ() + 0.4F + rand.nextFloat() * 0.2F);
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

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.BREWING_STAND;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.BREWING_STAND;
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
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
