package com.relly.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PermissionDTO {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "title")
    private String title;
    @JsonProperty(value = "key")
    private String key;
    @JsonProperty(value = "children")
    private List<PermissionDTO> children;
}
