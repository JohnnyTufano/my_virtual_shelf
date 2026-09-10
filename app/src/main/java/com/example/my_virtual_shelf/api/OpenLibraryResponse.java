package com.example.my_virtual_shelf.api;

import java.util.List;

public class OpenLibraryResponse {
    public String title;
    public List<Author> authors;
    public Cover cover;

    public static class Author {
        public String name;
    }

    public static class Cover {
        public String medium;
        public String large;
    }

    public String getAuthorsString() {
        if (authors == null || authors.isEmpty()) return "Autore sconosciuto";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i).name);
            if (i < authors.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}