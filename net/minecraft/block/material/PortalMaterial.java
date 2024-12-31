package net.minecraft.block.material;

public class PortalMaterial extends Material {
	public PortalMaterial(MaterialColor materialColor) {
		super(materialColor);
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean isTranslucent() {
		return false;
	}

	@Override
	public boolean blocksMovement() {
		return false;
	}
}
