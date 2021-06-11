package com.triskelapps.views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.triskelapps.R;
import com.triskelapps.util.WindowUtils;


public class EditTextDialog {

    private static final String TAG = "ContactDialog";

    private final Context context;
    private final TextView textView;

    public EditTextDialog(TextView textView) {
        this.context = textView.getContext();
        this.textView = textView;
    }

    public static EditTextDialog with(TextView textView) {
        return new EditTextDialog(textView);
    }

    public void show() {

        View layout = View.inflate(context, R.layout.view_dialog_edittext, null);

        final EditText editText = layout.findViewById(R.id.edit_text);
        editText.setText(textView.getText().toString());

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .setPositiveButton(R.string.accept, (dialogInterface, which) -> {

                    textView.setText(editText.getText().toString());

                })
                .setNeutralButton(R.string.cancel, null)
                .create();

        dialog.setOnDismissListener(dialogInterface -> WindowUtils.hideSoftKeyboard((Activity) context));

        dialog.show();
    }


}
