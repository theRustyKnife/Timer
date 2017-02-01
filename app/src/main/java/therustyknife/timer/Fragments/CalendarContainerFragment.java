package therustyknife.timer.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import therustyknife.timer.R;
import therustyknife.timer.Util;


// serves as a container for all the other calendar fragments
// contains the card view everything is situated in and the expand arrow
public class CalendarContainerFragment extends Fragment{
    // the currently displayed fragment
    private CalendarFragment contentFragment;

    private ImageButton expandButton;

    public static CalendarContainerFragment newInstance(){ return new CalendarContainerFragment(); }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_container, container, false);

        // set the default quick calendar fragment
        contentFragment = QuickCalendarFragment.newInstance();
        setContentFragment();

        // get a reference to the arrow button
        expandButton = (ImageButton) view.findViewById(R.id.calendar_expand_button);

        final View.OnClickListener expandClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contentFragment != null){
                    // swap the fragments and the arrow direction
                    if (contentFragment instanceof QuickCalendarFragment){
                        expandButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_36dp));

                        // set the big calendar fragment and listen for day clicks
                        swapContentFragment(CalendarBigFragment.newInstance(new CalendarBigFragment.OnDetailsRequestedListener() {
                            @Override
                            public void onDetailsRequested(int dayOffset) {
                                // display the day detail
                                swapContentFragment(CalendarDayDetailFragment.newInstance(dayOffset));
                            }
                        }));
                    }
                    else{
                        expandButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_36dp));

                        swapContentFragment(QuickCalendarFragment.newInstance());
                    }
                }
            }
        };

        // set the above listener to the button and the layout it's in, so the user can click anywhere on the bottom part to trigger it
        view.findViewById(R.id.calendar_expand_layout).setOnClickListener(expandClick);
        expandButton.setOnClickListener(expandClick);

        return view;
    }


    private void removeContentFragment(){
        if (contentFragment != null) getChildFragmentManager().beginTransaction().remove(contentFragment).commit();
    }

    private void setContentFragment(){
        if (contentFragment != null) getChildFragmentManager().beginTransaction().add(R.id.calendar_content, contentFragment).commit();
    }

    private void swapContentFragment(CalendarFragment f){
        removeContentFragment();
        contentFragment = f;
        setContentFragment();
    }

    @Override
    public void onResume(){
        super.onResume();

        // note: this shouldn't really be needed as the calendar view should be removed and recreated by the parent on load

        if (contentFragment != null) {
            if (contentFragment instanceof QuickCalendarFragment) contentFragment.resume(); // resume the fragment
            else{
                // set the default fragment back and make sure the arrow is correct
                final ImageButton expandButton = (ImageButton) getView().findViewById(R.id.calendar_expand_button);
                expandButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_36dp));
                swapContentFragment(QuickCalendarFragment.newInstance());
            }
        }
    }

    // handle the back click from the parent activity
    // return true if handled, false if the parent should handle it
    public boolean onBackPressed(){
        if (contentFragment != null && !(contentFragment instanceof QuickCalendarFragment)){
            swapContentFragment(QuickCalendarFragment.newInstance());
            expandButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_36dp));
            return true;
        }

        return false;
    }
}
