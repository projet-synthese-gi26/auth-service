package com.tramasys.auth.adapters.out.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

// Ce DTO matche le JSON renvoyé par le Media Service
@JsonIgnoreProperties(ignoreUnknown = true)
public record MediaResponseDto(
    UUID id,
    String uri,

    @JsonProperty("created_at") 
    String createdAt
) {}