package com.mahindra.be_lms.fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.activities.VideoViewActivity;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.MostViewedAdapter;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyHistory extends Fragment implements MyCallback {
    public static final String TAG = MyHistory.class.getSimpleName();
    @BindView(R.id.rvMyHistory)
    RecyclerView rvMyHistory;
    @BindView(R.id.tvMyHistoryRecordNotFound)
    TextView tvMyHistoryRecordNotFound;
    private MainActivity mainActivity;
    private DownloadManager downloadManager;
    private int status;
    private String filePath;
    private List<Document> documentList;
    private String download_url;

    public MyHistory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_history, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        init();
        // Inflate the layout for this fragment
        return view;
    }

    private void init() {
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        documentList = new DBHelper().getHistoryDocumentList();
        L.l(TAG, "List SIZE: " + documentList.toString());
        // List<MostView> mostViewList = DataProvider.getDummyMostViewList();
        //L.l("Mostviewlist size: "+ mostViewList.size());
        rvMyHistory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvMyHistory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        rvMyHistory.setHasFixedSize(true);
        if (documentList.size() > 0) {
//            MostViewedAdapter adapter = new MostViewedAdapter(getActivity(), documentList);
//            rvMyHistory.setAdapter(adapter);
//            adapter.setMyCallback(this);
        } else {
            tvMyHistoryRecordNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void myCallback(final int position) {
        try {
            String docName = null;
            JSONObject jsonObject = new JSONObject(documentList.get(position).getDocumentName());
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                final String key = iter.next();
                L.l("Document Title: " + key);
                try {
                    final Object value = jsonObject.get(key);
                    L.l("Document Name: " + value);
                    docName = value.toString();
                    download_url = Constants.DOC_TREE_DOWNLOAD_URL;
                    download_url = download_url + docName;
                    L.l(TAG, "DOC DOWNLOAD URL: " + download_url);
                    String extension = CommonFunctions.getExtension(value.toString());
                    L.l("File Extension : " + extension);
                    if (extension.equalsIgnoreCase("png")
                            || extension.equalsIgnoreCase("jpg")
                            || extension.equalsIgnoreCase("JPE")
                            || extension.equalsIgnoreCase("IMG")
                            || extension.equalsIgnoreCase("GIF")
                            || extension.equalsIgnoreCase("PSD")
                            || extension.equalsIgnoreCase("jpeg")) {
                        startActivity(new Intent(getActivity(), ProfilePictureActivity.class)
                                .putExtra("image_url", download_url)
                                .putExtra("title", key)
                                .putExtra("notProfilePhoto", true));
                    } else if (extension.equalsIgnoreCase("mp4")
                            || extension.equalsIgnoreCase("mpeg")
                            || extension.equalsIgnoreCase("flv")
                            || extension.equalsIgnoreCase("avi")
                            || extension.equalsIgnoreCase("mov")
                            || extension.equalsIgnoreCase("mpg")
                            || extension.equalsIgnoreCase("wmv") || extension.equalsIgnoreCase("3gp") || extension.equalsIgnoreCase("swf")) {
                        if (L.isNetworkAvailable(getActivity())) {
                            Intent videoview_intent = new Intent(getActivity(), VideoViewActivity.class);
                            videoview_intent.putExtra(getString(R.string.testpaper_url_parcelable_tag), download_url);
                            startActivity(videoview_intent);
                        } else {
                            //L.t(getString(R.string.err_network_connection));
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                    .setContentText(getString(R.string.err_network_connection))
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
//ChangeMobileNumberActivity.this.finish();
                                        }
                                    }).show();
                        }
                    } else if (extension.equalsIgnoreCase("html")) {
                        //  bundle.putString("testpaper_url",""+url);
                        if (L.isNetworkAvailable(getActivity())) {
                            HtmlViewFragment htmlViewFragment = HtmlViewFragment.newInstance(download_url);
                            mainActivity.replaceFrgament(htmlViewFragment);
                        } else {
                            //L.t(getString(R.string.err_network_connection));
                            new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                    .setContentText(getString(R.string.err_network_connection))
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                        @Override
                                        public void onClick(PKBDialog customDialog) {
                                            customDialog.dismiss();
//ChangeMobileNumberActivity.this.finish();
                                        }
                                    }).show();
                        }
                    } else {
                        if (CommonFunctions.checkDocumentAllreaddyExist(docName)) {
                            L.l(TAG, "File exists");
                            File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), docName);
                            new CommonFunctions().openFile(getActivity(), file.getPath());
                        } else {
                            if (documentList.get(position).getDocumentReferencedID() != 0L) {
                                DownloadManager.Query query = new DownloadManager.Query();
                                query.setFilterById(documentList.get(position).getDocumentReferencedID());
                                Cursor c = downloadManager.query(query);
                                if (c.moveToFirst()) {
                                    status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                        filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                        L.l("Receiver filename: " + filePath);
                                        String filename = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length());
                                        L.l("Receiver filename : " + filename);
                                    }
                                }
                                c.close();
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    new CommonFunctions().openFile(getActivity(), filePath);
                                } else {
                                    new CommonFunctions().CheckStatus(downloadManager, documentList.get(position).getDocumentReferencedID());
                                }
                            } else {
                                final String filename = docName;
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("To view " + key + " please download it.")
                                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (L.isNetworkAvailable(getActivity())) {
                                                    if (Utility.checkExternalStoragePermission(getActivity())) {
                                                        L.l(TAG, "Document url " + download_url);
                                                        Uri uri = Uri.parse(download_url);
                                                        //String filename = uri.getLastPathSegment();
                                                        String filename = value.toString();
                                                        L.l(TAG, "file to download " + filename);
                                                        //L.l(TAG, "Reference ID: " + documentTreeMastersList.get(position).getDocReferencedID());
                                                        long referencedID = new CommonFunctions().DownloadData(getActivity(), downloadManager, uri, filename);
                                                        L.l(TAG, "Reference ID: " + referencedID);
                                                        documentList.get(position).setDocumentReferencedID(referencedID);
                                                    } else {
                                                        Utility.callExternalStoragePermission(getActivity());
                                                    }
                                                } else {
                                                    new PKBDialog(getActivity(), PKBDialog.WARNING_TYPE)
                                                            .setContentText(getString(R.string.err_network_connection))
                                                            .setConfirmText("OK")
                                                            .setConfirmClickListener(new PKBDialog.OnPKBDialogClickListner() {
                                                                @Override
                                                                public void onClick(PKBDialog customDialog) {
                                                                    customDialog.dismiss();
                                                                }
                                                            }).show();
                                                }

                                            }
                                        }).setNegativeButton(getString(R.string.dialog_cancel), null)
                                        .show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    // Something went wrong!
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
