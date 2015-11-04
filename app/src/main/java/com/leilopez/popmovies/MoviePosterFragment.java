package com.leilopez.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main fragment that contains list of movie fragments.
 */
public class MoviePosterFragment extends Fragment implements SortDialogFragment.OnDialogSortListener {

    private PosterAdapter mPosterAdapter;

    public MoviePosterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            int selected = sp.getInt(getString(R.string.pref_sort_key), 0);

            SortDialogFragment dialog = SortDialogFragment.newInstance(selected);
            dialog.setDialogSelectorCallback(this);
            dialog.show(getFragmentManager(), getString(R.string.action_sort));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<Movie> temp = new ArrayList<>();
        mPosterAdapter = new PosterAdapter(getActivity(), temp);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(mPosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = mPosterAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(getString(R.string.parcel_key), movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void updateMovies(int selected) {
        String sort = getString(R.string.pref_sort_rating);;
        if (selected == 0) {
            sort = getString(R.string.pref_sort_popularity);
        }

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(sort);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(getString(R.string.pref_sort_key), selected);
        editor.apply();
    }

    @Override
    public void onStart() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        int selected = sp.getInt(getString(R.string.pref_sort_key), 0);
        updateMovies(selected);
        super.onStart();
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        // Things that need to be extracted
        private final String MDB_RESULTS = "results";
        private final String MDB_IMAGE_PATH = "poster_path";
        private final String MDB_TITLE = "title";
        private final String MDB_RELEASE = "release_date";
        private final String MDB_VOTE = "vote_average";
        private final String MDB_OVERVIEW = "overview";
        private final String MDB_ID = "id";

        private Movie[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {

            JSONObject forecastJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = forecastJson.getJSONArray(MDB_RESULTS);

            Movie[] results = new Movie[resultsArray.length()];
            JSONObject movieInfo;

            for (int i = 0; i < resultsArray.length(); i++) {
                movieInfo = resultsArray.getJSONObject(i);
                Log.v(LOG_TAG, movieInfo.getString(MDB_IMAGE_PATH));
                Movie movie = new Movie(movieInfo.getString(MDB_ID),
                                        getString(R.string.image_path_prefix) +
                                                getString(R.string.image_path_size) +
                                                movieInfo.getString(MDB_IMAGE_PATH),
                                        movieInfo.getString(MDB_TITLE),
                                        movieInfo.getString(MDB_RELEASE),
                                        movieInfo.getString(MDB_VOTE),
                                        movieInfo.getString(MDB_OVERVIEW));
                results[i] = movie;
            }
            return results;
        }

        @Override
        protected Movie[] doInBackground(String ... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            String sort = params[0];

            try {
                // Construct the URL for themovieDB query
                Uri builtUri = Uri.parse(getString(R.string.main_base_url)).buildUpon()
                        .appendQueryParameter(getString(R.string.sort_label), sort)
                        .appendQueryParameter(getString(R.string.sort_vote_count), getString(R.string.sort_vote_count_value))
                        .appendQueryParameter(getString(R.string.api_label), getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Prettify for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {

            if (result != null) {
                mPosterAdapter.clear();
                mPosterAdapter.addAll(Arrays.asList(result));
            }
        }

    }
}
