package net.minecraft.block.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.class_2763;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class StructureBlockEntity extends BlockEntity {
	private String field_12858 = "";
	private String field_12859 = "";
	private String field_12860 = "";
	private BlockPos field_12861 = new BlockPos(0, 1, 0);
	private BlockPos field_12862 = BlockPos.ORIGIN;
	private BlockMirror field_12863 = BlockMirror.NONE;
	private BlockRotation field_12864 = BlockRotation.NONE;
	private StructureBlockEntity.class_2739 field_12865 = StructureBlockEntity.class_2739.DATA;
	private boolean field_12866 = true;
	private boolean field_14838;
	private boolean field_14839;
	private boolean field_14840 = true;
	private float field_14841 = 1.0F;
	private long field_14842;

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putString("name", this.field_12858);
		nbt.putString("author", this.field_12859);
		nbt.putString("metadata", this.field_12860);
		nbt.putInt("posX", this.field_12861.getX());
		nbt.putInt("posY", this.field_12861.getY());
		nbt.putInt("posZ", this.field_12861.getZ());
		nbt.putInt("sizeX", this.field_12862.getX());
		nbt.putInt("sizeY", this.field_12862.getY());
		nbt.putInt("sizeZ", this.field_12862.getZ());
		nbt.putString("rotation", this.field_12864.toString());
		nbt.putString("mirror", this.field_12863.toString());
		nbt.putString("mode", this.field_12865.toString());
		nbt.putBoolean("ignoreEntities", this.field_12866);
		nbt.putBoolean("powered", this.field_14838);
		nbt.putBoolean("showair", this.field_14839);
		nbt.putBoolean("showboundingbox", this.field_14840);
		nbt.putFloat("integrity", this.field_14841);
		nbt.putLong("seed", this.field_14842);
		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.method_11673(nbt.getString("name"));
		this.field_12859 = nbt.getString("author");
		this.field_12860 = nbt.getString("metadata");
		int i = MathHelper.clamp(nbt.getInt("posX"), -32, 32);
		int j = MathHelper.clamp(nbt.getInt("posY"), -32, 32);
		int k = MathHelper.clamp(nbt.getInt("posZ"), -32, 32);
		this.field_12861 = new BlockPos(i, j, k);
		int l = MathHelper.clamp(nbt.getInt("sizeX"), 0, 32);
		int m = MathHelper.clamp(nbt.getInt("sizeY"), 0, 32);
		int n = MathHelper.clamp(nbt.getInt("sizeZ"), 0, 32);
		this.field_12862 = new BlockPos(l, m, n);

		try {
			this.field_12864 = BlockRotation.valueOf(nbt.getString("rotation"));
		} catch (IllegalArgumentException var11) {
			this.field_12864 = BlockRotation.NONE;
		}

		try {
			this.field_12863 = BlockMirror.valueOf(nbt.getString("mirror"));
		} catch (IllegalArgumentException var10) {
			this.field_12863 = BlockMirror.NONE;
		}

		try {
			this.field_12865 = StructureBlockEntity.class_2739.valueOf(nbt.getString("mode"));
		} catch (IllegalArgumentException var9) {
			this.field_12865 = StructureBlockEntity.class_2739.DATA;
		}

		this.field_12866 = nbt.getBoolean("ignoreEntities");
		this.field_14838 = nbt.getBoolean("powered");
		this.field_14839 = nbt.getBoolean("showair");
		this.field_14840 = nbt.getBoolean("showboundingbox");
		if (nbt.contains("integrity")) {
			this.field_14841 = nbt.getFloat("integrity");
		} else {
			this.field_14841 = 1.0F;
		}

		this.field_14842 = nbt.getLong("seed");
		this.method_13337();
	}

	private void method_13337() {
		if (this.world != null) {
			BlockPos blockPos = this.getPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK) {
				this.world.setBlockState(blockPos, blockState.with(StructureBlock.field_12799, this.field_12865), 2);
			}
		}
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 7, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	public boolean method_13342(PlayerEntity playerEntity) {
		if (!playerEntity.method_13567()) {
			return false;
		} else {
			if (playerEntity.getWorld().isClient) {
				playerEntity.method_13565(this);
			}

			return true;
		}
	}

	public String method_13345() {
		return this.field_12858;
	}

	public void method_11673(String string) {
		String string2 = string;

		for (char c : SharedConstants.field_14996) {
			string2 = string2.replace(c, '_');
		}

		this.field_12858 = string2;
	}

	public void method_13341(LivingEntity livingEntity) {
		if (!ChatUtil.isEmpty(livingEntity.getTranslationKey())) {
			this.field_12859 = livingEntity.getTranslationKey();
		}
	}

	public BlockPos method_13347() {
		return this.field_12861;
	}

	public void method_11677(BlockPos blockPos) {
		this.field_12861 = blockPos;
	}

	public BlockPos method_13350() {
		return this.field_12862;
	}

	public void method_11679(BlockPos blockPos) {
		this.field_12862 = blockPos;
	}

	public BlockMirror method_13351() {
		return this.field_12863;
	}

	public void method_11667(BlockMirror blockMirror) {
		this.field_12863 = blockMirror;
	}

	public BlockRotation method_13352() {
		return this.field_12864;
	}

	public void method_11668(BlockRotation blockRotation) {
		this.field_12864 = blockRotation;
	}

	public String method_13353() {
		return this.field_12860;
	}

	public void method_11678(String string) {
		this.field_12860 = string;
	}

	public StructureBlockEntity.class_2739 method_13354() {
		return this.field_12865;
	}

	public void method_11669(StructureBlockEntity.class_2739 arg) {
		this.field_12865 = arg;
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK) {
			this.world.setBlockState(this.getPos(), blockState.with(StructureBlock.field_12799, arg), 2);
		}
	}

	public void method_13355() {
		switch (this.method_13354()) {
			case SAVE:
				this.method_11669(StructureBlockEntity.class_2739.LOAD);
				break;
			case LOAD:
				this.method_11669(StructureBlockEntity.class_2739.CORNER);
				break;
			case CORNER:
				this.method_11669(StructureBlockEntity.class_2739.DATA);
				break;
			case DATA:
				this.method_11669(StructureBlockEntity.class_2739.SAVE);
		}
	}

	public boolean method_13356() {
		return this.field_12866;
	}

	public void method_11675(boolean bl) {
		this.field_12866 = bl;
	}

	public float method_13357() {
		return this.field_14841;
	}

	public void method_13338(float f) {
		this.field_14841 = f;
	}

	public long method_13358() {
		return this.field_14842;
	}

	public void method_13339(long l) {
		this.field_14842 = l;
	}

	public boolean method_11680() {
		if (this.field_12865 != StructureBlockEntity.class_2739.SAVE) {
			return false;
		} else {
			BlockPos blockPos = this.getPos();
			int i = 80;
			BlockPos blockPos2 = new BlockPos(blockPos.getX() - 80, 0, blockPos.getZ() - 80);
			BlockPos blockPos3 = new BlockPos(blockPos.getX() + 80, 255, blockPos.getZ() + 80);
			List<StructureBlockEntity> list = this.method_11671(blockPos2, blockPos3);
			List<StructureBlockEntity> list2 = this.method_11674(list);
			if (list2.size() < 1) {
				return false;
			} else {
				BlockBox blockBox = this.method_11672(blockPos, list2);
				if (blockBox.maxX - blockBox.minX > 1 && blockBox.maxY - blockBox.minY > 1 && blockBox.maxZ - blockBox.minZ > 1) {
					this.field_12861 = new BlockPos(blockBox.minX - blockPos.getX() + 1, blockBox.minY - blockPos.getY() + 1, blockBox.minZ - blockPos.getZ() + 1);
					this.field_12862 = new BlockPos(blockBox.maxX - blockBox.minX - 1, blockBox.maxY - blockBox.minY - 1, blockBox.maxZ - blockBox.minZ - 1);
					this.markDirty();
					BlockState blockState = this.world.getBlockState(blockPos);
					this.world.method_11481(blockPos, blockState, blockState, 3);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private List<StructureBlockEntity> method_11674(List<StructureBlockEntity> list) {
		Iterable<StructureBlockEntity> iterable = Iterables.filter(
			list,
			new Predicate<StructureBlockEntity>() {
				public boolean apply(@Nullable StructureBlockEntity structureBlockEntity) {
					return structureBlockEntity.field_12865 == StructureBlockEntity.class_2739.CORNER
						&& StructureBlockEntity.this.field_12858.equals(structureBlockEntity.field_12858);
				}
			}
		);
		return Lists.newArrayList(iterable);
	}

	private List<StructureBlockEntity> method_11671(BlockPos blockPos, BlockPos blockPos2) {
		List<StructureBlockEntity> list = Lists.newArrayList();

		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos, blockPos2)) {
			BlockState blockState = this.world.getBlockState(mutable);
			if (blockState.getBlock() == Blocks.STRUCTURE_BLOCK) {
				BlockEntity blockEntity = this.world.getBlockEntity(mutable);
				if (blockEntity != null && blockEntity instanceof StructureBlockEntity) {
					list.add((StructureBlockEntity)blockEntity);
				}
			}
		}

		return list;
	}

	private BlockBox method_11672(BlockPos blockPos, List<StructureBlockEntity> list) {
		BlockBox blockBox;
		if (list.size() > 1) {
			BlockPos blockPos2 = ((StructureBlockEntity)list.get(0)).getPos();
			blockBox = new BlockBox(blockPos2, blockPos2);
		} else {
			blockBox = new BlockBox(blockPos, blockPos);
		}

		for (StructureBlockEntity structureBlockEntity : list) {
			BlockPos blockPos3 = structureBlockEntity.getPos();
			if (blockPos3.getX() < blockBox.minX) {
				blockBox.minX = blockPos3.getX();
			} else if (blockPos3.getX() > blockBox.maxX) {
				blockBox.maxX = blockPos3.getX();
			}

			if (blockPos3.getY() < blockBox.minY) {
				blockBox.minY = blockPos3.getY();
			} else if (blockPos3.getY() > blockBox.maxY) {
				blockBox.maxY = blockPos3.getY();
			}

			if (blockPos3.getZ() < blockBox.minZ) {
				blockBox.minZ = blockPos3.getZ();
			} else if (blockPos3.getZ() > blockBox.maxZ) {
				blockBox.maxZ = blockPos3.getZ();
			}
		}

		return blockBox;
	}

	public void method_13340(ByteBuf byteBuf) {
		byteBuf.writeInt(this.pos.getX());
		byteBuf.writeInt(this.pos.getY());
		byteBuf.writeInt(this.pos.getZ());
	}

	public boolean method_11681() {
		return this.method_13343(true);
	}

	public boolean method_13343(boolean bl) {
		if (this.field_12865 == StructureBlockEntity.class_2739.SAVE && !this.world.isClient && !ChatUtil.isEmpty(this.field_12858)) {
			BlockPos blockPos = this.getPos().add(this.field_12861);
			ServerWorld serverWorld = (ServerWorld)this.world;
			MinecraftServer minecraftServer = this.world.getServer();
			class_2763 lv = serverWorld.method_12783();
			Structure structure = lv.method_11861(minecraftServer, new Identifier(this.field_12858));
			structure.saveFromWorld(this.world, blockPos, this.field_12862, !this.field_12866, Blocks.STRUCTURE_VOID);
			structure.setAuthor(this.field_12859);
			return !bl || lv.method_11863(minecraftServer, new Identifier(this.field_12858));
		} else {
			return false;
		}
	}

	public boolean method_11682() {
		return this.method_13344(true);
	}

	public boolean method_13344(boolean bl) {
		if (this.field_12865 == StructureBlockEntity.class_2739.LOAD && !this.world.isClient && !ChatUtil.isEmpty(this.field_12858)) {
			BlockPos blockPos = this.getPos();
			BlockPos blockPos2 = blockPos.add(this.field_12861);
			ServerWorld serverWorld = (ServerWorld)this.world;
			MinecraftServer minecraftServer = this.world.getServer();
			class_2763 lv = serverWorld.method_12783();
			Structure structure = lv.method_13384(minecraftServer, new Identifier(this.field_12858));
			if (structure == null) {
				return false;
			} else {
				if (!ChatUtil.isEmpty(structure.getAuthor())) {
					this.field_12859 = structure.getAuthor();
				}

				BlockPos blockPos3 = structure.getSize();
				boolean bl2 = this.field_12862.equals(blockPos3);
				if (!bl2) {
					this.field_12862 = blockPos3;
					this.markDirty();
					BlockState blockState = this.world.getBlockState(blockPos);
					this.world.method_11481(blockPos, blockState, blockState, 3);
				}

				if (bl && !bl2) {
					return false;
				} else {
					StructurePlacementData structurePlacementData = new StructurePlacementData()
						.method_11867(this.field_12863)
						.method_11868(this.field_12864)
						.method_11870(this.field_12866)
						.method_11865(null)
						.method_11866(null)
						.method_11873(false);
					if (this.field_14841 < 1.0F) {
						structurePlacementData.method_13385(MathHelper.clamp(this.field_14841, 0.0F, 1.0F)).method_13387(this.field_14842);
					}

					structure.method_11882(this.world, blockPos2, structurePlacementData);
					return true;
				}
			}
		} else {
			return false;
		}
	}

	public void method_13332() {
		ServerWorld serverWorld = (ServerWorld)this.world;
		class_2763 lv = serverWorld.method_12783();
		lv.method_13383(new Identifier(this.field_12858));
	}

	public boolean method_13333() {
		if (this.field_12865 == StructureBlockEntity.class_2739.LOAD && !this.world.isClient) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			MinecraftServer minecraftServer = this.world.getServer();
			class_2763 lv = serverWorld.method_12783();
			return lv.method_13384(minecraftServer, new Identifier(this.field_12858)) != null;
		} else {
			return false;
		}
	}

	public boolean method_13334() {
		return this.field_14838;
	}

	public void method_13346(boolean bl) {
		this.field_14838 = bl;
	}

	public boolean method_13335() {
		return this.field_14839;
	}

	public void method_13348(boolean bl) {
		this.field_14839 = bl;
	}

	public boolean method_13336() {
		return this.field_14840;
	}

	public void method_13349(boolean bl) {
		this.field_14840 = bl;
	}

	@Nullable
	@Override
	public Text getName() {
		return new TranslatableText(
			"structure_block.hover." + this.field_12865.field_12873, this.field_12865 == StructureBlockEntity.class_2739.DATA ? this.field_12860 : this.field_12858
		);
	}

	public static enum class_2739 implements StringIdentifiable {
		SAVE("save", 0),
		LOAD("load", 1),
		CORNER("corner", 2),
		DATA("data", 3);

		private static final StructureBlockEntity.class_2739[] field_12872 = new StructureBlockEntity.class_2739[values().length];
		private final String field_12873;
		private final int field_12874;

		private class_2739(String string2, int j) {
			this.field_12873 = string2;
			this.field_12874 = j;
		}

		@Override
		public String asString() {
			return this.field_12873;
		}

		public int method_11684() {
			return this.field_12874;
		}

		public static StructureBlockEntity.class_2739 method_11685(int i) {
			return i >= 0 && i < field_12872.length ? field_12872[i] : field_12872[0];
		}

		static {
			for (StructureBlockEntity.class_2739 lv : values()) {
				field_12872[lv.method_11684()] = lv;
			}
		}
	}
}
