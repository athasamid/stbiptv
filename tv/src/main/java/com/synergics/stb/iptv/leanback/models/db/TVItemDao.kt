package com.synergics.stb.iptv.leanback.models.db

import androidx.room.*
import com.synergics.stb.iptv.leanback.models.TVItems

@Dao
interface TVItemDao {
    @get:Query("SELECT * FROM tv_items")
    val all: List<TVItems?>?

    @Query("SELECT * FROM tv_items WHERE categoryId=:categoryId")
    fun getByCategory(categoryId: Int): List<TVItems>

    @Query("SELECT * FROM tv_items WHERE id=:id")
    fun getById(id: Int): TVItems?

    @Insert
    fun insert(vararg items: TVItems?)

    @Insert
    fun insert(items: List<TVItems>?)

    @Update
    fun update(vararg items: TVItems?)

    @Delete
    fun delete(vararg items: TVItems?)

    @Query("delete from tv_items")
    fun clear()

    @Query("delete from sqlite_sequence where name='tv_items'")
    fun resetAI()
}