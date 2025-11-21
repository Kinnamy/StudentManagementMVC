<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List - MVC</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 32px;
        }
        
        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-style: italic;
        }
        
        .message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            font-weight: 500;
        }
        
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .btn {
            display: inline-block;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }

        /* New Style for Search Button */
        .btn-search {
            background-color: #17a2b8;
            color: white;
            padding: 10px 20px;
        }
        
        .btn-danger {
            background-color: #dc3545;
            color: white;
            padding: 8px 16px;
            font-size: 13px;
        }
        
        .btn-danger:hover {
            background-color: #c82333;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            font-weight: 600;
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.5px;
        }
        
        tbody tr {
            transition: background-color 0.2s;
        }
        
        tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .actions {
            display: flex;
            gap: 10px;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }

        /* --- NEW STYLES FOR SEARCH BAR --- */
        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 15px;
            padding: 10px;
            background-color: #f8f9fa;
            border-radius: 8px;
            border: 1px solid #eee;
        }

        .search-form {
            display: flex;
            gap: 8px;
            align-items: center;
        }

        .search-input {
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            width: 250px;
            font-size: 14px;
        }

        .clear-link {
            color: #dc3545;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
        }
        
        .clear-link:hover {
            text-decoration: underline;
        }

        .search-result-msg {
            margin-top: -10px;
            margin-bottom: 15px;
            color: #555;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📚 Student Management System</h1>
        <p class="subtitle">MVC Pattern with Jakarta EE & JSTL</p>
        
        <c:if test="${not empty param.message}">
            <div class="message success">
                ✅ ${param.message}
            </div>
        </c:if>
        
        <c:if test="${not empty param.error}">
            <div class="message error">
                ❌ ${param.error}
            </div>
        </c:if>
        
        <div class="toolbar">
            <a href="student?action=new" class="btn btn-primary">
                ➕ Add New Student
            </a>

            <form action="student" method="get" class="search-form">
                <input type="hidden" name="action" value="search">
                
                <input type="text" name="keyword" class="search-input" 
                       placeholder="Search by Name, Email, or Code..." 
                       value="${keyword}">
                
                <button type="submit" class="btn btn-search">🔍 Search</button>
                
                <c:if test="${not empty keyword}">
                    <a href="student?action=list" class="clear-link">Show All</a>
                </c:if>
            </form>
        </div>

        <c:if test="${not empty keyword}">
            <div class="search-result-msg">
                Showing search results for: <strong>"${keyword}"</strong>
            </div>
        </c:if>
        
        <c:choose>
            <c:when test="${not empty students}">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Student Code</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Major</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="student" items="${students}">
                            <tr>
                                <td>${student.id}</td>
                                <td><strong>${student.studentCode}</strong></td>
                                <td>${student.fullName}</td>
                                <td>${student.email}</td>
                                <td>${student.major}</td>
                                <td>
                                    <div class="actions">
                                        <a href="student?action=edit&id=${student.id}" class="btn btn-secondary">
                                            ✏️ Edit
                                        </a>
                                        <a href="student?action=delete&id=${student.id}" 
                                           class="btn btn-danger"
                                           onclick="return confirm('Are you sure you want to delete this student?')">
                                            🗑️ Delete
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📭</div>
                    <h3>No students found</h3>
                    <p>
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                No matches found for "${keyword}". <a href="student?action=list">Clear search</a>
                            </c:when>
                            <c:otherwise>
                                Start by adding a new student
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>