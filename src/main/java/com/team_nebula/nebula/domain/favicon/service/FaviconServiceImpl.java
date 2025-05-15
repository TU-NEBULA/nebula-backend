package com.team_nebula.nebula.domain.favicon.service;

import com.team_nebula.nebula.domain.favicon.entity.Favicon;
import com.team_nebula.nebula.domain.favicon.repository.FaviconRepository;
import com.team_nebula.nebula.global.image.FaviconDownloader;
import com.team_nebula.nebula.global.image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FaviconServiceImpl implements FaviconService {
    private final FaviconRepository faviconRepository;
    private final S3Service s3Service;

    @Value("${favicon.tistory-default}")
    private String tistoryDefaultFavicon;

    @Override
    public Favicon getOrCreateFavicon(String siteUrl) {
        // 도메인 추출
        String domain = extractDomain(siteUrl);

        // tistoy일 경우 체크
        if (domain.equals("tistory.com") || domain.endsWith(".tistory.com")) {
            domain = tistoryDefaultFavicon;
        }

        // 기존 Favicon이 있는지 확인
        Optional<Favicon> existingFavicon = faviconRepository.findById(domain);
        if (existingFavicon.isPresent()) {
            return existingFavicon.get();
        }

        // 파비콘 다운로드 후 S3 업로드
        File faviconFile = downloadFavicon(domain);
        String faviconUrl = s3Service.saveFavicon(faviconFile, domain);

        // 새로운 Favicon 노드 저장 후 반환
        Favicon newFavicon = new Favicon(domain, faviconUrl);
        return faviconRepository.save(newFavicon);
    }

    private String extractDomain(String siteUrl) {
        try {
            java.net.URL url = new java.net.URL(siteUrl);
            return url.getHost();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + siteUrl);
        }
    }

    private File downloadFavicon(String domain) {
        return FaviconDownloader.downloadFavicon("https://www.google.com/s2/favicons?domain=" + domain);
    }
}
