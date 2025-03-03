package FinalVersion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * GitHelper provides methods to execute local git commands.
 */
public class GitHelper {

    private static final ProcessBuilder PB = new ProcessBuilder();
    private String localPath;

    /**
     * Sets the local path to the repository.
     *
     * @param localPath Local path to the repository
     */
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    /**
     * Executes the git merge-base command to find the merge base commit of branchA and branchB.
     *
     * @param branchA Branch A name
     * @param branchB Branch B name
     * @return Merge base commit hash
     */
    public String executeMergeBaseCommit(String branchA, String branchB) {
        PB.command("git", "merge-base", "origin/" + branchA, branchB);
        PB.directory(Path.of(localPath).toFile());

        try {
            Process p = PB.start();
            List<String> output = returnCommandOutput(p);
            if (output.isEmpty()) {
                throw new RuntimeException("No output received from command -> git merge-base!");
            }
            return output.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error while executing command -> git merge-base!", e);
        }
    }

    /**
     * Executes the git diff command to find files changed in branchB since the merge base commit.
     *
     * @param branchB         Branch B name
     * @param mergeBaseCommit Merge base commit hash
     * @return List of changed files
     */
    public List<String> executeGitDiff(String branchB, String mergeBaseCommit) {
        PB.command("git", "diff", "--name-only", mergeBaseCommit, branchB);
        PB.directory(Path.of(localPath).toFile());
        try {
            Process p = PB.start();
            List<String> output = returnCommandOutput(p);
            if (output.isEmpty()) {
                throw new RuntimeException("No output received from command -> git diff!");
            }
            return output;
        } catch (Exception e) {
            throw new RuntimeException("Error while executing command -> git diff!", e);
        }
    }

    /**
     * Returns the output of a command executed by the ProcessBuilder.
     *
     * @param p Process object
     * @return List of output lines
     */
    public List<String> returnCommandOutput(Process p) {
        List<String> outputStream = new ArrayList<>();
        try (InputStream inputStream = p.getInputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                outputStream.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while reading command output!", e);
        }
        return outputStream;
    }
}