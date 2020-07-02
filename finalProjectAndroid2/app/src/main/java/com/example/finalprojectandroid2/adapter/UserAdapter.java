package com.example.finalprojectandroid2.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroid2.R;
import com.example.finalprojectandroid2.model.event;


import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<event> events;
    UsersListener listener;
    private Bitmap my_image;



    public UserAdapter(List<event> events) { this.events = events; }

    public void setListener(UsersListener listener) {
        this.listener = listener;
    }


    public interface UsersListener { void onUserClicked(int position, View view);}



    public class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textViewName;
        ImageView imageViewIcon;

        public UserViewHolder(View itemView) {
            super(itemView);
            final Context context = itemView.getContext();

            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewCard);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onUserClicked(getAdapterPosition(), view);
                    }
                }
            });
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_layout,
                parent, false);
        UserViewHolder holder = new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        event tempEvent = events.get(position);
        if(tempEvent.getAvailability().equals("No"))
        {
            holder.textViewName.setText(tempEvent.getTitle());
            holder.imageViewIcon.setImageResource(R.drawable.no);
        }
        else
        {
            holder.textViewName.setText(tempEvent.getStartTime() + " - "+ tempEvent.getEndTime());
            holder.imageViewIcon.setImageResource(R.drawable.yes);
        }
    }
    @Override
    public int getItemCount() { return events.size(); }

}
