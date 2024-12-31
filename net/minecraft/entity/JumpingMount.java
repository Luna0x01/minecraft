package net.minecraft.entity;

public interface JumpingMount {
	void setJumpStrength(int strength);

	boolean canJump();

	void startJumping(int height);

	void stopJumping();
}
