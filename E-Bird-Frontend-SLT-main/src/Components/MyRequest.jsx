import React, { useState, useEffect, useMemo, useRef } from "react";
import { Eye, Paperclip, Download } from "lucide-react";
import "../Components/RequestCSS/MyRequest.css";
import { useTranslation } from "react-i18next";
import { getAccessToken } from "../utils/authUtils";
import { getCurrentUser } from "../utils/userUtils";
import { useData } from "../context/DataContext";
import Swal from "sweetalert2";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const BACKEND_URL = `${API_BASE_URL}/AddNewRequest`;

const MyRequest = () => {
  const { 
    requests, 
    categories, 
    loading: dataLoading, 
    error: dataError, 
    refreshData,
    getUserRequests,
    employeeMap,
    setEmployeeMap 
  } = useData();
  
  const [myRequests, setMyRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("All Categories");
  const [selectedStatus, setSelectedStatus] = useState("All Statuses");
  const [modalRequest, setModalRequest] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [currentUser, setCurrentUser] = useState(null);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const itemsPerPage = 6;
  const mountedRef = useRef(true);

  const { t } = useTranslation();

  useEffect(() => {
    mountedRef.current = true;
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const createHeaders = async () => {
    const user = getCurrentUser();
    const headers = {
      "Content-Type": "application/json",
    };

    if (user && user.profile !== "erp_employee") {
      try {
        const token = await getAccessToken();
        if (token) {
          headers.Authorization = `Bearer ${token}`;
        }
      } catch (error) {
        console.error("Error getting token:", error);
      }
    }

    return headers;
  };

  const fetchEmployeeNamesForRequests = async (requests, existingNameMap) => {
    try {
      const employeeNumbers = new Set();

      requests.forEach((req) => {
        if (req.assignTo && /^\d{6}$/.test(req.assignTo.trim())) {
          employeeNumbers.add(req.assignTo.trim());
        }
      });

      if (employeeNumbers.size === 0) {
        return existingNameMap;
      }

      console.log(
        "📋 Unique employee numbers in assignTo:",
        Array.from(employeeNumbers)
      );

      const updatedNameMap = { ...existingNameMap };

      const { getEmployeeHierarchy } = await import("../utils/erpApi");

      const fetchPromises = Array.from(employeeNumbers).map(async (empNum) => {
        if (!updatedNameMap[empNum]) {
          try {
            const response = await getEmployeeHierarchy(
              empNum,
              "string",
              "string"
            );

            if (
              response &&
              response.success &&
              response.data &&
              response.data.length > 0
            ) {
              const employee = response.data.find(
                (emp) => emp.employeeNumber === empNum
              );
              if (employee) {
                updatedNameMap[empNum] = employee.employeeName;
                console.log(
                  `✅ Fetched name for ${empNum}: ${employee.employeeName}`
                );
              }
            }
          } catch (error) {
            console.error(`❌ Error fetching name for ${empNum}:`, error);
            updatedNameMap[empNum] = empNum;
          }
        }
      });

      await Promise.all(fetchPromises);

      console.log("✅ Complete employee map:", updatedNameMap);
      if (mountedRef.current) {
        setEmployeeMap(updatedNameMap);
      }

      return updatedNameMap;
    } catch (error) {
      console.error("❌ Error in fetchEmployeeNamesForRequests:", error);
      return existingNameMap;
    }
  };

  useEffect(() => {
    const user = getCurrentUser();
    if (!user) {
      alert("Unable to identify current user. Please log in again.");
      return;
    }
    if (mountedRef.current) {
      setCurrentUser(user);
      console.log("Current user in MyRequest:", user);
    }
  }, []);

  useEffect(() => {
    if (!dataLoading && requests.length > 0 && currentUser && mountedRef.current) {
      const userRequests = getUserRequests(currentUser);
      setMyRequests(userRequests);
      setLoading(false);
      console.log(`✅ MyRequest: ${userRequests.length} requests for ${currentUser.name}`);

      // Fetch employee names in background
      const timer = setTimeout(() => {
        fetchEmployeeNamesForRequests(userRequests, employeeMap);
      }, 100);
      
      return () => clearTimeout(timer);
    } else if (!dataLoading && mountedRef.current) {
      setLoading(false);
    }
  }, [dataLoading, requests, currentUser, getUserRequests]);

  useEffect(
    () => {
      if (mountedRef.current) {
        setCurrentPage(1);
      }
    },
    [startDate, endDate, selectedCategory, selectedStatus]
  );

  const getAssignToDisplay = (assignTo) => {
    if (!assignTo) return "Not Assigned";

    const trimmedAssignTo = assignTo.trim();

    if (/^\d{6}$/.test(trimmedAssignTo)) {
      const name = employeeMap[trimmedAssignTo];
      if (name && name !== trimmedAssignTo) {
        return `${name} (${trimmedAssignTo})`;
      }
      return trimmedAssignTo;
    }

    return assignTo;
  };

  const getStatusProgress = (status) => {
    switch (status) {
      case "Pending":
        return { value: 10, color: "#f59e0b" };
      case "In Progress":
        return { value: 40, color: "#3b82f6" };
      case "Under Review":
        return { value: 60, color: "#8b5cf6" };
      case "Approved":
        return { value: 80, color: "#FFC0CB" };
      case "Completed":
        return { value: 100, color: "#22c55e" };
      case "Rejected":
        return { value: 100, color: "#ef4444" };
      default:
        return { value: 0, color: "#9ca3af" };
    }
  };

  const formatDisplayDate = (dateString) => {
    if (!dateString) return "";
    try {
      return new Date(dateString).toLocaleDateString("en-GB");
    } catch (error) {
      return dateString;
    }
  };

  const filteredRequestsAll = useMemo(
    () =>
      myRequests.filter((request) => {
        const matchesStartDate =
          !startDate || new Date(request.receivedDate) >= new Date(startDate);
        const matchesEndDate =
          !endDate || new Date(request.receivedDate) <= new Date(endDate);
        const matchesCategory =
          selectedCategory === "All Categories" ||
          request.mainCategory === selectedCategory;
        const matchesStatus =
          selectedStatus === "All Statuses" ||
          request.status === selectedStatus;
        return (
          matchesStartDate && matchesEndDate && matchesCategory && matchesStatus
        );
      }),
    [myRequests, startDate, endDate, selectedCategory, selectedStatus]
  );

  const totalFilteredPages = Math.ceil(filteredRequestsAll.length / itemsPerPage);

  const paginatedRequests = useMemo(() => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    return filteredRequestsAll.slice(startIndex, startIndex + itemsPerPage);
  }, [filteredRequestsAll, currentPage]);

  const openModal = (request) => {
    console.log("📋 Opening modal for request:", request.requestId);
    setModalRequest({ ...request });
  };
  
  const closeModal = () => setModalRequest(null);

  const handleChange = (e) => {
    setModalRequest({ ...modalRequest, [e.target.name]: e.target.value });
  };

  const handleDownloadAttachment = async (attachment) => {
    console.log("📥 Attempting download:", attachment);

    if (!attachment) {
      Swal.fire({
        icon: "error",
        title: "Download Failed",
        text: "Attachment data not available",
        confirmButtonColor: "#d33",
      });
      return;
    }

    Swal.fire({
      title: "Downloading...",
      text: "Please wait",
      allowOutsideClick: false,
      didOpen: () => {
        Swal.showLoading();
      },
    });

    try {
      if (attachment.cout && attachment.cout.trim()) {
        console.log("✅ Base64 data found, length:", attachment.cout.length);

        const base64Data = attachment.cout.replace(/\s/g, "");

        const byteCharacters = atob(base64Data);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);

        const fileName =
          attachment.attachName || attachment.title || "attachment";
        let mimeType = "application/octet-stream";

        if (fileName.toLowerCase().endsWith(".pdf")) {
          mimeType = "application/pdf";
        } else if (fileName.toLowerCase().match(/\.(jpg|jpeg)$/)) {
          mimeType = "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".png")) {
          mimeType = "image/png";
        } else if (fileName.toLowerCase().match(/\.(doc|docx)$/)) {
          mimeType = "application/msword";
        } else if (fileName.toLowerCase().match(/\.(xls|xlsx)$/)) {
          mimeType = "application/vnd.ms-excel";
        }

        const blob = new Blob([byteArray], { type: mimeType });

        const url = window.URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        Swal.close();

        Swal.fire({
          icon: "success",
          title: "Download Complete",
          text: `${fileName} has been downloaded`,
          timer: 2000,
          showConfirmButton: false,
        });
      } else {
        console.log("⚠️ No base64 data available");

        Swal.fire({
          icon: "warning",
          title: "Attachment Not Available",
          html: `
          <div style="text-align: left; padding: 10px;">
            <p><strong>File Name:</strong> ${attachment.attachName || "N/A"}</p>
            <p><strong>Title:</strong> ${attachment.title || "N/A"}</p>
            <p><strong>Size:</strong> ${attachment.size || "N/A"}</p>
          </div>
          <div style="margin-top: 15px; padding: 10px; background-color: #fef3c7; border-radius: 6px; font-size: 13px;">
            <strong>Note:</strong> The file content is not available for download. The file may need to be re-uploaded.
          </div>
        `,
          confirmButtonColor: "#3b82f6",
          confirmButtonText: "OK",
        });
      }
    } catch (error) {
      console.error("❌ Error downloading attachment:", error);
      Swal.fire({
        icon: "error",
        title: "Download Failed",
        text:
          error.message || "Failed to download attachment. Please try again.",
        confirmButtonColor: "#d33",
      });
    }
  };

  const handleSave = async () => {
    if (!modalRequest?.id) return alert("No request selected");
    
    setIsRefreshing(true);
    
    try {
      const headers = await createHeaders();
      const res = await fetch(`${BACKEND_URL}/${modalRequest.id}`, {
        method: "PUT",
        headers,
        body: JSON.stringify(modalRequest),
      });

      if (!res.ok) throw new Error("Failed to save changes");

      Swal.fire({
        icon: "success",
        title: `REQUEST UPDATED SUCCESSFULLY <br> ${modalRequest.requestId}`,
        showConfirmButton: false,
        timer: 2000,
        timerProgressBar: true,
      });

      closeModal();
      
      // Refresh data in context
      await refreshData();
      
    } catch (error) {
      console.error("Error saving request:", error);
      Swal.fire({
        icon: "error",
        title: "Update Failed",
        text: error.message || "Please try again",
        confirmButtonText: "OK",
        confirmButtonColor: "#d33",
      });
    } finally {
      if (mountedRef.current) {
        setIsRefreshing(false);
      }
    }
  };

  const handleDelete = async () => {
    if (!modalRequest?.id) return alert("No request selected");
    
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "Do you want to delete this request?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Yes, delete it!",
      cancelButtonText: "Cancel",
    });

    if (!result.isConfirmed) return;

    setIsRefreshing(true);
    
    try {
      const headers = await createHeaders();
      const res = await fetch(`${BACKEND_URL}/${modalRequest.id}`, {
        method: "DELETE",
        headers,
      });

      if (!res.ok) throw new Error("Failed to delete request");

      Swal.fire({
        icon: "success",
        title: "REQUEST DELETED SUCCESSFULLY",
        showConfirmButton: false,
        timer: 2000,
        timerProgressBar: true,
      });

      closeModal();

      // Refresh data in context
      await refreshData();
      
    } catch (error) {
      console.error("Error deleting request:", error);
      Swal.fire({
        icon: "error",
        title: "Delete Failed",
        text: error.message || "Please try again",
        confirmButtonText: "OK",
        confirmButtonColor: "#d33",
      });
    } finally {
      if (mountedRef.current) {
        setIsRefreshing(false);
      }
    }
  };

  const handleRetry = () => {
    refreshData();
  };

  if (loading || (dataLoading && !dataError)) {
    return (
      <div className="my-request-container">
        <div className="fancy-loader">
          <div className="loader-container">
            <div className="loader-box"></div>
            <div className="loader-box"></div>
            <div className="loader-box"></div>
            <div className="loader-box"></div>
          </div>
          <p style={{ marginTop: "20px", color: "#666", fontSize: "14px" }}>
            Loading your requests...
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="my-request-container">
      {/* Error Display */}
      {dataError && (
        <div style={{
          padding: "20px",
          margin: "20px 0",
          backgroundColor: "#fee2e2",
          border: "1px solid #ef4444",
          borderRadius: "8px",
          color: "#b91c1c",
          textAlign: "center"
        }}>
          <h3 style={{ marginBottom: "10px" }}>⚠️ Error Loading Data</h3>
          <p style={{ marginBottom: "15px" }}>{dataError}</p>
          <button 
            onClick={handleRetry}
            style={{
              padding: "8px 16px",
              backgroundColor: "#3b82f6",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
              marginTop: "10px",
              fontWeight: "500"
            }}
          >
            Retry
          </button>
        </div>
      )}

      {/* Refreshing Overlay */}
      {isRefreshing && (
        <div style={{
          position: "fixed",
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: "rgba(255,255,255,0.7)",
          zIndex: 9999,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          pointerEvents: "none"
        }}>
          <div style={{
            padding: "20px",
            backgroundColor: "white",
            borderRadius: "8px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.15)"
          }}>
            <div className="loader-container" style={{ transform: "scale(0.7)" }}>
              <div className="loader-box"></div>
              <div className="loader-box"></div>
              <div className="loader-box"></div>
              <div className="loader-box"></div>
            </div>
            <p style={{ marginTop: "10px", color: "#666" }}>Refreshing...</p>
          </div>
        </div>
      )}

      {/* Main Content */}
      <>
        <button className="my-request-btn">{t("my_requests.button")}</button>

        <div className="filter-section">
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
          >
            <option>{t("all_categories")}</option>
            {categories.map((cat, idx) => (
              <option key={idx} value={cat.name || cat}>
                {cat.name || cat}
              </option>
            ))}
          </select>
          <select
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value)}
          >
            <option>{t("all_statuses")}</option>
            <option>{t("pending")}</option>
            <option>{t("in_progress")}</option>
            <option>{t("under_review")}</option>
            <option>{t("approved")}</option>
            <option>{t("completed")}</option>
            <option>{t("rejected")}</option>
          </select>
        </div>

        <div className="status-legend">
          <div>
            <span className="legend-box pending"></span>
            {t("pending")}{" "}
          </div>
          <div>
            <span className="legend-box in-progress"></span>{" "}
            {t("in_progress")}
          </div>
          <div>
            <span className="legend-box under-review"></span>{" "}
            {t("under_review")}
          </div>
          <div>
            <span className="legend-box approved"></span> {t("approved")}
          </div>
          <div>
            <span className="legend-box completed"></span> {t("completed")}
          </div>
          <div>
            <span className="legend-box rejected"></span> {t("rejected")}
          </div>
        </div>

        <p className="request-count-info">
          {t("showing")} {paginatedRequests.length} {t("of")}{" "}
          {filteredRequestsAll.length} {t("requests")}
        </p>

        {paginatedRequests.length === 0 ? (
          <div
            style={{
              padding: "60px 40px",
              textAlign: "center",
              backgroundColor: "#f9f9f9",
              borderRadius: "8px",
              margin: "20px 0",
            }}
          >
            <h3 style={{ color: "#666", marginBottom: "15px" }}>
              No Requests Found
            </h3>
            <p style={{ fontSize: "14px", color: "#888" }}>
              You haven't created any requests yet, or no requests match the
              current filters.
            </p>
          </div>
        ) : (
          <div className="table-container">
            <table className="request-table">
              <thead>
                <tr>
                  <th>{t("reference")}</th>
                  <th>{t("created")}</th>
                  <th>{t("assign_from")}</th>
                  <th>{t("main_category")}</th>
                  <th>{t("assign_to")}</th>
                  <th>{t("current_status")}</th>
                  <th>{t("action")}</th>
                </tr>
              </thead>
              <tbody>
                {paginatedRequests.map((req) => (
                  <tr key={req.id}>
                    <td>
                      {req.requestId}
                      {req.attachments && req.attachments.length > 0 && (
                        <Paperclip
                          size={14}
                          style={{
                            marginLeft: "6px",
                            color: "#6b7280",
                            verticalAlign: "middle",
                          }}
                        />
                      )}
                    </td>
                    <td>{formatDisplayDate(req.receivedDate)}</td>
                    <td>{req.assignedBy}</td>
                    <td>{req.mainCategory}</td>
                    <td>{getAssignToDisplay(req.assignTo)}</td>
                    <td>
                      <div className="status-cell">
                        <div className="status-bar-wrapper">
                          <div
                            className="status-bar-fill"
                            style={{
                              width: `${
                                getStatusProgress(req.status).value
                              }%`,
                              backgroundColor: getStatusProgress(req.status)
                                .color,
                            }}
                          />
                        </div>
                      </div>
                    </td>
                    <td>
                      <button
                        className="view-btn"
                        onClick={() => openModal(req)}
                        disabled={isRefreshing}
                      >
                        <Eye size={17} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {totalFilteredPages > 1 && (
          <div className="pagination">
            <button
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
              disabled={currentPage === 1 || isRefreshing}
            >
              {t("previous")}
            </button>
            <span>
              {t("page")} {currentPage} {t("of")} {totalFilteredPages}
            </span>
            <button
              onClick={() =>
                setCurrentPage((prev) => Math.min(prev + 1, totalFilteredPages))
              }
              disabled={currentPage === totalFilteredPages || isRefreshing}
            >
              {t("next")}
            </button>
          </div>
        )}
      </>

      {/* Modal */}
      {modalRequest && (
        <div className="modal-overlay">
          <div
            className="modal"
            style={{ maxWidth: "700px", maxHeight: "90vh", overflowY: "auto" }}
          >
            <button className="close-button" onClick={closeModal} disabled={isRefreshing}>
              ×
            </button>
            <h3 className="modal-title">View / Edit Request</h3>
            <table className="modal-table">
              <tbody>
                <tr>
                  <td style={{ width: "200px" }}>
                    <strong>Reference</strong>
                  </td>
                  <td>
                    <input
                      type="text"
                      name="requestId"
                      value={modalRequest.requestId || ""}
                      readOnly
                      style={{ backgroundColor: "#f5f5f5" }}
                    />
                  </td>
                </tr>
                <tr>
                  <td>
                    <strong>Assign From</strong>
                  </td>
                  <td>
                    <input
                      type="text"
                      name="assignedBy"
                      value={modalRequest.assignedBy || ""}
                      readOnly
                      style={{ backgroundColor: "#f5f5f5" }}
                    />
                  </td>
                </tr>
                <tr>
                  <td>
                    <strong>Main Category</strong>
                  </td>
                  <td>
                    <input
                      type="text"
                      name="mainCategory"
                      value={modalRequest.mainCategory || ""}
                      onChange={handleChange}
                      disabled={isRefreshing}
                    />
                  </td>
                </tr>
                <tr>
                  <td>
                    <strong>Assign To</strong>
                  </td>
                  <td>
                    <input
                      type="text"
                      name="assignTo"
                      value={getAssignToDisplay(modalRequest.assignTo || "")}
                      readOnly
                      style={{ backgroundColor: "#f5f5f5" }}
                    />
                  </td>
                </tr>
                <tr>
                  <td>
                    <strong>Request in Brief</strong>
                  </td>
                  <td>
                    <textarea
                      name="requestInBrief"
                      value={modalRequest.requestInBrief || ""}
                      onChange={handleChange}
                      rows="4"
                      style={{
                        width: "100%",
                        padding: "8px",
                        fontSize: "14px",
                        border: "1px solid #ddd",
                        borderRadius: "4px",
                        fontFamily: "inherit",
                        resize: "vertical",
                      }}
                      placeholder="Enter request details..."
                      disabled={isRefreshing}
                    />
                  </td>
                </tr>
                <tr>
                  <td>
                    <strong>Remarks</strong>
                  </td>
                  <td>
                    <textarea
                      name="remarks"
                      value={modalRequest.remarks || ""}
                      onChange={handleChange}
                      rows="3"
                      style={{
                        width: "100%",
                        padding: "8px",
                        fontSize: "14px",
                        border: "1px solid #ddd",
                        borderRadius: "4px",
                        fontFamily: "inherit",
                        resize: "vertical",
                      }}
                      placeholder="Add remarks..."
                      disabled={isRefreshing}
                    />
                  </td>
                </tr>

                {modalRequest.attachments &&
                  modalRequest.attachments.length > 0 && (
                    <tr>
                      <td>
                        <strong>Attachments</strong>
                      </td>
                      <td>
                        <div
                          style={{
                            backgroundColor: "#f9fafb",
                            padding: "12px",
                            borderRadius: "6px",
                            border: "1px solid #e5e7eb",
                          }}
                        >
                          {modalRequest.attachments.map((attachment, index) => (
                            <div
                              key={index}
                              style={{
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                                padding: "10px",
                                marginBottom:
                                  index < modalRequest.attachments.length - 1
                                    ? "8px"
                                    : "0",
                                backgroundColor: "#ffffff",
                                borderRadius: "4px",
                                border: "1px solid #e5e7eb",
                              }}
                            >
                              <div
                                style={{
                                  display: "flex",
                                  alignItems: "center",
                                  gap: "10px",
                                  flex: 1,
                                }}
                              >
                                <Paperclip
                                  size={16}
                                  style={{ color: "#6b7280" }}
                                />
                                <div style={{ flex: 1 }}>
                                  <div
                                    style={{
                                      fontSize: "14px",
                                      fontWeight: "500",
                                      color: "#374151",
                                    }}
                                  >
                                    {attachment.title ||
                                      attachment.attachName ||
                                      `Attachment ${index + 1}`}
                                  </div>
                                  {attachment.size && (
                                    <div
                                      style={{
                                        fontSize: "12px",
                                        color: "#6b7280",
                                        marginTop: "2px",
                                      }}
                                    >
                                      {attachment.size}
                                    </div>
                                  )}
                                </div>
                              </div>
                              <button
                                onClick={() =>
                                  handleDownloadAttachment(attachment)
                                }
                                style={{
                                  display: "flex",
                                  alignItems: "center",
                                  gap: "6px",
                                  padding: "6px 12px",
                                  fontSize: "13px",
                                  backgroundColor: "#3b82f6",
                                  color: "white",
                                  border: "none",
                                  borderRadius: "4px",
                                  cursor: "pointer",
                                  transition: "background-color 0.2s",
                                }}
                                onMouseOver={(e) =>
                                  (e.target.style.backgroundColor = "#2563eb")
                                }
                                onMouseOut={(e) =>
                                  (e.target.style.backgroundColor = "#3b82f6")
                                }
                                disabled={isRefreshing}
                              >
                                <Download size={14} />
                                Download
                              </button>
                            </div>
                          ))}
                        </div>
                      </td>
                    </tr>
                  )}

                <tr>
                  <td>
                    <strong>Status</strong>
                  </td>
                  <td>
                    <div className="modal-progress-wrapper">
                      <div
                        className="modal-progress-fill"
                        style={{
                          width: `${
                            getStatusProgress(modalRequest.status).value
                          }%`,
                          backgroundColor: getStatusProgress(
                            modalRequest.status
                          ).color,
                        }}
                      />
                    </div>
                    <div className="modal-progress-label">
                      {getStatusProgress(modalRequest.status).value}% Complete
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
            <div className="modal-actions">
              <button onClick={handleSave} disabled={isRefreshing}>
                {isRefreshing ? "Saving..." : "Save Changes"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyRequest;