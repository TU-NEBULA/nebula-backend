package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.request.UpdateStarOneRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StarCommandService {

    public CreateStarResponseDTO createFirstStar(Long userId, MultipartFile htmlFile, String title, String siteUrl);

    public PutStarResponseDTO createCompleteStar(Long userId, UUID starId, CreateStarRequestDTO requestDTO);

    public GetStarOneResponseDTO updateStar(Long userId, UUID starId, UpdateStarOneRequestDTO requestDTO);

    public DeleteStarResponseDTO deleteStar(Long userId, UUID starId);

    public DeleteStarResponseDTO cancelStar(UUID starId);

    public AddBookMarkResponseDTO addBookMark(MultipartFile htmlFile, String title, String siteUrl);
}
