package com.dongxl.rootdao;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.dongxl.rootdao.daos.BookDao;
import com.dongxl.rootdao.daos.FlowDao;
import com.dongxl.rootdao.daos.UserDao;
import com.dongxl.rootdao.entities.BookBean;
import com.dongxl.rootdao.entities.FlowBean;
import com.dongxl.rootdao.entities.UserBean;
import com.dongxl.rootdao.utils.Converters;
import com.dongxl.rootdao.utils.DataUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//我们需要把所有的 model 对象 全都方式 @Database 的 entities 中，增删改 model 后，我们应该更新 version
@Database(entities = {
        UserBean.class,
        BookBean.class,
        FlowBean.class
}, version = RoomDatabaseManager.dbVersion)
// sqlite 只支持 NULL、INTEGER、REAL、TEXT、BLOB 这些类型，如果是 Date 或者自定义的枚举等类型，则需要声明 @TypeConverters 来做类型转换了
@TypeConverters(value = {DataUtils.class, Converters.class})
public abstract class RoomDatabaseManager extends RoomDatabase {

    private final static String dbName = "room_test.db";
    protected final static int dbVersion = 1;

    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private Context mContext;

    public RoomDatabaseManager() {
        super();
    }

    public RoomDatabaseManager(Context context) {
        super();
        this.mContext = context.getApplicationContext();
    }

    public static RoomDatabaseManager initDatabaseCreate(Context context) {
        return initDatabaseCreate(context, false);
    }

    public static RoomDatabaseManager initDatabaseCreate(Context context, boolean useTestDatabase) {
        RoomDatabase.Builder<RoomDatabaseManager> builder;
        if (useTestDatabase) {
            // 可以通过 inMemoryDatabaseBuilder 来构建内存Db,可用于测试
            builder = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), RoomDatabaseManager.class)
                    .allowMainThreadQueries();
        } else {
            builder = Room.databaseBuilder(context.getApplicationContext(), RoomDatabaseManager.class, dbName);
        }
        return builder.allowMainThreadQueries()//允许主线程做查询操作，默认不允许
                .fallbackToDestructiveMigration()//设置迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃 相似与@see #fallbackToDestructiveMigrationOnDowngrade()
//                .fallbackToDestructiveMigrationFrom(2)//设置从某个版本开始迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃
                .addCallback(new RoomCallback())// 监听数据库，创建和打开的操作
                .addMigrations(getMigrationSet())//设置数据库升级(迁移)的逻辑
//                .setQueryExecutor(databaseWriteExecutor)
                .build();
    }

    /**
     * 数据库升级配置
     *
     * @return
     */
    private static Migration[] getMigrationSet() {
        return new Migration[]{
                MIGRATION_1_2,
                MIGRATION_2_3
        };
    }

    /**
     * 数据库版本 1->2 user表格新增了age列
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN age Text");
        }
    };

    /**
     * 数据库版本 2->3 新增book表格
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `books` (`bookId` INTEGER PRIMARY KEY autoincrement, `title` TEXT , `user_id` INTEGER)");
        }
    };

    static class RoomCallback extends Callback {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
            super.onDestructiveMigration(db);
        }
    }

    public abstract UserDao userDao();

    public abstract BookDao bookDao();

    public abstract FlowDao flowDao();

}
