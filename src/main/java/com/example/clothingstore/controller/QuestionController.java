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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping
@RestController
public class QuestionController {
   @Value("${openai.api.key}")
   private String apiKey;
   
   @PostMapping("/questions")
    public ResponseEntity<?> openAIQuestionResponse(@RequestBody QuestionDTO questionDTO)  {
       OpenAiService openAiService = new OpenAiService(apiKey);
       String prompt = buildPrompt();

       final List<ChatMessage> messages = new ArrayList<>();
       final ChatMessage systemMessage = new ChatMessage(
               ChatMessageRole.SYSTEM.value(),
               prompt
       );
       final ChatMessage userMessage = new ChatMessage(
               ChatMessageRole.USER.value(),
               questionDTO.getQuestion()
       );

       messages.add(systemMessage);
       messages.add(userMessage);

       ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
               .builder()
               .model("gpt-3.5-turbo")
               .messages(messages)
               .n(1)
               .maxTokens(350)
               .logitBias(new HashMap<>())
               .build();

      ChatCompletionChoice choice = openAiService
              .createChatCompletion(chatCompletionRequest)
              .getChoices()
              .get(0);

       return ResponseEntity.ok(choice.getMessage());
   }

   private String buildPrompt() {
       String promptStart = "You are Vy, an assistant Q&A bot create by ADNCloth, a clothing shop. \n" +
               "Your role is to answer a question that the customer asks base on the provided information only. " +
               "Vietnamese answers only. " +
               "If the question is not fully clothing-related, gently ask them to try again.\n" +
               "This is the full information of our product. Each product is seperated by the semicolon.";

       String prompt = "Tên sản phẩm:Áo giữ nhiệt nam Modal Ultra Warm - mặc là ấm, thoáng khí,kích cỡ:1-màu:Trắng-giá:179000-số lượng còn lại:10,kích cỡ:2-màu:Trắng-giá:189000-số lượng còn lại:0,kích cỡ:1-màu:Đen-giá:189000-số lượng còn lại:50,kích cỡ:2-màu:Đen-giá:179000-số lượng còn lại:50,kích cỡ:3-màu:Đen-giá:179000-số lượng còn lại:50,kích cỡ:3-màu:Trắng-giá:179000-số lượng còn lại:69,kích cỡ:1-màu:Be-giá:200000-số lượng còn lại:10,kích cỡ:2-màu:Xám-giá:150000-số lượng còn lại:10,kích cỡ:1-màu:Xám-giá:150000-số lượng còn lại:9;Tên sản phẩm:Áo Thun Cổ Tròn Đơn Giản Y Nguyên Bản Ver77,kích cỡ:1-màu:Xanh nước nhạt-giá:199000-số lượng còn lại:0,kích cỡ:2-màu:Xanh nước nhạt-giá:199000-số lượng còn lại:48,kích cỡ:1-màu:Xanh nước đậm-giá:199000-số lượng còn lại:50,kích cỡ:2-màu:Xanh nước đậm-giá:200000-số lượng còn lại:60,kích cỡ:1-màu:Xám-giá:199000-số lượng còn lại:0,kích cỡ:2-màu:Xám-giá:199000-số lượng còn lại:46,kích cỡ:1-màu:Be-giá:199000-số lượng còn lại:50,kích cỡ:2-màu:Be-giá:200000-số lượng còn lại:59,kích cỡ:1-màu:Trắng-giá:199000-số lượng còn lại:0;Tên sản phẩm:Áo Thun Cổ Tròn Đơn Giản Y Nguyên Bản Ver121,kích cỡ:1-màu:Trắng-giá:179000-số lượng còn lại:77,kích cỡ:2-màu:Trắng-giá:179000-số lượng còn lại:79,kích cỡ:3-màu:Trắng-giá:179000-số lượng còn lại:80,kích cỡ:4-màu:Trắng-giá:179000-số lượng còn lại:80,kích cỡ:5-màu:Trắng-giá:179000-số lượng còn lại:80;Tên sản phẩm:Áo Thun Cổ Tròn Tối Giản M2,kích cỡ:1-màu:Trắng-giá:149000-số lượng còn lại:69,kích cỡ:2-màu:Trắng-giá:149000-số lượng còn lại:67,kích cỡ:3-màu:Trắng-giá:149000-số lượng còn lại:68,kích cỡ:4-màu:Trắng-giá:149000-số lượng còn lại:70;Tên sản phẩm:Quần Dài Vải Đơn Giản Y Nguyên Bản Ver10,kích cỡ:1-màu:Xanh nước đậm-giá:249000-số lượng còn lại:20,kích cỡ:2-màu:Xanh nước đậm-giá:249000-số lượng còn lại:19,kích cỡ:3-màu:Xanh nước đậm-giá:249000-số lượng còn lại:20;Tên sản phẩm:Quần Tây Đơn Giản Y Nguyên Bản Ver26,kích cỡ:1-màu:Đen-giá:249000-số lượng còn lại:50,kích cỡ:2-màu:Đen-giá:249000-số lượng còn lại:50,kích cỡ:3-màu:Đen-giá:249000-số lượng còn lại:50;Tên sản phẩm:Quần Tây Tối Giản HG11,kích cỡ:1-màu:Đen-giá:249000-số lượng còn lại:67,kích cỡ:2-màu:Đen-giá:249000-số lượng còn lại:70,kích cỡ:1-màu:Trắng-giá:249000-số lượng còn lại:0;Tên sản phẩm:Quần Tây Tối Giản HG17,kích cỡ:1-màu:Đen-giá:299000-số lượng còn lại:89,kích cỡ:2-màu:Đen-giá:299000-số lượng còn lại:90,kích cỡ:3-màu:Đen-giá:299000-số lượng còn lại:90,kích cỡ:4-màu:Đen-giá:299000-số lượng còn lại:88,kích cỡ:5-màu:Đen-giá:299000-số lượng còn lại:0;Tên sản phẩm:Quần Tây Tối Giản HG10,kích cỡ:1-màu:Đen-giá:199000-số lượng còn lại:50,kích cỡ:2-màu:Đen-giá:199000-số lượng còn lại:50,kích cỡ:3-màu:Đen-giá:199000-số lượng còn lại:50";
       return promptStart + prompt;
   }
}
