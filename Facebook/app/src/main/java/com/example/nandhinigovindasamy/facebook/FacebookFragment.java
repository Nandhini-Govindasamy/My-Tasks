package com.example.nandhinigovindasamy.facebook;

import android.app.Fragment;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

/**
 * Created by NandhiniGovindasamy on 7/30/18.
 */

public class FacebookFragment extends Fragment{
    private LoginButton loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());
        // Other app specific specialization
    }
}
