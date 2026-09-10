package com.example.my_virtual_shelf.api;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    public class RetrofitClient {
        private static final String BASE_URL = "https://openlibrary.org/";
        private static Retrofit retrofit = null;

        public static OpenLibraryService getService() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit.create(OpenLibraryService.class);
        }
    }

