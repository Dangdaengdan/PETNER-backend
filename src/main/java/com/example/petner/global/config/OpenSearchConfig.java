package com.example.petner.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.HttpHost;

@Configuration
 @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "opensearch.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class OpenSearchConfig {

    @Value("${opensearch.host:localhost}")
    private String host;

    @Value("${opensearch.port:9200}")
    private int port;

    @Value("${opensearch.scheme:http}")
    private String scheme;

    @Bean
    public RestClient restClient() {
        return RestClient.builder(
                new HttpHost(host, port, scheme)
        ).build();
    }

    @Bean
    public OpenSearchClient openSearchClient(RestClient restClient) {
        // OpenSearch용 ObjectMapper에 JSR310 모듈 추가
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(mapper)
        );
        return new OpenSearchClient(transport);
    }
}