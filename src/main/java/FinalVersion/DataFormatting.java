package FinalVersion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * DataFormatting provides methods to format JSON responses from the GitHub API.
 */
public class DataFormatting {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Formats the JSON response from the GitHub API to extract the list of changed files.
     *
     * @param json JSON response from the GitHub API
     * @return List of changed files
     */
    public List<String> formatJson(String json) {
        List<String> files = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode filesNode = rootNode.path("files");
            for (JsonNode node : filesNode) {
                files.add(node.path("filename").asText());
            }
            return files;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while processing json!", e);
        }
    }
}