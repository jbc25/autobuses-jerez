package com.triskelapps.ui.main.address;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.PlaceAutocomplete;
import com.google.android.libraries.places.widget.PlaceAutocompleteActivity;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.common.base.Preconditions;
import com.google.maps.android.SphericalUtil;
import com.triskelapps.App;
import com.triskelapps.CityData;
import com.triskelapps.R;
import com.triskelapps.base.BaseMainFragment;
import com.triskelapps.databinding.FragmentAddressBinding;
import com.triskelapps.model.BusLine;
import com.triskelapps.model.BusStop;
import com.triskelapps.model.NearbyLine;
import com.triskelapps.ui.main.filter.NearbyLinesAdapter;
import com.triskelapps.util.CountlyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AddressFragment extends BaseMainFragment {


    private FragmentAddressBinding binding;

    List<NearbyLine> nearbyLines = new ArrayList<>();
    private NearbyLinesAdapter nearbyLinesAdapter;
    private ActivityResultLauncher<Intent> placeAutocompleteActivityResultLauncher;
    private Intent autocompleteIntent;

    public AddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddressBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configurePlaceAutocompleteNew();

        nearbyLinesAdapter = new NearbyLinesAdapter(getActivity(), nearbyLines);
        nearbyLinesAdapter.setOnItemClickListener((nearbyLine) ->
                getMainPresenter().onNearbyLineClick(nearbyLine.getBusLine(), nearbyLine.getBusStop()));
        binding.recyclerNearbyLines.setAdapter(nearbyLinesAdapter);
        binding.viewNearbyLines.setVisibility(View.GONE);

    }

    private void configurePlaceAutocompleteNew() {

        // Optional, create a session token for Autocomplete request and the followup FetchPlace request
        AutocompleteSessionToken sessionToken = AutocompleteSessionToken.newInstance();

        autocompleteIntent =
                new PlaceAutocomplete.IntentBuilder()
                        .setCountries(List.of("ES"))
                        .setLocationRestriction(RectangularBounds.newInstance(
                                CityData.AUTOCOMPLETE_RESULTS_COORDS_SOUTH_WEST, CityData.AUTOCOMPLETE_RESULTS_COORDS_NORTH_EAST))
                        .setAutocompleteSessionToken(sessionToken) // optional
                        .build(getActivity());

        placeAutocompleteActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    if (result.getResultCode() == PlaceAutocompleteActivity.RESULT_OK) {

                        AutocompletePrediction prediction =
                                PlaceAutocomplete.getPredictionFromIntent(
                                        Preconditions.checkNotNull(intent));

                        AutocompleteSessionToken sessionToken1 =
                                PlaceAutocomplete.getSessionTokenFromIntent(
                                        Preconditions.checkNotNull(intent));

                        // create PlacesClient to make FetchPlace request (optional)
                        PlacesClient placesClient = Places.createClient(getActivity());
                        FetchPlaceRequest request =
                                FetchPlaceRequest.builder(prediction.getPlaceId(),
                                                Arrays.asList(Place.Field.DISPLAY_NAME, Place.Field.LOCATION))
                                        .setSessionToken(sessionToken1).build();
                        Task<FetchPlaceResponse> task = placesClient.fetchPlace(request);
                        task.addOnSuccessListener(fetchPlaceResponse -> {

                            Place place = fetchPlaceResponse.getPlace();
                            onPlaceSelected(place);
                        });
                    } else {
                        Log.i(TAG, "Error New Places API Autocomplete");
                        CountlyUtil.selectPlaceError(-1, "Error New Places API Autocomplete");
                    }
                }
        );

        binding.locationSearchView.setOnLocationSelectedListener(locationResult -> {

            Log.d("Location", "Seleccionado: " + locationResult.getName());

            getMainPresenter().onPlaceSelected(locationResult);
            findNearbyLines(locationResult);
            CountlyUtil.selectPlace(locationResult.getName());

            return null;
        });

        binding.locationSearchView.setOnClearListener(() -> {
            getMainPresenter().onClearPlaceAutocompleteText();
            binding.viewNearbyLines.setVisibility(View.GONE);
            return null;
        });


    }

    private void onPlaceSelected(Place place) {
//
//        Log.i(TAG, "Autocomplete Places. Selected: " + place.getDisplayName());
////        binding.tvSearchPlace.setText(place.getDisplayName());
//        getMainPresenter().onPlaceSelected(place);
//        findNearbyLines(place);
//        CountlyUtil.selectPlace(place.getDisplayName());
    }


    private void findNearbyLines(@NonNull LocationResult locationResult) {

        double distanceRange = 300; // meters

        Map<Integer, NearbyLine> nearbyLinesMap = new HashMap<>();

        List<BusLine> busLines = App.getBusLinesData(getActivity());
        for (BusLine busLine : busLines) {
            List<BusStop> busStops = busLine.getBusStops();

            // Find closest bus stop within distance range
            for (BusStop busStop : busStops) {
                LatLng coords = new LatLng(busStop.getCoordinates().get(0), busStop.getCoordinates().get(1));
                double distance = SphericalUtil.computeDistanceBetween(locationResult.getLatLng(), coords);

                if (distance <= distanceRange) {

                    Log.d(TAG, "findNearbyLinesRange: coordPlace=" + locationResult.getLatLng() + ", coordBusStop=" + coords + ", distance: " + distance);

                    NearbyLine nearbyLine = nearbyLinesMap.get(busLine.getId());
                    if (nearbyLine == null) {
                        nearbyLine = new NearbyLine(busLine, busStop, distance);
                    } else {
                        if (distance < nearbyLine.getDistance()) {
                            nearbyLine.setDistance(distance);
                            nearbyLine.setBusStop(busStop);
                        }
                    }

                    nearbyLinesMap.put(busLine.getId(), nearbyLine);
                }

            }
        }

        List<NearbyLine> nearbyLineList = nearbyLinesMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .sorted((o1, o2) -> {
                    if (o1.getDistance() == o2.getDistance()) {
                        return 0;
                    } else if (o1.getDistance() > o2.getDistance()) {
                        return 1;
                    } else {
                        return -1;
                    }
                })
                .collect(Collectors.toList());

        showNearbyLines(nearbyLineList);
    }

    private void showNearbyLines(List<NearbyLine> nearbyLines) {
        if (!nearbyLines.isEmpty()) {
            binding.viewNearbyLines.setVisibility(View.VISIBLE);
            this.nearbyLines.clear();
            this.nearbyLines.addAll(nearbyLines);
            nearbyLinesAdapter.notifyDataSetChanged();
        }
    }


    public void clearAddress() {
        binding.locationSearchView.clearSearchText();
        binding.viewNearbyLines.setVisibility(View.GONE);
    }
}
