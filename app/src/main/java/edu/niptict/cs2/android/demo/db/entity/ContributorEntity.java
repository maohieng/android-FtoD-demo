package edu.niptict.cs2.android.demo.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import edu.niptict.cs2.android.demo.model.Contributor;

@Entity(tableName = "contributors",
        indices = {@Index(value = {"login"}, unique = true)})
public class ContributorEntity implements Contributor {

    @PrimaryKey(autoGenerate = true)
    long id;

    /**
     * This field is indexed to be unique so that the database will ignore insert when its value exists.
     */
    String login;

    @ColumnInfo(name = "avatar_url")
    String avatarUrl;

    public ContributorEntity() {

    }

    // Used for logging
    @NonNull
    @Override
    public String toString() {
        return login;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getAvatarUrl() {
        return avatarUrl;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////

    public void setId(long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
