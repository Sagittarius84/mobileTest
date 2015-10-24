package org.noorganization.instalist.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helps to open database in the provider.
 * Created by damihe on 21.10.15.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public static int VERSION = 1;

    public DBOpenHelper(Context _context, String _name) {
        super(_context, _name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase _db) {
        // TODO: Create tables.
        _db.execSQL("CREATE TABLE category (_id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL)");
        _db.execSQL("CREATE TABLE list (_id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, " +
                "category TEXT, FOREIGN KEY (category) REFERENCES category (_id) ON UPDATE CASCADE " +
                "ON DELETE CASCADE)");
    }

    @Override
    public void onOpen(SQLiteDatabase _db) {
        super.onOpen(_db);
        if (!_db.isReadOnly()) {
            _db.execSQL("PRAGMA foreign_key = ON");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
        int currentVersion = _oldVersion;
        // Example:
        //if (currentVersion < 2 && 2 <= _newVersion) {
        //    // do upgrade to version 2
        //}
    }
}
