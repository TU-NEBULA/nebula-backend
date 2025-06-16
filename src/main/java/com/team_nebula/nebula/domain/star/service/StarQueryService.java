package com.team_nebula.nebula.domain.star.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.team_nebula.nebula.domain.star.dto.response.GetCategoryAndKeywordListDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetSearchedStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;

public interface StarQueryService {

	//    public CreateStarFileDTO starDataParsing(MultipartFile thumbnailImage, MultipartFile htmlFile, String starJsonData);

	public GetStarListResponseDTO getStarList(Long userId);

	public List<GetStarOneResponseDTO> findAllStar(Long userId);

	public GetStarOneResponseDTO getStarOne(UUID starId);

	public GetStarListResponseDTO getStarListInCategory(Long userId, UUID categoryId);

	public List<GetStarOneResponseDTO> findStarInCategory(Long userId, UUID categoryId);

	public GetStarListResponseDTO getStarListInKeyword(Long userId, String keywordId);

	public List<GetStarOneResponseDTO> findStarInKeyword(Long userId, String keywordId);

	public GetSearchedStarListResponseDTO searchStars(Long userId, String title);

	public Set<String> getStarUrls(List<String> urls, Long userId);

	public List<GetCategoryAndKeywordListDTO> getCategoryAndKeywordList(Long userId);
}
