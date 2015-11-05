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

package ztc1997.gxmu.teachingassessment;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashSet;
import java.util.Set;

import ztc1997.gxmu.teachingassessment.bean.StudentLoginInfo;

public class BaseApplication extends Application {
    private static final String PREFERENCES_NAME_STUDENT_LOGIN_INFO = "student_login_info";
    private static final Gson gson = new Gson();
    private static RequestQueue requestQueue;
    private static StudentLoginInfo studentLoginInfo;
    private static BaseApplication instance;
    public static Set<Activity> activities = new HashSet<>();

    public static Gson getGson() {
        return gson;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static StudentLoginInfo getStudentLoginInfo() {
        return studentLoginInfo;
    }

    public static void setStudentLoginInfo(StudentLoginInfo studentLoginInfo) {
        BaseApplication.studentLoginInfo = studentLoginInfo;
        SharedPreferences preferences = instance.getSharedPreferences(PREFERENCES_NAME_STUDENT_LOGIN_INFO, MODE_PRIVATE);
        preferences.edit().putString("cgroupId", studentLoginInfo.getCgroupId())
                .putString("jkclassId", studentLoginInfo.getJkclassId())
                .putString("res", studentLoginInfo.getRes())
                .putString("roleNum", studentLoginInfo.getRoleNum())
                .putString("studentId", studentLoginInfo.getStudentId())
                .putString("studentname", studentLoginInfo.getStudentname())
                .apply();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashReport.initCrashReport(this, "900010772", BuildConfig.DEBUG);
        requestQueue = Volley.newRequestQueue(this);
        loadStudentInfo();
    }

    private void loadStudentInfo() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME_STUDENT_LOGIN_INFO, MODE_PRIVATE);
        StudentLoginInfo studentLoginInfo = new StudentLoginInfo(preferences.getString("jkclassId", null),
                preferences.getString("res", null),
                preferences.getString("roleNum", null),
                preferences.getString("cgroupId", null),
                preferences.getString("studentname", null),
                preferences.getString("studentId", null));

        if (studentLoginInfo.getStudentId() != null) {
            BaseApplication.studentLoginInfo = studentLoginInfo;
        }
    }

    public static void logOut() {
        SharedPreferences preferences = instance.getSharedPreferences(PREFERENCES_NAME_STUDENT_LOGIN_INFO, MODE_PRIVATE);
        preferences.edit().remove("cgroupId")
                .remove("jkclassId")
                .remove("res")
                .remove("roleNum")
                .remove("studentId")
                .remove("studentname")
                .apply();
        studentLoginInfo = null;
        for (Activity activity : activities) {
            try {
                activity.finish();
            } catch (Throwable ignored) {
            }
        }
        activities.clear();
        final Intent intent = instance.getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
        instance.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
