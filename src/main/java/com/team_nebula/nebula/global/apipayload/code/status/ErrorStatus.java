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

	_UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED,"USER4000","인증되지 않은 사용자입니다."),
	_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자가 없습니다."),

	_CATEGORY_ALREADY_EXIST(HttpStatus.CONFLICT, "CATEGORY4000", "이미 존재하는 카테고리입니다."),
	_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY4001", "카테고리를 찾을 수 없습니다."),

	_STAR_NOT_FOUND(HttpStatus.NOT_FOUND, "STAR4001", "해당 스타 정보를 찾을 수 없습니다."),
	_STAR_CREATION_FAILED(HttpStatus.CREATED, "STAR5000", "스타 생성에 실패했습니다."),

	_KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "KEYWORD4001", "해당 스타안에 키워드를 찾지 못했습니다."),
	_KEYWORD_NOT_INPUT(HttpStatus.NOT_ACCEPTABLE, "KEYWORD4002", "키워드가 입력되지 않았습니다."),

	_MULTIPARTFILE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"MULTIPARTFILE5000","MultipartFile -> File로 변환이 실패하였습니다."),

	_AI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5000","AI 서버에서 문제가 발생했습니다."),
	_AI_EXTRACT_DATA_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5001","AI에서 썸네일과 추천 키워드을 가져오지 못했습니다.");

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
