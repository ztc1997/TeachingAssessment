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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import ztc1997.gxmu.teachingassessment.bean.AssessListItem;
import ztc1997.gxmu.teachingassessment.util.PostUtils;
import ztc1997.gxmu.teachingassessment.util.Utils;

public class AssessListActivity extends BaseActivity {
    private static final String TAG = AssessListActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private AssessListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assess_list);
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
    }

    private void refreshData() {
        StringRequest request = new StringRequest(Request.Method.POST, PostUtils.mergeUrlActionParams("ListXspjMobile", null),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
                        if (adapter == null) {
                            adapter = new AssessListAdapter(jsonObject.getString("strs"));
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.setStr(jsonObject.getString("strs"));
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        CrashReport.postCatchedException(e);
                    }
                }, e -> {
            CrashReport.postCatchedException(e);
            Toast.makeText(this, R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
            finish();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("stuid", BaseApplication.getStudentLoginInfo().getStudentId());
                return params;
            }
        };
        request.setTag(TAG);
        BaseApplication.getRequestQueue().add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void finish() {
        super.finish();
        BaseApplication.getRequestQueue().cancelAll(TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AssessListAdapter extends RecyclerView.Adapter<AssessListViewHolder> {
        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_NORMAL = 1;
        private List<AssessListItem> assessListItems = new ArrayList<>();
        private String dateRange;
        private int assessedCount = 0;

        public AssessListAdapter(String str) {
            setStr(str);
        }

        public void setStr(String str) {
            assessListItems.clear();
            String[] strings = str.split("~");
            dateRange = strings[0];
            for (int i = 1; i < strings.length; i++) {
                String[] strings1 = strings[i].split(";");
                boolean isAssessed = strings1[5].equals("1");
                if (isAssessed)
                    assessedCount++;
                AssessListItem item = new AssessListItem(strings1[2],
                        strings1[1], strings1[3], strings1[4], strings1[0], isAssessed);
                assessListItems.add(item);
            }
        }

        @Override
        public AssessListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_list_assess, parent, false);
            return new AssessListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(AssessListViewHolder holder, int position) {
            if (position == 0) {
                holder.content.setText(Utils.getAndFormatCharSequence(AssessListActivity.this, R.string.assess_recycler_header,
                        dateRange, assessListItems.size(), assessedCount, assessListItems.size() - assessedCount));
            } else {
                AssessListItem item = assessListItems.get(position - 1);
                holder.content.setText(Utils.getAndFormatCharSequence(AssessListActivity.this, R.string.assess_recycler_normal,
                        item.getName(), item.getContent(), item.getType(), item.getTeacher(), item.isAssessed() ? "是" : "否"));
                if (item.isAssessed())
                    holder.itemView.setClickable(false);
                else
                    holder.itemView.setOnClickListener(view -> {
                        if (item.isAssessed())
                            return;
                        Intent intent = new Intent(AssessListActivity.this, AssessActivity.class);
                        intent.putExtra("skcid", item.getSkcid());
                        startActivity(intent);
                    });
            }
        }

        @Override
        public int getItemCount() {
            return assessListItems.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                default:
                    return VIEW_TYPE_NORMAL;

                case 0:
                    return VIEW_TYPE_HEADER;
            }
        }
    }

    private static class AssessListViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView content;

        public AssessListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
