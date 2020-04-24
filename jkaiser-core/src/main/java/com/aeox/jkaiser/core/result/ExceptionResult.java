package com.aeox.jkaiser.core.result;

import com.aeox.jkaiser.core.Result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResult implements Result<String> {

	private String resultMsg;
	
	@Override
	public String getResult() {
		return this.resultMsg;
	}

	@Override
	public boolean wasError() {
		return true;
	}

}
