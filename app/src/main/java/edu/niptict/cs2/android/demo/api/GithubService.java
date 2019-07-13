package edu.niptict.cs2.android.demo.api;

import java.util.List;

import edu.niptict.cs2.android.demo.db.entity.ContributorEntity;
import edu.niptict.cs2.android.demo.model.Contributor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GithubService {

    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<ContributorEntity>> listContributors(@Path("owner") String owner,
                                                   @Path("repo") String repo);

    // TODO: 7/13/2019 this is for example only! Don't use
    @Deprecated
    @POST("/users/login")
    Call<Void> login(@Query("username") String username,
                     @Query("password") String password);

}
