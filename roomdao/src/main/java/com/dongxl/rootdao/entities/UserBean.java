package com.dongxl.rootdao.entities;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

//@Entity //表名默认类名
@Entity(tableName = "users")//表名改写成users
//@Entity(tableName = "users", primaryKeys = {"firstName", "last_name"})//表名改写成users 并设置一个多个主键 不能与字段id PrimaryKey 一起用
//@Entity(indices = {@Index("firstName"), @Index(value = {"last_name", "age"})})//@Entity的indices属性来给表格添加索引 可以添加一个或者多个 索引非唯一
//@Entity(indices = {@Index(value = {"first_name", "last_name"}, unique = true)}) //unique = true索引唯一
public class UserBean {
    //    @PrimaryKey
    @PrimaryKey(autoGenerate = true)//主键并自动生成
//    @ColumnInfo(name = "_id")
//    @NonNull //是否可以为空
    public int id;

    //    @ColumnInfo(name = "first_name")
    public String firstName;//columninfo默认字段名firstName

    @PrimaryKey //可以设置多个主键
    @ColumnInfo(name = "last_name")
    public String lastName;

    public String age;

//    @Embedded
    //注解来表示嵌入 对象和对象之间是有嵌套关系的 这时表有User表包含的列有：id, firstName,lastName,age, street, state, city, and post_code
    @Embedded(prefix = "loc_")//注解来表示嵌入 对象和对象之间是有嵌套关系的 这时表有User表包含的列有：id, firstName,lastName,age, loc_street, loc_state, loc_city, and loc_post_code
    public AddressBean addressBean;

    @Ignore //忽略这个字段
            Bitmap picture;
}
