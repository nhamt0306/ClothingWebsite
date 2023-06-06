package com.example.clothingstore.controller;

import com.example.clothingstore.dto.QuestionDTO;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;

@CrossOrigin(origins = "*")
@RequestMapping
@RestController
public class QuestionController {
   @Value("${openai.api.key}")
   private String apiKey;
   
   @PostMapping("/questions")
    public ResponseEntity<?> openAIQuestionResponse(@RequestBody QuestionDTO questionDTO)  {
       OpenAiService openAiService = new OpenAiService(apiKey, Duration.ofSeconds(45));
       String prompt = buildPrompt();

       final ChatMessage systemMessage = new ChatMessage(
               ChatMessageRole.SYSTEM.value(),
               prompt
       );

       questionDTO.getMessages().add(0, systemMessage);

       ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
               .builder()
               .model("gpt-3.5-turbo")
               .messages(questionDTO.getMessages())
               .n(1)
               .maxTokens(200)
               .logitBias(new HashMap<>())
               .build();

      ChatCompletionChoice choice = openAiService
              .createChatCompletion(chatCompletionRequest)
              .getChoices()
              .get(0);

       return ResponseEntity.ok(choice.getMessage());
   }

   private String buildPrompt() {
       String promptStart = "You are an assistant Q&A bot create by ADNCloth,a clothing shop. \n" +
               "Your role is to answer a short question that the customer asks base on the provided information only." +
               "Vietnamese answers only." +
               "If the question is not clothing-related,ask them to try again.\n" +
               "This is the full information of our product,which is seperated by the semicolon:\n";

       String uri = "http://localhost:8099/product/prompt";
       RestTemplate restTemplate = new RestTemplate();
       String result = restTemplate.getForObject(uri, String.class);

       return promptStart + result;
   }
}
