package org.ndexbio.enrichment.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.io.InputStream;
import org.ndexbio.enrichment.rest.model.DatabaseResults;
import org.ndexbio.enrichment.rest.model.EnrichmentQuery;
import org.ndexbio.enrichment.rest.model.EnrichmentQueryResults;
import org.ndexbio.enrichment.rest.model.EnrichmentQueryStatus;
import org.ndexbio.enrichment.rest.model.Task;
import org.ndexbio.enrichment.rest.model.exceptions.EnrichmentException;

/**
 * Enrichment REST client
 * @author churas
 */
public class EnrichmentRestClientImpl implements EnrichmentRestClient {
    
    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCEPT = "accept";
    public static final String CONTENT_TYPE = "Content-Type";
    private String _restEndPoint;
    private String _userAgent = "EnrichmentRestClient/0.1.0";
    private static boolean _jacksonConfigured = false;

    public EnrichmentRestClientImpl(final String restEndPoint, final String userAgent) {
        initializeJackson();
        if (userAgent != null){
            _userAgent = _userAgent + " " + userAgent;
        }
        if (restEndPoint == null){
            throw new IllegalArgumentException("restEndPoint cannot be null");
        } else if (restEndPoint.substring(restEndPoint.length() - 1).equals("/")) {
        	_restEndPoint = restEndPoint.substring(0, restEndPoint.length() - 1);
        } else {
        _restEndPoint = restEndPoint;
        }
    }
    
    /**
     * Configures Jackson to perform serialization/deserialization of json
     * to objects via unirest.
     */
    private synchronized void initializeJackson(){
        if (_jacksonConfigured == true){
            return;
        }
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                        = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    private String getQueryPostEndPoint() throws EnrichmentException {
        return _restEndPoint;
    }
    /**
     * Submits query for processing
     * @param query query to process
     * @return UUID as a string that is an identifier for query
     */
    @Override
    public String query(EnrichmentQuery query) throws EnrichmentException {
        if (query == null){
            throw new EnrichmentException("query cannot be null");
        }
        try {
            HttpResponse<Task> taskRes = Unirest.post(getQueryPostEndPoint())
                    .header(ACCEPT, APPLICATION_JSON)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .body(query).asObject(Task.class);
            Task task = taskRes.getBody();
            return task.getId();
        } catch(UnirestException ex){
            throw new EnrichmentException("Caught an exception: " + ex.getMessage());
        }
    }
    
    private String getSummaryOfDatabasesEndPoint() throws EnrichmentException {
        return _restEndPoint + "/database";
    }
    
    /**
     * Gets a summary of databases in engine
     * @return DatabaseResults object
     * @throws EnrichmentException if there is an error
     */
    @Override
    public DatabaseResults getDatabaseResults() throws EnrichmentException{
        try {
            HttpResponse<DatabaseResults> dbRes = Unirest.get(getSummaryOfDatabasesEndPoint())
                    .header(ACCEPT, APPLICATION_JSON)
                    .asObject(DatabaseResults.class);
            return dbRes.getBody();
        } catch(UnirestException ex){
            throw new EnrichmentException("Caught an exception: " + ex.getMessage());
        }
    }
    
    private String getQueryResultsEndPoint(final String id) throws EnrichmentException {
        return _restEndPoint + "/" + id;
    }
    /**
     * Gets query results
     * @param id
     * @param start
     * @param size
     * @return
     * @throws EnrichmentException  if there is an error
     */
    @Override
    public EnrichmentQueryResults getQueryResults(final String id, int start, int size) throws EnrichmentException{
        if (id == null){
            throw new IllegalArgumentException("id cannot be null");
        }
        try {
            HttpResponse<EnrichmentQueryResults> dbRes = Unirest.get(getQueryResultsEndPoint(id))
                    .header(ACCEPT, APPLICATION_JSON)
                    .queryString("start", Integer.toString(start))
                    .queryString("size", Integer.toString(size))
                    .asObject(EnrichmentQueryResults.class);
            return dbRes.getBody();
        } catch(UnirestException ex){
            throw new EnrichmentException("Caught an exception: " + ex.getMessage());
        }
    }
    
    private String getQueryStatusEndPoint(final String id) throws EnrichmentException {
        return _restEndPoint + "/" + id + "/status";
    }

    /**
     * Gets query status
     * @param id
     * @return
     * @throws EnrichmentException if there is an error
     */
    @Override
    public EnrichmentQueryStatus getQueryStatus(final String id) throws EnrichmentException {
        if (id == null){
            throw new IllegalArgumentException("id cannot be null");
        }
        try {
            HttpResponse<EnrichmentQueryStatus> dbRes = Unirest.get(getQueryStatusEndPoint(id))
                    .header(ACCEPT, APPLICATION_JSON)
                    .asObject(EnrichmentQueryStatus.class);
            return dbRes.getBody();
        } catch(UnirestException ex){
            throw new EnrichmentException("Caught an exception: " + ex.getMessage());
        }
    }
    
    private String getDeleteEndPoint(final String id) throws EnrichmentException {
        return _restEndPoint + "/" + id;
    }
    /**
     * Deletes query
     * @param id
     * @throws EnrichmentException if there is an error
     */
    @Override
    public void delete(final String id) throws EnrichmentException {
        if (id == null){
            throw new IllegalArgumentException("id cannot be null");
        }
        try {
            HttpResponse<JsonNode> dbRes = Unirest.delete(getDeleteEndPoint(id)).asJson();
            return;
        } catch(UnirestException ex){
            throw new EnrichmentException("Caught an exception: " + ex.getMessage());
        }
    }
    private String getNetworkOverlayEndPoint(final String id) throws EnrichmentException {
        return _restEndPoint + "/" + id + "/overlaynetwork";
    }
    /**
     * Gets a network as CX
     * @param id
     * @param databaseUUID
     * @param networkUUID
     * @return
     * @throws EnrichmentException 
     */
    @Override
    public InputStream getNetworkOverlayAsCX(final String id, final String databaseUUID, final String networkUUID) throws EnrichmentException{
        if (id == null){
            throw new IllegalArgumentException("id cannot be null");
        }
        try {
            HttpResponse<JsonNode> dbRes = Unirest.get(getNetworkOverlayEndPoint(id))
                    .header(ACCEPT, APPLICATION_JSON)
                    .queryString("databaseUUID", databaseUUID)
                    .queryString("networkUUID", networkUUID)
                    .asJson();
            return dbRes.getRawBody();
        } catch(UnirestException ex){
            throw new EnrichmentException("Caught an exception: " + ex.getMessage());
        }
    }

    @Override
    public void shutdown() throws EnrichmentException {
        try {
            Unirest.shutdown();
        }catch(IOException io){
            throw new EnrichmentException("Caught exception trying to shut unirest down: " + io.getMessage());
        }
    }

    
    
}
