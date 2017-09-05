package mashup.tecemer.com.busito.adaptador;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 20/05/2017.
 */

public class SectionAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> titlesFragments = new ArrayList<>();

    public SectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        titlesFragments.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titlesFragments.get(position);
    }
}