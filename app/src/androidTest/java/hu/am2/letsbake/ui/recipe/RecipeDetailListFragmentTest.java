package hu.am2.letsbake.ui.recipe;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import hu.am2.letsbake.R;
import hu.am2.letsbake.Utils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;


@RunWith(AndroidJUnit4.class)
public class RecipeDetailListFragmentTest {

    private static final int RECIPE_ID = 3;

    private IdlingResource idlingResource;

    @Rule
    public final ActivityTestRule<RecipeDetailActivity> activityTestRule = new ActivityTestRule<>(RecipeDetailActivity.class, true, false);

    @Before
    public void registerIdlingResource() {

        Intent intent = new Intent();
        intent.putExtra(Utils.EXTRA_RECIPE_ID, RECIPE_ID);
        activityTestRule.launchActivity(intent);

        idlingResource = activityTestRule.getActivity().getIdlingResource();

        IdlingRegistry.getInstance().register(idlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }

    @Test
    public void launchRecipeStepOnRecipeStepClick() {

        onView(ViewMatchers.withId(R.id.recipeDetailList)).perform(RecyclerViewActions.actionOnItemAtPosition(10, click()));

        onView(ViewMatchers.withId(R.id.recipeStepDescription)).check(matches(isDisplayed()));
    }
}
