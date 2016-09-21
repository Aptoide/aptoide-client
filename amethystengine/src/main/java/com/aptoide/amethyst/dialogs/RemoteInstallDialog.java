package com.aptoide.amethyst.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapter.ReceiverDeviceAdapter;
import com.aptoide.amethyst.remoteinstall.ReceiverDevice;
import com.aptoide.amethyst.remoteinstall.RemoteInstallationSenderListener;
import com.aptoide.amethyst.remoteinstall.RemoteInstallationSenderManager;

/**
 * Created by franciscoaleixo on 18-08-2016.
 */
public class RemoteInstallDialog extends DialogFragment implements RemoteInstallationSenderListener {
    private final static String APP_ID_TAG = "appid";
    private enum Error {NO_NETWORK, NO_DEVICES_FOUND}

    private ProgressBar pBar;
    private ListView listView;
    private LinearLayout errorLayout;
    private TextView errorText;
    private LinearLayout listLayout;
    private ImageButton refreshBtn;
    private TextView aptoideTVInstalledText;

    private RemoteInstallationSenderManager sManager;
    private ReceiverDeviceAdapter adapter;
    private String app;

    public static RemoteInstallDialog newInstance(long appid) {
        RemoteInstallDialog frag = new RemoteInstallDialog();
        Bundle args = new Bundle();
        args.putLong(APP_ID_TAG, appid);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        sManager.stopDiscoveringAptoideTVServices();
        super.onDismiss(dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = "" + getArguments().getLong(APP_ID_TAG);
        sManager = new RemoteInstallationSenderManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Needed for rounded edges
        InsetDrawable background = (InsetDrawable) getDialog().getWindow().getDecorView().getBackground();
        background.setAlpha(0);

        final View v = inflater.inflate(R.layout.dialog_remote_install, container, false);
        pBar = (ProgressBar) v.findViewById(R.id.progressBar);
        errorLayout = (LinearLayout) v.findViewById(R.id.errorLayout);
        errorText = (TextView) v.findViewById(R.id.no_connection_text);
        listLayout = (LinearLayout) v.findViewById(R.id.listLayout);
        Button helpBtn = (Button) v.findViewById(R.id.help_btn);
        aptoideTVInstalledText = (TextView) v.findViewById(R.id.aptoidetv_installed_text);
        refreshBtn = (ImageButton) v.findViewById(R.id.refreshButton);

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getContext().getString(R.string.remote_install_help_url)));
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryClick(v);
            }
        });
        if(adapter == null){
            listView = (ListView) v.findViewById(R.id.listView);
            listView.setEmptyView(v.findViewById(R.id.emptyListLayout));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                    ReceiverDevice device = (ReceiverDevice) adapter.getItemAtPosition(position);
                    if (sManager != null)
                        sManager.sendAppId(device, app);

                }
            });
            populateDeviceList();
            onRetryClick(container);
        }
        return v;
    }

    private void populateDeviceList() {
        List<ReceiverDevice> devices = new ArrayList<>();
        adapter = new ReceiverDeviceAdapter(getActivity().getApplicationContext(), R.layout.row_remote_install, devices);
        adapter.setAppId(app);
        listView.setAdapter(adapter);
    }

    public void onRetryClick(View view){
        populateDeviceList();
        sManager.discoverAptoideTVServices(this);
    }

    private void showErrorLayout(Error error){
        pBar.setVisibility(View.GONE);
        if(error == Error.NO_DEVICES_FOUND){
            aptoideTVInstalledText.setText(R.string.remote_install_notinstallated);
            errorText.setText(R.string.remote_install_nodevices);
        } else if ( error == Error.NO_NETWORK){
            aptoideTVInstalledText.setText(R.string.remote_install_nowifi_tip);
            errorText.setText(R.string.remote_install_nowifi);
        }

        listLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDiscoveryStarted() {
        refreshBtn.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);

        errorLayout.setVisibility(View.GONE);
        listLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDiscoveryStopped() {
        pBar.setVisibility(View.GONE);
        refreshBtn.setVisibility(View.VISIBLE);
        if(adapter.getCount() == 0){
            showErrorLayout(Error.NO_DEVICES_FOUND);
        }
    }

    @Override
    public void onAptoideTVServiceLost(ReceiverDevice device) {
        adapter.remove(device);
    }

    @Override
    public void onAptoideTVServiceFound(ReceiverDevice device) {
        adapter.add(device);
    }

    @Override
    public void onAppSendSuccess() {
        Toast.makeText(getContext(), R.string.remote_install_success, Toast.LENGTH_LONG).show();
        dismiss();
    }

    @Override
    public void onNoNetworkAccess() {
        showErrorLayout(Error.NO_NETWORK);
    }

    @Override
    public void onAppSendUnsuccess() {
        Toast.makeText(getContext(), R.string.remote_install_fail, Toast.LENGTH_LONG).show();
        dismiss();
    }
}
