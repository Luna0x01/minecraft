package net.minecraft.structure;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockBox;

public interface StructurePiecesHolder {
	void addPiece(StructurePiece piece);

	@Nullable
	StructurePiece getIntersecting(BlockBox box);
}
