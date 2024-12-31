package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntBlock extends Block {
	public static final BooleanProperty EXPLODE = BooleanProperty.of("explode");

	public TntBlock() {
		super(Material.TNT);
		this.setDefaultState(this.stateManager.getDefaultState().with(EXPLODE, false));
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		super.onCreation(world, pos, state);
		if (world.isReceivingRedstonePower(pos)) {
			this.onBreakByPlayer(world, pos, state.with(EXPLODE, true));
			world.setAir(pos);
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (world.isReceivingRedstonePower(pos)) {
			this.onBreakByPlayer(world, pos, state.with(EXPLODE, true));
			world.setAir(pos);
		}
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		if (!world.isClient) {
			TntEntity tntEntity = new TntEntity(
				world, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), explosion.getCausingEntity()
			);
			tntEntity.fuseTimer = world.random.nextInt(tntEntity.fuseTimer / 4) + tntEntity.fuseTimer / 8;
			world.spawnEntity(tntEntity);
		}
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
		this.activateTnt(world, pos, state, null);
	}

	public void activateTnt(World world, BlockPos pos, BlockState state, LivingEntity entity) {
		if (!world.isClient) {
			if ((Boolean)state.get(EXPLODE)) {
				TntEntity tntEntity = new TntEntity(world, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), entity);
				world.spawnEntity(tntEntity);
				world.playSound(tntEntity, "game.tnt.primed", 1.0F, 1.0F);
			}
		}
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (player.getMainHandStack() != null) {
			Item item = player.getMainHandStack().getItem();
			if (item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE) {
				this.activateTnt(world, pos, state.with(EXPLODE, true), player);
				world.setAir(pos);
				if (item == Items.FLINT_AND_STEEL) {
					player.getMainHandStack().damage(1, player);
				} else if (!player.abilities.creativeMode) {
					player.getMainHandStack().count--;
				}

				return true;
			}
		}

		return super.onUse(world, pos, state, player, direction, posX, posY, posZ);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient && entity instanceof AbstractArrowEntity) {
			AbstractArrowEntity abstractArrowEntity = (AbstractArrowEntity)entity;
			if (abstractArrowEntity.isOnFire()) {
				this.activateTnt(
					world,
					pos,
					world.getBlockState(pos).with(EXPLODE, true),
					abstractArrowEntity.owner instanceof LivingEntity ? (LivingEntity)abstractArrowEntity.owner : null
				);
				world.setAir(pos);
			}
		}
	}

	@Override
	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return false;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(EXPLODE, (data & 1) > 0);
	}

	@Override
	public int getData(BlockState state) {
		return state.get(EXPLODE) ? 1 : 0;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, EXPLODE);
	}
}
