package aleksey.sheyko.staticdemo.app.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import aleksey.sheyko.staticdemo.R;
import aleksey.sheyko.staticdemo.rest.twitter.RestClient;
import io.fabric.sdk.android.Fabric;

public class TwitterFragment extends Fragment {

    public static final String TAG = TwitterFragment.class.getSimpleName();

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "bmQCF0w32m1awcIOIua56yPqa";
    private static final String TWITTER_SECRET = "8XjePz2wz3TjuvRBuN5fk6O1WzthXYEEvB6AVk1xayVUq5hhnY";

    private TwitterLoginButton loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_twitter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));

        loginButton = (TwitterLoginButton)
                view.findViewById(R.id.login_button);
        loginButton.setEnabled(true);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a
                // TwitterSession for making API calls
                TwitterApiClient twitterApiClient =
                        TwitterCore.getInstance().getApiClient(result.data);

                twitterApiClient.getAccountService().verifyCredentials(
                        false, false, new Callback<User>() {
                            @Override
                            public void success(Result<User> userResult) {
                                int count = userResult.data.favouritesCount;
                            }

                            @Override
                            public void failure(TwitterException e) {
                                Log.e(TAG, "Failed to get account credentials: " + e);
                            }
                        }
                );

                Toast.makeText(getActivity(),
                        "Id: " + result.data.getUserId(), Toast.LENGTH_SHORT).show();

                new RestClient().getApiService().getDummieContent();

            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getActivity(), "Failure",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setTitle("Twitter");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode,
                data);
    }
}