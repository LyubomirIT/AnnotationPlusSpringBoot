package com.nbu.annotationplus.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "note")
public class Note extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private Long userId;

    @OneToMany(mappedBy = "noteId", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
    private Set<AnnotationCategory> annotationCategories;

    //@OneToMany(mappedBy = "noteId", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
    //private Set<Comment> comments;

    @OneToMany(mappedBy = "noteId", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
    private Set<Annotation> annotations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<AnnotationCategory> getAnnotationCategories() {
        return annotationCategories;
    }

    public void setAnnotationCategories(Set<AnnotationCategory> annotationCategories) {
        this.annotationCategories = annotationCategories;
    }

   /* public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }*/

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
    }
}
