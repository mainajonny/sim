
package com.aw.simitrac.model.activities;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activity_ implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("schoolCode")
    @Expose
    private String schoolCode;
    @SerializedName("activityCode")
    @Expose
    private String activityCode;
    @SerializedName("activityName")
    @Expose
    private String activityName;
    private final static long serialVersionUID = 2577751590967774810L;

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

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

}
