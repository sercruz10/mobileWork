/*  Copyright (C) 2015-2018 0nse, Andreas Shimokawa, Carsten Pfeiffer,
    Daniele Gobbetti

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
package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmount;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityAmounts;
import nodomain.freeyourgadget.gadgetbridge.model.ActivityUser;
import pm.seniorhelp.R;

public class WeekStepsChartFragment extends AbstractWeekChartFragment {
    @Override
    public String getTitle() {
        return getString(R.string.weekstepschart_steps_a_week);
    }

    @Override
    String getPieDescription(int targetValue) {
        return getString(R.string.weeksteps_today_steps_description, String.valueOf(targetValue));
    }

    @Override
    int getGoal() {
        return GBApplication.getPrefs().getInt(ActivityUser.PREF_USER_STEPS_GOAL, 10000);
    }

    @Override
    int getOffsetHours() {
        return 0;
    }

    @Override
    float[] getTotalsForActivityAmounts(ActivityAmounts activityAmounts) {
        int totalSteps = 0;
        for (ActivityAmount amount : activityAmounts.getAmounts()) {
            totalSteps += amount.getTotalSteps();
            amount.getTotalSteps();
        }
        return new float[]{totalSteps};
    }

    @Override
    protected String formatPieValue(int value) {
        return String.valueOf(value);
    }

    @Override
    String[] getPieLabels() {
        return new String[]{""};
    }

    @Override
    IValueFormatter getPieValueFormatter() {
        return null;
    }

    @Override
    IValueFormatter getBarValueFormatter() {
        return null;
    }

    @Override
    IAxisValueFormatter getYAxisFormatter() {
        return null;
    }

    @Override
    int[] getColors() {
        return new int[]{akActivity.color};
    }

    @Override
    protected void setupLegend(Chart chart) {
        // no legend here, it is all about the steps here
        chart.getLegend().setEnabled(false);
    }
}
