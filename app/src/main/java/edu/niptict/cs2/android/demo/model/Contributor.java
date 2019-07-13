package edu.niptict.cs2.android.demo.model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

// Why interface here? Because it is a model. Model is model, not object... Lolz
// Then we now use ContributorEntity instead

public interface Contributor {

    public long getId();

    public String getLogin();

    @SerializedName("avatar_url")
    public String getAvatarUrl();
}
