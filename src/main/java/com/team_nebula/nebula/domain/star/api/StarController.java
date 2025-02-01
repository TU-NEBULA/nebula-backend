package com.team_nebula.nebula.domain.star.api;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.service.StarCommandService;
import com.team_nebula.nebula.domain.star.service.StarQueryService;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "[ 스타 ]")
@RequestMapping("/api/v1/bookmarks")
public class StarController {

    private final StarCommandService starCommandService;
    private final StarQueryService starQueryService;

    @PostMapping("/")
    public ApiResponse<CreateStarResponseDTO> createStar(
            @RequestPart(value = "thumbnailImage",required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "htmlFile",required = false) MultipartFile htmlFile,
            @RequestPart(value = "star JSON data") String starJsonData
    ){
        CreateStarFileDTO requestDTO = starQueryService.starDataParsing(thumbnailImage, htmlFile, starJsonData);

        CreateStarResponseDTO responseDTO = starCommandService.createStar(requestDTO);
        return ApiResponse.onSuccessCreated(responseDTO);
    }

    @GetMapping("/{userId}")
    public ApiResponse<GetStarListResponseDTO> getStars(@PathVariable Long userId){
        GetStarListResponseDTO responseDTO = starQueryService.getStarList(userId);

        return ApiResponse.onSuccessCreated(responseDTO);
    }
}
