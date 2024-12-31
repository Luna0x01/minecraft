package net.minecraft.entity.ai.goal;

public abstract class Goal {
	private int categoryBits;

	public abstract boolean canStart();

	public boolean shouldContinue() {
		return this.canStart();
	}

	public boolean canStop() {
		return true;
	}

	public void start() {
	}

	public void stop() {
	}

	public void tick() {
	}

	public void setCategoryBits(int newBits) {
		this.categoryBits = newBits;
	}

	public int getCategoryBits() {
		return this.categoryBits;
	}
}
