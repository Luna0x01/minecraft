package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class StructureBlockBlockEntity extends BlockEntity {
	private Identifier structureName;
	private String author = "";
	private String metadata = "";
	private BlockPos offset = new BlockPos(0, 1, 0);
	private BlockPos size = BlockPos.ORIGIN;
	private BlockMirror mirror = BlockMirror.field_11302;
	private BlockRotation rotation = BlockRotation.field_11467;
	private StructureBlockMode mode = StructureBlockMode.field_12696;
	private boolean ignoreEntities = true;
	private boolean powered;
	private boolean showAir;
	private boolean showBoundingBox = true;
	private float integrity = 1.0F;
	private long seed;

	public StructureBlockBlockEntity() {
		super(BlockEntityType.field_11895);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putString("name", this.getStructureName());
		compoundTag.putString("author", this.author);
		compoundTag.putString("metadata", this.metadata);
		compoundTag.putInt("posX", this.offset.getX());
		compoundTag.putInt("posY", this.offset.getY());
		compoundTag.putInt("posZ", this.offset.getZ());
		compoundTag.putInt("sizeX", this.size.getX());
		compoundTag.putInt("sizeY", this.size.getY());
		compoundTag.putInt("sizeZ", this.size.getZ());
		compoundTag.putString("rotation", this.rotation.toString());
		compoundTag.putString("mirror", this.mirror.toString());
		compoundTag.putString("mode", this.mode.toString());
		compoundTag.putBoolean("ignoreEntities", this.ignoreEntities);
		compoundTag.putBoolean("powered", this.powered);
		compoundTag.putBoolean("showair", this.showAir);
		compoundTag.putBoolean("showboundingbox", this.showBoundingBox);
		compoundTag.putFloat("integrity", this.integrity);
		compoundTag.putLong("seed", this.seed);
		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.setStructureName(compoundTag.getString("name"));
		this.author = compoundTag.getString("author");
		this.metadata = compoundTag.getString("metadata");
		int i = MathHelper.clamp(compoundTag.getInt("posX"), -32, 32);
		int j = MathHelper.clamp(compoundTag.getInt("posY"), -32, 32);
		int k = MathHelper.clamp(compoundTag.getInt("posZ"), -32, 32);
		this.offset = new BlockPos(i, j, k);
		int l = MathHelper.clamp(compoundTag.getInt("sizeX"), 0, 32);
		int m = MathHelper.clamp(compoundTag.getInt("sizeY"), 0, 32);
		int n = MathHelper.clamp(compoundTag.getInt("sizeZ"), 0, 32);
		this.size = new BlockPos(l, m, n);

		try {
			this.rotation = BlockRotation.valueOf(compoundTag.getString("rotation"));
		} catch (IllegalArgumentException var11) {
			this.rotation = BlockRotation.field_11467;
		}

		try {
			this.mirror = BlockMirror.valueOf(compoundTag.getString("mirror"));
		} catch (IllegalArgumentException var10) {
			this.mirror = BlockMirror.field_11302;
		}

		try {
			this.mode = StructureBlockMode.valueOf(compoundTag.getString("mode"));
		} catch (IllegalArgumentException var9) {
			this.mode = StructureBlockMode.field_12696;
		}

		this.ignoreEntities = compoundTag.getBoolean("ignoreEntities");
		this.powered = compoundTag.getBoolean("powered");
		this.showAir = compoundTag.getBoolean("showair");
		this.showBoundingBox = compoundTag.getBoolean("showboundingbox");
		if (compoundTag.contains("integrity")) {
			this.integrity = compoundTag.getFloat("integrity");
		} else {
			this.integrity = 1.0F;
		}

		this.seed = compoundTag.getLong("seed");
		this.updateBlockMode();
	}

	private void updateBlockMode() {
		if (this.world != null) {
			BlockPos blockPos = this.getPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.field_10465) {
				this.world.setBlockState(blockPos, blockState.with(StructureBlock.MODE, this.mode), 2);
			}
		}
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 7, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	public boolean openScreen(PlayerEntity playerEntity) {
		if (!playerEntity.isCreativeLevelTwoOp()) {
			return false;
		} else {
			if (playerEntity.getEntityWorld().isClient) {
				playerEntity.openStructureBlockScreen(this);
			}

			return true;
		}
	}

	public String getStructureName() {
		return this.structureName == null ? "" : this.structureName.toString();
	}

	public String getStructurePath() {
		return this.structureName == null ? "" : this.structureName.getPath();
	}

	public boolean hasStructureName() {
		return this.structureName != null;
	}

	public void setStructureName(@Nullable String string) {
		this.setStructureName(ChatUtil.isEmpty(string) ? null : Identifier.tryParse(string));
	}

	public void setStructureName(@Nullable Identifier identifier) {
		this.structureName = identifier;
	}

	public void setAuthor(LivingEntity livingEntity) {
		this.author = livingEntity.getName().getString();
	}

	public BlockPos getOffset() {
		return this.offset;
	}

	public void setOffset(BlockPos blockPos) {
		this.offset = blockPos;
	}

	public BlockPos getSize() {
		return this.size;
	}

	public void setSize(BlockPos blockPos) {
		this.size = blockPos;
	}

	public BlockMirror getMirror() {
		return this.mirror;
	}

	public void setMirror(BlockMirror blockMirror) {
		this.mirror = blockMirror;
	}

	public BlockRotation getRotation() {
		return this.rotation;
	}

	public void setRotation(BlockRotation blockRotation) {
		this.rotation = blockRotation;
	}

	public String getMetadata() {
		return this.metadata;
	}

	public void setMetadata(String string) {
		this.metadata = string;
	}

	public StructureBlockMode getMode() {
		return this.mode;
	}

	public void setMode(StructureBlockMode structureBlockMode) {
		this.mode = structureBlockMode;
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.getBlock() == Blocks.field_10465) {
			this.world.setBlockState(this.getPos(), blockState.with(StructureBlock.MODE, structureBlockMode), 2);
		}
	}

	public void cycleMode() {
		switch (this.getMode()) {
			case field_12695:
				this.setMode(StructureBlockMode.field_12697);
				break;
			case field_12697:
				this.setMode(StructureBlockMode.field_12699);
				break;
			case field_12699:
				this.setMode(StructureBlockMode.field_12696);
				break;
			case field_12696:
				this.setMode(StructureBlockMode.field_12695);
		}
	}

	public boolean shouldIgnoreEntities() {
		return this.ignoreEntities;
	}

	public void setIgnoreEntities(boolean bl) {
		this.ignoreEntities = bl;
	}

	public float getIntegrity() {
		return this.integrity;
	}

	public void setIntegrity(float f) {
		this.integrity = f;
	}

	public long getSeed() {
		return this.seed;
	}

	public void setSeed(long l) {
		this.seed = l;
	}

	public boolean detectStructureSize() {
		if (this.mode != StructureBlockMode.field_12695) {
			return false;
		} else {
			BlockPos blockPos = this.getPos();
			int i = 80;
			BlockPos blockPos2 = new BlockPos(blockPos.getX() - 80, 0, blockPos.getZ() - 80);
			BlockPos blockPos3 = new BlockPos(blockPos.getX() + 80, 255, blockPos.getZ() + 80);
			List<StructureBlockBlockEntity> list = this.findStructureBlockEntities(blockPos2, blockPos3);
			List<StructureBlockBlockEntity> list2 = this.findCorners(list);
			if (list2.size() < 1) {
				return false;
			} else {
				BlockBox blockBox = this.makeBoundingBox(blockPos, list2);
				if (blockBox.maxX - blockBox.minX > 1 && blockBox.maxY - blockBox.minY > 1 && blockBox.maxZ - blockBox.minZ > 1) {
					this.offset = new BlockPos(blockBox.minX - blockPos.getX() + 1, blockBox.minY - blockPos.getY() + 1, blockBox.minZ - blockPos.getZ() + 1);
					this.size = new BlockPos(blockBox.maxX - blockBox.minX - 1, blockBox.maxY - blockBox.minY - 1, blockBox.maxZ - blockBox.minZ - 1);
					this.markDirty();
					BlockState blockState = this.world.getBlockState(blockPos);
					this.world.updateListeners(blockPos, blockState, blockState, 3);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private List<StructureBlockBlockEntity> findCorners(List<StructureBlockBlockEntity> list) {
		Predicate<StructureBlockBlockEntity> predicate = structureBlockBlockEntity -> structureBlockBlockEntity.mode == StructureBlockMode.field_12699
				&& Objects.equals(this.structureName, structureBlockBlockEntity.structureName);
		return (List<StructureBlockBlockEntity>)list.stream().filter(predicate).collect(Collectors.toList());
	}

	private List<StructureBlockBlockEntity> findStructureBlockEntities(BlockPos blockPos, BlockPos blockPos2) {
		List<StructureBlockBlockEntity> list = Lists.newArrayList();

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
			BlockState blockState = this.world.getBlockState(blockPos3);
			if (blockState.getBlock() == Blocks.field_10465) {
				BlockEntity blockEntity = this.world.getBlockEntity(blockPos3);
				if (blockEntity != null && blockEntity instanceof StructureBlockBlockEntity) {
					list.add((StructureBlockBlockEntity)blockEntity);
				}
			}
		}

		return list;
	}

	private BlockBox makeBoundingBox(BlockPos blockPos, List<StructureBlockBlockEntity> list) {
		BlockBox blockBox;
		if (list.size() > 1) {
			BlockPos blockPos2 = ((StructureBlockBlockEntity)list.get(0)).getPos();
			blockBox = new BlockBox(blockPos2, blockPos2);
		} else {
			blockBox = new BlockBox(blockPos, blockPos);
		}

		for (StructureBlockBlockEntity structureBlockBlockEntity : list) {
			BlockPos blockPos3 = structureBlockBlockEntity.getPos();
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

	public boolean saveStructure() {
		return this.saveStructure(true);
	}

	public boolean saveStructure(boolean bl) {
		if (this.mode == StructureBlockMode.field_12695 && !this.world.isClient && this.structureName != null) {
			BlockPos blockPos = this.getPos().add(this.offset);
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.getStructureManager();

			Structure structure;
			try {
				structure = structureManager.getStructureOrBlank(this.structureName);
			} catch (InvalidIdentifierException var8) {
				return false;
			}

			structure.method_15174(this.world, blockPos, this.size, !this.ignoreEntities, Blocks.field_10369);
			structure.setAuthor(this.author);
			if (bl) {
				try {
					return structureManager.saveStructure(this.structureName);
				} catch (InvalidIdentifierException var7) {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean loadStructure() {
		return this.loadStructure(true);
	}

	private static Random createRandom(long l) {
		return l == 0L ? new Random(Util.getMeasuringTimeMs()) : new Random(l);
	}

	public boolean loadStructure(boolean bl) {
		if (this.mode == StructureBlockMode.field_12697 && !this.world.isClient && this.structureName != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.getStructureManager();

			Structure structure;
			try {
				structure = structureManager.getStructure(this.structureName);
			} catch (InvalidIdentifierException var6) {
				return false;
			}

			return structure == null ? false : this.place(bl, structure);
		} else {
			return false;
		}
	}

	public boolean place(boolean bl, Structure structure) {
		BlockPos blockPos = this.getPos();
		if (!ChatUtil.isEmpty(structure.getAuthor())) {
			this.author = structure.getAuthor();
		}

		BlockPos blockPos2 = structure.getSize();
		boolean bl2 = this.size.equals(blockPos2);
		if (!bl2) {
			this.size = blockPos2;
			this.markDirty();
			BlockState blockState = this.world.getBlockState(blockPos);
			this.world.updateListeners(blockPos, blockState, blockState, 3);
		}

		if (bl && !bl2) {
			return false;
		} else {
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setMirrored(this.mirror)
				.setRotation(this.rotation)
				.setIgnoreEntities(this.ignoreEntities)
				.setChunkPosition(null);
			if (this.integrity < 1.0F) {
				structurePlacementData.clearProcessors()
					.addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0F, 1.0F)))
					.setRandom(createRandom(this.seed));
			}

			BlockPos blockPos3 = blockPos.add(this.offset);
			structure.place(this.world, blockPos3, structurePlacementData);
			return true;
		}
	}

	public void unloadStructure() {
		if (this.structureName != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.getStructureManager();
			structureManager.unloadStructure(this.structureName);
		}
	}

	public boolean isStructureAvailable() {
		if (this.mode == StructureBlockMode.field_12697 && !this.world.isClient && this.structureName != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.getStructureManager();

			try {
				return structureManager.getStructure(this.structureName) != null;
			} catch (InvalidIdentifierException var4) {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isPowered() {
		return this.powered;
	}

	public void setPowered(boolean bl) {
		this.powered = bl;
	}

	public boolean shouldShowAir() {
		return this.showAir;
	}

	public void setShowAir(boolean bl) {
		this.showAir = bl;
	}

	public boolean shouldShowBoundingBox() {
		return this.showBoundingBox;
	}

	public void setShowBoundingBox(boolean bl) {
		this.showBoundingBox = bl;
	}

	public static enum Action {
		field_12108,
		field_12110,
		field_12109,
		field_12106;
	}
}
