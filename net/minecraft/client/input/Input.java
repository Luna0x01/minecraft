package net.minecraft.client.input;

import net.minecraft.util.math.Vec2f;

public class Input {
	public float movementSideways;
	public float movementForward;
	public boolean pressingForward;
	public boolean pressingBack;
	public boolean pressingLeft;
	public boolean pressingRight;
	public boolean jumping;
	public boolean sneaking;

	public void tick(boolean bl) {
	}

	public Vec2f getMovementInput() {
		return new Vec2f(this.movementSideways, this.movementForward);
	}

	public boolean hasForwardMovement() {
		return this.movementForward > 1.0E-5F;
	}
}
