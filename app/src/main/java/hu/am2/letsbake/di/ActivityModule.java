package hu.am2.letsbake.di;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import hu.am2.letsbake.ui.recipe.RecipeDetailActivity;
import hu.am2.letsbake.ui.recipe.RecipeStepActivity;
import hu.am2.letsbake.ui.recipebrowser.RecipeBrowserActivity;
import hu.am2.letsbake.ui.widgetconfiguration.WidgetConfigurationActivity;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract RecipeBrowserActivity providesMaintActivity();

    @ContributesAndroidInjector
    abstract RecipeDetailActivity providesRecipeDetailActivity();

    @ContributesAndroidInjector
    abstract RecipeStepActivity providesRecipeStepActivity();

    @ContributesAndroidInjector
    abstract WidgetConfigurationActivity providesWidgetConfigurationActivity();
}
