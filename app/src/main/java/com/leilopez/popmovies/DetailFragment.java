package com.leilopez.popmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(getString(R.string.parcel_key))) {
            Movie movie = intent.getParcelableExtra(getString(R.string.parcel_key));

            ((TextView) rootView.findViewById(R.id.detail_title)).setText(movie.getTitle());

            ImageView posterView = (ImageView) rootView.findViewById(R.id.detail_image);
            Picasso.with(getContext()).load(movie.getImagePath()).into(posterView);

            String date = formatDate(movie.getReleaseDate());
            ((TextView) rootView.findViewById(R.id.detail_date)).setText(date);

            ((TextView) rootView.findViewById(R.id.detail_vote)).setText(movie.getVoteAverage() + getString(R.string.rating_denominator));
            ((TextView) rootView.findViewById(R.id.detail_overview)).setText(movie.getOverview());
        }

        return rootView;
    }

    private String formatDate(String inputDate) {
        String[] parts = inputDate.split("-");
        return parts[0];
    }
}
