package com.bazooka.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bazooka.bluetoothbox.cache.db.entity.SendSuccessFlash;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SEND_SUCCESS_FLASH".
*/
public class SendSuccessFlashDao extends AbstractDao<SendSuccessFlash, Long> {

    public static final String TABLENAME = "SEND_SUCCESS_FLASH";

    /**
     * Properties of entity SendSuccessFlash.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FlashId = new Property(1, Long.class, "flashId", false, "FLASH_ID");
        public final static Property FlashName = new Property(2, String.class, "flashName", false, "FLASH_NAME");
        public final static Property Index = new Property(3, int.class, "index", false, "INDEX");
    }


    public SendSuccessFlashDao(DaoConfig config) {
        super(config);
    }
    
    public SendSuccessFlashDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SEND_SUCCESS_FLASH\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"FLASH_ID\" INTEGER," + // 1: flashId
                "\"FLASH_NAME\" TEXT," + // 2: flashName
                "\"INDEX\" INTEGER NOT NULL );"); // 3: index
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SEND_SUCCESS_FLASH\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SendSuccessFlash entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long flashId = entity.getFlashId();
        if (flashId != null) {
            stmt.bindLong(2, flashId);
        }
 
        String flashName = entity.getFlashName();
        if (flashName != null) {
            stmt.bindString(3, flashName);
        }
        stmt.bindLong(4, entity.getIndex());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SendSuccessFlash entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long flashId = entity.getFlashId();
        if (flashId != null) {
            stmt.bindLong(2, flashId);
        }
 
        String flashName = entity.getFlashName();
        if (flashName != null) {
            stmt.bindString(3, flashName);
        }
        stmt.bindLong(4, entity.getIndex());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SendSuccessFlash readEntity(Cursor cursor, int offset) {
        SendSuccessFlash entity = new SendSuccessFlash( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // flashId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // flashName
            cursor.getInt(offset + 3) // index
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SendSuccessFlash entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFlashId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setFlashName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIndex(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SendSuccessFlash entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SendSuccessFlash entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SendSuccessFlash entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}