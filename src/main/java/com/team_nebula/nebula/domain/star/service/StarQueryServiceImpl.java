package com.team_nebula.nebula.domain.star.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team_nebula.nebula.domain.category.repository.CategoryRepository;
import com.team_nebula.nebula.domain.keyword.repository.KeywordRepository;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StarQueryServiceImpl implements StarQueryService {

    private final StarRepository starRepository;
    private final UserNodeRepository userNodeRepository;
    private final KeywordRepository keywordRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public CreateStarFileDTO starDataParsing(MultipartFile thumbnailImage, MultipartFile htmlFile, String starJsonData){

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // starJsonData 파싱
        CreateStarRequestDTO request;
        try {
            request = objectMapper.readValue(starJsonData, CreateStarRequestDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid JSON format for starJsonData.");
        }

        CreateStarFileDTO requestDTO = new CreateStarFileDTO();
        requestDTO.setThumbnailImage(thumbnailImage);
        requestDTO.setHtmlFile(htmlFile);
        requestDTO.setStarRequestDTO(request);

        return requestDTO;
    }

}
