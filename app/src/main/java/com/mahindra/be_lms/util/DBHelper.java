package com.mahindra.be_lms.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.db.Category;
import com.mahindra.be_lms.db.Company;
import com.mahindra.be_lms.db.Course;
import com.mahindra.be_lms.db.DaoSession;
import com.mahindra.be_lms.db.Designation;
import com.mahindra.be_lms.db.Document;
import com.mahindra.be_lms.db.DocumentDao;
import com.mahindra.be_lms.db.DocumentTree;
import com.mahindra.be_lms.db.DocumentTreeDao;
import com.mahindra.be_lms.db.Location;
import com.mahindra.be_lms.db.MenuRights;
import com.mahindra.be_lms.db.Model;
import com.mahindra.be_lms.db.ModelDao;
import com.mahindra.be_lms.db.Notifications;
import com.mahindra.be_lms.db.NotificationsDao;
import com.mahindra.be_lms.db.Product;
import com.mahindra.be_lms.db.ProductDao;
import com.mahindra.be_lms.db.Profile;
import com.mahindra.be_lms.db.ProfileDao;
import com.mahindra.be_lms.db.Qualification;
import com.mahindra.be_lms.db.Queries;
import com.mahindra.be_lms.db.QueriesDao;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.db.QueryResponseDao;
import com.mahindra.be_lms.db.SubCategory;
import com.mahindra.be_lms.db.SubCategoryDao;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.SearchModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by Chaitali on 10/21/16.
 */

public class DBHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    public void sync_Master(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getJSONArray("orgnization").length() > 0) {
            JSONArray companyArray = jsonObject.getJSONArray("orgnization");
            List<Company> companies = new ArrayList<>();
            List<Company> db_compnayList = new ArrayList<>();
            db_compnayList = getCompanyList();
            if (db_compnayList.size() > 0) {
                L.l(TAG, "Company available:" + db_compnayList.toString());
                for (int i = 0; i < companyArray.length(); i++) {
                    L.l("COMPANY " + companyArray.getJSONObject(i).toString());
                    Company comapny = new Gson().fromJson(String.valueOf(companyArray.getJSONObject(i)), Company.class);
                    L.l("Company: " + comapny);
                    companies.add(comapny);
                }
                boolean isPresent = false;
                for (int i = 0; i < companies.size(); i++) {
                    L.l(TAG, "In Update/Insert Company:");
                    isPresent = false;
                    for (Company company : db_compnayList) {
                        if (companies.get(i).getComapnayID().equals(company.getComapnayID())) {
                            isPresent = true;
                            L.l(TAG, "In Update Company State:" + isPresent);
                        }
                    }
                    L.l(TAG, "Out of for each Company State:" + isPresent);
                    if (isPresent) {
                        getAppDaoSession().getCompanyDao().updateInTx(companies.get(i));
                        L.l(TAG, "In Update Company:");
                    } else {
                        getAppDaoSession().getCompanyDao().insertInTx(companies.get(i));
                        L.l(TAG, "In Insert Company:");
                    }
                }
            } else {
                L.l(TAG, "COMPANY not available:");
                for (int i = 0; i < companyArray.length(); i++) {
                    L.l("COMPANY " + companyArray.getJSONObject(i).toString());
                    Company comapny = new Gson().fromJson(String.valueOf(companyArray.getJSONObject(i)), Company.class);
                    L.l("Company: " + comapny);
                    companies.add(comapny);
                }
                getAppDaoSession().getCompanyDao().insertInTx(companies);
            }
        }
        if (jsonObject.getJSONArray("location").length() > 0) {
            JSONArray locationArray = jsonObject.getJSONArray("location");
            List<Location> locations = new ArrayList<>();
            List<Location> db_loLocationList = new ArrayList<>();
            db_loLocationList = getLocaitonList();
            if (db_loLocationList.size() > 0) {
                L.l(TAG, "Locations available:" + db_loLocationList.toString());
                for (int i = 0; i < locationArray.length(); i++) {
                    Location location = new Gson().fromJson(String.valueOf(locationArray.getJSONObject(i)), Location.class);
                    L.l("Location: " + location);
                    locations.add(location);
                }
                boolean isPresent = false;
                for (int i = 0; i < locations.size(); i++) {
                    isPresent = false;
                    L.l(TAG, "In Update/Insert Locations:");
                    for (Location location : db_loLocationList) {
                        if (locations.get(i).getLocationID().equals(location.getLocationID())) {
                            isPresent = true;
                            L.l(TAG, "In Update Locations State:" + isPresent);
                        }
                    }
                    L.l(TAG, "Out of for each Locations State: " + isPresent);
                    if (isPresent) {
                        getAppDaoSession().getLocationDao().updateInTx(locations.get(i));
                        L.l(TAG, "In Update Locations:");
                    } else {
                        getAppDaoSession().getLocationDao().insertInTx(locations.get(i));
                        L.l(TAG, "In Insert Locations:");
                    }
                }
            } else {
                L.l(TAG, "Locations not available:");
                for (int i = 0; i < locationArray.length(); i++) {
                    Location location = new Gson().fromJson(String.valueOf(locationArray.getJSONObject(i)), Location.class);
                    L.l("Location: " + location);
                    locations.add(location);
                }
                getAppDaoSession().getLocationDao().insertInTx(locations);
            }
        }
        if (jsonObject.getJSONArray("designation").length() > 0) {
            JSONArray designationArray = jsonObject.getJSONArray("designation");
            List<Designation> designations = new ArrayList<>();
            List<Designation> db_DesignationList = new ArrayList<>();
            db_DesignationList = getDesignationList();
            if (db_DesignationList.size() > 0) {
                L.l(TAG, "Designations available: ");
                for (int i = 0; i < designationArray.length(); i++) {
                    Designation designation = new Gson().fromJson(String.valueOf(designationArray.getJSONObject(i)), Designation.class);
                    L.l("Designation: " + designation);
                    designations.add(designation);
                }
                boolean isPresent = false;
                for (int i = 0; i < designations.size(); i++) {
                    isPresent = false;
                    L.l(TAG, "In Update/Insert Designation:");
                    for (Designation designation : db_DesignationList) {
                        if (designations.get(i).getDesigID().equals(designation.getDesigID())) {
                            isPresent = true;
                            L.l(TAG, "In Update Designation STATE:" + isPresent);
                        }
                    }
                    L.l(TAG, "Out of for each Designation State:" + isPresent);
                    if (isPresent) {
                        getAppDaoSession().getDesignationDao().updateInTx(designations.get(i));
                        L.l(TAG, "In Update Designation:");
                    } else {
                        getAppDaoSession().getDesignationDao().insertInTx(designations.get(i));
                        L.l(TAG, "In Insert Designation:");
                    }
                }
            } else {
                L.l(TAG, "Designations available: ");
                for (int i = 0; i < designationArray.length(); i++) {
                    Designation designation = new Gson().fromJson(String.valueOf(designationArray.getJSONObject(i)), Designation.class);
                    L.l("Designation: " + designation);
                    designations.add(designation);
                }
                getAppDaoSession().getDesignationDao().insertInTx(designations);
            }
        }
      /*  if (jsonObject.getJSONArray("profile").length() > 0) {
            JSONArray profileArray = jsonObject.getJSONArray("profile");
            List<Profile> profiles = new ArrayList<>();
            List<Profile> db_ProfileList = new ArrayList<>();
            db_ProfileList = getProfileList();
            if (db_ProfileList.size() > 0) {
                L.l(TAG, "Profile available: ");
                for (int i = 0; i < profileArray.length(); i++) {
                    Profile profile = new Gson().fromJson(String.valueOf(profileArray.getJSONObject(i)), Profile.class);
                    profile.setProfile_approval(false);
                    profile.setProfile_requested(false);
                    L.l("profile: " + profile);
                    profiles.add(profile);
                }
                boolean isPresent = false;
                for (int i = 0; i < profiles.size(); i++) {
                    isPresent = false;
                    L.l(TAG, "In Update/Insert Profile:");
                    for (Profile profile : db_ProfileList) {
                        if (profiles.get(i).getProfileID().equals(profile.getProfileID())) {
                            isPresent = true;
                            L.l(TAG, "In Update Profile STATE:" + isPresent);
                        }
                    }
                    L.l(TAG, "Out of each Profile STATE:" + isPresent);
                    if (isPresent) {
                        getAppDaoSession().getProfileDao().updateInTx(profiles.get(i));
                        L.l(TAG, "In Update Profile:");
                    } else {
                        getAppDaoSession().getProfileDao().insertInTx(profiles);
                        L.l(TAG, "In Insert Profile:");
                    }
                }
            } else {
                L.l(TAG, "Profile not available: ");
                *//*for (int i = 0; i < profileArray.length(); i++) {
                    Profile profile = new Gson().fromJson(String.valueOf(profileArray.getJSONObject(i)), Profile.class);
                    profile.setProfile_approval(false);
                    profile.setProfile_requested(false);
                    L.l("profile: " + profile);
                    profiles.add(profile);
                }
                getAppDaoSession().getProfileDao().insertInTx(profiles);*//*
            }
        }*/
        Log.d(TAG, "sync_Master: PRODUCT LIST BEFORE: " + getAppDaoSession().getProductDao().loadAll().size());
        if (jsonObject.getJSONArray("product").length() > 0) {
            Log.d(TAG, "sync_Master: PRODUCT LIST SIZE: " + jsonObject.getJSONArray("product").length());
            JSONArray productArray = jsonObject.getJSONArray("product");
            List<Product> products = new ArrayList<>();
            List<Product> db_ProductList = new ArrayList<>();
            db_ProductList = getAppDaoSession().getProductDao().loadAll();
            for (int i = 0; i < productArray.length(); i++) {
                Product product = new Gson().fromJson(String.valueOf(productArray.getJSONObject(i)), Product.class);
                L.l("Product: " + product);
                products.add(product);
            }
            if (db_ProductList.size() > 0) {
                L.l(TAG, "Product available: ");
                boolean isPresent = false;
                for (int i = 0; i < products.size(); i++) {
                    isPresent = false;
                    L.l(TAG, "In Update/Insert Product:");
                    for (Product product : db_ProductList) {
                        if (products.get(i).getProductID().equals(product.getProductID())) {
                            isPresent = true;
                            L.l(TAG, "In Update Product state:" + isPresent);
                        }
                    }
                    L.l(TAG, "Out of for each Product state:" + isPresent);
                    if (isPresent) {
                        getAppDaoSession().getProductDao().updateInTx(products.get(i));
                        L.l(TAG, "In Update Product:");
                    } else {
                        getAppDaoSession().getProductDao().insertInTx(products.get(i));
                        L.l(TAG, "In Insert Product:");
                    }
                }
            } else {
                L.l(TAG, "Product not available: ");
                getAppDaoSession().getProductDao().insertInTx(products);
            }
        }
        Log.d(TAG, "sync_Master: PRODUCT LIST AFTER: " + getAppDaoSession().getProductDao().loadAll().size());
        if (jsonObject.getJSONArray("model").length() > 0) {
            JSONArray modelArray = jsonObject.getJSONArray("model");
            List<Model> models = new ArrayList<>();
            List<Model> db_modelList = new ArrayList<>();
            db_modelList = getModelList();
            for (int i = 0; i < modelArray.length(); i++) {
                Model model = new Gson().fromJson(String.valueOf(modelArray.getJSONObject(i)), Model.class);
                L.l("Model: " + model);
                models.add(model);
            }
            if (db_modelList.size() > 0) {
                boolean isPresent = false;
                L.l(TAG, "Model available: ");
                for (int i = 0; i < models.size(); i++) {
                    isPresent = false;
                    L.l(TAG, "In Update/Insert Model:");
                    for (Model model : db_modelList) {
                        if (models.get(i).getModelID().equals(model.getModelID())) {
                            isPresent = true;
                            L.l(TAG, "In Update Model State:" + isPresent);
                        }
                    }
                    L.l(TAG, "Out of For each Model State:" + isPresent);
                    if (isPresent) {
                        getAppDaoSession().getModelDao().updateInTx(models.get(i));
                        L.l(TAG, "In Update Model:");
                    } else {
                        getAppDaoSession().getModelDao().insertInTx(models.get(i));
                        L.l(TAG, "In Insert Model:");
                    }
                }
            } else {
                L.l(TAG, "Model not available: ");
                getAppDaoSession().getModelDao().insertInTx(models);
            }

        }
        if (jsonObject.getJSONArray("qualification").length() > 0) {
            JSONArray qualificationArray = jsonObject.getJSONArray("qualification");
            List<Qualification> qualificationList = new ArrayList<>();
            List<Qualification> db_qualificationList = new ArrayList<>();
            db_qualificationList = getQualificationList();
            for (int i = 0; i < qualificationArray.length(); i++) {
                Qualification qualification = new Gson().fromJson(String.valueOf(qualificationArray.getJSONObject(i)), Qualification.class);
                L.l("Qualification: " + qualification);
                qualificationList.add(qualification);
            }
            if (db_qualificationList.size() > 0) {
                L.l(TAG, "Qualification available: ");
                boolean isPresent = false;
                for (int i = 0; i < qualificationList.size(); i++) {
                    L.l(TAG, "In Update/Insert Qualification:");
                    isPresent = false;
                    for (Qualification qualification : db_qualificationList) {
                        if (qualificationList.get(i).getQualificationID().equals(qualification.getQualificationID())) {
                            isPresent = true;
                            L.l(TAG, "In Update Qualification State:" + isPresent);
                        }
                    }
                    L.l(TAG, "Out of each Qualification State:" + isPresent);
                    if (isPresent) {
                        getAppDaoSession().getQualificationDao().updateInTx(qualificationList.get(i));
                        L.l(TAG, "In Update Qualification:" + isPresent);
                    } else {
                        getAppDaoSession().getQualificationDao().insertInTx(qualificationList.get(i));
                        L.l(TAG, "In Insert Qualification:" + isPresent);
                    }
                }
            } else {
                L.l(TAG, "Qualification not available: ");
                getAppDaoSession().getQualificationDao().insertInTx(qualificationList);
            }
        }
        if (jsonObject.getJSONArray("category").length() > 0) {
            JSONArray categoryArray = jsonObject.getJSONArray("category");
            List<Category> categories = new ArrayList<>();
            for (int i = 0; i < categoryArray.length(); i++) {
                Category category = new Gson().fromJson(String.valueOf(categoryArray.getJSONObject(i)), Category.class);
                L.l("Category: " + category);
                categories.add(category);
            }
            getAppDaoSession().getCategoryDao().insertInTx(categories);
        }
        if (jsonObject.getJSONArray("subcategory").length() > 0) {
            JSONArray subCategoryArray = jsonObject.getJSONArray("subcategory");
            List<SubCategory> subCategories = new ArrayList<>();
            for (int i = 0; i < subCategoryArray.length(); i++) {
                SubCategory subCategory = new Gson().fromJson(String.valueOf(subCategoryArray.getJSONObject(i)), SubCategory.class);
                L.l("Sub Category: " + subCategory);
                subCategories.add(subCategory);
            }
            getAppDaoSession().getSubCategoryDao().insertInTx(subCategories);
        }
        if (jsonObject.getJSONArray("document_tree").length() > 0) {
            JSONArray documentTreeArray = jsonObject.getJSONArray("document_tree");
            List<DocumentTree> documentTrees = new ArrayList<>();
            List<Document> documentList = new ArrayList<>();
            List<DocumentTree> db_documentTreeList = new ArrayList<>();
            List<Document> db_documentList = new ArrayList<>();
            db_documentTreeList = getAppDaoSession().getDocumentTreeDao().loadAll();
            db_documentList = getAppDaoSession().getDocumentDao().loadAll();
            for (int i = 0; i < documentTreeArray.length(); i++) {
                DocumentTree documentTree = new Gson().fromJson(String.valueOf(documentTreeArray.getJSONObject(i)), DocumentTree.class);
                L.l("Doucment: " + documentTree);
                documentTrees.add(documentTree);
                if (!TextUtils.isEmpty(documentTreeArray.getJSONObject(i).getString("document"))) {
                    JSONArray documentJsonArray = new JSONArray(documentTreeArray.getJSONObject(i).getString("document"));
                    if (documentJsonArray.length() > 0) {
                        for (int j = 0; j < documentJsonArray.length(); j++) {
                            Document document = new Document();
                            JSONArray docjsonArray = documentJsonArray.getJSONArray(j);
                            L.l("Document documentdetail Json array: " + docjsonArray.toString());
                            document.setDocumentTreeID(documentTreeArray.getJSONObject(i).getString("id"));
                            String documentdetail = docjsonArray.getJSONObject(0).toString();
                            L.l("Document documentdetail: " + documentdetail.toString());
                            document.setDocumentName(documentdetail);
                            document.setDocumentHitCount(0);
                            document.setDocumentHitDate(new Date());
                            document.setDocumentReferencedID(0L);
                            String roledetail = docjsonArray.getJSONObject(1).toString();
                            L.l("Document Role: " + roledetail.toString());
                            JSONObject roleObject = new JSONObject(roledetail);
                            String role = roleObject.getString("role");
                            L.l("Document Role value: " + role);
                            document.setDocumentRole(role);
                            documentList.add(document);
                        }
                    }
                }
            }
            if (db_documentTreeList.size() > 0) {
                if (documentTrees.size() > 0) {
                    L.l(TAG, "Document Tree available");
                    boolean docTreePresent = false;
                    boolean docPresent = false;
                    boolean isdeletedoc = false;
                    for (int i = 0; i < documentTrees.size(); i++) {
                        L.l(TAG, "Insert/Update Document Tree");
                        docTreePresent = false;
                        L.l(TAG, "Before For each Document Tree Present State: " + docTreePresent);
                   /* Query query = ATableDao.queryBuilder().where(
                            new StringCondition("nameid IN " +
                                    "(SELECT nameid FROM B_Table )").build();*/
                        for (DocumentTree documentTree : db_documentTreeList) {
                            if (documentTrees.get(i).getDocumentTreeID().equals(documentTree.getDocumentTreeID())) {
                                docTreePresent = true;
                                L.l(TAG, "Document Tree Present State: " + docTreePresent);
                                break;
                            }
                        }
                        L.l(TAG, "Out of For each  Document Tree Present State: " + docTreePresent);
                        if (docTreePresent) {
                            Query<DocumentTree> query = getAppDaoSession().getDocumentTreeDao().queryBuilder()
                                    .where(DocumentTreeDao.Properties.DocumentTreeID.eq(documentTrees.get(i).getDocumentTreeID())).build();
                            List<DocumentTree> documentTrees1 = new ArrayList<>();
                            documentTrees1 = query.list();
                            L.l(TAG, "Query Document Tree List: " + documentTrees1.toString());
                            L.l(TAG, "Document Tree List doc to update: " + documentTrees.get(i).toString());
                            DocumentTree documentTree = documentTrees1.get(0);
                            documentTree.setDocumentTreeDocument(documentTrees.get(i).getDocumentTreeDocument());
                            documentTree.setDocumentTreeID(documentTrees.get(i).getDocumentTreeID());
                            documentTree.setDocumentTreeItem(documentTrees.get(i).getDocumentTreeItem());
                            documentTree.setDocumentTreeSequence(documentTrees.get(i).getDocumentTreeSequence());
                            documentTree.setModelID(documentTrees.get(i).getModelID());
                            documentTree.setParentID(documentTrees.get(i).getParentID());
                            documentTree.setProductID(documentTrees.get(i).getProductID());
                            getAppDaoSession().getDocumentTreeDao().update(documentTree);
                            DocumentTree documentTree1 = getAppDaoSession().getDocumentTreeDao().loadByRowId(documentTree.getId());
                            L.l(TAG, "updated document tree: " + documentTree1.toString());
                        } else {
                            getAppDaoSession().getDocumentTreeDao().insert(documentTrees.get(i));
                            L.l(TAG, "Insert Document Tree Present State: " + docTreePresent);
                        }
                    }
                    for (int i = 0; i < db_documentList.size(); i++) {
                        isdeletedoc = false;
                        for (Document document : documentList) {
                            if (db_documentList.get(i).getDocumentTreeID().equals(document.getDocumentTreeID())) {
                                L.l(TAG, "Delete Present Doc name: " + document.getDocumentName());
                                L.l(TAG, "Name to compare: " + db_documentList.get(i).getDocumentName());
                                if (db_documentList.get(i).getDocumentName().compareTo(document.getDocumentName()) == 0) {
                                    isdeletedoc = true;
                                    L.l(TAG, "Delete Document Present State: " + docPresent);
                                    break;
                                }
                            }
                        }
                        if (!isdeletedoc) {
                            L.l(TAG, "Delete document: " + db_documentList.get(i).toString());
                            getAppDaoSession().getDocumentDao().delete(db_documentList.get(i));
                        }
                    }
                    db_documentList = new ArrayList<>();
                    db_documentList = getAppDaoSession().getDocumentDao().loadAll();
                    for (int i = 0; i < documentList.size(); i++) {
                        L.l(TAG, "Document available");
                        L.l(TAG, "Insert/Update Document Tree");
                        docPresent = false;
                        //L.l(TAG, "Before For each Document Present State: " + docPresent);
                        for (Document document : db_documentList) {
                            if (documentList.get(i).getDocumentTreeID().equals(document.getDocumentTreeID())) {
                                L.l(TAG, "Present Doc name: " + document.getDocumentName());
                                L.l(TAG, "Name to compare: " + documentList.get(i).getDocumentName());
                                if (documentList.get(i).getDocumentName().compareTo(document.getDocumentName()) == 0) {
                                    try {
                                        docPresent = true;
                                        L.l(TAG, "Document Present State: " + docPresent);
                                        L.l(TAG, "Document Present State: " + documentList.get(i));
                                        Document document1 = fetchDocumentByID(String.valueOf(document.getId()));
                                        L.l(TAG, "Doc to update: " + document1.toString());
                                        document1.setDocumentRole(documentList.get(i).getDocumentRole());
                                        document1.setDocumentName(documentList.get(i).getDocumentName());
                                        document1.setDocumentTreeID(documentList.get(i).getDocumentTreeID());
                                        updateDocument(document1);
                                        break;
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        L.l(TAG, "Out of For each  Document Present State: " + docPresent);
                        if (!docPresent) {
                            try {
                                L.l(TAG, "Insert Document Present State: " + documentList.get(i).toString());
                                getAppDaoSession().getDocumentDao().insert(documentList.get(i));
                                L.l(TAG, "Insert Document Present State: " + documentList.get(i).toString());
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //L.l(TAG, "DB DOCList size: "+db_documentList.size()+" response doc list size: "+documentList.size());
                    //    L.l(TAG, "DB DOCList size: "+db_documentList.size()+" response doc list size: "+documentList.size());
                }
            } else {
                L.l(TAG, "DB not exists: " + documentList.toString());
                getAppDaoSession().getDocumentTreeDao().insertInTx(documentTrees);
                getAppDaoSession().getDocumentDao().insertInTx(documentList);
            }
            getAppDaoSession().clear();
        }
    }

    public void deleteDoc(String id) {
        DocumentDao documentdao = getAppDaoSession().getDocumentDao();
        QueryBuilder qb = documentdao.queryBuilder();
        qb.where(DocumentDao.Properties.Id.eq(id));
        List<Document> documentList = qb.list();
        documentdao.deleteInTx(documentList);
    }

    public void saveUser(User user) {
        getAppDaoSession().getUserDao().insert(user);
    }

    public User getUser() {

        return getAppDaoSession().getUserDao().loadAll().get(0);
    }

    public void updateUser(User user) {
        getAppDaoSession().getUserDao().update(user);
        getAppDaoSession().clear();
    }

    public void saveUserMenuRights(MenuRights menuRights) {
        getAppDaoSession().getMenuRightsDao().insertInTx(menuRights);
        getAppDaoSession().clear();
    }

    public void updateUserMenuRights(MenuRights menuRights) {
        getAppDaoSession().getMenuRightsDao().update(menuRights);
        getAppDaoSession().clear();
    }

    public void deleteUserMenuRights() {
        getAppDaoSession().getMenuRightsDao().deleteAll();
        getAppDaoSession().clear();
    }

    public MenuRights getUserMenuRights() {
        MenuRights menuRights = getAppDaoSession().getMenuRightsDao().loadAll().get(0);
        getAppDaoSession().clear();
        return menuRights;
    }

    public void clearUserData() {
        getAppDaoSession().getUserDao().deleteAll();
        getAppDaoSession().getQueriesDao().deleteAll();
        getAppDaoSession().getQueryResponseDao().deleteAll();
        getAppDaoSession().getNotificationsDao().deleteAll();
        getAppDaoSession().getProductDao().deleteAll();
        getAppDaoSession().getModelDao().deleteAll();
        getAppDaoSession().getDocumentTreeDao().deleteAll();
        getAppDaoSession().getDocumentDao().deleteAll();
        getAppDaoSession().getMenuRightsDao().deleteAll();

        getAppDaoSession().clear();
    }

    public void clearNotification() {
        getAppDaoSession().getNotificationsDao().deleteAll();
        getAppDaoSession().clear();
    }

    public void deleteUser() {
        L.l(TAG, "deleting user");
        getAppDaoSession().getUserDao().deleteAll();
        getAppDaoSession().clear();
    }

    public void deleteNotifications() {
        L.l(TAG, "deleting notifications");
        getAppDaoSession().getNotificationsDao().deleteAll();
        getAppDaoSession().clear();
    }

    public List<Company> getCompanyList() {
        return getAppDaoSession().getCompanyDao().loadAll();
    }

    public List<Designation> getDesignationList() {
        List<Designation> designationList = getAppDaoSession().getDesignationDao().loadAll();
        getAppDaoSession().clear();
        return designationList;
    }

    public List<Profile> getProfileList() {
        List<Profile> profileList = getAppDaoSession().getProfileDao().loadAll();
        getAppDaoSession().clear();
        return profileList;
    }

    public List<Location> getLocaitonList() {
        return getAppDaoSession().getLocationDao().loadAll();
    }

    public List<Product> getProductList() {
        List<Product> productList = getAppDaoSession().getProductDao().queryBuilder().orderAsc(ProductDao.Properties.ProductSequence)
                .where(new WhereCondition.StringCondition("PRODUCT_ID IN (SELECT DISTINCT(PRODUCT_ID) FROM MODEL WHERE MODEL_ID IN (SELECT MODEL_ID FROM DOCUMENT_TREE))"))
                .build().list();
        getAppDaoSession().clear();
        Log.d(TAG, "getProductList: SIZE " + productList.size());
        return productList;
    }

    public List<Category> getCategoryList() {
        return getAppDaoSession().getCategoryDao().loadAll();
    }
    public List<Course> getCourseList() {
        return getAppDaoSession().getCourseDao().loadAll();
    }

    public List<SubCategory> getSubCategoryList() {
        List<SubCategory> subCategoryList = getAppDaoSession().getSubCategoryDao().loadAll();
        getAppDaoSession().clear();
        return subCategoryList;
    }

    public List<SubCategory> getSubCategoryListByCategoryID(String categoryID) {
        return getAppDaoSession().getSubCategoryDao().queryBuilder().where(SubCategoryDao.Properties.CategoryID.eq(categoryID)).list();
    }

    public Query<Model> getModelList(String productID) {
        Query<Model> modelQuery = getAppDaoSession().getModelDao().queryBuilder().where(ModelDao.Properties.ProductID.eq(productID))
                .where(new WhereCondition.StringCondition("MODEL_ID IN (SELECT MODEL_ID FROM DOCUMENT_TREE)"))
                .orderAsc(ModelDao.Properties.ModelName).build();
        getAppDaoSession().clear();
        return modelQuery;
    }

    public Product getProduct(String productID) {
        Query<Product> productQuery = getAppDaoSession().getProductDao().queryBuilder().where(ProductDao.Properties.ProductID.eq(productID)).build();
        if (productQuery.list().size() > 0) {
            return productQuery.list().get(0);
        }
        return null;
    }

    public boolean getIsDocExist(String productID, String modelID) {
        Log.d(TAG, "getIsDocExist: START");
        int documentTreeID = 0;
        DocumentTree documentTree = null;
        do {
            documentTree = getAppDaoSession().getDocumentTreeDao().queryBuilder()
                    .where(DocumentTreeDao.Properties.ProductID.eq(productID))
                    .where(DocumentTreeDao.Properties.ModelID.eq(modelID))
                    .where(DocumentTreeDao.Properties.ParentID.eq(documentTreeID))
                    .where(new WhereCondition.StringCondition("DOCUMENT_TREE_ID IN (SELECT DOCUMENT_TREE_ID FROM DOCUMENT_TREE WHERE DOCUMENT_TREE_DOCUMENT!='[]' AND PARENT_ID='0' OR DOCUMENT_TREE_DOCUMENT='[]' AND PARENT_ID!='0')"))
                    .build().list().get(0);
            Log.d(TAG, "getIsDocExist: TREE OBJECT: " + documentTree);
            if (documentTree == null) {
                return false;
            }
            documentTreeID = Integer.parseInt(documentTree.getParentID());
        } while (documentTree.getDocumentTreeDocument().equals("[]"));


        return true;
    }

    public Model getModel(String modelID) {
        Query<Model> modelQuery = getAppDaoSession().getModelDao().queryBuilder().where(ModelDao.Properties.ModelID.eq(modelID)).build();
        if (modelQuery.list().size() > 0) {
            return modelQuery.list().get(0);
        }
        return null;
    }

    public DocumentTree getDoucmentTree(String documentID) {
        Query<DocumentTree> documentTreeQuery = getAppDaoSession().getDocumentTreeDao().queryBuilder().where(DocumentTreeDao.Properties.DocumentTreeID.eq(documentID)).build();
        if (documentTreeQuery.list().size() > 0) {
            return documentTreeQuery.list().get(0);
        }
        return null;
    }

    public List<Model> getModelList() {
        List<Model> modelList = getAppDaoSession().getModelDao().loadAll();
        getAppDaoSession().clear();
        return modelList;
    }

    public List<Model> getModelListForSearch() {
        List<Model> modelList = getModelList();
        for (int i = 0; i < modelList.size(); i++) {
            Product product = getAppDaoSession().getProductDao().queryBuilder().where(ProductDao.Properties.ProductID.eq(modelList.get(i).getProductID())).list().get(0);
            modelList.get(i).setModelName(product.getProductName() + "->" + modelList.get(i).getModelName());
        }
        getAppDaoSession().clear();
        return modelList;
    }

    public Query<DocumentTree> getDocumentTree(String productID, String modelID, String documentTreeID) {
        Query<DocumentTree> treeQuery = getAppDaoSession().getDocumentTreeDao().queryBuilder()
                .where(DocumentTreeDao.Properties.ProductID.eq(productID))
                .where(DocumentTreeDao.Properties.ModelID.eq(modelID))
                .where(DocumentTreeDao.Properties.ParentID.eq(documentTreeID))
                .where(new WhereCondition.StringCondition("DOCUMENT_TREE_ID IN (SELECT DOCUMENT_TREE_ID FROM DOCUMENT_TREE WHERE DOCUMENT_TREE_DOCUMENT!='[]' AND PARENT_ID='0' OR DOCUMENT_TREE_DOCUMENT='[]' AND PARENT_ID!='0')"))
                .build();
        getAppDaoSession().clear();
        //Log.d(TAG, "getDocumentTree: RESULT OF getIsDocExist: "+getIsDocExist(productID,modelID));
        return treeQuery;
    }

    public boolean getDocumentIsAvailable(String documentTreeID) {
        QueryBuilder<DocumentTree> documentQueryBuilder = getAppDaoSession().getDocumentTreeDao().queryBuilder()
                .where(DocumentTreeDao.Properties.ParentID.eq(documentTreeID));
        getAppDaoSession().clear();
        boolean docavail = documentQueryBuilder.list().size() > 0;
        L.l(TAG, "IS Document available STATE: " + docavail);
        return documentQueryBuilder.list().size() > 0;
    }

    public DocumentTree getDocument(String documentTreeID) {
        QueryBuilder<DocumentTree> documentQueryBuilder = getAppDaoSession().getDocumentTreeDao().queryBuilder()
                .where(DocumentTreeDao.Properties.ParentID.eq(documentTreeID));
        getAppDaoSession().clear();
        return documentQueryBuilder.list().get(0);
    }

    public DocumentTree getSelfDocumentTree(String documentTreeID) {
        QueryBuilder<DocumentTree> documentQueryBuilder = getAppDaoSession().getDocumentTreeDao().queryBuilder()
                .where(DocumentTreeDao.Properties.DocumentTreeID.eq(documentTreeID))
                .orderAsc(DocumentTreeDao.Properties.DocumentTreeSequence);
        getAppDaoSession().clear();
        return documentQueryBuilder.list().get(0);
    }

    public boolean getSelfDocument(String documentTreeID) {
        QueryBuilder<DocumentTree> documentQueryBuilder = getAppDaoSession().getDocumentTreeDao().queryBuilder()
                .where(DocumentTreeDao.Properties.DocumentTreeID.eq(documentTreeID));
        getAppDaoSession().clear();
        boolean selfdocavail = documentQueryBuilder.list().size() > 0;
        L.l(TAG, "IS Self Document available STATE: " + selfdocavail);
        return documentQueryBuilder.list().size() > 0;
    }


    public DaoSession getAppDaoSession() {
        return (MyApplication.getsInstance()).getDaoSession();
    }

//    public List<SearchModel> getSearchList(String searchTxt, String productID, String modelID) {
//
//        List<SearchModel> searchModelList = new ArrayList<>();
//        List<DocumentTree> documentTreeList = new ArrayList<>();
//
//        if (TextUtils.isEmpty(productID) && TextUtils.isEmpty(modelID)) {
//            List<Product> productList = getAppDaoSession().getProductDao().queryBuilder().where(ProductDao.Properties.ProductName.like("%" + searchTxt + "%")).list();
//            L.l("PRODUCT SIZE SEARCH: " + productList.size());
//            if (productList.size() > 0) {
//                for (Product product : productList) {
//                    searchModelList.add(new SearchModel(product.getProductID(), product.getProductName(), "product"));
//                }
//            }
//            List<Model> modelList = getAppDaoSession().getModelDao().queryBuilder().where(ModelDao.Properties.ModelName.like("%" + searchTxt + "%")).list();
//            L.l("MODEL SIZE SEARCH: " + modelList.size());
//            if (modelList.size() > 0) {
//                for (Model model : modelList) {
//                    Product product = getAppDaoSession().getProductDao().queryBuilder().where(ProductDao.Properties.ProductID.eq(model.getProductID())).list().get(0);
//                    searchModelList.add(new SearchModel(model.getModelID(), product.getProductName() + " -> " + model.getModelName(), "model"));
//                }
//            }
//            documentTreeList = getAppDaoSession().getDocumentTreeDao().queryBuilder().where(DocumentTreeDao.Properties.DocumentTreeItem.like("%" + searchTxt + "%")).list();
//
//        } else {
//            documentTreeList = getAppDaoSession().getDocumentTreeDao().queryBuilder().where(DocumentTreeDao.Properties.DocumentTreeItem.like("%" + searchTxt + "%")).
//                    where(DocumentTreeDao.Properties.ProductID.eq(productID))
//                    .where(DocumentTreeDao.Properties.ModelID.eq(modelID)).list();
//        }
//
//        L.l("DOCUMENT SIZE SEARCH: " + documentTreeList.size());
//        if (documentTreeList.size() > 0) {
//            for (DocumentTree tree : documentTreeList) {
//                Product product = getAppDaoSession().getProductDao().queryBuilder().where(ProductDao.Properties.ProductID.eq(tree.getProductID())).list().get(0);
//                Model model = getAppDaoSession().getModelDao().queryBuilder().where(ModelDao.Properties.ModelID.eq(tree.getModelID())).list().get(0);
//                searchModelList.add(new SearchModel(tree.getDocumentTreeID(), product.getProductName() + " -> " + model.getModelName() + " -> " + tree.getDocumentTreeItem(), "document"));
//            }
//            for (DocumentTree tree : documentTreeList) {
//                List<Document> documentList = new DBHelper().getDocumentList(tree.getDocumentTreeID());
//                if (documentList.size() > 0) {
//
//                    for (int i = 0; i < documentList.size(); i++) {
//                        String extension = CommonFunctions.getExtension(documentList.get(i).getDocumentName());
//                        Product product = getAppDaoSession().getProductDao().queryBuilder().where(ProductDao.Properties.ProductID.eq(tree.getProductID())).list().get(0);
//                        Model model = getAppDaoSession().getModelDao().queryBuilder().where(ModelDao.Properties.ModelID.eq(tree.getModelID())).list().get(0);
//                        searchModelList.add(new SearchModel(tree.getDocumentTreeID(), product.getProductName() + " -> " + model.getModelName() + " -> " + documentList.get(i).getDocumentName(), "document", String.valueOf(documentList.get(i).getId())));
//                    }
//                }
//            }
//        }
//        L.l("SEARCH LIST SIZE: " + searchModelList.size());
//        getAppDaoSession().clear();
//        return searchModelList;
//    }

    public void saveQuery(Queries queries) {
        getAppDaoSession().getQueriesDao().insert(queries);
        getAppDaoSession().clear();
    }

    public List<Queries> getQueriesList() {
        List<Queries> queriesList = getAppDaoSession().getQueriesDao().queryBuilder().orderDesc(QueriesDao.Properties.QueryInsertedID).list();
        getAppDaoSession().clear();
        return queriesList;
    }

    public void updateQuery(Queries queries) {
        getAppDaoSession().getQueriesDao().update(queries);
        getAppDaoSession().clear();
    }

    public void saveResponse(QueryResponse queryResponse) {
        L.l("SAVE Query RESPONSE", "" + queryResponse.toString());
        getAppDaoSession().getQueryResponseDao().insert(queryResponse);
        getAppDaoSession().clear();
    }

    public List<QueryResponse> getQueryResponseList(String queryID) {
        List<QueryResponse> queryResponseList = new ArrayList<>();
        try {
            queryResponseList = getAppDaoSession().getQueryResponseDao().queryBuilder().where(QueryResponseDao.Properties.QueryID.eq(queryID)).list();
            getAppDaoSession().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryResponseList;
    }

    public List<Profile> getRequestProfileList() {
//        List<Profile> profileList = getAppDaoSession().getProfileDao().loadAll();
        List<Profile> profileList = getAppDaoSession().getProfileDao().queryBuilder().where(ProfileDao.Properties.Profile_approval.eq(false))
                .where(ProfileDao.Properties.Profile_requested.eq(false)).list();
        getAppDaoSession().clear();
        return profileList;
    }

    public List<Document> getDocumentList(String documentTreeID) {
        List<Document> documentList = getAppDaoSession().getDocumentDao().queryBuilder().where(DocumentDao.Properties.DocumentTreeID.eq(documentTreeID)).list();
        getAppDaoSession().clear();
        return documentList;
    }

    public List<Document> getAllDocs() {
        List<Document> documentList = getAppDaoSession().getDocumentDao().loadAll();
        return documentList;
    }

    public Document fetchDocumentByID(String docID) {
        L.l(TAG, "Doc ID to fetch: " + docID);
        Document document = getAppDaoSession().getDocumentDao().loadByRowId(Long.parseLong(docID));
        getAppDaoSession().clear();
        if (document != null) {
            L.l(TAG, "Fetch Document: " + document.toString());
        }
        return document;
    }

    public void updateDocument(Document document) {
        getAppDaoSession().getDocumentDao().update(document);
        // L.l(TAG, "Updated Document: " + fetchDocumentByID(document.getId().toString()).toString());
        getAppDaoSession().clear();
    }

    public List<Document> getHistoryDocumentList() {
        List<Document> documentList = getAppDaoSession().getDocumentDao().queryBuilder().where(DocumentDao.Properties.DocumentHitCount.notEq(0)).orderDesc(DocumentDao.Properties.DocumentHitDate).list();
        L.l(TAG, documentList.toString());
        getAppDaoSession().clear();
        return documentList;
    }

    public List<Document> getMostViewDocumentList() {
        List<Document> documentList = getAppDaoSession().getDocumentDao().queryBuilder().where(DocumentDao.Properties.DocumentHitCount.notEq(0)).orderDesc(DocumentDao.Properties.DocumentHitCount).list();
        L.l(TAG, documentList.toString());
        getAppDaoSession().clear();
        return documentList;
    }

    public void saveNotifications(Notifications notifications) {
        L.l("SAVE Notifications RESPONSE", "" + notifications.toString());
        getAppDaoSession().getNotificationsDao().insertInTx(notifications);
        getAppDaoSession().clear();
    }

    public List<Notifications> getNotificationList() {
        List<Notifications> notificationsList = getAppDaoSession().getNotificationsDao().queryBuilder().orderDesc(NotificationsDao.Properties.Id).list();
        getAppDaoSession().clear();
        return notificationsList;
    }

    public List<Profile> fetchProfileByID(String profile_id) {
        List<Profile> profileList = getAppDaoSession().getProfileDao().queryBuilder().where(ProfileDao.Properties.ProfileID.eq(profile_id)).list();
        L.l(TAG, "fetched PROFILE: " + profileList.toString());
        return profileList;
    }

    public void updateProfile(Profile profile) {
        getAppDaoSession().getProfileDao().update(profile);
        L.l(TAG, "UPDATED PROFILE: " + fetchProfileByID(profile.getProfileID()));
        getAppDaoSession().clear();
    }

    public List<Profile> getApprovedProfileList() {
        List<Profile> profileList = getAppDaoSession().getProfileDao().queryBuilder().where(ProfileDao.Properties.Profile_approval.eq(true)).list();
        L.l(TAG, "APPROVED PROFILE LIST: " + profileList.toString());
        getAppDaoSession().clear();
        return profileList;
    }

    public List<Document> documentHitList() {
        List<Document> documentHitList = getAppDaoSession().getDocumentDao().queryBuilder().where(DocumentDao.Properties.DocumentHitCount.gt(0)).list();
        getAppDaoSession().clear();
        return documentHitList;
    }

    public List<Qualification> getQualificationList() {
        List<Qualification> qualificationList = getAppDaoSession().getQualificationDao().loadAll();
        L.l(TAG, "QUALIFICATION LIST: " + qualificationList.toString());
        getAppDaoSession().clear();
        return qualificationList;
    }

    public Profile getProfileByID(String profileID) {
        Profile profile = getAppDaoSession().getProfileDao().queryBuilder().where(ProfileDao.Properties.ProfileID.eq(profileID)).list().get(0);
        getAppDaoSession().clear();
        return profile;
    }

    public void setRequestedProfile(Profile profile) {
        getAppDaoSession().getProfileDao().update(profile);
        getAppDaoSession().clear();
    }

    public Queries getQueriesByID(long queri_id) {
        Queries queries = getAppDaoSession().getQueriesDao().loadByRowId(queri_id);
        getAppDaoSession().clear();
        return queries;
    }
    public Queries getQueriesByInsertedID(String queri_id) {
        Queries queries = getAppDaoSession().getQueriesDao().queryBuilder().where(QueriesDao.Properties.QueryInsertedID.eq(queri_id)).list().get(0);
        getAppDaoSession().clear();
        return queries;
    }

    public List<DocumentTree> getdoclist() {
        List<DocumentTree> documentTreeList = getAppDaoSession().getDocumentTreeDao().loadAll();
        return documentTreeList;
    }
}
