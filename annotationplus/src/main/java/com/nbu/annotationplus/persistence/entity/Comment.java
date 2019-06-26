package com.nbu.annotationplus.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long noteId;

    @Column
    private String comment;

    @Column
    private Long userId;

    @Column
    private String userName;

    @Column
    private String annotationId;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
