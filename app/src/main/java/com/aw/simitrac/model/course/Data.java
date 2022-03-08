
package com.aw.simitrac.model.course;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("CoursesResponse")
    @Expose
    private List<CoursesResponse> coursesResponse = null;
    private final static long serialVersionUID = 1192857044859769165L;

    public List<CoursesResponse> getCoursesResponse() {
        return coursesResponse;
    }

    public void setCoursesResponse(List<CoursesResponse> coursesResponse) {
        this.coursesResponse = coursesResponse;
    }

}
