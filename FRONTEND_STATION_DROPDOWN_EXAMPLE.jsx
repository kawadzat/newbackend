// React Component Example for Station Dropdown
// This component fetches stations from /station/getAll and populates a dropdown

import React, { useState, useEffect } from 'react';
import axios from 'axios'; // or use fetch API

const StationDropdown = ({ onStationChange, selectedStationId = '' }) => {
  const [stations, setStations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchStations();
  }, []);

  const fetchStations = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Get auth token from your auth context/storage
      const token = localStorage.getItem('authToken'); // Adjust based on your auth implementation
      
      const response = await axios.get('http://localhost:8080/station/getAll', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.data) {
        setStations(response.data);
      }
    } catch (err) {
      console.error('Error fetching stations:', err);
      setError('Failed to load stations');
      if (err.response?.status === 401) {
        setError('Authentication required');
      } else if (err.response?.status === 403) {
        setError('You do not have permission to view stations');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const stationId = e.target.value;
    if (onStationChange) {
      onStationChange(stationId);
    }
  };

  if (loading) {
    return (
      <select disabled>
        <option>Loading stations...</option>
      </select>
    );
  }

  if (error) {
    return (
      <div>
        <select disabled>
          <option>{error}</option>
        </select>
        <button onClick={fetchStations}>Retry</button>
      </div>
    );
  }

  return (
    <select
      value={selectedStationId}
      onChange={handleChange}
      className="station-dropdown" // Add your CSS class
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

export default StationDropdown;

// ============================================
// Alternative: Using Fetch API instead of Axios
// ============================================

const StationDropdownWithFetch = ({ onStationChange, selectedStationId = '' }) => {
  const [stations, setStations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchStations();
  }, []);

  const fetchStations = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const token = localStorage.getItem('authToken');
      
      const response = await fetch('http://localhost:8080/station/getAll', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setStations(data);
      } else if (response.status === 401) {
        setError('Authentication required');
      } else if (response.status === 403) {
        setError('You do not have permission to view stations');
      } else {
        setError('Failed to load stations');
      }
    } catch (err) {
      console.error('Error fetching stations:', err);
      setError('Failed to load stations');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const stationId = e.target.value;
    if (onStationChange) {
      onStationChange(stationId);
    }
  };

  if (loading) {
    return (
      <select disabled>
        <option>Loading stations...</option>
      </select>
    );
  }

  if (error) {
    return (
      <div>
        <select disabled>
          <option>{error}</option>
        </select>
        <button onClick={fetchStations}>Retry</button>
      </div>
    );
  }

  return (
    <select
      value={selectedStationId}
      onChange={handleChange}
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

// ============================================
// Usage Example in a Form Component
// ============================================

const LaptopForm = () => {
  const [formData, setFormData] = useState({
    manufacturer: '',
    model: '',
    stationId: ''
  });

  const handleStationChange = (stationId) => {
    setFormData(prev => ({
      ...prev,
      stationId: stationId
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Submit form data including stationId
    console.log('Form data:', formData);
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Manufacturer:</label>
        <input
          type="text"
          value={formData.manufacturer}
          onChange={(e) => setFormData(prev => ({ ...prev, manufacturer: e.target.value }))}
        />
      </div>
      
      <div>
        <label>Model:</label>
        <input
          type="text"
          value={formData.model}
          onChange={(e) => setFormData(prev => ({ ...prev, model: e.target.value }))}
        />
      </div>
      
      <div>
        <label>Station:</label>
        <StationDropdown
          selectedStationId={formData.stationId}
          onStationChange={handleStationChange}
        />
      </div>
      
      <button type="submit">Submit</button>
    </form>
  );
};






