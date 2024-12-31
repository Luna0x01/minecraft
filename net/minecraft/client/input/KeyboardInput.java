package net.minecraft.client.input;

import net.minecraft.client.option.GameOptions;

public class KeyboardInput extends Input {
	private final GameOptions options;

	public KeyboardInput(GameOptions gameOptions) {
		this.options = gameOptions;
	}

	@Override
	public void tick() {
		this.movementSideways = 0.0F;
		this.movementForward = 0.0F;
		if (this.options.forwardKey.isPressed()) {
			this.movementForward++;
			this.pressingForward = true;
		} else {
			this.pressingForward = false;
		}

		if (this.options.backKey.isPressed()) {
			this.movementForward--;
			this.pressingBack = true;
		} else {
			this.pressingBack = false;
		}

		if (this.options.leftKey.isPressed()) {
			this.movementSideways++;
			this.pressingLeft = true;
		} else {
			this.pressingLeft = false;
		}

		if (this.options.rightKey.isPressed()) {
			this.movementSideways--;
			this.pressingRight = true;
		} else {
			this.pressingRight = false;
		}

		this.jumping = this.options.jumpKey.isPressed();
		this.sneaking = this.options.sneakKey.isPressed();
		if (this.sneaking) {
			this.movementSideways = (float)((double)this.movementSideways * 0.3);
			this.movementForward = (float)((double)this.movementForward * 0.3);
		}
	}
}
