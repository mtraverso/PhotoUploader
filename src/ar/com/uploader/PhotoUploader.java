
/*
    Copyright 2015 Mark Otway
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package ar.com.uploader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.mediarss.MediaContent;
import com.google.gdata.data.photos.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ParseException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.XmlBlob;

import java.io.*;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * This is a simple client that provides high-level operations on the Picasa Web
 * Albums GData API. It can also be used as a command-line application to test
 * out some of the features of the API.
 */
public class PhotoUploader {
    private static final String SYNC_CLIENT_NAME = "com.matias.uploader";
    private static final int CONNECTION_TIMEOUT_SECS = 10;

    private static final String API_PREFIX
            = "https://picasaweb.google.com/data/feed/api/user/";

    private final PicasawebService service = new PicasawebService(SYNC_CLIENT_NAME);
    ;

    /**
     * Constructs a new un-authenticated client.
     */
    public PhotoUploader(Credential credential) {

        service.setOAuth2Credentials(credential);
        service.setConnectTimeout(1000 * CONNECTION_TIMEOUT_SECS);
        service.setReadTimeout(1000 * CONNECTION_TIMEOUT_SECS);
    }

    /**
     * Constructs a new client with the given username and password.
     */
    public PhotoUploader(String uname, String passwd) {


        if (uname != null && passwd != null) {
            try {
                service.setUserCredentials(uname, passwd);
            } catch (AuthenticationException e) {
                throw new IllegalArgumentException(
                        "Authentication failed. Illegal username/password combination.");
            }
        } else {
            throw new IllegalArgumentException(
                    "Authentication failed. User/pass not set.");
        }
    }

    public void upload(String filename) throws IOException, ServiceException, java.text.ParseException {
        URL albumPostUrl = new URL("https://picasaweb.google.com/data/feed/api/user/matias.traverso/");

        PhotoEntry myPhoto = new PhotoEntry();
        myPhoto.setTitle(new PlainTextConstruct(filename.substring(filename.lastIndexOf('/')+1,filename.length())));
        myPhoto.setDescription(new PlainTextConstruct(filename.substring(filename.lastIndexOf('/')+1,filename.length())+"_uploaded"));
        myPhoto.setClient("matias.traverso");

        MediaFileSource myMedia = new MediaFileSource(new File(filename), "image/jpeg");
        myPhoto.setMediaSource(myMedia);
        myMedia.getLastModified().setDateOnly(true);
        myPhoto.setTimestamp(new SimpleDateFormat("yyyy-MM-dd").parse(myMedia.getLastModified().toString()));
        PhotoEntry returnedPhoto = service.insert(albumPostUrl, myPhoto);
    }

    public static void main(String[] args) throws IOException, ServiceException,  java.text.ParseException {
        Credential credential = OAuth2Auth.auth();

        PhotoUploader uploader = new PhotoUploader(credential);
        uploader.upload("/Users/matias/Downloads/AA2E9E92-9EDB-4493-B1EB-2C4D7529EFAB.jpg");
    }
}