package com.mojang.realmsclient.dto;

public class RealmsDescriptionDto extends ValueObject {
	public String name;
	public String description;

	public RealmsDescriptionDto(String string, String string2) {
		this.name = string;
		this.description = string2;
	}
}
