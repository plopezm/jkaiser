package com.aeox.jkaiser.core;

public interface Result<T> {
	T getResult();
	boolean wasError();
}
