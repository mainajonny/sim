
package com.aw.simitrac.model.activities;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("schoolCode")
    @Expose
    private String schoolCode;
    @SerializedName("groupCode")
    @Expose
    private String groupCode;
    @SerializedName("groupName")
    @Expose
    private String groupName;
    @SerializedName("groupEntryYear")
    @Expose
    private String groupEntryYear;
    @SerializedName("groupExitYear")
    @Expose
    private String groupExitYear;
    @SerializedName("status")
    @Expose
    private String status;
    private final static long serialVersionUID = 1418586110926337586L;

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

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupEntryYear() {
        return groupEntryYear;
    }

    public void setGroupEntryYear(String groupEntryYear) {
        this.groupEntryYear = groupEntryYear;
    }

    public String getGroupExitYear() {
        return groupExitYear;
    }

    public void setGroupExitYear(String groupExitYear) {
        this.groupExitYear = groupExitYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
