
package dekar.bakerapp.views;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dekar.bakerapp.R;
import dekar.bakerapp.models.Recipe;
import dekar.bakerapp.models.Step;

import static dekar.bakerapp.views.RecipeActivity.SELECTED_INDEX;
import static dekar.bakerapp.views.RecipeActivity.SELECTED_RECIPES;
import static dekar.bakerapp.views.RecipeActivity.SELECTED_STEPS;


public class RecipeStepDetailFragment extends Fragment {
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private ArrayList<Step> steps = new ArrayList<>();
    private int selectedIndex;
    ArrayList<Recipe> recipe;
    String recipeName;

    public RecipeStepDetailFragment() {

    }

    private ListItemClickListener itemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(List<Step> allSteps, int Index, String recipeName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView recipeDefaulsText;
        bandwidthMeter = new DefaultBandwidthMeter();

        itemClickListener = (RecipeDetailActivity) getActivity();

        recipe = new ArrayList<>();

        if (savedInstanceState != null) {
            steps = savedInstanceState.getParcelableArrayList(SELECTED_STEPS);
            selectedIndex = savedInstanceState.getInt(SELECTED_INDEX);
            recipeName = savedInstanceState.getString("Title");


        } else {
            steps = getArguments().getParcelableArrayList(SELECTED_STEPS);
            if (steps != null) {
                steps = getArguments().getParcelableArrayList(SELECTED_STEPS);
                selectedIndex = getArguments().getInt(SELECTED_INDEX);
                recipeName = getArguments().getString("Title");
            } else {
                recipe = getArguments().getParcelableArrayList(SELECTED_RECIPES);
                //casting List to ArrayList
                steps = (ArrayList<Step>) recipe.get(0).getSteps();
                selectedIndex = 0;
            }

        }


        View rootView = inflater.inflate(R.layout.recipe_step_detail_fragment_body_part, container, false);
        recipeDefaulsText = rootView.findViewById(R.id.recipe_step_detail_text);
        recipeDefaulsText.setText(steps.get(selectedIndex).getDescription());
        recipeDefaulsText.setVisibility(View.VISIBLE);

        simpleExoPlayerView = rootView.findViewById(R.id.playerView);
        simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        String videoURL = steps.get(selectedIndex).getVideoURL();

        recipeName = ((RecipeDetailActivity) getActivity()).recipeName;
        ((RecipeDetailActivity) getActivity()).getSupportActionBar().setTitle(recipeName);


        String imageUrl = steps.get(selectedIndex).getThumbnailURL();
        ImageView thumbImage = rootView.findViewById(R.id.thumbImage);
        if (!imageUrl.trim().isEmpty()) {
            Uri builtUri = Uri.parse(imageUrl).buildUpon().build();
            Picasso.with(getContext()).load(builtUri).into(thumbImage);
            thumbImage.setVisibility(View.VISIBLE);
        } else {
          thumbImage.setVisibility(View.INVISIBLE);
        }

        if (!videoURL.isEmpty()) {
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            long playerPosition = 0;
            boolean isPlayingNow = true;
            if(null != savedInstanceState){
                playerPosition = savedInstanceState.getLong("playerPosition", 0);
                isPlayingNow = savedInstanceState.getBoolean("playerIsPlayingNow", true);
            }

            initializePlayer(Uri.parse(steps.get(selectedIndex).getVideoURL()), playerPosition, isPlayingNow);

        } else {
            simpleExoPlayerView.setVisibility(View.GONE);
            player = null;
        }


        Button mPrevStep = rootView.findViewById(R.id.previousStep);
        Button mNextstep = rootView.findViewById(R.id.nextStep);

        mPrevStep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (steps.get(selectedIndex).getId() > 0) {
                    if (player != null) {
                        player.stop();
                    }
                    itemClickListener.onListItemClick(steps, steps.get(selectedIndex).getId() - 1, recipeName);
                } else {
                    Toast.makeText(getActivity(), getResources().getText(R.string.recipe_first_page), Toast.LENGTH_SHORT).show();

                }
            }
        });

        mNextstep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                int lastIndex = steps.size() - 1;
                if (steps.get(selectedIndex).getId() < steps.get(lastIndex).getId()) {
                    if (player != null) {
                        player.stop();
                    }
                    itemClickListener.onListItemClick(steps, steps.get(selectedIndex).getId() + 1, recipeName);
                } else {
                    Toast.makeText(getContext(), getResources().getText(R.string.recipe_last_page), Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }

    private void initializePlayer(Uri mediaUri, long position, boolean isPlayingNow) {
        if (player == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            simpleExoPlayerView.setPlayer(player);

            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            DataSource.Factory dataSource = new DefaultDataSourceFactory(getContext(), userAgent);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSource)
                    .createMediaSource(mediaUri);
            player.prepare(mediaSource);
            player.seekTo(position);
            player.setPlayWhenReady(isPlayingNow);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList(SELECTED_STEPS, steps);
        currentState.putInt(SELECTED_INDEX, selectedIndex);
        currentState.putString("Title", recipeName);

        if(null != player) {
            boolean isPlayWhenReady = player.getPlayWhenReady();
            currentState.putBoolean("playerIsPlayingNow", isPlayWhenReady);
            currentState.putLong("playerPosition", player.getCurrentPosition());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

}
