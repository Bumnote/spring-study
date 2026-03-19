package com.laboratory.springai.domain.ai;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class OpenAiService {

  private final OpenAiChatModel openAiChatModel;
  private final OpenAiEmbeddingModel openAiEmbeddingModel;
  private final OpenAiImageModel openAiImageModel;
  private final OpenAiAudioSpeechModel openAiAudioSpeechModel;
  private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

  // 1. chat model
  public String generate(String text) {

    // 메시지
    SystemMessage systemMessage = new SystemMessage("");
    UserMessage userMessage = new UserMessage(text);
    AssistantMessage assistantMessage = new AssistantMessage("");

    // 옵션
    OpenAiChatOptions options = OpenAiChatOptions.builder()
        .model("gpt-4.1-mini")
        .temperature(0.7)
        .build();

    // 프롬프트
    Prompt prompt = new Prompt(List.of(systemMessage, userMessage, assistantMessage), options);

    // 요청 및 응답
    ChatResponse response = openAiChatModel.call(prompt);
    return response.getResult().getOutput().getText();
  }

  // 1. chat model : response stream
  public Flux<String> generateStream(String text) {

    // 메시지
    SystemMessage systemMessage = new SystemMessage("");
    UserMessage userMessage = new UserMessage(text);
    AssistantMessage assistantMessage = new AssistantMessage("");

    // 옵션
    OpenAiChatOptions options = OpenAiChatOptions.builder()
        .model("gpt-4.1-mini")
        .temperature(0.7)
        .build();

    // 프롬프트
    Prompt prompt = new Prompt(List.of(systemMessage, userMessage, assistantMessage), options);

    // 요청 및 응답
    return openAiChatModel.stream(prompt)
        .mapNotNull(response -> response.getResult().getOutput().getText());
  }

  // 2. Embedding model
  public List<float[]> generateEmbedding(List<String> texts, String model) {

    // option
    EmbeddingOptions embeddingOptions = OpenAiEmbeddingOptions.builder()
        .model(model)
        .build();

    EmbeddingRequest prompt = new EmbeddingRequest(texts, embeddingOptions);

    // prompt
    EmbeddingResponse response = openAiEmbeddingModel.call(prompt);

    return response.getResults().stream()
        .map(Embedding::getOutput)
        .toList();
  }

  // 3. Image model
  public List<String> generateImage(String text, int count, int height, int width) {

    // option
    OpenAiImageOptions imageOptions = OpenAiImageOptions.builder()
        .quality("hd")
        .N(count)
        .height(height)
        .width(width)
        .build();

    // prompt
    ImagePrompt prompt = new ImagePrompt(text, imageOptions);

    // request & response
    ImageResponse response = openAiImageModel.call(prompt);
    return response.getResults().stream()
        .map(image -> image.getOutput().getUrl())
        .toList();
  }
}
