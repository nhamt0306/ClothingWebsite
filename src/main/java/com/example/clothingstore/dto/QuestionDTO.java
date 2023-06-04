package com.example.clothingstore.dto;

import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.List;

public class QuestionDTO {
    List<ChatMessage> messages;

    public QuestionDTO(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public QuestionDTO() {
    }

}
