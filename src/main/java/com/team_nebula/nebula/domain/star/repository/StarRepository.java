package com.team_nebula.nebula.domain.star.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.team_nebula.nebula.domain.star.search.dto.response.GetStarOneWithUserIdResponseDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import com.team_nebula.nebula.domain.star.dto.response.GetSearchedStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;

@Primary
public interface StarRepository extends Neo4jRepository<Star, UUID>, StarNeo4jRepositoryCustom {
	@Query("""
		    MATCH (u:UserNode)-[:CREATED]->(s:Star)
		    WHERE u.userId = $userId AND s.isDeletedStatus = false
		    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
		    OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
		    OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
		
		    RETURN s.id AS starId,
		           s.title AS title,
		           s.siteUrl AS siteUrl,
		           s.thumbnailUrl AS thumbnailUrl,
		           s.summaryAI AS summaryAI,
		           s.userMemo AS userMemo,
		           s.views AS views,
		           c.name AS categoryName,
		           f.faviconUrl AS faviconUrl,
		           s.lastAccessedAt AS lastAccessedAt,
		           COLLECT(k.name) AS keywordList
		""")
	List<GetStarOneResponseDTO> findStarsByUserId(@Param("userId") Long userId);

	@Query("""
			MATCH (s:Star {id: $starId})
			WHERE s.isDeletedStatus = false
			OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
			OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
			OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
			SET s.views = s.views + 1,
				s.lastAccessedAt = datetime()
			RETURN s.id AS starId,
				   s.title AS title,
				   s.siteUrl AS siteUrl,
				   s.thumbnailUrl AS thumbnailUrl,
				   s.summaryAI AS summaryAI,
				   s.userMemo AS userMemo,
				   s.views AS views,
				   c.name AS categoryName,
				   f.faviconUrl AS faviconUrl,
				   s.lastAccessedAt AS lastAccessedAt,
				   COLLECT(DISTINCT k.name) AS keywordList
		""")
	GetStarOneResponseDTO findStarDetailById(@Param("starId") UUID starId);


	@Query("""
		MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:BELONGS_TO]->(c:Category)
		WHERE u.userId = $userId AND c.id = $categoryId AND s.isDeletedStatus = false
		OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
		OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
		RETURN s.id AS starId,
		       s.title AS title,
		       s.siteUrl AS siteUrl,
		       s.thumbnailUrl AS thumbnailUrl,
		       s.summaryAI AS summaryAI,
		       s.userMemo AS userMemo,
		       s.views AS views,
		       c.name AS categoryName,
		       f.faviconUrl AS faviconUrl,
		       s.lastAccessedAt AS lastAccessedAt,
		       COLLECT(k.name) AS keywordList
		""")
	List<GetStarOneResponseDTO> findStarsInCategory(@Param("userId") Long userId, @Param("categoryId") UUID categoryId);

	@Query("""
		MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:TAGGED]->(k:Keyword)
		WHERE u.userId = $userId AND k.name = $keywordId AND s.isDeletedStatus = false
		OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
		OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
		RETURN s.id AS starId,
		       s.title AS title,
		       s.siteUrl AS siteUrl,
		       s.thumbnailUrl AS thumbnailUrl,
		       s.summaryAI AS summaryAI,
		       s.userMemo AS userMemo,
		       s.views AS views,
		       c.name AS categoryName,
		       f.faviconUrl AS faviconUrl,
		       s.lastAccessedAt AS lastAccessedAt,
		       COLLECT(k.name) AS keywordList
		""")
	List<GetStarOneResponseDTO> findStarsInKeyword(@Param("userId") Long userId, @Param("keywordId") String keywordId);

	@Query("""
		MATCH (u:UserNode)-[:CREATED]->(s:Star)
		WHERE u.userId = $userId
		  AND ($title IS NULL OR toLower(s.title) CONTAINS toLower($title))
		  AND s.isDeletedStatus = false
		
		OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
		OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
		OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
		
		OPTIONAL MATCH (s)-[:LINKED]->(l:Link)-[:LINKED]-(s2:Star)
		WHERE s2 <> s
		OPTIONAL MATCH (s2)-[:BELONGS_TO]->(c2:Category)
		OPTIONAL MATCH (s2)-[:TAGGED]->(k2:Keyword)
		OPTIONAL MATCH (s2)-[:HAS_FAVICON]->(f2:Favicon)
		
		WITH
		    s, c, f, COLLECT(DISTINCT k.name) AS keywordList,
		    s2, c2, f2, COLLECT(DISTINCT k2.name) AS linkedKeywordList,
		    COLLECT(DISTINCT {
		        linkId: l.id,
		        sharedKeywordNum: l.sharedKeywordNum,
		        similarity: l.similarityScore
		    }) AS links
		
		RETURN
		    COLLECT(DISTINCT {
		        searchedStar: {
		            starId: s.id,
		            categoryName: c.name,
		            title: s.title,
		            siteUrl: s.siteUrl,
		            thumbnailUrl: s.thumbnailUrl,
		            summaryAI: s.summaryAI,
		            userMemo: s.userMemo,
		            views: s.views,
		            faviconUrl: f.faviconUrl,
		            lastAccessedAt: s.lastAccessedAt,
		            keywordList: keywordList
		        },
		        linkData: links,
		        linkedStar: {
		            starId: s2.id,
		            categoryName: c2.name,
		            title: s2.title,
		            siteUrl: s2.siteUrl,
		            thumbnailUrl: s2.thumbnailUrl,
		            summaryAI: s2.summaryAI,
		            userMemo: s2.userMemo,
		            views: s2.views,
		            faviconUrl: f2.faviconUrl,
		            keywordList: linkedKeywordList
		        }
		    }) AS result
		""")
	List<GetSearchedStarOneResponseDTO> searchStars(@Param("userId") Long userId, @Param("title") String title);

	@Query("""
		MATCH (u:UserNode)-[:CREATED]->(s:Star)
		WHERE u.userId = $userId AND s.siteUrl IN $urls AND s.isDeletedStatus = false
		RETURN s.siteUrl
		""")
	List<String> findStarUrlsByUrlsAndUserId(@Param("urls") List<String> urls, @Param("userId") Long userId);

	@Query("""
        MATCH (u:UserNode)-[:CREATED]->(s:Star {id: $starId})
        RETURN u.userId
        """)
	Optional<Long> findUserIdByStarId(@Param("starId") UUID starId);

	@Query("""
        MATCH (s:Star)
        WHERE s.isDeletedStatus = false
        RETURN s
        """)
	List<Star> findByIsDeletedStatusFalse();

	@Query("""
		MATCH (u:UserNode)-[:CREATED]->(s:Star)
		WHERE s.isDeletedStatus = false
		OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
		OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
		OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
	
		RETURN s.id AS starId,
			   u.userId AS userId,
			   s.title AS title,
			   s.siteUrl AS siteUrl,
			   s.thumbnailUrl AS thumbnailUrl,
			   s.summaryAI AS summaryAI,
			   s.userMemo AS userMemo,
			   s.views AS views,
			   c.name AS categoryName,
			   f.faviconUrl AS faviconUrl,
			   s.lastAccessedAt AS lastAccessedAt,
			   COLLECT(k.name) AS keywordList
		""")
	List<GetStarOneWithUserIdResponseDTO> findAllStarWithKeywordsAndFavicons();

}
