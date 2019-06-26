package com.nbu.annotationplus.dto;

import java.time.LocalDateTime;

public class DtoComment {

    private Long id;
    private Long noteId;
    private String comment;
    private Long userId;
    private String annotationId;
    private LocalDateTime createdTs;
    private LocalDateTime updatedTs;
    private String userName;

    public Long getId() {
        return id;
    }

    public Long getNoteId() {
        return noteId;
    }

    public String getComment() {
        return comment;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAnnotationId() {
        return annotationId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAnnotationId(String annotationId) {
        this.annotationId = annotationId;
    }

    public void setCreatedTs(LocalDateTime createdTs) {
        this.createdTs = createdTs;
    }

    public void setUpdatedTs(LocalDateTime updatedTs) {
        this.updatedTs = updatedTs;
    }

    public LocalDateTime getCreatedTs() {
        return createdTs;
    }

    public LocalDateTime getUpdatedTs() {
        return updatedTs;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
