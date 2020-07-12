package com.labsession.notetaking.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.labsession.notetaking.model.Note;

import java.util.ArrayList;
import java.util.List;

import static com.labsession.notetaking.util.Constants.COLUMN_BODY;
import static com.labsession.notetaking.util.Constants.COLUMN_ID;
import static com.labsession.notetaking.util.Constants.COLUMN_TITLE;
import static com.labsession.notetaking.util.Constants.DATABASE_NAME;
import static com.labsession.notetaking.util.Constants.DATABASE_VERSION;
import static com.labsession.notetaking.util.Constants.TABLE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TITLE + " TEXT," + COLUMN_BODY + " TEXT);");
        Log.v("Table initialized", "db");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    /**
     * this method would be use to insert note in database
     *
     * @param note to be inserted
     * @return row id of added note
     */
    public long insetNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_BODY, note.getBody());
        long rowID = db.insert(TABLE_NAME, null, values);
        db.close();
        return rowID;
    }

    /**
     * this method will return all rows data from table i.e notes list
     *
     * @return notes list
     */
    public List<Note> getAllNotes() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        List<Note> notes = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Note note = new Note();
                note.setNoteID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                note.setBody(cursor.getString(cursor.getColumnIndex(COLUMN_BODY)));
                notes.add(note);
                cursor.moveToNext();
            }
        }
        return notes;
    }

    /**
     * This method will be use to update notes
     *
     * @param note to be updated
     */
    public int updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_BODY, note.getBody());
        return db.update(TABLE_NAME, values, COLUMN_ID + " = " + note.getNoteID(), null);
    }

    /**
     * method will be use to delete the particular note
     *
     * @param id of note that user want to delete
     */
    public void deleteNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = " + id, null);
    }
}
