package ExpenseTracking.ExpenseTracking.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Expenses {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column
	private String username;
	@Column
	private String Description;
	@Column
	private LocalDate date;
	@Column
	private String Category;
	@Column
	private String PaidBy_username;
	@Column
	private String Remark;
	@Column
	private String currency;
	@Column
	private Integer amount;
	@Column
	private String Status;
	@Column
	private String company;
	
	
}
