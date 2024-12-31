package net.minecraft.block.material;

public class PlantMaterial extends Material {
	public PlantMaterial(MaterialColor materialColor) {
		super(materialColor);
		this.setCanBeBrokenInAdventureMode();
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
