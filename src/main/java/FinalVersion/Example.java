package FinalVersion;

public class Example {
    public static void main(String[] args) {
        String owner = ""; // "insert_repo_owner";
        String repo = ""; // "insert_repo";
        String localPath = ""; // "insert_local_path";
        String branchA = ""; // "insert_branch_A";
        String branchB = ""; // "insert_branchB";
        String token = ""; // "insert_your_token";

        GitHubApiRequest gh = new GitHubApiRequest();
        System.out.println(gh.findChangedFiles(owner, repo, localPath, token, branchA, branchB));
    }
}
