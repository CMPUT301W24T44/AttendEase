package com.example.qreate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.R;

public class EditProfileScreenFragment extends Fragment {
    public interface OnFragmentInteractionListener {
        void onFragmentDestroyed();
    }
    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_profile_info, container, false);
        //This one goes to the next screen, the class UserAccountScreenActivity pop back previous fragment
        // so two classes, same fragment layout but different behaviour
        // on pressing confirm, validates user details and returns

        Button updateDetails = view.findViewById(R.id.edit_profile_confirm_button);

        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment(); //removes the fragment
            }
        });
        return view;
    }

    public void removeFragment() {
        //removes fragment from the back stack, in this case its the current fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this); // 'this' refers to the current fragment
        fragmentTransaction.commit();
        fragmentManager.popBackStack(); // This line ensures the fragment is removed from the back stack
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.onFragmentDestroyed();
        }
    }

}
