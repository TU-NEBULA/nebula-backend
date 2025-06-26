package com.team_nebula.nebula.global.config;


import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
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
}