package com.hover.tester;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hover.sdk.onboarding.HoverIntegrationActivity;
import com.hover.sdk.operators.Permission;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements ActionListFragment.OnListFragmentInteractionListener {
    public final static String TAG = "MainActivity";
    private final int INTEGRATE_REQUEST = 111;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        setUpToolbar();
        if (findViewById(R.id.detail_container) != null)
            mTwoPane = true;
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void addIntegration(View view) {
        Intent integrationIntent = new Intent(this, HoverIntegrationActivity.class);
        integrationIntent.putExtra(HoverIntegrationActivity.SERVICE_IDS, new int[] { 1, 4 });
        integrationIntent.putExtra(HoverIntegrationActivity.PERM_LEVEL, Permission.NORMAL);
        startActivityForResult(integrationIntent, INTEGRATE_REQUEST);
    }

    public void setToolbarTitle(OperatorService opService) {
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(opService.mName);
        ((Toolbar) findViewById(R.id.toolbar)).setSubtitle(getString(R.string.country, opService.mCountryIso, opService.mCurrencyIso));
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTEGRATE_REQUEST && resultCode == RESULT_OK)
            onIntegrateSuccess(data);
        else if (requestCode == INTEGRATE_REQUEST)
            Toast.makeText(this, data.getStringExtra("error"), Toast.LENGTH_SHORT).show();
//        else if (resultCode == RESULT_CANCELED) {
//            Utils.saveActionResult(serviceId, (String) v.getTag(), false, this);
//            setResultInView(v, serviceId, (String) v.getTag());
//        } else {
//            Utils.saveActionResult(serviceId, (String) v.getTag(), false, this);
//            setIcon(v, R.drawable.circle_unknown);
//        }
    }

    public void onIntegrateSuccess(Intent data) {
        ActionListFragment frag = (ActionListFragment) getSupportFragmentManager().findFragmentById(R.id.action_list_fragment);
        frag.update(new OperatorService(data, this), frag.getView());
    }

    @Override
    public void onListFragmentInteraction(OperatorAction act) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(OperatorAction.SLUG, act.mSlug);
            arguments.putInt(OperatorService.ID, act.mOpId);
            ActionDetailFragment fragment = new ActionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ActionDetailActivity.class);
            intent.putExtra(OperatorAction.SLUG, act.mSlug);
            intent.putExtra(OperatorService.ID, act.mOpId);
            startActivity(intent);
        }
    }
}
