# Station Dropdown API Endpoint

## Endpoint Details

**URL:** `GET /station/getAll`  
**Full URL:** `http://localhost:8080/station/getAll`  
**Method:** `GET`  
**Authentication:** Required (Bearer token)  
**Permissions Required:** 
- `ALL_STATION` - Returns all stations
- `VIEW_STATION` - Returns only stations assigned to the user

## Response Format

The endpoint returns an array of station objects:

```json
[
  {
    "id": 1,
    "name": "CHIVHU_MAG_COURT"
  },
  {
    "id": 2,
    "name": "GOROMONZI_HIGH_COURT"
  },
  {
    "id": 3,
    "name": "HWEDZA_MAG_COURT"
  }
]
```

## Frontend Implementation Example

### Using Fetch API:
```javascript
const fetchStations = async () => {
  try {
    const response = await fetch('http://localhost:8080/station/getAll', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${yourAuthToken}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (response.ok) {
      const stations = await response.json();
      // stations is an array of {id, name} objects
      return stations;
    } else {
      console.error('Failed to fetch stations');
      return [];
    }
  } catch (error) {
    console.error('Error fetching stations:', error);
    return [];
  }
};
```

### Using Axios:
```javascript
import axios from 'axios';

const fetchStations = async () => {
  try {
    const response = await axios.get('http://localhost:8080/station/getAll', {
      headers: {
        'Authorization': `Bearer ${yourAuthToken}`
      }
    });
    return response.data; // Array of {id, name} objects
  } catch (error) {
    console.error('Error fetching stations:', error);
    return [];
  }
};
```

### React Example with useState:
```javascript
import { useState, useEffect } from 'react';

const StationDropdown = () => {
  const [stations, setStations] = useState([]);
  const [selectedStation, setSelectedStation] = useState('');

  useEffect(() => {
    const loadStations = async () => {
      const stationsData = await fetchStations();
      setStations(stationsData);
    };
    loadStations();
  }, []);

  return (
    <select 
      value={selectedStation} 
      onChange={(e) => setSelectedStation(e.target.value)}
    >
      <option value="">Select Station</option>
      {stations.map((station) => (
        <option key={station.id} value={station.id}>
          {station.name}
        </option>
      ))}
    </select>
  );
};
```

## Response Fields

- **id** (Long): The station ID - use this as the value for the dropdown option
- **name** (String): The station name - use this as the display text for the dropdown option

## Error Handling

- **401 Unauthorized**: User is not authenticated - ensure the Authorization header is included
- **403 Forbidden**: User doesn't have `ALL_STATION` or `VIEW_STATION` permission
- **200 OK**: Success - returns array of stations

## Notes

- The endpoint requires authentication
- Users with `ALL_STATION` permission see all stations
- Users with `VIEW_STATION` permission only see stations they're assigned to
- The response format is consistent: `[{id, name}, ...]`






