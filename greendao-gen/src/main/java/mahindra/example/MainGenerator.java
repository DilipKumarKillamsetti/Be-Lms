package mahindra.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MainGenerator {
    private static final String PROJECT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {
        Schema schema = new Schema(1, "mahindra.agri.astra.db");
        schema.enableKeepSectionsByDefault();
        addTables(schema);
        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "/app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(Schema schema) {
        Entity company = addCompany(schema);
        Entity designation = addDesignation(schema);
        Entity location = addLocation(schema);
        Entity qualification = addQualification(schema);
        Entity profile = addProfile(schema);
        Entity user = addUser(schema);
        Entity product = addProduct(schema);
        Entity model = addModel(schema);
        Entity category = addCategory(schema);
        Entity subCategory = addSubCategory(schema);
        Entity documentTree = addDocumentTree(schema);
        Entity query = addQuery(schema);
        Entity query_response = addQueryResponse(schema);
        Entity event = addEvent(schema);
        Entity course = addCourse(schema);
        Entity coordinators = addCoordinators(schema);
        Entity trainers = addTrainers(schema);
        Entity document = addDocument(schema);
        Entity notifications = addNotifications(schema);
        Entity technicalUploads = addTechnicalUploads(schema);
        Entity menurights= addMenuRights(schema);
    }

    private static Entity addMenuRights(Schema schema) {
        Entity menurights = schema.addEntity("MenuRights");
        menurights.addIdProperty().primaryKey().autoincrement();
        menurights.addStringProperty("registration");
        menurights.addStringProperty("search");
        menurights.addStringProperty("powerolCare");
        menurights.addStringProperty("mostViewed");
        menurights.addStringProperty("myProfile");
        menurights.addStringProperty("surveyFeedbacks");
        menurights.addStringProperty("myTrainingPassport");
        menurights.addStringProperty("learningTestQuizs");
        menurights.addStringProperty("manualsBulletins");
        menurights.addStringProperty("trainingCalenderNomination");
        menurights.addStringProperty("queriesResponse");
        menurights.addStringProperty("technicalUploads");
        menurights.addStringProperty("myFieldRecords");
        menurights.addStringProperty("reports");
        menurights.addStringProperty("manpowerEdition");
        return null;
    }

    private static Entity addQualification(Schema schema) {
        Entity qualification = schema.addEntity("Qualification");
        qualification.addIdProperty().primaryKey().autoincrement();
        qualification.addStringProperty("qualificationID");
        qualification.addStringProperty("qualificationName");
        qualification.addStringProperty("qualificationRemark");
        return qualification;

    }

    private static Entity addTechnicalUploads(Schema schema) {
        Entity technicalUpload = schema.addEntity("TechnicalUpload");
        technicalUpload.addIdProperty().primaryKey().autoincrement();
        technicalUpload.addStringProperty("technicalUploadID");
        technicalUpload.addStringProperty("technicalUploadCategory");
        technicalUpload.addStringProperty("technicalUploadSubcategory");
        technicalUpload.addStringProperty("technicalUploadTitle");
        technicalUpload.addStringProperty("technicalUploadDesc");
        technicalUpload.addStringProperty("technicalUploadAttachments");
        return technicalUpload;
    }

    private static Entity addNotifications(Schema schema) {
        Entity notification = schema.addEntity("Notifications");
        notification.addIdProperty().primaryKey().autoincrement();
        notification.addStringProperty("notificationID");
        notification.addStringProperty("notificationTitle");
        notification.addStringProperty("notificationMsg");
        notification.addStringProperty("notificationType");
        notification.addStringProperty("notificationOtherType");
        notification.addStringProperty("notificationattachLink");
        notification.addStringProperty("notificationextraLink");
        return notification;
    }

    private static Entity addTrainers(Schema schema) {
        Entity entity = schema.addEntity("Trainers");
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addStringProperty("trainersID");
        entity.addStringProperty("trainersName");
        return entity;
    }

    private static Entity addCoordinators(Schema schema) {
        Entity entity = schema.addEntity("Coordinators");
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addStringProperty("coordinatorsID");
        entity.addStringProperty("coordinatorsName");
        return entity;
    }


    private static Entity addCourse(Schema schema) {
        Entity entity = schema.addEntity("Course");
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addStringProperty("CourseID");
        entity.addStringProperty("CourseName");
        return entity;
    }

    private static Entity addEvent(Schema schema) {
        Entity entity = schema.addEntity("Event");
        entity.addIdProperty().primaryKey().autoincrement();
        entity.addStringProperty("eventID");
        entity.addStringProperty("eventName");
        entity.addStringProperty("eventMonth");
        entity.addStringProperty("eventFromdate");
        entity.addStringProperty("eventTodate");
        entity.addStringProperty("eventNomination");
        entity.addStringProperty("eventVenue");
        entity.addStringProperty("eventProgram_no");
        return entity;
    }


    private static Entity addQueryResponse(Schema schema) {
        Entity query_response = schema.addEntity("QueryResponse");
        query_response.addIdProperty().primaryKey().autoincrement();
        query_response.addStringProperty("queryID");
        query_response.addStringProperty("resposePerson");
        query_response.addStringProperty("message");
        query_response.addStringProperty("title");
        query_response.addStringProperty("msg_type");
        query_response.addStringProperty("queryReplyAttachment");
        query_response.addStringProperty("queryResponseExtraLink");
        query_response.addStringProperty("queryResponseType");
        return query_response;
    }

    private static Entity addQuery(Schema schema) {
        Entity query = schema.addEntity("Queries");
        query.addIdProperty().primaryKey().autoincrement();
        query.addStringProperty("queryInsertedID");
        query.addStringProperty("productID");
        query.addStringProperty("querySubject");
        query.addStringProperty("queryBody");
        query.addStringProperty("queryAttachment");
        query.addBooleanProperty("queryStatus").notNull();
        return query;
    }
    private static Entity addProfile(Schema schema) {
        Entity profile = schema.addEntity("Profile");
        profile.addIdProperty().primaryKey().autoincrement();
        profile.addStringProperty("profileID");
        profile.addStringProperty("profileName");
        profile.addStringProperty("profile_json");
        profile.addBooleanProperty("profile_approval");
        profile.addBooleanProperty("profile_requested");

        return profile;
    }


    private static Entity addLocation(Schema schema) {
        Entity location = schema.addEntity("Location");
        location.addIdProperty().primaryKey().autoincrement();
        location.addStringProperty("locationID");
        location.addStringProperty("locationName");
        return location;
    }

    private static Entity addDesignation(Schema schema) {
        Entity designation = schema.addEntity("Designation");
        designation.addIdProperty().primaryKey().autoincrement();
        designation.addStringProperty("desigID");
        designation.addStringProperty("desigName");
        return designation;
    }

    private static Entity addCompany(Schema schema) {
        Entity company = schema.addEntity("Company");
        company.addIdProperty().primaryKey().autoincrement();
        company.addStringProperty("comapnayID");
        company.addStringProperty("compnayName");
        company.addStringProperty("companyCode");
        return company;
    }

    private static Entity addUser(Schema schema) {
        Entity user = schema.addEntity("User");
        user.addIdProperty().primaryKey().autoincrement();
        user.addStringProperty("userID");
        user.addStringProperty("userFirstName");
        user.addStringProperty("userLastName");
        user.addStringProperty("username");
        user.addStringProperty("password");
        user.addStringProperty("userEmailID");
        user.addStringProperty("userMobileNo");
        user.addStringProperty("userOrg");
        user.addStringProperty("userOrgCode");
        user.addStringProperty("userLocationID");
        user.addStringProperty("userDesignationID");
        user.addStringProperty("userPicture");
        user.addStringProperty("userQualificationID");
        user.addStringProperty("userDOB");
        user.addStringProperty("userDOJ");
        user.addStringProperty("userRole");
        user.addStringProperty("profiles");
        user.addStringProperty("userGroups");
        return user;
    }

    private static Entity addProduct(Schema schema) {
        Entity product = schema.addEntity("Product");
        product.addIdProperty().primaryKey().autoincrement();
        product.addStringProperty("productID");
        product.addStringProperty("productName");
        product.addStringProperty("productSequence");
        product.addStringProperty("productImageUrl");
        return product;
    }

    private static Entity addModel(Schema schema) {
        Entity model = schema.addEntity("Model");
        model.addIdProperty().primaryKey().autoincrement();
        model.addStringProperty("modelID");
        model.addStringProperty("modelName");
        model.addStringProperty("modelSequence");
        model.addStringProperty("productID");
        return model;
    }

    private static Entity addCategory(Schema schema) {
        Entity category = schema.addEntity("Category");
        category.addIdProperty().primaryKey().autoincrement();
        category.addStringProperty("categoryID");
        category.addStringProperty("categoryName");
        category.addStringProperty("categorySequence");
        return category;
    }

    private static Entity addSubCategory(Schema schema) {
        Entity subCategory = schema.addEntity("SubCategory");
        subCategory.addIdProperty().primaryKey().autoincrement();
        subCategory.addStringProperty("subCategoryID");
        subCategory.addStringProperty("subCategoryName");
        subCategory.addStringProperty("subCategorySequence");
        subCategory.addStringProperty("categoryID");
        return subCategory;
    }

    private static Entity addDocumentTree(Schema schema) {
        Entity documentTree = schema.addEntity("DocumentTree");
        documentTree.addIdProperty().primaryKey().autoincrement();
        documentTree.addStringProperty("documentTreeID");
        documentTree.addStringProperty("documentTreeItem");
        documentTree.addStringProperty("documentTreeDocument");
        documentTree.addStringProperty("documentTreeSequence");
        documentTree.addStringProperty("productID");
        documentTree.addStringProperty("modelID");
        documentTree.addStringProperty("parentID");
        return documentTree;
    }

    private static Entity addDocument(Schema schema) {
        Entity document = schema.addEntity("Document");
        document.addIdProperty().primaryKey().autoincrement();
        document.addStringProperty("documentTreeID");
        document.addStringProperty("documentName");
        document.addLongProperty("documentReferencedID");
        document.addDateProperty("documentHitDate");
        document.addIntProperty("documentHitCount");
        document.addStringProperty("documentRole");
        return document;
    }
}
