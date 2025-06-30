package com.team_nebula.nebula.global.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.team_nebula.nebula.domain.star.search.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String[] elasticsearchUris;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUris)
                .withConnectTimeout(10000)
                .withSocketTimeout(60000)
                .withClientConfigurer(
                        ElasticsearchClients.ElasticsearchRestClientConfigurationCallback.from(restClientBuilder -> {
                            restClientBuilder.setDefaultHeaders(new Header[]{
                                    new BasicHeader("Content-Type", "application/json")
                            });
                            return restClientBuilder;
                        })
                )
                .build();
    }

    @Bean
    public ObjectMapper elasticsearchObjectMapper() {
        // JsonMapper.builder() 사용 (ObjectMapper 대신)
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        // 추가 설정
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

    @Bean
    @Primary  // Primary 어노테이션 추가
    public ElasticsearchClient elasticsearchClient() {
        // 커스텀 ObjectMapper로 JacksonJsonpMapper 생성
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(elasticsearchObjectMapper());

        // RestClient 생성
        RestClient restClient = RestClient.builder(
                org.apache.http.HttpHost.create(elasticsearchUris[0])
        ).build();

        // ElasticsearchTransport 생성
        ElasticsearchTransport transport = new RestClientTransport(restClient, jsonpMapper);

        return new ElasticsearchClient(transport);
    }

    @Bean
    public ElasticsearchOperations elasticsearchOperations(ElasticsearchClient client) {
        return new org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate(client);
    }
}
