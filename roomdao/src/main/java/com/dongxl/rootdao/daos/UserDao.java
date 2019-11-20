package com.dongxl.rootdao.daos;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.dongxl.rootdao.beans.NameTuple;
import com.dongxl.rootdao.entities.UserBean;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//onConflict 表示插入发生冲突时 处理方法 REPLACE
    List<Long> insert(UserBean... users);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserBean userBean);

    @Insert(onConflict = OnConflictStrategy.ABORT)
//默认
    List<Long> insert(List<UserBean> lists);

    @Query("SELECT * FROM users")
    List<UserBean> getUserList();

    @Query("SELECT * FROM users")
    UserBean[] loadAllUsers();

    @Query("SELECT * FROM users WHERE firstName == :name")
    UserBean[] loadAllUsersByFirstName(String name);

    @Query("SELECT * FROM users WHERE age > :minAge LIMIT 5")
    Cursor loadRawUsersOlderThan(int minAge);

    @Query("SELECT * FROM users WHERE age BETWEEN :minAge AND :maxAge")
    UserBean[] loadAllUsersBetweenAges(int minAge, int maxAge);

    @Query("SELECT * FROM users WHERE firstName LIKE :search " + "OR last_name LIKE :search")
    List<UserBean> findUserWithName(String search);


    @Query("SELECT firstName, last_name FROM users")
    List<NameTuple> loadFullName();

    @Transaction// 使用 @Transaction 标示使用 transition
    //在查询的时候您可能需要传递一组(数组或者List)参数进去。
    @Query("SELECT firstName, last_name FROM users WHERE age IN (:ages)")
    List<NameTuple> loadUsersFromRegions(List<String> ages);

    //查询到结果的时候，UI能够自动更新。Room为了实现这一效果，查询的返回值的类型为LiveData。
//    @Query("SELECT firstName, last_name FROM users WHERE age IN (:ages)")
//    LiveData<List<NameTuple>> loadUsersFromRegionsSync(List<String> ages);

    @Query("SELECT * from users")
    Flowable<List<UserBean>> loadUser();

    @Delete
    void deleteUsers(UserBean... users);

    // @Query 并不是指查询数据库，而是执行数据库语句
    @Query("DELETE FROM users WHERE id=:id")
    void deleteUser(String id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateUsers(UserBean... users);


}

