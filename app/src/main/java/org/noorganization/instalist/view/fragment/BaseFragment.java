package org.noorganization.instalist.view.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;

/**
 * A general fragment that abstracts the normal fragment. In this should be done a lot of work for later
 * version compatibility.
 * <p>Inherit from this to get the best fitting fragment experience and to reduce repeated maintenance work.</p>
 * Created by Tino on 24.10.2015.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * Deprecated on API 23.
     * Use onAttach(Context _Context) instead.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity _Activity) {
        super.onAttach(_Activity);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(_Activity);
        }
    }

    /**
     * onAttach(Context) is not called before android m.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context _Context) {
        super.onAttach(_Context);
        onAttachToContext(_Context);
    }

    /**
     * Used to initialize the context of the app. Used to implement a good solution for android with
     * deprecated onAttach(Activity activity) and current onAttach(Context context) to get the best
     * experience.
     *
     * @param _Context the context of the current activity.
     */
    protected abstract void onAttachToContext(Context _Context);
}
