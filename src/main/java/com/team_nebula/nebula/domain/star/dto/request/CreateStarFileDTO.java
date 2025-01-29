package com.team_nebula.nebula.domain.star.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateStarFileDTO {
    private MultipartFile thumbnailImage;
    private MultipartFile htmlFile;
    private CreateStarRequestDTO starRequestDTO;
}
