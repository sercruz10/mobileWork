/*  Copyright (C) 2017-2018 Carsten Pfeiffer, Daniele Gobbetti

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import pm.seniorhelp.R;

public class AppBlacklistAdapter extends RecyclerView.Adapter<AppBlacklistAdapter.AppBLViewHolder> implements Filterable {

    private List<ApplicationInfo> applicationInfoList;
    private final int mLayoutId;
    private final Context mContext;
    private final PackageManager mPm;
    private final IdentityHashMap<ApplicationInfo, String> mNameMap;

    private ApplicationFilter applicationFilter;

    public AppBlacklistAdapter(int layoutId, Context context) {
        mLayoutId = layoutId;
        mContext = context;
        mPm = context.getPackageManager();

        applicationInfoList = mPm.getInstalledApplications(PackageManager.GET_META_DATA);

        // sort the package list by label and blacklist status
        mNameMap = new IdentityHashMap<>(applicationInfoList.size());
        for (ApplicationInfo ai : applicationInfoList) {
            CharSequence name = mPm.getApplicationLabel(ai);
            if (name == null) {
                name = ai.packageName;
            }
            if (GBApplication.appIsBlacklisted(ai.packageName)) {
                // sort blacklisted first by prefixing with a '!'
                name = "!" + name;
            }
            mNameMap.put(ai, name.toString());
        }

        Collections.sort(applicationInfoList, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo ai1, ApplicationInfo ai2) {
                final String s1 = mNameMap.get(ai1);
                final String s2 = mNameMap.get(ai2);
                return s1.compareTo(s2);
            }
        });

    }

    @Override
    public AppBlacklistAdapter.AppBLViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        return new AppBLViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppBlacklistAdapter.AppBLViewHolder holder, int position) {
        final ApplicationInfo appInfo = applicationInfoList.get(position);

        holder.deviceAppVersionAuthorLabel.setText(appInfo.packageName);
        holder.deviceAppNameLabel.setText(mNameMap.get(appInfo));
        holder.deviceImageView.setImageDrawable(appInfo.loadIcon(mPm));

        holder.checkbox.setChecked(GBApplication.appIsBlacklisted(appInfo.packageName));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = ((CheckBox) v.findViewById(R.id.item_checkbox));
                checkBox.toggle();
                if (checkBox.isChecked()) {
                    GBApplication.addAppToBlacklist(appInfo.packageName);
                } else {
                    GBApplication.removeFromAppsBlacklist(appInfo.packageName);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return applicationInfoList.size();
    }

    @Override
    public Filter getFilter() {
        if (applicationFilter == null)
            applicationFilter = new ApplicationFilter(this, applicationInfoList);
        return applicationFilter;
    }

    public class AppBLViewHolder extends RecyclerView.ViewHolder {

        final CheckBox checkbox;
        final ImageView deviceImageView;
        final TextView deviceAppVersionAuthorLabel;
        final TextView deviceAppNameLabel;

        AppBLViewHolder(View itemView) {
            super(itemView);

            checkbox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
            deviceImageView = (ImageView) itemView.findViewById(R.id.item_image);
            deviceAppVersionAuthorLabel = (TextView) itemView.findViewById(R.id.item_details);
            deviceAppNameLabel = (TextView) itemView.findViewById(R.id.item_name);
        }

    }

    private class ApplicationFilter extends Filter {

        private final AppBlacklistAdapter adapter;
        private final List<ApplicationInfo> originalList;
        private final List<ApplicationInfo> filteredList;

        private ApplicationFilter(AppBlacklistAdapter adapter, List<ApplicationInfo> originalList) {
            super();
            this.originalList = new ArrayList<>(originalList);
            this.filteredList = new ArrayList<>();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence filter) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (filter == null || filter.length() == 0)
                filteredList.addAll(originalList);
            else {
                final String filterPattern = filter.toString().toLowerCase().trim();

                for (ApplicationInfo ai : originalList) {
                    CharSequence name = mPm.getApplicationLabel(ai);
                    if (name.toString().contains(filterPattern) ||
                            (ai.packageName.contains(filterPattern))) {
                        filteredList.add(ai);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.applicationInfoList.clear();
            adapter.applicationInfoList.addAll((List<ApplicationInfo>) filterResults.values);
            adapter.notifyDataSetChanged();
        }
    }

}
