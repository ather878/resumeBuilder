# Resume Enhancement API - PDF Upload Endpoint

## Enhance Resume (PDF Upload)

Enhance a resume by uploading a PDF file.

### Request

```http
POST /api/resume/enhance/upload
Content-Type: multipart/form-data
```

### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| resume | File | Yes | PDF file containing the resume |
| jobDescription | String | Yes | The job description to tailor the resume to |
| instructions | String | No | Specific instructions for enhancement |

### Example using cURL

```bash
curl -X POST 'http://localhost:8080/api/resume/enhance/upload' \
  -H 'Content-Type: multipart/form-data' \
  -F 'resume=@/path/to/your/resume.pdf' \
  -F 'jobDescription=Senior Software Engineer position requiring 5+ years of Java experience...' \
  -F 'instructions=Emphasize Java and Spring experience'
```

### Success Response

**Status Code:** `200 OK`

```json
{
  "enhancedResume": "[Enhanced resume text...]",
  "summaryOfChanges": "Updated work experience to highlight Java and Spring...",
  "status": "SUCCESS"
}
```

### Error Responses

**Status Code:** `400 Bad Request`
- Missing required file
- Invalid file type (non-PDF)
- Missing job description

**Status Code:** `500 Internal Server Error`
- Error processing the PDF file
- Error calling the AI service

### Notes
- Maximum file size: 10MB
- Only PDF files are supported
- The enhanced resume is returned as plain text
