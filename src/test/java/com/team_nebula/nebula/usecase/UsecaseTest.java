package com.team_nebula.nebula.usecase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team_nebula.nebula.domain.history.dto.request.CreateHistoryRequestDTO;
import com.team_nebula.nebula.global.oauth.dto.TokenResponseDTO;
import com.team_nebula.nebula.global.oauth.service.CustomOAuth2UserService;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UsecaseTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	private Cookie accessTokenCookie;
	private Cookie refreshTokenCookie;

	@BeforeEach
	void setUp() {
		TokenResponseDTO tokenResponseDTO = customOAuth2UserService.generate();
		accessTokenCookie = new Cookie("accessToken", tokenResponseDTO.getAccessToken());
		refreshTokenCookie = new Cookie("refreshToken", tokenResponseDTO.getRefreshToken());
	}

	@Test
	@DisplayName("시나리오 1: 첫 번째 북마크 저장 및 조회")
	void testSaveFirstBookmarkIntegration() throws Exception {
		String title = "테스트 북마크";
		String siteUrl = "https://www.naver.com";

		MockMultipartFile htmlFile = new MockMultipartFile(
			"htmlFile",
			"bookmark.html",
			"text/html",
			"<html><body>북마크 테스트 페이지</body></html>".getBytes()
		);

		mockMvc.perform(multipart("/api/v2/stars")
				.file(htmlFile)
				.param("title", title)
				.param("siteUrl", siteUrl)
				.cookie(accessTokenCookie, refreshTokenCookie)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.title").value(title))
			.andExpect(jsonPath("$.result.siteUrl").value(siteUrl))
			.andExpect(jsonPath("$.result.keywords").isArray())
			.andDo(print());
	}

	@Test
	@DisplayName("시나리오 2: 방문기록 수집 및 히스토리 리스트 조회")
	void testHistoryScenario() throws Exception {
		List<CreateHistoryRequestDTO> requests = new ArrayList<>();
		requests.add(CreateHistoryRequestDTO.builder()
			.lastVisitTime(1746898245.0)
			.title("테스트 방문기록 1")
			.typedCount(1L)
			.url("https://example.com/1")
			.visitCount(1L)
			.build());
		requests.add(CreateHistoryRequestDTO.builder()
			.lastVisitTime(1746898246.0)
			.title("테스트 방문기록 2")
			.typedCount(1L)
			.url("https://example.com/2")
			.visitCount(1L)
			.build());

		mockMvc.perform(post("/api/v1/histories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requests))
				.cookie(accessTokenCookie, refreshTokenCookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andDo(print());

		mockMvc.perform(get("/api/v1/histories")
				.param("page", "0")
				.param("size", "10")
				.cookie(accessTokenCookie, refreshTokenCookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.content").isArray())
			.andExpect(jsonPath("$.result.content[0].url").exists())
			.andExpect(jsonPath("$.result.content[0].lastVisitTime").exists())
			.andExpect(jsonPath("$.result.content[0].title").exists())
			.andDo(print());
	}

	@Test
	@DisplayName("시나리오 4: 키워드 기반 연관 콘텐츠 탐색")
	void testKeywordBasedContentExploration() throws Exception {
		String keywordName = "string";

		mockMvc.perform(get("/api/v1/stars/keywords/{keywordId}", keywordName)
				.cookie(accessTokenCookie, refreshTokenCookie))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.type").value("Keyword"))
			.andExpect(jsonPath("$.result.starListDto").isArray())
			.andExpect(jsonPath("$.result.linkListDto").isArray())
			.andDo(print());
	}
}