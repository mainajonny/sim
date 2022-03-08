
package com.aw.simitrac.model.units;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("Activities")
    @Expose
    private List<Activity> activities = null;
    private final static long serialVersionUID = 7794841379482531920L;

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

}
