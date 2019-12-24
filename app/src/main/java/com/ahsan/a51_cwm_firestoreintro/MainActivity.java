package com.ahsan.a51_cwm_firestoreintro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ahsan.a51_cwm_firestoreintro.models.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * Created by User on 5/14/2018.
 */

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        IMainActivity,
        SwipeRefreshLayout.OnRefreshListener
{

    private static final String TAG = "MainActivity";

    //Because this project doesn't show us register and login methods, I manually created user in FireBase Authentication and pasted it's user id
    private static final String UID = "2ah0Ahi6G2W1LIflYiJuKyEb5LJ2";

    //FireBase
    private FirebaseAuth.AuthStateListener mAuthListener;


    //widgets
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //vars
    private View mParentLayout;
    private ArrayList<Note> mNotes = new ArrayList<>();
    private NoteRecyclerViewAdapter mNoteRecyclerViewAdapter;
    private DocumentSnapshot mLastQueriedDocument;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mParentLayout = findViewById(android.R.id.content);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mFab.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        setupFirebaseAuth();
        initRecyclerView();
        getNotes();
    }

    //Method implemented from IMainActivity interface.
    @Override
    public void deleteNote(final Note note){
        Log.d(TAG, "deleteNote: Note received from ViewNoteDialog. Note_id= " + note.getNote_id());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference noteRef = db.collection("notes")
                .document(note.getNote_id());

        noteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    makeSnackBarMessage("Deleted Note. " + note.getNote_id());
                    mNoteRecyclerViewAdapter.removeNote(note); //Also remove note from RecyclerView

                } else {
                    makeSnackBarMessage("Failed. Check Log.");
                }
            }
        });

    }



    //Get all notes from FireStore and display in RecyclerView
    private void getNotes(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //You can think of DocumentReference as an object and CollectionReference as a list of objects.
        CollectionReference notesCollectionRef = db
                .collection("notes");

        //Searching functionality is a lot better in FireStore, it indexes all the data = First need to "Index" data in Console
        Query notesQuery = null;

        //We are doing this to prevent showing duplicate data after refreshing
        if (mLastQueriedDocument != null){
            Log.d(TAG, "(Query run after refresh) if:  mLastQueriedDocument != null");
            notesQuery = notesCollectionRef
                    .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .startAfter(mLastQueriedDocument);//Start after previous stored DocumentSnapshot, means only load newly added items to the list.

        } else {
            //This is our initial query, means when the app runs the first time, this will run.
            Log.d(TAG, "(Query initial run) else:  mLastQueriedDocument == null");
            notesQuery = notesCollectionRef
                    .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderBy("timestamp", Query.Direction.ASCENDING);
        }

        //get() method gets us all data associated with the above query in list form.
        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    //TODO: Instead of QueryDocumentSnapshot used in Mitch Tabian's Tutorial(Feature of FireStore 12 I think), I tried this and it worked.
                    //Loop through all the received data and add to our list of objects
                    for (DocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        Note note = queryDocumentSnapshot.toObject(Note.class);
                        mNotes.add(note);
                        Log.d(TAG, "queryDocumentSnapshot: note added: " + note.getTitle());
                    }

                    if (task.getResult().size() != 0){
                        mLastQueriedDocument = task.getResult().getDocuments().get(task.getResult().size() -1);
                    }

                    mNoteRecyclerViewAdapter.notifyDataSetChanged();

                } else {
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }

            }
        });
    }

    //Initialize RecyclerView
    private void initRecyclerView(){
        if (mNoteRecyclerViewAdapter == null){
            mNoteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this, mNotes);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mNoteRecyclerViewAdapter);
    }

    //Method implemented from IMainActivity interface.
    @Override
    public void updateNote(final Note note){
        Log.d(TAG, "updateNote: received note in MainActivity from ViewNoteDialog. Note Title = " + note.getTitle());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference noteRef = db.collection("notes")
                .document(note.getNote_id());//Get note who's note id matches with this one

        noteRef.update("title", note.getTitle(),
                            "content", note.getContent())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            makeSnackBarMessage("Update Note");
                            mNoteRecyclerViewAdapter.updateNote(note);
                        } else {
                            makeSnackBarMessage("Failed. Check Log.");
                        }
                    }
                });

    }

    //Method implemented from IMainActivity interface.
    //When Note is selected from RecyclerView, this note object is opened in ViewNoteDialog. Visit ViewNoteDialog newInstance method.
    @Override
    public void onNoteSelected(Note note) {
        Log.d(TAG, "onNoteSelected: received note in MainActivity from RecyclerViewAdapter. Note Title = " + note.getTitle());
        ViewNoteDialog dialog = ViewNoteDialog.newInstance(note);
        dialog.show(getSupportFragmentManager(), getString(R.string.dialog_new_note));
    }


    //Method implemented from IMainActivity interface.
    @Override
    public void createNewNote(String title, String content) //Title and content are received from NewNoteDialog
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //You can think of DocumentReference as an object and CollectionReference as a list of objects.
        DocumentReference newNoteRef = db.collection("notes") //Create database named "notes"
                .document(); //Tell FireStore you're inserting a new document

        //Our Note object that we want to insert to "notes" collection
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setUser_id(userId);
        note.setNote_id(newNoteRef.getId());

        //Now upload Note object to FireStore
        newNoteRef.set(note)
                //onComplete is better than onSuccess because it listens for both onSuccess and onFailure
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            makeSnackBarMessage("Created new note");
                            //Note: After adding new note, RecyclerView does not get automatically updated. We need to refresh by swiping up.
                            //Editing/ Updating is a different method, it required instant updating of RecyclerView with new data.
                        } else {
                            makeSnackBarMessage("Failed, Check log");
                        }
                    }
                });

    }






    //New Note method when + button is clicked
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.fab:{
                //create a new note
                NewNoteDialog dialog = new NewNoteDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_new_note));
                break;
            }
        }
    }


    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onRefresh() {
        getNotes();
        mSwipeRefreshLayout.setRefreshing(false);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionSignOut:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    /*
            ----------------------------- Firebase setup ---------------------------------
         */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void signOut(){
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }



}










