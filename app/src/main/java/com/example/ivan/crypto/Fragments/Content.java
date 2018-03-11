package com.example.ivan.crypto.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivan.crypto.Adapters.RecyclerAdapter;
import com.example.ivan.crypto.R;

import static com.example.ivan.crypto.R.layout.fragment_layout;

/**
 * Created by ivan on 10/03/18.
 */

public class Content extends Fragment {
    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(fragment_layout,container,false);
        view.setTag("Content");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null){
            switch (args.getInt("position")) {
                case R.id.home:
                    break;
                case R.id.favorites:
                    break;
                case R.id.highlights:
                    break;
            }
        }

    }

    public static Content getInstance(int selectedView){
        Content content = new Content();
        Bundle args = new Bundle();
        args.putInt("position",selectedView);
        content.setArguments(args);
        return content;
    }

}
