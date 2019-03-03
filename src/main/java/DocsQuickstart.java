import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocsQuickstart {
    private static final String APPLICATION_NAME = "Google Docs API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String DOCUMENT_ID = "YOUT_DOCUMENT_ID";
    private static final List<String> SCOPES = Collections.singletonList(DocsScopes.DOCUMENTS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DocsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receier = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receier).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Docs service = new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // https://docs.google.com/document/d/195j9eDD3ccgjQRttHhJPymLJUCOUjs-jmwTrekvdjFE/edit
        Document response = service.documents().get(DOCUMENT_ID).execute();
        String title = response.getTitle();
        System.out.printf("The title of the doc is: %s\n", title);
        //////// FORMAT TEXT
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setUpdateTextStyle(new UpdateTextStyleRequest()
                .setTextStyle(new TextStyle()
                        .setBold(true)
                        .setItalic(true))
                .setRange(new Range()
                        .setStartIndex(1)
                        .setEndIndex(5))
                .setFields("bold")));
        requests.add(new Request()
                .setUpdateTextStyle(new UpdateTextStyleRequest()
                        .setRange(new Range()
                                .setStartIndex(6)
                                .setEndIndex(10))
                        .setTextStyle(new TextStyle()
                                .setWeightedFontFamily(new WeightedFontFamily()
                                        .setFontFamily("Times New Roman"))
                                .setFontSize(new Dimension()
                                        .setMagnitude(14.0)
                                        .setUnit("PT"))
                                .setForegroundColor(new OptionalColor()
                                        .setColor(new Color().setRgbColor(new RgbColor()
                                                .setBlue(1.0F)
                                                .setGreen(0.0F)
                                                .setRed(0.0F)))))
                        .setFields("foregroundColor,weightedFontFamily,fontSize")));
        requests.add(new Request()
                .setUpdateTextStyle(new UpdateTextStyleRequest()
                        .setRange(new Range()
                                .setStartIndex(11)
                                .setEndIndex(15))
                        .setTextStyle(new TextStyle()
                                .setLink(new Link()
                                        .setUrl("www.example.com")))
                        .setFields("link")));
        BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);
        BatchUpdateDocumentResponse responseFormatText = service.documents()
                .batchUpdate(DOCUMENT_ID, body).execute();
    }
}