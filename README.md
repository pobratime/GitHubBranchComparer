# GitHub branch comparer

This project is developed as part of the JetBrains internship application process.
## Project Overview

This library is designed to find all files with the same path that were changed in both `branchA` (remotely) and `branchB` (locally) independently since the merge base commit.

### Usage

1. Clone this repository to your local machine.
2. Update the `Example` class with your GitHub repository details and access token.
3. Run the `Example` class to find the files changed in both `branchA` and `branchB` since the merge base commit.


### Testing

The library includes tests to ensure the correctness of the implemented functionalities. Make sure to run the tests using `mvn test`.
### Disclaimer for testing
If `mvn test` doesn't work or outputs an error try changing these lines:
```xml
    <maven.compiler.source>23</maven.compiler.source>
    <maven.compiler.source>23</maven.compiler.source>
```
so that they use a different version, if you dont have jdk 23.

