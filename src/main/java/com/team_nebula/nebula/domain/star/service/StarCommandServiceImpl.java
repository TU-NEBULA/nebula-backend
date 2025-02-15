package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.category.repository.CategoryRepository;
import com.team_nebula.nebula.domain.category.service.CategoryCommandService;
import com.team_nebula.nebula.domain.category.service.CategoryQueryService;
import com.team_nebula.nebula.domain.image.S3Service;
import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.keyword.service.KeywordCommandService;
import com.team_nebula.nebula.domain.link.service.LinkCommandService;
import com.team_nebula.nebula.domain.star.converter.StarConverter;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.request.UpdateStarOneRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.DeleteStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.PutStarResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StarCommandServiceImpl implements StarCommandService {

    private final StarRepository starRepository;
    private final CategoryRepository categoryRepository;
    private final UserNodeRepository userNodeRepository;
    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;
    private final KeywordCommandService keywordCommandService;
    private final LinkCommandService linkCommandService;
    private final S3Service s3Service;

    @Override
    public CreateStarResponseDTO createFirstStar(User user, MultipartFile htmlFile, String title, String siteUrl){
        UserNode userNode = userNodeRepository.findById(user.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        Star star = Star.builder()
                .title(title)
                .siteUrl(siteUrl)
                .htmlFileUrl(s3Service.saveHtmlFile(htmlFile, title))
                .build();

        Star savedStar = starRepository.save(star);
        if (savedStar.getId() == null) {
            throw new GeneralException(ErrorStatus._STAR_CREATION_FAILED);
        }

        // 유저 노드와 관계 설정 후 저장
        userNode.getStars().add(savedStar);
        userNodeRepository.save(userNode);

        return CreateStarResponseDTO.builder()
                .starId(savedStar.getId())
                .title(savedStar.getTitle())
                .build();
    }

//    @Override
//    public CreateStarResponseDTO createStar(User user, CreateStarFileDTO requestDTO){
//        CreateStarRequestDTO starRequestDTO = requestDTO.getStarRequestDTO();
//
//        UserNode userNode = userNodeRepository.findById(user.getId())
//                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
//
//        // 스타 생성 및 유저와 관계 설정
//        Star star = createStarEntity(requestDTO, userNode);
//
//        // 카테고리 관계 설정
//        categoryCommandService.linkStarToCategory(star, starRequestDTO.getCategoryName());
//
//        // 키워드 생성 및 관계 설정
//        keywordCommandService.linkStarToKeywords(star, starRequestDTO.getKeywordList());
//
//        // 키워드+ 카테고리 포함된 스타를 다시 조회
//        Star savedStar = starRepository.findById(star.getId())
//                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));
//
//        // 스타 간 Link 노드 생성
//        linkCommandService.createLinksForStar(savedStar);
//
//        return CreateStarResponseDTO.builder()
//                .starId(star.getId())
//                .title(star.getTitle())
//                .categoryName(starRequestDTO.getCategoryName())
//                .keywordList(savedStar.getKeywords().stream()
//                        .map(Keyword::getName)
//                        .collect(Collectors.toList()))
//                .build();
//
//    }

    @Override
    public PutStarResponseDTO putStar(UUID starId, CreateStarRequestDTO requestDTO){

        Star star = starRepository.findById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        System.out.println("카테고리 이름" + requestDTO.getCategoryName());
        // 카테고리 관계 설정
        categoryCommandService.linkStarToCategory(star, requestDTO.getCategoryName());

        // 키워드 생성 및 관계 설정
        keywordCommandService.linkStarToKeywords(star, requestDTO.getKeywordList());

        // 키워드+ 카테고리 포함된 스타를 다시 조회
        Star savedStar = starRepository.findById(star.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        // 스타 간 Link 노드 생성
        linkCommandService.createLinksForStar(savedStar);

        savedStar.updateStar(requestDTO.getThumbnailUrl(), requestDTO.getSummaryAI(), requestDTO.getUserMemo(), requestDTO.getEmbedding());

        return PutStarResponseDTO.builder()
                .starId(star.getId())
                .title(star.getTitle())
                .categoryName(requestDTO.getCategoryName())
                .keywordList(savedStar.getKeywords().stream()
                        .map(Keyword::getName)
                        .collect(Collectors.toList()))
                .build();
    }


//    public Star createStarEntity(CreateStarFileDTO requestDTO, UserNode userNode){
//        CreateStarRequestDTO starRequestDTO = requestDTO.getStarRequestDTO();
//
//        Star star = Star.builder()
//                .thumbnailUrl(s3Service.saveThumbnail(requestDTO.getThumbnailImage(), starRequestDTO.getTitle()))
//                .htmlFileUrl(s3Service.saveHtmlFile(requestDTO.getHtmlFile(), starRequestDTO.getTitle()))
//                .summaryAI(starRequestDTO.getSummaryAI())
//                .userMemo(starRequestDTO.getUserMemo())
//                .views(0)
//                .embedding(starRequestDTO.getEmbedding())
//                .build();
//
//        Star savedStar = starRepository.save(star);
//        if (savedStar.getId() == null) {
//            throw new GeneralException(ErrorStatus._STAR_CREATION_FAILED);
//        }
//
//        // 유저 노드와 관계 설정 후 저장
//        userNode.getStars().add(savedStar);
//        userNodeRepository.save(userNode);
//
//        return savedStar;
//    }

    public GetStarOneResponseDTO updateStar(UUID starId, UpdateStarOneRequestDTO requestDTO){
        Star star = starRepository.findById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        String categoryName = "";

        if(requestDTO.getTitle() != null){
            star.updateTitle(requestDTO.getTitle());
        }

        if(requestDTO.getCategoryName() != null){
            categoryName = categoryCommandService.linkStarToCategoryAndGetName(star, requestDTO.getCategoryName());
        }
        else{
            categoryName = categoryQueryService.findCategoryNameByStar(star.getId());
        }

        if(requestDTO.getSummaryAI() != null){
            star.updateSummaryAI(requestDTO.getSummaryAI());
        }

        if(requestDTO.getUserMemo() != null){
            star.updateUserMemo(requestDTO.getUserMemo());
        }

        Star updateStar = starRepository.save(star);

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
    public DeleteStarResponseDTO deleteStar(UUID starId){
        Star star = starRepository.findById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        star.updateIsDeletedStatus();
        starRepository.save(star);

        String deleteMessage = "Star with ID : " + starId + " was deleted";
        return DeleteStarResponseDTO.builder()
                .starId(starId)
                .deleteStatus(deleteMessage)
                .build();
    }

}
