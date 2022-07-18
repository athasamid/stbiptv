package com.synergics.stb.iptv.leanback.models.db

import androidx.room.*
import com.synergics.stb.iptv.leanback.models.TVCategories

@Dao
interface TVCategoriesDao {
    @Query("SELECT * FROM tv_categories")
    fun all(): List<TVCategories?>?

    @Query("SELECT * FROM tv_categories WHERE nama=:nama")
    fun getByNama(nama: String?): TVCategories?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categories: TVCategories?): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg categories: TVCategories?): LongArray?

    @Update
    fun update(vararg categories: TVCategories?)

    @Delete
    fun delete(vararg categories: TVCategories?)

    @Query("delete from tv_categories")
    fun clear()

    @Query("delete from sqlite_sequence where name='tv_categories'")
    fun resetAI()
}