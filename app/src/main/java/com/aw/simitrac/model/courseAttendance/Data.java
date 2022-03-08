
package com.aw.simitrac.model.courseAttendance;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("courseAttendance")
    @Expose
    private List<CourseAttendance> courseAttendance = null;
    private final static long serialVersionUID = 7284310022447232794L;

    public List<CourseAttendance> getCourseAttendance() {
        return courseAttendance;
    }

    public void setCourseAttendance(List<CourseAttendance> courseAttendance) {
        this.courseAttendance = courseAttendance;
    }

}
