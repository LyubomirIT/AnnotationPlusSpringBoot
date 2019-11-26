package com.nbu.annotationplus.persistence.entity;

import javax.persistence.*;
import java.util.Set;

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
    private Long sourceId;

    @Column
    private String content;

    @Column
    private Long annotationCategoryId;

    @Column
    private String color;

    @OneToMany(mappedBy = "annotationId", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
    private Set<Comment> comments;

   // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
    /*@OneToMany
    @JoinTable(name="comment",
            joinColumns=@JoinColumn(name="annotationUid"),
            inverseJoinColumns=@JoinColumn(name="uid"))
    //@JoinColumn(name="annotationUid", referencedColumnName = "uid")
    private List<Comment> comments;*/

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

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
