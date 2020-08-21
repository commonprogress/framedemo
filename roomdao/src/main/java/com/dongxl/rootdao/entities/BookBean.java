package com.dongxl.rootdao.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "books"/*, foreignKeys = @ForeignKey(entity = UserBean.class,
        parentColumns = "id",
        childColumns = "user_id")*/)//设置外键 books表的user_id字段 关联 users 表的 id字段
public class BookBean {
    @PrimaryKey
    public int bookId;

    public String title;

    @ColumnInfo(name = "user_id")
    public int userId;
}
