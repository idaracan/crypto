package com.example.ivan.crypto.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ivan.crypto.R;

import static com.example.ivan.crypto.R.layout.fragment_layout;

/**
 * Created by ivan on 10/03/18.
 */

public class Content extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(fragment_layout,container,false);
    }

    public static Content getInstance(int selectedView){
        Content content = new Content();
        Bundle args = new Bundle();
        args.putInt("position",selectedView);
        content.setArguments(args);
        return content;
    }

}
