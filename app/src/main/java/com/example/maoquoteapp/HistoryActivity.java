package com.example.maoquoteapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class HistoryActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MaoQuotePrefs";
    private static final String FAVORITES_KEY = "favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // 设置红色主题
        setTheme(R.style.AppTheme);

        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 获取引用
        TextView historyTitle = findViewById(R.id.historyTitle);
        Button clearHistoryButton = findViewById(R.id.clearHistoryButton);
        ListView historyListView = findViewById(R.id.historyListView);

        // 设置标题
        historyTitle.setText("历史记录");

        // 获取历史记录
        String favorites = sharedPreferences.getString(FAVORITES_KEY, "");
        if (favorites.isEmpty()) {
            Toast.makeText(this, "暂无历史记录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 分割历史记录
        ArrayList<String> historyList = new ArrayList<>(Arrays.asList(favorites.split(";")));

        // 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);

        // 设置清除历史记录按钮点击事件
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });

        // 设置列表项点击事件
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuote = historyList.get(position);
                Toast.makeText(HistoryActivity.this, "已选择: " + selectedQuote, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearHistory() {
        // 清除历史记录
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(FAVORITES_KEY);
        editor.apply();

        Toast.makeText(this, "历史记录已清除", Toast.LENGTH_SHORT).show();
        finish();
    }
}