package com.aeox.jkaiser.core;

public enum ParameterType {
	INTEGER("INTEGER"), FLOAT("FLOAT"), STRING("STRING"), BOOL("BOOL"), MAP("MAP");
	
	public final String label;
    private ParameterType(String label) {
        this.label = label;
    }
}
