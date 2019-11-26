package com.nbu.annotationplus.dto;

import java.time.LocalDateTime;

public class DtoAnnotation {

    private Long id;
    private String username;
    private Long userId;
    private Long sourceId;
    private Long annotationCategoryId;
    private String content;
    private String color;
    private LocalDateTime createdTs;
    private LocalDateTime updatedTs;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getAnnotationCategoryId() {
        return annotationCategoryId;
    }

    public void setAnnotationCategoryId(Long annotationCategoryId) {
        this.annotationCategoryId = annotationCategoryId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDateTime getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(LocalDateTime createdTs) {
        this.createdTs = createdTs;
    }

    public LocalDateTime getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(LocalDateTime updatedTs) {
        this.updatedTs = updatedTs;
    }
}
