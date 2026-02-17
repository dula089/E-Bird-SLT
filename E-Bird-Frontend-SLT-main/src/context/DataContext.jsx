import React, { createContext, useContext, useState, useEffect, useCallback, useRef } from 'react';
import { getAccessToken } from '../utils/authUtils';
import { getCurrentUser } from '../utils/userUtils';

const DataContext = createContext();

export const useData = () => {
  const context = useContext(DataContext);
  if (!context) {
    throw new Error('useData must be used within a DataProvider');
  }
  return context;
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const DataProvider = ({ children }) => {
  const [requests, setRequests] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [employeeMap, setEmployeeMap] = useState({});
  
  // Use refs to track fetch state without causing re-renders
  const lastFetchRef = useRef(null);
  const isFetchingRef = useRef(false);
  const initialFetchDoneRef = useRef(false);
  const mountedRef = useRef(true);

  useEffect(() => {
    mountedRef.current = true;
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const createHeaders = useCallback(async () => {
    const user = getCurrentUser();
    const headers = { "Content-Type": "application/json" };
    
    if (user && user.profile !== "erp_employee") {
      try {
        const token = await getAccessToken();
        if (token) headers.Authorization = `Bearer ${token}`;
      } catch (error) {
        console.error("Error getting token:", error);
      }
    }
    return headers;
  }, []);

  const fetchAllData = useCallback(async (force = false) => {
    // Prevent multiple simultaneous fetches
    if (isFetchingRef.current) {
      console.log("⏳ Fetch already in progress, skipping...");
      return;
    }

    // Don't refetch if data is less than 30 seconds old (unless forced)
    const now = Date.now();
    if (!force && lastFetchRef.current && now - lastFetchRef.current < 30000) {
      console.log("📦 Using cached data from", new Date(lastFetchRef.current).toLocaleTimeString());
      return;
    }

    try {
      isFetchingRef.current = true;
      if (mountedRef.current) setLoading(true);
      if (mountedRef.current) setError(null);
      
      const headers = await createHeaders();
      
      console.log("📡 DataContext fetching all data...");
      
      // Fetch both in parallel
      const [requestsRes, categoriesRes] = await Promise.all([
        fetch(`${API_BASE_URL}/AddNewRequest?page=0&size=100`, { headers }),
        fetch(`${API_BASE_URL}/Categories`, { headers })
      ]);

      if (!requestsRes.ok) {
        const errorText = await requestsRes.text();
        throw new Error(`Failed to fetch requests: ${requestsRes.status} ${errorText}`);
      }
      
      if (!categoriesRes.ok) {
        const errorText = await categoriesRes.text();
        throw new Error(`Failed to fetch categories: ${categoriesRes.status} ${errorText}`);
      }

      const requestsData = await requestsRes.json();
      const categoriesData = await categoriesRes.json();

      // Handle paginated response
      let allRequests = [];
      if (requestsData && requestsData.requests && Array.isArray(requestsData.requests)) {
        allRequests = requestsData.requests;
        console.log(`📊 Paginated response: ${allRequests.length} requests, total: ${requestsData.totalItems}`);
      } else if (Array.isArray(requestsData)) {
        allRequests = requestsData;
        console.log(`📊 Array response: ${allRequests.length} requests`);
      } else {
        console.error("Unexpected requests data format:", requestsData);
        allRequests = [];
      }

      if (mountedRef.current) {
        setRequests(allRequests);
        setCategories(Array.isArray(categoriesData) ? categoriesData : []);
        lastFetchRef.current = Date.now();
        initialFetchDoneRef.current = true;
      }
      
      console.log(`✅ DataContext loaded: ${allRequests.length} requests, ${categoriesData.length} categories`);
      
    } catch (error) {
      console.error("❌ Error fetching data:", error);
      if (mountedRef.current) setError(error.message);
    } finally {
      if (mountedRef.current) setLoading(false);
      isFetchingRef.current = false;
    }
  }, [createHeaders]);

  // Refresh data - memoized with stable reference
  const refreshData = useCallback(() => fetchAllData(true), [fetchAllData]);

  // Initial fetch - only once
  useEffect(() => {
    if (!initialFetchDoneRef.current && mountedRef.current) {
      fetchAllData();
    }
  }, [fetchAllData]);

  // Get requests by user (created by) - memoized with stable reference
  const getUserRequests = useCallback((user) => {
    if (!user || !requests.length) return [];
    
    return requests.filter((request) => {
      const assignedBy = request.assignedBy || "";

      if (user.profile === "erp_employee") {
        const employeeMatch = assignedBy.includes(user.employeeNumber);
        const nameMatch = assignedBy.toLowerCase().includes(user.name.toLowerCase());
        return employeeMatch || nameMatch;
      }

      const matchesEmail = assignedBy.toLowerCase() === user.email?.toLowerCase();
      const matchesUsername = assignedBy.toLowerCase() === user.username?.toLowerCase();
      const matchesName = assignedBy.toLowerCase() === user.name?.toLowerCase();
      const containsUsername = assignedBy.toLowerCase().includes(user.username?.toLowerCase() || "");
      const containsName = assignedBy.toLowerCase().includes(user.name?.toLowerCase() || "");

      return matchesEmail || matchesUsername || matchesName || containsUsername || containsName;
    });
  }, [requests]);

  // Get assignments for user - memoized with stable reference
  const getUserAssignments = useCallback((user) => {
    if (!user || !requests.length) return [];
    
    return requests.filter((request) => {
      if (!request.assignTo) return false;

      const assignToLower = request.assignTo.toLowerCase();

      if (user.profile === "erp_employee") {
        const employeeMatch = assignToLower.includes(user.employeeNumber);
        const nameMatch = assignToLower.includes(user.name.toLowerCase());
        return employeeMatch || nameMatch;
      }

      const userEmailLower = user.email?.toLowerCase() || "";
      const userNameLower = user.name?.toLowerCase() || "";
      const userUsernameLower = user.username?.toLowerCase() || "";

      return (
        assignToLower === userEmailLower ||
        assignToLower === userNameLower ||
        assignToLower === userUsernameLower ||
        assignToLower.includes(userEmailLower.split("@")[0]) ||
        assignToLower.includes(userNameLower) ||
        assignToLower.includes(userUsernameLower)
      );
    });
  }, [requests]);

  // Get requests visible to user - memoized with stable reference
  const getVisibleRequests = useCallback((user, subordinates = []) => {
    if (!user || !requests.length) return [];
    
    const employeeNumbers = new Set();
    const employeeIdentifiers = new Set();

    if (user.profile === "erp_employee" && user.employeeNumber) {
      employeeNumbers.add(user.employeeNumber);
      if (user.name) {
        employeeIdentifiers.add(user.name.toLowerCase());
        employeeIdentifiers.add(user.name.toUpperCase());
      }
    } else {
      if (user.email) employeeIdentifiers.add(user.email.toLowerCase());
      if (user.username) employeeIdentifiers.add(user.username.toLowerCase());
      if (user.name) {
        employeeIdentifiers.add(user.name.toLowerCase());
        employeeIdentifiers.add(user.name.toUpperCase());
      }
    }

    subordinates.forEach((emp) => {
      const empNumber = emp.employeeNumber || emp.employeeNo;
      if (empNumber) {
        employeeNumbers.add(empNumber);
        const fullName = `${emp.employeeTitle || ""} ${emp.employeeInitials || ""} ${emp.employeeSurname || ""}`.trim();
        if (fullName) {
          employeeIdentifiers.add(fullName.toLowerCase());
          employeeIdentifiers.add(fullName.toUpperCase());
        }
        if (emp.employeeName) {
          employeeIdentifiers.add(emp.employeeName.toLowerCase());
          employeeIdentifiers.add(emp.employeeName.toUpperCase());
        }
      }
    });

    return requests.filter((request) => {
      const assignedBy = String(request.assignedBy || "").trim();
      const assignTo = String(request.assignTo || "").trim();
      const forwardedBy = String(request.forwardedBy || "").trim();

      // Check exact matches
      if (employeeNumbers.has(assignedBy) || employeeNumbers.has(assignTo) || employeeNumbers.has(forwardedBy)) {
        return true;
      }

      // Check contains matches
      for (const empNum of employeeNumbers) {
        if (assignedBy.includes(empNum) || assignTo.includes(empNum) || forwardedBy.includes(empNum)) {
          return true;
        }
      }

      // Check name matches
      const assignedByLower = assignedBy.toLowerCase();
      const assignToLower = assignTo.toLowerCase();
      const forwardedByLower = forwardedBy.toLowerCase();

      for (const identifier of employeeIdentifiers) {
        if (typeof identifier === "string" && !/^\d{6}$/.test(identifier)) {
          if (assignedByLower.includes(identifier.toLowerCase()) || 
              assignToLower.includes(identifier.toLowerCase()) || 
              forwardedByLower.includes(identifier.toLowerCase())) {
            return true;
          }
        }
      }

      return false;
    });
  }, [requests]);

  const value = {
    requests,
    categories,
    loading,
    error,
    refreshData,
    getUserRequests,
    getUserAssignments,
    getVisibleRequests,
    employeeMap,
    setEmployeeMap
  };

  return <DataContext.Provider value={value}>{children}</DataContext.Provider>;
};