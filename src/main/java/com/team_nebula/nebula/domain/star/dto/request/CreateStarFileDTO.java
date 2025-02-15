package com.team_nebula.nebula.domain.star.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class CreateStarFileDTO {
    private MultipartFile htmlFile;
//    private CreateStarRequestDTO starRequestDTO;
    private String title;
    private String siteUrl;
}
