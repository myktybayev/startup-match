package kz.diplomka.startupmatch.ui.home.command;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

/**
 * Инвесторлар табы — дизайн келесі жаңартуда толықтырылады.
 */
public class LinkedInvestorsFragment extends Fragment {

    private static final String ARG_PROJECT_ID = ProjectFlowExtras.EXTRA_PROJECT_ID;

    public static LinkedInvestorsFragment newInstance(long projectId) {
        LinkedInvestorsFragment f = new LinkedInvestorsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PROJECT_ID, projectId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_linked_investors, container, false);
    }
}
