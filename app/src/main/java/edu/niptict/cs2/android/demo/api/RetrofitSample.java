package edu.niptict.cs2.android.demo.api;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.niptict.cs2.android.demo.db.entity.ContributorEntity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitSample {

    public static void main(String... args) throws IOException {
        // Create a GithubService Implementation
        GithubService service = RESTClient.retrofit().create(GithubService.class);

        // Create a request
        Call<List<ContributorEntity>> contributorCall = service.listContributors("square", "retrofit");

        // Call synchronously
//        Response<List<Contributor>> response = contributorCall.execute();
//        if (response.isSuccessful()) {
//            List<Contributor> body = response.body();
//            for (Contributor contributor : body) {
//                System.out.println(contributor.getLogin());
//            }
//        } else {
//            int code = response.code();
//            System.out.println("Error code: " + code);
//        }

        // Call asynchronously
        contributorCall.enqueue(new Callback<List<ContributorEntity>>() {
            @Override
            public void onResponse(Call<List<ContributorEntity>> call, Response<List<ContributorEntity>> response) {
                if (response.isSuccessful()) {
                    List<ContributorEntity> contributors = response.body();
                    for (ContributorEntity contributor : contributors) {
                        System.out.println(contributor.getLogin());
                    }
                } else {
                    int code = response.code();
                    Log.e("request", "Request error code: "+code);
                }
            }

            @Override
            public void onFailure(Call<List<ContributorEntity>> call, Throwable t) {
                Log.e("request", "Fail to load contributors. Please check your connection.");
            }
        });
    }

}
