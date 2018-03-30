package com.hbln.smsintercept.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.hbln.smsintercept.db.bean.SuccessSmsBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SUCCESS_SMS_BEAN".
*/
public class SuccessSmsBeanDao extends AbstractDao<SuccessSmsBean, Long> {

    public static final String TABLENAME = "SUCCESS_SMS_BEAN";

    /**
     * Properties of entity SuccessSmsBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Create_time = new Property(0, long.class, "create_time", true, "_id");
        public final static Property Mobile = new Property(1, String.class, "mobile", false, "MOBILE");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
    }


    public SuccessSmsBeanDao(DaoConfig config) {
        super(config);
    }
    
    public SuccessSmsBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SUCCESS_SMS_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: create_time
                "\"MOBILE\" TEXT," + // 1: mobile
                "\"CONTENT\" TEXT);"); // 2: content
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SUCCESS_SMS_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SuccessSmsBean entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getCreate_time());
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(2, mobile);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SuccessSmsBean entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getCreate_time());
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(2, mobile);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public SuccessSmsBean readEntity(Cursor cursor, int offset) {
        SuccessSmsBean entity = new SuccessSmsBean( //
            cursor.getLong(offset + 0), // create_time
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // mobile
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // content
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SuccessSmsBean entity, int offset) {
        entity.setCreate_time(cursor.getLong(offset + 0));
        entity.setMobile(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SuccessSmsBean entity, long rowId) {
        entity.setCreate_time(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SuccessSmsBean entity) {
        if(entity != null) {
            return entity.getCreate_time();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SuccessSmsBean entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
