package com.ams.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean enabled;
    private String status; // Will store "PENDING", "APPROVED", or "REJECTED"
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private StudentDetails studentDetails;


    // Required by Hibernate
    public User() {}
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }


	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public StudentDetails getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(StudentDetails studentDetails) {
		this.studentDetails = studentDetails;
	}
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private TeacherDetails teacherDetails;

	// The missing method that was causing your error
	public void setTeacherDetails(TeacherDetails teacherDetails) {
	    this.teacherDetails = teacherDetails;
	}

	public TeacherDetails getTeacherDetails() {
	    return teacherDetails;
	}
	
}