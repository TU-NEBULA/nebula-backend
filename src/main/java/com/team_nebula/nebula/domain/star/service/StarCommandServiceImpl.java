package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.favicon.entity.Favicon;
import com.team_nebula.nebula.domain.favicon.repository.FaviconRepository;
import com.team_nebula.nebula.domain.favicon.service.FaviconService;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.*;
import com.team_nebula.nebula.global.AI.dto.GetThumbnailAndKeywordsResponseDTO;
import com.team_nebula.nebula.global.AI.service.AiMessageService;
import com.team_nebula.nebula.global.AI.service.AiService;
import com.team_nebula.nebula.domain.category.service.CategoryCommandService;
import com.team_nebula.nebula.domain.category.service.CategoryQueryService;
import com.team_nebula.nebula.global.image.S3Service;
import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.keyword.service.KeywordCommandService;
import com.team_nebula.nebula.domain.link.service.LinkCommandService;
import com.team_nebula.nebula.domain.star.dto.request.CompleteStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.request.UpdateStarOneRequestDTO;
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
    private final FaviconService faviconService;
    private final S3Service s3Service;
    private final AiService aiService;
    private final AiMessageService aiMessageService;
    private final FaviconRepository faviconRepository;

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

        Favicon favicon = faviconService.getOrCreateFavicon(siteUrl);

        savedStar.getFavicons().add(favicon);


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
                    .faviconUrl(favicon.getFaviconUrl())
                    .keywordList(responseDTO.getKeywords())
                    .build();
        } catch (Exception e) {
            starRepository.delete(savedStar);
            throw new GeneralException(ErrorStatus._AI_SERVER_ERROR);
        }
    }



    @Override
    public PutStarResponseDTO createCompleteStar(Long userId, UUID starId, CompleteStarRequestDTO requestDTO){

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
        linkCommandService.createLinksForStar(userId, savedStar);

        savedStar.updateStar(requestDTO.getThumbnailUrl(), requestDTO.getSummaryAI(), requestDTO.getUserMemo());
        starRepository.save(savedStar);

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

        // 카테고리 업데이트. 만약 수정되지 않으면 기존 카테고리 이름 반환
        String categoryName = Optional.ofNullable(requestDTO.getCategoryName())
                .map(category -> categoryCommandService.linkStarToCategoryAndGetName(star, category))
                .orElseGet(() -> categoryQueryService.findCategoryNameByStar(star.getId()));

        // 키워드 업데이트 -> 링크 재설정
        Optional.ofNullable(requestDTO.getKeywordList()).ifPresent(keywords ->
                keywordCommandService.updateKeywordsForStar(userId, star, keywords));


        // 업데이트된 최신 스타객체 조회
        Star latestStar = starRepository.findById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        // title, AI요약, 사용자 메모 업데이트
        Optional.ofNullable(requestDTO.getTitle()).ifPresent(latestStar::updateTitle);
        Optional.ofNullable(requestDTO.getSummaryAI()).ifPresent(latestStar::updateSummaryAI);
        Optional.ofNullable(requestDTO.getUserMemo()).ifPresent(latestStar::updateUserMemo);

        // DB에 저장
        Star updatedStar = starRepository.save(latestStar);

        // 유저 스타 작업 횟수 증가
//        aiService.checkUpdatedCnt(userId);

        return GetStarOneResponseDTO.builder()
                .starId(updatedStar.getId())
                .categoryName(categoryName)
                .title(updatedStar.getTitle())
                .siteUrl(updatedStar.getSiteUrl())
                .thumbnailUrl(updatedStar.getThumbnailUrl())
                .summaryAI(updatedStar.getSummaryAI())
                .userMemo(updatedStar.getUserMemo())
                .views(updatedStar.getViews())
                .faviconUrl(updatedStar.getFavicons().toString())
                .keywordList(updatedStar.getKeywords().stream()
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

    @Override
    public AddBookMarkResponseDTO addBookMark(Long userId, MultipartFile htmlFile, String title, String siteUrl){
        String htmlFileKey = s3Service.saveHtmlFile(htmlFile, title);

        Favicon favicon = faviconService.getOrCreateFavicon(siteUrl);

        GetThumbnailAndKeywordsResponseDTO responseDTO = aiMessageService.analyzeHtmlFile(userId, htmlFileKey);

        return AddBookMarkResponseDTO.builder()
                .title(title)
                .siteUrl(siteUrl)
                .faviconUrl(favicon.getFaviconUrl())
                .thumbnailUrl(responseDTO.getImage_url())
                .keywords(responseDTO.getKeywords())
                .s3key(htmlFileKey)
                .build();
    }

    @Override
    public CreateStarResponseDTO createStar(Long userId, CreateStarRequestDTO requestDTO){
        // 유저 찾기
        UserNode userNode = userNodeRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        // 스타 노드 생성
        Star star = Star.builder()
                .title(requestDTO.getTitle())
                .siteUrl(requestDTO.getSiteUrl())
                .thumbnailUrl(requestDTO.getThumbnailUrl())
                .summaryAI(requestDTO.getSummaryAI())
                .userMemo(requestDTO.getUserMemo())
                .build();

        Star savedStar = starRepository.save(star);
        if (savedStar.getId() == null) {
            throw new GeneralException(ErrorStatus._STAR_CREATION_FAILED);
        }

        // 유저-스타 관계 설정
        userNode.getStars().add(savedStar);
        userNodeRepository.save(userNode);

        // 카테고리 생성 및 유저-카테고리 관게설정
        categoryCommandService.linkStarToCategory(star, requestDTO.getCategoryName());

        // 스타-파비콘 관계 설정
        Favicon favicon = faviconRepository.findByFaviconUrl(requestDTO.getFaviconUrl());
        savedStar.getFavicons().add(favicon);
        Star updatedStar = starRepository.save(savedStar);

        // 키워드 노드 생성 및 스타-키워드 관계설정
        keywordCommandService.linkStarToKeywords(updatedStar, requestDTO.getKeywordList());

        // 스타간 링크 노드 생성 및 관계설정
        Star lastStar = starRepository.findById(updatedStar.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));
        linkCommandService.createLinksForStar(userId, lastStar);

        starRepository.save(lastStar);

        // 유저 스타 작업 횟수 증가
//        aiService.checkUpdatedCnt(userId);

        return CreateStarResponseDTO.builder()
                .starId(lastStar.getId())
                .title(lastStar.getTitle())
                .siteUrl(lastStar.getSiteUrl())
                .thumbnailUrl(lastStar.getThumbnailUrl())
                .faviconUrl(favicon.getFaviconUrl())
                .keywordList(lastStar.getKeywords().stream()
                        .map(Keyword::getName)
                        .toList())
                .build();
    }


}
