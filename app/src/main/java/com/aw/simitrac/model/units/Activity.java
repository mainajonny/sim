
package com.aw.simitrac.model.units;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activity implements Serializable
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
    @SerializedName("isAll")
    @Expose
    private String isAll;
    @SerializedName("date")
    @Expose
    private String date;
    private final static long serialVersionUID = 4353288036187359052L;

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

    public String getIsAll() {
        return isAll;
    }

    public void setIsAll(String isAll) {
        this.isAll = isAll;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
