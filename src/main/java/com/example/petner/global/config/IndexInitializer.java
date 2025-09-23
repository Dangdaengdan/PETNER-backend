package com.example.petner.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.RestClient;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class IndexInitializer {

    private final RestClient restClient;

    @Bean
    public ApplicationRunner initializeIndices() {
        return args -> {
            try {
                createPostsIndex();
                createDogsIndex();
                log.info("OpenSearch indices initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize OpenSearch indices", e);
            }
        };
    }

    private void createPostsIndex() throws IOException {
        String indexName = "posts";

        if (!indexExists(indexName)) {
            String mapping = """
                {
                  "settings": {
                    "analysis": {
                      "analyzer": {
                        "nori_analyzer": {
                          "type": "custom",
                          "tokenizer": "nori_tokenizer",
                          "filter": ["lowercase", "nori_part_of_speech"]
                        }
                      },
                      "tokenizer": {
                        "nori_tokenizer": {
                          "type": "nori_tokenizer",
                          "decompound_mode": "mixed"
                        }
                      }
                    }
                  },
                  "mappings": {
                    "properties": {
                      "postId": {"type": "long"},
                      "title": {
                        "type": "text",
                        "analyzer": "nori_analyzer",
                        "fields": {
                          "keyword": {"type": "keyword"}
                        }
                      },
                      "content": {
                        "type": "text",
                        "analyzer": "nori_analyzer"
                      },
                      "viewCount": {"type": "integer"},
                      "thumbImageUrl": {"type": "keyword"},
                      "createdAt": {"type": "date"},
                      "updatedAt": {"type": "date"},
                      "authorId": {"type": "long"},
                      "authorName": {
                        "type": "text",
                        "analyzer": "nori_analyzer",
                        "fields": {
                          "keyword": {"type": "keyword"}
                        }
                      }
                    }
                  }
                }
                """;

            Request request = new Request("PUT", "/" + indexName);
            request.setJsonEntity(mapping);
            restClient.performRequest(request);
            log.info("Posts index created successfully");
        }
    }

    private void createDogsIndex() throws IOException {
        String indexName = "dogs";

        if (!indexExists(indexName)) {
            String mapping = """
                {
                  "settings": {
                    "analysis": {
                      "analyzer": {
                        "nori_analyzer": {
                          "type": "custom",
                          "tokenizer": "nori_tokenizer",
                          "filter": ["lowercase", "nori_part_of_speech"]
                        }
                      },
                      "tokenizer": {
                        "nori_tokenizer": {
                          "type": "nori_tokenizer",
                          "decompound_mode": "mixed"
                        }
                      }
                    }
                  },
                  "mappings": {
                    "properties": {
                      "dogId": {"type": "long"},
                      "name": {
                        "type": "text",
                        "analyzer": "nori_analyzer",
                        "fields": {
                          "keyword": {"type": "keyword"}
                        }
                      },
                      "breedName": {"type": "keyword"},
                      "birthDate": {"type": "keyword"},
                      "gender": {"type": "keyword"},
                      "dogSize": {"type": "keyword"},
                      "weight": {"type": "double"},
                      "healthStatus": {
                        "type": "text",
                        "analyzer": "nori_analyzer"
                      },
                      "description": {
                        "type": "text",
                        "analyzer": "nori_analyzer"
                      },
                      "adoptionStatus": {"type": "keyword"},
                      "createdAt": {"type": "date"},
                      "updatedAt": {"type": "date"},
                      "imageUrl": {"type": "keyword"},
                      "memberId": {"type": "long"},
                      "shelterId": {"type": "long"},
                      "shelterName": {"type": "keyword"},
                      "location": {"type": "keyword"}
                    }
                  }
                }
                """;

            Request request = new Request("PUT", "/" + indexName);
            request.setJsonEntity(mapping);
            restClient.performRequest(request);
            log.info("Dogs index created successfully");
        }
    }

    private boolean indexExists(String indexName) {
        try {
            Request request = new Request("HEAD", "/" + indexName);
            Response response = restClient.performRequest(request);
            return response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }
}