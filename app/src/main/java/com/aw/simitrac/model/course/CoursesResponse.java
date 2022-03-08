
package com.aw.simitrac.model.course;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoursesResponse implements Serializable
{

    @SerializedName("Course")
    @Expose
    private Course course;
    @SerializedName("CourseCategory")
    @Expose
    private CourseCategory courseCategory;
    private final static long serialVersionUID = -6215898169795416111L;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseCategory getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(CourseCategory courseCategory) {
        this.courseCategory = courseCategory;
    }

}
