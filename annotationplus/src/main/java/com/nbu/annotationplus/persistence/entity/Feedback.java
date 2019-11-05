package com.nbu.annotationplus.persistence.entity;

import com.nbu.annotationplus.utils.Component;
import com.nbu.annotationplus.utils.Type;

import javax.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "type")
    private Type type;

    @Column(name = "component")
    private Component component;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    public Component getComponent() {
        return component;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }
}
