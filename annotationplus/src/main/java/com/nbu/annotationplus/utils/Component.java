package com.nbu.annotationplus.utils;

public enum Component {
    WEBSITE("Website"),
    ANNOTATION("Annotation"),
    OTHER ("Other");

    private String displayName;

    Component(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /*public static Component getByName(String name) {
        for (Component component : values()) {
            if (name != null && name.equals(component.displayName)) {
                return component;
            }
        }
        throw new InvalidInputParamsException(name != null ? "The provided component: " +'"'+ name +'"'+ " does not exist." : "Component is missing.");
     }*/
}
