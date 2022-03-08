
package com.aw.simitrac.model.courseAttendance;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CourseAttendance implements Serializable
{

    @SerializedName("Attendance")
    @Expose
    private Attendance attendance;
    @SerializedName("Student")
    @Expose
    private Student student;
    private final static long serialVersionUID = -8740749507441750807L;

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

}
