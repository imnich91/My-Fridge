package iann91.uw.tacoma.edu.myfridge.Recipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import iann91.uw.tacoma.edu.myfridge.R;
import iann91.uw.tacoma.edu.myfridge.Recipe.recipeItem.RecipeContent;


/**
 * Class for RecipeDetailFragment. This class represents the details about a recipe.
 *
 */
public class RecipeDetailFragment extends Fragment {

    /** Constant for which recipe has been selected. */
    public final static String RECIPE_ITEM_SELECTED = "recipe_selected";

    /** TextView element for the recipe's title. */
    private TextView mRecipeTitle;

    /** ImageView element for the recipe's image. */
    private ImageView mRecipeImage;

    /** TextView element for the recipe's ingredients. */
    private TextView mRecipeIngredients;

    /** Button which adds a recipe to my recipes content.   */
    private Button mAddButton;


    /** Current Recipe which is being used. */
    private RecipeContent mRecipe;

    /**
     *  Required empty public constructor
     */
    public RecipeDetailFragment() {

    }

    /**
     * Method for constructing a new instance of this Fragment.
     *
     * @return A new instance of fragment RecipeDetailFragment.
     */
    public static RecipeDetailFragment newInstance() {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate() method for this Fragment.
     *
     * @param savedInstanceState - the savedInstanceState for this Fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     *
     * Inflates the view and the view elements for this Fragment.
     *
     * @param inflater - the inflater for this Fragment.
     * @param container - the container for this Fragment.
     * @param savedInstanceState - the savedInstanceState for this Fragment.
     *
     * @return - a view object of this Fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.recipe_detail_frag, container, false);
        mRecipeTitle = (TextView) view.findViewById(R.id.recipe_title_tv);
        mRecipeImage = (ImageView) view.findViewById(R.id.recipe_img);
        mRecipeIngredients = (TextView) view.findViewById(R.id.recipe_ingredients);
        mAddButton = (Button) view.findViewById(R.id.saveRecipeButton);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            // HERE we are saving recipe to my saved recipe class
            @Override
            public void onClick(View v) {
                RecipeContent newRecipe = new RecipeContent(mRecipeTitle.getText().toString(), mRecipe.getmImageUrl(),  mRecipe.getmIngredients() ,mRecipe.getmInstructionUrl() );
                MyRecipesFragment newrecipe = MyRecipesFragment.newInstance(newRecipe);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Toast.makeText(getActivity(), "Added new recipe to My Recipes", Toast.LENGTH_LONG).show();
                fm.popBackStackImmediate();


            }
        });
        return view;
    }

    /**
     * onStart() method for this Fragment. Sets article based on argument passed in.
     */
    @Override
    public void onStart() {
        super.onStart();
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            updateView((RecipeContent) args.getSerializable(RECIPE_ITEM_SELECTED));
        }

    }

    /**
     * Updates the view for this Fragment based on which recipe has been selected.
     *
     * @param recipe - The recipe that has been selected.
     */
    public void updateView(final RecipeContent recipe) {
        if (recipe != null) {
            mRecipe= recipe;
            mRecipeTitle.setText(recipe.getmTitle());
            MyRecipeRecyclerViewAdapter.DownloadImageTask task = new MyRecipeRecyclerViewAdapter
                    .DownloadImageTask(mRecipeImage);
            task.execute(recipe.getmImageUrl());

            String joinIngredients = "";
            for(String s : recipe.getmIngredients()) {
                joinIngredients += s + "\n";
            }
            mRecipeIngredients.setText(joinIngredients);


        }
    }

}
