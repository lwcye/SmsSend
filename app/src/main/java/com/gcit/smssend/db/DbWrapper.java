package com.gcit.smssend.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.util.LogUtils;
import com.gcit.smssend.db.dao.DaoMaster;
import com.gcit.smssend.db.dao.DaoSession;
import com.gcit.smssend.db.dao.SmsModelDao;
import com.gcit.smssend.utils.MigrationHelper;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2017/10/24
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class DbWrapper {
    /** 数据库名称 */
    public static final String DB_NAME = "app.db";

    /** 数据库帮助类 */
    private static AppOpenHelper mHelper;
    /** Dao管理 */
    private static DaoMaster mDaoMaster;
    /** Dao会话 */
    private static DaoSession mDaoSession;

    /**
     * 初始化函数
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        mHelper = new AppOpenHelper(context, DB_NAME, null);
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 获取会话
     *
     * @return 会话
     */
    public static DaoSession getSession() {
        return mDaoSession;
    }

    /**
     * 重庆城支撑库数据库
     */
    private static class AppOpenHelper extends DaoMaster.OpenHelper {
        /**
         * 构造函数
         *
         * @param context 上下文
         * @param name 数据库名
         */
        public AppOpenHelper(Context context, String name) {
            super(context, name);
        }

        /**
         * 构造函数
         *
         * @param context 上下文
         * @param name 数据库名
         * @param factory 工厂
         */
        public AppOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);

            LogUtils.d("upgrade db(" + DB_NAME + ") from " + oldVersion + " to " + newVersion);

            // 升级数据库
            MigrationHelper.migrate(db, SmsModelDao.class);
        }
    }
}
