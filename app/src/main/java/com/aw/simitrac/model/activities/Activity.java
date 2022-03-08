
package com.aw.simitrac.model.activities;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activity implements Serializable
{

    @SerializedName("ActivityList")
    @Expose
    private ActivityList activityList;
    @SerializedName("Activity")
    @Expose
    private Activity_ activity;
    @SerializedName("Venue")
    @Expose
    private Venue venue;
    @SerializedName("Group")
    @Expose
    private Group group;
    private final static long serialVersionUID = -1436411898060129631L;

    public ActivityList getActivityList() {
        return activityList;
    }

    public void setActivityList(ActivityList activityList) {
        this.activityList = activityList;
    }

    public Activity_ getActivity() {
        return activity;
    }

    public void setActivity(Activity_ activity) {
        this.activity = activity;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

}
