package com.nbu.annotationplus.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "annotation")
public class Annotation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private Long userId;

    @Column
    private String annotationId;

    @Column
    private Long noteId;

    @Column
    private String content;

    @Column
    private Long annotationCategoryId;

    @Column
    private String color;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAnnotationId(String annotationId) {
        this.annotationId = annotationId;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
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
}
