package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.category.service.CategoryCommandService;
import com.team_nebula.nebula.domain.image.S3Service;
import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.keyword.service.KeywordCommandService;
import com.team_nebula.nebula.domain.link.service.LinkCommandService;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
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

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StarCommandServiceImpl implements StarCommandService {

    private final StarRepository starRepository;
    private final UserNodeRepository userNodeRepository;
    private final CategoryCommandService categoryCommandService;
    private final KeywordCommandService keywordCommandService;
    private final LinkCommandService linkCommandService;
    private final S3Service s3Service;

    @Override
    public CreateStarResponseDTO createStar(User user, CreateStarFileDTO requestDTO){
        CreateStarRequestDTO starRequestDTO = requestDTO.getStarRequestDTO();

        UserNode userNode = userNodeRepository.findById(user.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        // 스타 생성 및 유저와 관계 설정
        Star star = createStarEntity(requestDTO, userNode);

        // 카테고리 관계 설정
        categoryCommandService.linkStarToCategory(star, starRequestDTO.getCategoryName());

        // 키워드 생성 및 관계 설정
        Star savedStar = keywordCommandService.linkStarToKeywords(star, starRequestDTO.getKeywordList());
        if(savedStar.getId() == null) {
            throw new GeneralException(ErrorStatus._STAR_NOT_FOUND);
        }

        // 스타 간 Link 노드 생성
        linkCommandService.createLinksForStar(savedStar);

        return CreateStarResponseDTO.builder()
                .starId(star.getId())
                .title(star.getTitle())
                .categoryName(starRequestDTO.getCategoryName())
                .keywordList(savedStar.getKeywords().stream()
                        .map(Keyword::getName)
                        .collect(Collectors.toList()))
                .build();

    }

    public Star createStarEntity(CreateStarFileDTO requestDTO, UserNode userNode){
        CreateStarRequestDTO starRequestDTO = requestDTO.getStarRequestDTO();

        Star star = Star.builder()
                .title(starRequestDTO.getTitle())
                .siteUrl(starRequestDTO.getSiteUrl())
                .thumbnailUrl(s3Service.saveThumbnail(requestDTO.getThumbnailImage(), starRequestDTO.getTitle()))
                .htmlFileUrl(s3Service.saveHtmlFile(requestDTO.getHtmlFile(), starRequestDTO.getTitle()))
                .summaryAI(starRequestDTO.getSummaryAI())
                .userMemo(starRequestDTO.getUserMemo())
                .views(0)
                .embedding(starRequestDTO.getEmbedding())
                .build();

        Star savedStar = starRepository.save(star);
        if (savedStar.getId() == null) {
            throw new GeneralException(ErrorStatus._STAR_CREATION_FAILED);
        }

        // 유저 노드와 관계 설정 후 저장
        userNode.getStars().add(savedStar);
        userNodeRepository.save(userNode);

        return savedStar;
    }

}
