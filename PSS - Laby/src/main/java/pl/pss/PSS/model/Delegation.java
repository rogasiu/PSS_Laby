package pl.pss.PSS.model;

import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pl.pss.PSS.model.enums.AutoCapacity;
import pl.pss.PSS.model.enums.TransportType;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class  Delegation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long delegationId;

	@Type(type = "text")
	@NotEmpty(message = "Wpisz opis delegacji!")
	private String description;
	
	@Column(name = "date_time_start", nullable = false)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@Future
	@NotNull(message = "Wybierz datę początku delegacji!")
	private LocalDateTime dateTimeStart;

	@Column(name = "date_time_stop", nullable = false)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@Future
	@NotNull(message = "Wybierz datę zakończenia delegacji!")
	private LocalDateTime dateTimeStop;
	
	@Column(name = "travel_diet_amount")
	@Min(value = 0, message = "Wartość Travel Diet Amount nie może być ujemna!")
	private double travelDietAmount = 30.00;
	
	@Column(name = "breakfast_number")
	@Min(value = 0, message = "Wartość Breakfast Number nie może być ujemna!")
	private int breakfastNumber = 0;
	
	@Column(name = "dinner_number")
	@Min(value = 0, message = "Wartość Dinner Number nie może być ujemna!")
	private int dinnerNumber = 0;
	
	@Column(name = "supper_number")
	@Min(value = 0, message = "Wartość Supper Number nie może być ujemna!")
	private int supperNumber = 0;
	
	@Column(name = "transport_type")
	@Enumerated(value = EnumType.STRING)
	@NotNull(message = "Wybierz Transport Type!")
	private TransportType transportType;

	@Column(name = "ticket_price")
	@Min(value = 0, message = "Wartość Ticket price nie może być ujemna!")
	private double ticketPrice;

	@Column(name = "auto_capacity")
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private AutoCapacity autoCapacity;

	@Min(value = 0, message = "Wartość Kilometers nie może być ujemna!")
	private int km;

	@Column(name = "accomodation_price")
	@Min(value = 0, message = "Wartość Accomodation Price nie może być ujemna!")
	private double accomodationPrice;
	
	@Column(name = "other_tickets_price")
	@Min(value = 0, message = "Wartość Other Tickets Price nie może być ujemna!")
	private double otherTicketsPrice;

	@Column(name = "other_outlay_desc")
	@Min(value = 0, message = "Wartość Other Outlay Desc nie może być ujemna!")
	private double otherOutlayDesc;
	
	@Column(name = "other_outlay_price")
	@Min(value = 0, message = "Wartość Other Outlay Price nie może być ujemna!")
	private double otherOutlayPrice;
	
	@ManyToOne
	@JsonIgnoreProperties({"delegations"})
    private User delegant;

	public Delegation(String description, LocalDateTime dateTimeStart, LocalDateTime dateTimeStop,
					  double travelDietAmount, int breakfastNumber, int dinnerNumber, int supperNumber,
					  TransportType transportType, double ticketPrice,
					  AutoCapacity autoCapacity, int km, double accomodationPrice,
					  double otherTicketsPrice, double otherOutlayDesc, double otherOutlayPrice){
		this.description = description;
		this.dateTimeStart = dateTimeStart;
		this.dateTimeStop = dateTimeStop;
		this.travelDietAmount = travelDietAmount;
		this.breakfastNumber = breakfastNumber;
		this.dinnerNumber = dinnerNumber;
		this.supperNumber = supperNumber;
		this.transportType = transportType;
		this.ticketPrice = ticketPrice;
		this.autoCapacity = autoCapacity;
		this.km = km;
		this.accomodationPrice = accomodationPrice;
		this.otherTicketsPrice = otherTicketsPrice;
		this.otherOutlayDesc = otherOutlayDesc;
		this.otherOutlayPrice = otherOutlayPrice;

	}
}