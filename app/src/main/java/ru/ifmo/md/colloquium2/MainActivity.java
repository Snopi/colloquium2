package ru.ifmo.md.colloquium2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int CM_DELETE_ID = 1;
    private static final int CM_RENAME = 2;
    private static final int CM_VOTE = 3;
    DB db;
    SimpleCursorAdapter scAdapter;
    ListView lv;
    EditText edit;
    Button addd;
    Button reset;
    int sum = 0;
    Button startVote;
    boolean vote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vote = false;
        db = new DB(this);
        db.open();
        edit = (EditText) findViewById(R.id.edit);
        addd = (Button) findViewById(R.id.add);

        // формируем столбцы сопоставления
        String[] from = new String[]{DB.CONDIDAT_NAME, DB.VOTES};
        int[] to = new int[]{R.id.dude, R.id.votes};
        scAdapter = new SimpleCursorAdapter(this, R.layout.cond, null, from, to);
        lv = (ListView) findViewById(R.id.lit);
        registerForContextMenu(lv);
        getSupportLoaderManager().initLoader(0, null, this);
        addd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vote) {
                    return;
                }
                db.addRec(edit.getText().toString(), 0);
                Log.d("tral", "azaza");
                // получаем новый курсор с данными
                getSupportLoaderManager().getLoader(0).forceLoad();
            }
        });
        lv.setAdapter(scAdapter);
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote = false;
                db.clearDB();
                getSupportLoaderManager().getLoader(0).forceLoad();
            }
        });
        startVote = (Button) findViewById(R.id.start);
        startVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vote = true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_recort);
        menu.add(0, CM_RENAME, 0, R.string.rename);
        menu.add(0, CM_VOTE, 0, R.string.vote);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID && !vote) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            db.delRec(acmi.id);
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        } else if (item.getItemId() == CM_RENAME && !vote) {
            final AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            ContentValues x = new ContentValues();
            AlertDialog.Builder c = new AlertDialog.Builder(this);
            c.setMessage("new Name");
            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setText("");
            input.setLayoutParams(lp);
            c.setView(input);
            c.setCancelable(true);
            c.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    db.renameById(acmi.id, input.getText().toString());
                    getSupportLoaderManager().getLoader(0).forceLoad();
                }
            });
            c.show();

        } else if (item.getItemId() == CM_VOTE && vote) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            db.incVoteById(acmi.id);
            //Toast.makeText().show();
            getSupportLoaderManager().getLoader(0).forceLoad();
            sum++;
            updatePercents();
        }
        return super.onContextItemSelected(item);
    }

    public void updatePercents() {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    //loaders
    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            return db.getAllData();
        }

    }
}