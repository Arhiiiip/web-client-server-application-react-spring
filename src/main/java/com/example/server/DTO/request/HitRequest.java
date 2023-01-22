package com.example.server.DTO.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class HitRequest {

    @JsonProperty(value = "xValue")
    private String xValue;

    @JsonProperty(value = "yValue")
    private String yValue;

    @JsonProperty(value = "rValue")
    private String rValue;
}
