# Firebase Setup Guide

## Current Status ✅

Your project is **already configured** with Firebase! The `google-services.json` file is included and Firebase Firestore is ready to use.

## Firebase Project Details

- **Project ID**: `dahondetector`
- **Project Number**: `200024254832`
- **Package Name**: `com.example.dahondetector`
- **Storage Bucket**: `dahondetector.firebasestorage.app`

## What's Configured

### Firebase Services Used

1. **Firestore Database**
   - Stores chat logs
   - Real-time synchronization
   - Automatic persistence

2. **Firebase Android SDK**
   - Integrated via `google-services.json`
   - Auto-configured authentication

## Firestore Data Structure

### Collections

#### `chatLogs` Collection
Each document contains:
```kotlin
{
  "id": String,              // Auto-generated document ID
  "plantName": String,       // Detected plant name
  "plantImageUri": String,   // Local path to image
  "confidence": Float,       // Detection confidence (0-1)
  "userQuery": String,       // User's question
  "aiResponse": String,      // Gemini's response
  "timestamp": Long          // Unix timestamp in milliseconds
}
```

### Example Document
```json
{
  "id": "abc123xyz",
  "plantName": "Monstera Deliciosa",
  "plantImageUri": "/storage/emulated/0/...",
  "confidence": 0.95,
  "userQuery": "How often should I water this plant?",
  "aiResponse": "Monstera plants prefer to dry out slightly between waterings...",
  "timestamp": 1700000000000
}
```

## Firebase Console Access

### Accessing Your Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Sign in with your Google account
3. Select the **"dahondetector"** project

### What You Can Do
- View all chat logs in real-time
- Monitor database usage
- Set up security rules
- View analytics
- Manage users (if authentication added)

## Security Rules

### Current Rules (Recommended Setup)

For development/testing, use these rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read/write to chatLogs for all users
    match /chatLogs/{document=**} {
      allow read, write: if true;
    }
  }
}
```

⚠️ **Warning**: These rules allow anyone to read/write. Good for testing, but not for production!

### Production Rules (Recommended)

For production, implement authentication:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Require authentication
    match /chatLogs/{chatLogId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null 
                            && request.auth.uid == resource.data.userId;
    }
  }
}
```

### Setting Security Rules
1. Go to **Firestore Database** in Firebase Console
2. Click **Rules** tab
3. Edit the rules
4. Click **Publish**

## Adding Authentication (Optional)

### Why Add Authentication?
- Secure user data
- Separate chat logs per user
- Prevent abuse
- Better security rules

### Steps to Add Firebase Auth

1. **Enable Authentication**
   ```bash
   # Add to app/build.gradle.kts
   implementation("com.google.firebase:firebase-auth-ktx")
   ```

2. **Enable Sign-In Methods** in Firebase Console
   - Email/Password
   - Google Sign-In
   - Anonymous Auth (easiest)

3. **Update Data Model**
   ```kotlin
   data class ChatLog(
       // ... existing fields ...
       val userId: String = "",  // Add user ID
   )
   ```

4. **Implement Sign-In**
   ```kotlin
   // Example: Anonymous sign-in
   FirebaseAuth.getInstance()
       .signInAnonymously()
       .addOnSuccessListener { result ->
           // User signed in
           val userId = result.user?.uid
       }
   ```

## Database Operations

### What the App Does

#### 1. Save Chat Log
```kotlin
chatLogRepository.saveChatLog(chatLog)
```
- Creates a new document in `chatLogs`
- Auto-generates document ID
- Uploads to Firebase

#### 2. Load Chat Logs
```kotlin
chatLogRepository.getChatLogs()
```
- Retrieves all chat logs
- Orders by timestamp (newest first)
- Real-time updates via Flow

#### 3. Delete Chat Log
```kotlin
chatLogRepository.deleteChatLog(id)
```
- Deletes a specific document
- Immediate removal from UI

## Monitoring Usage

### Database Usage
1. Go to **Firestore Database** in Firebase Console
2. View **Usage** tab
3. Monitor:
   - Document reads
   - Document writes
   - Document deletes
   - Storage used

### Quotas (Free Spark Plan)
- **Stored data**: 1 GB
- **Document reads**: 50,000/day
- **Document writes**: 20,000/day
- **Document deletes**: 20,000/day
- **Network egress**: 10 GB/month

### Upgrading
If you exceed limits:
1. Go to **Usage and billing** in Firebase Console
2. Click **Upgrade project**
3. Choose **Blaze (pay-as-you-go)** plan
4. Add payment method

## Offline Support

### Built-in Offline Persistence
Firebase automatically:
- Caches data locally
- Syncs when online
- Handles conflicts

### How It Works
```kotlin
// Firestore automatically handles offline mode
// No additional code needed!
```

## Troubleshooting

### Chat Logs Not Saving
1. **Check internet connection**
2. **Verify google-services.json** is in `app/` folder
3. **Check Firebase Console** - Rules tab
4. **Look for errors** in Android Logcat

### Chat Logs Not Loading
1. **Verify Firestore is enabled** in Firebase Console
2. **Check security rules** allow read access
3. **Ensure collection name** is exactly `chatLogs`
4. **Check for network errors** in logs

### Google Services Error
```
Error: File google-services.json is missing
```
**Solution**: 
- Ensure `google-services.json` is in the `app/` directory
- Sync Gradle files
- Rebuild project

### Firestore Rules Error
```
PERMISSION_DENIED: Missing or insufficient permissions
```
**Solution**:
- Go to Firebase Console > Firestore > Rules
- Update rules to allow access
- Publish changes

## Data Privacy

### What's Stored in Firebase
- ✅ Plant names
- ✅ User questions
- ✅ AI responses
- ✅ Timestamps
- ✅ Local image paths (not actual images)

### What's NOT Stored
- ❌ Plant images (stored locally only)
- ❌ User personal information
- ❌ Gemini API keys
- ❌ Device information

### GDPR Compliance
If deploying in EU:
1. Add user consent dialogs
2. Implement data export
3. Implement data deletion
4. Update privacy policy

## Advanced Features

### Real-time Listeners
Already implemented via Flow:
```kotlin
chatLogRepository.getChatLogs().collect { logs ->
    // Automatically updates when data changes
}
```

### Querying Data
Modify `ChatLogRepository.kt` to add filters:
```kotlin
fun getChatLogsByPlant(plantName: String): Flow<List<ChatLog>> {
    return callbackFlow {
        val subscription = chatLogsCollection
            .whereEqualTo("plantName", plantName)
            .addSnapshotListener { ... }
        // ...
    }
}
```

### Pagination
For better performance with many logs:
```kotlin
fun getChatLogsPaginated(limit: Int): Query {
    return chatLogsCollection
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .limit(limit.toLong())
}
```

## Backup and Export

### Manual Backup
1. Go to **Firestore Database** in Console
2. Click **Import/Export**
3. Choose **Export**
4. Select collections
5. Choose Google Cloud Storage bucket

### Automated Backups
Set up scheduled exports:
1. Go to Google Cloud Console
2. Set up Cloud Scheduler
3. Configure Firestore export task
4. Set schedule (daily/weekly)

## Performance Optimization

### Best Practices
1. **Index frequently queried fields**
2. **Limit document size** (max 1 MB)
3. **Use pagination** for large lists
4. **Avoid deeply nested data**
5. **Batch writes** when possible

### Indexes
Firebase auto-creates basic indexes. For complex queries:
1. Go to **Firestore > Indexes**
2. Create composite indexes
3. Follow console recommendations

## Cost Optimization

### Tips to Reduce Costs
1. **Enable offline persistence** (already done)
2. **Cache UI data** in memory
3. **Avoid unnecessary reads**
4. **Delete old chat logs** periodically
5. **Monitor usage** regularly

### Estimated Costs (Blaze Plan)
- **Documents < 100K/day**: Likely free
- **Storage < 1 GB**: Free
- **Network < 10 GB/month**: Free

Most users will stay within free limits!

## Testing

### Test Your Firebase Connection
1. Launch the app
2. Scan a plant
3. Ask a question
4. Check Firebase Console
5. Verify document in `chatLogs` collection

### Clear Test Data
In Firebase Console:
1. Go to **Firestore Database**
2. Select `chatLogs` collection
3. Delete test documents
4. Or delete entire collection

## Resources

### Official Documentation
- [Firebase Docs](https://firebase.google.com/docs)
- [Firestore Docs](https://firebase.google.com/docs/firestore)
- [Android Setup Guide](https://firebase.google.com/docs/android/setup)

### Video Tutorials
- [Firebase for Android](https://www.youtube.com/watch?v=sw2NP8SuZlE)
- [Firestore Tutorial](https://www.youtube.com/watch?v=v_hR4K4auoQ)

### Community Support
- [Stack Overflow](https://stackoverflow.com/questions/tagged/firebase)
- [Firebase Community](https://firebase.google.com/community)

---

**Your Firebase setup is complete and ready to use!** 🎉

For app usage, see [README.md](README.md)

