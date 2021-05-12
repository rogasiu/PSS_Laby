package pl.pss.PSS.model;

import java.time.LocalDate;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;


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
	@Size(min = 10, max = 10, message = "Wpisz 10 liczb NIP")
	private String companyNip;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String lastName;
	
	@Column(unique = true, nullable = false)
	@Email(message = "Wpisz poprawny Email")
	private String email;

	@Column(nullable = false)
	@JsonIgnore
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
	message = "Hasło powinno składać się z 8 znaków zawierających w sobie: cyfrę, małą literę, wielką literę, znak specjalny")
	/*
		^                 # start-of-string
		(?=.*[0-9])       # a digit must occur at least once
		(?=.*[a-z])       # a lower case letter must occur at least once
		(?=.*[A-Z])       # an upper case letter must occur at least once
		(?=.*[@#$%^&+=])  # a special character must occur at least once
		(?=\S+$)          # no whitespace allowed in the entire string
		.{8,}             # anything, at least eight places though
		$                 # end-of-string
	 */
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
	private Set<@NotNull Role> roles = new HashSet<>() {{
		add(new Role("ROLE_USER"));
	}};

	@OneToMany(
            mappedBy = "delegant",
			fetch = FetchType.EAGER
    )
    @JsonIgnoreProperties({"delegant"})
    private List<@NotNull Delegation> delegations = new ArrayList<>();

	private String activateCode;
	private boolean isActive=false;

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

	public boolean isAdmin()
	{
		Optional<@NotNull Role> r = roles.stream().filter(x->x.getRoleName().equals("ROLE_ADMIN")).findFirst();

		if(r.isPresent())
			return true;
		else
			return false;
	}
}
