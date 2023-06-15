package com.triskelapps.ui.main.address;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.SphericalUtil;
import com.triskelapps.App;
import com.triskelapps.CityData;
import com.triskelapps.R;
import com.triskelapps.base.BaseMainFragment;
import com.triskelapps.databinding.FragmentAddressBinding;
import com.triskelapps.model.BusLine;
import com.triskelapps.model.BusStop;
import com.triskelapps.util.CountlyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends BaseMainFragment {


    private FragmentAddressBinding binding;
    private CustomPlaceAutocompleteFragment autocompleteFragment;

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
        configurePlaceAutocomplete();
    }

    private void configurePlaceAutocomplete() {

        autocompleteFragment = (CustomPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.fragment_places_autocomplete);

        autocompleteFragment.setCountries("ES");

        // https://developers.google.com/places/android-sdk/autocomplete#constrain-AC-results
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
                CityData.AUTOCOMPLETE_RESULTS_COORDS_SOUTH_WEST, CityData.AUTOCOMPLETE_RESULTS_COORDS_NORTH_EAST));

//        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Autocomplete Places. Selected: " + place.getName());
                getMainPresenter().onPlaceSelected(place);
                findNearbyLines(place);
                CountlyUtil.selectPlace(place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "Autocomplete Places. Error: " + status);
                CountlyUtil.selectPlaceError(status.getStatusCode(), status.getStatusMessage());
            }
        });

        autocompleteFragment.setClearRunnable(() -> getMainPresenter().onClearPlaceAutocompleteText());
    }

    private void findNearbyLines(@NonNull Place place) {

        double distanceLimit = 200; // meters

        List<BusLine> busLines = App.getBusLinesData(getActivity());
        for (BusLine busLine : busLines) {
            List<BusStop> busStops = busLine.getBusStops();

            // Verifica si la polilínea está cerca del punto de referencia
            for (BusStop busStop : busStops) {
                LatLng coords = new LatLng(busStop.getCoordinates().get(0), busStop.getCoordinates().get(1));
                double distance = SphericalUtil.computeDistanceBetween(place.getLatLng(), coords);

//                Log.d(TAG, "findNearbyLines: coordPlace=" + place.getLatLng() + ", coordBusStop=" + coords + ", distance: " + distance);

                if (distance <= distanceLimit) {
                    Log.d(TAG, "findNearbyLines: linea: " + busLine.getId());
                    break;
                }
            }
        }
    }


    public void clearAddress() {
        autocompleteFragment.setText("");
    }
}
