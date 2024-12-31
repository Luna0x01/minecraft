package net.minecraft.block.material;

public class FluidMaterial extends Material {
	public FluidMaterial(MaterialColor materialColor) {
		super(materialColor);
		this.setReplaceable();
		this.setNoPushing();
	}

	@Override
	public boolean isFluid() {
		return true;
	}

	@Override
	public boolean blocksMovement() {
		return false;
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
