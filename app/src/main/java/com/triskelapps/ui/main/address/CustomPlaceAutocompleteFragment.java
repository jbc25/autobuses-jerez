package com.triskelapps.ui.main.address;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.triskelapps.R;

public class CustomPlaceAutocompleteFragment extends AutocompleteSupportFragment {

    private Runnable clearRunnable;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        config();
    }

    private void config() {

        setHint(getString(R.string.where_to_go));
        getView().findViewById(R.id.places_autocomplete_clear_button)
                .setOnClickListener(view -> {
                    if(clearRunnable != null)
                        clearRunnable.run();

                    setText("");

                });

    }

    public void setClearRunnable(Runnable runnable) {
        this.clearRunnable = runnable;
    }
}
