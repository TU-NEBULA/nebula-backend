package com.team_nebula.nebula.domain.favicon.service;

import com.team_nebula.nebula.domain.favicon.entity.Favicon;

public interface FaviconService {

    public Favicon getOrCreateFavicon(String siteUrl);

    }
