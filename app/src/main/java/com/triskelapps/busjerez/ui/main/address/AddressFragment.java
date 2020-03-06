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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.base.BaseMainFragment;
import com.triskelapps.busjerez.databinding.FragmentAddressBinding;

import java.util.Arrays;


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

        autocompleteFragment.setCountry("ES");
//        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Autocomplete Places. Selected: " + place.getName());
                getMainPresenter().onPlaceSelected(place);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "Autocomplete Places. Error: " + status);
            }
        });

        autocompleteFragment.setClearRunnable(() -> getMainPresenter().onClearPlaceAutocompleteText());
    }


}
