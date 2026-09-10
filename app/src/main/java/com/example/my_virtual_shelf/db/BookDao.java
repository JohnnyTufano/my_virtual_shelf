package com.example.my_virtual_shelf.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {

    @Insert
    void insert(Book book);

    @Delete
    void delete(Book book);

    @Update
    void update(Book book);

    @Query("SELECT * FROM books ORDER BY id DESC")
    List<Book> getAllBooks();
}