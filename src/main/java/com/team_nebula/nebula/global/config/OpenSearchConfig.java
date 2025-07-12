package com.team_nebula.nebula.global.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenSearchConfig {

    @Value("${spring.opensearch.host}")
    private String opensearchHost;

    @Value("${spring.opensearch.port:443}")
    private int opensearchPort;

    @Value("${spring.opensearch.username:}")
    private String username;

    @Value("${spring.opensearch.password:}")
    private String password;

    @Value("${spring.opensearch.scheme:https}")
    private String scheme;

    @Bean
    public OpenSearchClient openSearchClient() {
        log.info("Initializing OpenSearch client with host: {}:{}, scheme: {}", opensearchHost, opensearchPort, scheme);
        
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(opensearchHost, opensearchPort, scheme)
        );

        // AWS OpenSearch 인증 설정
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            log.info("Setting up authentication for OpenSearch with username: {}", username);
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, 
                new UsernamePasswordCredentials(username, password));
            
            builder.setHttpClientConfigCallback(httpClientBuilder -> 
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            );
        }

        // 타임아웃 설정 증가
        builder.setRequestConfigCallback(requestConfigBuilder -> 
            requestConfigBuilder
                .setConnectTimeout(30000)  // 30초로 증가
                .setSocketTimeout(120000)  // 120초로 증가
        );

        RestClient restClient = builder.build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        
        log.info("OpenSearch client initialized successfully");
        return new OpenSearchClient(transport);
    }
}
