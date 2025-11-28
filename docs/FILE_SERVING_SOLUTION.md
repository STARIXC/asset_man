# File Upload and Serving Solution for Production Tomcat

## Problem
In the development environment, logo images were loading correctly, but in the production Tomcat environment, images stored in external directories (outside the WAR file) were not accessible via the `/uploads/**` URL pattern.

## Root Cause
The original implementation used Spring's `ResourceHandlerRegistry` to map `/uploads/**` URLs to an external file system directory. While this works in embedded servers (like Spring Boot's embedded Tomcat during development), it can fail in production Tomcat deployments due to:

1. **Security restrictions**: Tomcat may restrict access to directories outside the webapp context
2. **Path resolution issues**: The `file://` URI scheme may not resolve correctly in all Tomcat configurations
3. **Context path differences**: Production Tomcat may have different context paths or deployment structures

## Solution
We replaced the resource handler approach with a **dedicated controller-based file serving mechanism**:

### 1. FileController (`FileController.java`)
- **Purpose**: Explicitly serves uploaded files via HTTP endpoints
- **Endpoint**: `GET /uploads/{filename}`
- **Features**:
  - Security validation to prevent path traversal attacks
  - Proper content type detection
  - File existence checking
  - Appropriate HTTP headers for inline display

### 2. Updated WebConfig (`WebConfig.java`)
- **Removed**: Resource handler configuration for `/uploads/**`
- **Reason**: Controller-based approach is more reliable and portable

### 3. File Storage Structure
Files are stored in OS-specific external directories:
- **Windows**: `C:\ASMAN\DO_NOT_DELETE\Documents\uploads`
- **Linux/Mac**: `/opt/HRH/DO_NOT_DELETE/Documents/uploads`
- **Solaris**: `/var/HRH/DO_NOT_DELETE/Documents/uploads`

## How It Works

### For HTML Templates (settings.html)
```html
<!-- Images are referenced using /uploads/ URL pattern -->
<img th:src="'/uploads/' + ${logoFileName}" />
```
- The browser requests: `http://yourserver.com/uploads/abc123.jpg`
- `FileController` intercepts the request
- Controller reads the file from external storage
- Returns the file with appropriate content type

### For PDF Generation (receipt.html)
```html
<!-- Images use file:// URIs for Flying Saucer PDF renderer -->
<img th:src="${receipt.logoMain}" />
```
- `EnhancedPdfService.getLogoPath()` returns: `file:///C:/ASMAN/.../abc123.jpg`
- Flying Saucer PDF renderer reads directly from file system
- No HTTP request needed

## Benefits

1. **Production Compatibility**: Works reliably in both embedded and standalone Tomcat
2. **Security**: Built-in path traversal protection
3. **Debugging**: Easier to log and troubleshoot file serving issues
4. **Flexibility**: Can add caching, compression, or other features easily
5. **Consistency**: Same behavior across development and production environments

## Testing

### Development Environment
1. Upload a logo via Settings page
2. Verify image displays in Settings page preview
3. Generate a PDF receipt and verify logos appear

### Production Environment (Tomcat)
1. Deploy WAR file to Tomcat
2. Ensure external storage directory exists and is writable
3. Upload logos via Settings page
4. Verify:
   - Images display in Settings page (`/uploads/` URLs work)
   - PDFs generate correctly with logos (`file://` URIs work)

## Troubleshooting

### Images not displaying in Settings page
- Check Tomcat logs for FileController errors
- Verify file exists in storage directory
- Check file permissions (Tomcat user must have read access)
- Verify URL: `http://yourserver.com/uploads/filename.jpg`

### Images not appearing in PDFs
- Check that files exist in storage directory
- Verify `getLogoPath()` returns valid `file://` URI
- Check Flying Saucer has file system access permissions

### Permission Issues
```bash
# Linux/Mac - ensure Tomcat user can read files
sudo chown -R tomcat:tomcat /opt/HRH/DO_NOT_DELETE/Documents/uploads
sudo chmod -R 755 /opt/HRH/DO_NOT_DELETE/Documents/uploads
```

## Files Modified

1. **Created**: `FileController.java` - New controller for serving uploaded files
2. **Modified**: `WebConfig.java` - Removed resource handler configuration
3. **Unchanged**: 
   - `FileStorageService.java` - Still handles file storage/deletion
   - `SettingService.java` - Still manages logo settings
   - `EnhancedPdfService.java` - Still uses file:// URIs for PDFs
   - `settings.html` - Still uses /uploads/ URLs for display

## Migration Notes

If upgrading from the old resource handler approach:
1. No database changes needed
2. No file migration needed
3. Simply deploy new code
4. Existing uploaded files will work immediately
5. No configuration changes required
