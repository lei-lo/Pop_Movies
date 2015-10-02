package com.leilopez.popmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PosterAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = PosterAdapter.class.getSimpleName();

    public PosterAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    public View getView(int position, View view, ViewGroup parent) {

        Movie movie = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.movie_poster, parent, false);
        }

        ImageView posterView = (ImageView) view.findViewById(R.id.poster_image);
        Picasso.with(getContext()).load(movie.getImagePath()).into(posterView);

        return view;
    }
}
