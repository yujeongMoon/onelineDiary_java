package com.example.onelinediary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.onelinediary.R;
import com.example.onelinediary.dto.Profile;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;

public class GridViewImageItemAdapter extends BaseAdapter {
    private final ArrayList<Profile> profileList;
    Profile selectedProfile = null;

    public GridViewImageItemAdapter(ArrayList<Profile> profiles) {
        this.profileList = profiles;
    }

    @Override
    public int getCount() {
        return profileList.size();
    }

    @Override
    public Object getItem(int position) {
        return profileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.viewholder_grid_view_image_item, parent, false);

            ImageView profile = convertView.findViewById(R.id.profile_image);
            profile.setImageResource(Utility.getResourceImage(parent.getContext(), profileList.get(position).getImageResName()));

            LinearLayout layout = convertView.findViewById(R.id.profile_layout);
            profileList.get(position).setProfileLayout(layout);

            convertView.setTag(position);

            if(profileList.get(position).isChecked()) {
                layout.setBackgroundResource(R.drawable.bg_rectangle_red);
            } else {
                layout.setBackgroundResource(0);
            }

            convertView.setOnClickListener(v -> {
                if (profileList.get((int)v.getTag()).isChecked()) {
                    profileList.get((int)v.getTag()).setChecked(false);
                    selectedProfile = null;
                } else {
                    profileList.get((int)v.getTag()).setChecked(true);
                    selectedProfile = profileList.get((int)v.getTag());
                }

                onSelectOtherProfile(profileList.get((int)v.getTag()));
            });
        } else {
            View view = new View(parent.getContext());
            view = (View) convertView;
        }

        return convertView;
    }

    public void onSelectOtherProfile(Profile selectedProfile) {
        for (Profile profile : profileList) {
            if (selectedProfile != profile) {
                profile.setChecked(false);
            }
        }
        setCheckLayout();
    }

    private void setCheckLayout() {
        for (Profile profile : profileList) {
            if(profile.isChecked()) {
                profile.getProfileLayout().setBackgroundResource(R.drawable.bg_rectangle_red);
            } else {
                profile.getProfileLayout().setBackgroundResource(0);
            }
        }
        notifyDataSetChanged();
    }

    public Profile getSelectedProfile() {
        return this.selectedProfile;
    }
}
