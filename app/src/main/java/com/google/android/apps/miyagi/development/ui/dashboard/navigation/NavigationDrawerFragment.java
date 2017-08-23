package com.google.android.apps.miyagi.development.ui.dashboard.navigation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.commondata.Student;
import com.google.android.apps.miyagi.development.data.models.commondata.menu.Legal;
import com.google.android.apps.miyagi.development.data.models.commondata.menu.Menu;
import com.google.android.apps.miyagi.development.data.models.commondata.menu.MenuItem;
import com.google.android.apps.miyagi.development.data.models.menu.BaseNavigationMenuItem;
import com.google.android.apps.miyagi.development.data.models.menu.NavigationMenuFooterItem;
import com.google.android.apps.miyagi.development.data.models.menu.NavigationMenuHeaderItem;
import com.google.android.apps.miyagi.development.data.models.menu.NavigationMenuItem;
import com.google.android.apps.miyagi.development.data.models.menu.NavigationMenuSeparatorItem;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponse;
import com.google.android.apps.miyagi.development.data.net.responses.commondata.CommonDataResponseData;
import com.google.android.apps.miyagi.development.data.net.responses.core.ResponseStatus;
import com.google.android.apps.miyagi.development.data.net.responses.notifications.NotificationsRequestData;
import com.google.android.apps.miyagi.development.data.net.services.CommonDataService;
import com.google.android.apps.miyagi.development.data.net.services.NotificationsService;
import com.google.android.apps.miyagi.development.data.storage.config.ConfigStorage;
import com.google.android.apps.miyagi.development.ui.dashboard.navigation.common.NavigationDrawerCallbacks;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 03.11.2016.
 */

public class NavigationDrawerFragment extends Fragment {

    public static final String NAVIGATION_DRAWER_TAG = "NAVIGATION_DRAWER";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    @Inject CommonDataService mCommonDataService;
    @Inject NotificationsService mNotificationsService;
    @Inject ConfigStorage mConfigStorage;
    private Subscription mApiSubscription;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mMenuRecycler;
    private NavigationAdapter mMenuAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationDrawerCallbacks mCallbacks;
    private int mCurrentSelectedPosition = 1;
    private boolean mRefreshCommonData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationDrawerCallbacks) {
            mCallbacks = (NavigationDrawerCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleApplication.getInstance().getAppComponent().inject(this);
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            mRefreshCommonData = bundle.getBoolean(BundleKey.REFRESH_COMMON_DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_drawer_fragment, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> insets.consumeSystemWindowInsets());

        mMenuRecycler = (RecyclerView) view.findViewById(R.id.menu_recycler);
        initRecycler();
        initMenu();
        return view;
    }

    /**
     * Users of this fragment must call this method to set up the navigation
     * drawer interactions.
     *
     * @param toolbar      toolbar to interact with ActionBarDrawerToggle
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setup(Toolbar toolbar, DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.empty, R.string.empty);

        mDrawerLayout.post(() -> mDrawerToggle.syncState());
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRefreshCommonData) {
            getCommonData();
        } else {
            initCommonData(mConfigStorage.getCommonData());
        }
    }

    private void getCommonData() {
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = Observable.create((Observable.OnSubscribe<CommonDataResponse>) subscriber -> {
            mCommonDataService.getCommonData().subscribe(new Subscriber<CommonDataResponse>() {
                @Override
                public void onCompleted() {
                    subscriber.onCompleted();
                }

                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onNext(CommonDataResponse commonData) {
                    subscriber.onNext(commonData);
                }
            });
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCommonDataReceived);
    }

    private void onCommonDataReceived(CommonDataResponse commonDataResponse) {
        mRefreshCommonData = false;
        mConfigStorage.saveCommonData(commonDataResponse.getResponseData());
        initCommonData(commonDataResponse.getResponseData());
    }

    private void initCommonData(CommonDataResponseData commonDataResponseData) {
        if ((commonDataResponseData != null)
                && (commonDataResponseData.getMenu() != null)
                && commonDataResponseData.getMenu().isUsesSystemFont()) {
            GoogleApplication.getInstance().setupSystemFonts();
        }
        initMenu();
        setSelectedItem(mCurrentSelectedPosition, null);
        if (mConfigStorage.readShouldUpdatePushToken()) {
            sendNotificationToken();
        }
    }

    /**
     * Call this method to get menu from web and set it to adapter.
     */
    public void initMenu() {
        Observable.create((Observable.OnSubscribe<ArrayList<BaseNavigationMenuItem>>) subscriber -> {
            Menu menuConfig = mConfigStorage.getCommonData().getMenu();
            ArrayList<BaseNavigationMenuItem> menu = new ArrayList<>();

            Student student = mConfigStorage.getCommonData().getStudent();
            menu.add(new NavigationMenuHeaderItem(student.getUsername(), student.getAvatar()));

            MenuItem menuDash = menuConfig.getDash();
            menu.add(new NavigationMenuItem(menuDash.getTitle(), null, menuDash.getIcon(), null, BaseNavigationMenuItem.MenuType.DASHBOARD));

            menu.add(new NavigationMenuSeparatorItem());

            List<MenuItem> menuExternal = menuConfig.getExternal();
            for (MenuItem itemExternal : menuExternal) {
                menu.add(new NavigationMenuItem(itemExternal.getTitle(), itemExternal.getUrl(), itemExternal.getIcon(), null, BaseNavigationMenuItem.MenuType.WEB));
            }

            menu.add(new NavigationMenuSeparatorItem());

            MenuItem menuProfile = menuConfig.getProfile();
            menu.add(new NavigationMenuItem(menuProfile.getTitle(), null, menuProfile.getIcon(), null, BaseNavigationMenuItem.MenuType.PROFILE));

            MenuItem menuSignout = menuConfig.getSignout();
            menu.add(new NavigationMenuItem(menuSignout.getTitle(), null, menuSignout.getIcon(), null, BaseNavigationMenuItem.MenuType.SIGNOUT));

            menu.add(new NavigationMenuSeparatorItem());

            Legal menuLegal = menuConfig.getLegal();
            menu.add(new NavigationMenuFooterItem(menuLegal));

            subscriber.onNext(menu);
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<BaseNavigationMenuItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(ArrayList<BaseNavigationMenuItem> items) {
                        mMenuAdapter.setData(items);
                    }
                });
    }

    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMenuRecycler.setLayoutManager(layoutManager);
        mMenuAdapter = new NavigationAdapter(getActivity(), mConfigStorage.getCommonData().getColors().getMainBackgroundColor());
        mMenuAdapter.setOnItemSelectedListener(this::selectItem);
        mMenuRecycler.setAdapter(mMenuAdapter);
    }

    private void selectItem(int position, BaseNavigationMenuItem item) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START, true);
        }
        if (mCallbacks != null && item != null) {
            mCallbacks.onNavigationDrawerItemSelected(position, item);
        }
        setSelectedItem(position, item);
    }

    private void setSelectedItem(int position, BaseNavigationMenuItem item) {
        mCurrentSelectedPosition = position;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDrawerLayout != null) {
            mDrawerLayout.removeDrawerListener(mDrawerToggle);
        }
        mCallbacks = NavigationDrawerCallbacks.NULL;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onStop() {
        super.onStop();
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = null;
    }

    private void sendNotificationToken() {
        SubscriptionHelper.unsubscribe(mApiSubscription);
        mApiSubscription = Observable.create((Observable.OnSubscribe<ResponseStatus>) subscriber -> {
            NotificationsRequestData notificationsRequestData = new NotificationsRequestData(
                    mConfigStorage.readPushToken(),
                    mConfigStorage.getCommonData().getPushXsrfToken());
            mNotificationsService.addNotificationsToken(notificationsRequestData).subscribe(new Subscriber<ResponseStatus>() {
                @Override
                public void onCompleted() {
                    subscriber.onCompleted();
                }

                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onNext(ResponseStatus responseStatus) {
                    subscriber.onNext(responseStatus);
                }
            });
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNotificationTokenSent);
    }

    private void onNotificationTokenSent(ResponseStatus responseStatus) {
        mConfigStorage.saveShouldUpdatePushToken(false);
    }

    public static NavigationDrawerFragment newInstnce(boolean refreshCommonData) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BundleKey.REFRESH_COMMON_DATA, refreshCommonData);
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public interface BundleKey {
        String REFRESH_COMMON_DATA = "REFRESH_COMMON_DATA";
    }
}