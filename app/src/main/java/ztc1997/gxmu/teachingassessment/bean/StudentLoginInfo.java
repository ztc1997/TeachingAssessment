/*
 * Copyright 2015 Alex Zhang aka. ztc1997
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ztc1997.gxmu.teachingassessment.bean;

import java.io.Serializable;

public class StudentLoginInfo implements Serializable {
    private String jkclassId, res, roleNum, cgroupId, studentname, studentId;

    public StudentLoginInfo(String jkclassId, String res, String roleNum, String cgroupId, String studentname, String studentId) {
        this.jkclassId = jkclassId;
        this.res = res;
        this.roleNum = roleNum;
        this.cgroupId = cgroupId;
        this.studentname = studentname;
        this.studentId = studentId;
    }

    public String getJkclassId() {
        return jkclassId;
    }

    public String getRes() {
        return res;
    }

    public String getRoleNum() {
        return roleNum;
    }

    public String getCgroupId() {
        return cgroupId;
    }

    public String getStudentname() {
        return studentname;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public String toString() {
        return "jkclassId = " + jkclassId
                + "\nres = " + res
                + "\nroleNum = " + roleNum
                + "\ncgroupId = " + cgroupId
                + "\nstudentname = " + studentname
                + "\nstudentId = " + studentId;
    }
}
