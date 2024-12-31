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

	public void tick() {
	}

	public Vec2f method_13424() {
		return new Vec2f(this.movementSideways, this.movementForward);
	}
}
