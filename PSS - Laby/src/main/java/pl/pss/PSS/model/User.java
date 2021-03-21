package pl.pss.PSS.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

	@Column(name = "company_name", nullable = false)
	private String companyName;
	
	@Column(name = "company_address", nullable = false)
	private String companyAddress;
	
	@Column(name = "company_nip", unique = true, nullable = false)
	private String companyNip;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String lastName;
	
	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	@JsonIgnore
	private String password;
	
	private Boolean status = true;

	@Column(name = "registration_date")
	private LocalDate registrationDate = LocalDate.now();
	
	@ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name = "users_to_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
	private Set<Role> roles = new HashSet<>() {{
		add(new Role("ROLE_USER"));
	}};

	@OneToMany(
            mappedBy = "delegant",
			fetch = FetchType.EAGER
    )
    @JsonIgnoreProperties({"delegant"})
    private List<Delegation> delegations = new ArrayList<>();

	public User(String companyName, String companyAddress, String companyNip, String name, 
	String lastName, String email, String password){
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.companyNip = companyNip;
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}
}
