package com.team_nebula.nebula.domain.star.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class CreateStarFileDTO {
    private MultipartFile thumbnailImage;
    private MultipartFile htmlFile;
    private CreateStarRequestDTO starRequestDTO;
}
