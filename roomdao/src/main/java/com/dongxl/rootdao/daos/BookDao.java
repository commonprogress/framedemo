package com.dongxl.rootdao.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;

import com.dongxl.rootdao.beans.UserPet;
import com.dongxl.rootdao.entities.BookBean;

import java.util.List;

@Dao
public interface BookDao {

//    @Query("SELECT * FROM books "
//            + "INNER JOIN users ON users.id = books.user_id "
//            + "WHERE users.firstName LIKE :userName")
//    List<BookBean> findBooksBorrowedByNameSync(String userName);

    @Query("SELECT users.firstName AS userName, books.title AS petName "
            + "FROM users, books "
            + "WHERE users.id = books.user_id")
    LiveData<List<UserPet>> loadUserAndPetNames();
}
