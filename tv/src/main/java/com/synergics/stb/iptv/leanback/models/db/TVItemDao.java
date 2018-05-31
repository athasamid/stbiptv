package com.synergics.stb.iptv.leanback.models.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.synergics.stb.iptv.leanback.models.TVItems;

import java.util.List;

@Dao
public interface TVItemDao {
    @Query("SELECT * FROM tv_items")
    List<TVItems> getAll();

    @Query("SELECT * FROM tv_items WHERE categoryId=:categoryId")
    List<TVItems> getByCategory(int categoryId);

    @Query("SELECT * FROM tv_items WHERE id=:id")
    TVItems getById(int id);

    @Insert
    void insert(TVItems... items);

    @Insert
    void insert(List<TVItems> items);

    @Update
    void update(TVItems... items);

    @Delete
    void delete(TVItems... items);
}
