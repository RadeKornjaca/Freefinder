package org.freefinder.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.freefinder.R;
import org.freefinder.api.PlaceApi;
import org.freefinder.api.places.SearchPlacesService;
import org.freefinder.model.Category;
import org.freefinder.model.Place;
import org.freefinder.osmdroid.PlaceInfoWindow;
import org.freefinder.shared.SharedPreferencesHelper;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int APP_LOCATION_PERMISSION = 1;
    private static final int DEFAULT_INACTIVITY_DELAY_IN_MILLISECONDS = 200;

    public static final String UPPER_LEFT_COORDINATES = "upperLeftCoordinates";
    public static final String DOWN_RIGHT_COORDINATES = "downRightCoordinates";
    public static final String CATEGORY_ID = "categoryId";
    public static final String USER_LOCATION = "userLocation";

    private final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int ZOOM_LEVEL = 20;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.map) MapView mapView;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationNewOverlay;

    private boolean mLocationRequest = false;
    private LocationManager mLocationManager;
    private Location mLocation;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private String query;
    private String queryType;

    private Realm realm;
    private Category category;
    private RealmResults<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialization
        Realm.init(this);

        // End of initialization
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                mLocationRequest = true;
            } else {

                ActivityCompat.requestPermissions(this, permissions, APP_LOCATION_PERMISSION);
            }
        } else {
            mLocationRequest = true;
        }

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                fetchPlacesFromBoundary();
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                fetchPlacesFromBoundary();
                return false;
            }
        }, DEFAULT_INACTIVITY_DELAY_IN_MILLISECONDS));

        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), mapView);
        myLocationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(this.myLocationNewOverlay);

        mapController = mapView.getController();
        mapController.setZoom(ZOOM_LEVEL);

        if(mLocationRequest == true) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            String locationProvider = mLocationManager.getBestProvider(criteria, true);
            mLocation = mLocationManager.getLastKnownLocation(locationProvider);
            updateLocationUI(mLocation);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPlaceIntent = new Intent(MainActivity.this, AddPlaceActivity.class);
                addPlaceIntent.putExtra(USER_LOCATION, mLocation);
                startActivity(addPlaceIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Search querying
        realm = Realm.getDefaultInstance();

        Intent currentIntent = getIntent();
        if (Intent.ACTION_SEARCH.equals(currentIntent.getAction())) {
            query = currentIntent.getStringExtra(SearchManager.QUERY);

            clearPlaces();
            category = realm.where(Category.class).equalTo("name", query).findFirst();
            queryType = (category != null) ? "Category" : "Place";
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String accessToken = sharedPreferences.getString(getString(R.string.settings_access_token), null);

        String accessToken = SharedPreferencesHelper.getAuthorizationToken(this);

        // When in onResume method, app will insist that user logs in whatever he tries to do
        // For example: tries to press back button while on login form activity
        if (accessToken == null) {
            Intent authenticationIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(authenticationIntent);
        }

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        final String searchCategoryName = preferences.getString("searchCategoryName", null);

        if(searchCategoryName != null) {
            category = realm.where(Category.class).equalTo("name", searchCategoryName).findFirst();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(category != null) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("searchCategoryName", category.getName());
            editor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.apply();

        realm.close();
    }

    private void clearPlaces() {
        mapView.getOverlayManager().clear();
        mapView.invalidate();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Place.class);
            }
        });
    }

    private void fetchPlacesFromBoundary() {
        if(mLocation != null && query != null && queryType != null) {
            BoundingBox boundingBox = mapView.getProjection().getBoundingBox();
            GeoPoint screenTopLeft = new GeoPoint(boundingBox.getLatNorth(),
                                                  boundingBox.getLonWest());
            GeoPoint screenBottomRight = new GeoPoint(boundingBox.getLatSouth(),
                                                      boundingBox.getLonEast());

//            PlaceApi.SearchAreaByCategoryService.startService(this, category,
//                                                              screenTopLeft, screenBottomRight);

            SearchPlacesService.startService(this, query, queryType,
                                             screenTopLeft, screenBottomRight);
            places = realm.where(Place.class)
//                          .between("lat", screenBottomRight.getLatitude(),
//                                          screenTopLeft.getLatitude())
//                          .between("lng", screenTopLeft.getLongitude(),
//                                          screenBottomRight.getLatitude())
//                          .equalTo("category.id", category.getId())
                          .findAllAsync();

            places.addChangeListener(new RealmChangeListener<RealmResults<Place>>() {
                @Override
                public void onChange(RealmResults<Place> places) {
                    mapView.getOverlayManager().clear();

                    for(Place place : places) {
                        Marker placeMarker = new Marker(mapView);
                        final GeoPoint placePoint = new GeoPoint(place.getLat(), place.getLng());
                        placeMarker.setPosition(placePoint);
                        placeMarker.setIcon(getResources().getDrawable(R.drawable.ic_place));
                        placeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        placeMarker.setInfoWindow(new PlaceInfoWindow(mapView));
                        placeMarker.setRelatedObject(place);

                        mapView.getOverlays().add(placeMarker);
                    }

                    mapView.invalidate();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.map_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_categories) {
            Intent categoryIntent = new Intent(getApplicationContext(), CategoriesActivity.class);
            startActivity(categoryIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_my_location) {
            updateLocationUI(mLocation);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {
            case APP_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationRequest = true;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateLocationUI(Location location) {
        if(location != null) {
            final double userLatitude = location.getLatitude();
            final double userLongitude = location.getLongitude();

            GeoPoint myLocationPoint = new GeoPoint(userLatitude, userLongitude);
            mapController.setCenter(myLocationPoint);
        } else {
            Snackbar.make( findViewById(android.R.id.content),
                           "Can't find user location!",
                           Snackbar.LENGTH_LONG ).show();
        }
    }
}
