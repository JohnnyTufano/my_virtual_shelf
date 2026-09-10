package com.example.my_virtual_shelf.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book {

        @PrimaryKey(autoGenerate = true)
        public int id;

        public String isbn;
        public String title;
        public String author;
        public String coverUrl;
        public String status; // Valori: "Da leggere", "In lettura", "Letto"

    public Book(String isbn, String title, String author, String coverUrl, String status) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.coverUrl = coverUrl;
            this.status = status;
        }
    }
}
