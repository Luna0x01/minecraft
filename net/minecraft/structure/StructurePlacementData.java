package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class StructurePlacementData {
	private BlockMirror mirror = BlockMirror.NONE;
	private BlockRotation rotation = BlockRotation.NONE;
	private BlockPos position = BlockPos.ORIGIN;
	private boolean ignoreEntities;
	@Nullable
	private BlockBox boundingBox;
	private boolean placeFluids = true;
	@Nullable
	private Random random;
	@Nullable
	private int field_15575;
	private final List<StructureProcessor> processors = Lists.newArrayList();
	private boolean updateNeighbors;
	private boolean field_24043;

	public StructurePlacementData copy() {
		StructurePlacementData structurePlacementData = new StructurePlacementData();
		structurePlacementData.mirror = this.mirror;
		structurePlacementData.rotation = this.rotation;
		structurePlacementData.position = this.position;
		structurePlacementData.ignoreEntities = this.ignoreEntities;
		structurePlacementData.boundingBox = this.boundingBox;
		structurePlacementData.placeFluids = this.placeFluids;
		structurePlacementData.random = this.random;
		structurePlacementData.field_15575 = this.field_15575;
		structurePlacementData.processors.addAll(this.processors);
		structurePlacementData.updateNeighbors = this.updateNeighbors;
		structurePlacementData.field_24043 = this.field_24043;
		return structurePlacementData;
	}

	public StructurePlacementData setMirror(BlockMirror mirror) {
		this.mirror = mirror;
		return this;
	}

	public StructurePlacementData setRotation(BlockRotation rotation) {
		this.rotation = rotation;
		return this;
	}

	public StructurePlacementData setPosition(BlockPos position) {
		this.position = position;
		return this;
	}

	public StructurePlacementData setIgnoreEntities(boolean ignoreEntities) {
		this.ignoreEntities = ignoreEntities;
		return this;
	}

	public StructurePlacementData setBoundingBox(BlockBox boundingBox) {
		this.boundingBox = boundingBox;
		return this;
	}

	public StructurePlacementData setRandom(@Nullable Random random) {
		this.random = random;
		return this;
	}

	public StructurePlacementData setPlaceFluids(boolean placeFluids) {
		this.placeFluids = placeFluids;
		return this;
	}

	public StructurePlacementData setUpdateNeighbors(boolean updateNeighbors) {
		this.updateNeighbors = updateNeighbors;
		return this;
	}

	public StructurePlacementData clearProcessors() {
		this.processors.clear();
		return this;
	}

	public StructurePlacementData addProcessor(StructureProcessor processor) {
		this.processors.add(processor);
		return this;
	}

	public StructurePlacementData removeProcessor(StructureProcessor processor) {
		this.processors.remove(processor);
		return this;
	}

	public BlockMirror getMirror() {
		return this.mirror;
	}

	public BlockRotation getRotation() {
		return this.rotation;
	}

	public BlockPos getPosition() {
		return this.position;
	}

	public Random getRandom(@Nullable BlockPos pos) {
		if (this.random != null) {
			return this.random;
		} else {
			return pos == null ? new Random(Util.getMeasuringTimeMs()) : new Random(MathHelper.hashCode(pos));
		}
	}

	public boolean shouldIgnoreEntities() {
		return this.ignoreEntities;
	}

	@Nullable
	public BlockBox getBoundingBox() {
		return this.boundingBox;
	}

	public boolean shouldUpdateNeighbors() {
		return this.updateNeighbors;
	}

	public List<StructureProcessor> getProcessors() {
		return this.processors;
	}

	public boolean shouldPlaceFluids() {
		return this.placeFluids;
	}

	public Structure.PalettedBlockInfoList getRandomBlockInfos(List<Structure.PalettedBlockInfoList> list, @Nullable BlockPos pos) {
		int i = list.size();
		if (i == 0) {
			throw new IllegalStateException("No palettes");
		} else {
			return (Structure.PalettedBlockInfoList)list.get(this.getRandom(pos).nextInt(i));
		}
	}

	public StructurePlacementData method_27264(boolean bl) {
		this.field_24043 = bl;
		return this;
	}

	public boolean method_27265() {
		return this.field_24043;
	}
}
