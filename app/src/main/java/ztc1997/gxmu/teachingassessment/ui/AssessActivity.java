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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ztc1997.gxmu.teachingassessment.BaseApplication;
import ztc1997.gxmu.teachingassessment.R;
import ztc1997.gxmu.teachingassessment.adapter.EmptyAdapter;
import ztc1997.gxmu.teachingassessment.bean.AssessItem;
import ztc1997.gxmu.teachingassessment.util.PostUtils;
import ztc1997.gxmu.teachingassessment.util.Utils;

public class AssessActivity extends BaseActivity {
    private static final String TAG = AssessActivity.class.getSimpleName();
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assess);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new EmptyAdapter(this));

        Map<String, String> params = new HashMap<>();
        params.put("skcid", getIntent().getStringExtra("skcid"));
        params.put("stuid", BaseApplication.getStudentLoginInfo().getStudentId());
        StringRequest request = new StringRequest(Request.Method.POST, PostUtils.mergeUrlActionParams("regXspjMobile", params),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
                        recyclerView.setAdapter(new AssessAdapter(jsonObject.getString("strs")));
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
    public void finish() {
        super.finish();
        BaseApplication.getRequestQueue().cancelAll(TAG);
    }

    private class AssessAdapter extends RecyclerView.Adapter<AssessViewHolder> {
        private List<AssessItem> assessItems = new ArrayList<>();
        private String[] strings;
        private Map<AssessItem, Integer> scores = new HashMap<>();

        private AssessAdapter(String strs) {
            strings = strs.split("~");
            for (int i = 1; i < strings.length; i++) {
                String[] strings1 = strings[i].split(";");
                AssessItem assessItem = new AssessItem(strings1[2], strings1[1], strings1[0], Integer.parseInt(strings1[3]));
                assessItems.add(assessItem);
            }
        }

        @Override
        public AssessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_NORMAL:
                    return new AssessViewHolder(getLayoutInflater().inflate(R.layout.item_assess, parent, false), VIEW_TYPE_NORMAL);

                case VIEW_TYPE_HEADER:
                    return new AssessViewHolder(getLayoutInflater().inflate(R.layout.header_assess, parent, false), VIEW_TYPE_HEADER);

                case VIEW_TYPE_FOOTER:
                    return new AssessViewHolder(getLayoutInflater().inflate(R.layout.footer_assess, parent, false), VIEW_TYPE_FOOTER);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(AssessViewHolder holder, int position) {
            switch (holder.viewType) {
                case VIEW_TYPE_NORMAL:
                    AssessItem item = assessItems.get(position - 1);
                    holder.content.setText(Utils.getAndFormatCharSequence(AssessActivity.this, R.string.assess_content, item.getContent(), item.getSubject(), item.getScore()));
                    Integer rating0 = scores.get(item);
                    holder.ratingBar.setRating(rating0 == null ? 0 : rating0);
                    holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                        if (fromUser)
                            scores.put(item, (int) (rating * item.getScore() / 5));
                    });
                    break;
                case VIEW_TYPE_HEADER:
                    String[] strings1 = strings[0].split(";");
                    holder.title.setText(strings1[5]);
                    holder.content.setText(Utils.getAndFormatCharSequence(AssessActivity.this, R.string.assess_header,
                            strings1[4], strings1[0], strings1[2], strings1[3], strings1[6], strings1[7], strings1[1], strings1[8]));
                    break;
                case VIEW_TYPE_FOOTER:
                    holder.btnCommit.setOnClickListener(view -> {
                        if (scores.size() < assessItems.size()) {
                            Snackbar.make(holder.btnCommit, R.string.error_assess_not_fill, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        int score = 0;
                        for (int i : scores.values()) {
                            score += i;
                        }
                        final int finalScore = score;
                        new AlertDialog.Builder(AssessActivity.this)
                                .setTitle(R.string.title_dialog_commit_assess)
                                .setMessage(getString(R.string.msg_dialog_commit_assess) + score)
                                .setPositiveButton(android.R.string.ok, ((dialog, which) -> {
                                    ProgressDialog progressDialog = new ProgressDialog(AssessActivity.this);
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage(getString(R.string.msg_commiting_assess));
                                    progressDialog.show();

                                    StringBuilder ids = new StringBuilder();
                                    StringBuilder scr = new StringBuilder();
                                    for (Map.Entry<AssessItem, Integer> entry : scores.entrySet()) {
                                        scr.append(entry.getValue()).append(";");
                                        ids.append(entry.getKey().getId()).append(";");
                                    }

                                    Map<String, String> params = new HashMap<>();
                                    params.put("skcid", getIntent().getStringExtra("skcid"));
                                    params.put("stuid", BaseApplication.getStudentLoginInfo().getStudentId());
                                    String yj = holder.feedback.getText().toString();
                                    params.put("yj", yj.equals("") ? "notyj" : yj);
                                    params.put("strs", scr.append("~").append(ids).append("~").append(finalScore).toString());
                                    System.out.println(params.get("strs"));
                                    StringRequest request = new StringRequest(Request.Method.POST, PostUtils.mergeUrlActionParams("doRegXspjMobile", params),
                                            response -> {
                                                try {
                                                    System.out.println(response);
                                                    JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
                                                    if ("success".equals(jsonObject.getString("flag"))) {
                                                        Toast.makeText(AssessActivity.this, R.string.msg_commit_assess_sucess, Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        return;
                                                    }
                                                    Toast.makeText(AssessActivity.this, R.string.msg_commit_assess_failed, Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }, e -> {
                                        CrashReport.postCatchedException(e);
                                        progressDialog.dismiss();
                                        Toast.makeText(AssessActivity.this, R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
                                    });
                                    BaseApplication.getRequestQueue().add(request);
                                }))
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return assessItems.size() + 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1)
                return VIEW_TYPE_FOOTER;
            switch (position) {
                default:
                    return VIEW_TYPE_NORMAL;
                case 0:
                    return VIEW_TYPE_HEADER;
            }
        }
    }

    private class AssessViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView content, title;
        public RatingBar ratingBar;
        public EditText feedback;
        public Button btnCommit;
        public int viewType;

        public AssessViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            this.itemView = itemView;
            content = (TextView) itemView.findViewById(R.id.content);
            switch (viewType) {
                case VIEW_TYPE_NORMAL:
                    ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
                    break;
                case VIEW_TYPE_HEADER:
                    title = (TextView) itemView.findViewById(R.id.title);
                    break;
                case VIEW_TYPE_FOOTER:
                    feedback = (EditText) itemView.findViewById(R.id.feedback);
                    btnCommit = (Button) itemView.findViewById(R.id.btn_commit);
                    break;
            }
        }
    }
}
