package net.minecraft.client.util;

public class GlException extends RuntimeException {
	public GlException(String message) {
		super(message);
	}

	public GlException(String message, Throwable cause) {
		super(message, cause);
	}
}
