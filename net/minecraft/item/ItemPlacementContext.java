package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemPlacementContext extends ItemUsageContext {
	private final BlockPos placementPos;
	protected boolean canReplaceExisting = true;

	public ItemPlacementContext(PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 0: aload 0
		// 1: aload 1
		// 2: getfield net/minecraft/entity/player/PlayerEntity.world Lnet/minecraft/world/World;
		// 5: aload 1
		// 6: aload 2
		// 7: aload 3
		// 8: aload 4
		// a: invokespecial net/minecraft/item/ItemPlacementContext.<init> (Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/hit/BlockHitResult;)V
		// d: return
	}

	public ItemPlacementContext(ItemUsageContext context) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 00: aload 0
		// 01: aload 1
		// 02: invokevirtual net/minecraft/item/ItemUsageContext.getWorld ()Lnet/minecraft/world/World;
		// 05: aload 1
		// 06: invokevirtual net/minecraft/item/ItemUsageContext.getPlayer ()Lnet/minecraft/entity/player/PlayerEntity;
		// 09: aload 1
		// 0a: invokevirtual net/minecraft/item/ItemUsageContext.getHand ()Lnet/minecraft/util/Hand;
		// 0d: aload 1
		// 0e: invokevirtual net/minecraft/item/ItemUsageContext.getStack ()Lnet/minecraft/item/ItemStack;
		// 11: aload 1
		// 12: invokevirtual net/minecraft/item/ItemUsageContext.getHitResult ()Lnet/minecraft/util/hit/BlockHitResult;
		// 15: invokespecial net/minecraft/item/ItemPlacementContext.<init> (Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/hit/BlockHitResult;)V
		// 18: return
	}

	protected ItemPlacementContext(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
		super(world, playerEntity, hand, itemStack, blockHitResult);
		this.placementPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
		this.canReplaceExisting = world.getBlockState(blockHitResult.getBlockPos()).canReplace(this);
	}

	public static ItemPlacementContext offset(ItemPlacementContext context, BlockPos pos, Direction side) {
		return new ItemPlacementContext(
			context.getWorld(),
			context.getPlayer(),
			context.getHand(),
			context.getStack(),
			new BlockHitResult(
				new Vec3d(
					(double)pos.getX() + 0.5 + (double)side.getOffsetX() * 0.5,
					(double)pos.getY() + 0.5 + (double)side.getOffsetY() * 0.5,
					(double)pos.getZ() + 0.5 + (double)side.getOffsetZ() * 0.5
				),
				side,
				pos,
				false
			)
		);
	}

	@Override
	public BlockPos getBlockPos() {
		return this.canReplaceExisting ? super.getBlockPos() : this.placementPos;
	}

	public boolean canPlace() {
		return this.canReplaceExisting || this.getWorld().getBlockState(this.getBlockPos()).canReplace(this);
	}

	public boolean canReplaceExisting() {
		return this.canReplaceExisting;
	}

	public Direction getPlayerLookDirection() {
		return Direction.getEntityFacingOrder(this.getPlayer())[0];
	}

	public Direction[] getPlacementDirections() {
		Direction[] directions = Direction.getEntityFacingOrder(this.getPlayer());
		if (this.canReplaceExisting) {
			return directions;
		} else {
			Direction direction = this.getSide();
			int i = 0;

			while (i < directions.length && directions[i] != direction.getOpposite()) {
				i++;
			}

			if (i > 0) {
				System.arraycopy(directions, 0, directions, 1, i);
				directions[0] = direction.getOpposite();
			}

			return directions;
		}
	}
}
