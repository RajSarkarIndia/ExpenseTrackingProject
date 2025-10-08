package ExpenseTracking.ExpenseTracking.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface ExpensesRepository extends JpaRepository<Expenses,Integer> {
	
	List<Expenses> findAllByUsername(String username);
	
	@Query(value="select ifnull(sum(amount),0) from expenses where status=\"Submitted\" AND company=:company",
			nativeQuery=true)
	Integer sumOfSubmittedByCompany(String company);
	
	@Query(value="select ifnull(sum(amount),0) from expenses where status=\"Approved\" AND company=:company",
			nativeQuery=true)
	Integer sumOfApprovedByCompany(String company);
	
	@Query(value="select ifnull(sum(amount),0) from expenses where status=\"Rejected\" AND company=:company",
			nativeQuery=true)
	Integer sumOfRejectedByCompany(String company);
	
	@Query(value="select ifnull(sum(amount),0) from expenses where username=:username AND status=\"Submitted\"AND company=:company",
			nativeQuery=true)
	double sumofSubmittedByUsername(@Param("username") String username,@Param("company") String company);

	@Query(value="select ifnull(sum(amount),0) from expenses where username=:username AND status=\"Approved\" AND company=:company",
			nativeQuery=true)
	double sumofApprovedByUsername(@Param("username") String username,@Param("company") String company);

	@Query(value="select * from expenses where status=\"Submitted\" AND company=:company",
			nativeQuery=true)
	List<Expenses> submittedList(@Param("company") String company);

	@Modifying
	@Transactional
	@Query(value = "update expenses set status = 'Approved' where username = :username and id = :id", 
			nativeQuery = true)
	int approveByUsername(@Param("username") String username, @Param("id") int id);
	@Modifying
	@Transactional
	@Query(value = "update expenses set status = 'Rejected' where username = :username and id = :id", 
			nativeQuery = true)
	int rejectByUsername(@Param("username") String username, @Param("id") int id);

}




