package com.ahsan.a51_cwm_firestoreintro;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by User on 5/14/2018.
 */

public class NewNoteDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "NewNoteDialog";

    //widgets
    private EditText mTitle, mContent;
    private TextView mCreate, mCancel;

    //vars
    private IMainActivity mIMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_note, container, false);
        mTitle = (EditText) view.findViewById(R.id.note_title);
        mContent = (EditText) view.findViewById(R.id.note_content);
        mCreate = (TextView) view.findViewById(R.id.create);
        mCancel = (TextView) view.findViewById(R.id.cancel);

        mCancel.setOnClickListener(this);
        mCreate.setOnClickListener(this);

        getDialog().setTitle("New Note");

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.create:{

                // insert the new note

                String title = mTitle.getText().toString();
                String content = mContent.getText().toString();

                //We could also use && or || here along with !content.equals("") to also check for content.
                if(!title.equals("")){
                    mIMainActivity.createNewNote(title, content); //Call this method to
                    getDialog().dismiss();

                }
                else{
                    Toast.makeText(getActivity(), "Enter a title", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.cancel:{
                getDialog().dismiss();
                break;
            }
        }
    }

    //Instantiate our interface in onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity)getActivity();
    }
}





















