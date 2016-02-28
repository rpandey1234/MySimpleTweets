package com.codepath.apps.mysimpletweets;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rahul on 2/20/16.
 */
public class ComposeDialog extends DialogFragment {

    public interface TweetListener {
        void setTweet(String body);
    }

    @Bind(R.id.editTextCompose) EditText editTextCompose;
    @Bind(R.id.btnSubmit) Button btnSubmit;
    @Bind(R.id.tvCharLeft) TextView tvCharLeft;

    public static final String REPLY_AT = "reply_at";
    public static final int CHARACTER_LIMIT = 140;
    public static final int WARNING_CHARACTER_REMAINING = 10;
    private int charWritten = 0;

    public ComposeDialog() {
    }

    public static ComposeDialog newInstance(String replyAt) {
        ComposeDialog composeDialog = new ComposeDialog();
        Bundle args = new Bundle();
        args.putString(REPLY_AT, replyAt);
        composeDialog.setArguments(args);
        return composeDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        ButterKnife.bind(this, view);
        updateCharacterLeftTv(CHARACTER_LIMIT);

        editTextCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charWritten = editTextCompose.getText().toString().length();
                int remaining = CHARACTER_LIMIT - charWritten;
                int color = getResources().getColor(R.color.black);
                if (remaining < WARNING_CHARACTER_REMAINING) {
                    color = getResources().getColor(R.color.red);
                }
                tvCharLeft.setTextColor(color);
                updateCharacterLeftTv(remaining);
                boolean isValid = charWritten != 0 && charWritten <= CHARACTER_LIMIT;
                btnSubmit.setEnabled(isValid);
                btnSubmit.setClickable(isValid);
                Drawable background = getResources().getDrawable(R.drawable.twitter_button);
                if (!isValid) {
                    background = getResources().getDrawable(R.drawable.twitter_button_disabled);
                }
                btnSubmit.setBackground(background);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TODO: sizing according to https://github.com/codepath/android_guides/wiki/Using-DialogFragment
//        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
//        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        getDialog().getWindow().setAttributes(params);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        String replyAt = getArguments().getString(REPLY_AT, "");
        if (!replyAt.isEmpty()) {
            String handle = getResources().getString(R.string.handleTemplate);
            editTextCompose.setText(String.format(handle, replyAt));
        }

        // Show soft keyboard automatically and request focus to field
        editTextCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @OnClick(R.id.btnSubmit)
    public void onSubmitClicked(View view) {
        // TODO: this is broken
        TweetListener listener = (TweetListener) getActivity();
        listener.setTweet(editTextCompose.getText().toString().trim());
        dismiss();
    }

    private void updateCharacterLeftTv(int nChars) {
        String charactersLeft = getResources().getQuantityString(
                R.plurals.characters_left, nChars, nChars);
        tvCharLeft.setText(charactersLeft);
    }

}
