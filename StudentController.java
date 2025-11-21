package com.student.controller;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "search":
                searchStudents(request, response);
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
        }
    }
    
    // List all students
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Student> students = studentDAO.getAllStudents();
        request.setAttribute("students", students);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for new student
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for editing student
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        
        request.setAttribute("student", existingStudent);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Insert new student
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student newStudent = new Student(studentCode, fullName, email, major);
        
        // --- NEW: Perform Validation ---
        if (validateStudent(newStudent, request)) {
            // Validation Passed: Save to DB
            if (studentDAO.addStudent(newStudent)) {
                response.sendRedirect("student?action=list&message=Student added successfully");
            } else {
                response.sendRedirect("student?action=list&error=Failed to add student");
            }
        } else {
            // Validation Failed: Go back to form
            request.setAttribute("student", newStudent); 
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    // Update student
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student(studentCode, fullName, email, major);
        student.setId(id);
        
        // --- NEW: Perform Validation ---
        if (validateStudent(student, request)) {
            // Validation Passed: Update DB
            if (studentDAO.updateStudent(student)) {
                response.sendRedirect("student?action=list&message=Student updated successfully");
            } else {
                response.sendRedirect("student?action=list&error=Failed to update student");
            }
        } else {
            // Validation Failed: Go back to form
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    // Delete student
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
    
    // Search for students
    private void searchStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get keyword parameter from the form
        String keyword = request.getParameter("keyword");
        
        List<Student> resultList;
        
        // 2. Decide which DAO method to call & 3. Get the student list
        // If keyword is null or just whitespace, we should show all students
        if (keyword != null && !keyword.trim().isEmpty()) {
            resultList = studentDAO.searchStudents(keyword);
        } else {
            resultList = studentDAO.getAllStudents();
        }
        
        // 4. Set request attributes
        request.setAttribute("students", resultList);
        // We send the keyword back so the JSP can keep it in the search box (User Experience)
        request.setAttribute("keyword", keyword); 
        
        // 5. Forward to view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // Validate student data
    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;
        
        // 1. Validate Student Code
        // Rule: Required, Pattern 2 Uppercase letters + 3 or more digits (e.g. SV001)
        String studentCode = student.getStudentCode();
        String codePattern = "[A-Z]{2}[0-9]{3,}";
        
        if (studentCode == null || studentCode.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
            isValid = false;
        } else if (!studentCode.matches(codePattern)) {
            request.setAttribute("errorCode", "Invalid format. Use 2 Uppercase + 3 Digits (e.g., SV001)");
            isValid = false;
        }
        
        // 2. Validate Full Name
        // Rule: Required, Min length 2
        String fullName = student.getFullName();
        
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else if (fullName.trim().length() < 2) {
            request.setAttribute("errorName", "Name must be at least 2 characters");
            isValid = false;
        }
        
        // 3. Validate Email
        // Rule: If provided, must look like an email
        String email = student.getEmail();
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches(emailPattern)) {
                request.setAttribute("errorEmail", "Invalid email format");
                isValid = false;
            }
        }        
        // 4. Validate Major
        // Rule: Required
        String major = student.getMajor();
        
        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required");
            isValid = false;
        }
        
        return isValid;
    }
    
    
}
