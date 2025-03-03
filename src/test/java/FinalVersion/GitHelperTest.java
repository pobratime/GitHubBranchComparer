package FinalVersion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GitHelperTest {

    private GitHelper gh;
    private Path repoDir;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) throws Exception {
        repoDir = tempDir;
        gh = new GitHelper();
        gh.setLocalPath(repoDir.toString());

        runCommand("git", "init", "-b", "main");
        runCommand("git", "config", "user.name", "Test User");
        runCommand("git", "config", "user.email", "test@example.com");

        Files.write(repoDir.resolve("file1.txt"), "Content".getBytes());
        runCommand("git", "add", ".");
        runCommand("git", "commit", "-m", "Initial commit");

        runCommand("git", "update-ref", "refs/remotes/origin/main", "main");

        runCommand("git", "checkout", "-b", "branchA");
        Files.write(repoDir.resolve("file2.txt"), "Content".getBytes());
        runCommand("git", "add", ".");
        runCommand("git", "commit", "-m", "Commit on branchA");

        runCommand("git", "checkout", "main");
        runCommand("git", "checkout", "-b", "branchB");
        Files.write(repoDir.resolve("file3.txt"), "Content".getBytes());
        runCommand("git", "add", ".");
        runCommand("git", "commit", "-m", "Commit on branchB");
    }

    private List<String> runCommand(String... command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(repoDir.toFile());
        Process p = pb.start();
        List<String> output = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        }
        p.waitFor();
        return output;
    }

    @Test
    public void test_executeMergeBaseCommit_allValid() throws Exception {
        String mergeBase = gh.executeMergeBaseCommit("main", "branchB");
        String mainCommit = runCommand("git", "rev-parse", "main").getFirst();
        assertEquals(mainCommit, mergeBase);
    }

    @Test
    public void test_executeMergeBaseCommit_invalidBranch() {
        assertThrows(RuntimeException.class, () -> gh.executeMergeBaseCommit("nonexistent", "branchB"));
    }

    @Test
    public void test_executeGitDiff_validInput() throws Exception {
        String mainCommit = runCommand("git", "rev-parse", "main").getFirst();
        List<String> diffFiles = gh.executeGitDiff("branchB", mainCommit);
        assertTrue(diffFiles.contains("file3.txt"));
    }

    @Test
    public void test_executeGitDiff_invalidCommit() {
        assertThrows(RuntimeException.class, () -> gh.executeGitDiff("branchB", "invalidCommit"));
    }
}