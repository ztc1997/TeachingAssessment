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
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ztc1997.gxmu.teachingassessment.BaseApplication;
import ztc1997.gxmu.teachingassessment.R;
import ztc1997.gxmu.teachingassessment.util.PostUtils;

public class ResetPwdActivity extends BaseActivity {
    private static final String TAG = AssessListActivity.class.getSimpleName();
    private EditText password, check;
    private Button btnResetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        password = (EditText) findViewById(R.id.password);
        check = (EditText) findViewById(R.id.check);
        btnResetPwd = (Button) findViewById(R.id.btn_reset_pwd);
        btnResetPwd.setOnClickListener(view -> {
            String pwd = password.getText().toString();
            String pwd2 = check.getText().toString();
            if (!pwd.equals(pwd2)) {
                check.setError(getString(R.string.error_reset_pwd_not_equals));
                return;
            }

            if (pwd.length() < 4) {
                password.setError(getString(R.string.error_reset_pwd_too_short));
                return;
            }
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.msg_reseting_pwd));
            progressDialog.show();

            new AlertDialog.Builder(this)
                    .setPositiveButton(android.R.string.ok, ((dialog, which) -> {
                        Map<String, String> params = new HashMap<>();
                        params.put("roleNum", BaseApplication.getStudentLoginInfo().getRoleNum());
                        params.put("studentId", BaseApplication.getStudentLoginInfo().getStudentId());
                        params.put("newPwd", pwd);
                        StringRequest request = new StringRequest(Request.Method.POST, PostUtils.mergeUrlActionParams("doResetPwdMobile", params),
                                response -> {
                                    try {
                                        JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
                                        if ("ok".equals(jsonObject.getString("res"))) {
                                            Toast.makeText(ResetPwdActivity.this, R.string.msg_reset_pwd_sucess, Toast.LENGTH_SHORT).show();
                                            finish();
                                            return;
                                        }
                                        password.setError(getString(R.string.error_reset_pwd_failed));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, e -> {
                            CrashReport.postCatchedException(e);
                            progressDialog.dismiss();
                            Toast.makeText(this, R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
                        });
                        request.setTag(TAG);
                        BaseApplication.getRequestQueue().add(request);
                    }))
                    .setNegativeButton(android.R.string.cancel, null)
                    .setTitle(R.string.title_dialog_reset_pwd)
                    .setMessage(R.string.msg_dialog_reset_pwd)
                    .show();

        });
    }

    @Override
    public void finish() {
        super.finish();
        BaseApplication.getRequestQueue().cancelAll(TAG);
    }
}
