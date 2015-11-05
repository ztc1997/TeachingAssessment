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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import ztc1997.gxmu.teachingassessment.util.Utils;

public class InfoActivity extends BaseActivity {
    private static final String TAG = InfoActivity.class.getSimpleName();
    private TextView content;
    private Button btnResetPwd, btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initContent();
        btnResetPwd = (Button) findViewById(R.id.btn_reset_pwd);
        btnLogOut = (Button) findViewById(R.id.btn_log_out);
        btnResetPwd.setOnClickListener(view -> startActivity(new Intent(this, ResetPwdActivity.class)));
        btnLogOut.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_log_out)
                    .setMessage(R.string.msg_dialog_log_out)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        BaseApplication.logOut();
                    }).show();
        });
    }

    private void initContent() {
        content = (TextView) findViewById(R.id.content);

        Map<String, String> params = new HashMap<>();
        params.put("studentId", BaseApplication.getStudentLoginInfo().getStudentId());
        params.put("roleNum", BaseApplication.getStudentLoginInfo().getRoleNum());
        StringRequest request = new StringRequest(Request.Method.POST,
                PostUtils.mergeUrlActionParams("getGrxxMobile", params), response -> {
            try {
                JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
                String[] strs = jsonObject.getString("str").split(",");
                content.setText(Utils.getAndFormatCharSequence(this, R.string.content_info,
                        strs[1], strs[2], strs[3], strs[4], strs[5], strs[6], strs[7], strs[8], strs[9], strs[10]));
                findViewById(R.id.progress_overlay).setVisibility(View.GONE);
            } catch (JSONException e) {
                CrashReport.postCatchedException(e);
            }
        }, e -> {
            CrashReport.postCatchedException(e);
            Toast.makeText(this, R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
            finish();
        });
        request.setTag(TAG);
        BaseApplication.getRequestQueue().add(request);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public void finish() {
        super.finish();
        BaseApplication.getRequestQueue().cancelAll(TAG);
    }
}
