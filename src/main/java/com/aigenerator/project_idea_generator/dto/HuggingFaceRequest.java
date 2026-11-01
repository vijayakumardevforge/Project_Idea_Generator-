package com.aigenerator.project_idea_generator.dto;

public class HuggingFaceRequest {
    private String inputs;

    public HuggingFaceRequest() {}

    public HuggingFaceRequest(String inputs) {
        this.inputs = inputs;
    }

    public String getInputs() { return inputs; }
    public void setInputs(String inputs) { this.inputs = inputs; }
}
