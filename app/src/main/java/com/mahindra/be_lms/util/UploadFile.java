package com.mahindra.be_lms.util;

import android.util.Log;

import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.KeyValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Pravin on 11/12/16.
 */

public class UploadFile {
    public static final String UPLOAD_URL = Constants.BE_LMS_PIC_UPLOAD_URL+MyApplication.mySharedPreference.getUserToken();

    private int serverResponseCode;

    public String uploadVideo(String file, List<KeyValue> param) {

        String fileName = file;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        Log.e("Huzza", "FileName: " + file);
        String filePath = file.contains("/mnt/media_rw/") ? file.replace("/mnt/media_rw/", "/storage/") : file;
        Log.e("Huzza replace", "FileName: " + filePath);
        File sourceFile = new File(filePath);
        if (!sourceFile.exists()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(UPLOAD_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("filedata", fileName);

            dos = new DataOutputStream(conn.getOutputStream());


            for (KeyValue keyValue : param) {
                L.l("PARAM 0 POSITON " + keyValue.getKey() + "=" + keyValue.getValue());
                try {
                    keyValue.setValue(URLEncoder.encode(keyValue.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    keyValue.setValue(keyValue.getValue());
                }

                dos.writeBytes("Content-Disposition: form-data; name=" + keyValue.getKey() + "" + lineEnd);
                //dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                //dos.writeBytes("Content-Length: " + name.length() + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(keyValue.getValue()); // mobile_no is String variable
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
            }
            dos.writeBytes("Content-Disposition: form-data; name=\"document_name\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i("Huzza", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            Log.e("_++_+_++_+",sb.toString());
            return sb.toString();
        } else {
            return "Could not upload";
        }
    }
}
