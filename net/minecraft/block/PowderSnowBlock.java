package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PowderSnowBlock extends Block implements FluidDrainable {
	private static final float field_31216 = 0.083333336F;
	private static final float field_31217 = 0.9F;
	private static final float field_31218 = 1.5F;
	private static final float field_31219 = 2.5F;
	private static final VoxelShape field_31220 = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.9F, 1.0);

	public PowderSnowBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!(entity instanceof LivingEntity) || entity.getBlockStateAtPos().isOf(this)) {
			entity.slowMovement(state, new Vec3d(0.9F, 1.5, 0.9F));
			if (world.isClient) {
				Random random = world.getRandom();
				boolean bl = entity.lastRenderX != entity.getX() || entity.lastRenderZ != entity.getZ();
				if (bl && random.nextBoolean()) {
					world.addParticle(
						ParticleTypes.SNOWFLAKE,
						entity.getX(),
						(double)(pos.getY() + 1),
						entity.getZ(),
						(double)(MathHelper.nextBetween(random, -1.0F, 1.0F) * 0.083333336F),
						0.05F,
						(double)(MathHelper.nextBetween(random, -1.0F, 1.0F) * 0.083333336F)
					);
				}
			}
		}

		entity.setInPowderSnow(true);
		if (!world.isClient) {
			if (entity.isOnFire() && (world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) || entity instanceof PlayerEntity) && entity.canModifyAt(world, pos)) {
				world.breakBlock(pos, false);
			}

			entity.setOnFire(false);
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (context instanceof EntityShapeContext entityShapeContext) {
			Optional<Entity> optional = entityShapeContext.getEntity();
			if (optional.isPresent()) {
				Entity entity = (Entity)optional.get();
				if (entity.fallDistance > 2.5F) {
					return field_31220;
				}

				boolean bl = entity instanceof FallingBlockEntity;
				if (bl || canWalkOnPowderSnow(entity) && context.isAbove(VoxelShapes.fullCube(), pos, false) && !context.isDescending()) {
					return super.getCollisionShape(state, world, pos, context);
				}
			}
		}

		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	public static boolean canWalkOnPowderSnow(Entity entity) {
		if (entity.getType().isIn(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
			return true;
		} else {
			return entity instanceof LivingEntity ? ((LivingEntity)entity).getEquippedStack(EquipmentSlot.FEET).isOf(Items.LEATHER_BOOTS) : false;
		}
	}

	@Override
	public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
		if (!world.isClient()) {
			world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
		}

		return new ItemStack(Items.POWDER_SNOW_BUCKET);
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return true;
	}
}
