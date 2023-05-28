package com.example.clothingstore.dto;

public class PromptDTO {
    private String prompt;

    public PromptDTO(String prompt) {
        this.prompt = prompt;
    }

    public PromptDTO() {
        this.prompt = "";
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
