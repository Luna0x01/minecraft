package net.minecraft.entity;

public interface JumpingMount {
	void setJumpStrength(int i);

	boolean canJump();

	void startJumping(int i);

	void stopJumping();
}
