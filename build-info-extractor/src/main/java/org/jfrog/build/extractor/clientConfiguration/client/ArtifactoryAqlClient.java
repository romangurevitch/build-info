package org.jfrog.build.extractor.clientConfiguration.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.type.TypeReference;
import org.jfrog.build.api.search.AqlSearchResult;
import org.jfrog.build.api.util.Log;
import org.jfrog.build.client.PreemptiveHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tamirh on 21/04/2016.
 */
public class ArtifactoryAqlClient extends ArtifactoryBaseClient {

    public ArtifactoryAqlClient(String artifactoryUrl, String username, String password, Log log) {
        super(artifactoryUrl, username, password, log);
    }

    public AqlSearchResult search(String aql) throws IOException {
        PreemptiveHttpClient client = httpClient.getHttpClient();

        String url = artifactoryUrl + "/api/search/aql";
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(aql);
        httpPost.setEntity(entity);
        AqlSearchResult result = readResponse(client.execute(httpPost),
                new TypeReference<AqlSearchResult>() {
                },
                "Failed to search artifact by the aql '" + aql + "'");
        return result;
    }

    /**
     * Reads HTTP response and converts it to object of the type specified.
     *
     * @param response     response to read
     * @param valueType    response object type
     * @param errorMessage error message to throw in case of error
     * @param <T>          response object type
     * @return response object converted from HTTP Json reponse to the type specified.
     * @throws java.io.IOException if reading or converting response fails.
     */
    private <T> T readResponse(HttpResponse response, TypeReference<T> valueType, String errorMessage)
            throws IOException {

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }

            InputStream content = null;

            try {
                content = entity.getContent();
                JsonParser parser = httpClient.createJsonParser(content);
                ((ObjectMapper) parser.getCodec()).configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                // http://wiki.fasterxml.com/JacksonDataBinding
                return parser.readValueAs(valueType);
            } finally {
                if (content != null) {
                    IOUtils.closeQuietly(content);
                }
            }
        } else {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                IOUtils.closeQuietly(httpEntity.getContent());
            }
            throw new IOException(errorMessage + ": " + response.getStatusLine());
        }
    }
}
