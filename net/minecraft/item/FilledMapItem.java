package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FilledMapItem extends NetworkSyncedItem {
	public FilledMapItem(Item.Settings settings) {
		super(settings);
	}

	public static ItemStack createMap(World world, int x, int z, byte scale, boolean showIcons, boolean unlimitedTracking) {
		ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
		createMapState(itemStack, world, x, z, scale, showIcons, unlimitedTracking, world.getRegistryKey());
		return itemStack;
	}

	@Nullable
	public static MapState getMapState(ItemStack stack, World world) {
		return world.getMapState(getMapName(getMapId(stack)));
	}

	@Nullable
	public static MapState getOrCreateMapState(ItemStack map, World world) {
		MapState mapState = getMapState(map, world);
		if (mapState == null && world instanceof ServerWorld) {
			mapState = createMapState(
				map, world, world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnZ(), 3, false, false, world.getRegistryKey()
			);
		}

		return mapState;
	}

	public static int getMapId(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		return compoundTag != null && compoundTag.contains("map", 99) ? compoundTag.getInt("map") : 0;
	}

	private static MapState createMapState(
		ItemStack stack, World world, int x, int z, int scale, boolean showIcons, boolean unlimitedTracking, RegistryKey<World> dimension
	) {
		int i = world.getNextMapId();
		MapState mapState = new MapState(getMapName(i));
		mapState.init(x, z, scale, showIcons, unlimitedTracking, dimension);
		world.putMapState(mapState);
		stack.getOrCreateTag().putInt("map", i);
		return mapState;
	}

	public static String getMapName(int mapId) {
		return "map_" + mapId;
	}

	public void updateColors(World world, Entity entity, MapState state) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 000: aload 1
		// 001: invokevirtual net/minecraft/world/World.getRegistryKey ()Lnet/minecraft/util/registry/RegistryKey;
		// 004: aload 3
		// 005: getfield net/minecraft/item/map/MapState.dimension Lnet/minecraft/util/registry/RegistryKey;
		// 008: if_acmpne 012
		// 00b: aload 2
		// 00c: instanceof net/minecraft/entity/player/PlayerEntity
		// 00f: ifne 013
		// 012: return
		// 013: bipush 1
		// 014: aload 3
		// 015: getfield net/minecraft/item/map/MapState.scale B
		// 018: ishl
		// 019: istore 4
		// 01b: aload 3
		// 01c: getfield net/minecraft/item/map/MapState.xCenter I
		// 01f: istore 5
		// 021: aload 3
		// 022: getfield net/minecraft/item/map/MapState.zCenter I
		// 025: istore 6
		// 027: aload 2
		// 028: invokevirtual net/minecraft/entity/Entity.getX ()D
		// 02b: iload 5
		// 02d: i2d
		// 02e: dsub
		// 02f: invokestatic net/minecraft/util/math/MathHelper.floor (D)I
		// 032: iload 4
		// 034: idiv
		// 035: bipush 64
		// 037: iadd
		// 038: istore 7
		// 03a: aload 2
		// 03b: invokevirtual net/minecraft/entity/Entity.getZ ()D
		// 03e: iload 6
		// 040: i2d
		// 041: dsub
		// 042: invokestatic net/minecraft/util/math/MathHelper.floor (D)I
		// 045: iload 4
		// 047: idiv
		// 048: bipush 64
		// 04a: iadd
		// 04b: istore 8
		// 04d: sipush 128
		// 050: iload 4
		// 052: idiv
		// 053: istore 9
		// 055: aload 1
		// 056: invokevirtual net/minecraft/world/World.getDimension ()Lnet/minecraft/world/dimension/DimensionType;
		// 059: invokevirtual net/minecraft/world/dimension/DimensionType.hasCeiling ()Z
		// 05c: ifeq 065
		// 05f: iload 9
		// 061: bipush 2
		// 062: idiv
		// 063: istore 9
		// 065: aload 3
		// 066: aload 2
		// 067: checkcast net/minecraft/entity/player/PlayerEntity
		// 06a: invokevirtual net/minecraft/item/map/MapState.getPlayerSyncData (Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/map/MapState$PlayerUpdateTracker;
		// 06d: astore 10
		// 06f: aload 10
		// 071: dup
		// 072: getfield net/minecraft/item/map/MapState$PlayerUpdateTracker.field_131 I
		// 075: bipush 1
		// 076: iadd
		// 077: putfield net/minecraft/item/map/MapState$PlayerUpdateTracker.field_131 I
		// 07a: bipush 0
		// 07b: istore 11
		// 07d: iload 7
		// 07f: iload 9
		// 081: isub
		// 082: bipush 1
		// 083: iadd
		// 084: istore 12
		// 086: iload 12
		// 088: iload 7
		// 08a: iload 9
		// 08c: iadd
		// 08d: if_icmpge 3fd
		// 090: iload 12
		// 092: bipush 15
		// 094: iand
		// 095: aload 10
		// 097: getfield net/minecraft/item/map/MapState$PlayerUpdateTracker.field_131 I
		// 09a: bipush 15
		// 09c: iand
		// 09d: if_icmpeq 0a8
		// 0a0: iload 11
		// 0a2: ifne 0a8
		// 0a5: goto 3f7
		// 0a8: bipush 0
		// 0a9: istore 11
		// 0ab: dconst_0
		// 0ac: dstore 13
		// 0ae: iload 8
		// 0b0: iload 9
		// 0b2: isub
		// 0b3: bipush 1
		// 0b4: isub
		// 0b5: istore 15
		// 0b7: iload 15
		// 0b9: iload 8
		// 0bb: iload 9
		// 0bd: iadd
		// 0be: if_icmpge 3f7
		// 0c1: iload 12
		// 0c3: iflt 3f1
		// 0c6: iload 15
		// 0c8: bipush -1
		// 0c9: if_icmplt 3f1
		// 0cc: iload 12
		// 0ce: sipush 128
		// 0d1: if_icmpge 3f1
		// 0d4: iload 15
		// 0d6: sipush 128
		// 0d9: if_icmplt 0df
		// 0dc: goto 3f1
		// 0df: iload 12
		// 0e1: iload 7
		// 0e3: isub
		// 0e4: istore 16
		// 0e6: iload 15
		// 0e8: iload 8
		// 0ea: isub
		// 0eb: istore 17
		// 0ed: iload 16
		// 0ef: iload 16
		// 0f1: imul
		// 0f2: iload 17
		// 0f4: iload 17
		// 0f6: imul
		// 0f7: iadd
		// 0f8: iload 9
		// 0fa: bipush 2
		// 0fb: isub
		// 0fc: iload 9
		// 0fe: bipush 2
		// 0ff: isub
		// 100: imul
		// 101: if_icmple 108
		// 104: bipush 1
		// 105: goto 109
		// 108: bipush 0
		// 109: istore 18
		// 10b: iload 5
		// 10d: iload 4
		// 10f: idiv
		// 110: iload 12
		// 112: iadd
		// 113: bipush 64
		// 115: isub
		// 116: iload 4
		// 118: imul
		// 119: istore 19
		// 11b: iload 6
		// 11d: iload 4
		// 11f: idiv
		// 120: iload 15
		// 122: iadd
		// 123: bipush 64
		// 125: isub
		// 126: iload 4
		// 128: imul
		// 129: istore 20
		// 12b: invokestatic com/google/common/collect/LinkedHashMultiset.create ()Lcom/google/common/collect/LinkedHashMultiset;
		// 12e: astore 21
		// 130: aload 1
		// 131: new net/minecraft/util/math/BlockPos
		// 134: dup
		// 135: iload 19
		// 137: bipush 0
		// 138: iload 20
		// 13a: invokespecial net/minecraft/util/math/BlockPos.<init> (III)V
		// 13d: invokevirtual net/minecraft/world/World.getWorldChunk (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/WorldChunk;
		// 140: astore 22
		// 142: aload 22
		// 144: invokevirtual net/minecraft/world/chunk/WorldChunk.isEmpty ()Z
		// 147: ifeq 14d
		// 14a: goto 3f1
		// 14d: aload 22
		// 14f: invokevirtual net/minecraft/world/chunk/WorldChunk.getPos ()Lnet/minecraft/util/math/ChunkPos;
		// 152: astore 23
		// 154: iload 19
		// 156: bipush 15
		// 158: iand
		// 159: istore 24
		// 15b: iload 20
		// 15d: bipush 15
		// 15f: iand
		// 160: istore 25
		// 162: bipush 0
		// 163: istore 26
		// 165: dconst_0
		// 166: dstore 27
		// 168: aload 1
		// 169: invokevirtual net/minecraft/world/World.getDimension ()Lnet/minecraft/world/dimension/DimensionType;
		// 16c: invokevirtual net/minecraft/world/dimension/DimensionType.hasCeiling ()Z
		// 16f: ifeq 1cf
		// 172: iload 19
		// 174: iload 20
		// 176: ldc 231871
		// 178: imul
		// 179: iadd
		// 17a: istore 29
		// 17c: iload 29
		// 17e: iload 29
		// 180: imul
		// 181: ldc 31287121
		// 183: imul
		// 184: iload 29
		// 186: bipush 11
		// 188: imul
		// 189: iadd
		// 18a: istore 29
		// 18c: iload 29
		// 18e: bipush 20
		// 190: ishr
		// 191: bipush 1
		// 192: iand
		// 193: ifne 1b0
		// 196: aload 21
		// 198: getstatic net/minecraft/block/Blocks.DIRT Lnet/minecraft/block/Block;
		// 19b: invokevirtual net/minecraft/block/Block.getDefaultState ()Lnet/minecraft/block/BlockState;
		// 19e: aload 1
		// 19f: getstatic net/minecraft/util/math/BlockPos.ORIGIN Lnet/minecraft/util/math/BlockPos;
		// 1a2: invokevirtual net/minecraft/block/BlockState.getTopMaterialColor (Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/MaterialColor;
		// 1a5: bipush 10
		// 1a7: invokeinterface com/google/common/collect/Multiset.add (Ljava/lang/Object;I)I 3
		// 1ac: pop
		// 1ad: goto 1c7
		// 1b0: aload 21
		// 1b2: getstatic net/minecraft/block/Blocks.STONE Lnet/minecraft/block/Block;
		// 1b5: invokevirtual net/minecraft/block/Block.getDefaultState ()Lnet/minecraft/block/BlockState;
		// 1b8: aload 1
		// 1b9: getstatic net/minecraft/util/math/BlockPos.ORIGIN Lnet/minecraft/util/math/BlockPos;
		// 1bc: invokevirtual net/minecraft/block/BlockState.getTopMaterialColor (Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/MaterialColor;
		// 1bf: bipush 100
		// 1c1: invokeinterface com/google/common/collect/Multiset.add (Ljava/lang/Object;I)I 3
		// 1c6: pop
		// 1c7: ldc2_w 100.0
		// 1ca: dstore 27
		// 1cc: goto 2ee
		// 1cf: new net/minecraft/util/math/BlockPos$Mutable
		// 1d2: dup
		// 1d3: invokespecial net/minecraft/util/math/BlockPos$Mutable.<init> ()V
		// 1d6: astore 29
		// 1d8: new net/minecraft/util/math/BlockPos$Mutable
		// 1db: dup
		// 1dc: invokespecial net/minecraft/util/math/BlockPos$Mutable.<init> ()V
		// 1df: astore 30
		// 1e1: bipush 0
		// 1e2: istore 31
		// 1e4: iload 31
		// 1e6: iload 4
		// 1e8: if_icmpge 2ee
		// 1eb: bipush 0
		// 1ec: istore 32
		// 1ee: iload 32
		// 1f0: iload 4
		// 1f2: if_icmpge 2e8
		// 1f5: aload 22
		// 1f7: getstatic net/minecraft/world/Heightmap$Type.WORLD_SURFACE Lnet/minecraft/world/Heightmap$Type;
		// 1fa: iload 31
		// 1fc: iload 24
		// 1fe: iadd
		// 1ff: iload 32
		// 201: iload 25
		// 203: iadd
		// 204: invokevirtual net/minecraft/world/chunk/WorldChunk.sampleHeightmap (Lnet/minecraft/world/Heightmap$Type;II)I
		// 207: bipush 1
		// 208: iadd
		// 209: istore 33
		// 20b: iload 33
		// 20d: bipush 1
		// 20e: if_icmple 2a0
		// 211: iinc 33 -1
		// 214: aload 29
		// 216: aload 23
		// 218: invokevirtual net/minecraft/util/math/ChunkPos.getStartX ()I
		// 21b: iload 31
		// 21d: iadd
		// 21e: iload 24
		// 220: iadd
		// 221: iload 33
		// 223: aload 23
		// 225: invokevirtual net/minecraft/util/math/ChunkPos.getStartZ ()I
		// 228: iload 32
		// 22a: iadd
		// 22b: iload 25
		// 22d: iadd
		// 22e: invokevirtual net/minecraft/util/math/BlockPos$Mutable.set (III)Lnet/minecraft/util/math/BlockPos$Mutable;
		// 231: pop
		// 232: aload 22
		// 234: aload 29
		// 236: invokevirtual net/minecraft/world/chunk/WorldChunk.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
		// 239: astore 34
		// 23b: aload 34
		// 23d: aload 1
		// 23e: aload 29
		// 240: invokevirtual net/minecraft/block/BlockState.getTopMaterialColor (Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/MaterialColor;
		// 243: getstatic net/minecraft/block/MaterialColor.CLEAR Lnet/minecraft/block/MaterialColor;
		// 246: if_acmpne 24e
		// 249: iload 33
		// 24b: ifgt 211
		// 24e: iload 33
		// 250: ifle 2a8
		// 253: aload 34
		// 255: invokevirtual net/minecraft/block/BlockState.getFluidState ()Lnet/minecraft/fluid/FluidState;
		// 258: invokevirtual net/minecraft/fluid/FluidState.isEmpty ()Z
		// 25b: ifne 2a8
		// 25e: iload 33
		// 260: bipush 1
		// 261: isub
		// 262: istore 35
		// 264: aload 30
		// 266: aload 29
		// 268: invokevirtual net/minecraft/util/math/BlockPos$Mutable.set (Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/BlockPos$Mutable;
		// 26b: pop
		// 26c: aload 30
		// 26e: iload 35
		// 270: iinc 35 -1
		// 273: invokevirtual net/minecraft/util/math/BlockPos$Mutable.setY (I)V
		// 276: aload 22
		// 278: aload 30
		// 27a: invokevirtual net/minecraft/world/chunk/WorldChunk.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
		// 27d: astore 36
		// 27f: iinc 26 1
		// 282: iload 35
		// 284: ifle 292
		// 287: aload 36
		// 289: invokevirtual net/minecraft/block/BlockState.getFluidState ()Lnet/minecraft/fluid/FluidState;
		// 28c: invokevirtual net/minecraft/fluid/FluidState.isEmpty ()Z
		// 28f: ifeq 26c
		// 292: aload 0
		// 293: aload 1
		// 294: aload 34
		// 296: aload 29
		// 298: invokespecial net/minecraft/item/FilledMapItem.getFluidStateIfVisible (Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
		// 29b: astore 34
		// 29d: goto 2a8
		// 2a0: getstatic net/minecraft/block/Blocks.BEDROCK Lnet/minecraft/block/Block;
		// 2a3: invokevirtual net/minecraft/block/Block.getDefaultState ()Lnet/minecraft/block/BlockState;
		// 2a6: astore 34
		// 2a8: aload 3
		// 2a9: aload 1
		// 2aa: aload 23
		// 2ac: invokevirtual net/minecraft/util/math/ChunkPos.getStartX ()I
		// 2af: iload 31
		// 2b1: iadd
		// 2b2: iload 24
		// 2b4: iadd
		// 2b5: aload 23
		// 2b7: invokevirtual net/minecraft/util/math/ChunkPos.getStartZ ()I
		// 2ba: iload 32
		// 2bc: iadd
		// 2bd: iload 25
		// 2bf: iadd
		// 2c0: invokevirtual net/minecraft/item/map/MapState.removeBanner (Lnet/minecraft/world/BlockView;II)V
		// 2c3: dload 27
		// 2c5: iload 33
		// 2c7: i2d
		// 2c8: iload 4
		// 2ca: iload 4
		// 2cc: imul
		// 2cd: i2d
		// 2ce: ddiv
		// 2cf: dadd
		// 2d0: dstore 27
		// 2d2: aload 21
		// 2d4: aload 34
		// 2d6: aload 1
		// 2d7: aload 29
		// 2d9: invokevirtual net/minecraft/block/BlockState.getTopMaterialColor (Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/MaterialColor;
		// 2dc: invokeinterface com/google/common/collect/Multiset.add (Ljava/lang/Object;)Z 2
		// 2e1: pop
		// 2e2: iinc 32 1
		// 2e5: goto 1ee
		// 2e8: iinc 31 1
		// 2eb: goto 1e4
		// 2ee: iload 26
		// 2f0: iload 4
		// 2f2: iload 4
		// 2f4: imul
		// 2f5: idiv
		// 2f6: istore 26
		// 2f8: dload 27
		// 2fa: dload 13
		// 2fc: dsub
		// 2fd: ldc2_w 4.0
		// 300: dmul
		// 301: iload 4
		// 303: bipush 4
		// 304: iadd
		// 305: i2d
		// 306: ddiv
		// 307: iload 12
		// 309: iload 15
		// 30b: iadd
		// 30c: bipush 1
		// 30d: iand
		// 30e: i2d
		// 30f: ldc2_w 0.5
		// 312: dsub
		// 313: ldc2_w 0.4
		// 316: dmul
		// 317: dadd
		// 318: dstore 29
		// 31a: bipush 1
		// 31b: istore 31
		// 31d: dload 29
		// 31f: ldc2_w 0.6
		// 322: dcmpl
		// 323: ifle 329
		// 326: bipush 2
		// 327: istore 31
		// 329: dload 29
		// 32b: ldc2_w -0.6
		// 32e: dcmpg
		// 32f: ifge 335
		// 332: bipush 0
		// 333: istore 31
		// 335: aload 21
		// 337: invokestatic com/google/common/collect/Multisets.copyHighestCountFirst (Lcom/google/common/collect/Multiset;)Lcom/google/common/collect/ImmutableMultiset;
		// 33a: getstatic net/minecraft/block/MaterialColor.CLEAR Lnet/minecraft/block/MaterialColor;
		// 33d: invokestatic com/google/common/collect/Iterables.getFirst (Ljava/lang/Iterable;Ljava/lang/Object;)Ljava/lang/Object;
		// 340: checkcast net/minecraft/block/MaterialColor
		// 343: astore 32
		// 345: aload 32
		// 347: getstatic net/minecraft/block/MaterialColor.WATER Lnet/minecraft/block/MaterialColor;
		// 34a: if_acmpne 37e
		// 34d: iload 26
		// 34f: i2d
		// 350: ldc2_w 0.1
		// 353: dmul
		// 354: iload 12
		// 356: iload 15
		// 358: iadd
		// 359: bipush 1
		// 35a: iand
		// 35b: i2d
		// 35c: ldc2_w 0.2
		// 35f: dmul
		// 360: dadd
		// 361: dstore 29
		// 363: bipush 1
		// 364: istore 31
		// 366: dload 29
		// 368: ldc2_w 0.5
		// 36b: dcmpg
		// 36c: ifge 372
		// 36f: bipush 2
		// 370: istore 31
		// 372: dload 29
		// 374: ldc2_w 0.9
		// 377: dcmpl
		// 378: ifle 37e
		// 37b: bipush 0
		// 37c: istore 31
		// 37e: dload 27
		// 380: dstore 13
		// 382: iload 15
		// 384: ifge 38a
		// 387: goto 3f1
		// 38a: iload 16
		// 38c: iload 16
		// 38e: imul
		// 38f: iload 17
		// 391: iload 17
		// 393: imul
		// 394: iadd
		// 395: iload 9
		// 397: iload 9
		// 399: imul
		// 39a: if_icmplt 3a0
		// 39d: goto 3f1
		// 3a0: iload 18
		// 3a2: ifeq 3b2
		// 3a5: iload 12
		// 3a7: iload 15
		// 3a9: iadd
		// 3aa: bipush 1
		// 3ab: iand
		// 3ac: ifne 3b2
		// 3af: goto 3f1
		// 3b2: aload 3
		// 3b3: getfield net/minecraft/item/map/MapState.colors [B
		// 3b6: iload 12
		// 3b8: iload 15
		// 3ba: sipush 128
		// 3bd: imul
		// 3be: iadd
		// 3bf: baload
		// 3c0: istore 33
		// 3c2: aload 32
		// 3c4: getfield net/minecraft/block/MaterialColor.id I
		// 3c7: bipush 4
		// 3c8: imul
		// 3c9: iload 31
		// 3cb: iadd
		// 3cc: i2b
		// 3cd: istore 34
		// 3cf: iload 33
		// 3d1: iload 34
		// 3d3: if_icmpeq 3f1
		// 3d6: aload 3
		// 3d7: getfield net/minecraft/item/map/MapState.colors [B
		// 3da: iload 12
		// 3dc: iload 15
		// 3de: sipush 128
		// 3e1: imul
		// 3e2: iadd
		// 3e3: iload 34
		// 3e5: bastore
		// 3e6: aload 3
		// 3e7: iload 12
		// 3e9: iload 15
		// 3eb: invokevirtual net/minecraft/item/map/MapState.markDirty (II)V
		// 3ee: bipush 1
		// 3ef: istore 11
		// 3f1: iinc 15 1
		// 3f4: goto 0b7
		// 3f7: iinc 12 1
		// 3fa: goto 086
		// 3fd: return
	}

	private BlockState getFluidStateIfVisible(World world, BlockState state, BlockPos pos) {
		FluidState fluidState = state.getFluidState();
		return !fluidState.isEmpty() && !state.isSideSolidFullSquare(world, pos, Direction.UP) ? fluidState.getBlockState() : state;
	}

	private static boolean hasPositiveDepth(Biome[] biomes, int scale, int x, int z) {
		return biomes[x * scale + z * scale * 128 * scale].getDepth() >= 0.0F;
	}

	public static void fillExplorationMap(ServerWorld serverWorld, ItemStack map) {
		MapState mapState = getOrCreateMapState(map, serverWorld);
		if (mapState != null) {
			if (serverWorld.getRegistryKey() == mapState.dimension) {
				int i = 1 << mapState.scale;
				int j = mapState.xCenter;
				int k = mapState.zCenter;
				Biome[] biomes = new Biome[128 * i * 128 * i];

				for (int l = 0; l < 128 * i; l++) {
					for (int m = 0; m < 128 * i; m++) {
						biomes[l * 128 * i + m] = serverWorld.getBiome(new BlockPos((j / i - 64) * i + m, 0, (k / i - 64) * i + l));
					}
				}

				for (int n = 0; n < 128; n++) {
					for (int o = 0; o < 128; o++) {
						if (n > 0 && o > 0 && n < 127 && o < 127) {
							Biome biome = biomes[n * i + o * i * 128 * i];
							int p = 8;
							if (hasPositiveDepth(biomes, i, n - 1, o - 1)) {
								p--;
							}

							if (hasPositiveDepth(biomes, i, n - 1, o + 1)) {
								p--;
							}

							if (hasPositiveDepth(biomes, i, n - 1, o)) {
								p--;
							}

							if (hasPositiveDepth(biomes, i, n + 1, o - 1)) {
								p--;
							}

							if (hasPositiveDepth(biomes, i, n + 1, o + 1)) {
								p--;
							}

							if (hasPositiveDepth(biomes, i, n + 1, o)) {
								p--;
							}

							if (hasPositiveDepth(biomes, i, n, o - 1)) {
								p--;
							}

							if (hasPositiveDepth(biomes, i, n, o + 1)) {
								p--;
							}

							int q = 3;
							MaterialColor materialColor = MaterialColor.CLEAR;
							if (biome.getDepth() < 0.0F) {
								materialColor = MaterialColor.ORANGE;
								if (p > 7 && o % 2 == 0) {
									q = (n + (int)(MathHelper.sin((float)o + 0.0F) * 7.0F)) / 8 % 5;
									if (q == 3) {
										q = 1;
									} else if (q == 4) {
										q = 0;
									}
								} else if (p > 7) {
									materialColor = MaterialColor.CLEAR;
								} else if (p > 5) {
									q = 1;
								} else if (p > 3) {
									q = 0;
								} else if (p > 1) {
									q = 0;
								}
							} else if (p > 0) {
								materialColor = MaterialColor.BROWN;
								if (p > 3) {
									q = 1;
								} else {
									q = 3;
								}
							}

							if (materialColor != MaterialColor.CLEAR) {
								mapState.colors[n + o * 128] = (byte)(materialColor.id * 4 + q);
								mapState.markDirty(n, o);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			MapState mapState = getOrCreateMapState(stack, world);
			if (mapState != null) {
				if (entity instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity)entity;
					mapState.update(playerEntity, stack);
				}

				if (!mapState.locked && (selected || entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == stack)) {
					this.updateColors(world, entity, mapState);
				}
			}
		}
	}

	@Nullable
	@Override
	public Packet<?> createSyncPacket(ItemStack stack, World world, PlayerEntity player) {
		return getOrCreateMapState(stack, world).getPlayerMarkerPacket(stack, world, player);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		CompoundTag compoundTag = stack.getTag();
		if (compoundTag != null && compoundTag.contains("map_scale_direction", 99)) {
			scale(stack, world, compoundTag.getInt("map_scale_direction"));
			compoundTag.remove("map_scale_direction");
		} else if (compoundTag != null && compoundTag.contains("map_to_lock", 1) && compoundTag.getBoolean("map_to_lock")) {
			copyMap(world, stack);
			compoundTag.remove("map_to_lock");
		}
	}

	protected static void scale(ItemStack map, World world, int amount) {
		MapState mapState = getOrCreateMapState(map, world);
		if (mapState != null) {
			createMapState(
				map,
				world,
				mapState.xCenter,
				mapState.zCenter,
				MathHelper.clamp(mapState.scale + amount, 0, 4),
				mapState.showIcons,
				mapState.unlimitedTracking,
				mapState.dimension
			);
		}
	}

	public static void copyMap(World world, ItemStack stack) {
		MapState mapState = getOrCreateMapState(stack, world);
		if (mapState != null) {
			MapState mapState2 = createMapState(stack, world, 0, 0, mapState.scale, mapState.showIcons, mapState.unlimitedTracking, mapState.dimension);
			mapState2.copyFrom(mapState);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		MapState mapState = world == null ? null : getOrCreateMapState(stack, world);
		if (mapState != null && mapState.locked) {
			tooltip.add(new TranslatableText("filled_map.locked", getMapId(stack)).formatted(Formatting.GRAY));
		}

		if (context.isAdvanced()) {
			if (mapState != null) {
				tooltip.add(new TranslatableText("filled_map.id", getMapId(stack)).formatted(Formatting.GRAY));
				tooltip.add(new TranslatableText("filled_map.scale", 1 << mapState.scale).formatted(Formatting.GRAY));
				tooltip.add(new TranslatableText("filled_map.level", mapState.scale, 4).formatted(Formatting.GRAY));
			} else {
				tooltip.add(new TranslatableText("filled_map.unknown").formatted(Formatting.GRAY));
			}
		}
	}

	public static int getMapColor(ItemStack stack) {
		CompoundTag compoundTag = stack.getSubTag("display");
		if (compoundTag != null && compoundTag.contains("MapColor", 99)) {
			int i = compoundTag.getInt("MapColor");
			return 0xFF000000 | i & 16777215;
		} else {
			return -12173266;
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		if (blockState.isIn(BlockTags.BANNERS)) {
			if (!context.getWorld().isClient) {
				MapState mapState = getOrCreateMapState(context.getStack(), context.getWorld());
				mapState.addBanner(context.getWorld(), context.getBlockPos());
			}

			return ActionResult.success(context.getWorld().isClient);
		} else {
			return super.useOnBlock(context);
		}
	}
}
