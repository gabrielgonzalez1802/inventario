package com.developergg.app.enums;

public enum Status {
	ACTIVO(1),
	INACTIVO(0);
	
	private int status;

	Status(int status) {
		this.status  = status;
	}

	public int getStatus() {
		return status;
	}
}
