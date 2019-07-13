package edu.niptict.cs2.android.demo.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.niptict.cs2.android.demo.db.entity.ContributorEntity;

@Dao
public interface ContributorDAO {

    ///////////////////////////////////////////////////////////////////////////
    // Synchronous
    ///////////////////////////////////////////////////////////////////////////

    // ignore inserting them when find conflict entity
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insert(List<ContributorEntity> contributors);

    @Delete
    public void deleteAll(List<ContributorEntity> contributors);

    @Query("select * from contributors")
    public List<ContributorEntity> listAll();

    @Query("select * from contributors where login = :login")
    public List<ContributorEntity> listByLogin(String login);

    @Query("select * from contributors where login in (:logins)")
    public List<ContributorEntity> listByLogins(List<String> logins);

    ///////////////////////////////////////////////////////////////////////////
    // Asynchronous
    ///////////////////////////////////////////////////////////////////////////

    @Query("select * from contributors")
    public LiveData<List<ContributorEntity>> listAllAsync();

    @Query("select * from contributors where login = :login")
    public LiveData<List<ContributorEntity>> listByLoginAsync(String login);

    @Query("select * from contributors where login in (:logins)")
    public LiveData<List<ContributorEntity>> listByLoginsAsync(List<String> logins);

}
