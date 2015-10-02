package com.leilopez.popmovies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import android.os.Bundle;
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
 * A fragment containing movie posters.
 */
public class MoviePosterFragment extends Fragment {

    private static final String LOG_TAG = MoviePosterFragment.class.getSimpleName();

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            DialogFragment dialog = new SortDialog();
            dialog.show(getFragmentManager(), getString(R.string.action_sort)); //TODO what is this string for?
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SortDialog extends DialogFragment { //TODO close when area around dialog is clicked
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.action_sort)
                    .setSingleChoiceItems(R.array.sort_array, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                // TODO set shared prefs, call update movie
                            } else {
                                // TODO set shared prefs, call movie
                            }
                        }
                    });

            return builder.create();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO replace with real placeholder images
        List<Movie> temp = new ArrayList<>();
        temp.add(new Movie("76341",
                        "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                        "Title", "Date", "Average", "Overview"));
        temp.add(new Movie("76341",
                "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                "Title", "Date", "Average", "Overview"));
        temp.add(new Movie("76341",
                "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                "Title", "Date", "Average", "Overview"));
        mPosterAdapter = new PosterAdapter(getActivity(), temp);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(mPosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = mPosterAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie", movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovies() {
        String sort = "popularity.desc"; //TODO get from shared prefs
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(sort);
    }

    @Override
    public void onStart() {

        updateMovies();
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
        // TODO change "w185" value based on phone or tablet!
        private final String MDB_IMAGE_PATH_PREFIX = "http://image.tmdb.org/t/p/w185/";

        private Movie[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {

            JSONObject forecastJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = forecastJson.getJSONArray(MDB_RESULTS);

            Movie[] results = new Movie[resultsArray.length()];
            JSONObject movieInfo;

            for (int i = 0; i < resultsArray.length(); i++) {
                movieInfo = resultsArray.getJSONObject(i);
                Log.v(LOG_TAG, movieInfo.getString(MDB_IMAGE_PATH));
                Movie movie = new Movie(movieInfo.getString(MDB_ID),
                                        MDB_IMAGE_PATH_PREFIX + movieInfo.getString(MDB_IMAGE_PATH),
                                        movieInfo.getString(MDB_TITLE),
                                        movieInfo.getString(MDB_RELEASE),
                                        movieInfo.getString(MDB_VOTE),
                                        movieInfo.getString(MDB_OVERVIEW));
                Log.v(LOG_TAG, movie.getTitle());
                results[i] = movie;
            }
            return results;
        }

        @Override
        protected Movie[] doInBackground(String ... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            // TODO add check that these exist
            String sort = params[0];
            //String sort = "popularity.desc";
            //SharedPreferences sharedPrefs =
            //        PreferenceManager.getDefaultSharedPreferences(getActivity());
            //String sort = sharedPrefs.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_popularity));
            String apiKey = "placeholder";

            try {
                // Construct the URL for themovieDB query
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort) //todo change
                        .appendQueryParameter(API_KEY_PARAM, apiKey) //todo change
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, url.toString());

                // Create the request to themovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Prettify for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting
                // to parse it.
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

            // This will only happen if there was an error getting or parsing the forecast.
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
