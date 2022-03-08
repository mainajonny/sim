
package com.aw.simitrac.model.student;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable
{

    @SerializedName("Student")
    @Expose
    private Student student;
    private final static long serialVersionUID = 1140478615307189002L;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

}
