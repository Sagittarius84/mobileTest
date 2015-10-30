package org.noorganization.instalist.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.model.Unit;

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
        _db.execSQL(Unit.DATABASE_CREATE);
        _db.execSQL(Product.DATABASE_CREATE);
        _db.execSQL("CREATE TABLE listentry (" +
                "_id TEXT PRIMARY KEY NOT NULL, " +
                "amount REAL NOT NULL DEFAULT 1, " +
                "priority INTEGER NOT NULL DEFAULT 0, " +
                "product TEXT NOT NULL, " +
                "list TEXT NOT NULL, " +
                "struck INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY (product) REFERENCES product (_id) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (list) REFERENCES list (_id) ON UPDATE CASCADE ON DELETE CASCADE)");
        _db.execSQL(Tag.DATABASE_CREATE);
        _db.execSQL(TaggedProduct.DATABASE_CREATE);
        _db.execSQL(Ingredient.DATABASE_CREATE);
        _db.execSQL(Recipe.DATABASE_CREATE);
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
