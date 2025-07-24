# Resume Enhancement Service

A Spring Boot application that enhances resumes based on job descriptions using OpenAI's GPT models.

## Features

- Upload your resume and job description
- Get an enhanced resume tailored to the job description
- Customizable enhancement instructions
- RESTful API for easy integration

## Prerequisites

- Java 17 or higher
- Gradle 7.6 or higher
- OpenAI API key

## Setup

1. Clone the repository
2. Set your OpenAI API key as an environment variable:
   ```bash
   export OPENAI_API_KEY='your-openai-api-key'
   ```
3. Build the project:
   ```bash
   ./gradlew build
   ```
4. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### Enhance Resume

Enhance a resume based on a job description.

- **URL**: `/api/resume/enhance`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "originalResume": "Your resume text here...",
    "jobDescription": "Job description text here...",
    "enhancementInstructions": "Any specific instructions for enhancement (optional)"
  }
  ```
- **Success Response**:
  - **Code**: 200
  - **Content**:
    ```json
    {
      "enhancedResume": "Enhanced resume text...",
      "summaryOfChanges": "Summary of changes made...",
      "status": "SUCCESS"
    }
    ```

## Configuration

Configuration can be modified in `src/main/resources/application.properties`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
