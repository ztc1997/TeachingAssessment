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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ztc1997.gxmu.teachingassessment.BaseApplication;
import ztc1997.gxmu.teachingassessment.R;
import ztc1997.gxmu.teachingassessment.util.EntryImpl;

public class MainActivity extends BaseActivity {
    private long lastBackPress;
    private SliderLayout sliderLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initSliderLayout();
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        List<Map.Entry<Integer, Integer>> buttonList = new ArrayList<>();
        buttonList.add(new EntryImpl<>(R.string.item_main_info, R.mipmap.ic_main_info));
        buttonList.add(new EntryImpl<>(R.string.title_activity_assess_recycler, R.mipmap.ic_main_assess));
        buttonList.add(new EntryImpl<>(R.string.title_activity_about, android.R.drawable.ic_menu_info_details));
        recyclerView.setAdapter(new MainAdapter(buttonList));

    }

    private void initSliderLayout() {
        sliderLayout = (SliderLayout) findViewById(R.id.slider);
        sliderLayout.addSlider(new DefaultSliderView(this).image(R.mipmap.bg_main_1));
        sliderLayout.addSlider(new DefaultSliderView(this).image(R.mipmap.bg_main_1a));
        sliderLayout.addSlider(new DefaultSliderView(this).image(R.mipmap.bg_main_2));
        sliderLayout.addSlider(new DefaultSliderView(this).image(R.mipmap.bg_main_2a));
        sliderLayout.addSlider(new DefaultSliderView(this).image(R.mipmap.bg_main_3));
        sliderLayout.addSlider(new DefaultSliderView(this).image(R.mipmap.bg_main_3a));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderLayout.stopAutoCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderLayout.startAutoCycle();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastBackPress < 2000) {
            super.onBackPressed();
        } else {
            lastBackPress = System.currentTimeMillis();
            Snackbar.make(sliderLayout, R.string.snack_press_back_again_to_exit, Snackbar.LENGTH_SHORT).show();
        }
    }

    private class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
        private List<Map.Entry<Integer, Integer>> buttonList;

        private MainAdapter(List<Map.Entry<Integer, Integer>> buttonList) {
            this.buttonList = buttonList;
        }

        @Override
        public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_recycler_main, parent, false);
            return new MainViewHolder(v, MainActivity.this);
        }

        @Override
        public void onBindViewHolder(MainViewHolder holder, int position) {
            Map.Entry<Integer, Integer> entry = buttonList.get(position);
            holder.setTextRes(entry.getKey());
            holder.image.setImageResource(entry.getValue());
            holder.image.post(() -> {
                ViewGroup.LayoutParams layoutParams = holder.image.getLayoutParams();
                layoutParams.height = holder.image.getWidth();
                holder.image.setLayoutParams(layoutParams);
            });
        }

        @Override
        public int getItemCount() {
            return buttonList.size();
        }
    }

    private static class MainViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        private TextView text;
        private int textRes;

        public MainViewHolder(View itemView, Activity activity) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(view -> {
                switch (textRes) {
                    case R.string.item_main_info:
                        activity.startActivity(new Intent(activity, InfoActivity.class));
                        break;

                    case R.string.title_activity_assess_recycler:
                        activity.startActivity(new Intent(activity, AssessListActivity.class));
                        break;

                    case R.string.title_activity_about:
                        activity.startActivity(new Intent(activity, AboutActivity.class));
                        break;
                }
            });
        }


        public void setTextRes(int textRes) {
            this.textRes = textRes;
            text.setText(textRes);
        }
    }

}
