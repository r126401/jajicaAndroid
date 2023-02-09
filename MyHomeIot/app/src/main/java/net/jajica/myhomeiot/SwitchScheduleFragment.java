package net.jajica.myhomeiot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jajica.myhomeiot.databinding.FragmentSwitchScheduleBinding;
public class SwitchScheduleFragment extends Fragment {


    private FragmentSwitchScheduleBinding mbinding;

    public SwitchScheduleFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        mbinding = FragmentSwitchScheduleBinding.inflate(inflater, container, false);
        rootView = mbinding.getRoot();

        return rootView;

    }
}