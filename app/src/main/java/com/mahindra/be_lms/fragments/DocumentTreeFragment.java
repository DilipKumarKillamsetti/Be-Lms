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
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.ProfilePictureActivity;
import com.mahindra.be_lms.activities.VideoViewActivity;
import com.mahindra.be_lms.adapter.DividerItemDecoration;
import com.mahindra.be_lms.adapter.DocumentTreeAdapter;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.db.DocumentTree;
import com.mahindra.be_lms.db.Model;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.interfaces.MyCallback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.PKBDialog;
import com.mahindra.be_lms.model.DocumentTreeMaster;
import com.mahindra.be_lms.util.CommonFunctions;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.dao.query.Query;

/**
 * A simple {@link Fragment} subclass.
 * Modified by Chaitali Chavan on 11/11/16.
 * Change in DocumentTreeMaster value assignment
 */
public class DocumentTreeFragment extends Fragment implements MyCallback {

    public static final String TAG = DocumentTreeFragment.class.getSimpleName();
    private static String parentID = "0";
    @BindView(R.id.rvDocumentTreeDocumentTreeList)
    RecyclerView rvDocumentTreeDocumentTreeList;
    @BindView(R.id.llRecordNotFound)
    LinearLayout llRecordNotFound;
    private List<DocumentTreeMaster> documentTreeMastersList;
    private List<DocumentTree> documentTreeList = new ArrayList<>();
    private MainActivity mainActivity;
    private Model model;
    private String product_id;
    private Query<DocumentTree> treeQuery;
    private DownloadManager downloadManager;
    private int status;
    private String filePath;
    private String download_url = "";
    private List<String> userRoleList = new ArrayList<>();

    public DocumentTreeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_document_tree, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mainActivity.setDrawerEnabled(false);
        L.l(TAG, "init");
        Bundle bundle = getArguments();
        if (bundle != null) {
            model = bundle.getParcelable(getString(R.string.model_parcelable_tag));
            product_id = bundle.getString("product_id");
            downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            parentID = bundle.getString("parent_id", "0");
            L.l(TAG, "PRODUCT ID: " + product_id + " MODEL ID: " + model.getModelID() + " PARENT ID: " + parentID);
            treeQuery = new DBHelper().getDocumentTree(product_id, model.getModelID(), parentID);
            User user = new DBHelper().getUser();
            if (!TextUtils.isEmpty(user.getProfiles()) && !user.getProfiles().equalsIgnoreCase("null")) {
                String role = user.getProfiles();
                userRoleList = new ArrayList<>();
                userRoleList = new ArrayList<String>(Arrays.asList(role.split(",")));
                for (int i = 0; i < userRoleList.size(); i++) {
                    L.l(TAG, "User Role List: " + userRoleList.get(i));
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!MyApplication.mySharedPreference.checkUserLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            } else {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadDocument();
                    }
                });

                mainActivity.getSupportActionBar().show();
                mainActivity.getSupportActionBar().setTitle("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.footertext)
    public void OnfooterTextClick() {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFrgament(new HomeFragment());
    }

    //Fri Nov 11 16:14:36 GMT+05:30 2016
    private void loadDocument() {
        documentTreeList = treeQuery.list();
        // L.l(TAG, "Doucmnet List Size: " + documentTreeList.size()+"TREE: "+documentTreeList.toString());
        documentTreeMastersList = new ArrayList<>();
        Collections.sort(documentTreeList, new Comparator<DocumentTree>() {
            @Override
            public int compare(DocumentTree tree1, DocumentTree tree2) {
                return Integer.parseInt(tree1.getDocumentTreeSequence()) > Integer.parseInt(tree2.getDocumentTreeSequence()) ? 1 : Integer.parseInt(tree1.getDocumentTreeSequence()) < Integer.parseInt(tree2.getDocumentTreeSequence()) ? -1 : 0;
            }
        });
        for (DocumentTree tree : documentTreeList) {
            if (!tree.getDocumentTreeItem().equals("")) {
                documentTreeMastersList.add(new DocumentTreeMaster(tree.getDocumentTreeID(), tree.getDocumentTreeItem(), "folder", 0L));
            } else {
                DocumentTree documentTree = new DBHelper().getDocument(parentID);
                L.l(TAG, "DOCUMENT FIND " + documentTree.toString());
                List<Document> documentList = new DBHelper().getDocumentList(tree.getDocumentTreeID());
                if (!documentList.isEmpty()) {
                    try {
                        for (Document document : documentList) {
                            L.l(TAG, "Document: " + document.toString());
                            checkRole(document);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        L.l(TAG, "EXCEPTION in fetching document: " + e.getMessage());
                    }
                }
            }
        }
        if (!parentID.equalsIgnoreCase("0")) {
            L.l(TAG, "PARENT ID: " + parentID);
            if (new DBHelper().getDocumentIsAvailable(parentID)) {
                DocumentTree documentTree = new DBHelper().getDocument(parentID);
                L.l(TAG, "DOCUMENT FIND " + documentTree.toString());
                List<Document> documentList = new DBHelper().getDocumentList(documentTree.getDocumentTreeID());
                if (!documentList.isEmpty()) {
                    try {
                        for (Document document : documentList) {
                            L.l(TAG, "Document: " + document.toString());
                            checkRole(document);
                            // documentTreeMastersList.add(new DocumentTreeMaster(document.getId().toString(), document.getDocumentName(), "doc", document.getDocumentReferencedID()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        L.l(TAG, "EXCEPTION in fetching document: " + e.getMessage());
                    }
                }
            } else {
                boolean isselfdocavail = new DBHelper().getSelfDocument(parentID);
                L.l(TAG, "Self doc available: " + isselfdocavail);
                DocumentTree documentTree = new DBHelper().getSelfDocumentTree(parentID);
                L.l(TAG, "Self DOCUMENT FIND " + documentTree.toString());
                if (!documentTree.getParentID().equalsIgnoreCase("0")) {
                    List<Document> documentList = new DBHelper().getDocumentList(documentTree.getDocumentTreeID());
                    if (!documentList.isEmpty()) {
                        try {
                            for (Document document : documentList) {
                                L.l(TAG, "Self Document: " + document.toString());
                                checkRole(document);
                                //documentTreeMastersList.add(new DocumentTreeMaster(document.getId().toString(), document.getDocumentName(), "doc", document.getDocumentReferencedID()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            L.l(TAG, "Self DOC EXCEPTION in fetching document: " + e.getMessage());
                        }
                    }
                } else {
                    List<Document> documentList = new DBHelper().getDocumentList(documentTree.getDocumentTreeID());
                    if (!documentList.isEmpty()) {
                        try {
                            for (Document document : documentList) {
                                L.l(TAG, "Self Document PARENT ID 0: " + document.toString());
                                checkRole(document);
                                // documentTreeMastersList.add(new DocumentTreeMaster(document.getId().toString(), document.getDocumentName(), "doc", document.getDocumentReferencedID()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            L.l(TAG, "Self DOC EXCEPTION in fetching document: " + e.getMessage());
                        }
                    }
                }
            }
        }
        L.l(TAG, "Tree List size: " + documentTreeMastersList.size());
        if (documentTreeMastersList.size() > 0) {
            DocumentTreeAdapter adapter = new DocumentTreeAdapter(getActivity(), documentTreeMastersList);
            rvDocumentTreeDocumentTreeList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvDocumentTreeDocumentTreeList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            rvDocumentTreeDocumentTreeList.setAdapter(adapter);
            adapter.setMyCallback(this);
        } else {
            llRecordNotFound.setVisibility(View.VISIBLE);
            rvDocumentTreeDocumentTreeList.setVisibility(View.GONE);
        }
    }

    private void checkRole(Document document) {
        L.l(TAG, "Document Role List: " + document.getDocumentRole());
        List<String> documentRole = new ArrayList<>();
        documentRole = new ArrayList<String>(Arrays.asList(document.getDocumentRole().split(",")));
        L.l(TAG, "Document Role List: " + documentRole);
        if (!userRoleList.isEmpty() && !documentRole.isEmpty()) {
            L.l(TAG, "ListSize: " + "User: " + userRoleList.size() + "Doc: " + documentRole.size());
            if (userRoleList.size() > documentRole.size()) {
                L.l(TAG, "User Role Loop: ");
                for (int i = 0; i < userRoleList.size(); i++) {
                    for (String docRole : documentRole) {
                        L.l(TAG, "User Role: " + userRoleList.get(i));
                        if (userRoleList.get(i).compareTo(docRole) == 0) {
                            L.l(TAG, "User Role match to doc: " + docRole);
                            documentTreeMastersList.add(new DocumentTreeMaster(document.getId().toString(), document.getDocumentName(), "doc", document.getDocumentReferencedID()));
                        } else {
                            L.l(TAG, "User Role not match to doc: " + docRole);
                        }
                    }
                }
            } else {
                for (int i = 0; i < documentRole.size(); i++) {
                    L.l(TAG, "Doc Role Loop: ");
                    for (String userRole : userRoleList) {
                        L.l(TAG, "Doc Role: " + documentRole.get(i));
                        if (documentRole.get(i).compareTo(userRole) == 0) {
                            L.l(TAG, "Doc Role match to user: " + userRole);
                            documentTreeMastersList.add(new DocumentTreeMaster(document.getId().toString(), document.getDocumentName(), "doc", document.getDocumentReferencedID()));
                        } else {
                            L.l(TAG, "Doc Role not match to user: " + userRole);
                        }
                    }
                }
            }

        }
    }

    @Override
    public void myCallback(final int position) {
        if (documentTreeMastersList.get(position).getType().equalsIgnoreCase("doc")) {
            String docName = "";
            String extension = null;
            try {
                JSONObject jsonObject = new JSONObject(documentTreeMastersList.get(position).getName());
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    final String key = iter.next();
                    L.l("Document Title: " + key);
                    try {
                        Object value = jsonObject.get(key);
                        docName = value.toString();
                        L.l(TAG, "doc Name: " + docName);
                        extension = CommonFunctions.getExtension(value.toString());
                        L.l(TAG, "File Extension : " + extension);
                        download_url = Constants.DOC_TREE_DOWNLOAD_URL;
                        download_url = download_url + docName;
                        L.l(TAG, "DOC DOWNLOAD URL: " + download_url);
                        if (extension.equalsIgnoreCase("png")
                                || extension.equalsIgnoreCase("jpg")
                                || extension.equalsIgnoreCase("JPE")
                                || extension.equalsIgnoreCase("IMG")
                                || extension.equalsIgnoreCase("GIF")
                                || extension.equalsIgnoreCase("PSD")
                                || extension.equalsIgnoreCase("jpeg")) {
                            Document document = new DBHelper().fetchDocumentByID(documentTreeMastersList.get(position).getDocID());
                            document.setDocumentHitDate(new Date());
                            document.setDocumentHitCount(document.getDocumentHitCount() + 1);
                            new DBHelper().updateDocument(document);
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
                                Document document = new DBHelper().fetchDocumentByID(documentTreeMastersList.get(position).getDocID());
                                document.setDocumentHitDate(new Date());
                                document.setDocumentHitCount(document.getDocumentHitCount() + 1);
                                new DBHelper().updateDocument(document);
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
                                Document document = new DBHelper().fetchDocumentByID(documentTreeMastersList.get(position).getDocID());
                                document.setDocumentHitDate(new Date());
                                document.setDocumentHitCount(document.getDocumentHitCount() + 1);
                                new DBHelper().updateDocument(document);
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
                                File file = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()), docName);
                                new CommonFunctions().openFile(getActivity(), file.getPath());
                                Document document = new DBHelper().fetchDocumentByID(documentTreeMastersList.get(position).getDocID());
                                L.l(TAG, "Document to update: " + document.toString());
                                document.setDocumentHitDate(new Date());
                                document.setDocumentHitCount(document.getDocumentHitCount() + 1);
                                new DBHelper().updateDocument(document);
                            } else {
                                if (documentTreeMastersList.get(position).getDocReferencedID() != 0L) {
                                    DownloadManager.Query query = new DownloadManager.Query();
                                    query.setFilterById(documentTreeMastersList.get(position).getDocReferencedID());
                                    Cursor c = downloadManager.query(query);
                                    if (c.moveToFirst()) {
                                        status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                            filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                            L.l(TAG, "Receiver filename: " + filePath);
                                            L.l(TAG, "Receiver filename extension: " + extension);
                                        }
                                    }
                                    c.close();
                                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                        new CommonFunctions().openFile(getActivity(), filePath);
                                        Document document = new DBHelper().fetchDocumentByID(documentTreeMastersList.get(position).getDocID());
                                        document.setDocumentHitDate(new Date());
                                        document.setDocumentHitCount(document.getDocumentHitCount() + 1);
                                        new DBHelper().updateDocument(document);
                                    } else {
                                        new CommonFunctions().CheckStatus(downloadManager, documentTreeMastersList.get(position).getDocReferencedID());
                                    }
                                } else {
                                    if (L.isNetworkAvailable(getActivity())) {
                                        final String filename = docName;
                                        new AlertDialog.Builder(getActivity())
                                                .setMessage("To view " + key + " please download it.")
                                                .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        if (Utility.checkExternalStoragePermission(getActivity())) {
                                                            L.l(TAG, "Document url " + download_url);
                                                            Uri uri = Uri.parse(download_url);
                                                            //  String filename = uri.getLastPathSegment();

                                                            L.l(TAG, "file to download " + filename);
                                                            L.l(TAG, "Reference ID: " + documentTreeMastersList.get(position).getDocReferencedID());
                                                            long referencedID = new CommonFunctions().DownloadData(getActivity(), downloadManager, uri, filename);
                                                            L.l(TAG, "Reference ID: " + referencedID);
                                                            documentTreeMastersList.get(position).setDocReferencedID(referencedID);
                                                            Document document = new DBHelper().fetchDocumentByID(documentTreeMastersList.get(position).getDocID());
                                                            document.setDocumentReferencedID(referencedID);
                                                            document.setDocumentHitDate(new Date());
                                                            document.setDocumentHitCount(document.getDocumentHitCount() + 1);
                                                            new DBHelper().updateDocument(document);
                                                        } else {
                                                            Utility.callExternalStoragePermission(getActivity());
                                                        }

                                                    }
                                                }).setNegativeButton(getString(R.string.dialog_cancel), null)
                                                .show();
                                    } else {
                                        //L.t(getString(R.string.err_network_connection));
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

        } else {
            L.l(TAG, "OLD TREE : " + documentTreeList.get(position).toString());
            DocumentTreeFragment documentTreeFragment = new DocumentTreeFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.model_parcelable_tag), model);
            bundle.putString("product_id", product_id);
            bundle.putString("parent_id", documentTreeList.get(position).getDocumentTreeID());
            documentTreeFragment.setArguments(bundle);
            mainActivity.replaceFrgament(documentTreeFragment);
        }
    }
}
