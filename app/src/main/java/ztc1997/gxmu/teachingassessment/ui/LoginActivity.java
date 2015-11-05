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

package ztc1997.gxmu.teachingassessment.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ztc1997.gxmu.teachingassessment.BaseApplication;
import ztc1997.gxmu.teachingassessment.R;
import ztc1997.gxmu.teachingassessment.bean.StudentLoginInfo;
import ztc1997.gxmu.teachingassessment.util.PostUtils;

/**
 * 这个Activity是由SDK自动生成的
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            doLogin(username, password);
        }
    }

    private void doLogin(String username, String password) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.msg_logining));
        progressDialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        map.put("roleNum", "5");
        StringRequest request = new StringRequest(Request.Method.POST, PostUtils.mergeUrlActionParams("doLoginDquserMobile", map), response -> {
            StudentLoginInfo studentLoginInfo = ((List<StudentLoginInfo>) BaseApplication.getGson()
                    .fromJson(response, new TypeToken<List<StudentLoginInfo>>() {
                    }.getType())).get(0);
            progressDialog.dismiss();
            if (studentLoginInfo.getStudentId() == null) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                return;
            }
            BaseApplication.setStudentLoginInfo(studentLoginInfo);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, e -> {
            CrashReport.postCatchedException(e);
            progressDialog.dismiss();
            Toast.makeText(this, R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
        });
        BaseApplication.getRequestQueue().add(request);
    }
}

