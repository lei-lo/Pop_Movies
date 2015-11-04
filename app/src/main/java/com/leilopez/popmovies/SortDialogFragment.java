package com.leilopez.popmovies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SortDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    static int mSelectedIndex;
    static OnDialogSortListener mDialogSelectorCallback;

    private final String LOG_TAG = SortDialogFragment.class.getSimpleName();

    public interface OnDialogSortListener {
        void updateMovies(int selected);
    }

    public static SortDialogFragment newInstance(int selected) {
        final SortDialogFragment dialog = new SortDialogFragment();
        mSelectedIndex = selected;
        return dialog;
    }

    public void setDialogSelectorCallback(OnDialogSortListener listener) {
        mDialogSelectorCallback = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_sort)
                .setSingleChoiceItems(R.array.sort_array, mSelectedIndex, this);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which != 0) {
            mDialogSelectorCallback.updateMovies(1);
        } else {
            mDialogSelectorCallback.updateMovies(0);
        }
        dialog.dismiss();
    }
}