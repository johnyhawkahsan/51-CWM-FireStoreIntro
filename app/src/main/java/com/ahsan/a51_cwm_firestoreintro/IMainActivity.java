package com.ahsan.a51_cwm_firestoreintro;
import com.ahsan.a51_cwm_firestoreintro.models.Note;

/**
 * Created by User on 5/14/2018.
 */

public interface IMainActivity {

    void createNewNote(String title, String content);

    void updateNote(Note note);

    void onNoteSelected(Note note);

    void deleteNote(Note note);
}
