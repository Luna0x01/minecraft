package net.minecraft.util;

public class TypedActionResult<T> {
	private final ActionResult result;
	private final T object;

	public TypedActionResult(ActionResult actionResult, T object) {
		this.result = actionResult;
		this.object = object;
	}

	public ActionResult getActionResult() {
		return this.result;
	}

	public T getObject() {
		return this.object;
	}
}
