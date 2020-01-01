package com.example.newsapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NewsDao {

    @Insert
    void insertNews(News news);

    @Insert
    void insertMultipleNews(List<News> news);

    @Delete
    void deleteNews(News news);

    @Update
    void updateNews(News news);

    @Query("SELECT * FROM news")
    List<News> getAllNews();

    @Query("SELECT * FROM news WHERE id = :newsId")
    News getOneNews(int newsId);

    @Query("DELETE FROM news")
    public void clearTable();
}