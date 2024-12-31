package net.minecraft.item;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AutomaticItemPlacementContext extends ItemPlacementContext {
	private final Direction facing;

	public AutomaticItemPlacementContext(World world, BlockPos pos, Direction facing, ItemStack stack, Direction side) {
		super(world, null, Hand.MAIN_HAND, stack, new BlockHitResult(Vec3d.ofBottomCenter(pos), side, pos, false));
		this.facing = facing;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.getHitResult().getBlockPos();
	}

	@Override
	public boolean canPlace() {
		return this.getWorld().getBlockState(this.getHitResult().getBlockPos()).canReplace(this);
	}

	@Override
	public boolean canReplaceExisting() {
		return this.canPlace();
	}

	@Override
	public Direction getPlayerLookDirection() {
		return Direction.DOWN;
	}

	@Override
	public Direction[] getPlacementDirections() {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 000: getstatic net/minecraft/item/AutomaticItemPlacementContext$1.field_13363 [I
		// 003: aload 0
		// 004: getfield net/minecraft/item/AutomaticItemPlacementContext.facing Lnet/minecraft/util/math/Direction;
		// 007: invokevirtual net/minecraft/util/math/Direction.ordinal ()I
		// 00a: iaload
		// 00b: tableswitch 37 1 6 37 79 121 163 205 247
		// 030: bipush 6
		// 032: anewarray 11
		// 035: dup
		// 036: bipush 0
		// 037: getstatic net/minecraft/util/math/Direction.DOWN Lnet/minecraft/util/math/Direction;
		// 03a: aastore
		// 03b: dup
		// 03c: bipush 1
		// 03d: getstatic net/minecraft/util/math/Direction.NORTH Lnet/minecraft/util/math/Direction;
		// 040: aastore
		// 041: dup
		// 042: bipush 2
		// 043: getstatic net/minecraft/util/math/Direction.EAST Lnet/minecraft/util/math/Direction;
		// 046: aastore
		// 047: dup
		// 048: bipush 3
		// 049: getstatic net/minecraft/util/math/Direction.SOUTH Lnet/minecraft/util/math/Direction;
		// 04c: aastore
		// 04d: dup
		// 04e: bipush 4
		// 04f: getstatic net/minecraft/util/math/Direction.WEST Lnet/minecraft/util/math/Direction;
		// 052: aastore
		// 053: dup
		// 054: bipush 5
		// 055: getstatic net/minecraft/util/math/Direction.UP Lnet/minecraft/util/math/Direction;
		// 058: aastore
		// 059: areturn
		// 05a: bipush 6
		// 05c: anewarray 11
		// 05f: dup
		// 060: bipush 0
		// 061: getstatic net/minecraft/util/math/Direction.DOWN Lnet/minecraft/util/math/Direction;
		// 064: aastore
		// 065: dup
		// 066: bipush 1
		// 067: getstatic net/minecraft/util/math/Direction.UP Lnet/minecraft/util/math/Direction;
		// 06a: aastore
		// 06b: dup
		// 06c: bipush 2
		// 06d: getstatic net/minecraft/util/math/Direction.NORTH Lnet/minecraft/util/math/Direction;
		// 070: aastore
		// 071: dup
		// 072: bipush 3
		// 073: getstatic net/minecraft/util/math/Direction.EAST Lnet/minecraft/util/math/Direction;
		// 076: aastore
		// 077: dup
		// 078: bipush 4
		// 079: getstatic net/minecraft/util/math/Direction.SOUTH Lnet/minecraft/util/math/Direction;
		// 07c: aastore
		// 07d: dup
		// 07e: bipush 5
		// 07f: getstatic net/minecraft/util/math/Direction.WEST Lnet/minecraft/util/math/Direction;
		// 082: aastore
		// 083: areturn
		// 084: bipush 6
		// 086: anewarray 11
		// 089: dup
		// 08a: bipush 0
		// 08b: getstatic net/minecraft/util/math/Direction.DOWN Lnet/minecraft/util/math/Direction;
		// 08e: aastore
		// 08f: dup
		// 090: bipush 1
		// 091: getstatic net/minecraft/util/math/Direction.NORTH Lnet/minecraft/util/math/Direction;
		// 094: aastore
		// 095: dup
		// 096: bipush 2
		// 097: getstatic net/minecraft/util/math/Direction.EAST Lnet/minecraft/util/math/Direction;
		// 09a: aastore
		// 09b: dup
		// 09c: bipush 3
		// 09d: getstatic net/minecraft/util/math/Direction.WEST Lnet/minecraft/util/math/Direction;
		// 0a0: aastore
		// 0a1: dup
		// 0a2: bipush 4
		// 0a3: getstatic net/minecraft/util/math/Direction.UP Lnet/minecraft/util/math/Direction;
		// 0a6: aastore
		// 0a7: dup
		// 0a8: bipush 5
		// 0a9: getstatic net/minecraft/util/math/Direction.SOUTH Lnet/minecraft/util/math/Direction;
		// 0ac: aastore
		// 0ad: areturn
		// 0ae: bipush 6
		// 0b0: anewarray 11
		// 0b3: dup
		// 0b4: bipush 0
		// 0b5: getstatic net/minecraft/util/math/Direction.DOWN Lnet/minecraft/util/math/Direction;
		// 0b8: aastore
		// 0b9: dup
		// 0ba: bipush 1
		// 0bb: getstatic net/minecraft/util/math/Direction.SOUTH Lnet/minecraft/util/math/Direction;
		// 0be: aastore
		// 0bf: dup
		// 0c0: bipush 2
		// 0c1: getstatic net/minecraft/util/math/Direction.EAST Lnet/minecraft/util/math/Direction;
		// 0c4: aastore
		// 0c5: dup
		// 0c6: bipush 3
		// 0c7: getstatic net/minecraft/util/math/Direction.WEST Lnet/minecraft/util/math/Direction;
		// 0ca: aastore
		// 0cb: dup
		// 0cc: bipush 4
		// 0cd: getstatic net/minecraft/util/math/Direction.UP Lnet/minecraft/util/math/Direction;
		// 0d0: aastore
		// 0d1: dup
		// 0d2: bipush 5
		// 0d3: getstatic net/minecraft/util/math/Direction.NORTH Lnet/minecraft/util/math/Direction;
		// 0d6: aastore
		// 0d7: areturn
		// 0d8: bipush 6
		// 0da: anewarray 11
		// 0dd: dup
		// 0de: bipush 0
		// 0df: getstatic net/minecraft/util/math/Direction.DOWN Lnet/minecraft/util/math/Direction;
		// 0e2: aastore
		// 0e3: dup
		// 0e4: bipush 1
		// 0e5: getstatic net/minecraft/util/math/Direction.WEST Lnet/minecraft/util/math/Direction;
		// 0e8: aastore
		// 0e9: dup
		// 0ea: bipush 2
		// 0eb: getstatic net/minecraft/util/math/Direction.SOUTH Lnet/minecraft/util/math/Direction;
		// 0ee: aastore
		// 0ef: dup
		// 0f0: bipush 3
		// 0f1: getstatic net/minecraft/util/math/Direction.UP Lnet/minecraft/util/math/Direction;
		// 0f4: aastore
		// 0f5: dup
		// 0f6: bipush 4
		// 0f7: getstatic net/minecraft/util/math/Direction.NORTH Lnet/minecraft/util/math/Direction;
		// 0fa: aastore
		// 0fb: dup
		// 0fc: bipush 5
		// 0fd: getstatic net/minecraft/util/math/Direction.EAST Lnet/minecraft/util/math/Direction;
		// 100: aastore
		// 101: areturn
		// 102: bipush 6
		// 104: anewarray 11
		// 107: dup
		// 108: bipush 0
		// 109: getstatic net/minecraft/util/math/Direction.DOWN Lnet/minecraft/util/math/Direction;
		// 10c: aastore
		// 10d: dup
		// 10e: bipush 1
		// 10f: getstatic net/minecraft/util/math/Direction.EAST Lnet/minecraft/util/math/Direction;
		// 112: aastore
		// 113: dup
		// 114: bipush 2
		// 115: getstatic net/minecraft/util/math/Direction.SOUTH Lnet/minecraft/util/math/Direction;
		// 118: aastore
		// 119: dup
		// 11a: bipush 3
		// 11b: getstatic net/minecraft/util/math/Direction.UP Lnet/minecraft/util/math/Direction;
		// 11e: aastore
		// 11f: dup
		// 120: bipush 4
		// 121: getstatic net/minecraft/util/math/Direction.NORTH Lnet/minecraft/util/math/Direction;
		// 124: aastore
		// 125: dup
		// 126: bipush 5
		// 127: getstatic net/minecraft/util/math/Direction.WEST Lnet/minecraft/util/math/Direction;
		// 12a: aastore
		// 12b: areturn
	}

	@Override
	public Direction getPlayerFacing() {
		return this.facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.facing;
	}

	@Override
	public boolean shouldCancelInteraction() {
		return false;
	}

	@Override
	public float getPlayerYaw() {
		return (float)(this.facing.getHorizontal() * 90);
	}
}
