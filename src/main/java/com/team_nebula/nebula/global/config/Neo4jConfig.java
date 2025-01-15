package com.team_nebula.nebula.global.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableNeo4jAuditing
@EnableNeo4jRepositories(
        basePackages = {
                "com.team_nebula.nebula.domain.user.repository.neo4j",
                "com.team_nebula.nebula.domain.star.repository",
                "com.team_nebula.nebula.domain.link.repository",
                "com.team_nebula.nebula.domain.category.repository",
                "com.team_nebula.nebula.domain.keyword.repository"
        },
        transactionManagerRef = "neo4jTransactionManager"
)
public class Neo4jConfig {

    @Bean
    public Driver neo4jDriver(
            @Value("${spring.neo4j.uri}") String uri,
            @Value("${spring.neo4j.authentication.username}") String username,
            @Value("${spring.neo4j.authentication.password}") String password) {

        Config config = Config.builder()
                .withMaxConnectionPoolSize(50) // 최대 연결 수
                .withConnectionAcquisitionTimeout(30, TimeUnit.SECONDS) // 연결 대기 시간 (초 단위)
                .withConnectionTimeout(15, TimeUnit.SECONDS) // 연결 설정 시간 (초 단위)
                .withMaxTransactionRetryTime(15, TimeUnit.SECONDS) // 트랜잭션 재시도 시간 (초 단위)
                .build();

        return GraphDatabase.driver(uri, AuthTokens.basic(username, password), config);
    }

    @Bean(name = "neo4jTransactionManager")
    public Neo4jTransactionManager transactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}

