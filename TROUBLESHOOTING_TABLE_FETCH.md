# Troubleshooting: Table Can't Fetch Data

## Quick Checks:

### 1. Verify Endpoint is Accessible
Test the endpoint directly in browser or Postman:
```
GET http://localhost:8080/laptop/getAll
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "manufacturer": "Dell",
    "model": "Latitude 5420",
    "serialNumber": "",
    "title": null,
    "description": null,
    ...
  }
]
```

### 2. Check Browser Console
- Open Developer Tools (F12)
- Go to Network tab
- Look for the request to `/laptop/getAll`
- Check:
  - Status code (should be 200, not 401)
  - Response body
  - Request headers

### 3. Verify Frontend is Calling Correct Endpoint
The frontend should call:
- `http://localhost:8080/laptop/getAll` OR
- `http://localhost:8080/laptop/all`

Both work and return the same data.

### 4. Check CORS
CORS is configured for:
- `http://localhost:4200` (Angular default)
- `http://localhost:3000` (React default)

If your frontend runs on a different port, add it to CORS config.

### 5. Response Format
The endpoint returns a **direct array**, not wrapped:
```json
[
  { "id": 1, "manufacturer": "...", ... },
  { "id": 2, "manufacturer": "...", ... }
]
```

NOT:
```json
{
  "message": "...",
  "data": [...]
}
```

### 6. Frontend Code Example
```typescript
// Angular Service
getAllLaptops(): Observable<LaptopDto[]> {
  return this.http.get<LaptopDto[]>('http://localhost:8080/laptop/getAll');
}

// In Component
this.laptopService.getAllLaptops().subscribe({
  next: (laptops) => {
    this.laptops = laptops; // Direct array
    console.log('Laptops loaded:', laptops);
  },
  error: (error) => {
    console.error('Error fetching laptops:', error);
  }
});
```

### 7. Common Issues:

**Issue: 401 Unauthorized**
- **Solution:** Restart Spring Boot application

**Issue: CORS Error**
- **Solution:** Check if frontend origin is in CORS allowed list

**Issue: Empty Array**
- **Solution:** Check if there's data in the database

**Issue: Wrong Response Format**
- **Solution:** The endpoint returns direct array, not wrapped object

### 8. Test with cURL
```bash
curl http://localhost:8080/laptop/getAll
```

This should return JSON array without authentication.




