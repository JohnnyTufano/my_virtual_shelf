package com.example.my_virtual_shelf.api;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenLibraryService {
    @GET("api/books")
    Call<Map<String, OpenLibraryResponse>> getBookInfo(
            @Query("bibkeys") String bibkeys,
            @Query("format") String format,
            @Query("jscmd") String jscmd
    );
}