package com.solvemate.dto;

public class AiChatResponse {
    private String answer;

    public AiChatResponse() {}
    public AiChatResponse(String answer) { this.answer = answer; }

    public String getAnswer()       { return answer; }
    public void setAnswer(String v) { this.answer = v; }
}