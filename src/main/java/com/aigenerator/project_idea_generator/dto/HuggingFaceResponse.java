package com.aigenerator.project_idea_generator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HuggingFaceResponse {
    
    @JsonProperty("generated_text")
    private String generatedText;
}
