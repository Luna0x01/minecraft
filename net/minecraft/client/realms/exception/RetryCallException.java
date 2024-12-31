package net.minecraft.client.realms.exception;

public class RetryCallException extends RealmsServiceException {
	public static final int DEFAULT_DELAY_SECONDS = 5;
	public final int delaySeconds;

	public RetryCallException(int delaySeconds, int httpResultCode) {
		super(httpResultCode, "Retry operation", -1, "");
		if (delaySeconds >= 0 && delaySeconds <= 120) {
			this.delaySeconds = delaySeconds;
		} else {
			this.delaySeconds = 5;
		}
	}
}
