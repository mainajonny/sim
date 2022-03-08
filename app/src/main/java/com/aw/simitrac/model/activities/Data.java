
package com.aw.simitrac.model.activities;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("activities")
    @Expose
    private List<Activity> activities = null;
    private final static long serialVersionUID = 8315950065931225714L;

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

}
