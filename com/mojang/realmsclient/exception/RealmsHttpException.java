package com.mojang.realmsclient.exception;

public class RealmsHttpException extends RuntimeException {
	public RealmsHttpException(String string, Exception exception) {
		super(string, exception);
	}
}
