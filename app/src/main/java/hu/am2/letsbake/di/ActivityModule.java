package hu.am2.letsbake.di;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.am2.letsbake.ui.recipe.RecipeDetailActivity;
import hu.am2.letsbake.ui.recipebrowser.RecipeListActivity;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract RecipeListActivity providesMaintActivity();

    @ContributesAndroidInjector
    abstract RecipeDetailActivity providesRecipeDetailActivity();
}
