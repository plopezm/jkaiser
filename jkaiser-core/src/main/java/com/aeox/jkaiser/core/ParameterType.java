package com.aeox.jkaiser.core;

public enum ParameterType {
	INTEGER("INTEGER"), FLOAT("FLOAT"), STRING("STRING"), BOOL("BOOL"), STRING_MAP("STRING_MAP");
	
	public final String label;
    private ParameterType(String label) {
        this.label = label;
    }
}
