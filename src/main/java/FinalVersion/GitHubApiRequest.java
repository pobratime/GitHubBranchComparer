package FinalVersion;

import FinalVersion.Exceptions.ApiRequestException;
import FinalVersion.Exceptions.GitCommandException;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * GitHubApiRequest provides methods to find files changed in both remote branchA
 * and local branchB since their merge base commit using GitHub API and local git commands.
 */
public class GitHubApiRequest {

    private final GitHubApiClient gitApiClient = new GitHubApiClient();
    private final GitHelper gitHelper = new GitHelper();
    private final DataFormatting dataForm = new DataFormatting();

    /**
     * Finds files changed in both remote branchA and local branchB since their merge base commit.
     *
     * @param owner       GitHub repository owner
     * @param repo        GitHub repository name
     * @param localPath   Local path to the repository
     * @param accessToken GitHub access token for authentication
     * @param branchA     Remote branch name
     * @param branchB     Local branch name
     * @return List of common changed files in both branches
     */
    public List<String> findChangedFiles(String owner, String repo, String localPath, String accessToken, String branchA, String branchB) {
        List<String> files = new ArrayList<>();
        gitHelper.setLocalPath(localPath);

        // Find the merge base commit of branchA and branchB
        String mergeBaseCommit;
        try {
            mergeBaseCommit = gitHelper.executeMergeBaseCommit(branchA, branchB);
        } catch (Exception e) {
            throw new GitCommandException("Failed to find merge base commit!", e);
        }

        // Find files changed in remote branchA
        List<String> findChangedBranchA;
        try {
            findChangedBranchA = findFilesBranchA(branchA, mergeBaseCommit, owner, repo, accessToken);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to find files changed in branchA!", e);
        }

        // Find files changed in local branchB
        List<String> findChangedBranchB;
        try {
            findChangedBranchB = findFilesBranchB(branchB, mergeBaseCommit);
        } catch (Exception e) {
            throw new GitCommandException("Failed to find files changed in branchB!", e);
        }

        // Find common changed files in both branches
        for (String fileA : findChangedBranchA) {
            if (findChangedBranchB.contains(fileA)) {
                files.add(fileA);
            }
        }

        return files;
    }

    /**
     * Finds files changed in remote branchA since the merge base commit.
     *
     * @param branchA         Remote branch name
     * @param mergeBaseCommit Merge base commit of branchA and branchB
     * @param owner           GitHub repository owner
     * @param repo            GitHub repository name
     * @param accessToken     GitHub access token for authentication
     * @return List of files changed in branchA
     */
    protected List<String> findFilesBranchA(String branchA, String mergeBaseCommit, String owner, String repo, String accessToken) {
        // Create URI
        String uriString = String.format("https://api.github.com/repos/%s/%s/compare/%s...%s", owner, repo, mergeBaseCommit, branchA);
        URI uri = URI.create(uriString);
        // Request a response
        HttpResponse<String> response = gitApiClient.getResponse(uri, accessToken);
        // Format the response and return it
        return dataForm.formatJson(response.body());
    }

    /**
     * Finds files changed in local branchB since the merge base commit.
     *
     * @param branchB         Local branch name
     * @param mergeBaseCommit Merge base commit of branchA and branchB
     * @return List of files changed in branchB
     */
    protected List<String> findFilesBranchB(String branchB, String mergeBaseCommit) {
        return gitHelper.executeGitDiff(branchB, mergeBaseCommit);
    }
}