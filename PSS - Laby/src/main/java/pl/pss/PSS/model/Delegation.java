package pl.pss.PSS.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pss.PSS.model.enums.AutoCapacity;
import pl.pss.PSS.model.enums.TransportType;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class  Delegation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int delegationId;

	@Type(type = "text")
	private String description;
	
	@Column(name = "date_time_start", nullable = false)
	private LocalDateTime dateTimeStart;
	
	@Column(name = "date_time_stop", nullable = false)
	private LocalDateTime dateTimeStop;
	
	@Column(name = "travel_diet_amount")
	private double travelDietAmount = 30.00;
	
	@Column(name = "breakfast_number")
	private int breakfastNumber = 0;
	
	@Column(name = "dinner_number")
	private int dinnerNumber = 0;
	
	@Column(name = "supper_number")
	private int supperNumber = 0;
	
	@Column(name = "transport_type")
	@Enumerated(value = EnumType.STRING)
	private TransportType transportType;

	@Column(name = "ticket_price")
	private double ticketPrice;

	@Column(name = "auto_capacity")
	@Enumerated(value = EnumType.STRING)
	private AutoCapacity autoCapacity;
	
	private int km;

	@Column(name = "accomodation_price")
	private double accomodationPrice;
	
	@Column(name = "other_tickets_price")
	private double otherTicketsPrice;

	@Column(name = "other_outlay_desc")
	private double otherOutlayDesc;
	
	@Column(name = "other_outlay_price")
	private double otherOutlayPrice;
	
	@ManyToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private User delegant;

	public Delegation(String description, LocalDateTime dateTimeStart, LocalDateTime dateTimeStop,
					  TransportType transportType, double ticketPrice,
					  AutoCapacity autoCapacity, int km, double accomodationPrice,
					  double otherTicketsPrice, double otherOutlayDesc, double otherOutlayPrice){
		this.description = description;
		this.dateTimeStart = dateTimeStart;
		this.dateTimeStop = dateTimeStop;
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