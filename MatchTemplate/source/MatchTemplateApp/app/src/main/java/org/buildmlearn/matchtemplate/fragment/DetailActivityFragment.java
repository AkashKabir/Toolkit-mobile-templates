package org.buildmlearn.matchtemplate.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import org.buildmlearn.matchtemplate.Constants;
import org.buildmlearn.matchtemplate.R;
import org.buildmlearn.matchtemplate.activities.MainActivity;
import org.buildmlearn.matchtemplate.adapter.MatchArrayAdapter_A;
import org.buildmlearn.matchtemplate.adapter.MatchArrayAdapter_B;
import org.buildmlearn.matchtemplate.data.MatchDb;
import org.buildmlearn.matchtemplate.data.MatchModel;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @brief Activity for displaying score with matched results in match template's app.
 *
 * Created by Anupam (opticod) on 26/7/16.
 */
public class DetailActivityFragment extends Fragment {

    private static final String SELECTED_KEY_A = "selected_position_a";
    private static final String SELECTED_KEY_B = "selected_position_b";

    private int mPositionA = ListView.INVALID_POSITION;
    private int mPositionB = ListView.INVALID_POSITION;
    private ListView listViewA;
    private ListView listViewB;

    private ArrayList<MatchModel> matchListA;
    private ArrayList<MatchModel> matchListB;
    private MatchDb db;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("matchListA", matchListA);
        outState.putParcelableArrayList("matchListB", matchListB);
        if (mPositionA != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY_A, mPositionA);
        }
        if (mPositionB != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY_B, mPositionB);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("matchListA") || !savedInstanceState.containsKey("matchListB")) {
            matchListA = new ArrayList<>();
            matchListB = new ArrayList<>();
        } else {
            matchListA = savedInstanceState.getParcelableArrayList("matchListA");
            matchListB = savedInstanceState.getParcelableArrayList("matchListB");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = new MatchDb(getContext());
        db.open();

        Bundle arguments = getArguments();
        if (arguments != null) {
            matchListA = arguments.getParcelableArrayList(Constants.first_list);
            matchListB = arguments.getParcelableArrayList(Constants.second_list);
        }

        long countScore = 0;
        for (int i = 0; i < matchListA.size(); i++) {
            MatchModel matchA = matchListA.get(i);
            MatchModel matchB = matchListB.get(i);
            if (!matchA.getMatchA().equals(matchB.getMatchA())) {
                matchA.setCorrect(1);
                matchB.setCorrect(1);
            } else {
                countScore++;
                matchA.setCorrect(2);
                matchB.setCorrect(2);
            }
        }


        MatchArrayAdapter_A matchListAdapterA = new MatchArrayAdapter_A(
                getActivity(), matchListA);

        MatchArrayAdapter_B matchListAdapterB = new MatchArrayAdapter_B(
                getActivity(), matchListB);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listViewA = (ListView) rootView.findViewById(R.id.list_view_match_A);
        listViewB = (ListView) rootView.findViewById(R.id.list_view_match_B);

        ColorDrawable colDivider = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.white_primary_text));
        listViewA.setDivider(colDivider);
        listViewB.setDivider(colDivider);

        listViewA.setDividerHeight(2);
        listViewB.setDividerHeight(2);

        handleListViewListeners();


        View header_A = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_detail_header_a, null);
        View footer_A = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_detail_footer_a, null);
        listViewA.addHeaderView(header_A);
        listViewA.addFooterView(footer_A);
        listViewA.setAdapter(matchListAdapterA);

        View header_B = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_detail_header_b, null);
        View footer_B = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_detail_footer_b, null);
        listViewB.addHeaderView(header_B);
        listViewB.addFooterView(footer_B);
        listViewB.setAdapter(matchListAdapterB);

        ((TextView) rootView.findViewById(R.id.score)).setText(String.format(Locale.ENGLISH, "Score : %d of %d", countScore, matchListA.size()));

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY_A) && savedInstanceState.containsKey(SELECTED_KEY_B)) {
            mPositionA = savedInstanceState.getInt(SELECTED_KEY_A);
            mPositionB = savedInstanceState.getInt(SELECTED_KEY_B);
        }

        rootView.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class)
                        .setType("text/plain")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });

        Cursor meta = db.getMetaCursor();

        meta.moveToFirst();
        ((TextView) rootView.findViewById(R.id.first_list_title)).setText(meta.getString(Constants.COL_FIRST_TITLE));
        ((TextView) rootView.findViewById(R.id.second_list_title)).setText(meta.getString(Constants.COL_SECOND_TITLE));

        return rootView;
    }

    private void handleListViewListeners() {

        listViewA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

        listViewB.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //Left empty
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View v = view.getChildAt(0);
                if (v != null)
                    listViewA.setSelectionFromTop(firstVisibleItem, v.getTop());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
