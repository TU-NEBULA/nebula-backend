package com.team_nebula.nebula.global.apipayload.exception;


import com.team_nebula.nebula.global.apipayload.code.BaseErrorCode;
import com.team_nebula.nebula.global.apipayload.code.ErrorReasonDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

	private BaseErrorCode code;

	public ErrorReasonDto getErrorReason() {
		return this.code.getReason();
	}

	public ErrorReasonDto getErrorReasonHttpStatus() {
		return this.code.getReasonHttpStatus();
	}
}
