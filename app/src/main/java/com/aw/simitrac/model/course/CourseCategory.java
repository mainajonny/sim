
package com.aw.simitrac.model.course;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CourseCategory implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("schoolCode")
    @Expose
    private String schoolCode;
    @SerializedName("categoryCode")
    @Expose
    private String categoryCode;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    private final static long serialVersionUID = -6058408087774824888L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
