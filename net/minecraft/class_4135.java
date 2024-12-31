package net.minecraft;

public interface class_4135 extends class_4134 {
	int method_18472();

	int method_18474();

	void method_18473(int i, int j);

	boolean method_18475();

	float method_18476();

	default float method_18477() {
		return this.getBearingX();
	}

	default float method_18478() {
		return this.method_18477() + (float)this.method_18472() / this.method_18476();
	}

	default float method_18479() {
		return this.getBearingY();
	}

	default float method_18480() {
		return this.method_18479() + (float)this.method_18474() / this.method_18476();
	}

	default float getBearingY() {
		return 3.0F;
	}
}
