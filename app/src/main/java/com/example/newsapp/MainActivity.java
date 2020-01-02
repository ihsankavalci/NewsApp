package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;
import androidx.room.Room;

import com.example.newsapp.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String API_KEY = "f81ab2c2216f460382c42e04e6b7cbd4";
    ListView listNews;
    ProgressBar loader;
    LinearLayout item;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listNews = findViewById(R.id.listNews);
        loader = findViewById(R.id.loader);
        listNews.setEmptyView(loader);
        item = findViewById(R.id.item);

        getNews news = new getNews();
        news.execute();

    }

    class getNews extends AsyncTask<String, Void, List<News>> {
        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        protected List<News> doInBackground(String... args) {
            try {
                if (isNetworkConnected()) {
                    String xml = HelperService.getRequest("https://newsapi.org/v2/top-headlines?country=tr&apiKey=" + API_KEY);
                    JSONObject jsonResponse = new JSONObject(xml);
                    JSONArray jsonArray = jsonResponse.optJSONArray("articles");
                    List<News> allNews = Arrays.asList(new Gson().fromJson(jsonArray.toString(), News[].class));
                    return allNews;

                } else {
                    NewsDatabase database = Room.databaseBuilder(getApplicationContext(),
                            NewsDatabase.class, "My-Database")
                            .allowMainThreadQueries()
                            .build();

                    NewsDao dao = database.newsDao();
                    List<News> allNews = dao.getAllNews();
                    String json = new GsonBuilder().setPrettyPrinting().create().toJson(allNews);
                    return allNews;
                }
            }
            catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Get News Error", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<News> allNews) {
            NewsDatabase database = Room.databaseBuilder(getApplicationContext(),
                    NewsDatabase.class, "My-Database")
                    .allowMainThreadQueries()
                    .build();

            NewsDao dao = database.newsDao();
            if (isNetworkConnected()) {

                dao.clearTable();
            }

            for (News news: allNews) {
                HashMap<String, String> map = new HashMap<>();
                map.put("author", news.getAuthor());
                map.put("title",  news.getTitle());
                map.put("description",  news.getDescription());
                map.put("urlToImage",  news.getUrlToImage());
                map.put("publishedAt",  news.getPublishedAt());
                map.put("content",  news.getContent());

                if (isNetworkConnected()) {
                    dao.insertNews(news);
                }

                dataList.add(map);
            }

            ListNewsAdapter adapter = new ListNewsAdapter(MainActivity.this, dataList);
            listNews.setAdapter(adapter);

            listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("image", dataList.get(position).get("urlToImage"));
                    intent.putExtra("author", dataList.get(position).get("author"));
                    intent.putExtra("title", dataList.get(position).get("title"));
                    intent.putExtra("content", dataList.get(position).get("content"));
                    intent.putExtra("publishedAt", dataList.get(position).get("publishAt"));
                    startActivity(intent);
                }
            });


        }
    }

}