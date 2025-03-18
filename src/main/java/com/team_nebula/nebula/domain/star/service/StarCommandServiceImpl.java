package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.global.AI.dto.GetThumbnailAndKeywordsResponseDTO;
import com.team_nebula.nebula.global.AI.service.AiService;
import com.team_nebula.nebula.domain.category.service.CategoryCommandService;
import com.team_nebula.nebula.domain.category.service.CategoryQueryService;
import com.team_nebula.nebula.global.image.S3Service;
import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.keyword.service.KeywordCommandService;
import com.team_nebula.nebula.domain.link.service.LinkCommandService;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.request.UpdateStarOneRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.DeleteStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.PutStarResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StarCommandServiceImpl implements StarCommandService {

    private final StarRepository starRepository;
    private final UserNodeRepository userNodeRepository;
    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;
    private final KeywordCommandService keywordCommandService;
    private final LinkCommandService linkCommandService;
    private final S3Service s3Service;
    private final AiService aiService;

    @Override
    public CreateStarResponseDTO createFirstStar(Long userId, MultipartFile htmlFile, String title, String siteUrl){

        UserNode userNode = userNodeRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        String htmlFileKey = s3Service.saveHtmlFile(htmlFile, title);

        Star star = Star.builder()
                .title(title)
                .siteUrl(siteUrl)
                .htmlFileUrl(htmlFileKey)
                .build();

        Star savedStar = starRepository.save(star);
        if (savedStar.getId() == null) {
            throw new GeneralException(ErrorStatus._STAR_CREATION_FAILED);
        }

        try {
            // AI 기능 호출 (썸네일 및 추천 키워드 생성)
            GetThumbnailAndKeywordsResponseDTO responseDTO = aiService.analyzeHtmlFile(savedStar.getId(), userId, htmlFileKey);

            // 유저 노드와 관계 설정 후 저장
            userNode.getStars().add(savedStar);
            userNodeRepository.save(userNode);

            // 유저 스타 수정 횟수 증가
            aiService.checkUpdatedCnt(userId);

            return CreateStarResponseDTO.builder()
                    .starId(savedStar.getId())
                    .title(savedStar.getTitle())
                    .siteUrl(savedStar.getSiteUrl())
                    .thumbnailUrl(responseDTO.getImage_url())
                    .keywords(responseDTO.getKeywords())
                    .build();
        } catch (Exception e) {
            starRepository.delete(savedStar);
            throw new GeneralException(ErrorStatus._AI_SERVER_ERROR);
        }
    }



    @Override
    public PutStarResponseDTO createCompleteStar(UUID starId, CreateStarRequestDTO requestDTO){

        Star star = starRepository.findById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        // 카테고리 관계 설정
        categoryCommandService.linkStarToCategory(star, requestDTO.getCategoryName());

        // 키워드 생성 및 관계 설정
        keywordCommandService.linkStarToKeywords(star, requestDTO.getKeywordList());

        // 키워드+ 카테고리 포함된 스타를 다시 조회
        Star savedStar = starRepository.findById(star.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        // 스타 간 Link 노드 생성
        linkCommandService.createLinksForStar(savedStar);

        savedStar.updateStar(requestDTO.getThumbnailUrl(), requestDTO.getSummaryAI(), requestDTO.getUserMemo());

        return PutStarResponseDTO.builder()
                .starId(star.getId())
                .title(star.getTitle())
                .categoryName(requestDTO.getCategoryName())
                .keywordList(savedStar.getKeywords().stream()
                        .map(Keyword::getName)
                        .collect(Collectors.toList()))
                .build();
    }


    @Override
    public GetStarOneResponseDTO updateStar(Long userId, UUID starId, UpdateStarOneRequestDTO requestDTO){
        Star star = starRepository.findById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        Optional.ofNullable(requestDTO.getTitle()).ifPresent(star::updateTitle);
        Optional.ofNullable(requestDTO.getSummaryAI()).ifPresent(star::updateSummaryAI);
        Optional.ofNullable(requestDTO.getUserMemo()).ifPresent(star::updateUserMemo);
        Optional.ofNullable(requestDTO.getKeywords()).ifPresent(keywords ->
                keywordCommandService.updateKeywordsForStar(star, keywords));

        String categoryName = Optional.ofNullable(requestDTO.getCategoryName())
                .map(category -> categoryCommandService.linkStarToCategoryAndGetName(star, category))
                .orElseGet(() -> categoryQueryService.findCategoryNameByStar(star.getId()));

        Star updateStar = starRepository.save(star);

        // 유저 스타 작업 횟수 증가
        aiService.checkUpdatedCnt(userId);

        return GetStarOneResponseDTO.builder()
                .starId(updateStar.getId())
                .categoryName(categoryName)
                .title(updateStar.getTitle())
                .siteUrl(updateStar.getSiteUrl())
                .thumbnailUrl(updateStar.getThumbnailUrl())
                .summaryAI(updateStar.getSummaryAI())
                .userMemo(updateStar.getUserMemo())
                .views(updateStar.getViews())
                .keywordList(updateStar.getKeywords().stream()
                        .map(Keyword::getName)
                        .toList())
                .build();    
    }

    @Override
    public DeleteStarResponseDTO deleteStar(Long userId, UUID starId){
        Star star = starRepository.findById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        star.updateIsDeletedStatus();
        starRepository.save(star);

        // 유저 스타 작업 횟수 증가
        aiService.checkUpdatedCnt(userId);

        String deleteMessage = "Star with ID : " + starId + " was deleted";
        return DeleteStarResponseDTO.builder()
                .starId(starId)
                .deleteStatus(deleteMessage)
                .build();
    }

    @Override
    public DeleteStarResponseDTO cancelStar(UUID starId){
        Star star = starRepository.findById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        s3Service.deleteHtmlFileInS3(star.getHtmlFileUrl());
        starRepository.delete(star);

        String canceledMessage = "Star with ID : " + starId + " was completely deleted";
        return DeleteStarResponseDTO.builder()
                .starId(starId)
                .deleteStatus(canceledMessage)
                .build();
    }

}
