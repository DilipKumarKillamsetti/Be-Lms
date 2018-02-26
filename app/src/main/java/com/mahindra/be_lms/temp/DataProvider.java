package com.mahindra.be_lms.temp;

import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.Company;
import com.mahindra.be_lms.model.Course;
import com.mahindra.be_lms.model.Designation;
import com.mahindra.be_lms.model.Location;
import com.mahindra.be_lms.model.MostView;
import com.mahindra.be_lms.model.Product;
import com.mahindra.be_lms.model.Program;
import com.mahindra.be_lms.model.Queries;
import com.mahindra.be_lms.model.Quiz;
import com.mahindra.be_lms.model.QuizQuestion;
import com.mahindra.be_lms.model.TestPaper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

/**
 * Created by android7 on 10/1/16.
 */

public class DataProvider {


    public static List<Company> getDummyCompList() {
        List<Company> companyList = new ArrayList<>();
        companyList.add(new Company("0", "Select Company"));
        companyList.add(new Company("1", "Taraba SOft"));
        companyList.add(new Company("2", "SOftline Solution"));
        companyList.add(new Company("3", "Krios Technology"));
        return companyList;
    }

    public static List<Designation> getDummyDesigList() {
        List<Designation> designationList = new ArrayList<>();
        designationList.add(new Designation("0", "Select Designation"));
        designationList.add(new Designation("1", "Powerol Manager"));
        designationList.add(new Designation("2", "Dealer Manager"));
        designationList.add(new Designation("3", "Dealer Technician"));
        return designationList;
    }

    public static List<Location> getDummyLocationList() {
        List<Location> locationList = new ArrayList<>();
        locationList.add(new Location("0", "Select Location"));
        locationList.add(new Location("1", "Nashik"));
        locationList.add(new Location("2", "Pune"));
        locationList.add(new Location("3", "Mumbai"));
        return locationList;
    }
    /*public static List<Qualification> getDummyQualificationList() {
        List<Qualification> qualificationList = new ArrayList<>();
        qualificationList.add(new Qualification(0l,"0", "Select Qualification"));
        qualificationList.add(new Qualification(0l,"1", "MBA"));
        qualificationList.add(new Qualification(0l,"2", "BAM"));
        qualificationList.add(new Qualification(0l,"3", "BE"));
        qualificationList.add(new Qualification(0l,"4", "ME"));
        return qualificationList;
    }*/

    public static List<MostView> getDummyMostViewList() {
        List<MostView> mostViewList = new ArrayList<>();
//        mostViewList.add(new MostView("0", "Engine", "lorEverything we do is connected with love: life, beauty, result, art."));
//        mostViewList.add(new MostView("1", "Engine", "lorEverything we do is connected with love: life, beauty, result, art."));
//        mostViewList.add(new MostView("2", "Engine", "lorEverything we do is connected with love: life, beauty, result, art."));
//        mostViewList.add(new MostView("3", "Engine", "lorEverything we do is connected with love: life, beauty, result, art."));
//        mostViewList.add(new MostView("4", "Engine", "lorEverything we do is connected with love: life, beauty, result, art."));
//        mostViewList.add(new MostView("5", "Engine", "lorEverything we do is connected with love: life, beauty, result, art."));
        return mostViewList;
    }

    public static List<Program> getDummyProgramList() {
        List<Program> programList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            List<Course> courseList = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                courseList.add(new Course("" + j, "Where is the old doubloons?", "Parasites view from flights like harmless vogons."));
            }
            GregorianCalendar gc = new GregorianCalendar();

            int year = 2016;

            gc.set(Calendar.YEAR, year);

            int dayOfYear = randBetween(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));

            gc.set(Calendar.DAY_OF_YEAR, dayOfYear);
            String randomDate = (gc.get(Calendar.DAY_OF_MONTH) + "-" + (gc.get(Calendar.MONTH) + 1)) + "-" + gc.get(Calendar.YEAR);
            L.l("RAMDOM DATE : " + randomDate);
            programList.add(new Program("" + i, "Hilotae, accola, et turpis.", randomDate, "The collective resists friendship like a carnivorous c-beam.", new Random().nextBoolean(), courseList));
        }
        return programList;
    }

    public static List<Course> getDummyCourseList() {
        List<Course> courseList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            courseList.add(new Course("" + i, "Course-" + i, "lorEverything we do is connected with love: life, beauty, result, art."));
        }
        return courseList;
    }

    public static List<Quiz> getDummyQuizList() {
        List<Quiz> quizList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            List<QuizQuestion> quizQuestionList = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                quizQuestionList.add(new QuizQuestion("" + j, j + ": Question", "option A", "option B", "option C", "option D", new Random().nextInt(4), 0));
            }
            quizList.add(new Quiz("" + i, "Quiz-" + i, "The vital space suit virtually consumes the transporter.", quizQuestionList));

        }
        return quizList;
    }

/*   public static List<TechnicalUpload> getDummyTUVList() {
      List<TechnicalUpload> technicalUploadList;
        technicalUploadList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            technicalUploadList.add(new TechnicalUpload("" + i, "Compater magnum ignigena est.", "Visitors, doers, and playful bodies will always protect them.", "Video", ""));
        }
        return technicalUploadList;
    }

    public static List<TechnicalUpload> getDummyTUHList() {
       List<TechnicalUpload> technicalUploadList;
        technicalUploadList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            technicalUploadList.add(new TechnicalUpload("" + i, "Compater magnum ignigena est.", "Visitors, doers, and playful bodies will always protect them.", "Html", "http://rise.mahindra.com/living-rise/"));
        }
        return technicalUploadList;
    }

    public static List<TechnicalUpload> getDummyTUDList() {
        List<TechnicalUpload> technicalUploadList;
        technicalUploadList = new ArrayList<>();
        String[] docType = {"Pdf", "Doc", "Excel"};
        for (int i = 0; i < 10; i++) {

            int index = new Random().nextInt(docType.length);

            String url = null;
            if (index == 0) {
                url = "http://www.cbu.edu.zm/downloads/pdf-sample.pdf";
            } else if (index == 1) {
                url = "https://www.sample-videos.com/xls/Sample-Spreadsheet-10-rows.xls";
            } else if (index == 2) {
                url = "https://www.sample-videos.com/doc/Sample-doc-file-100kb.doc";
            }

            technicalUploadList.add(new TechnicalUpload("" + i, "Compater magnum ignigena est.", "Visitors, doers, and playful bodies will always protect them.", docType[index], url));
        }
        return technicalUploadList;
    }*/

    public static List<TestPaper> getDummyTestPaperList() {
        List<TestPaper> testPaperList = new ArrayList<>();
        String[] type = new String[]{"Pdf", "Video", "Excel", "Doc", "Html"};
        for (int i = 1; i <= 10; i++) {
            int index = new Random().nextInt(type.length);
            String url = null;
            if (index == 0) {
                url = "http://www.cbu.edu.zm/downloads/pdf-sample.pdf";
            } else if (index == 1) {
                url = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
            } else if (index == 2) {
                url = "http://spreadsheetpage.com/downloads/xl/king-james-bible.xlsm";
            } else if (index == 3) {
                url = "http://tech44.site40.net/mydoc/letterlegal5.doc";
            } else if (index == 4) {
                url = "http://getbootstrap.com/";
            }

            testPaperList.add(new TestPaper("" + i, "Test Paper-" + i, type[index], "The particle is more space now than star. ship-wide and accelerative apocalyptic.", url));
        }
        return testPaperList;
    }

    public static int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    public static List<Product> getProductList() {
        List<Product> productList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            productList.add(new Product("" + i, "Product " + i, "To some, a scholar is an acceptance for meetting."));
        }
        return productList;
    }

//    public static List<Queries> getQueryList() {
//        List<Queries> queriesList = new ArrayList<>();
//        String[] status = new String[]{"Solve", "Unsolve"};
//        for (int i = 1; i < 8; i++) {
//            queriesList.add(new Queries("" + i, "Queries " + i, "Yo-ho-ho, swashbuckling cannon. go to tubbataha reef.", status[new Random().nextInt(status.length)], "Amay Chadda"));
//        }
//        return queriesList;
//    }
}
