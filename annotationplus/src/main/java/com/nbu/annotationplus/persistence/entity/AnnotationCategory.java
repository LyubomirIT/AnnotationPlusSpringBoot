package com.nbu.annotationplus.persistence.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "annotation_category")
public class AnnotationCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Long userId;

    @Column
    private Long sourceId;

    @OneToMany(mappedBy = "annotationCategoryId", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.LAZY)
    private Set<Annotation> annotations;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
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

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
    }
}
