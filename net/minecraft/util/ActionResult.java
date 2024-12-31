package net.minecraft.util;

public enum ActionResult {
	field_5812,
	field_21466,
	field_5811,
	field_5814;

	public boolean isAccepted() {
		return this == field_5812 || this == field_21466;
	}

	public boolean shouldSwingHand() {
		return this == field_5812;
	}
}
