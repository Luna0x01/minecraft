package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntBlock extends Block {
	public static final BooleanProperty field_18529 = Properties.UNSTABLE;

	public TntBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.getDefaultState().withProperty(field_18529, Boolean.valueOf(false)));
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			if (world.isReceivingRedstonePower(pos)) {
				this.method_16751(world, pos);
				world.method_8553(pos);
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (world.isReceivingRedstonePower(pos)) {
			this.method_16751(world, pos);
			world.method_8553(pos);
		}
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		if (!(Boolean)blockState.getProperty(field_18529)) {
			super.method_410(blockState, world, blockPos, f, i);
		}
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.method_16390() && !player.isCreative() && (Boolean)state.getProperty(field_18529)) {
			this.method_16751(world, pos);
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		if (!world.isClient) {
			TntEntity tntEntity = new TntEntity(
				world, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), explosion.getCausingEntity()
			);
			tntEntity.setFuse((short)(world.random.nextInt(tntEntity.getRemainingFuse() / 4) + tntEntity.getRemainingFuse() / 8));
			world.method_3686(tntEntity);
		}
	}

	public void method_16751(World world, BlockPos blockPos) {
		this.method_11639(world, blockPos, null);
	}

	private void method_11639(World world, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
		if (!world.isClient) {
			TntEntity tntEntity = new TntEntity(
				world, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), livingEntity
			);
			world.method_3686(tntEntity);
			world.playSound(null, tntEntity.x, tntEntity.y, tntEntity.z, Sounds.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
			return super.onUse(state, world, pos, player, hand, direction, distanceX, distanceY, distanceZ);
		} else {
			this.method_11639(world, pos, player);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			if (item == Items.FLINT_AND_STEEL) {
				itemStack.damage(1, player);
			} else {
				itemStack.decrement(1);
			}

			return true;
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && entity instanceof AbstractArrowEntity) {
			AbstractArrowEntity abstractArrowEntity = (AbstractArrowEntity)entity;
			Entity entity2 = abstractArrowEntity.method_15950();
			if (abstractArrowEntity.isOnFire()) {
				this.method_11639(world, pos, entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null);
				world.method_8553(pos);
			}
		}
	}

	@Override
	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return false;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18529);
	}
}
