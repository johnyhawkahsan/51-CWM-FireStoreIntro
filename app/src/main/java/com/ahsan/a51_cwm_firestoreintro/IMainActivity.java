package com.ahsan.a51_cwm_firestoreintro;
import com.ahsan.a51_cwm_firestoreintro.models.Note;

/**
Because of this IMainActivity Interface, we are able to perform all the task in MainActivity.
 */

public interface IMainActivity {

    void createNewNote(String title, String content);

    void updateNote(Note note);

    void onNoteSelected(Note note);

    void deleteNote(Note note);
}
