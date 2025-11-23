# API Configuration Guide

## Getting Your Gemini API Key

### Step 1: Visit Google AI Studio
Go to [Google AI Studio](https://makersuite.google.com/app/apikey)

### Step 2: Sign In
- Sign in with your Google account
- Accept the terms of service if prompted

### Step 3: Create API Key
1. Click on **"Get API Key"** or **"Create API Key"**
2. Select an existing Google Cloud project or create a new one
3. Click **"Create API Key"**
4. Copy the API key (it will look like: `AIzaSy...`)

### Step 4: Add to App
1. Open the Dahon Detector app
2. On first launch, tap **"Setup API Key First"**
3. Paste your API key
4. Tap **"Save API Key"**

## API Key Security

### ✅ Good Practices
- Never share your API key publicly
- Don't commit API keys to version control
- Use separate keys for development and production
- Monitor your API usage regularly

### ⚠️ API Key Storage
- The app stores your API key locally using Android DataStore
- The key is encrypted by Android's security system
- The key never leaves your device except when calling the Gemini API

## API Quotas and Limits

### Free Tier
- **60 requests per minute**
- **1,500 requests per day**
- Sufficient for personal use and testing

### Rate Limits
If you exceed the rate limit:
- You'll receive an error message
- Wait a minute before trying again
- Consider upgrading to a paid plan for higher limits

## Monitoring Usage

### Check Your Usage
1. Go to [Google AI Studio](https://makersuite.google.com/)
2. Navigate to your project
3. View **API Usage** dashboard
4. Monitor requests and errors

### Cost Estimation
- Free tier is generous for personal use
- Paid tier pricing available on Google Cloud Console
- Each plant scan = 1 API request
- Each question asked = 1 additional request

## Troubleshooting

### Invalid API Key Error
- Double-check you copied the entire key
- Ensure no extra spaces before/after the key
- Try generating a new key
- Verify the API key has Gemini API access enabled

### Quota Exceeded Error
- You've hit the daily/minute limit
- Wait for the quota to reset
- Upgrade to a paid tier
- Optimize app usage (fewer scans/questions)

### API Not Enabled Error
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project
3. Navigate to **APIs & Services > Library**
4. Search for "Generative Language API"
5. Click **Enable**

## Model Information

### Current Model: Gemini 1.5 Flash
- **Fast response times**
- **High accuracy** for plant identification
- **Cost-effective** for production use
- **Multimodal** (supports text + images)

### Alternative Models
You can modify the code to use:
- `gemini-1.5-pro` - Higher accuracy, slower, more expensive
- `gemini-1.0-pro-vision` - Previous generation

To change the model, edit `GeminiService.kt`:
```kotlin
private val model by lazy {
    GenerativeModel(
        modelName = "gemini-1.5-flash", // Change this
        apiKey = apiKey
    )
}
```

## API Features Used

### Image Analysis
- Multimodal input (image + text prompt)
- Structured response parsing
- Confidence estimation

### Text Chat
- Context-aware conversations
- Plant-specific Q&A
- Natural language responses

## Privacy & Data

### What's Sent to Google
- Plant images (during detection)
- Your questions about plants
- Plant names and descriptions (for context)

### What's NOT Sent
- Your API key is stored locally only
- Chat logs are stored in your Firebase
- No personal information is shared

### Data Retention
- Google may temporarily cache API requests
- Refer to [Google AI Terms](https://ai.google.dev/terms) for details
- Firebase data remains until you delete it

## Support Resources

### Official Documentation
- [Gemini API Docs](https://ai.google.dev/docs)
- [Google AI Studio](https://makersuite.google.com/)
- [Google Cloud Console](https://console.cloud.google.com/)

### Getting Help
- Check API status at [Google Cloud Status](https://status.cloud.google.com/)
- Review [Gemini API limits](https://ai.google.dev/pricing)
- Contact Google Cloud Support for API issues

## Best Practices for Production

### If Deploying This App

1. **Backend API Key Management**
   - Don't hardcode API keys in the app
   - Use a backend server to proxy API calls
   - Implement user authentication

2. **Rate Limiting**
   - Implement client-side rate limiting
   - Queue requests to avoid hitting limits
   - Show appropriate error messages

3. **Caching**
   - Cache plant detection results
   - Avoid duplicate API calls
   - Store common plant information locally

4. **Error Handling**
   - Gracefully handle API errors
   - Provide offline fallback
   - Log errors for monitoring

5. **Cost Optimization**
   - Compress images before sending
   - Batch requests when possible
   - Monitor and alert on unusual usage

---

**Need Help?** Check the main [README.md](README.md) for troubleshooting tips!

