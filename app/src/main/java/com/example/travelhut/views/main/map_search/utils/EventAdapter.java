package com.example.travelhut.views.main.map_search.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.Event;
import com.example.travelhut.views.EventActivity;

import java.util.List;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    //Instance Variables
    private Context context;
    private List<Event> eventsList;

    //Constructor
    public EventAdapter(Context context, List<Event> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_view, parent, false);
        return new EventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Get current Event object and set view's text and image from Event object data
        final Event event = eventsList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDate());
        holder.eventAddress.setText(event.getVenue());
        Glide.with(context).load(event.getImageUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).dontAnimate().into(holder.eventImage);

        //Set OnClickListener for a trip item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventActivity.class);
            intent.putExtra(StringsRepository.EVENT_ID, event.getEventid());
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    //ViewHolder class to give access to UI views
    public class ViewHolder extends RecyclerView.ViewHolder {


        //Instance Variables
        public ImageView eventImage;
        public TextView eventName, eventAddress, eventDate;
        public ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Initializing view objects
            eventImage = itemView.findViewById(R.id.event_item_image_view);
            eventName = itemView.findViewById(R.id.event_item_place_name);
            eventAddress = itemView.findViewById(R.id.event_item_place_address);
            eventDate = itemView.findViewById(R.id.event_item_date);
            progressBar = itemView.findViewById(R.id.event_item_progress_bar);
        }
    }

}
