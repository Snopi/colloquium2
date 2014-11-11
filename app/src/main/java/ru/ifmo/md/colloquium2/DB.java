package ru.ifmo.md.colloquium2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Snopi on 11.11.2014.
 */public class DB {

    private static final String DB_NAME = "myydb";
    private static final int DB_VERSION = 1;
    public static final String DB_TABLE = "mytab";

    public static final String COLUMN_ID = "_id";
    public static final String VOTES = "votes";
    public static final String CONDIDAT_NAME = "condidate_name";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    VOTES + " integer, " +
                    CONDIDAT_NAME + " text unique not null" +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    public SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }
    public void renameById(long id, String newName) {
        ContentValues x = new ContentValues();
        x.put(CONDIDAT_NAME, newName);
        mDB.update(DB_TABLE, x, "_id=?", new String[] {"" + id});
        //String
    }
    void incVoteById(long id) {
        ContentValues x = new ContentValues();
        Cursor cur = mDB.query(DB_TABLE, new String[] {VOTES}, "_id=?", new String[] {"" + id}, null, null, null);
        cur.moveToFirst();
        int votes = cur.getInt(cur.getColumnIndex(VOTES));
        x.put(VOTES, votes + 1);
        mDB.update(DB_TABLE, x, "_id=?", new String[] {"" + id});
    }
    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }
    public void clearDB() {
        mDB.delete(DB_TABLE, null, null);
    }
    public void addRec(String name, int votes) {
        ContentValues cv = new ContentValues();
        cv.put(CONDIDAT_NAME, name);
        cv.put(VOTES, votes);
        mDB.insert(DB_TABLE, null, cv);
    }

    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}