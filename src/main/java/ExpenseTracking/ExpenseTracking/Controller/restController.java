package ExpenseTracking.ExpenseTracking.Controller;

import java.security.Principal;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import ExpenseTracking.ExpenseTracking.entity.User;
import ExpenseTracking.ExpenseTracking.entity.UserRepository;

@RestController
public class restController {

	@Autowired
	private UserRepository UserRepository;
	@Autowired
	private PasswordEncoder PasswordEncoder;
	@Autowired
	private JavaMailSender sender;

	@GetMapping("/userRegistration")
	@PreAuthorize("hasRole(ROLE_ADMIN)")
	public ResponseEntity<User> userRegister(@ModelAttribute User user, Principal principal) {
		final SecureRandom secureRandom = new SecureRandom();
		String companyname=principal.getName();
		int n = secureRandom.nextInt(1_000_000);
		String rawTemp = String.format("%06d", n);

		user.setPassword(PasswordEncoder.encode(rawTemp));
		System.out.println(rawTemp);
		user.setCompany(companyname);
		UserRepository.save(user);
		//sending the password
		SimpleMailMessage message=new SimpleMailMessage();
		message.setFrom("Rajsarkar943592@gmail.com");
		message.setTo(user.getEmail());
		message.setSubject("Your Password");
		message.setText(
				"Hello,\n\n" +
				"An account has been created for expense tracking.\n\n" +
				"Account details:\n" +
				"- Username: " + user.getUsername() + "\n" +
				"- Email: " + user.getEmail() + "\n" +
				"- Temporary Password: " + rawTemp + "\n\n" +
				"For your security, please change your password after first login.\n\n" +
				"Regards,\n" +user.getCompany()+
				" Admin"
				);
	try {
		sender.send(message);
	}catch(MailException ex) {
		System.out.println("Mail error");
	}
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}
}
