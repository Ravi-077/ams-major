package com.ams.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
@Entity
public class StudentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @Column(name="roll_number")
    private String  rollNumber;
    private Double attendance; 
    private String feeStatus;
    private String year;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private Integer midMarks = 0;
    private Integer assignments = 0;
    private String grade = "N/A";
    
    @OneToOne
    @JoinColumn(name = "user_id") // Links to the 'id' column in your 'users' table
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "dept_id") // Links to the departments table
    private Department department;

    
    @ManyToOne
    @JoinColumn(name = "course_id") // Links to the courses table
    private Course course;

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	
	public Long getId() {
		return id;
	}
	

	public Double getAttendance() {
		return attendance;
	}

	public void setAttendance(Double attendance) {
		this.attendance = attendance;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Department getDepartment() {
	    return department;
	}

	public void setDepartment(Department department) {
	    this.department = department;
	}

	
	public String getFeeStatus() {
		return feeStatus;
	}

	public void setFeeStatus(String feeStatus) {
		this.feeStatus = feeStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getMidMarks() {
		return midMarks;
	}

	public void setMidMarks(Integer midMarks) {
		this.midMarks = midMarks;
	}


	public Integer getAssignments() {
		return assignments;
	}

	public void setAssignments(Integer assignments) {
		this.assignments = assignments;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}
    
	public void setCourse(Course course) {
	    this.course = course;
	}

	public Course getCourse() {
	    return course;
	}
    
    
}