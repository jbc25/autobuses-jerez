package com.triskelapps.busjerez.ui.main.address;

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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.base.BaseMainFragment;
import com.triskelapps.busjerez.databinding.FragmentAddressBinding;
import com.triskelapps.busjerez.util.CountlyUtil;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends BaseMainFragment {

    public final LatLng AUTOCOMPLETE_RESULTS_JEREZ_NORTH_EAST = new LatLng(36.765444, -6.017372);
    public final LatLng AUTOCOMPLETE_RESULTS_JEREZ_SOUTH_WEST = new LatLng(36.626432, -6.161407);

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

        autocompleteFragment.setCountry("ES");

        // https://developers.google.com/places/android-sdk/autocomplete#constrain-AC-results
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
                AUTOCOMPLETE_RESULTS_JEREZ_SOUTH_WEST, AUTOCOMPLETE_RESULTS_JEREZ_NORTH_EAST));

//        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Autocomplete Places. Selected: " + place.getName());
                getMainPresenter().onPlaceSelected(place);
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


    public void clearAddress() {
        autocompleteFragment.setText("");
    }
}
