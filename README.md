# :warning: Deprecated :warning:
Deprecated! Use [Architecture Components](https://developer.android.com/topic/libraries/architecture/)

# RainbowMVP
Lightweight MVP library(for personal using) with easy lifecycle.

For good understanding approach read this [article](https://medium.com/@czyrux/presenter-surviving-orientation-changes-with-loaders-6da6d86ffbbf).

* Really lighweight library
* Easy integrate to project
* Minimum actions to implement MVP
* Doesn't destroy presenter after rotation device
* You can cached data in presenter for restore data after rotate device

In presenter you have 3 methods:
- bindView(V view) - you need call this method for attach your view to presenter.
- unbindView() - you need call this method when view not available already.
- onDestoy() - call, when your presenter will destroy.

Important thing:
You can bind your view to presenter <b>ONLY</b> after <b>onStart()</b> in <b>Activity</b>, and after <b>onResume()</b> in <b>Fragment</b>.

[![](https://jitpack.io/v/ne1c/rainbowmvp.svg)](https://jitpack.io/#ne1c/rainbowmvp)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RainbowMVP-green.svg?style=true)](https://android-arsenal.com/details/1/4112)

# Dependency

Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Step 2. Add the dependency
```groovy
dependencies {
   implementation 'com.github.ne1c:rainbowmvp:1.2.4'
   annotationProcessor 'com.github.ne1c.rainbowmvp:processor:1.2.4'
}
```

# Step 1
Create you View interface:

```java
public interface MyView {
    void showResult(String text);
    
    void showError(@StringRes int resId);
}
```

# Step 2
Create presenter. You need inherit of BasePresenter and add tag:

```java
public class MyPresenter extends BasePresenter<MyView> {
    public static final TAG = "my_presenter";
    ...
    public void makeParty() {
        // some actions
        boolean success = ...
        if (success) {
            getView().showResult(...);
        } else {
            getView().showError(R.string.error);
        }
    }
    ...
}
```
# Step 3
* Implement PresenterStorage:
```java
public class MyPresenterStorage implements PresenterStorage {
    public MyPresenterStorage(...) {
        ...
    }

    @Override
    public BasePresenter create(String tag) {
        if (tag.equals(MyPresenter.TAG)) {
            return new MyPresenter(...);
        }

        return null;
    }
}
```

# Step 4
* Init PresenterFactory, before use any presenter. You can do it in onCreate() of Applicaton or splash screen:
```java
public class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        ...
        PresenterFactory.init(new MyPresenterStorage(...));
    }
}
```

# Step 5
Your activity or fragment need to inherit of BaseActivity/BaseFragment and override getPresenterTag():
```java
@PresenterTag(MyPresenter.TAG)
public class MyActivity extends BaseActivity<MyPresenter> implements MyView {
    ...
    @Ovveride
    public void onStart() {
        super.onStart();
        
        getPresenter().bindView(this);
    }
    
    @Ovveride
    public void onStop() {
        super.onStop();
        
        getPresenter().unbindView();
    }
    ...
}
```

# ViewState
You can use [ViewState](https://github.com/Ne1c/RainbowMVP/blob/master/rainbowmvp/src/main/java/com/ne1c/rainbowmvp/ViewState.java) for saving state of view, and than restore after <b>bindView(...)</b>. For it action you need implement [ViewStateListener](https://github.com/Ne1c/RainbowMVP/blob/master/rainbowmvp/src/main/java/com/ne1c/rainbowmvp/ViewStateListener.java) in your presenter or another place. Method stateChanged(...) will call after every change of ViewState or after call method <b>bindView(...)</b>. Example how it works:
```java
public class MyPresenter extends BasePresenter<MyView> implements ViewStateListener {
    public MyPresenter(...) {
    	...
    	addViewStateListener(this);
    }
    ...
    public void makeParty() {
        setViewState(ViewState.IN_PROGRESS);
        
        getApi().loadData(response -> {
                if (response.isSuccess()) {
                    setViewState(ViewState.SUCCESS);
                    
                    if (getView() != null) {
                        getView().showResult(response.getData());
                    }
                } else if (response.isError()) {
                    setViewState(ViewState.ERROR);
                    
                    if (getView() != null) {
                        getView().showErrorLoadData();
                    }
                }
            });
    }
    
    @Override
    public void stateChanged(ViewState state) {
        if (state == ViewState.IN_PROGRESS) {
            getView().showProgress();
        }

        if (state == ViewState.SUCCESS) {
            getView().showRepos(mCachedData);
            getView().hideProgress();
        }
        
        if (state == ViewState.ERROR) {
            getView().showErrorLoadData()
        }
        
        setViewState(ViewState.NOTHING);
    }
    ...
}
```
