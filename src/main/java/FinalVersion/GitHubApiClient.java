package FinalVersion;

import FinalVersion.Exceptions.ApiRequestException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * GitHubApiClient handles HTTP requests to the GitHub API.
 */
public class GitHubApiClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    /**
     * Sends an HTTP GET request to the specified URI with the provided access token.
     *
     * @param uri         URI to send the request to
     * @param accessToken GitHub access token for authentication
     * @return HttpResponse containing the response body as a string
     */
    public HttpResponse<String> getResponse(URI uri, String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new ApiRequestException("Token cannot be empty");
        }
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Authorization", "Bearer " + accessToken).build();
        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ApiRequestException("Error!");
            }
            return response;
        } catch (Exception e) {
            throw new ApiRequestException("GitHub API request failed", e);
        }
    }
}