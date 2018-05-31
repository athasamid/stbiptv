package com.synergics.stb.iptv.leanback.models.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.synergics.stb.iptv.leanback.models.TVCategories;

import java.util.List;
@Dao
public interface TVCategoriesDao {
    @Query("SELECT * FROM tv_categories")
    List<TVCategories> all();

    @Query("SELECT * FROM tv_categories WHERE nama=:nama")
    TVCategories getByNama(String nama);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TVCategories categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(TVCategories... categories);

    @Update
    void update(TVCategories... categories);

    @Delete
    void delete(TVCategories... categories);
}
