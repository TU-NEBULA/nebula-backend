package com.team_nebula.nebula.domain.star.service;

import java.util.*;
import java.util.stream.Collectors;

import com.team_nebula.nebula.domain.star.dto.response.*;
import com.team_nebula.nebula.domain.star.repository.StarNeo4jRepositoryCustom;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.dto.response.SearchResultResponseDTO;
import com.team_nebula.nebula.domain.star.search.dto.response.SearchStarResponseDTO;
import com.team_nebula.nebula.domain.star.search.service.ElasticsearchService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.link.service.LinkQueryService;
import com.team_nebula.nebula.domain.star.converter.StarConverter;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StarQueryServiceImpl implements StarQueryService {

	private final StarRepository starRepository;
	private final LinkQueryService linkQueryService;
	private final StarNeo4jRepositoryCustom starNeo4jRepositoryCustom;
	private final ElasticsearchService elasticsearchService;

	// 스타 + 링크 전체 조회
	@Override
	public GetStarListResponseDTO getStarList(Long userId) {

		// 스타 전체 조회
		List<GetStarOneResponseDTO> stars = findAllStar(userId);

		// 링크 전체 조회
		List<GetLinkOneResponseDTO> links = linkQueryService.getAllLink(userId);

		return GetStarListResponseDTO.builder()
			.type("ALL")
			.totalStarCnt(stars.size())
			.totalLinkCnt(links.size())
			.starListDto(stars)
			.linkListDto(links)
			.build();
	}

	// 스타 노드 전체 조회
	@Override
	public List<GetStarOneResponseDTO> findAllStar(Long userId) {

		List<GetStarOneResponseDTO> starDataList = starRepository.findStarsByUserId(userId);

		return starDataList.stream()
			.map(StarConverter::convertToStarOneDto)
			.toList();
	}

	// 단일 스타 조회
	@Override
	public GetStarOneResponseDTO getStarOne(UUID starId) {
		GetStarOneResponseDTO data = starRepository.findStarDetailById(starId);

		if (data == null) {
			throw new GeneralException(ErrorStatus._STAR_NOT_FOUND);
		}

		return StarConverter.convertToStarOneDto(data);
	}

	// 카테고리별 스타 조회
	@Override
	public GetStarListResponseDTO getStarListInCategory(Long userId, UUID categoryId) {

		List<GetStarOneResponseDTO> starsInCategory = findStarInCategory(userId, categoryId);
		List<GetLinkOneResponseDTO> linksInCategory = linkQueryService.getLinkInCategory(userId, categoryId);

		return GetStarListResponseDTO.builder()
			.type("Category")
			.totalStarCnt(starsInCategory.size())
			.totalLinkCnt(linksInCategory.size())
			.starListDto(starsInCategory)
			.linkListDto(linksInCategory)
			.build();
	}

	@Override
	public List<GetStarOneResponseDTO> findStarInCategory(Long userId, UUID categoryId) {
		List<GetStarOneResponseDTO> starDataList = starRepository.findStarsInCategory(userId, categoryId);

		return starDataList.stream()
			.map(StarConverter::convertToStarOneDto)
			.toList();
	}

	@Override
	public GetStarListResponseDTO getStarListInKeyword(Long userId, String keywordId) {

		List<GetStarOneResponseDTO> starsInCategory = findStarInKeyword(userId, keywordId);
		List<GetLinkOneResponseDTO> linksInCategory = linkQueryService.getLinkInKeyword(userId, keywordId);

		return GetStarListResponseDTO.builder()
			.type("Keyword")
			.totalStarCnt(starsInCategory.size())
			.totalLinkCnt(linksInCategory.size())
			.starListDto(starsInCategory)
			.linkListDto(linksInCategory)
			.build();
	}

	@Override
	public List<GetStarOneResponseDTO> findStarInKeyword(Long userId, String keywordId) {
		List<GetStarOneResponseDTO> starDataList = starRepository.findStarsInKeyword(userId, keywordId);

		return starDataList.stream()
			.map(StarConverter::convertToStarOneDto)
			.toList();
	}

	@Override
	public GetSearchedStarListResponseDTO searchStars(Long userId, String title) {
		List<GetSearchedStarOneResponseDTO> queryResult = starRepository.searchStars(userId, title);
		return StarConverter.convertToStarListDto(queryResult);
	}

	@Override
	public Set<String> getStarUrls(List<String> urls, Long userId) {
		return new HashSet<>(starRepository.findStarUrlsByUrlsAndUserId(urls, userId));
	}

	@Override
	public List<GetCategoryAndKeywordListDTO> getCategoryAndKeywordList(Long userId) {
		List<GetCategoryKeywordStarRawDTO> rawData = starNeo4jRepositoryCustom.fetchRawCategoryKeywordStarData(userId);
		return StarConverter.convertToNestedDto(rawData);
	}

	@Override
	public SearchResultResponseDTO searchStarsV2(String keyword, Long userId, int page, int size) {
		// 최근 검색어 저장
		// saveRecentSearch(userId, keyword);

		ElasticsearchService.SearchResultsWithCount searchResults =
				elasticsearchService.searchStars(keyword, userId, page, size);

		// DTO 변환 (검색 점수 포함)
		List<SearchStarResponseDTO> starDTOs = searchResults.results().stream()
				.map(result -> StarConverter.convertToSearchStarDTO(result.document(), result.score()))
				.collect(Collectors.toList());

		long totalCount = searchResults.totalCount();
		int totalPages = (int) Math.ceil((double) totalCount / size);

		return SearchResultResponseDTO.builder()
				.stars(starDTOs)
				.totalCount(totalCount)
				.currentPage(page)
				.totalPages(totalPages)
				.hasNext(page < totalPages - 1)
				.build();
	}



	@Override
	public List<String> getAutoComplete(String query, Long userId, int size) {

		if (query == null || query.trim().isEmpty()) {
			return Collections.emptyList();
		}
		if (userId == null) {
			throw new GeneralException(ErrorStatus._USER_NOT_FOUND);
		}
		if (size <= 0 || size > 10) {
			throw new IllegalArgumentException("Size must be between 1 and 10");
		}
		return elasticsearchService.getAutoComplete(query, userId, size);

	}
}
