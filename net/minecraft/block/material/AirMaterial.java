package net.minecraft.block.material;

public class AirMaterial extends Material {
	public AirMaterial(MaterialColor materialColor) {
		super(materialColor);
		this.setReplaceable();
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
