package com.ahsan.a51_cwm_firestoreintro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.ahsan.a51_cwm_firestoreintro.models.Note;

/**
 * Created by User on 5/14/2018.
 */

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NoteRecyclerViewAdapter";

    private ArrayList<Note> mNotes = new ArrayList<>();
    private IMainActivity mIMainActivity;
    private Context mContext;
    private int mSelectedNoteIndex;

    //Constructor for NoteRecyclerViewAdapter
    public NoteRecyclerViewAdapter(Context context, ArrayList<Note> notes) {
        mNotes = notes;
        mContext = context;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note_list_item, parent, false);

        holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof ViewHolder){
            ((ViewHolder)holder).title.setText(mNotes.get(position).getTitle());

            SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy");
            String date = spf.format(mNotes.get(position).getTimestamp());
            ((ViewHolder)holder).timestamp.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }


    //Update RecyclerView items
    public void updateNote(Note note){
       mNotes.get(mSelectedNoteIndex).setTitle(note.getTitle());
       mNotes.get(mSelectedNoteIndex).setContent(note.getContent());
       notifyDataSetChanged();
    }

    //Remove item from RecyclerView
    public void removeNote(Note note){
        mNotes.remove(note);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMainActivity = (IMainActivity) mContext;
    }

    //Our ViewHolder Class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title, timestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectedNoteIndex = getAdapterPosition();
            mIMainActivity.onNoteSelected(mNotes.get(mSelectedNoteIndex));//Execute interface method to send note index to MainActivity.
        }
    }
}
















