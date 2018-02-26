package com.mahindra.be_lms.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "EVENT".
 */
public class EventDao extends AbstractDao<Event, Long> {

    public static final String TABLENAME = "EVENT";

    public EventDao(DaoConfig config) {
        super(config);
    }


    public EventDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"EVENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"EVENT_ID\" TEXT," + // 1: eventID
                "\"EVENT_NAME\" TEXT," + // 2: eventName
                "\"EVENT_MONTH\" TEXT," + // 3: eventMonth
                "\"EVENT_FROMDATE\" TEXT," + // 4: eventFromdate
                "\"EVENT_TODATE\" TEXT," + // 5: eventTodate
                "\"EVENT_NOMINATION\" TEXT," + // 6: eventNomination
                "\"EVENT_VENUE\" TEXT," + // 7: eventVenue
                "\"EVENT_PROGRAM_NO\" TEXT);"); // 8: eventProgram_no
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EVENT\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, Event entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String eventID = entity.getEventID();
        if (eventID != null) {
            stmt.bindString(2, eventID);
        }

        String eventName = entity.getEventName();
        if (eventName != null) {
            stmt.bindString(3, eventName);
        }

        String eventMonth = entity.getEventMonth();
        if (eventMonth != null) {
            stmt.bindString(4, eventMonth);
        }

        String eventFromdate = entity.getEventFromdate();
        if (eventFromdate != null) {
            stmt.bindString(5, eventFromdate);
        }

        String eventTodate = entity.getEventTodate();
        if (eventTodate != null) {
            stmt.bindString(6, eventTodate);
        }

        String eventNomination = entity.getEventNomination();
        if (eventNomination != null) {
            stmt.bindString(7, eventNomination);
        }

        String eventVenue = entity.getEventVenue();
        if (eventVenue != null) {
            stmt.bindString(8, eventVenue);
        }

        String eventProgram_no = entity.getEventProgram_no();
        if (eventProgram_no != null) {
            stmt.bindString(9, eventProgram_no);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public Event readEntity(Cursor cursor, int offset) {
        Event entity = new Event( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // eventID
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // eventName
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // eventMonth
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // eventFromdate
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // eventTodate
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // eventNomination
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // eventVenue
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // eventProgram_no
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, Event entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setEventID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setEventName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setEventMonth(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setEventFromdate(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setEventTodate(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setEventNomination(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setEventVenue(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setEventProgram_no(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(Event entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(Event entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    /**
     * Properties of entity Event.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property EventID = new Property(1, String.class, "eventID", false, "EVENT_ID");
        public final static Property EventName = new Property(2, String.class, "eventName", false, "EVENT_NAME");
        public final static Property EventMonth = new Property(3, String.class, "eventMonth", false, "EVENT_MONTH");
        public final static Property EventFromdate = new Property(4, String.class, "eventFromdate", false, "EVENT_FROMDATE");
        public final static Property EventTodate = new Property(5, String.class, "eventTodate", false, "EVENT_TODATE");
        public final static Property EventNomination = new Property(6, String.class, "eventNomination", false, "EVENT_NOMINATION");
        public final static Property EventVenue = new Property(7, String.class, "eventVenue", false, "EVENT_VENUE");
        public final static Property EventProgram_no = new Property(8, String.class, "eventProgram_no", false, "EVENT_PROGRAM_NO");
    }

}
