package FinalVersion;

import FinalVersion.Exceptions.ApiRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitHubApiClientTest {

    private GitHubApiClient client;
    private HttpClient mockHttpClient;
    private HttpResponse<String> mockHttpResponse;

    @BeforeEach
    public void setUp() {
        client = new GitHubApiClient();
        mockHttpClient = mock(HttpClient.class);
        mockHttpResponse = mock(HttpResponse.class);
    }

    @Test
    public void test_getResponse_emptyToken() {
        assertThrows(ApiRequestException.class, () -> client.getResponse(URI.create("https://api.github.com"), ""));
    }

    @Test
    public void test_getResponse_httpError() throws Exception {
        MockedStatic<HttpClient> httpClientMock = Mockito.mockStatic(HttpClient.class);
        httpClientMock.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

        when(mockHttpResponse.statusCode()).thenReturn(401);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        assertThrows(ApiRequestException.class, () -> client.getResponse(new URI("https://api.github.com"), "token"));
    }
}