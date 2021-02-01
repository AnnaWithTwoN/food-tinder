package edu.um.feri.pora.foodtinder.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import edu.um.feri.pora.foodtinder.MyApplication;
import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.foodtinder.activities.ExplorerActivity;
import edu.um.feri.pora.lib.User;

public class MapFragment extends Fragment {
    private User user;
    private MapView map;
    private Marker marker;
    private List<TargetAdapter> targets;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        user = ((MyApplication)((ExplorerActivity) requireActivity()).getApplication()).getUser();
        targets = new ArrayList<>();

        //Context ctx = getContext();
        //Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = (MapView) root.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(17f);
        GeoPoint startPoint = new GeoPoint(46.55951,15.63970); //Postavi GPS lokacijo
        mapController.setCenter(startPoint);

        //startPoint.setLatitude(startPoint.getLatitude()+(rnd.nextDouble()-0.5)*0.001);
        //mapController.setCenter(startPoint);
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    if(data.getKey().equals(user.getId()) || user.hasLiked(data.getKey())) continue;
                    User p = data.getValue(User.class);
                    Log.d("marker", "came - " + p.getName());
                    putMarker(p);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO error handling
            }
        });
        return root;
    }

    private class TargetAdapter extends View implements Target{
        private User user;

        public TargetAdapter(Context context, User user) {
            super(context);
            this.user = user;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable icon = getResources().getDrawable(R.drawable.ic_location_photo);
            Bitmap croppedPhoto;
            if (bitmap.getWidth() >= bitmap.getHeight()) {
                croppedPhoto = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight());
            } else {
                croppedPhoto = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());
            }
            Drawable photoDrawable = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(croppedPhoto,  icon.getIntrinsicWidth() - 50, icon.getIntrinsicWidth() - 50, false));

            Marker markerBackground = new Marker(map);
            markerBackground.setPosition(new GeoPoint(user.getLatitude(), user.getLongitude()));
            markerBackground.setIcon(icon);
            markerBackground.setTitle(user.getName());
            markerBackground.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            Marker markerPhoto = new Marker(map);
            markerPhoto.setTitle(user.getName());
            markerPhoto.setIcon(photoDrawable);
            markerPhoto.setPosition(new GeoPoint(user.getLatitude(), user.getLongitude()));
            markerPhoto.setAnchor(Marker.ANCHOR_CENTER, 1.3f);

            map.getOverlays().add(markerBackground);
            map.getOverlays().add(markerPhoto);
            map.invalidate();
            Log.d("marker", user.getName() + " with coords " + user.getLatitude() + ", " + user.getLongitude());
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Log.d("marker", user.getName() + " failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            //Log.d("marker", user.getName() + " loaded");
        }
    }

    private void putMarker(final User user) {
        /*marker2.setTitle("Here I am");
        marker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker2.setPosition(new GeoPoint(46.55951,15.63970));
        marker2.setTextLabelBackgroundColor(Color.GREEN);
        marker2.setTextLabelForegroundColor(Color.RED);
        marker2.setTextLabelFontSize(20);
        marker2.setTextIcon("text");*/

        TargetAdapter targetAdapter = new TargetAdapter(getContext(), user);
        targets.add(targetAdapter);
        Picasso.get().load(user.getPhotoUri()).into(targetAdapter);

    }

}