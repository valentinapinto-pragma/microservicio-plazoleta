package com.pragma.plazoleta.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramMessageRequestDto {

    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("text")
    private String text;
}