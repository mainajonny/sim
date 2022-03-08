
package com.aw.simitrac.model.group;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("Groups")
    @Expose
    private List<Group> groups = null;
    private final static long serialVersionUID = -5608810424697944657L;

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

}
