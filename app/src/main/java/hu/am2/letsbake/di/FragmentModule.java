package hu.am2.letsbake.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.am2.letsbake.ui.recipe.RecipeDetailListFragment;
import hu.am2.letsbake.ui.recipe.RecipeStepFragment;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract RecipeDetailListFragment providesRecipeDetailListFragment();

    @ContributesAndroidInjector
    abstract RecipeStepFragment providesRecipeStepFragment();

}
