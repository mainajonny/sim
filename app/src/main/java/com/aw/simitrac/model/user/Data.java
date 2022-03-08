
package com.aw.simitrac.model.user;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("USERS")
    @Expose
    private List<USER> uSERS = null;
    private final static long serialVersionUID = 8833833261100295587L;

    public List<USER> getUSERS() {
        return uSERS;
    }

    public void setUSERS(List<USER> uSERS) {
        this.uSERS = uSERS;
    }

}
