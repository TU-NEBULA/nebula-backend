package com.team_nebula.nebula.global.apipayload.code.status;

import org.springframework.http.HttpStatus;

import com.team_nebula.nebula.global.apipayload.code.BaseErrorCode;
import com.team_nebula.nebula.global.apipayload.code.ErrorReasonDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러. 관리자에게 문의하세요."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청"),
	_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자가 없습니다.");

	private HttpStatus httpStatus;
	private String code;
	private String message;

	@Override
	public ErrorReasonDto getReason() {
		return ErrorReasonDto.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}

	@Override
	public ErrorReasonDto getReasonHttpStatus() {
		return ErrorReasonDto.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build();
	}
}
