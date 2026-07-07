package com.solvemate.dto;

public class AiChatRequest {
    private Long polymerId;
    private String question;

    public AiChatRequest() {}

    public Long getPolymerId()        { return polymerId; }
    public String getQuestion()       { return question; }

    public void setPolymerId(Long v)  { this.polymerId = v; }
    public void setQuestion(String v) { this.question = v; }
}