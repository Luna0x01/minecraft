package com.mojang.realmsclient.exception;

public class RetryCallException extends RealmsServiceException {
	public final int delaySeconds;

	public RetryCallException(int i) {
		super(503, "Retry operation", -1, "");
		if (i >= 0 && i <= 120) {
			this.delaySeconds = i;
		} else {
			this.delaySeconds = 5;
		}
	}
}
