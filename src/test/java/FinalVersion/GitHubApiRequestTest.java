package FinalVersion;

import FinalVersion.Exceptions.ApiRequestException;
import FinalVersion.Exceptions.GitCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitHubApiRequestTest {

    private GitHubApiRequest ghapr;
    private GitHelper mockGitHelper;
    private GitHubApiClient mockApiClient;
    private DataFormatting mockDataForm;
    private HttpResponse<String> mockHttpResponse;

    @BeforeEach
    public void setUp() throws Exception {
        mockGitHelper = mock(GitHelper.class);
        mockApiClient = mock(GitHubApiClient.class);
        mockDataForm = mock(DataFormatting.class);
        mockHttpResponse = mock(HttpResponse.class);

        ghapr = new GitHubApiRequest();

        when(mockHttpResponse.body()).thenReturn("{}");

        setField(ghapr, "gitHelper", mockGitHelper);
        setField(ghapr, "gitApiClient", mockApiClient);
        setField(ghapr, "dataForm", mockDataForm);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void test_findChangedFiles_allValid() {
        when(mockGitHelper.executeMergeBaseCommit(anyString(), anyString())).thenReturn("mergeBase");
        when(mockApiClient.getResponse(any(URI.class), anyString())).thenReturn(mockHttpResponse);
        when(mockDataForm.formatJson(anyString())).thenReturn(List.of("file1.txt"));
        when(mockGitHelper.executeGitDiff(anyString(), anyString())).thenReturn(List.of("file1.txt"));

        List<String> result = ghapr.findChangedFiles("owner", "repo", "path", "token", "a", "b");
        assertEquals(List.of("file1.txt"), result);
    }

    @Test
    void test_findChangedFiles_gitHelperThrows() {
        when(mockGitHelper.executeMergeBaseCommit(anyString(), anyString())).thenThrow(new RuntimeException());
        assertThrows(GitCommandException.class, () -> ghapr.findChangedFiles("o", "r", "p", "t", "a", "b"));
    }

    @Test
    void test_findChangedFiles_apiClientThrows() {
        when(mockGitHelper.executeMergeBaseCommit(anyString(), anyString())).thenReturn("mergeBase");
        when(mockApiClient.getResponse(any(), anyString())).thenThrow(new ApiRequestException("Error"));
        assertThrows(ApiRequestException.class, () -> ghapr.findChangedFiles("o", "r", "p", "t", "a", "b"));
    }
}