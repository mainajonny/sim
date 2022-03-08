
package com.aw.simitrac.model.checking;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("User")
    @Expose
    private User user;
    @SerializedName("URL")
    @Expose
    private String uRL;
    private final static long serialVersionUID = 3443521600557787878L;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getURL() {
        return uRL;
    }

    public void setURL(String uRL) {
        this.uRL = uRL;
    }

}
