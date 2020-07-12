package com.labsession.notetaking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import com.labsession.notetaking.R;
import com.labsession.notetaking.adapter.NotesListAdapter;
import com.labsession.notetaking.db.DatabaseHelper;
import com.labsession.notetaking.model.Note;

import java.util.List;

import static com.labsession.notetaking.util.Constants.FLOW_FOR;
import static com.labsession.notetaking.util.Constants.FOR_ADD;
import static com.labsession.notetaking.util.Constants.FOR_EDIT;
import static com.labsession.notetaking.util.Constants.NOTE_DATA;

public class MainActivity extends BaseActivity {

    private DatabaseHelper db;
    private NotesListAdapter adapter;
    private List<Note> notes;
    private ListView listView;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private Toolbar toolbar;
    private Note selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        listView = (ListView) findViewById(R.id.listView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes List");

        setUI();

    }

    private void setUI() {
        /**
         * getting all notes from database
         */
        notes = db.getAllNotes();
        /**
         * setting up adapter
         */
        adapter = new NotesListAdapter(this, R.layout.item_notes_list, notes);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.emptyListText));
        actionModeCallback = new ActionModeCallback();


        /**
         * setting on long click listener to enable action mode for edit,delete and share
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode == null) {
                    actionMode = startSupportActionMode(actionModeCallback);
                }
                selectedNote = notes.get(position);
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_note) {
            gotoActivity(CreateNoteActivity.class,FLOW_FOR,FOR_ADD);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_modes, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.share:
                    finishActionMode();
                    shareNotesToOtherApps(selectedNote);
                    break;
                case R.id.delete:
                    deleteNote();
                    break;
                case R.id.edit:
                    editNote();
                    finishActionMode();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    }

    /**
     *  intent to createNoteActivity in edit mode
     */
    private void editNote() {
        gotoActivity(CreateNoteActivity.class, FLOW_FOR, FOR_EDIT, NOTE_DATA, selectedNote);
    }

    /**
     *  delete note from database as well as from list
     */
    private void deleteNote() {
        db.deleteNote(selectedNote.getNoteID());
        notes.remove(selectedNote);
        adapter.notifyDataSetChanged();
        finishActionMode();
    }

    /**
     * finishing action mode and set default
     */
    private void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }
}



