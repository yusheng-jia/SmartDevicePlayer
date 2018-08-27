package com.mantic.control.entiy;

/**
 * Created by lin on 2018/1/17.
 */

public class ClassificationBean {
    private boolean isTitle = false;
    private String classificationName;
    private String category;

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    @Override
    public String toString() {
        return "ClassificationBean{" +
                "isTitle=" + isTitle +
                ", classificationName='" + classificationName + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
