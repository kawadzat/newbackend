// Frontend Fetch Examples for Laptop Endpoints
// These endpoints are now PUBLIC (no authentication required)

// ============================================
// Fetch All Laptops - GET /laptop/getAll
// ============================================

// Using Fetch API
async function fetchAllLaptops() {
    try {
        const response = await fetch('http://localhost:8080/laptop/getAll', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
                // NO Authorization header needed - endpoint is public
            }
        });
        
        if (response.ok) {
            const laptops = await response.json();
            console.log('Laptops:', laptops);
            return laptops;
        } else {
            console.error('Failed to fetch laptops:', response.status, response.statusText);
            return [];
        }
    } catch (error) {
        console.error('Error fetching laptops:', error);
        return [];
    }
}

// Using Axios
import axios from 'axios';

async function fetchAllLaptopsAxios() {
    try {
        const response = await axios.get('http://localhost:8080/laptop/getAll', {
            headers: {
                'Content-Type': 'application/json'
                // NO Authorization header needed
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching laptops:', error);
        return [];
    }
}

// ============================================
// Create Laptop - POST /laptop/create
// ============================================

async function createLaptop(laptopData) {
    try {
        const response = await fetch('http://localhost:8080/laptop/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
                // NO Authorization header needed - endpoint is public
            },
            body: JSON.stringify({
                manufacturer: laptopData.manufacturer,
                model: laptopData.model, // Optional
                serialNumber: laptopData.serialNumber,
                title: laptopData.title,
                description: laptopData.description,
                ram: laptopData.ram,
                processor: laptopData.processor,
                status: laptopData.status,
                issuedTo: laptopData.issuedTo,
                email: laptopData.email,
                department: laptopData.department,
                designation: laptopData.designation,
                issueDate: laptopData.issueDate,
                replacementDate: laptopData.replacementDate
            })
        });
        
        if (response.ok) {
            const result = await response.json();
            console.log('Laptop created:', result);
            return result;
        } else {
            const error = await response.json();
            console.error('Failed to create laptop:', error);
            throw new Error(error.message || 'Failed to create laptop');
        }
    } catch (error) {
        console.error('Error creating laptop:', error);
        throw error;
    }
}

// ============================================
// Fetch Stations - GET /station/getAll
// ============================================

async function fetchAllStations() {
    try {
        const response = await fetch('http://localhost:8080/station/getAll', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
                // NO Authorization header needed - endpoint is public
            }
        });
        
        if (response.ok) {
            const stations = await response.json();
            return stations; // Array of {id, name}
        } else {
            console.error('Failed to fetch stations:', response.status);
            return [];
        }
    } catch (error) {
        console.error('Error fetching stations:', error);
        return [];
    }
}

// ============================================
// React Component Example
// ============================================

import { useState, useEffect } from 'react';

function LaptopList() {
    const [laptops, setLaptops] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadLaptops();
    }, []);

    const loadLaptops = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await fetchAllLaptops();
            setLaptops(data);
        } catch (err) {
            setError('Failed to load laptops');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <h1>Laptops</h1>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Manufacturer</th>
                        <th>Model</th>
                        <th>Serial Number</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    {laptops.map(laptop => (
                        <tr key={laptop.id}>
                            <td>{laptop.id}</td>
                            <td>{laptop.manufacturer}</td>
                            <td>{laptop.model || 'N/A'}</td>
                            <td>{laptop.serialNumber || 'N/A'}</td>
                            <td>{laptop.status || 'N/A'}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

// ============================================
// Angular Service Example
// ============================================

// laptop.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LaptopService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getAllLaptops(): Observable<any[]> {
    // NO Authorization header needed - endpoint is public
    return this.http.get<any[]>(`${this.apiUrl}/laptop/getAll`);
  }

  createLaptop(laptop: any): Observable<any> {
    // NO Authorization header needed - endpoint is public
    return this.http.post<any>(`${this.apiUrl}/laptop/create`, laptop);
  }

  getAllStations(): Observable<any[]> {
    // NO Authorization header needed - endpoint is public
    return this.http.get<any[]>(`${this.apiUrl}/station/getAll`);
  }
}

// ============================================
// Key Points:
// ============================================
// 1. NO Authorization header needed
// 2. NO Bearer token required
// 3. Endpoints are PUBLIC
// 4. Just use standard fetch/axios/http calls
// 5. Make sure backend is RESTARTED after security changes




