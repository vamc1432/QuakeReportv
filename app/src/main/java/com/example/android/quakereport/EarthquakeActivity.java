/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthQuake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private static final int EARTH_QUAKE_LOADER = 1;

    private EarthquakeAdapter mAdapter;

    private TextView mEmptyTextView;

    private static final String USGS_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view);

        earthquakeListView.setEmptyView(mEmptyTextView);

        mAdapter = new EarthquakeAdapter(this, new ArrayList<EarthQuake>());
        // Create a new {@link ArrayAdapter} of earthquakes
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                EarthQuake curentEQ = mAdapter.getItem(pos);

                Uri earthquakeUri = Uri.parse(curentEQ.getmUrl());

                Intent i = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                startActivity(i);
            }
        });

        ConnectivityManager conmg = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = conmg.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(EARTH_QUAKE_LOADER, null, this);

        } else {

            View loadingindic = findViewById(R.id.progress_bar);

            loadingindic.setVisibility(View.GONE);

            mEmptyTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<EarthQuake>> onCreateLoader(int i, Bundle bundle) {


        return new EarthQuakeLoader(this, USGS_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<EarthQuake>> loader, List<EarthQuake> earthQuakes) {

        View loadingindic = findViewById(R.id.progress_bar);
        loadingindic.setVisibility(View.GONE);

        mEmptyTextView.setText(R.string.no_earthquakes);

         mAdapter.clear();

        if (earthQuakes == null && !earthQuakes.isEmpty()) {
            mAdapter.addAll(earthQuakes);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<EarthQuake>> loader) {

        mAdapter.clear();

    }
}

