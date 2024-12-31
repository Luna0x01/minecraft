package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
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
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (world.isReceivingRedstonePower(blockPos)) {
			this.onBreakByPlayer(world, blockPos, blockState.with(EXPLODE, true));
			world.setAir(blockPos);
		}
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		if (!world.isClient) {
			TntEntity tntEntity = new TntEntity(
				world, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), explosion.getCausingEntity()
			);
			tntEntity.setFuse((short)(world.random.nextInt(tntEntity.getRemainingFuse() / 4) + tntEntity.getRemainingFuse() / 8));
			world.spawnEntity(tntEntity);
		}
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
		this.method_11639(world, pos, state, null);
	}

	public void method_11639(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity) {
		if (!world.isClient) {
			if ((Boolean)blockState.get(EXPLODE)) {
				TntEntity tntEntity = new TntEntity(
					world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), livingEntity
				);
				world.spawnEntity(tntEntity);
				world.playSound(null, tntEntity.x, tntEntity.y, tntEntity.z, Sounds.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
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
		if (itemStack != null && (itemStack.getItem() == Items.FLINT_AND_STEEL || itemStack.getItem() == Items.FIRE_CHARGE)) {
			this.method_11639(world, blockPos, blockState.with(EXPLODE, true), playerEntity);
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
			if (itemStack.getItem() == Items.FLINT_AND_STEEL) {
				itemStack.damage(1, playerEntity);
			} else if (!playerEntity.abilities.creativeMode) {
				itemStack.count--;
			}

			return true;
		} else {
			return super.method_421(world, blockPos, blockState, playerEntity, hand, itemStack, direction, f, g, h);
		}
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient && entity instanceof AbstractArrowEntity) {
			AbstractArrowEntity abstractArrowEntity = (AbstractArrowEntity)entity;
			if (abstractArrowEntity.isOnFire()) {
				this.method_11639(
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
