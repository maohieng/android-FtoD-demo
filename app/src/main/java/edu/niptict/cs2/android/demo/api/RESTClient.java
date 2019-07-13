package edu.niptict.cs2.android.demo.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTClient {
        private RESTClient() {
            //no instance
        }

        public static Retrofit retrofit() {
            return new Retrofit.Builder()
                    .baseUrl("https://api.github.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }