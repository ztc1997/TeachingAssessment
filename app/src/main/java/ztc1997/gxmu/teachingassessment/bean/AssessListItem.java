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

public class AssessListItem implements Serializable {
    private String name, content, type, teacher, skcid;
    private boolean isAssessed;

    public AssessListItem(String name, String content, String type, String teacher, String skcid, boolean isAssessed) {
        this.name = name;
        this.content = content;
        this.type = type;
        this.teacher = teacher;
        this.skcid = skcid;
        this.isAssessed = isAssessed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public boolean isAssessed() {
        return isAssessed;
    }

    public void setIsAssessed(boolean isAssessed) {
        this.isAssessed = isAssessed;
    }

    public String getSkcid() {
        return skcid;
    }

    public void setSkcid(String skcid) {
        this.skcid = skcid;
    }
}
