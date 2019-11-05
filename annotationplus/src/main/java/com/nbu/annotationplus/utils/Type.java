package com.nbu.annotationplus.utils;

public enum Type {
    BUG("Bug"),
    QUESTION("Question"),
    FEEDBACK("Feedback"),
    FEATURE_REQUEST("Feature Request");

    private String displayName;

    Type(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /*public static Type getByName(String name) {
        for (Type type : values()) {
            if (name != null && name.equals(type.displayName)) {
                return type;
            }
        }
        throw new InvalidInputParamsException(name != null ? "The provided type: " +'"'+ name +'"'+ " does not exist." : "The type is missing.");
    }*/
}
