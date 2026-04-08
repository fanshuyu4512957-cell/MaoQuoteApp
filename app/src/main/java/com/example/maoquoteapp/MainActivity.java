package com.example.maoquoteapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView quoteText;
    private Button drawCardButton;
    private ProgressBar progressBar;
    private ImageView quoteImage;
    private Button favoriteButton;
    private Button shareButton;
    private Button historyButton;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MaoQuotePrefs";
    private static final String FAVORITES_KEY = "favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置红色主题
        setTheme(R.style.AppTheme);

        // 获取引用
        quoteText = findViewById(R.id.quoteText);
        drawCardButton = findViewById(R.id.drawCardButton);
        progressBar = findViewById(R.id.progressBar);
        quoteImage = findViewById(R.id.quoteImage);
        favoriteButton = findViewById(R.id.favoriteButton);
        shareButton = findViewById(R.id.shareButton);
        historyButton = findViewById(R.id.historyButton);

        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 设置按钮点击事件
        drawCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 这里将实现抽取卡牌的逻辑
                drawTodayCard();
            }
        });

        // 设置收藏按钮点击事件
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFavorites();
            }
        });

        // 设置分享按钮点击事件
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQuote();
            }
        });

        // 设置历史记录按钮点击事件
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 导航到历史记录页面
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void drawTodayCard() {
        // 显示进度条
        progressBar.setVisibility(View.VISIBLE);
        drawCardButton.setEnabled(false);

        // 执行网络请求获取语录
        new FetchQuoteTask().execute();
    }

    private class FetchQuoteTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                // 调用网络工具类获取语录
                String jsonResult = NetworkUtils.getMaoQuotes();
                if (jsonResult != null) {
                    // 解析JSON数据
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    JSONArray quotesArray = jsonObject.getJSONArray("quotes");

                    // 随机选择一条语录
                    int randomIndex = (int) (Math.random() * quotesArray.length());
                    JSONObject quoteObject = quotesArray.getJSONObject(randomIndex);

                    String content = quoteObject.getString("content");
                    String author = quoteObject.getString("author");

                    return content + " - " + author;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // 隐藏进度条
            progressBar.setVisibility(View.GONE);
            drawCardButton.setEnabled(true);

            if (result != null) {
                // 更新UI显示语录
                quoteText.setText(result);

                // 加载相关图片（示例：使用本地资源）
                loadQuoteImage();
            } else {
                // 显示错误信息
                Toast.makeText(MainActivity.this, "获取语录失败，请重试", Toast.LENGTH_SHORT).show();
                quoteText.setText("点击下方按钮抽取今日卡牌");
            }
        }
    }

    private void loadQuoteImage() {
        // 这里可以实现从网络加载图片的逻辑
        // 由于示例中使用了本地资源，我们可以根据语录内容选择不同的图片
        // 这里使用一个固定的图片作为示例
        quoteImage.setImageResource(R.drawable.quote_background);
    }

    private void saveToFavorites() {
        String currentQuote = quoteText.getText().toString();
        if (currentQuote.equals("点击下方按钮抽取今日卡牌")) {
            Toast.makeText(this, "请先抽取一张卡牌", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取现有的收藏
        String favorites = sharedPreferences.getString(FAVORITES_KEY, "");

        // 检查是否已收藏
        if (favorites.contains(currentQuote)) {
            Toast.makeText(this, "该语录已收藏", Toast.LENGTH_SHORT).show();
            return;
        }

        // 添加新收藏
        if (!favorites.isEmpty()) {
            favorites += ";";
        }
        favorites += currentQuote;

        // 保存到SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FAVORITES_KEY, favorites);
        editor.apply();

        Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
    }

    private void shareQuote() {
        String currentQuote = quoteText.getText().toString();
        if (currentQuote.equals("点击下方按钮抽取今日卡牌")) {
            Toast.makeText(this, "请先抽取一张卡牌", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建分享意图
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentQuote);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "毛选语录分享");

        // 启动分享对话框
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}