package net.minecraft.advancement.criterion;

import net.minecraft.util.Identifier;

public class AbstractCriterionInstance implements CriterionInstance {
	private final Identifier criterion;

	public AbstractCriterionInstance(Identifier identifier) {
		this.criterion = identifier;
	}

	@Override
	public Identifier getCriterion() {
		return this.criterion;
	}

	public String toString() {
		return "AbstractCriterionInstance{criterion=" + this.criterion + '}';
	}
}
